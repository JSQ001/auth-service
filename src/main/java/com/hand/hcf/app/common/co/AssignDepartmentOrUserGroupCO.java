package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>
 *     预付款单据类型分配部门或员工组DTO
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/9/4
 */
@Data
@ToString
@EqualsAndHashCode
public class AssignDepartmentOrUserGroupCO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String code;

    private String name;


}
