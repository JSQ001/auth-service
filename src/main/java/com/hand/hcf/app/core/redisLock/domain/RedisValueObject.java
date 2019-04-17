package com.hand.hcf.app.core.redisLock.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RedisValueObject {

    //mac地址
    private String macAddress;
    //进程id
    private long jvmPid;
    //线程id
    private long threadId;
    //过期时间
    private long expires;

    //借用toString序列化
    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "RedisValueObject{" +
            "macAddress='" + macAddress + '\'' +
            ", jvmPid=" + jvmPid +
            ", threadId=" + threadId +
            ", expires=" + expires +
            '}';
    }

    //反序列化
    public static RedisValueObject fromString(String str) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(str, RedisValueObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
