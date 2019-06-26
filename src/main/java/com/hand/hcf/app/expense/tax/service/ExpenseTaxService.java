package com.hand.hcf.app.expense.tax.service;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ApprovalFormCO;
import com.hand.hcf.app.common.co.SetOfBooksInfoCO;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.report.service.ExpenseReportTypeExpenseTypeService;
import com.hand.hcf.app.expense.tax.domain.ExpenseTaxAssign;
import com.hand.hcf.app.expense.tax.domain.ExpenseTaxAssignDepartment;
import com.hand.hcf.app.expense.tax.dto.ExpenseReportTaxRequestDTO;
import com.hand.hcf.app.expense.tax.persistence.ExpenseTaxMapper;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 *     申请类型Service
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/6
 */
@Service
public class ExpenseTaxService extends BaseService<ExpenseTaxMapper, ExpenseTaxAssign> {

    @Autowired
    private ExpenseTaxMapper expenseTaxMapper;

    @Autowired
    private BaseI18nService baseI18nService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ExpenseReportTypeExpenseTypeService expenseReportTypeExpenseTypeService;

    /**
     * 自定义条件查询 报账单类型(分页)
     *
     * @param setOfBooksId  账套
     * @param reportTypeCode 报账单code
     * @param reportTypeName 报账单名称
     * @param enabled  启用
     * @param page  分页信息
     * @return
     */
    public List<ExpenseTaxAssign> getExpenseReportTypeByCond(Long setOfBooksId, String reportTypeCode, String reportTypeName, Boolean enabled, Page page){
        List<ExpenseTaxAssign> list = new ArrayList<>();

        if (setOfBooksId == null){
            return list;
        }
        list = expenseTaxMapper.selectPage(page,
                new EntityWrapper<ExpenseTaxAssign>()
                        .where("deleted = false")
                        .eq("set_of_books_id",setOfBooksId)
                        .like(reportTypeCode != null, "report_type_code",reportTypeCode, SqlLike.DEFAULT)
                        .like(reportTypeName != null, "report_type_name",reportTypeName, SqlLike.DEFAULT)
                        .eq(enabled != null, "enabled",enabled)
                        .orderBy("enabled",false)
                        .orderBy("report_type_code")
                );

        //list = baseI18nService.selectListTranslatedTableInfoWithI18nByEntity(list, ExpenseIncomeAssign.class);
        SetOfBooksInfoCO setOfBooks = organizationService.getSetOfBooksInfoCOById(setOfBooksId, false);
        Map<String, String> sysCodeValueMap = organizationService.mapSysCodeValueByCode(
                "ZJ_PAYMENT_TYPE");
        Map<Long, String> formNameMap = new HashMap<>(16);
        for (ExpenseTaxAssign expenseReportType : list){
            //返回账套code、账套name
            if (setOfBooks != null) {
                expenseReportType.setSetOfBooksCode(setOfBooks.getSetOfBooksCode());
                expenseReportType.setSetOfBooksName(setOfBooks.getSetOfBooksName());
            }
            //返回付款方式类型name
//            expenseReportType.setPaymentMethodName(sysCodeValueMap.get(expenseReportType.getPaymentMethod()));
            //返回付款方式name
            expenseReportType.setPaymentTypeName(sysCodeValueMap.get(expenseReportType.getPaymentType()));

            //返回关联表单名称formName
            if (!formNameMap.containsKey(expenseReportType.getFormId())) {
                ApprovalFormCO formCO = organizationService.getApprovalFormById(expenseReportType.getFormId());
                if (formCO != null){
                    formNameMap.put(expenseReportType.getFormId(), formCO.getFormName());
                }
            }
            expenseReportType.setFormName(formNameMap.get(expenseReportType.getFormId()));
        }
        for(int i = 0;i<list.size();i++){
            list.get(i).setRn(i+1);
        }
        return list;
    }

    public ExpenseReportTaxRequestDTO getExpenseReportTax(Long id){
        ExpenseReportTaxRequestDTO expenseReportTypeRequestDTO = new ExpenseReportTaxRequestDTO();
        //返回报账单类型数据
        ExpenseTaxAssign expenseReportType = this.selectById(id);
        SetOfBooksInfoCO setOfBooksInfoCOById = organizationService.getSetOfBooksInfoCOById(expenseReportType.getSetOfBooksId(), false);
        if (setOfBooksInfoCOById != null) {
            expenseReportType.setSetOfBooksCode(setOfBooksInfoCOById.getSetOfBooksCode());
            expenseReportType.setSetOfBooksName(setOfBooksInfoCOById.getSetOfBooksName());
        }
        //返回付款方式name
        SysCodeValueCO sysCodeValue = organizationService.getSysCodeValueByCodeAndValue("ZJ_PAYMENT_TYPE", expenseReportType.getPaymentType());
        if (sysCodeValue != null) {
            expenseReportType.setPaymentTypeName(sysCodeValue.getName());
        }
        //返回关联表单名称formName
        ApprovalFormCO approvalFormById = organizationService.getApprovalFormById(expenseReportType.getFormId());
        if (approvalFormById != null){
            expenseReportType.setFormName(approvalFormById.getFormName());
        }
        expenseReportTypeRequestDTO.setExpenseTaxAssign(expenseReportType);

        return expenseReportTypeRequestDTO;
    }

    /**
     * 新增 报账单类型
     * @param expenseReportTaxRequestDTO
     * @return
     */
    @Transactional
    public ExpenseTaxAssign createExpenseReportTax(ExpenseReportTaxRequestDTO expenseReportTaxRequestDTO){
        //插入报账单类型表
        ExpenseTaxAssign expenseTaxAssign = expenseReportTaxRequestDTO.getExpenseTaxAssign();
        if (expenseTaxAssign.getId() != null){
            throw new BizException(RespCode.EXPENSE_REPORT_TYPE_ALREADY_EXISTS);
        }
        if (expenseTaxMapper.selectList(
                new EntityWrapper<ExpenseTaxAssign>()
                        .eq("set_of_books_id",expenseTaxAssign.getSetOfBooksId())
                        .eq("report_type_code",expenseTaxAssign.getReportTypeCode())
        ).size() > 0){
            throw new BizException(RespCode.EXPENSE_REPORT_TYPE_CODE_NOT_ALLOWED_TO_REPEAT);
        }
        this.insert(expenseTaxAssign);
        return  expenseTaxAssign;
    }
    /**
     * 修改 报账单类型
     * @param expenseReportTaxRequestDTO
     * @return
     */
    @Transactional
    public ExpenseTaxAssign updateExpenseReportTax(ExpenseReportTaxRequestDTO expenseReportTaxRequestDTO){
        //修改报账单类型表
        ExpenseTaxAssign expenseIncomeAssign = expenseReportTaxRequestDTO.getExpenseTaxAssign();
        ExpenseTaxAssign reportIncome = expenseTaxMapper.selectById(expenseIncomeAssign.getId());
        if (reportIncome == null){
            throw new BizException(RespCode.EXPENSE_REPORT_TYPE_NOT_EXIST);
        }
        expenseIncomeAssign.setReportTypeCode(reportIncome.getReportTypeCode());
        this.updateAllColumnById(expenseIncomeAssign);
        return  expenseIncomeAssign;
    }


    public ResponseEntity<List<ExpenseTaxAssignDepartment>> getExpenseReportTaxDepartmentByCond(
            @ApiParam(value = "报账类型ID") @RequestParam(value = "reportTypeId") Long reportTypeId,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        //page page = pageutil.getpage(pageable);
        List<ExpenseTaxAssignDepartment> list = expenseTaxMapper.queryTaxDepartmentInfo(reportTypeId);
        //HttpHeaders headers = PageUtil.getTotalHeader(page);
//        return new ResponseEntity(list,headers, HttpStatus.OK);
        return  new ResponseEntity(list, HttpStatus.OK);
    }

    public ResponseEntity<List<ExpenseTaxAssignDepartment>> getExpenseDepartmentFilter(
            @ApiParam(value = "单据编号") @RequestParam(value = "reportTypeId",required = false) Long reportTypeId,
            @ApiParam(value = "部门代码") @RequestParam(value = "departmentCode",required = false) String departmentCode,
            @ApiParam(value = "部门名称") @RequestParam(value = "name",required = false) String name,
            @ApiParam(value = "部门代码从") @RequestParam(value = "departmentFrom",required = false) String departmentFrom,
            @ApiParam(value = "部门代码至") @RequestParam(value = "departmentTo",required = false) String departmentTo,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        List<ExpenseTaxAssignDepartment> list = expenseTaxMapper.queryTaxDepartmentFilter(reportTypeId,departmentCode,name,departmentFrom,departmentTo);
        return  new ResponseEntity(list, HttpStatus.OK);
    }

    public void distributionDepartment(List<ExpenseTaxAssignDepartment> list){
        for(int i=0;i<list.size();i++){
            expenseTaxMapper.distributionDepartment(list.get(i));
        }
    }

    public void changeDepartmentStatus(ExpenseTaxAssignDepartment expenseTypeAssignDepartment){
        expenseTaxMapper.changeDepartmentStatus(expenseTypeAssignDepartment);
    }
}
