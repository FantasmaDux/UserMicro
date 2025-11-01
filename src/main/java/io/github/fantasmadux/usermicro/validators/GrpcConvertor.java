package io.github.fantasmadux.usermicro.validators;

import com.google.protobuf.NullValue;
import com.google.protobuf.Value;
import io.github.fantasmadux.usermicro.api.exceptions.ServerAnswerException;

import java.util.Map;
import java.util.stream.Collectors;

public class GrpcConvertor {
    public static Map<String, Value> convertToProtoValueMap(Map<String, Object> userData) {
        return userData.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> convertObjectToProtoValue(entry.getValue())
                ));
    }

    public static Map<String, Object> convertToObjectMap(Map<String, Value> data) {
        return data.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> convertProtoValueToObject(entry.getValue())
                ));
    }

    private static Value convertObjectToProtoValue(Object value) {
        Value.Builder builder = Value.newBuilder();
        return switch (value) {
            case null -> builder.setNullValue(NullValue.NULL_VALUE).build();
            case String s -> builder.setStringValue(s).build();
            case Boolean b -> builder.setBoolValue(b).build();
            case Number n -> builder.setNumberValue(n.doubleValue()).build();
            default -> throw new ServerAnswerException();
        };
    }

    private static Object convertProtoValueToObject(Value value) {
        return switch (value.getKindCase()) {
            case STRING_VALUE -> value.getStringValue();
            case BOOL_VALUE -> value.getBoolValue();
            case NUMBER_VALUE -> value.getNumberValue();
            case NULL_VALUE, KIND_NOT_SET -> null;
            default -> throw new ServerAnswerException();
        };
    }
}
