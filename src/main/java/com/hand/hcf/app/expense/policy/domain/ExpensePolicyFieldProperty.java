package com.hand.hcf.app.expense.policy.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainLogic;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * @description: 费用政策动态字段关联属性表
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/1/29 10:32
 */
@Data
@TableName(value = "exp_policy_field_property")
public class ExpensePolicyFieldProperty extends DomainLogic {

    /**
     * 费用地点级别ID
     */
    private Long locationLevelId;
    /**
     * 同行人职务Code
     */
    private String dutyType;
    /**
     * 同行人员工级别Code
     */
    private String staffLevel;
    /**
     * 同行人所属部门ID
     */
    private Long departmentId;
    /**
     * 日期字段1
     */
    private ZonedDateTime dateTime1;
    /**
     * 日期字段2
     */
    private ZonedDateTime dateTime2;
}
