package com.hand.hcf.app.base.userRole.dto;

import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/27.
 * 前端 界面用户分配角色时，封装数据
 */
@Data
public class UserAssignRoleDTO {


    private Long roleId;// 角色ID
    private String flag;// 创建:1001，删除:1002

}
