package com.hand.hcf.app.expense.type.web.dto;

import com.hand.hcf.app.expense.type.domain.ExpenseTypeAssignCompany;
import com.hand.hcf.app.expense.type.domain.ExpenseTypeAssignUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/12
 */
@ApiModel(description = "费用类型分配信息")
@Data
public class ExpenseTypeAssignInfoDTO implements Serializable {
    @ApiModelProperty(value = "分配用户")
    private List<ExpenseTypeAssignUser> assignUsers;
    @ApiModelProperty(value = "分配公司")
    private List<ExpenseTypeAssignCompany> assignCompanies;
    @ApiModelProperty(value = "全公司标志")
    private Boolean allCompanyFlag;
    @ApiModelProperty(value = "申请类型")
    private Integer applyType;
}
