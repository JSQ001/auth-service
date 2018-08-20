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

    String COMPONENT_NAME_NULL = "10071";// 组件名称不允许为空
    String COMPONENT_TYPE_INVALID = "10072";// 组件类型值无效,只能为1或2.

    String MODULE_CODE_NULL = "10081";// 模块代码不允许为空
    String MODULE_NAME_NULL = "10082";// 模块名称不允许为空
    String MODULE_CODE_NOT_UNION = "10083";// 模块代码已经存在

    String LANGUAGE_CODE_NULL = "10093";// 语言代码不允许为空
    String MODULE_ID_NULL = "10094";// 必须关联模块
    String LANGUAGE_CODE_NOT_UNION = "10095";// 必须关联模块

    String REQUEST_NAME_NULL = "10110";// 请求名称不允许为空
    String REQUEST_INTERFACE_NULL = "10111";// 请求必须关联接口

    String RESPONSE_NAME_NULL = "10120";// 响应名称不允许为空
    String RESPONSE_INTERFACE_NULL = "10121";// 响应必须关联接口

}
