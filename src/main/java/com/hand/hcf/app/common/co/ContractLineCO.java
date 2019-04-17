package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.web.dto.DomainObjectDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @description:
 * @version: 1.0
 * @author: wenzhou.tang@hand-china.com
 * @date: 2017/10/24 18:40
 */

@Data
public class ContractLineCO extends DomainObjectDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long headerId;//合同头ID

    private Integer lineNumber;//行号

    private BigDecimal amount;//金额

    private String currency;//币种

    private String paymentMethod;//付款方法

    private String partnerCategory;//对象类型  EMPLOYEE个人    VENDER供应商

    private String partnerCategoryName;//合同方类型名称

    @JsonSerialize(using = ToStringSerializer.class)
    private Long partnerId;//合同方ID

    private String partnerName;//合同方名称

    private ZonedDateTime dueDate;//签订日期

    private String remark;//备注

    private Integer versionNumber;//版本

    private BigDecimal functionAmount;
    private Double exchangeRate;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
}
