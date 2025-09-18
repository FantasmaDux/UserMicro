package io.github.pavelshe11.networkingmicro.normalization;

import io.github.pavelshe11.networkingmicro.store.enums.ContactMethodType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static io.github.pavelshe11.networkingmicro.store.enums.ContactMethodType.EMAIL;

@Component
public class DataNormalisation {

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

    public String normalizeContact(String contact, ContactMethodType type) {
        if (contact == null) return null;
        String trimmed = contact.trim();

        return switch (type) {
            case EMAIL -> trimmed.toLowerCase();
            case LINK, PHONE -> trimmed;
        };
    }

    public static String normalizeTextForComparison(String text) {
        if (text == null) return null;
        return text.trim().toLowerCase().replaceAll("\\s+", " ");
    }
}
