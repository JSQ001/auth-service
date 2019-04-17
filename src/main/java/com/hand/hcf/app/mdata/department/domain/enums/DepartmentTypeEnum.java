package com.hand.hcf.app.mdata.department.domain.enums;


import com.hand.hcf.app.core.enums.SysEnum;

/**
 * 部门状态枚举
 */
public enum DepartmentTypeEnum implements SysEnum {
    ENABLE(101),//启用
    DISABLE(102),//禁用
    DELETE(103),//删除
    FIND_ENABLE(1001),//查启用
    FIND_ENABLN_DISABLE(1002),//查启用禁用
    FIND_ALL(1003),//查全部

    ;
    private Integer id;

    DepartmentTypeEnum(Integer id) {
        this.id = id;
    }

    public static DepartmentTypeEnum parse(Integer id) {
        for (DepartmentTypeEnum departmentTypeEnum : DepartmentTypeEnum.values()) {
            if (departmentTypeEnum.getId().equals(id)) {
                return departmentTypeEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
