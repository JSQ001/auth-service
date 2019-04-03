package com.hand.hcf.app.expense.report.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentOrUserGroupDTO {
    //部门或人员组id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    //部门path 或 人员组name
    private String name;
}
