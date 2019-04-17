package com.hand.hcf.app.core.web.filter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom Jackson deserializer for transforming a JSON object to a ZonedDateTime object.
 */
public class CustomDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

    @Override
    public ZonedDateTime deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            if (StringUtils.isEmpty(str)) {
                return null;
            }
            return ZonedDateTime.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("UTC")));
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return ZonedDateTime.of(LocalDateTime.ofEpochSecond(jp.getLongValue()/1000,0, ZoneOffset.UTC),ZoneId.of("UTC"));
        }
       return (ZonedDateTime) ctxt.handleUnexpectedToken(ZonedDateTime.class,jp);
    }
}
