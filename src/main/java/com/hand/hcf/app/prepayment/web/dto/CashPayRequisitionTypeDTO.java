package com.hand.hcf.app.prepayment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hand.hcf.app.common.co.AssignDepartmentOrUserGroupCO;
import com.hand.hcf.app.core.serializer.CollectionToStringSerializer;
import com.hand.hcf.app.prepayment.domain.CashPayRequisitionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by 韩雪 on 2017/12/13.
 */
@ApiModel(description = "现金支付申请单类型DTO")
@Data
public class CashPayRequisitionTypeDTO {
    /*预付款单类型定义*/
    /*关联申请单依据*/
    /*是否全部申请单类型、是否全部现金事务分类*/
    @NotNull
    @ApiModelProperty(value = "现金支付申请单类型")
    private CashPayRequisitionType cashPayRequisitionType;

    /*关联申请单类型id集合*/
    @JsonSerialize(using = CollectionToStringSerializer.class)
    @ApiModelProperty(value = "关联申请单类型id集合")
    private List<Long> requisitionTypeIdList;

    /*关联现金事务分类id集合*/
    @JsonSerialize(using = CollectionToStringSerializer.class)
    @ApiModelProperty(value = "关联现金事务分类id集合")
    private List<Long> transactionClassIdList;

    /*关联部门或人员组id集合*/
    @JsonSerialize(using = CollectionToStringSerializer.class)
    @ApiModelProperty(value = "关联部门或人员组id集")
    private List<Long> departmentOrUserGroupIdList;

    /*关联部门或人员组的对象集合*/
    @ApiModelProperty(value = "关联部门或人员组的对象集合")
    private List<AssignDepartmentOrUserGroupCO> departmentOrUserGroupList;
}
