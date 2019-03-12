package com.hand.hcf.app.base.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.hand.hcf.core.web.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class OAuthExceptionSerializer extends StdSerializer<ExtOAuthException> {
    public OAuthExceptionSerializer() {
        super(ExtOAuthException.class);
    }

    @Autowired
    ObjectMapper mapper;

    @Override
    public void serialize(ExtOAuthException value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        gen.writeStartObject();
        gen.writeStringField(Constant.MESSAGE_TAG, value.getMessage());
        gen.writeStringField(Constant.ERROR_TAG, String.valueOf(value.getHttpErrorCode()));
        gen.writeStringField(Constant.PATH_TAG, request.getServletPath());
        gen.writeStringField(Constant.TIMESTAMP_TAG, ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        if (value.getAdditionalInformation()!=null) {
            for (Map.Entry<String, String> entry : value.getAdditionalInformation().entrySet()) {
                String key = entry.getKey();
                String add = entry.getValue();
                gen.writeStringField(key, add);
            }
        }
        gen.writeEndObject();
    }
}
