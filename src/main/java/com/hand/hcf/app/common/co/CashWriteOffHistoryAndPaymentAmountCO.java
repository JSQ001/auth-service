package com.hand.hcf.app.common.co;

import lombok.Data;

import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/5/31 16:04
 * @remark
 */
@Data
public class CashWriteOffHistoryAndPaymentAmountCO {

    private List<PublicReportLineAmountCO> publicAmounts;
    private List<CashWriteOffCO> cashWriteOffHistories;
}
