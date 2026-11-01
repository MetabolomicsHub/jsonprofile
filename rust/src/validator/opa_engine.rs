use serde_json::Value;
use std::sync::Arc;

pub trait OpaEngine: Send + Sync {
    fn evaluate(
        &self,
        input_data: &Value,
        entrypoint: Option<&str>,
        data: Option<&Value>,
    ) -> Result<Value, String>;
}

pub trait OpaEngineFactory: Send + Sync {
    fn get_opa_engine(
        &self,
        wasm_file_path: &str,
        wasm_file_download_url: Option<&str>,
    ) -> Result<Arc<dyn OpaEngine>, String>;
}

#[derive(Default)]
pub struct NotImplementedOpaEngine;

impl OpaEngine for NotImplementedOpaEngine {
    fn evaluate(
        &self,
        _input_data: &Value,
        _entrypoint: Option<&str>,
        _data: Option<&Value>,
    ) -> Result<Value, String> {
        Err("Enable 'opa' feature or provide a custom OpaEngineFactory to evaluate OPA requirements".to_string())
    }
}

#[derive(Default)]
pub struct NotImplementedOpaEngineFactory;

impl OpaEngineFactory for NotImplementedOpaEngineFactory {
    fn get_opa_engine(
        &self,
        _wasm_file_path: &str,
        _wasm_file_download_url: Option<&str>,
    ) -> Result<Arc<dyn OpaEngine>, String> {
        Err("Enable 'opa' feature or provide a custom OpaEngineFactory to evaluate OPA requirements".to_string())
    }
}

pub fn default_opa_engine_factory() -> Arc<dyn OpaEngineFactory> {
    #[cfg(feature = "opa")]
    {
        Arc::new(LocalOpaEngineFactory::default())
    }
    #[cfg(not(feature = "opa"))]
    {
        Arc::new(NotImplementedOpaEngineFactory)
    }
}

#[cfg(feature = "opa")]
mod local_opa {
    use super::{OpaEngine, OpaEngineFactory};
    use chrono::{DateTime, NaiveDateTime, TimeZone, Utc};
    use flate2::read::GzDecoder;
    use regex::Regex;
    use serde_json::{json, Value};
    use std::collections::HashMap;
    use std::fs;
    use std::io::Read;
    use std::path::{Path, PathBuf};
    use std::sync::{Arc, Mutex};
    use tar::Archive;
    use wasmi::{
        Caller, Engine, Extern, Func, Instance, Linker, Memory, MemoryType, Module, Store,
        TypedFunc,
    };

    struct HostState {
        memory: Option<Memory>,
        builtins_by_id: HashMap<i32, String>,
    }

    type OpaEvalFunc = TypedFunc<(i32, i32, i32, i32, i32, i32, i32), i32>;

    struct OpaWasmRuntime {
        store: Store<HostState>,
        memory: Memory,
        opa_eval: OpaEvalFunc,
        opa_heap_ptr_set: TypedFunc<i32, ()>,
        opa_heap_ptr_get: TypedFunc<(), i32>,
        opa_malloc: TypedFunc<i32, i32>,
        opa_json_parse: TypedFunc<(i32, i32), i32>,
        entrypoints: HashMap<String, i32>,
        bundle_data: Option<Value>,
        data_address: i32,
        base_heap_pointer: i32,
        data_heap_pointer: i32,
        heap_pointer: i32,
    }

    pub struct LocalOpaEngine {
        wasm_path: PathBuf,
        runtime: Mutex<OpaWasmRuntime>,
    }

    impl LocalOpaEngine {
        pub fn new(wasm_path: impl AsRef<Path>) -> Result<Self, String> {
            let wasm_path = wasm_path.as_ref().to_path_buf();
            let raw_bytes = fs::read(&wasm_path)
                .map_err(|e| format!("Failed to read WASM file {}: {e}", wasm_path.display()))?;
            let (wasm_bytes, bundle_data) = if is_gzip(&raw_bytes) {
                extract_tar_gz(&raw_bytes, &wasm_path)?
            } else {
                (raw_bytes, None)
            };

            let engine = Engine::default();
            let module = Module::new(&engine, &wasm_bytes[..])
                .map_err(|e| format!("Failed to parse WASM module {}: {e}", wasm_path.display()))?;
            let mut store = Store::new(
                &engine,
                HostState {
                    memory: None,
                    builtins_by_id: HashMap::new(),
                },
            );
            let mem_ty = MemoryType::new(5120, Some(16384))
                .map_err(|e| format!("Invalid WASM memory limits: {e}"))?;
            let memory = Memory::new(&mut store, mem_ty)
                .map_err(|e| format!("Failed to allocate WASM memory: {e}"))?;
            store.data_mut().memory = Some(memory);

            let mut linker = <Linker<HostState>>::new(&engine);
            linker
                .define("env", "memory", memory)
                .map_err(|e| format!("Failed to link env.memory: {e}"))?;

            let b0 = Func::wrap(
                &mut store,
                |mut caller: Caller<'_, HostState>, id: i32, _ctx: i32| -> i32 {
                    dispatch_builtin(&mut caller, id, &[])
                },
            );
            let b1 = Func::wrap(
                &mut store,
                |mut caller: Caller<'_, HostState>, id: i32, _ctx: i32, a1: i32| -> i32 {
                    dispatch_builtin(&mut caller, id, &[a1])
                },
            );
            let b2 = Func::wrap(
                &mut store,
                |mut caller: Caller<'_, HostState>, id: i32, _ctx: i32, a1: i32, a2: i32| -> i32 {
                    dispatch_builtin(&mut caller, id, &[a1, a2])
                },
            );
            let b3 = Func::wrap(
                &mut store,
                |mut caller: Caller<'_, HostState>,
                 id: i32,
                 _ctx: i32,
                 a1: i32,
                 a2: i32,
                 a3: i32|
                 -> i32 { dispatch_builtin(&mut caller, id, &[a1, a2, a3]) },
            );
            let b4 = Func::wrap(
                &mut store,
                |mut caller: Caller<'_, HostState>,
                 id: i32,
                 _ctx: i32,
                 a1: i32,
                 a2: i32,
                 a3: i32,
                 a4: i32|
                 -> i32 { dispatch_builtin(&mut caller, id, &[a1, a2, a3, a4]) },
            );
            let abort_fn = Func::wrap(
                &mut store,
                |caller: Caller<'_, HostState>, addr: i32| -> () {
                    let msg = read_cstring_from_caller(&caller, addr);
                    panic!("OPA aborted: {msg}");
                },
            );
            let println_fn = Func::wrap(&mut store, |caller: Caller<'_, HostState>, addr: i32| {
                let _msg = read_cstring_from_caller(&caller, addr);
            });

            linker
                .define("env", "opa_builtin0", b0)
                .map_err(|e| e.to_string())?;
            linker
                .define("env", "opa_builtin1", b1)
                .map_err(|e| e.to_string())?;
            linker
                .define("env", "opa_builtin2", b2)
                .map_err(|e| e.to_string())?;
            linker
                .define("env", "opa_builtin3", b3)
                .map_err(|e| e.to_string())?;
            linker
                .define("env", "opa_builtin4", b4)
                .map_err(|e| e.to_string())?;
            linker
                .define("env", "opa_abort", abort_fn)
                .map_err(|e| e.to_string())?;
            linker
                .define("env", "opa_println", println_fn)
                .map_err(|e| e.to_string())?;

            let instance: Instance = linker
                .instantiate(&mut store, &module)
                .map_err(|e| format!("Failed to instantiate WASM: {e}"))?
                .start(&mut store)
                .map_err(|e| format!("Failed to start WASM instance: {e}"))?;

            let opa_eval = instance
                .get_typed_func::<(i32, i32, i32, i32, i32, i32, i32), i32>(&store, "opa_eval")
                .map_err(|e| format!("Missing opa_eval export: {e}"))?;
            let opa_heap_ptr_set = instance
                .get_typed_func::<i32, ()>(&store, "opa_heap_ptr_set")
                .map_err(|e| format!("Missing opa_heap_ptr_set export: {e}"))?;
            let opa_heap_ptr_get = instance
                .get_typed_func::<(), i32>(&store, "opa_heap_ptr_get")
                .map_err(|e| format!("Missing opa_heap_ptr_get export: {e}"))?;
            let opa_malloc = instance
                .get_typed_func::<i32, i32>(&store, "opa_malloc")
                .map_err(|e| format!("Missing opa_malloc export: {e}"))?;
            let opa_json_parse = instance
                .get_typed_func::<(i32, i32), i32>(&store, "opa_json_parse")
                .map_err(|e| format!("Missing opa_json_parse export: {e}"))?;
            let opa_json_dump = instance
                .get_typed_func::<i32, i32>(&store, "opa_json_dump")
                .map_err(|e| format!("Missing opa_json_dump export: {e}"))?;

            let ep_fn = instance
                .get_typed_func::<(), i32>(&store, "entrypoints")
                .map_err(|e| format!("Missing entrypoints export: {e}"))?;
            let ep_addr = ep_fn.call(&mut store, ()).map_err(|e| e.to_string())?;
            let ep_val = fetch_json(&mut store, &memory, &opa_json_dump, ep_addr)?;
            let mut entrypoints = HashMap::new();
            if let Some(obj) = ep_val.as_object() {
                for (k, v) in obj {
                    if let Some(id) = v.as_i64() {
                        entrypoints.insert(k.clone(), id as i32);
                    }
                }
            }

            let builtins_fn = instance
                .get_typed_func::<(), i32>(&store, "builtins")
                .map_err(|e| format!("Missing builtins export: {e}"))?;
            let b_addr = builtins_fn
                .call(&mut store, ())
                .map_err(|e| e.to_string())?;
            let b_val = fetch_json(&mut store, &memory, &opa_json_dump, b_addr)?;
            if let Some(obj) = b_val.as_object() {
                for (name, id_val) in obj {
                    if let Some(id) = id_val.as_i64() {
                        store
                            .data_mut()
                            .builtins_by_id
                            .insert(id as i32, name.clone());
                    }
                }
            }

            let data_address = put_json(
                &mut store,
                &memory,
                &opa_malloc,
                &opa_json_parse,
                &json!({}),
            )?;
            let base_heap_pointer = opa_heap_ptr_get
                .call(&mut store, ())
                .map_err(|e| e.to_string())?;

            let mut rt = OpaWasmRuntime {
                store,
                memory,
                opa_eval,
                opa_heap_ptr_set,
                opa_heap_ptr_get,
                opa_malloc,
                opa_json_parse,
                entrypoints,
                bundle_data: bundle_data.clone(),
                data_address,
                base_heap_pointer,
                data_heap_pointer: base_heap_pointer,
                heap_pointer: base_heap_pointer,
            };

            if let Some(ref bdata) = bundle_data {
                rt.set_data(bdata)?;
            }

            Ok(Self {
                wasm_path,
                runtime: Mutex::new(rt),
            })
        }

        pub fn wasm_path(&self) -> &Path {
            &self.wasm_path
        }
    }

    impl OpaWasmRuntime {
        fn set_data(&mut self, data: &Value) -> Result<(), String> {
            self.opa_heap_ptr_set
                .call(&mut self.store, self.base_heap_pointer)
                .map_err(|e| e.to_string())?;
            self.data_address = put_json(
                &mut self.store,
                &self.memory,
                &self.opa_malloc,
                &self.opa_json_parse,
                data,
            )?;
            self.data_heap_pointer = self
                .opa_heap_ptr_get
                .call(&mut self.store, ())
                .map_err(|e| e.to_string())?;
            self.heap_pointer = self.data_heap_pointer;
            Ok(())
        }

        fn resolve_entrypoint(&self, entrypoint: Option<&str>) -> Result<i32, String> {
            let Some(ep) = entrypoint else {
                return Ok(0);
            };
            if ep.trim().is_empty() || self.entrypoints.is_empty() {
                return Ok(0);
            }
            let stripped = ep.trim().trim_start_matches('/');
            let mut candidates = vec![ep.to_string(), stripped.to_string()];
            if let Some(rest) = stripped.strip_prefix("data.") {
                candidates.push(rest.replace('.', "/"));
            }
            candidates.push(stripped.replace('.', "/"));
            for c in &candidates {
                if let Some(&id) = self.entrypoints.get(c) {
                    return Ok(id);
                }
            }
            for (k, &id) in &self.entrypoints {
                if k.ends_with(&format!("/{stripped}")) || k.eq_ignore_ascii_case(stripped) {
                    return Ok(id);
                }
            }
            Err(format!("Invalid OPA entrypoint '{ep}'"))
        }

        fn put_json_in_memory(&mut self, value: &Value) -> Result<(i32, i32), String> {
            let bytes = serde_json::to_vec(value).map_err(|e| e.to_string())?;
            let len = bytes.len() as i32;
            let addr = self.heap_pointer;
            let end_byte = (addr + len) as usize;
            let cur_size = self.memory.data_size(&self.store);
            if end_byte > cur_size {
                let needed_pages = ((end_byte - cur_size) / 65536 + 1) as u32;
                self.memory
                    .grow(&mut self.store, needed_pages)
                    .map_err(|e| format!("Failed to grow WASM memory: {e}"))?;
            }
            self.memory
                .write(&mut self.store, addr as usize, &bytes)
                .map_err(|e| format!("Failed to write input JSON to WASM memory: {e}"))?;
            self.heap_pointer = addr + len;
            Ok((addr, len))
        }
    }

    impl OpaEngine for LocalOpaEngine {
        fn evaluate(
            &self,
            input_data: &Value,
            entrypoint: Option<&str>,
            data: Option<&Value>,
        ) -> Result<Value, String> {
            let mut rt = self.runtime.lock().map_err(|e| e.to_string())?;
            if let Some(d) = data {
                rt.set_data(d)?;
            } else if let Some(bdata) = rt.bundle_data.clone() {
                rt.set_data(&bdata)?;
            }
            let ep_id = rt.resolve_entrypoint(entrypoint)?;
            rt.heap_pointer = rt.data_heap_pointer;
            let (in_addr, in_len) = rt.put_json_in_memory(input_data)?;
            let data_addr = rt.data_address;
            let heap_ptr = rt.heap_pointer;
            let opa_eval = rt.opa_eval;
            let res_addr = opa_eval
                .call(
                    &mut rt.store,
                    (0, ep_id, data_addr, in_addr, in_len, heap_ptr, 0),
                )
                .map_err(|e| format!("OPA evaluation error: {e}"))?;
            let raw = fetch_json_raw(&rt.store, &rt.memory, res_addr)?;
            if let Some(arr) = raw.as_array() {
                if let Some(first) = arr.first() {
                    if let Some(res) = first.get("result") {
                        return Ok(res.clone());
                    }
                }
            }
            Ok(Value::Array(vec![]))
        }
    }

    fn is_gzip(bytes: &[u8]) -> bool {
        bytes.len() >= 2 && bytes[0] == 0x1f && bytes[1] == 0x8b
    }

    fn extract_tar_gz(
        gz_bytes: &[u8],
        wasm_path: &Path,
    ) -> Result<(Vec<u8>, Option<Value>), String> {
        let gz = GzDecoder::new(gz_bytes);
        let mut archive = Archive::new(gz);
        let mut extracted_wasm: Option<Vec<u8>> = None;
        let mut bundle_data: Option<Value> = None;

        let entries = archive.entries().map_err(|e| e.to_string())?;
        for entry_res in entries {
            let mut entry = entry_res.map_err(|e| e.to_string())?;
            let path_str = entry
                .path()
                .map_err(|e| e.to_string())?
                .to_string_lossy()
                .to_string();
            if path_str.ends_with(".wasm") && extracted_wasm.is_none() {
                let mut buf = Vec::new();
                entry.read_to_end(&mut buf).map_err(|e| e.to_string())?;
                extracted_wasm = Some(buf);
            } else if path_str == "/data.json"
                || path_str == "data.json"
                || path_str == "./data.json"
            {
                let mut buf = Vec::new();
                entry.read_to_end(&mut buf).map_err(|e| e.to_string())?;
                if let Ok(val) = serde_json::from_slice::<Value>(&buf) {
                    bundle_data = Some(val);
                }
            }
        }
        let wasm = extracted_wasm
            .ok_or_else(|| format!("No .wasm file found in bundle {}", wasm_path.display()))?;
        Ok((wasm, bundle_data))
    }

    fn read_cstring(data: &[u8], addr: i32) -> String {
        let start = addr as usize;
        if start >= data.len() {
            return String::new();
        }
        let slice = &data[start..];
        let end = slice.iter().position(|&b| b == 0).unwrap_or(slice.len());
        String::from_utf8_lossy(&slice[..end]).to_string()
    }

    fn read_cstring_from_caller(caller: &Caller<'_, HostState>, addr: i32) -> String {
        let Some(mem) = caller.data().memory else {
            return String::new();
        };
        read_cstring(mem.data(caller), addr)
    }

    fn fetch_json_raw(
        store: &Store<HostState>,
        memory: &Memory,
        json_addr: i32,
    ) -> Result<Value, String> {
        let s = read_cstring(memory.data(store), json_addr);
        serde_json::from_str(&s).map_err(|e| format!("Failed to parse OPA JSON '{s}': {e}"))
    }

    fn fetch_json(
        store: &mut Store<HostState>,
        memory: &Memory,
        opa_json_dump: &TypedFunc<i32, i32>,
        addr: i32,
    ) -> Result<Value, String> {
        let json_addr = opa_json_dump
            .call(&mut *store, addr)
            .map_err(|e| e.to_string())?;
        fetch_json_raw(store, memory, json_addr)
    }

    fn put_json(
        store: &mut Store<HostState>,
        memory: &Memory,
        opa_malloc: &TypedFunc<i32, i32>,
        opa_json_parse: &TypedFunc<(i32, i32), i32>,
        value: &Value,
    ) -> Result<i32, String> {
        let bytes = serde_json::to_vec(value).map_err(|e| e.to_string())?;
        let size = bytes.len() as i32;
        let dest_addr = opa_malloc
            .call(&mut *store, size)
            .map_err(|e| e.to_string())?;
        memory
            .write(&mut *store, dest_addr as usize, &bytes)
            .map_err(|e| e.to_string())?;
        let json_addr = opa_json_parse
            .call(&mut *store, (dest_addr, size))
            .map_err(|e| e.to_string())?;
        if json_addr == 0 {
            return Err("OPA failed to parse JSON value".to_string());
        }
        Ok(json_addr)
    }

    fn dispatch_builtin(
        caller: &mut Caller<'_, HostState>,
        builtin_id: i32,
        arg_addrs: &[i32],
    ) -> i32 {
        let fn_name = caller
            .data()
            .builtins_by_id
            .get(&builtin_id)
            .cloned()
            .unwrap_or_default();
        let Some(memory) = caller.data().memory else {
            return 0;
        };
        let Some(Extern::Func(dump_func)) = caller.get_export("opa_json_dump") else {
            return 0;
        };
        let Some(Extern::Func(malloc_func)) = caller.get_export("opa_malloc") else {
            return 0;
        };
        let Some(Extern::Func(parse_func)) = caller.get_export("opa_json_parse") else {
            return 0;
        };
        let Ok(dump_typed) = dump_func.typed::<i32, i32>(&*caller) else {
            return 0;
        };
        let Ok(malloc_typed) = malloc_func.typed::<i32, i32>(&*caller) else {
            return 0;
        };
        let Ok(parse_typed) = parse_func.typed::<(i32, i32), i32>(&*caller) else {
            return 0;
        };

        let mut args = Vec::with_capacity(arg_addrs.len());
        for &addr in arg_addrs {
            let Ok(json_addr) = dump_typed.call(&mut *caller, addr) else {
                return 0;
            };
            let s = read_cstring(memory.data(&*caller), json_addr);
            let val = serde_json::from_str::<Value>(&s).unwrap_or(Value::Null);
            args.push(val);
        }

        let result_val = evaluate_builtin(&fn_name, &args);
        let bytes = serde_json::to_vec(&result_val).unwrap_or_else(|_| b"null".to_vec());
        let size = bytes.len() as i32;
        let Ok(dest_addr) = malloc_typed.call(&mut *caller, size) else {
            return 0;
        };
        if memory
            .write(&mut *caller, dest_addr as usize, &bytes)
            .is_err()
        {
            return 0;
        };
        parse_typed
            .call(&mut *caller, (dest_addr, size))
            .unwrap_or(0)
    }

    fn evaluate_builtin(name: &str, args: &[Value]) -> Value {
        match name {
            "sprintf" => builtin_sprintf(args),
            "time.now_ns" => Value::Number((Utc::now().timestamp_nanos_opt().unwrap_or(0)).into()),
            "time.format" => builtin_time_format(args),
            "time.parse_ns" => builtin_time_parse_ns(args),
            "regex.find_n" => builtin_regex_find_n(args),
            "regex.replace" => builtin_regex_replace(args),
            "json.match_schema" => builtin_json_match_schema(args),
            _ => Value::Null,
        }
    }

    fn value_to_display(v: &Value) -> String {
        match v {
            Value::String(s) => s.clone(),
            Value::Null => "null".to_string(),
            Value::Bool(b) => b.to_string(),
            Value::Number(n) => n.to_string(),
            other => other.to_string(),
        }
    }

    fn builtin_sprintf(args: &[Value]) -> Value {
        if args.len() < 2 {
            return Value::String(String::new());
        }
        let fmt = args[0].as_str().unwrap_or("");
        let Some(vals) = args[1].as_array() else {
            return Value::String(fmt.to_string());
        };
        let mut out = String::new();
        let chars: Vec<char> = fmt.chars().collect();
        let mut i = 0;
        let mut val_idx = 0;
        while i < chars.len() {
            if chars[i] == '%' && i + 1 < chars.len() {
                let next = chars[i + 1];
                if next == '%' {
                    out.push('%');
                    i += 2;
                    continue;
                }
                if matches!(next, 's' | 'v' | 'd' | 'f' | 'g' | 'e') {
                    if let Some(v) = vals.get(val_idx) {
                        out.push_str(&value_to_display(v));
                        val_idx += 1;
                    }
                    i += 2;
                    continue;
                }
            }
            out.push(chars[i]);
            i += 1;
        }
        Value::String(out)
    }

    fn translate_go_layout(layout: &str) -> String {
        layout
            .replace("2006", "%Y")
            .replace("01", "%m")
            .replace("02", "%d")
            .replace("15", "%H")
            .replace("04", "%M")
            .replace("05", "%S")
    }

    fn builtin_time_format(args: &[Value]) -> Value {
        if args.is_empty() {
            return Value::String(String::new());
        }
        let (ns, layout) = if let Some(arr) = args[0].as_array() {
            let n = arr.first().and_then(|v| v.as_i64()).unwrap_or(0);
            let l = arr.get(1).and_then(|v| v.as_str()).map(|s| s.to_string());
            (n, l)
        } else {
            (args[0].as_i64().unwrap_or(0), None)
        };
        let dt = Utc.timestamp_nanos(ns);
        if let Some(l) = layout {
            let fmt = translate_go_layout(&l);
            Value::String(dt.format(&fmt).to_string())
        } else {
            Value::String(dt.to_rfc3339())
        }
    }

    fn builtin_time_parse_ns(args: &[Value]) -> Value {
        if args.len() < 2 {
            return json!(0);
        }
        let layout = args[0].as_str().unwrap_or("");
        let value = args[1].as_str().unwrap_or("");
        let fmt = translate_go_layout(layout);
        if let Ok(ndt) = NaiveDateTime::parse_from_str(value, &fmt) {
            return json!(ndt.and_utc().timestamp_nanos_opt().unwrap_or(0));
        }
        if let Ok(dt) = DateTime::parse_from_rfc3339(value) {
            return json!(dt.with_timezone(&Utc).timestamp_nanos_opt().unwrap_or(0));
        }
        json!(0)
    }

    fn builtin_regex_find_n(args: &[Value]) -> Value {
        if args.len() < 3 {
            return json!([]);
        }
        let pattern = args[0].as_str().unwrap_or("");
        let value = args[1].as_str().unwrap_or("");
        let limit = args[2].as_i64().unwrap_or(-1);
        let Ok(re) = Regex::new(pattern) else {
            return json!([]);
        };
        let mut matches = Vec::new();
        for m in re.find_iter(value) {
            matches.push(Value::String(m.as_str().to_string()));
            if limit >= 0 && matches.len() as i64 >= limit {
                break;
            }
        }
        Value::Array(matches)
    }

    fn builtin_regex_replace(args: &[Value]) -> Value {
        if args.len() < 3 {
            return Value::String(String::new());
        }
        let s = args[0].as_str().unwrap_or("");
        let pattern = args[1].as_str().unwrap_or("");
        let repl = args[2].as_str().unwrap_or("");
        let Ok(re) = Regex::new(pattern) else {
            return Value::String(s.to_string());
        };
        Value::String(re.replace_all(s, repl).to_string())
    }

    fn builtin_json_match_schema(args: &[Value]) -> Value {
        if args.len() < 2 {
            return json!([true, []]);
        }
        let doc = if let Some(s) = args[0].as_str() {
            match serde_json::from_str::<Value>(s) {
                Ok(v) => v,
                Err(_) => return json!([false, ["invalid JSON document"]]),
            }
        } else {
            args[0].clone()
        };
        let schema = if let Some(s) = args[1].as_str() {
            match serde_json::from_str::<Value>(s) {
                Ok(v) => v,
                Err(_) => return json!([false, ["invalid JSON schema"]]),
            }
        } else {
            args[1].clone()
        };
        let Ok(validator) = jsonschema::validator_for(&schema) else {
            return json!([false, ["invalid JSON schema"]]);
        };
        let errors: Vec<String> = validator.iter_errors(&doc).map(|e| e.to_string()).collect();
        if errors.is_empty() {
            json!([true, []])
        } else {
            json!([false, errors])
        }
    }

    #[derive(Default)]
    pub struct LocalOpaEngineFactory {
        engines: Mutex<HashMap<PathBuf, Arc<dyn OpaEngine>>>,
    }

    impl LocalOpaEngineFactory {
        fn resolve_wasm_path(wasm_file_path: &str) -> PathBuf {
            let path = PathBuf::from(wasm_file_path);
            if !path.exists() {
                let alt_parent = PathBuf::from("..").join(&path);
                if alt_parent.exists() {
                    return alt_parent;
                }
                if let Some(stripped) = wasm_file_path.strip_prefix("../") {
                    let alt_stripped = PathBuf::from(stripped);
                    if alt_stripped.exists() {
                        return alt_stripped;
                    }
                }
            }
            path
        }
    }

    impl OpaEngineFactory for LocalOpaEngineFactory {
        fn get_opa_engine(
            &self,
            wasm_file_path: &str,
            wasm_file_download_url: Option<&str>,
        ) -> Result<Arc<dyn OpaEngine>, String> {
            if wasm_file_path.trim().is_empty() {
                return Err("Invalid WASM file path".to_string());
            }
            let resolved = Self::resolve_wasm_path(wasm_file_path);
            let mut map = self.engines.lock().map_err(|e| e.to_string())?;
            if let Some(existing) = map.get(&resolved) {
                return Ok(existing.clone());
            }
            if !resolved.exists() {
                let Some(url) = wasm_file_download_url.filter(|u| !u.trim().is_empty()) else {
                    return Err(
                        "Invalid WASM file does not exist and URL is not defined.".to_string()
                    );
                };
                if let Some(parent) = resolved.parent() {
                    fs::create_dir_all(parent).map_err(|e| e.to_string())?;
                }
                let resp = reqwest::blocking::get(url).map_err(|e| e.to_string())?;
                let bytes = resp.bytes().map_err(|e| e.to_string())?;
                fs::write(&resolved, &bytes).map_err(|e| e.to_string())?;
            }
            let engine: Arc<dyn OpaEngine> = Arc::new(LocalOpaEngine::new(&resolved)?);
            map.insert(resolved, engine.clone());
            Ok(engine)
        }
    }
}

#[cfg(feature = "opa")]
pub use local_opa::{LocalOpaEngine, LocalOpaEngineFactory};
