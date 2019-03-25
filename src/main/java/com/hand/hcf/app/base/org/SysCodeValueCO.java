package com.hand.hcf.app.base.org;

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
    String value;
    String name;
    Long codeId;
    Long id;
    Boolean enabled;
}
