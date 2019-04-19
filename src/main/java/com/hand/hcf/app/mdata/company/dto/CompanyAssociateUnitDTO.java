package com.hand.hcf.app.mdata.company.dto;

import com.hand.hcf.app.mdata.company.domain.CompanyAssociateUnit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/4/15
 */
@Data
@ApiModel("公司关联部门对象")
public class CompanyAssociateUnitDTO extends CompanyAssociateUnit {
    @ApiModelProperty("部门代码")
    private String departmentCode;
    @ApiModelProperty("部门名称")
    private String departmentName;
    @ApiModelProperty("上级部门名称")
    private String parentDepartmentName;
    @ApiModelProperty("默认责任中心")
    private String responsibilityName;
}
