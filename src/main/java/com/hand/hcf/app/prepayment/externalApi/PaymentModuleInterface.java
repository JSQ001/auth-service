package com.hand.hcf.app.prepayment.externalApi;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.payment.implement.web.PaymentImplementController;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

//import com.hand.hcf.app.apply.payment.PaymentClient;
//import com.hand.hcf.app.apply.payment.dto.*;


/**
 * Created by kai.zhang on 2017-10-31.
 */
@Service
public class PaymentModuleInterface {
    //jiu.zhao 支付
    private static final Logger log = LoggerFactory.getLogger(PaymentModuleInterface.class);

    @Autowired
    private PaymentImplementController paymentClient;
    @Autowired
    private MapperFacade mapper;


    public  List<PaymentDocumentAmountCO> getPrepaymentPayAndReturnAmount(List<Long> headerIds, Boolean flag, Long employeeId, Long companyId, Long typeId) {
        return paymentClient.listAmountByPrepaymentIds(headerIds, employeeId, companyId, typeId);
    }


    public  List<PaymentDocumentAmountCO> getPayReturnAmountByLines(List<Long> lines) {
        return paymentClient.listAmountByPrepaymentLineIds(lines);
    }

    public  List<CashWriteOffDocumentAmountCO> getCashWriteOffDocumentAmountDTOByInput(Double noWriteOffDocumentAmountFrom, Double noWriteOffDocumentAmountTo, Long setOfBooksId) {
        return paymentClient.listDocumentByWriteOffAmount(noWriteOffDocumentAmountFrom != null ?
                        BigDecimal.valueOf(noWriteOffDocumentAmountFrom) : null,
                noWriteOffDocumentAmountTo != null ? BigDecimal.valueOf(noWriteOffDocumentAmountTo) : null, setOfBooksId);

    }

    public Map<Long, List<PublicReportWriteOffCO>> getPrepaymentLineWriteInfo(Long prepaymentHeaderId, List<Long> prepaymentLineIds) {
        PrepaymentDocumentIdsCO prepaymentDocumentIdsDTO = new PrepaymentDocumentIdsCO();
        prepaymentDocumentIdsDTO.setHeaderId(prepaymentHeaderId);
        prepaymentDocumentIdsDTO.setLineIds(prepaymentLineIds);
        return paymentClient.listReportWriteOffCO(prepaymentDocumentIdsDTO);

    }

    public  boolean setPushPrepaymentToPayment(List<CashTransactionDataCreateCO> dataCOS) {
        /*ExceptionDetail exceptionDetail = paymentClient.saveTransactionDataBatch(dataCOS);
        if (!"0000".equals(exceptionDetail.getErrorCode())) {
            String errorCode = exceptionDetail.getErrorCode();
            switch (errorCode) {
                case "80108":
                    throw new BizException(RespCode.PREPAY_TOTAL_AMOUNT_ERROR);
                case "80109":
                    throw new BizException(RespCode.PREPAY_WRITTEN_OFF_AMOUNT_LESS_LEAN_ZERO);
                case "80110":
                    throw new BizException(RespCode.PREPAY_WRITTEN_OFF_AMOUNT_MORE_LEAN_ZERO);
                case "80111":
                    throw new BizException(RespCode.PREPAY_NO_PAY_COMPANY);
                case "80112":
                    throw new BizException(RespCode.PREPAY_RECEIVABLES_TYPE_ERROR);
                case "80113":
                    throw new BizException(RespCode.PREPAY_PAYMENT_METHOD_TYPE_ERROR);
                case "80114":
                    throw new BizException(RespCode.PREPAY_CASH_TYPE_ERROR);
                case "80115":
                    throw new BizException(RespCode.PREPAY_CURRENCY_NOT_FOUND);
                case "80116":
                    throw new BizException(RespCode.PREPAY_SOURCE_DOCUMENT_INFO_NULL);
                case "80117":
                    throw new BizException(RespCode.PREPAY_NOT_FOUND_SOURCE_DOCUMENT);
                case "80118":
                    throw new BizException(RespCode.PREPAY_CREATE_IS_NULL);
                default:
                    throw new BizException(exceptionDetail.getErrorCode(), exceptionDetail.getMessage());
            }
        }
        return true;*/

        //bo.liu 支付
//        paymentClient.saveTransactionDataBatch(dataCOS);
        return true;
    }


    /**
     * 通过账套ID->setOfBookId，获取当前账套下，启用的、现金事务类型为PREPAYMENT(预付款) 的 现金事务分类
     *
     * @param setOfBookId
     * @return
     */
    public  List<CashTransactionClassCO> listCashTransactionClassBySetOfBookId(Long setOfBookId) {
        //bo.liu 支付
//        List<CashTransactionClassCO> cashTransactionClassDTOS = persistence.mapAsList(paymentClient.listCashTransactionClassBySetOfBookId(setOfBookId), CashTransactionClassCO.class);
//        return cashTransactionClassDTOS;
        return null;
    }

    /**
     * 根据现金事务分类ID获取详情
     *
     * @param cashTransactionClassId
     * @return
     */
    public CashTransactionClassCO selectCashTransactionClassById(Long cashTransactionClassId) {
        //bo.liu 支付
        //return persistence.map(paymentClient.getCashTransactionClassById(cashTransactionClassId), CashTransactionClassCO.class);
        return null;

    }

    /**
     * 根据现金事务分类ID集合 查询详情
     *
     * @param cashTransactionClassIdList
     * @return
     */
    public  List<CashTransactionClassCO> listCashTransactionClassByIdList(List<Long> cashTransactionClassIdList) {
        //bo.liu 支付
//        List<CashTransactionClassCO> list = persistence.mapAsList(paymentClient.listCashTransactionClassByIdList(cashTransactionClassIdList), CashTransactionClassCO.class);
//        return list;
        return null;
    }

    /**
     * 根据指定范围 查询现金事务分类
     *
     * @param cashTransactionClassForOtherCO
     * @return
     */
    public  List<CashTransactionClassCO> listTransactionClassByRange(CashTransactionClassForOtherCO cashTransactionClassForOtherCO, Page page) throws Exception {
        //bo.liu 支付
//        Page<CashTransactionClassCO>  casPage = paymentClient.listCashTransactionClassForPerPayByRange(cashTransactionClassForOtherCO, page);
//        page.setTotal(casPage.getTotal());
//        List<CashTransactionClassCO> cashTransactionClassDTOs = persistence.mapAsList(
//                casPage.getRecords(),
//                CashTransactionClassCO.class);
//        return cashTransactionClassDTOs;
        return null;

    }
    /**
     * 根据现金事务分类ID ，获取现金事务分类详情 以及其下关联的默认现金流量项
     *
     * @param cashTransactionClassId
     * @return
     */
    public  CashDefaultFlowItemCO selectTransactionClassAndFlowItemById(Long cashTransactionClassId) {
        //bo.liu 支付
//        return paymentClient.getCashDefaultFlowItemByTransactionClassId(cashTransactionClassId);
        return null;
    }


    public  List<Long> getExcludeDocumentCashWriteOffAmountDTOByInput(Double noWriteOffDocumentAmountFrom,Double noWriteOffDocumentAmountTo,Long setOfBooksId){
        //bo.liu 支付

//        return paymentClient.listExcludeDocumentByWriteOffAmount(noWriteOffDocumentAmountFrom != null ?
//                        BigDecimal.valueOf(noWriteOffDocumentAmountFrom) : null,
//                noWriteOffDocumentAmountTo != null ? BigDecimal.valueOf(noWriteOffDocumentAmountTo) : null, setOfBooksId);
        return null;
    }
}
