package com.helioscloud.atlantis.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/30.
 * 角色分配菜单的按钮
 */
@Data
public class RoleAssignMenuButtonDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;      // menuId or buttonId
    private String code; // menuCode or buttonCode
    private String name;    // menuName or buttonName
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId; // menuId or parentId   菜单的上级菜单Id，按钮时取按钮对应的菜单ID
    private String type; // 类型 MenuTypeEnum中的 DIRECTORY 或 BUTTON 用于判断是按钮还是菜单
    private String flag;// 引用 FlagEnum 枚举类 创建:1001，删除:1002

}
