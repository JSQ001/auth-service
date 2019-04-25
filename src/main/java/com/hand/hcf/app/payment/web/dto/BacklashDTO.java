package com.hand.hcf.app.payment.web.dto;

import com.hand.hcf.app.payment.domain.CashTransactionDetail;
import lombok.Data;

/**
 * Created by 刘亮 on 2018/4/3.
 */
@Data
public class BacklashDTO {

    //单据信息
    private PayDocumentDTO payDocumentDTO;

    //原来付款明细
    private CashTransactionDetail detail;

    //反冲付款明细
    private CashTransactionDetail backDetail;

    //标志
    private Boolean flashFlag;
}
