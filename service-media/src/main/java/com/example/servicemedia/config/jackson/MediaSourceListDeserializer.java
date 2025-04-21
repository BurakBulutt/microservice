package com.example.servicemedia.config.jackson;


import com.example.servicemedia.media.dto.MediaSourceDto;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MediaSourceListDeserializer extends JsonDeserializer<List<MediaSourceDto>> {

    @Override
    public List<MediaSourceDto> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();

        JsonNode node = mapper.readTree(jsonParser);

        MediaSourceDto[] array = mapper.convertValue(node, MediaSourceDto[].class);

        return Arrays.asList(array);
    }
}
