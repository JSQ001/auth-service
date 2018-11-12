package com.hand.hcf.app.base.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/24.
 *
 */
@Data
public class FrontKeyDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String keyCode; //界面keyCode
    private String lang; // 语言
    private String descriptions; // 语言描述
    @JsonSerialize(using = ToStringSerializer.class)
    private Long moduleId;  // 模块ID
}
