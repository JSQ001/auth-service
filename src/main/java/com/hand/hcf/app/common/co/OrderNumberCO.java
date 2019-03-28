package com.hand.hcf.app.common.co;

import lombok.Data;

import java.util.List;

/**
 * Created by cbc on 2018/3/15.
 */
@Data
public class OrderNumberCO {
    private String code;
    private List<Message> message;
    private String orderNumber;

    @Data
    public static class Message{
        private String language;
        private String content;
    }

}