package com.hand.hcf.app.payment.web.adapter;

import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.co.ExpenseReportTypeCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.expense.report.implement.web.ExpenseReportTypControllerImpl;
import com.hand.hcf.app.payment.domain.PaymentCompanyConfig;
import com.hand.hcf.app.payment.domain.PaymentRequisitionTypes;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.externalApi.PaymentPrepaymentService;
import com.hand.hcf.app.payment.service.PaymentRequisitionTypesService;
import com.hand.hcf.app.payment.utils.RespCode;
import com.hand.hcf.app.payment.web.dto.PaymentCompanyConfigDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by 刘亮 on 2017/9/29.
 */
@Component
public class PaymentCompanyConfigAdapter {

    @Autowired
    private PaymentOrganizationService organizationService;
    @Autowired
    private PaymentPrepaymentService prepaymentService;
    @Autowired
    private PaymentRequisitionTypesService paymentRequisitionTypesService;
    @Autowired
    private ExpenseReportTypControllerImpl expenseReportTypeClient;


    /*public PaymentCompanyConfigAdapter(CompanyService companyService,ExpenseTypeService expenseTypeService,PrePaymentAPIService prePaymentAPIService,CustomFormMapper customFormMapper,PaymentAPI paymentAPI){
        PaymentCompanyConfigAdapter.companyService=companyService;
        PaymentCompanyConfigAdapter.expenseTypeService = expenseTypeService;
        PaymentCompanyConfigAdapter.prePaymentAPIService = prePaymentAPIService;
        PaymentCompanyConfigAdapter.customFormMapper = customFormMapper;
        PaymentCompanyConfigAdapter.paymentAPI= paymentAPI;
    }*/


    //    @Autowired
//    public PaymentCompanyConfigAdapter(CompanyService companyService,ExpenseTypeService expenseTypeService){
//        PaymentCompanyConfigAdapter.companyService=companyService;
//        PaymentCompanyConfigAdapter.expenseTypeService = expenseTypeService;
//    }

    public PaymentCompanyConfigDTO toDTO(PaymentCompanyConfig domain) {
        PaymentCompanyConfigDTO dto = new PaymentCompanyConfigDTO();
        System.out.println(organizationService.toString());
        CompanyCO company = organizationService.getById(domain.getCompanyId());
        dto.setCompanyCode(company.getCompanyCode());
        dto.setCompanyName(company.getName());
        CompanyCO pamentCompany = organizationService.getById(domain.getPaymentCompanyId());
        dto.setPaymentCompanyCode(pamentCompany.getCompanyCode());
        dto.setPaymentCompanyName(pamentCompany.getName());
        BeanUtils.copyProperties(domain, dto);
        if (domain.getDucumentTypeId() != null) {
            switch (domain.getDucumentCategory()) {
                case "PREPAYMENT_REQUISITION"://预付款单
                    String typeName = prepaymentService.getPrepaymentTypeByID(domain.getDucumentTypeId());
                    if (typeName == null) {
                        throw new BizException(RespCode.PAYMENT_DOCUMENT_TYPE_NO_DATA_FOUND);
                    }
                    dto.setDucumentType(typeName);
                    break;
                case "PUBLIC_REPORT"://对公报账单
                    ExpenseReportTypeCO reportType = expenseReportTypeClient.getExpenseReportTypeById(domain.getDucumentTypeId());
                    if(reportType==null){
                    throw new BizException(RespCode.PAYMENT_DOCUMENT_TYPE_NO_DATA_FOUND);
                    }
                    dto.setDucumentType(reportType.getReportTypeName());
                    break;
                case "ACP_REQUISITION"://付款申请单
                    PaymentRequisitionTypes paymentRequisitionType = paymentRequisitionTypesService.getTypesById(domain.getDucumentTypeId());
                    if (paymentRequisitionType == null) {
                        throw new BizException(RespCode.PAYMENT_DOCUMENT_TYPE_NO_DATA_FOUND);
                    }
                    dto.setDucumentType(paymentRequisitionType.getDescription());
                    break;

            }
        }
        return dto;
    }
}
