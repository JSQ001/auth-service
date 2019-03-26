package com.hand.hcf.app.mdata.department.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 部门角色用户导入视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentPositionUserImportDTO {
    /**
     * 部门名称
     */
    private String departmentName;
    /**
     * 部门角色id坐标位置map
     */
    private Map<Long,Integer> positionMap;
    /**
     * 部门角色id用户工号map
     */
    private Map<Long,String> departmentPositionEmployeeIdMap;
    /**
     * 错误描述
     */
    private String errorDetail;
    /**
     * 行号
     */
    private Integer rowNum;

    public DepartmentPositionUserImportDTO(String departmentName, Map<Long,Integer> positionMap, Map<Long,String> departmentPositionEmployeeIdMap, Integer rowNum){
        this.departmentName = departmentName;
        this.positionMap = positionMap;
        this.departmentPositionEmployeeIdMap = departmentPositionEmployeeIdMap;
        this.rowNum = rowNum;
    }
}
