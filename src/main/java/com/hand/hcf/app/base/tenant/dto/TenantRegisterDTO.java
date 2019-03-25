package com.hand.hcf.app.base.tenant.dto;

import com.hand.hcf.app.base.tenant.domain.Tenant;
import lombok.Data;


/**
 * <p>
 *  租户注册dto
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/1/11
 */
@Data
public class TenantRegisterDTO extends Tenant {

    private String login;
    private String password;
    private String passwordConfirm;
    private String fullName;
    private String mobile;
    private String email;
    private String employeeId;
    private String title;



}
