package com.hand.hcf.app.common.co;

import lombok.Data;

/**
 * Created by kai.zhang on 2017-11-13.
 * 预算校验返回信息
 */
@Data
public class BudgetCheckReturnCO {

    private String messageLevel;             //控制层级
    private String errorMessage;            //返回错误信息,当messageLevel为NO_MESSAGE，无返回信息

    public BudgetCheckReturnCO(String messageLevel, String errorMessage){
        this.messageLevel = messageLevel;
        this.errorMessage = errorMessage;
    };
}
