package com.hand.hcf.app.prepayment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by 刘亮 on 2018/3/12.
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractHeaderLineDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long headerId;//合同头ID
    private String contractNumber;//合同编号
    private String contractName;//合同名称
    private Double contractAmount;//合同总金额
    @JsonSerialize(using = ToStringSerializer.class)
    private Long lineId;//合同行ID
    private Integer lineNumber;//行号
    private String lineCurrency;//合同行币种
    private Double lineAmount;//合同行金额
    private String dueDate;//签订日期
}

