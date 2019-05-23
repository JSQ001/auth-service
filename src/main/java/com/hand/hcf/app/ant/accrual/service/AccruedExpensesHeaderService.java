package com.hand.hcf.app.ant.accrual.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.accrual.domain.AccruedExpensesHeader;
import com.hand.hcf.app.ant.accrual.persistence.AccruedExpensesHeaderMapper;
import com.hand.hcf.app.common.co.ApprovalFormCO;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualType;
import com.hand.hcf.app.expense.accrual.service.ExpenseAccrualTypeService;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

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
            accruedExpensesHeader.setApplicantCode(userById.getEmployeeCode());
            accruedExpensesHeader.setApplicantName(userById.getFullName());
            // 单据类型
            ExpenseAccrualType expenseAccrualType = expenseAccrualTypeService.selectById(accruedExpensesHeader.getDocumentTypeId());
            accruedExpensesHeader.setDocumentTypeName(expenseAccrualType.getExpAccrualTypeName());
            accruedExpensesHeader.setFormId(expenseAccrualType.getFormId());
            ApprovalFormCO approvalFormById = organizationService.getApprovalFormById(expenseAccrualType.getFormId());
            accruedExpensesHeader.setFormOid(approvalFormById.getFormOid());
        });
        return accruedExpensesHeaders;

    }
}
