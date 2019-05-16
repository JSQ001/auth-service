package com.hand.hcf.app.expense.travel.dto;

import com.hand.hcf.app.expense.report.dto.DepartmentOrUserGroupDTO;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationType;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationTypeAssignType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author shouting.cheng
 * @date 2019/3/4
 */
@ApiModel(description = "差旅申请单类型")
@Data
public class TravelApplicationTypeDTO extends TravelApplicationType {
    /**
     * 账套代码
     */
    @ApiModelProperty(value = "账套代码")
    private String setOfBooksCode;
    /**
     * 账套名称
     */
    @ApiModelProperty(value = "账套名称")
    private String setOfBooksName;
    /**
     * 关联表单名称
     */
    @ApiModelProperty(value = "关联表单名称")
    private String formName;


    /**
     * 关联申请类型ID集合
     */
    @ApiModelProperty(value = "关联申请类型ID集合")
    private List<TravelApplicationTypeAssignType> requisitionTypeList;
    /**
     * 关联部门或人员组id集合
     */
    @ApiModelProperty(value = "关联部门或人员组id集合")
    private List<DepartmentOrUserGroupDTO> deptOrUserGroupList;
}
