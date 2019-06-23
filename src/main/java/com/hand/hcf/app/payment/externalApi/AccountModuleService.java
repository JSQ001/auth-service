package com.hand.hcf.app.payment.externalApi;

import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: bin.xie
 * @Description: 调用核算接口
 * @Date: Created in 10:20 2018/3/5
 * @Modified by
 */
@Service
public class AccountModuleService {

    @Autowired
    private PaymentAccountingService accountingService;

    @Autowired
    private MapperFacade mapper;

    /*public String sendCashTransactionDetails(Long createdBy, List<PaymentDetail> paymentDetails,Boolean flag) {
        if (!flag){
            return "SUCCESS";
        }
        PaymentDetailDto paymentDetailDto = new PaymentDetailDto();
        paymentDetailDto.setCreatedBy(createdBy);
        paymentDetailDto.setPaymentDetails(paymentDetails);
        PaymentInterface paymentInterfaceClient = new PaymentInterface();
        // 赋值
        persistence.map(paymentDetailDto,paymentInterfaceClient);
        return accountingService.initializePaymentGeneralLedgerJournalLine(paymentInterfaceClient);
    }*/

    /**
     * 核销记录生成凭证
     * @param writeOffInterface
     * @return
     */
//    public String sendCashWriteOffDetails(WriteOffInterface writeOffInterface) {
//        com.hand.hcf.app.apply.accounting.dto.writeOff.WriteOffInterface writeOffInterfaceClient = new com.hand.hcf.app.apply.accounting.dto.writeOff.WriteOffInterface();
//
//        // 赋值
//        persistence.map(writeOffInterface,writeOffInterfaceClient);
//        return accountingService.initializeWriteOffGeneralLedgerJournalLine(writeOffInterfaceClient);
//    }

    /**
     * 核销凭证过账
     * @param tenantId
     * @param transactionHeaderId
     * @param transactionLineId
     * @param transactionDistId
     * @param lastUpdatedBy
     */
    /*public void accountPosting(Long tenantId,
                                      Long transactionHeaderId,
                                      Long transactionLineId,
                                      Long transactionDistId,
                                      Long lastUpdatedBy){
        String result = accountingService.accountPosting(tenantId, "CSH_WRITE_OFF",transactionHeaderId,
                transactionLineId, transactionDistId, lastUpdatedBy);
        if(! "SUCCESS".equals(result)){
            throw new ValidationException(result);
        }
    }*/

    /**
     * 核销凭证过账回滚
     * @param tenantId
     * @param transactionHeaderId
     * @param transactionLineId
     * @param transactionDistId
     * @param lastUpdatedBy
     */
    /*public void accountPostingRollback(Long tenantId,
                                      Long transactionHeaderId,
                                      Long transactionLineId,
                                      Long transactionDistId,
                                      Long lastUpdatedBy){
        String result = accountingService.accountPostingRollback(tenantId, "CSH_WRITE_OFF",transactionHeaderId,
                transactionLineId, transactionDistId, lastUpdatedBy);
        if(! "SUCCESS".equals(result)){
            throw new ValidationException(result);
        }
    }*/

    /**
     * 核销凭证过账批量处理
     * @param accountPostingHandleDTOs
     */
//    public void accountPostingBatch(List<AccountPostingHandleDTOClient> accountPostingHandleDTOs){
//        String result = accountingService.accountPostingBatch(accountPostingHandleDTOs);
//        if(! "SUCCESS".equals(result)){
//            throw new ValidationException(result);
//        }
//    }

    /**
     * 核销凭证过账回滚批量处理
     * @param accountPostingHandleDTOs
     */
//    public void accountPostingRollBackBatch(List<AccountPostingHandleDTOClient> accountPostingHandleDTOs){
//        String result = accountingService.accountPostingRollbackBatch(accountPostingHandleDTOs);
//        if(! "SUCCESS".equals(result)){
//            throw new ValidationException(result);
//        }
//    }
}