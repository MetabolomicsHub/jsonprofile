package org.metabolomicshub.jsonprofile.validator;

import com.dylibso.chicory.runtime.ByteArrayMemory;
import com.dylibso.chicory.runtime.ExportFunction;
import com.dylibso.chicory.runtime.HostFunction;
import com.dylibso.chicory.runtime.ImportMemory;
import com.dylibso.chicory.runtime.ImportValues;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.runtime.Memory;
import com.dylibso.chicory.wasm.Parser;
import com.dylibso.chicory.wasm.WasmModule;
import com.dylibso.chicory.wasm.types.MemoryLimits;
import com.dylibso.chicory.wasm.types.ValueType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.metabolomicshub.jsonprofile.profile.JsonProfileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local OPA WASM / bundle engine executing compiled Rego policies via the OPA WASM ABI v1.2
 * (Chicory).
 */
public class LocalOpaEngine implements OpaEngine {
  private static final Logger logger = LoggerFactory.getLogger(LocalOpaEngine.class);

  private final Path wasmPath;
  private final byte[] wasmBytes;
  private Map<String, Object> bundleData = null;

  private final Memory memory;
  private final Instance instance;
  private final ExportFunction opaEval;
  private final ExportFunction opaHeapPtrSet;
  private final ExportFunction opaHeapPtrGet;
  private final ExportFunction opaMalloc;
  private final ExportFunction opaJsonParse;
  private final ExportFunction opaJsonDump;

  private final Map<String, Integer> entrypoints = new LinkedHashMap<>();
  private final Map<Integer, Function<List<Object>, Object>> builtinsById = new HashMap<>();

  private int dataAddress;
  private final int baseHeapPointer;
  private int dataHeapPointer;
  private int heapPointer;

  public LocalOpaEngine(Path wasmPath) {
    this.wasmPath = wasmPath;
    try {
      byte[] fileBytes = Files.readAllBytes(wasmPath);
      if (isGzip(fileBytes)) {
        this.wasmBytes = extractFromTarGz(fileBytes);
      } else {
        this.wasmBytes = fileBytes;
      }

      this.memory = new ByteArrayMemory(new MemoryLimits(5120, 16384));
      ImportValues imports =
          ImportValues.builder()
              .addMemory(new ImportMemory("env", "memory", this.memory))
              .addFunction(
                  new HostFunction(
                      "env",
                      "opa_builtin0",
                      List.of(ValueType.I32, ValueType.I32),
                      List.of(ValueType.I32),
                      (inst, args) -> new long[] {opaBuiltin0((int) args[0], (int) args[1])}),
                  new HostFunction(
                      "env",
                      "opa_builtin1",
                      List.of(ValueType.I32, ValueType.I32, ValueType.I32),
                      List.of(ValueType.I32),
                      (inst, args) ->
                          new long[] {opaBuiltin1((int) args[0], (int) args[1], (int) args[2])}),
                  new HostFunction(
                      "env",
                      "opa_builtin2",
                      List.of(ValueType.I32, ValueType.I32, ValueType.I32, ValueType.I32),
                      List.of(ValueType.I32),
                      (inst, args) ->
                          new long[] {
                            opaBuiltin2((int) args[0], (int) args[1], (int) args[2], (int) args[3])
                          }),
                  new HostFunction(
                      "env",
                      "opa_builtin3",
                      List.of(
                          ValueType.I32,
                          ValueType.I32,
                          ValueType.I32,
                          ValueType.I32,
                          ValueType.I32),
                      List.of(ValueType.I32),
                      (inst, args) ->
                          new long[] {
                            opaBuiltin3(
                                (int) args[0],
                                (int) args[1],
                                (int) args[2],
                                (int) args[3],
                                (int) args[4])
                          }),
                  new HostFunction(
                      "env",
                      "opa_builtin4",
                      List.of(
                          ValueType.I32,
                          ValueType.I32,
                          ValueType.I32,
                          ValueType.I32,
                          ValueType.I32,
                          ValueType.I32),
                      List.of(ValueType.I32),
                      (inst, args) ->
                          new long[] {
                            opaBuiltin4(
                                (int) args[0],
                                (int) args[1],
                                (int) args[2],
                                (int) args[3],
                                (int) args[4],
                                (int) args[5])
                          }),
                  new HostFunction(
                      "env",
                      "opa_abort",
                      List.of(ValueType.I32),
                      List.of(),
                      (inst, args) -> {
                        opaAbort((int) args[0]);
                        return null;
                      }),
                  new HostFunction(
                      "env",
                      "opa_println",
                      List.of(ValueType.I32),
                      List.of(),
                      (inst, args) -> {
                        opaPrintln((int) args[0]);
                        return null;
                      }))
              .build();

      WasmModule module = Parser.parse(this.wasmBytes);
      this.instance = Instance.builder(module).withImportValues(imports).build();

      this.opaEval = instance.export("opa_eval");
      this.opaHeapPtrSet = instance.export("opa_heap_ptr_set");
      this.opaHeapPtrGet = instance.export("opa_heap_ptr_get");
      this.opaMalloc = instance.export("opa_malloc");
      this.opaJsonParse = instance.export("opa_json_parse");
      this.opaJsonDump = instance.export("opa_json_dump");

      int epAddr = (int) instance.export("entrypoints").apply()[0];
      Object epObj = fetchJson(epAddr);
      if (epObj instanceof Map<?, ?> epMap) {
        for (Map.Entry<?, ?> entry : epMap.entrySet()) {
          if (entry.getValue() instanceof Number num) {
            this.entrypoints.put(String.valueOf(entry.getKey()), num.intValue());
          }
        }
      }

      Map<String, Function<List<Object>, Object>> builtinsMap = createDefaultBuiltins();
      int builtinsAddr = (int) instance.export("builtins").apply()[0];
      Object builtinsObject = fetchJson(builtinsAddr);
      if (builtinsObject instanceof Map<?, ?> builtinsById) {
        for (Map.Entry<?, ?> entry : builtinsById.entrySet()) {
          String fnName = String.valueOf(entry.getKey());
          if (entry.getValue() instanceof Number num) {
            Function<List<Object>, Object> fn = builtinsMap.get(fnName);
            if (fn == null) {
              throw new IllegalStateException(
                  "Required OPA builtin '" + fnName + "' is not provided");
            }
            this.builtinsById.put(num.intValue(), fn);
          }
        }
      }

      this.dataAddress = putJson(Map.of());
      this.baseHeapPointer = (int) opaHeapPtrGet.apply()[0];
      this.dataHeapPointer = this.baseHeapPointer;
      this.heapPointer = this.baseHeapPointer;

      if (this.bundleData != null) {
        setData(this.bundleData);
      }
      logger.info("Loaded OPA WASM module from {} ({} bytes)", wasmPath, this.wasmBytes.length);
    } catch (IOException ex) {
      throw new IllegalArgumentException(
          "Failed to load WASM module " + wasmPath + ": " + ex.getMessage(), ex);
    }
  }

  private static Map<String, Function<List<Object>, Object>> createDefaultBuiltins() {
    Map<String, Function<List<Object>, Object>> map = new HashMap<>();
    map.put("sprintf", LocalOpaEngine::builtinSprintf);
    map.put("time.now_ns", LocalOpaEngine::builtinTimeNowNs);
    map.put("time.format", LocalOpaEngine::builtinTimeFormat);
    map.put("time.parse_ns", LocalOpaEngine::builtinTimeParseNs);
    map.put("regex.find_n", LocalOpaEngine::builtinRegexFindN);
    map.put("regex.replace", LocalOpaEngine::builtinRegexReplace);
    map.put("json.match_schema", LocalOpaEngine::builtinJsonMatchSchema);
    return map;
  }

  private static Object builtinSprintf(List<Object> args) {
    if (args.size() < 2) {
      return "";
    }
    String fmt = String.valueOf(args.get(0));
    Object valsObj = args.get(1);
    if (!(valsObj instanceof List<?> vals)) {
      return fmt;
    }
    String javaFmt = fmt.replace("%v", "%s");
    try {
      return String.format(javaFmt, vals.toArray());
    } catch (Exception ex) {
      StringBuilder sb = new StringBuilder();
      int valIdx = 0;
      int i = 0;
      while (i < fmt.length()) {
        char c = fmt.charAt(i);
        if (c == '%' && i + 1 < fmt.length()) {
          char next = fmt.charAt(i + 1);
          if (next == '%') {
            sb.append('%');
            i += 2;
            continue;
          }
          if (valIdx < vals.size()) {
            sb.append(vals.get(valIdx++));
            i += 2;
            continue;
          }
        }
        sb.append(c);
        i++;
      }
      return sb.toString();
    }
  }

  private static Object builtinTimeNowNs(List<Object> args) {
    Instant now = Instant.now();
    return now.getEpochSecond() * 1_000_000_000L + now.getNano();
  }

  private static Object builtinTimeFormat(List<Object> args) {
    if (args.isEmpty()) {
      return "";
    }
    long ns;
    String layout = null;
    Object first = args.get(0);
    if (first instanceof List<?> list && !list.isEmpty()) {
      ns = ((Number) list.get(0)).longValue();
      if (list.size() > 1 && list.get(1) != null) {
        layout = String.valueOf(list.get(1));
      }
    } else if (first instanceof Number num) {
      ns = num.longValue();
    } else {
      return "";
    }
    Instant instant = Instant.ofEpochSecond(ns / 1_000_000_000L, ns % 1_000_000_000L);
    OffsetDateTime dt = instant.atOffset(ZoneOffset.UTC);
    if (layout == null || layout.isBlank()) {
      return dt.toString();
    }
    String pattern = translateGoTimeLayout(layout);
    try {
      return DateTimeFormatter.ofPattern(pattern).format(dt);
    } catch (Exception ex) {
      return dt.toString();
    }
  }

  private static Object builtinTimeParseNs(List<Object> args) {
    if (args.size() < 2) {
      return 0L;
    }
    String layout = String.valueOf(args.get(0));
    String value = String.valueOf(args.get(1));
    String pattern = translateGoTimeLayout(layout);
    try {
      LocalDateTime ldt = LocalDateTime.parse(value, DateTimeFormatter.ofPattern(pattern));
      Instant inst = ldt.toInstant(ZoneOffset.UTC);
      return inst.getEpochSecond() * 1_000_000_000L + inst.getNano();
    } catch (Exception ex) {
      try {
        OffsetDateTime odt = OffsetDateTime.parse(value);
        Instant inst = odt.toInstant();
        return inst.getEpochSecond() * 1_000_000_000L + inst.getNano();
      } catch (Exception ex2) {
        return 0L;
      }
    }
  }

  private static String translateGoTimeLayout(String layout) {
    return layout
        .replace("2006", "yyyy")
        .replace("01", "MM")
        .replace("02", "dd")
        .replace("15", "HH")
        .replace("04", "mm")
        .replace("05", "ss");
  }

  private static Object builtinRegexFindN(List<Object> args) {
    if (args.size() < 3) {
      return List.of();
    }
    String patternStr = String.valueOf(args.get(0));
    String value = String.valueOf(args.get(1));
    int limit = ((Number) args.get(2)).intValue();
    List<String> matches = new ArrayList<>();
    Matcher matcher = Pattern.compile(patternStr).matcher(value);
    while (matcher.find()) {
      matches.add(matcher.group());
      if (limit >= 0 && matches.size() >= limit) {
        break;
      }
    }
    return matches;
  }

  private static Object builtinRegexReplace(List<Object> args) {
    if (args.size() < 3) {
      return "";
    }
    String s = String.valueOf(args.get(0));
    String pattern = String.valueOf(args.get(1));
    String repl = String.valueOf(args.get(2));
    return s.replaceAll(pattern, repl);
  }

  private static Object builtinJsonMatchSchema(List<Object> args) {
    if (args.size() < 2) {
      return List.of(true, List.of());
    }
    try {
      Object docObj = args.get(0);
      Object schemaObj = args.get(1);
      JsonNode docNode =
          docObj instanceof String s
              ? JsonProfileUtils.mapper().readTree(s)
              : JsonProfileUtils.mapper().valueToTree(docObj);
      JsonNode schemaNode =
          schemaObj instanceof String s
              ? JsonProfileUtils.mapper().readTree(s)
              : JsonProfileUtils.mapper().valueToTree(schemaObj);
      JsonSchema schema =
          JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(schemaNode);
      Set<ValidationMessage> errors = schema.validate(docNode);
      if (errors.isEmpty()) {
        return List.of(true, List.of());
      }
      List<String> messages = errors.stream().map(ValidationMessage::getMessage).toList();
      return List.of(false, messages);
    } catch (Exception ex) {
      return List.of(false, List.of(ex.getMessage()));
    }
  }

  private int opaBuiltin0(int builtinId, int ctx) {
    return dispatchBuiltin(builtinId, List.of());
  }

  private int opaBuiltin1(int builtinId, int ctx, int a1) {
    return dispatchBuiltin(builtinId, List.of(fetchJson(a1)));
  }

  private int opaBuiltin2(int builtinId, int ctx, int a1, int a2) {
    return dispatchBuiltin(builtinId, List.of(fetchJson(a1), fetchJson(a2)));
  }

  private int opaBuiltin3(int builtinId, int ctx, int a1, int a2, int a3) {
    return dispatchBuiltin(builtinId, List.of(fetchJson(a1), fetchJson(a2), fetchJson(a3)));
  }

  private int opaBuiltin4(int builtinId, int ctx, int a1, int a2, int a3, int a4) {
    return dispatchBuiltin(
        builtinId, List.of(fetchJson(a1), fetchJson(a2), fetchJson(a3), fetchJson(a4)));
  }

  private int dispatchBuiltin(int builtinId, List<Object> args) {
    Function<List<Object>, Object> fn = builtinsById.get(builtinId);
    if (fn == null) {
      throw new IllegalStateException("Unknown OPA builtin ID: " + builtinId);
    }
    Object result = fn.apply(args);
    return putJson(result);
  }

  private void opaAbort(int addr) {
    String msg = memory.readCString(addr);
    throw new IllegalStateException("OPA Aborted with message: " + msg);
  }

  private void opaPrintln(int addr) {
    String msg = memory.readCString(addr);
    logger.info("OPA: {}", msg);
  }

  private Object fetchJson(int addr) {
    int jsonAddr = (int) opaJsonDump.apply(addr)[0];
    return fetchJsonRaw(jsonAddr);
  }

  private Object fetchJsonRaw(int jsonAddr) {
    String jsonStr = memory.readCString(jsonAddr);
    try {
      return JsonProfileUtils.mapper().readValue(jsonStr, Object.class);
    } catch (IOException ex) {
      throw new IllegalStateException(
          "Failed to deserialize OPA JSON output: " + ex.getMessage(), ex);
    }
  }

  private int putJson(Object value) {
    try {
      byte[] bytes = JsonProfileUtils.mapper().writeValueAsBytes(value);
      int size = bytes.length;
      int destAddr = (int) opaMalloc.apply(size)[0];
      memory.write(destAddr, bytes);
      int jsonAddr = (int) opaJsonParse.apply(destAddr, size)[0];
      if (jsonAddr == 0) {
        throw new IllegalStateException("OPA failed to parse JSON value");
      }
      return jsonAddr;
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to serialize JSON for OPA: " + ex.getMessage(), ex);
    }
  }

  private int[] putJsonInMemory(Object value) {
    try {
      byte[] bytes = JsonProfileUtils.mapper().writeValueAsBytes(value);
      int len = bytes.length;
      int addr = this.heapPointer;
      int requiredPages = ((addr + len) / Memory.PAGE_SIZE) + 1;
      if (requiredPages > memory.pages()) {
        memory.grow(requiredPages - memory.pages());
      }
      memory.write(addr, bytes);
      this.heapPointer = addr + len;
      return new int[] {addr, len};
    } catch (IOException ex) {
      throw new IllegalStateException(
          "Failed to write input JSON to OPA memory: " + ex.getMessage(), ex);
    }
  }

  public synchronized void setData(Map<String, Object> data) {
    opaHeapPtrSet.apply(this.baseHeapPointer);
    this.dataAddress = putJson(data != null ? data : Map.of());
    this.dataHeapPointer = (int) opaHeapPtrGet.apply()[0];
    this.heapPointer = this.dataHeapPointer;
  }

  private int resolveEntrypoint(String entrypoint) {
    if (entrypoint == null || entrypoint.isBlank()) {
      return 0;
    }
    if (entrypoints.isEmpty()) {
      return 0;
    }
    String stripped = entrypoint.strip().replaceAll("^/+", "");
    List<String> candidates = new ArrayList<>();
    candidates.add(entrypoint);
    candidates.add(stripped);
    if (stripped.startsWith("data.")) {
      candidates.add(stripped.substring("data.".length()).replace('.', '/'));
    }
    candidates.add(stripped.replace('.', '/'));

    for (String c : candidates) {
      if (entrypoints.containsKey(c)) {
        return entrypoints.get(c);
      }
    }
    for (Map.Entry<String, Integer> entry : entrypoints.entrySet()) {
      String key = entry.getKey();
      if (key.endsWith("/" + stripped) || key.equalsIgnoreCase(stripped)) {
        return entry.getValue();
      }
    }
    throw new IllegalArgumentException(
        "The specified entrypoint '"
            + entrypoint
            + "' is not valid. Available: "
            + entrypoints.keySet());
  }

  private static boolean isGzip(byte[] bytes) {
    return bytes != null
        && bytes.length >= 2
        && (bytes[0] & 0xFF) == 0x1f
        && (bytes[1] & 0xFF) == 0x8b;
  }

  private byte[] extractFromTarGz(byte[] gzBytes) throws IOException {
    byte[] extractedWasm = null;
    try (InputStream bais = new ByteArrayInputStream(gzBytes);
        GZIPInputStream gzis = new GZIPInputStream(bais);
        TarArchiveInputStream tis = new TarArchiveInputStream(gzis)) {
      TarArchiveEntry entry;
      while ((entry = tis.getNextEntry()) != null) {
        if (entry.isDirectory()) {
          continue;
        }
        String name = entry.getName();
        if (name.endsWith(".wasm") && extractedWasm == null) {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          tis.transferTo(baos);
          extractedWasm = baos.toByteArray();
        } else if ("/data.json".equals(name) || "data.json".equals(name)) {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          tis.transferTo(baos);
          this.bundleData =
              JsonProfileUtils.mapper()
                  .readValue(baos.toByteArray(), new TypeReference<Map<String, Object>>() {});
        }
      }
    }
    if (extractedWasm == null) {
      throw new IllegalArgumentException("No .wasm file found in bundle " + wasmPath);
    }
    return extractedWasm;
  }

  public Path getWasmPath() {
    return wasmPath;
  }

  public byte[] getWasmBytes() {
    return wasmBytes;
  }

  public Map<String, Object> getBundleData() {
    return bundleData != null ? bundleData : Map.of();
  }

  @Override
  public synchronized Object evaluate(
      Map<String, Object> inputData, String entrypoint, Map<String, Object> data) {
    if (data != null) {
      setData(data);
    } else if (this.bundleData != null) {
      setData(this.bundleData);
    }
    int entrypointId = resolveEntrypoint(entrypoint);
    this.heapPointer = this.dataHeapPointer;
    int[] addrAndLen = putJsonInMemory(inputData);
    int resultAddr =
        (int)
            opaEval
                .apply(
                    0,
                    entrypointId,
                    this.dataAddress,
                    addrAndLen[0],
                    addrAndLen[1],
                    this.heapPointer,
                    0)[0];
    Object rawResult = fetchJsonRaw(resultAddr);
    if (rawResult instanceof List<?> list && !list.isEmpty()) {
      Object first = list.get(0);
      if (first instanceof Map<?, ?> map && map.containsKey("result")) {
        return map.get("result");
      }
    }
    return List.of();
  }
}
