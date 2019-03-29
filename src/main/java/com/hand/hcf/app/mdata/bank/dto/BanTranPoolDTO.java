package com.hand.hcf.app.mdata.bank.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/24 09:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BanTranPoolDTO  implements Serializable {
    /**
     * ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 账单月,yyyy-MM
     */
    private String bilMon;
    /**
     * 账单日,yyyy-MM-dd
     */
    private String bilDate;
    /**
     * 卡号
     */
    private String crdNum;
    /**
     * 交易日,yyyy-MM-dd
     */
    private String trsDate;
    /**
     * 交易时间,HHmmss
     */
    private String trxTim;
    /**
     * 交易金额
     */
    private BigDecimal oriCurAmt;
    /**
     * 交易币种
     */
    private String oriCurCod;
    /**
     * 入账日,yyyy-MM-dd
     */
    private String posDate;
    /**
     * 入账金额
     */
    private BigDecimal posCurAmt;
    /**
     * 入账货币
     */
    private String posCurCod;
    /**
     * 商户名称
     */
    private String acpName;
    /**
     * 交易类型，00-一般消费,01-预借现金,12-预借现金退货,20-一般消费退货,60-还款及费用
     */
    private String trsCod;

    /**
     * 是否逾期？ true-逾期 ，false-未逾期
     */
    private Boolean overTime;
    /**
     * 审核通过期限
     */
    private String approvedDeadLineDate;
    /**
     * 备注
     */
    private String remark;
    /**
     * 银行名称
     */
    private String BankName;
}
