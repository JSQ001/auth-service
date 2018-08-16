package com.helioscloud.atlantis.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.cloudhelios.atlantis.domain.VersionDomainObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 * 菜单按钮
 */
@Data
@TableName("sys_menu_button")
public class MenuButton extends VersionDomainObject {

    @TableField("menu_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long menuId;//菜单ID

    @TableField("button_code")
    private String buttonCode;

    @TableField("is_hide")
    private Boolean hide; // 是否隐藏

}
