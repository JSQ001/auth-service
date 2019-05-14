package com.hand.hcf.app.common.co;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *  系统代码值
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/28
 */
@Data
public class SysCodeValueCO implements Serializable {
    private String value;
    private String name;
    private Long codeId;
    private Long id;
    private Boolean enabled;
    private String remark;

}
