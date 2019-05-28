package com.hand.hcf.app.prepayment.web.adapter;


//import com.hand.hcf.app.application.dto.ApplicationDTO;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.hand.hcf.app.common.co.CashPaymentRequisitionLineCO;
import com.hand.hcf.app.common.co.SetOfBooksInfoCO;
import com.hand.hcf.app.core.domain.SystemCustomEnumerationType;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.prepayment.domain.CashPayRequisitionType;
import com.hand.hcf.app.prepayment.domain.CashPaymentRequisitionHead;
import com.hand.hcf.app.prepayment.domain.CashPaymentRequisitionLine;
import com.hand.hcf.app.prepayment.externalApi.PaymentModuleInterface;
import com.hand.hcf.app.prepayment.externalApi.PrepaymentHcfOrganizationInterface;
import com.hand.hcf.app.prepayment.service.CashPayRequisitionTypeService;
import com.hand.hcf.app.prepayment.service.CashPaymentRequisitionHeadService;
import com.hand.hcf.app.prepayment.service.CashPaymentRequisitionLineService;
import com.hand.hcf.app.prepayment.service.PrepaymentAttachmentService;
import com.hand.hcf.app.prepayment.utils.RespCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CashPaymentRequisitionLineAdapter {
    @Autowired
    private  CashPaymentRequisitionLineService cashPaymentRequisitionLineService;
    @Autowired
    private  PrepaymentAttachmentService prepaymentAttachmentService;
    @Autowired
    private  CashPayRequisitionTypeService cashPayRequisitionTypeService;
    @Autowired
    private  CashPaymentRequisitionHeadService cashPaymentRequisitionHeadService;

    @Autowired
    private PrepaymentHcfOrganizationInterface prepaymentHcfOrganizationInterface;
    @Autowired
    private PaymentModuleInterface paymentModuleInterface;

    static class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {
        @Override
        public ZonedDateTime deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException {
            JsonToken t = jp.getCurrentToken();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (t == JsonToken.VALUE_STRING) {
                String str = jp.getText().trim();
                if (StringUtils.isEmpty(str)) {
                    return null;
                }
                LocalDate localDate = LocalDate.parse(str, dateTimeFormatter);
                return localDate.atStartOfDay(ZoneId.systemDefault());
            }
            if (t == JsonToken.VALUE_NUMBER_INT) {
                Instant instant = Instant.ofEpochSecond(jp.getLongValue());
                return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
            throw ctxt.mappingException("");
        }
    }

    public  CashPaymentRequisitionLineCO toDTO(CashPaymentRequisitionLine line){
        CashPaymentRequisitionLineCO dto = new CashPaymentRequisitionLineCO();
        BeanUtils.copyProperties(line,dto);
        try {
            dto.setCompanyName(prepaymentHcfOrganizationInterface.getCompanyById(line.getCompanyId()).getName());

        }catch (Exception e){
            e.printStackTrace();
            throw new BizException(RespCode.SYS_COMPANY_INFO_NOT_EXISTS);
        }
        if (line.getPaymentMethodCategory() != null && !line.getPaymentMethodCategory().equals("") ) {
            try {
                dto.setPaymentMethodName(prepaymentHcfOrganizationInterface.getSysCodeValue(SystemCustomEnumerationType.CSH_PAYMENT_TYPE,
                        line.getPaymentMethodCategory(), RespCode.SYS_CODE_TYPE_NOT_EXIT).get(line.getPaymentMethodCategory()));
            } catch (Exception e) {
                e.printStackTrace();
                throw new BizException(RespCode.PREPAY_SYSTEM_VALUE_PAYMENT_METHOD_ERROR);
            }
        }
        if (line.getPaymentType() != null && !line.getPaymentType().equals("") ) {
            dto.setPaymentTypeName(prepaymentHcfOrganizationInterface.getSysCodeValue("ZJ_PAYMENT_TYPE",
                    line.getPaymentType(), RespCode.SYS_CODE_TYPE_NOT_EXIT).get(line.getPaymentType()));
        }

        if(dto.getContractId()!=null){
            //根据合同行id查询合同头行信息
            try{
                //jiu.zhao 合同
				//通过合同服务对外接口获取合同行信息
                /*ContractHeaderLineCO headerLineDTO = ContractModuleInterface.getContractInfoById(dto.getContractId(), dto.getContractLineId());
                dto.setContractId(headerLineDTO.getHeaderId());
                dto.setContractNumber(headerLineDTO.getContractNumber());
                dto.setContractName(headerLineDTO.getContractName());
                dto.setContractLineId(headerLineDTO.getLineId());
                dto.setContractLineNumber(headerLineDTO.getLineNumber().toString());
                dto.setDueDate(headerLineDTO.getDueDate());*/
            }catch (Exception e){
                e.printStackTrace();
                throw new BizException(RespCode.PREPAY_CONTRACT_INFO_ERROR);
            }
            CashPaymentRequisitionHead head = cashPaymentRequisitionHeadService.selectById(line.getPaymentRequisitionHeaderId());
            //查询申请单信息
            if(line.getRefDocumentId()!=null){
//                ApplicationDTO applicationDTO = prepaymentHcfOrganizationInterface.getApplicapayCurrency = nulltionById(line.getRefDocumentId());
//                dto.setRefDocumentTotalAmount(BigDecimal.valueOf(applicationDTO.getTotalAmount()));
            }
        }

        if(dto.getPaymentRequisitionHeaderId()!=null){
            try{
                CashPayRequisitionType requisitionType = cashPayRequisitionTypeService.getCashPayRequisitionType(cashPaymentRequisitionHeadService.selectById(dto.getPaymentRequisitionHeaderId()).getPaymentReqTypeId()).getCashPayRequisitionType();
                dto.setPaymentReqTypeId(requisitionType.getId());
                dto.setTypeName(requisitionType.getTypeName());
            }catch (Exception e){
                throw new BizException(RespCode.PREPAY_CASH_PAY_REQUISITION_TYPE_NOT_EXIST);
            }

        }

        if(dto.getCshTransactionClassId()!=null){
            try{
                //jiu.zhao 支付
                /*CashTransactionClassCO cashTransactionClassDTO = paymentModuleInterface.selectCashTransactionClassById(dto.getCshTransactionClassId());
                dto.setCshTransactionClassName(cashTransactionClassDTO.getDescription());*/
            }catch (Exception e){
                throw new BizException(RespCode.PREPAY_CLASS_NAME_ERROR);
            }
        }

        //关联申请
        if(line.getRefDocumentId()!=null){
            try{
//                ApplicationDTO applicationDTO = prepaymentHcfOrganizationInterface.getApplicationById(line.getRefDocumentId());
//                dto.setRefDocumentOid(applicationDTO.getApplicantOID().toString());
//                dto.setRefDocumentCode(applicationDTO.getBusinessCode());
//                dto.setRefDocumentRemark(applicationDTO.getTitle());
//                dto.setRefDocumentTotalAmount(BigDecimal.valueOf(applicationDTO.getTotalAmount()));
            }catch (Exception e){
                throw new BizException(RespCode.PREPAY_GET_APPLICATION_INFO_ERROR);
            }
        }

        return dto;
    }

    public  CashPaymentRequisitionLine toDomain(CashPaymentRequisitionLineCO co){
        String baseCurrencyCode = "";
        CashPaymentRequisitionLine line = new CashPaymentRequisitionLine();
        SetOfBooksInfoCO setOfBooksInfoCO = prepaymentHcfOrganizationInterface.getSetOfBookById(OrgInformationUtil.getCurrentSetOfBookId());
        if(setOfBooksInfoCO != null){
            baseCurrencyCode = setOfBooksInfoCO.getFunctionalCurrencyCode();
        }
        Double rate = prepaymentHcfOrganizationInterface.selectCurrencyByOtherCurrency(baseCurrencyCode,co.getCurrency()).getRate();
//        Map otherCurrenry = new HashMap<>();
//        Double rate = 0D;
//        String currencyName;
//        try{
//             otherCurrenry = HeliosOrganizationInterface.selectCurrencyByOtherCurrency(dto.getCurrency());
//             rate = Optional
//                    .ofNullable(otherCurrenry)
//                    .map(u -> TypeConversionUtils.parseDouble(u.get("rate")))
//                    .orElseThrow(() ->new BizException(RespCode.SYS_CURRENCY_ERR));
//        }catch ( Exception e){
//            throw new BizException(RespCode.SYS_CURRENCY_ERR);
//        }
        BeanUtils.copyProperties(co,line);
        line.setExchangeRate(rate);
        line.setFunctionAmount(TypeConversionUtils.roundHalfUp(BigDecimal.valueOf(co.getAmount().doubleValue()*rate)));
        return line;
    }


}
