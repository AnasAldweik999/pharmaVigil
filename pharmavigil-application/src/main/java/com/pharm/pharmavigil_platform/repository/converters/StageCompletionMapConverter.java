package com.pharm.pharmavigil_platform.repository.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.Map;

@Converter
public class StageCompletionMapConverter implements AttributeConverter<Map<String, Boolean>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Boolean> stages) {
        if (stages == null || stages.isEmpty()) {
            return "{}";
        }
        try {
            return MAPPER.writeValueAsString(stages);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize stages map", e);
        }
    }

    @Override
    public Map<String, Boolean> convertToEntityAttribute(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return MAPPER.readValue(json, new TypeReference<Map<String, Boolean>>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize stages map", e);
        }
    }
}
