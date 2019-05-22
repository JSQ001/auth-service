package com.hand.hcf.app.expense.accrual.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "费用预提单关联部门或人员组DTO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentOrUserGroupReturnDTO {
    //部门或人员组id
    @ApiModelProperty(value = "部门或人员组id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    //部门path 或 人员组name
    @ApiModelProperty(value = "部门path 或 人员组name")
    private String pathOrName;
}
