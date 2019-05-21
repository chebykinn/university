package com.ghostflow.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Utils {
    public static <T> T firstNonNull(T t1, T t2) {
        return t1 != null ? t1 : t2;
    }

    public static ObjectNode exceptionToObjectNode(ObjectMapper objectMapper, Exception e) {
        ObjectNode error = objectMapper.createObjectNode();
        error.put("message", e.toString());
        error.put("stackTrace", Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n")));
        return error;
    }
}
