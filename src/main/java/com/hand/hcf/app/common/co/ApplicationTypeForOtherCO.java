package com.hand.hcf.app.common.co;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/3/13
 */
@Data
public class ApplicationTypeForOtherCO {
    //账套ID
    private Long setOfBooksId;

    //所选范围 (all-全部 selected-已选 notChoose-未选)
    private String range;

    //费用申请单类型代码
    private String code;

    //费用申请单类型名称
    private String name;

    //是否启用
    private Boolean enabled;

    //申请单类型id集合
    private List<Long> idList;
}
