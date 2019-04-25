package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * <p>
 *     付款方式dto
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/9/11
 */
@Data
public class CashPaymentMethodCO implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String paymentMethodCode;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonIgnore
    private Long tenantId;
    private String paymentMethodCategory;
    private String description;
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer versionNumber;
    private Boolean isEnabled;
    private Boolean isDeleted;
    private ZonedDateTime createdDate;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createdBy;
    private ZonedDateTime lastUpdatedDate;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long lastUpdatedBy;
}
