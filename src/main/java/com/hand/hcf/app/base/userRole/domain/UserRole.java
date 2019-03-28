package com.hand.hcf.app.base.userRole.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainEnable;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 * 用户角色关联
 */
@Data
@TableName("sys_user_role")
public class UserRole extends DomainEnable {

    @NotNull

    @TableField("user_id")
    private Long userId;// 用户ID

    @NotNull

    @TableField("role_id")
    private Long roleId;// 角色Id

    /**
     * 数据权限ID
     */
    @TableField("data_authority_id")
    private Long dataAuthorityId;

    /**
     * 有效日期从
     */
    @TableField("valid_date_from")
    private ZonedDateTime validDateFrom;

    /**
     * 有效日期至
     */
    @TableField("valid_date_to")
    private ZonedDateTime validDateTo;
}
