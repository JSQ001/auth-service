package com.hand.hcf.app.base.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/31.
 * 前端 界面角色分配菜单时，封装数据
 */
@Data
public class RoleMenuButtonDTO {
    private Long roleId;
    private List<RoleAssignMenuButtonDTO> assignMenuButtonList;
}
