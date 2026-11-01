package org.metabolomicshub.jsonprofile.validator;

import java.util.Map;

public interface OpaEngine {
  Object evaluate(Map<String, Object> inputData, String entrypoint, Map<String, Object> data);
}
