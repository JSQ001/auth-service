package com.hand.hcf.app.base.userRole.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @description: 角色分配功能表
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_role_function")
public class RoleFunction extends Domain {
    //角色id
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("role_id")
    private Long roleId;

    //功能id
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("function_id")
    private Long functionId;

    //数据操作类型(true表示新增，false表示删除)
    @TableField(exist = false)
    private Boolean flag;
}
