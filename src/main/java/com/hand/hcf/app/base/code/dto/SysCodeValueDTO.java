package com.hand.hcf.app.base.code.dto;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/26
 */
@Data
public class SysCodeValueDTO {
    private String name;

    private String code;


    private String remark;  // 说明


    private String enabledStr;
}
