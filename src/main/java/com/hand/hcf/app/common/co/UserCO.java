package com.hand.hcf.app.common.co;

import com.hand.hcf.app.common.enums.UserStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * <p>
 *  员工对外co
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/20
 */
@Data
public class UserCO implements Serializable {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户OID
     */
    protected UUID userOid;

    /**
     * 登录凭证
     */
    protected String login;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 语言
     */
    protected String language;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 备注
     */
    private String remark;

    /**
     *电话号码
     */
    private String phoneNumber;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态
     */
    private UserStatusEnum status;
}
