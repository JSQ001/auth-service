package com.hand.hcf.app.common.co;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by kai.zhang on 2018-01-02.
 * 核算接口主类
 */
@Data
public class AccountingBaseCO implements Serializable {
    //创建人
    private Long createdBy;
}
