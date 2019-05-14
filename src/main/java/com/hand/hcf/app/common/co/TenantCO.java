package com.hand.hcf.app.common.co;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/5/9
 */

@Data
public class TenantCO implements Serializable {
    private Long id;
    private String tenantName;
    private String tenantShortName;
    private String status;
    private String tenantCode;
    private String countryCode;
    private Boolean systemFlag;
    private Boolean enabled;
}