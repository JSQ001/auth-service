package com.hand.hcf.app.workflow.brms.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hand.hcf.app.workflow.brms.dto.SimpleValueDetailDTO;

import java.io.IOException;

public class SimpleValueDetailSerializer extends JsonSerializer<SimpleValueDetailDTO> {

    @Override
    public void serialize(SimpleValueDetailDTO simpleValueDetailDTO, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(simpleValueDetailDTO);
        jsonGenerator.writeString(jsonString);
    }
}
