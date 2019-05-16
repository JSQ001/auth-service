package com.hand.hcf.app.common.co;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/3/5
 */
@Data
public class BusinessDataCO implements Serializable {
    /**
     * 租户id
     */
    @NotNull
    private Long tenantId;
    /**
     * 账套id
     */
    @NotNull
    private Long sobId;
    /**
     * 业务类型id
     */
    @NotBlank
    private String businessTypeCode;
    /**
     * 单据id
     */
    @NotNull
    private Long documentId;
    /**
     * 单据编号
     */
    @NotBlank
    private String documentNumber;
    /**
     * 公司id
     */
    @NotNull
    private Long companyId;
    /**
     * 公司代码
     */
    @NotBlank
    private String companyCode;
    /**
     * 部门id
     */
    @NotNull
    private Long departmentId;
    /**
     * 部门代码
     */
    @NotBlank
    private String departmentCode;
    /**
     * 申请人id
     */
    @NotNull
    private Long employeeId;
    /**
     * 币种
     */
    @NotBlank
    private String currencyCode;
    /**
     * 汇率
     */
    @NotNull
    private BigDecimal exchangeRate;
    /**
     * 原币金额
     */
    @NotNull
    private BigDecimal amount;
    /**
     * 本位币金额
     */
    @NotNull
    private BigDecimal functionalAmount;

    /**
     * 单据类型Id
     */
    @NotNull
    private Long documentTypeId;
    /**
     * 单据类别
     */
    @NotBlank
    private String documentCategory;

    @NotBlank
    private String documentOid;

    private Map<String, List<Map<String, Object>>> otherLines;

}
