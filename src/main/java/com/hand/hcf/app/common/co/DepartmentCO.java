package com.hand.hcf.app.common.co;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentCO {
    /**
     * 部门id
     */

    private Long id;
    /**
     * 部门Oid
     */
    private UUID departmentOid;
    /**
     * 部门编码
     */
    private String departmentCode;
    /**
     * 部门名称
     */
    String name;
    /**
     * 部门路径
     */
    String path;
}
