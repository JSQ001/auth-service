package com.hand.hcf.app.base.util;

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
    String HAVING_CHILD_MENU = "10023";// 菜单代码已经存在
    String MENU_FUNCTION_PARENT_MUST_BE_CATALOG = "10024";// 功能的上级菜单必须是目录
    String MENU_PARENT_CATALOG_ERROR = "10025";//功能只能添加到最底级的目录
    String ROOT_CATALOG_MUST_BE_CATALOG = "10026";//功能只能添加到最底级的目录
    String HAS_CHILD_CATALOG_CAN_NOT_BE_FUNCTION = "10027";//当前菜单存在子目录，不允许由目录变更为功能

    String USER_ROLE_EXISTS = "10031"; // 用户角色组合已经存在
    String ROLE_MENU_EXISTS = "10041"; // 角色菜单组合已经存在

    String BUTTON_CODE_NULL = "10050";//按钮代码不允许为空
    String BUTTON_CODE_NOT_UNION = "10051";// 按钮代码在菜单中已经存在
    String BUTTON_NAME_NULL = "10052";//按钮名称不允许为空

    String ROLE_MENU_BUTTON_EXISTS = "10061"; // 角色菜单按钮组合已经存在

    String COMPONENT_NAME_NULL = "10071";// 组件名称不允许为空
    String COMPONENT_TYPE_INVALID = "10072";// 组件类型值无效,只能为1或2.

    String MODULE_CODE_NULL = "10081";// 模块代码不允许为空
    String MODULE_NAME_NULL = "10082";// 模块名称不允许为空
    String MODULE_CODE_NOT_UNION = "10083";// 模块代码已经存在

    String FRONT_KEY_NULL = "10093";// Key不允许为空
    String MODULE_ID_NULL = "10094";// 必须关联模块
    String FRONT_KEY_NOT_UNION = "10095";// KEY必须唯一

    String REQUEST_CODE_NULL = "10110";// 请求名称不允许为空
    String REQUEST_INTERFACE_NULL = "10111";// 请求必须关联接口

    String RESPONSE_CODE_NULL = "10120";// 响应名称不允许为空
    String RESPONSE_INTERFACE_NULL = "10121";// 响应必须关联接口

    String LANGUAGE_CODE_NULL = "10131";// 语言代码不允许为空
    String LANGUAGE_NAME_NULL = "10132";// 语言名称不允许为空
    String LANGUAGE_CODE_NOT_UNION = "10133";// 语言代码已经存在

    String ERROR_CODE_NULL = "10141";// 报错代码不允许为空
    String ERROR_MESSAGE_NULL = "10142";// 报错信息不允许为空

    //数据权限
    String DATA_AUTHORITY_CITED = "auth_200001";        //该数据权限已被引用，不可删除！
    String DATA_AUTHORITY_RULE_EXISTS = "auth_200002";  //同一权限下，规则名称不能重复！
    String DATA_AUTHORITY_RULE_DETAIL_VALUE_NONE = "auth_200003";   //数据权限为手工选择时，请至少选择一条数据！
    String DATA_AUTHORITY_RULE_DETAIL_EXISTS = "auth_200004";   //数据权限规则数据类型已存在，请勿重复保存！
}
