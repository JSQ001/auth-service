package com.hand.hcf.app.common.co;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/19
 */
@Data
public class BudgetItemCO implements Serializable {

    private String itemCode;//预算项目代码
    private String itemName;//预算项目名称
    private Long id;
    private Long sourceItemId; // 申请类型or费用类型 Id
}
