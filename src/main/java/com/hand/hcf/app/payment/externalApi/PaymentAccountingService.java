package com.hand.hcf.app.payment.externalApi;

import org.springframework.stereotype.Service;

/**
 * @description: 调用核算模块三方接口
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2018/12/25
 */
@Service
public class PaymentAccountingService {
    //bo.liu 核算
    /*@Autowired
    private AccountingClient accountingClient;
    @Autowired
    private MapperFacade persistence;

    *//**
     * 核销数据生成凭证
     * @param writeOffInterface 核销数据
     * @return 返回"SUCCESS"表示核销数据凭证生成成功
     *//*
    public String saveInitializeWriteOffGeneralLedgerJournalLine(WriteOffInterfaceCO writeOffInterface){
        return accountingClient.saveInitializeWriteOffGeneralLedgerJournalLine(writeOffInterface);
    }

    *//**
     * 凭证过账批量处理
     * @param accountPostingHandleCOs
     * @return
     *//*
    public void updateAccountPostingBatch(List<AccountPostingHandleCO> accountPostingHandleCOs){
        String result = accountingClient.updateAccountPostingBatch(accountPostingHandleCOs);
        if(! "SUCCESS".equals(result)){
            throw new ValidationException(result);
        }
    }

    *//**
     * 凭证过账回滚批量处理
     * @param accountPostingHandleCOs
     * @return
     *//*
    public void updateAccountPostingRollbackBatch(List<AccountPostingHandleCO> accountPostingHandleCOs){
        String result = accountingClient.updateAccountPostingRollbackBatch(accountPostingHandleCOs);
        if(! "SUCCESS".equals(result)){
            throw new ValidationException(result);
        }
    }

    *//**
     * 支付数据生成凭证
     * @param createdBy 操作人
     * @param paymentDetails 支付数据
     * @param flag 标志
     * @return 返回"SUCCESS"表示支付数据凭证生成成功
     *//*
    public String sendCashTransactionDetails(Long createdBy, List<PaymentDetail> paymentDetails, Boolean flag){
        if (!flag){
            return "SUCCESS";
        }
        List<PaymentDetailCO> paymentDetailCO = new ArrayList<>();
        //赋值
        paymentDetailCO =  persistence.mapAsList(paymentDetails,PaymentDetailCO.class);

        PaymentInterfaceCO paymentInterfaceCO = new PaymentInterfaceCO();
        paymentInterfaceCO.setCreatedBy(createdBy);
        paymentInterfaceCO.setPaymentDetails(paymentDetailCO);

        return accountingClient.saveInitializePaymentGeneralLedgerJournalLine(paymentInterfaceCO);
    }*/


}
