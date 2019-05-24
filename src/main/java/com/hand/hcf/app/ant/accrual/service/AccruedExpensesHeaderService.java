package com.hand.hcf.app.ant.accrual.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.accrual.domain.AccruedExpensesHeader;
import com.hand.hcf.app.ant.accrual.persistence.AccruedExpensesHeaderMapper;
import com.hand.hcf.app.common.co.ApprovalFormCO;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.CurrencyRateCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualType;
import com.hand.hcf.app.expense.accrual.service.ExpenseAccrualTypeService;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.currency.domain.CurrencyI18n;
import com.hand.hcf.app.mdata.currency.persistence.CurrencyI18nMapper;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.implement.web.CompanyControllerImpl;
import com.hand.hcf.app.payment.utils.RespCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/5/21
 */
@Service
@Slf4j
public class AccruedExpensesHeaderService extends BaseService<AccruedExpensesHeaderMapper, AccruedExpensesHeader> {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ExpenseAccrualTypeService expenseAccrualTypeService;

    @Autowired
    private CompanyControllerImpl hcfOrganizationInterface;

    @Autowired
    private CurrencyI18nMapper currencyI18nMapper;


    /**
     * 预提单
     *
     * @param requisitionNumber
     * @param requisitionDateFrom
     * @param requisitionDateTo
     * @param companyId
     * @param documentTypeId
     * @param demanderId
     * @param currencyCode
     * @param amountFrom
     * @param amountTo
     * @param editor
     * @param passed
     * @param page
     * @return
     */
    public List<AccruedExpensesHeader> getExpenseReports(String requisitionNumber,
                                                         ZonedDateTime requisitionDateFrom,
                                                         ZonedDateTime requisitionDateTo,
                                                         Long companyId,
                                                         Long documentTypeId,
                                                         Long demanderId,
                                                         String currencyCode,
                                                         BigDecimal amountFrom,
                                                         BigDecimal amountTo,
                                                         Boolean editor,
                                                         Boolean passed,
                                                         Page page) {
        Long currentUserId = OrgInformationUtil.getCurrentUserId();
        Wrapper<AccruedExpensesHeader> wrapper = new EntityWrapper<AccruedExpensesHeader>()
                .eq("created_by", currentUserId)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(requisitionNumber), "requisition_number", requisitionNumber)
                .ge(requisitionDateFrom != null, "requisition_date", requisitionDateFrom)
                .le(requisitionDateTo != null, "requisition_date", requisitionDateTo)
                .eq(companyId != null, "companyId", companyId)
                .eq(documentTypeId != null, "document_type_id", documentTypeId)
                .eq(demanderId != null, "demander_id", demanderId)
                .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(currencyCode), "currency_code", currencyCode)
                .ge(amountFrom != null, "total_amount", amountFrom)
                .le(amountTo != null, "total_amount", amountTo)
                .orderBy("requisition_number", false);
        /*if (editor) {
            wrapper = wrapper.in("status", "1001,1003,1005,2001");
        } else  if (passed) {
            wrapper = wrapper.in("status", "1004,2002");
        } else {
            wrapper = wrapper.eq(status != null, "status", status);
        }*/
        List<AccruedExpensesHeader> accruedExpensesHeaders = baseMapper.selectPage(page, wrapper);
        accruedExpensesHeaders.stream().forEach(accruedExpensesHeader -> {
            ContactCO userById = organizationService.getUserById(accruedExpensesHeader.getApplicantId());
            //申请人转化
            accruedExpensesHeader.setApplicantCode(userById.getEmployeeCode());
            accruedExpensesHeader.setApplicantName(userById.getFullName());
            //责任人转化
            accruedExpensesHeader.setDemanderCode(userById.getEmployeeCode());
            accruedExpensesHeader.setDemanderName(userById.getFullName());
            // 单据类型转化
            ExpenseAccrualType expenseAccrualType = expenseAccrualTypeService.selectById(accruedExpensesHeader.getDocumentTypeId());
            accruedExpensesHeader.setDocumentTypeName(expenseAccrualType.getExpAccrualTypeName());
            accruedExpensesHeader.setFormId(expenseAccrualType.getFormId());
            ApprovalFormCO approvalFormById = organizationService.getApprovalFormById(expenseAccrualType.getFormId());
            accruedExpensesHeader.setFormOid(approvalFormById.getFormOid());
            //期间范围转化From-To
            String fromDate = accruedExpensesHeader.getFromDate();
            String toDate = accruedExpensesHeader.getToDate();
            if (StringUtils.isNotEmpty(fromDate)||StringUtils.isNotEmpty(toDate)) {
                accruedExpensesHeader.setFromToPeriod(fromDate+ "-" +toDate);
            }

            //公司转化
            Map<Long, String> comanyMap = new HashMap<Long, String>();
            if (comanyMap.get(accruedExpensesHeader.getCompanyId()) != null) {
                accruedExpensesHeader.setCompanyName(comanyMap.get(accruedExpensesHeader.getCompanyId()));
            } else {
                CompanyCO otherCompany = hcfOrganizationInterface.getById(accruedExpensesHeader.getCompanyId());
                String companyName = Optional
                        .ofNullable(otherCompany)
                        .map(u -> TypeConversionUtils.parseString(u.getName()))
                        .orElseThrow(() -> new BizException(RespCode.SYS_COMPANY_INFO_NOT_EXISTS));
                comanyMap.put(accruedExpensesHeader.getCompanyId(), companyName);
                accruedExpensesHeader.setCompanyName(companyName);
            }

            //币种转化
            if (StringUtils.isNotEmpty(accruedExpensesHeader.getCurrencyCode())) {
                CurrencyI18n currencyI18n = new CurrencyI18n();
                currencyI18n.setCurrencyCode(accruedExpensesHeader.getCurrencyCode());
                String language = OrgInformationUtil.getCurrentLanguage();
                currencyI18n.setLanguage(language);
                CurrencyI18n currencyOne = currencyI18nMapper.selectOne(currencyI18n);
                if(StringUtils.isNotEmpty(currencyOne.getCurrencyName())){
                    accruedExpensesHeader.setCurrencyName(currencyOne.getCurrencyName());
                }
            }
        });
        return accruedExpensesHeaders;

    }
}
