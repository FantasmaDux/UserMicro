package io.github.pavelshe11.networkingmicro.normalization;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataNormalisation {
    public static String normalizeTextData(Object value) {
        if (value instanceof String str) {
            str = str.trim();
            return str.isEmpty() ? null : str;
        }
        return null;
    }

    public static Map<String, Object> normalizeInput(Map<String, Object> input) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String str) {
                String trimmed = str.trim();
                result.put(key, trimmed.isEmpty() ? null : trimmed);
            } else {
                result.put(key, value);
            }
        }
        return result;
    }
}
