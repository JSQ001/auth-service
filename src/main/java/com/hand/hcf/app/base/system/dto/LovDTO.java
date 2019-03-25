package com.hand.hcf.app.base.system.dto;

import lombok.Data;

@Data
public class LovDTO {
    private String code;//独立代码
    private String value;//显示值
    private String comments;//备注
    private String type;//类型


    public LovDTO(String code, String value, String type, String comments) {
        this.code = code;
        this.value = value;
        this.type = type;
        this.comments = comments;
    }

}
