package com.hand.hcf.app.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 魏胜
 * @Description:
 * @Date: 2018/4/11 10:59
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendorTypeCO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String vendorTypeCode;

    private String name;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    private Boolean enabled;
}
