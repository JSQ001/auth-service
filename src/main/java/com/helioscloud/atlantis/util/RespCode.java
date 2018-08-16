package com.helioscloud.atlantis.util;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 */
public interface RespCode {
    String SUCESS = "0000";// 成功
    String FAILED = "0001";// 失败
    String ROLE_CODE_NULL = "10010";//角色代码不允许为空
    String ROLE_NAME_NULL = "10011";// 角色名称不允许为空
    String CODE_NOT_UNION_IN_TENANT = "10012";// 当前租户已存在该角色代码
    String ID_NULL = "00006"; //id 不应该为空!
    String ID_NOT_NULL = "00007"; //ID 应该为空
    String DB_NOT_EXISTS = "00003"; //数据库不存在

    String MENU_CODE_NULL = "10020";//菜单代码不允许为空
    String MENU_NAME_NULL = "10021";// 菜单名称不允许为空
    String CODE_NOT_UNION = "10022";// 菜单代码已经存在

    String USER_ROLE_EXISTS = "10031"; // 用户角色组合已经存在
    String ROLE_MENU_EXISTS = "10041"; // 角色菜单组合已经存在

    String BUTTON_CODE_NULL = "10050";//按钮代码不允许为空
    String BUTTON_CODE_NOT_UNION = "10051";// 按钮代码在菜单中已经存在

    String ROLE_MENU_BUTTON_EXISTS = "10061"; // 角色菜单按钮组合已经存在
}
