package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description: 合同行详细信息
 * @version: 1.0
 * @author: wenzhou.tang@hand-china.com
 * @date: 2018/2/2 9:50
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractHeaderLineCO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long headerId;//合同头ID
    private String contractNumber;//合同编号
    private String contractName;//合同名称
    private BigDecimal contractAmount;//合同总金额
    private BigDecimal functionAmount;
    private Double exchangeRate;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long lineId;//合同行ID
    private Integer lineNumber;//行号
    private String lineCurrency;//合同行币种
    private BigDecimal lineAmount;//合同行金额
    private String dueDate;//签订日期
    private BigDecimal lineFunctionAmount;
    private Double lineExchangeRate;
}
