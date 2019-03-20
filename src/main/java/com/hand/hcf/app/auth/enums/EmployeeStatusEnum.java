package com.hand.hcf.app.auth.enums;

import com.hand.hcf.core.enums.SysEnum;

/**
 * Created by lichao on 2016/9/23.
 */
public enum EmployeeStatusEnum implements SysEnum {
    //正常
    NORMAL(1001),
    //待离职
    LEAVING(1002),
    //已离职
    LEAVED(1003);
    private Integer id;

    EmployeeStatusEnum(Integer id) {
        this.id = id;
    }

    public static EmployeeStatusEnum parse(Integer id) {
        for (EmployeeStatusEnum employeeStatusEnum : EmployeeStatusEnum.values()) {
            if (employeeStatusEnum.getId().equals(id)) {
                return employeeStatusEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
