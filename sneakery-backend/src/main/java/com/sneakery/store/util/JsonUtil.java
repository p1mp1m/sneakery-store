package com.sneakery.store.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * Utility class for JSON operations
 * Reduces code duplication in services
 */
@Slf4j
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Parse JSON string to List of Strings
     * Returns empty list if parsing fails or input is null/empty
     * 
     * @param jsonString The JSON string to parse
     * @return List of strings, or empty list if parsing fails
     */
    public static List<String> parseJsonToStringList(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(jsonString, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse JSON string to list: {}", jsonString, e);
            return Collections.emptyList();
        }
    }

    /**
     * Convert List of Strings to JSON string
     * Returns empty JSON array if input is null or empty
     * 
     * @param list The list to convert
     * @return JSON string representation
     */
    public static String stringListToJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }

        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            log.warn("Failed to convert list to JSON: {}", list, e);
            return "[]";
        }
    }

    /**
     * Get ObjectMapper instance
     * 
     * @return ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Convert any object to JSON string
     */
    public static String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            log.error("Failed to serialize object to JSON", e);
            return "[]"; // tránh null gây lỗi parse
        }
    }

    /**
     * Parse JSON to List<T> (generic)
     */
    public static <T> List<T> parseJsonToList(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(
                    json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz)
            );
        } catch (Exception e) {
            log.error("Failed to parse JSON to List<{}>: {}", clazz.getSimpleName(), json, e);
            return Collections.emptyList();
        }
    }
}

