package com.hand.hcf.app.mdata.department.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author lichao
 * @date 18/1/8
 */
@Getter
@Setter
public class DepartmentTreeDTO {

    private Long id;

    private Long parentId;
    private UUID parentDepartmentOid;
    /**
     * 部门oid
     */
    private UUID departmentOid;
    /**
     * 部门名称
     */
    private String name;
    /**
     * 部门路径
     */
    private String path;
    /**
     * 状态
     */
    private int status;

    /**
     * 是否可用
     */
    private boolean available;
    /**
     * 子部门
     */
    private List<DepartmentTreeDTO> childrenDepartment = new ArrayList<>();
    /**
     * 路径深度
     */
    private Integer pathDepth;

    public Integer getPathDepth(){
        if(pathDepth != null){
            return this.pathDepth;
        }
        if(StringUtils.isNotEmpty(path)){
            this.pathDepth = StringUtils.countMatches(path,"|")+1;
            return this.pathDepth;
        }
        return 0;
    }

    /**
     * 根部门标记
     */
    private boolean rootFlag;

    private Boolean hasUsers;

    private Boolean hasChildrenDepartments;

    private Integer userCounts;

    private String departmentCode;
}
