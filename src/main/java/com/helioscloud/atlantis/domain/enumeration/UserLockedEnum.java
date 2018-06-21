package com.helioscloud.atlantis.domain.enumeration;

/**
 * Created by zhouhui on 2016/11/23.
 */
public enum UserLockedEnum implements SysEnum {

    //未锁定
    UNLOCKED(2001),
    //锁定
    LOCKED(2002);

    private Integer id;

    UserLockedEnum(Integer id) {
        this.id = id;
    }

    public static EmployeeStatusEnum parse(Integer id) {
        for (EmployeeStatusEnum employeeStatusEnum : EmployeeStatusEnum.values()) {
            if (employeeStatusEnum.getID().equals(id)) {
                return employeeStatusEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getID() {
        return this.id;
    }
}
