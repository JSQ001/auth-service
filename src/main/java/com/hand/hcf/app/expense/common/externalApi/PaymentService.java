package com.hand.hcf.app.expense.common.externalApi;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.common.co.CashDefaultFlowItemCO;
import com.hand.hcf.app.common.co.CashTransactionClassCO;
import com.hand.hcf.app.common.co.CashTransactionClassForOtherCO;
import com.hand.hcf.app.common.co.CashWriteOffHistoryAndPaymentAmountCO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  调用支付模块API
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/4
 */
@Service
public class PaymentService {
    //jiu.zhao 支付
    /*@Autowired
    private PaymentClient paymentClient;paymentClient

    *//**
     * 获取某个报账单类型下，当前账套下、启用的、PAYMENT类型的 已分配的、未分配的、全部的 付款用途(现金事物分类)
     * @param forArtemisCO
     * @param page
     * @return
     * @throws URISyntaxException
     *//*
    public List<CashTransactionClassCO> listCashTransactionClassByRange(
            CashTransactionClassForOtherCO forArtemisCO,
            Page page) throws URISyntaxException{
        Page<CashTransactionClassCO> cashTransactionClassCOPage = paymentClient.listCashTransactionClassByRange(forArtemisCO, page);
        page.setTotal(cashTransactionClassCOPage.getTotal());
        List<CashTransactionClassCO> result = cashTransactionClassCOPage.getRecords();
        return result;
    }

    *//**
     * 根据现金事务类型ID集合查询数据
     * @param ids
     * @return
     *//*
    public List<CashTransactionClassCO> listCashTransactionClassByIdList(List<Long> ids){
        if(CollectionUtils.isEmpty(ids)){
            return Arrays.asList();
        }
        return paymentClient.listCashTransactionClassByIdList(ids);
    }

    *//**
     * 获取现金事务对应的默认现金流量项
     * @param transactionClassId
     * @return
     *//*
    public CashDefaultFlowItemCO getCashDefaultFlowItemByTransactionClassId(Long transactionClassId){
        return paymentClient.getCashDefaultFlowItemByTransactionClassId(transactionClassId);
    }

    *//**
     * 获取计划付款行的核销,支付信息集合
     * @param documentType 单据类型
     * @param documentHeaderId 单据头id
     * @param documentLineId 单据行id
     * @return
     *//*
    public CashWriteOffHistoryAndPaymentAmountCO listCashWriteOffHistoryAll(String documentType,
                                                                            Long documentHeaderId,
                                                                            Long documentLineId){
        return paymentClient.listCashWriteOffHistoryAll(documentType,documentHeaderId,documentLineId);
    }

    *//**
     * 根据现金事务类型ID查询数据
     * @param id
     * @return
     *//*
    public CashTransactionClassCO getCashTransactionClassById(Long id){
        return paymentClient.getCashTransactionClassById(id);
    }

    *//**
     * 根据单据信息获取核销金额记录
     * @param documentType
     * @param documentHeaderId
     * @param documentLineId
     * @return
     *//*
    public Map<Long, BigDecimal> listDocumentWriteOffAmount(String documentType, Long documentHeaderId, Long documentLineId) {
        return this.paymentClient.listDocumentWriteOffAmount(documentType, documentHeaderId, documentLineId);
    }

    *//**
     * 根据单据信息删除核销信息
     * @param documentType
     * @param documentHeaderId
     * @param documentLineId
     * @return
     *//*
    public Boolean deleteWriteOffForDocumentMessage(String documentType,
                                                    Long documentHeaderId,
                                                    Long documentLineId){
        return paymentClient.deleteWriteOffForDocumentMessage(documentType,documentHeaderId,documentLineId);
    }

    *//**
     * @param cashTransactionDataCreateCOS
     * 报账单对接支付
     *//*
    public void saveDataToPayment(List<CashTransactionDataCreateCO> cashTransactionDataCreateCOS){
        paymentClient.saveTransactionDataBatch(cashTransactionDataCreateCOS);
    }

    *//**
     * 核销记录生效
     * @param documentType
     * @param documentHeaderId
     * @param documentLineIds
     * @param lastUpdatedBy
     * @return
     *//*
    public String saveWriteOffTakeEffect(String documentType, Long documentHeaderId, List<Long> documentLineIds, Long lastUpdatedBy) {
        return paymentClient.saveWriteOffTakeEffect(documentType, documentHeaderId, documentLineIds, lastUpdatedBy);
    }

    *//**
     * 回滚核算记录
     * @param documentType
     * @param documentHeaderId
     * @param documentLineIds
     * @param lastUpdatedBy
     * @return
     *//*
    public String updateWriteOffRollback(String documentType, Long documentHeaderId, List<Long> documentLineIds, Long lastUpdatedBy) {
        return paymentClient.updateRollback(documentType, documentHeaderId, documentLineIds, lastUpdatedBy);
    }

    *//**
     * 创建核销凭证
     * @param co
     * @return
     *//*
    public String saveWriteOffJournalLines(CashWriteOffAccountCO co){
        return paymentClient.saveWriteOffJournalLines(co);
    }

    *//**
     *根据 根据已付金额来确定报销单数据
     *//*
    public  List<CashTransactionDetailCO> queryCashTransactionDetailByReport(BigDecimal paidAmountFrom,
                                                                             BigDecimal paidAmountTo,
                                                                             String backlashFlag){
        return paymentClient.queryCashTransactionDetailByReport(paidAmountFrom,paidAmountTo,backlashFlag);
    }*/
}
