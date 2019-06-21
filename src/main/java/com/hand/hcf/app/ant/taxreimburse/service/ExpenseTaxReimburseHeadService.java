package com.hand.hcf.app.ant.taxreimburse.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpBankFlow;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxReport;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpenseTaxReimburseHead;
import com.hand.hcf.app.ant.taxreimburse.persistence.ExpBankFlowMapper;
import com.hand.hcf.app.ant.taxreimburse.persistence.ExpTaxReportMapper;
import com.hand.hcf.app.ant.taxreimburse.persistence.ExpenseTaxReimburseHeadMapper;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.common.domain.enums.ExpenseDocumentTypeEnum;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.service.CommonService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.contact.domain.Contact;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.currency.domain.CurrencyI18n;
import com.hand.hcf.app.mdata.currency.persistence.CurrencyI18nMapper;
import com.hand.hcf.app.mdata.department.domain.Department;
import com.hand.hcf.app.mdata.department.persistence.DepartmentMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description:国内税金缴纳报账单service
 * @date 2019/6/6 16:11
 */
@Service
public class ExpenseTaxReimburseHeadService extends BaseService<ExpenseTaxReimburseHeadMapper, ExpenseTaxReimburseHead> {

    @Autowired
    ExpenseTaxReimburseHeadMapper expenseTaxReimburseHeadMapper;

    @Autowired
    private CurrencyI18nMapper currencyI18nMapper;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private ExpTaxReportMapper expTaxReportMapper;

    @Autowired
    private ExpBankFlowMapper expBankFlowMapper;

    @Autowired
    private ContactService contactService;

    public final String WARNING2 = "未勾兑的数据不可报账！";

    /**
     * 查询所有报账单头信息
     *
     * @param documentTypeId
     * @param requisitionDateFrom
     * @param requisitionDateTo
     * @param applicantId
     * @param status
     * @param currencyCode
     * @param amountFrom
     * @param amountTo
     * @param remark
     * @param benefitedId
     * @param benefitedDepartName
     * @param requisitionNumber
     * @param page
     * @return
     */
    public List<ExpenseTaxReimburseHead> getTaxReimburseList(
            Long documentTypeId,
            ZonedDateTime requisitionDateFrom,
            ZonedDateTime requisitionDateTo,
            Long applicantId,
            Integer status,
            String currencyCode,
            BigDecimal amountFrom,
            BigDecimal amountTo,
            String remark,
            Long benefitedId,
            String benefitedDepartName,
            String requisitionNumber,
            Page page) {
        Wrapper<ExpenseTaxReimburseHead> wrapper = new EntityWrapper<ExpenseTaxReimburseHead>()
                .eq(documentTypeId != null, "document_type_id", documentTypeId)
                .ge(requisitionDateFrom != null, "requisition_date", requisitionDateFrom)
                .le(requisitionDateTo != null, "requisition_date", requisitionDateTo)
                .eq(applicantId != null, "applicant_id", applicantId)
                .eq(status != null, "status", status)
                .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(currencyCode), "currency_code", currencyCode)
                .ge(amountFrom != null, "total_amount", amountFrom)
                .le(amountTo != null, "total_amount", amountTo)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(remark), "remark", remark)
                .eq(benefitedId != null, "benefited_id", benefitedId)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(benefitedDepartName), "benefited_depart_name", benefitedDepartName)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(requisitionNumber), "requisition_number", requisitionNumber)
                .orderBy("requisition_number", false);
        List<ExpenseTaxReimburseHead> expenseTaxReimburseHeadList = expenseTaxReimburseHeadMapper.selectPage(page, wrapper);
        converDesc(expenseTaxReimburseHeadList);
        return expenseTaxReimburseHeadList;
    }

    /**
     * 分页查询报账单头信息
     *
     * @param documentTypeId
     * @param requisitionDateFrom
     * @param requisitionDateTo
     * @param applicantId
     * @param status
     * @param currencyCode
     * @param amountFrom
     * @param amountTo
     * @param remark
     * @param benefitedId
     * @param benefitedDepartName
     * @param requisitionNumber
     * @param page
     * @return
     */
    public Page<ExpenseTaxReimburseHead> getTaxReimburseHeadByPage(
            Long documentTypeId,
            ZonedDateTime requisitionDateFrom,
            ZonedDateTime requisitionDateTo,
            Long applicantId,
            Integer status,
            String currencyCode,
            BigDecimal amountFrom,
            BigDecimal amountTo,
            String remark,
            Long benefitedId,
            String benefitedDepartName,
            String requisitionNumber,
            Page page) {
        Wrapper<ExpenseTaxReimburseHead> wrapper = new EntityWrapper<ExpenseTaxReimburseHead>()
                .eq(documentTypeId != null, "document_type_id", documentTypeId)
                .ge(requisitionDateFrom != null, "requisition_date", requisitionDateFrom)
                .le(requisitionDateTo != null, "requisition_date", requisitionDateTo)
                .eq(applicantId != null, "applicant_id", applicantId)
                .eq(status != null, "status", status)
                .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(currencyCode), "currency_code", currencyCode)
                .ge(amountFrom != null, "total_amount", amountFrom)
                .le(amountTo != null, "total_amount", amountTo)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(remark), "remark", remark)
                .eq(benefitedId != null, "benefited_id", benefitedId)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(benefitedDepartName), "benefited_depart_name", benefitedDepartName)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(requisitionNumber), "requisition_number", requisitionNumber)
                .orderBy("requisition_number", false);
        List<ExpenseTaxReimburseHead> expenseTaxReimburseHeadList = this.selectPage(page, wrapper).getRecords();
        converDesc(expenseTaxReimburseHeadList);
        return page.setRecords(expenseTaxReimburseHeadList);
    }

    /**
     * 导出国内税金缴纳报账单数据
     *
     * @param documentTypeId
     * @param requisitionDateFrom
     * @param requisitionDateTo
     * @param applicantId
     * @param status
     * @param currencyCode
     * @param amountFrom
     * @param amountTo
     * @param remark
     * @param benefitedId
     * @param benefitedDepartName
     * @param requisitionNumber
     * @return
     */
    public List<ExpenseTaxReimburseHead> exportTaxReimburseHead(
            Long documentTypeId,
            ZonedDateTime requisitionDateFrom,
            ZonedDateTime requisitionDateTo,
            Long applicantId,
            Integer status,
            String currencyCode,
            BigDecimal amountFrom,
            BigDecimal amountTo,
            String remark,
            Long benefitedId,
            String benefitedDepartName,
            String requisitionNumber) {
        Wrapper<ExpenseTaxReimburseHead> wrapper = new EntityWrapper<ExpenseTaxReimburseHead>()
                .eq(documentTypeId != null, "document_type_id", documentTypeId)
                .ge(requisitionDateFrom != null, "requisition_date", requisitionDateFrom)
                .le(requisitionDateTo != null, "requisition_date", requisitionDateTo)
                .eq(applicantId != null, "applicant_id", applicantId)
                .eq(status != null, "status", status)
                .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(currencyCode), "currency_code", currencyCode)
                .ge(amountFrom != null, "total_amount", amountFrom)
                .le(amountTo != null, "total_amount", amountTo)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(remark), "remark", remark)
                .eq(benefitedId != null, "benefited_id", benefitedId)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(benefitedDepartName), "benefited_depart_name", benefitedDepartName)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(requisitionNumber), "requisition_number", requisitionNumber)
                .orderBy("requisition_number", false);
        List<ExpenseTaxReimburseHead> expenseTaxReimburseHeadList = this.selectList(wrapper);
        converDesc(expenseTaxReimburseHeadList);
        return expenseTaxReimburseHeadList;
    }


    /**
     * 转换DESC
     *
     * @param expenseTaxReimburseHeadList
     * @return
     */
    public List<ExpenseTaxReimburseHead> converDesc(List<ExpenseTaxReimburseHead> expenseTaxReimburseHeadList) {
        expenseTaxReimburseHeadList.stream().forEach(expenseTaxReimburseHead -> {
            //报账人转化
            ContactCO userById = organizationService.getUserById(expenseTaxReimburseHead.getApplicantId());
            expenseTaxReimburseHead.setApplicantCode(userById.getEmployeeCode());
            expenseTaxReimburseHead.setApplicantName(userById.getFullName());

            //创建人转化
            ContactCO userByIdone = organizationService.getUserById(expenseTaxReimburseHead.getCreatedBy());
            expenseTaxReimburseHead.setCreateByCode(userByIdone.getEmployeeCode());
            expenseTaxReimburseHead.setCreateByName(userByIdone.getFullName());

            //币种转化
            if (StringUtils.isNotEmpty(expenseTaxReimburseHead.getCurrencyCode())) {
                CurrencyI18n currencyI18n = new CurrencyI18n();
                currencyI18n.setCurrencyCode(expenseTaxReimburseHead.getCurrencyCode());
                String language = OrgInformationUtil.getCurrentLanguage();
                currencyI18n.setLanguage(language);
                CurrencyI18n currencyOne = currencyI18nMapper.selectOne(currencyI18n);
                if (StringUtils.isNotEmpty(currencyOne.getCurrencyName())) {
                    expenseTaxReimburseHead.setCurrencyName(currencyOne.getCurrencyName());
                }
            }
        });
        return expenseTaxReimburseHeadList;
    }

    /**
     * 获取报账单头明细信息
     *
     * @param expenseReportId
     * @return
     */

    public ExpenseTaxReimburseHead getExpenseReportById(Long expenseReportId) {
        ExpenseTaxReimburseHead expenseTaxReimburseHead = expenseTaxReimburseHeadMapper.selectById(expenseReportId);
        //报账人转化
        ContactCO userById = organizationService.getUserById(expenseTaxReimburseHead.getApplicantId());
        if (StringUtils.isNotEmpty(userById.getEmployeeCode())) {
            expenseTaxReimburseHead.setApplicantCode(userById.getEmployeeCode());
        }
        if (StringUtils.isNotEmpty(userById.getFullName())) {
            expenseTaxReimburseHead.setApplicantName(userById.getFullName());
        }
        //报账公司转化
        if (null != expenseTaxReimburseHead.getCompanyId()) {
            Company company = companyService.selectById(expenseTaxReimburseHead.getCompanyId());
            if (StringUtils.isNotEmpty(company.getName())) {
                expenseTaxReimburseHead.setCompanyName(company.getName());
            }
        }
        //报账部门转化
        if (null != expenseTaxReimburseHead.getDepartmentId()) {
            Department department = departmentMapper.selectOneSimpleById(expenseTaxReimburseHead.getDepartmentId());
            if (null != department) {
                expenseTaxReimburseHead.setDepartmentName(department.getName());
            }
        }

        //受益人转化
        ContactCO benefitUser = organizationService.getUserById(expenseTaxReimburseHead.getBenefitedId());
        if (StringUtils.isNotEmpty(benefitUser.getEmployeeCode())) {
            expenseTaxReimburseHead.setBenefitedCode(benefitUser.getEmployeeCode());
        }
        if (StringUtils.isNotEmpty(benefitUser.getFullName())) {
            expenseTaxReimburseHead.setBenefitedName(benefitUser.getFullName());
        }
        //受益公司转化
        if (null != expenseTaxReimburseHead.getBenefitedCompanyId()) {
            Company company = companyService.selectById(expenseTaxReimburseHead.getBenefitedCompanyId());
            if (StringUtils.isNotEmpty(company.getName())) {
                expenseTaxReimburseHead.setBenefitedCompanyName(company.getName());
            }
        }
        //受益部门转化
        /*if (null != expenseTaxReimburseHead.getBenefitedDepartId()) {
            Department department = departmentMapper.selectOneSimpleById(expenseTaxReimburseHead.getBenefitedDepartId());
            if (null != department) {
                expenseTaxReimburseHead.setBenefitedDepartName(department.getName());
            }
        }*/


        //创建人转化
        ContactCO userByIdone = organizationService.getUserById(expenseTaxReimburseHead.getCreatedBy());
        if (StringUtils.isNotEmpty(userByIdone.getEmployeeCode())) {
            expenseTaxReimburseHead.setCreateByCode(userByIdone.getEmployeeCode());
        }
        if (StringUtils.isNotEmpty(userByIdone.getFullName())) {
            expenseTaxReimburseHead.setCreateByName(userByIdone.getFullName());
        }


        //币种转化
        if (StringUtils.isNotEmpty(expenseTaxReimburseHead.getCurrencyCode())) {
            CurrencyI18n currencyI18n = new CurrencyI18n();
            currencyI18n.setCurrencyCode(expenseTaxReimburseHead.getCurrencyCode());
            String language = OrgInformationUtil.getCurrentLanguage();
            currencyI18n.setLanguage(language);
            CurrencyI18n currencyOne = currencyI18nMapper.selectOne(currencyI18n);
            if (StringUtils.isNotEmpty(currencyOne.getCurrencyName())) {
                expenseTaxReimburseHead.setCurrencyName(currencyOne.getCurrencyName());
            }
        }
        return expenseTaxReimburseHead;
    }

    /**
     * 保存新建的报账单头信息，并且发起报账后修改对应数据的状态和外键
     *
     * @param ids
     * @param expenseTaxReimburseHead
     * @return
     */
    public ExpenseTaxReimburseHead saveTaxReimburseHead(String ids, ExpenseTaxReimburseHead expenseTaxReimburseHead) {
        //先在税金报账单中插入数据
        String idsArr[] = null;
        if (null != ids) {
            idsArr = ids.split(",");
        }
        if (expenseTaxReimburseHead.getId() == null) {
            expenseTaxReimburseHead.setTenantId(
                    expenseTaxReimburseHead.getTenantId() != null ? expenseTaxReimburseHead.getTenantId() : OrgInformationUtil.getCurrentTenantId());
            expenseTaxReimburseHead.setSetOfBooksId(
                    expenseTaxReimburseHead.getSetOfBooksId() != null ? expenseTaxReimburseHead.getSetOfBooksId() : OrgInformationUtil.getCurrentSetOfBookId());
            expenseTaxReimburseHead.setRequisitionNumber(
                    commonService.getCoding(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getCategory(), expenseTaxReimburseHead.getCompanyId(), null));
            expenseTaxReimburseHead.setRequisitionDate(
                    expenseTaxReimburseHead.getRequisitionDate() == null ? ZonedDateTime.now() : expenseTaxReimburseHead.getRequisitionDate());
            expenseTaxReimburseHead.setStatus(DocumentOperationEnum.GENERATE.getId());
            //创建人相关信息
            expenseTaxReimburseHead.setCreatedBy(OrgInformationUtil.getCurrentUserId());
            expenseTaxReimburseHead.setCreatedDate(ZonedDateTime.now());
            expenseTaxReimburseHead.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
            expenseTaxReimburseHead.setLastUpdatedDate(ZonedDateTime.now());
            //审批状态
            expenseTaxReimburseHead.setAuditFlag("N");
            expenseTaxReimburseHead.setDocumentOid(UUID.randomUUID().toString());
            BigDecimal sumAmount = BigDecimal.ZERO;
            for (int i = 0; i < idsArr.length; i++) {
                ExpTaxReport expTaxReport = expTaxReportMapper.selectById(idsArr[i]);
                if (null != expTaxReport) {
                    BigDecimal requestAmount = expTaxReport.getRequestAmount();
                    sumAmount = sumAmount.add(requestAmount);
                }
            }
            //总金额
            expenseTaxReimburseHead.setTotalAmount(sumAmount);
            //本币总金额
            expenseTaxReimburseHead.setFunctionalAmount(sumAmount);

            //受益人公司
            if (null != expenseTaxReimburseHead.getBenefitedId()) {
                Long userId = expenseTaxReimburseHead.getBenefitedId();
                Contact contact = contactService.getContactByUserId(userId);
                expenseTaxReimburseHead.setBenefitedCompanyId(contact.getCompanyId());
            }

            //单据类型相关信息
            expenseTaxReimburseHead.setDocumentTypeId(1138438668785152002L);
            expenseTaxReimburseHead.setDocumentTypeName("国内税金缴纳报账单");


            //新建
            int flag = expenseTaxReimburseHeadMapper.insert(expenseTaxReimburseHead);
            if (flag > 0) {
                //插入报账单数据后，获取数据id
                Long taxReimburseHeadId = expenseTaxReimburseHead.getId();
                //然后再修改税金申报数据的报账状态和银行流水数据的报账状态，并且更新外键报账单id,至此，头行数据就关联起来了
                for (int i = 0; i < idsArr.length; i++) {
                    ExpTaxReport expTaxReport = expTaxReportMapper.selectById(idsArr[i]);
                    expTaxReport.setStatus(true);
                    expTaxReport.setExpReimburseHeaderId(taxReimburseHeadId);
                    //更新税金申报的数据状态，并且设置外键taxReimburseHeadId
                    System.out.println("id" + expTaxReport.getId());
                    expTaxReportMapper.updateStatusById(Long.valueOf(idsArr[i]), taxReimburseHeadId);
                }
                Map<Long, String> map = getMap(ids);
                for (Map.Entry<Long, String> entry : map.entrySet()) {
                    Long companyId = entry.getKey();
                    String currencyCode = entry.getValue();
                    //根据公司和币种更新银行流水的数据，并且设置外键taxReimburseHeadId
                    expBankFlowMapper.updateStatusByGroup(companyId, currencyCode, taxReimburseHeadId);
                }
            }
        } else {
            //头信息编辑的保存
            expenseTaxReimburseHeadMapper.updateById(expenseTaxReimburseHead);
        }
        return expenseTaxReimburseHead;
    }

    /**
     * 根据选择的税金申报数据的id,返回涉及到的公司和币种的kv组合，以便判断是否全部将同组合的数据勾选，然后查询出对应已勾兑的银行流水的数据
     *
     * @param ids
     * @return
     */
    public Map<Long, String> getMap(String ids) {
        Map<Long, String> map = new HashMap<>();
        String idsArr[] = ids.split(",");
        //将所有id涉及到的companyId、currencyCode组合加入到map中，然后进行比较。
        for (int i = 0; i < idsArr.length; i++) {
            ExpTaxReport expTaxReport = expTaxReportMapper.selectById(idsArr[i]);
            Long companyId = expTaxReport.getCompanyId();
            String currencyCode = expTaxReport.getCurrencyCode();
            boolean blendStatus = expTaxReport.getBlendStatus();
            boolean status = expTaxReport.getStatus();
            if ((blendStatus || blendStatus == true) && (status == true)) {
                //当map中不存在此key,或者不存在此vlaue就加入--只有两种同时存在则不加入
                while (!map.containsKey(companyId) || !map.containsValue(currencyCode)) {
                    map.put(companyId, currencyCode);
                }
            } else {
                throw new BizException(WARNING2);
            }
        }
        return map;
    }

    /**
     * 提交--修改状态
     *
     * @param documentId
     * @return
     */
    public boolean submit(String documentId) {
        Boolean flag = false;
        if (StringUtils.isNotEmpty(documentId)) {
            ExpenseTaxReimburseHead expenseTaxReimburseHead = expenseTaxReimburseHeadMapper.selectById(documentId);
            if (null != expenseTaxReimburseHead) {
                if (expenseTaxReimburseHead.getStatus() == 1001) {
                    expenseTaxReimburseHead.setStatus(1002);
                }
            }
            int success = expenseTaxReimburseHeadMapper.updateById(expenseTaxReimburseHead);
            if (success > 0) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 撤回--修改状态
     *
     * @param documentId
     * @return
     */
    public boolean withdraw(String documentId) {
        Boolean flag = false;
        if (StringUtils.isNotEmpty(documentId)) {
            ExpenseTaxReimburseHead expenseTaxReimburseHead = expenseTaxReimburseHeadMapper.selectById(documentId);
            if (null != expenseTaxReimburseHead) {
                if (expenseTaxReimburseHead.getStatus() == 1002) {
                    expenseTaxReimburseHead.setStatus(1001);
                }
            }
            int success = expenseTaxReimburseHeadMapper.updateById(expenseTaxReimburseHead);
            if (success > 0) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 删除--删除头信息，修改行数据状态
     *
     * @param documentId
     * @return
     */
    public boolean deleteById(String documentId) {
        Boolean flag = false;
        List<ExpTaxReport> expTaxReportList = new ArrayList<>();
        List<ExpBankFlow> expBankFlowList = new ArrayList<>();
        if (StringUtils.isNotEmpty(documentId)) {
            Wrapper<ExpTaxReport> taxReportWrapper = new EntityWrapper<ExpTaxReport>()
                    .eq(documentId != null, "exp_reimburse_header_id", documentId);
            expTaxReportList = expTaxReportMapper.selectList(taxReportWrapper);
            Wrapper<ExpBankFlow> expBankFlowWrapper = new EntityWrapper<ExpBankFlow>()
                    .eq(documentId != null, "exp_reimburse_header_id", documentId);
            expBankFlowList = expBankFlowMapper.selectList(expBankFlowWrapper);

            ExpenseTaxReimburseHead expenseTaxReimburseHead = expenseTaxReimburseHeadMapper.selectById(documentId);
            if (null != expenseTaxReimburseHead) {
                int taxUpdates = 0;
                int taxBanks = 0;
                if (expTaxReportList.size() > 0) {
                    taxUpdates = expTaxReportMapper.updateTaxByHeadId(Long.valueOf(documentId));
                }
                if (expBankFlowList.size() > 0) {
                    taxBanks = expBankFlowMapper.updateBankFlowByHeadId(Long.valueOf(documentId));
                }
                int success = expenseTaxReimburseHeadMapper.deleteById(expenseTaxReimburseHead);
                if (taxUpdates + taxBanks + success > 0) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    /**
     * 批量-报账单删除-只有编辑中的才可删除，并且修改税金/银行数据状态
     *
     * @param ids
     * @return
     */
    public boolean deleteReimburseBatchs(String ids) {
        Boolean flag = false;
        String idsArr[] = ids.split(",");
        for (int i = 0; i < idsArr.length; i++) {
            ExpenseTaxReimburseHead expenseTaxReimburseHead = expenseTaxReimburseHeadMapper.selectById(idsArr[i]);
            if (null != expenseTaxReimburseHead && expenseTaxReimburseHead.getStatus() == 1001) {
                flag = deleteById(idsArr[i]);
            } else {
                return flag;
            }
        }
        return flag;
    }


}
