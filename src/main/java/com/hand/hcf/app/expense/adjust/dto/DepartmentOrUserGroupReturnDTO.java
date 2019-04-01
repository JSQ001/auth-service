package com.hand.hcf.app.expense.adjust.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by 韩雪 on 2018/3/16.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentOrUserGroupReturnDTO {
    //部门或人员组id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    //部门path 或 人员组name
    private String pathOrName;
}
