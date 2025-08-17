package io.github.pavelshe11.networkingmicro.normalization;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataNormalisation {

    private static String normalizeTextData(Object value) {
        if (value instanceof String str) {
            str = str.trim();
            return str.isEmpty() ? null : str;
        }
        return null;
    }

    private static Object normalizeNumberData(String value) {
        try {
            if (value.matches("-?\\d+")) {
                return Integer.parseInt(value);
            }

            if (value.matches("-?\\d*\\.\\d+([eE][-+]?\\d+)?")) {
                return Double.parseDouble(value);
            }
        } catch (NumberFormatException ignored) {
        }

        return value;
    }

    public static Map<String, Object> normalizeInput(Map<String, Object> input) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String str) {
                String trimmed = str.trim();
                if (trimmed.isEmpty()) {
                    result.put(key, null);
                    continue;
                }

                Object normalized = normalizeNumberData(trimmed);
                result.put(key, normalized);
            } else {
                result.put(key, value);
            }
        }
        return result;
    }
}
