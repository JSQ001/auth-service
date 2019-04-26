package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.web.dto.DomainObjectDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 15:09 2018/1/24
 * @Modified by
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequisitionLineCO extends DomainObjectDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;  //主键ID

    @JsonSerialize(using = ToStringSerializer.class)
    private Long headerId; //头表ID

    @JsonSerialize(using = ToStringSerializer.class)
    private Long cshTransactionId; //来源待付数据ID

    private String refDocumentType; //关联单据类型

    @JsonSerialize(using = ToStringSerializer.class)
    private Long refDocumentId; //关联单据头ID

    @JsonSerialize(using = ToStringSerializer.class)
    private Long refDocumentLineId; //关联单据行ID

    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId; //机构ID

    private String companyName;//机构名称

    private String partnerCategory; //收款对象

    @JsonSerialize(using = ToStringSerializer.class)
    private Long partnerId; //收款对象ID

    private String partnerName;//收款对象名称
    private String cshTransactionTypeCode; //现金事务类型代码

    private String cshTransactionClassId;//现金事务分类ID
    private String cshTransactionClassName;//现金事务分类名称
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cashFlowItemId;//现金流量项ID

    private String currencyCode;//币种

    private Double exchangeRate;//汇率

    private BigDecimal amount;//原币金额

    private BigDecimal functionAmount;//本位币金额

    private String lineDescription;//行描述

    private String accountName; //银行户名

    private String accountNumber;//银行户名

    private String bankLocationCode;//分行代码

    private String bankLocationName;//分行名称

    private String provinceCode; //省份代码

    private String province;//省份名称

    private String cityCode;//城市代码

    private String cityName;//城市名称

    private String paymentMethodCategory;//付款方式类型

    private String paymentMethodCategoryName;//付款方式类型名称

    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractHeaderId;//关联合同ID


    @JsonSerialize(using = ToStringSerializer.class)
    private Long paymentScheduleLineId;//资金计划行ID

    private ZonedDateTime schedulePaymentDate;//计划付款日期

    private Integer VersionNumber;//版本号

    /*从报账单取开始*/
    private Double availableAmount; //可支付金额

    private Double freezeAmount;//冻结金额

    private String reportNumber;//报账单编号

    private String refDocumentNumber;//关联单据编号

    private String scheduleLineNumber;//付款序号
    /*从报账单取结束*/

    /*取合同开始*/
    private String contractNumber;//合同编号
    private String contractLineNumber; //合同行次号
    private String contractDueDate;//合同计划付款日期
    /*取合同结束*/
}
