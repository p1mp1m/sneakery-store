package com.sneakery.store.service;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmailTemplateRenderer {

    /**
     * Render template string bằng cách thay thế {{variable}} thành giá trị trong map.
     */
    public String render(String template, Map<String, Object> variables) {
        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String key = "{{" + entry.getKey() + "}}";
            String value = String.valueOf(entry.getValue());
            result = result.replace(key, value);
        }
        return result;
    }
}