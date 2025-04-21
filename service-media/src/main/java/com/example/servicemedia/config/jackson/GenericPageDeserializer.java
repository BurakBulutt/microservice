package com.example.servicemedia.config.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.List;

public class GenericPageDeserializer extends JsonDeserializer<PageImpl<?>> {

    @Override
    public PageImpl<?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();

        JsonNode node = mapper.readTree(jsonParser);

        JsonNode contentNode = node.get("content");
        List<?> content = mapper.convertValue(contentNode, new TypeReference<>() {});

        int number = node.get("number").asInt();
        int size = node.get("size").asInt();
        long total = node.get("totalElements").asLong();

        return new PageImpl<>(content, PageRequest.of(number, size), total);
    }
}
