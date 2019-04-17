package com.hand.hcf.app.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsonorg.JSONObjectDeserializer;
import com.fasterxml.jackson.datatype.jsonorg.JSONObjectSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 *  @author baochao.chen@hand-china.com
 */


@ConditionalOnMissingBean(Jackson2ObjectMapperBuilder.class)
@Configuration
public class JacksonConfiguration {

    @Bean
    Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(JSONObject.class, new JSONObjectSerializer());
        module.addDeserializer(JSONObject.class, new JSONObjectDeserializer());
        module.addSerializer(Long.class, new ToStringSerializer());
        module.addSerializer(Long.TYPE, new ToStringSerializer());
        return new Jackson2ObjectMapperBuilder()
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .findModulesViaServiceLoader(true)
                .modulesToInstall(module);
    }
}

