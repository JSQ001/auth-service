package com.hand.hcf.app.mdata.contact.dto;

import lombok.Data;

import java.util.UUID;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/2/25 16:06
 * @remark
 */
@Data
public class UserSaveCO {

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
     * 语言
     */
    protected String language;

    /**
     * 租户ID
     */
    private Long tenantId;
}
