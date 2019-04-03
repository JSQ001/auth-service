package com.hand.hcf.app.prepayment.web.dto;

import com.hand.hcf.app.prepayment.domain.CashPaymentRequisitionHead;
import com.hand.hcf.app.prepayment.domain.CashPaymentRequisitionLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * <p>
 *
 * </p>
 * CashPaymentRequisitionLineDto
 *
 * @author hao.yi
 * @date 2019/3/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashPaymentRequisitionHeadDto extends CashPaymentRequisitionHead {
    //现金事务名称
    private  String repTypeName;
    //现金事务名称
    private  Long cshTransactionClassId;
    // 关联金额
    private BigDecimal relevancyAmount;
    //行Id
    private Long lineId;
}
