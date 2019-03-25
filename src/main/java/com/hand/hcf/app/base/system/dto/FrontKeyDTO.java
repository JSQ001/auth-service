package com.hand.hcf.app.base.system.dto;

import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/24.
 *
 */
@Data
public class FrontKeyDTO {

    private Long id;
    private String keyCode; //界面keyCode
    private String lang; // 语言
    private String descriptions; // 语言描述

    private Long moduleId;  // 模块ID
}
