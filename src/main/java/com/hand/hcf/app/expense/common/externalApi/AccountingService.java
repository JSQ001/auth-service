package com.hand.hcf.app.expense.common.externalApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/4/1 18:46
 * @remark 核算模块第三方接口
 */
@Service
public class AccountingService {

    //jiu.zhao 核算
    /*@Autowired
    private AccountingClient accountingClient;

    *//**
     * 报账单生成凭证
     * @param report 对公报账单数据
     * @return 返回"SUCCESS"表示报账单数据凭证生成成功
     *//*
    public String saveInitializeExpReportGeneralLedgerJournalLine(ExpenseReportCO report){
        return accountingClient.saveInitializeExpReportGeneralLedgerJournalLine(report);
    }

    *//**
     * 根据报账单ID删除凭证
     * 同时删除核销凭证
     * @param headerId
     *//*
    public void deleteExpReportGeneralLedgerJournalDataByHeaderId(Long headerId){
        accountingClient.deleteExpReportGeneralLedgerJournalDataByHeaderId(headerId);
    }*/

/**
 * 进项税单生成凭证
 * @param inputTaxCO 进项税单数据
 * @return 返回"SUCCESS"表示报账单数据凭证生成成功
 */
    //bo.liu 核算
//    public String saveInitializeExpInputTaxGeneralLedgerJournalLine(ExpenseInputTaxCO inputTaxCO){
//        return accountingClient.saveInitializeExpInputTaxGeneralLedgerJournalLine(inputTaxCO);
//    }
}
