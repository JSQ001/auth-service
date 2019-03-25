package com.hand.hcf.app.base.user.enums;

import com.hand.hcf.core.enums.SysEnum;

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


    @Override
    public Integer getId() {
        return this.id;
    }
}
