package com.hand.hcf.app.common.co;

import lombok.Data;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description 申请单查询参数 （预算余额）
 * @date 2019/4/17 18:12
 * @version: 1.0.0
 */
@Data
public class ExpenseApportionQueryParamCO {

    private Long documentHeaderId;

    private Long documentLineId;
}
