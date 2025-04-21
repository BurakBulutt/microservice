package com.example.servicereaction.config.jackson;

import com.example.servicereaction.comment.dto.CommentDto;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.List;

public class CommentPageDeserializer extends JsonDeserializer<Page<CommentDto>> {
    @Override
    public Page<CommentDto> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();

        JsonNode node = mapper.readTree(jsonParser);

        JsonNode contentNode = node.get("content");
        List<CommentDto> content = mapper.convertValue(contentNode, new TypeReference<>() {});

        int number = node.get("number").asInt();
        int size = node.get("size").asInt();
        long total = node.get("totalElements").asLong();

        return new PageImpl<>(content, PageRequest.of(number, size), total);
    }
}
