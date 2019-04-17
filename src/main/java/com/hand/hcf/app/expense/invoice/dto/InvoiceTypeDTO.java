package com.hand.hcf.app.expense.invoice.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import com.hand.hcf.app.expense.invoice.domain.InvoiceTypeMouldHeadColumn;
import com.hand.hcf.app.expense.invoice.domain.InvoiceTypeMouldLineColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/1/22 14:15
 * @version: 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceTypeDTO extends DomainI18nEnable {
    //租户id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    // 账套ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;

    private String setOfBooksCode;

    private String setOfBooksName;

    //发票类型代码
    private String invoiceTypeCode;

    //发票类型名称
    @I18nField
    private String invoiceTypeName;

    //抵扣标志
    private String deductionFlag;

    //创建方式（系统预置：SYS；自定义：CUSTOM）
    private String creationMethod;

    //发票代码长度
    private String invoiceCodeLength;

    //发票号码长度
    private String invoiceNumberLength;

    //默认税率
    private String defaultTaxRate;

    //接口映射值
    private String interfaceMapping;

    private InvoiceTypeMouldHeadColumn invoiceTypeMouldHeadColumn;

    private InvoiceTypeMouldLineColumn invoiceTypeMouldLineColumn;

}
