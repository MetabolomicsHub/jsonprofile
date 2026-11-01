package org.metabolomicshub.jsonprofile.validator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalOpaEngineFactory implements OpaEngineFactory {
  private static final Logger logger = LoggerFactory.getLogger(LocalOpaEngineFactory.class);
  private final Map<Path, OpaEngine> engines = new ConcurrentHashMap<>();

  public void loadFromUrl(String url, Path targetPath) {
    try {
      if (targetPath.getParent() != null) {
        Files.createDirectories(targetPath.getParent());
      }
      logger.info("Downloading {} to {}", url, targetPath);
      try (InputStream in = URI.create(url).toURL().openStream()) {
        Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException ex) {
      throw new IllegalArgumentException(
          "Failed to download WASM from " + url + ": " + ex.getMessage(), ex);
    }
  }

  private static Path resolveWasmPath(String wasmFilePath) {
    Path path = Path.of(wasmFilePath);
    if (!Files.exists(path)) {
      Path altParent = Path.of("..").resolve(path);
      if (Files.exists(altParent)) {
        return altParent;
      }
      if (wasmFilePath.startsWith("../")) {
        Path altStripped = Path.of(wasmFilePath.substring(3));
        if (Files.exists(altStripped)) {
          return altStripped;
        }
      }
    }
    return path;
  }

  @Override
  public OpaEngine getOpaEngine(String wasmFilePath, String wasmFileDownloadUrl) {
    if (wasmFilePath == null || wasmFilePath.isBlank()) {
      throw new IllegalArgumentException("Invalid WASM file path");
    }
    Path path = resolveWasmPath(wasmFilePath);
    return engines.computeIfAbsent(
        path,
        p -> {
          if (!Files.exists(p)) {
            if (wasmFileDownloadUrl == null || wasmFileDownloadUrl.isBlank()) {
              throw new IllegalArgumentException(
                  "Invalid WASM file does not exist and URL is not defined.");
            }
            loadFromUrl(wasmFileDownloadUrl, p);
          }
          return new LocalOpaEngine(p);
        });
  }
}
