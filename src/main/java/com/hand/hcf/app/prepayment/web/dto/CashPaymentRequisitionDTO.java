package com.hand.hcf.app.prepayment.web.dto;

import com.hand.hcf.app.core.web.dto.DomainObjectDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Created by cbc on 2017/10/27.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CashPaymentRequisitionDTO extends DomainObjectDTO {

    /* 单据编号 */
    private String requisitionNumber;

    /* 单据类型 */
    private String typeName;

    /* 申请人  */
    private String employeeName;

    /* 申请日期 */
    private ZonedDateTime requisitionDate;

    /* 说明  */
    private String description;

    /* 预付款金额 */
    private Double advancePaymentAmount;

    /* 已核销金额 */
    private Double writedAmount;

    /* 状态 */
    private String status;

    private Integer versionNumber;
}
