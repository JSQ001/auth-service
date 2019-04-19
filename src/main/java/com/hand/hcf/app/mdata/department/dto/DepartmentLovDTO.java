package com.hand.hcf.app.mdata.department.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/4/16
 */
@Data
@ApiModel("部门Lov对象")
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentLovDTO implements Serializable {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("部门代码")
    private String departmentCode;
    @ApiModelProperty("部门名称")
    private String departmentName;
    @ApiModelProperty("部门Oid")
    private String departmentOid;
    @ApiModelProperty("上级部门Id")
    private Long parentId;
    @ApiModelProperty("部门path")
    private String departmentPath;
    @ApiModelProperty("租户id")
    private Long tenantId;
    @ApiModelProperty("状态 101-启用 102-禁用")
    private Long status;

}
