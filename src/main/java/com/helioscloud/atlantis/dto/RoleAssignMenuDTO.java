package com.helioscloud.atlantis.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/24.
 * 前端 界面角色分配菜单时，封装数据
 */
@Data
public class RoleAssignMenuDTO{
    @JsonSerialize(using = ToStringSerializer.class)
    private Long menuId;// 菜单ID
    private String flag;// 创建:1001，删除:1002
}
