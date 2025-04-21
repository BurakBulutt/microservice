package com.example.servicereaction.config.jackson;


import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
public class CustomListDeserializer<T> extends JsonDeserializer<List<T>> {
    private final Class<T> clazz;

    @Override
    public List<T> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        JsonNode node = mapper.readTree(jsonParser);

        JavaType type = mapper.getTypeFactory().constructCollectionType(List.class,clazz);

        return mapper.convertValue(node, type);
    }
}
