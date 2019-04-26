package com.hand.hcf.app.payment.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 支付流水详情DTO
 * Created by 刘亮 on 2017/12/20.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOfFlowDetail {

    //支付状态
    private String payStatus;

    //付款单据
    private PayDocumentDTO payDocumentDTO;

    //付款详情
    private PayDetailDTO payDetailDTO;

    //财务信息
    private FinancialDTO financialDTO;

    //操作详情
    private List<OperationDTO> operationDTO;

    //核销历史
    private List<WriteOffHistoryDTO> writeOffHistoryDTOS;


}
