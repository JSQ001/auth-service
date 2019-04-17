package com.hand.hcf.app.core.web.filter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @description: 自定义反序列化-将时间格式的数据转化为ZonedDateTime
 * @version: 1.0
 * @author: wenzhou.tang@hand-china.com
 * @date: 2017-10-25 14:59:55
 */
@Deprecated
public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

    @Override
    public ZonedDateTime deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            if (StringUtils.isEmpty(str)) {
                return null;
            }
            return ZonedDateTime.parse(str,DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("UTC")));
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            Instant instant = Instant.ofEpochSecond(jp.getLongValue());
            return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        }
        return (ZonedDateTime) ctxt.handleUnexpectedToken(ZonedDateTime.class,jp);
    }
}
