package com.hand.hcf.app.workflow.brms.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class NotifyInfo implements Serializable {
    //是否勾选App消息推送
    private  Boolean app;
    //是否勾选企业微信号消息推送
    private  Boolean weChat;
    //网页端消息
    private  Boolean web;
    //是否勾选姓名表单名称
    private  Boolean name;
    //自定义标题
    private  String titles;
    //是否勾选总金额
    private  Boolean money;
    //是否勾选单据上的是事由
    private  Boolean reason;
    //自定义内容
    private  String content;
}
