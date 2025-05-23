package com.example.serviceusers.config.jackson.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class CustomPageDeserializer<T> extends JsonDeserializer<Page<T>> {
    private final Class<T> clazz;

    @Override
    public Page<T> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        JsonNode node = mapper.readTree(jsonParser);

        JsonNode contentNode = node.get("content");

        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
        List<T> content = mapper.convertValue(contentNode, collectionType);

        int number = node.get("number").asInt();
        int size = node.get("size").asInt();
        long total = node.get("totalElements").asLong();

        return new PageImpl<>(content, PageRequest.of(number, size), total);
    }
}
