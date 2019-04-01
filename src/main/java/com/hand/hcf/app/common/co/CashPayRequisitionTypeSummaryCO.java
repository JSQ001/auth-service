package com.hand.hcf.app.common.co;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CashPayRequisitionTypeSummaryCO implements java.io.Serializable{
    /*预付款单类型定义*/
    /*关联申请单依据*/
    /*是否全部申请单类型、是否全部现金事务分类*/
    @NotNull
    private CashPayRequisitionTypeCO cashPayRequisitionType;

    /*关联申请单类型id集合*/
    private List<Long> requisitionTypeIdList;

    /*关联现金事务分类id集合*/
    private List<Long> transactionClassIdList;

    /*关联部门或人员组id集合*/
    private List<Long> departmentOrUserGroupIdList;

    /*关联部门或人员组的对象集合*/
    private List<AssignDepartmentOrUserGroupCO> departmentOrUserGroupList;


    /*根据id查询时，返回关联申请单类型id集合*/
    private List<String> returnRequisitionTypeIdList;

    /*根据id查询时，返回关联现金事务分类id集合*/
    private List<String> returnTransactionClassIdList;
}
