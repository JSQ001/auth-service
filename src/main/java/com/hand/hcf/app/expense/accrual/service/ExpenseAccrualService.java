package com.hand.hcf.app.expense.accrual.service;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.SetOfBooksInfoCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualAssign;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualAssignDepartment;
import com.hand.hcf.app.expense.accrual.dto.ExpenseReportAccrualRequestDTO;
import com.hand.hcf.app.expense.accrual.persistence.ExpenseAccrualMapper;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.report.service.ExpenseReportTypeExpenseTypeService;
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
import java.util.List;


/**
 * <p>
 *     申请类型Service
 * </p>
 *
 * @Author: dazhuang.xie
 * @Date: 2019/6/24
 */
@Service
public class ExpenseAccrualService extends BaseService<ExpenseAccrualMapper, ExpenseAccrualAssign> {

    @Autowired
    private  ExpenseAccrualMapper expenseAccrualMapper;

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
     * @param enabled  启用
     * @param page  分页信息
     * @return
     */
    public List<ExpenseAccrualAssign> getExpenseReportTypeByCond(Long setOfBooksId, String typeCode, String typeName, Boolean enabled, Page page, boolean dataAuthFlag){
//        ExpenseAccrualAssign queryParams = new ExpenseAccrualAssign(typeCode, setOfBooksId);
//        queryParams.setTypeName(typeName);
//        queryParams.setEnabled(enabled);
//
//        String dataAuthLabel = null;
//        if(dataAuthFlag){
//            Map<String,String> map = new HashMap<>();
//            map.put(DataAuthorityUtil.TABLE_NAME,"exp_report_accrual");
//            map.put(DataAuthorityUtil.SOB_COLUMN,"set_of_books_id");
//            dataAuthLabel = DataAuthorityUtil.getDataAuthLabel(map);
//        }
        int isbig = -1;
        if(enabled != null){
            if(enabled){
                isbig = 1;
            }else{
                isbig = 0;
            }
        }
        List<ExpenseAccrualAssign> list = baseMapper.selectPage(page,
                new EntityWrapper<ExpenseAccrualAssign>()
                        .where("1=1")
                        .eq("set_of_books_id",setOfBooksId)
                        .like(typeCode != null, "type_code",typeCode, SqlLike.DEFAULT)
                        .like(typeName != null, "type_name",typeName, SqlLike.DEFAULT)
                        .eq(enabled != null, "enabled",isbig)
                        .orderBy("typeCode")
        );
        for(int i = 0;i<list.size();i++){
            list.get(i).setRn(i+1);
        }
//        if (!CollectionUtils.isEmpty(list)){
//            SetOfBooksInfoCO setOfBooksInfoDTO = organizationService.getSetOfBooksInfoCOById(setOfBooksId, false);
//            list.forEach(e -> {
//                e.setSetOfBooksName(setOfBooksInfoDTO.getSetOfBooksCode() + "-" + setOfBooksInfoDTO.getSetOfBooksName());
//                if(!StringUtil.isNullOrEmpty(e.getFormOid())) {
//                    ApprovalFormCO approvalFormCO = organizationService.getApprovalFormByOid(e.getFormOid());
//                    if (null != approvalFormCO) {
//                        e.setFormName(approvalFormCO.getFormName());
//                    }
//                }
//            });
//        }
        return list;
    }

    public ExpenseReportAccrualRequestDTO getExpenseReportAccrual(Long id){
        ExpenseReportAccrualRequestDTO expenseReportTypeRequestDTO = new ExpenseReportAccrualRequestDTO();
        //返回报账单类型数据
        ExpenseAccrualAssign expenseReportType = this.selectById(id);
        SetOfBooksInfoCO setOfBooksInfoCOById = organizationService.getSetOfBooksInfoCOById(expenseReportType.getSetOfBooksId(), false);
        if (setOfBooksInfoCOById != null) {
            //expenseReportType.setSetOfBooksCode(setOfBooksInfoCOById.getSetOfBooksCode());
            expenseReportType.setSetOfBooksName(setOfBooksInfoCOById.getSetOfBooksName());
        }
//        //返回付款方式name
//        SysCodeValueCO sysCodeValue = organizationService.getSysCodeValueByCodeAndValue("ZJ_PAYMENT_TYPE", expenseReportType.getPaymentType());
//        if (sysCodeValue != null) {
//            expenseReportType.setPaymentTypeName(sysCodeValue.getName());
//        }
        //返回关联表单名称formName
//        ApprovalFormCO approvalFormById = organizationService.getApprovalFormById(expenseReportType.getFormId());
//        if (approvalFormById != null){
//            expenseReportType.setFormName(approvalFormById.getFormName());
//        }
        expenseReportTypeRequestDTO.setExpenseAccrualAssign(expenseReportType);

        return expenseReportTypeRequestDTO;
    }

    /**
     * 新增 报账单类型
     * @param expenseReportAccrualRequestDTO
     * @return
     */
    @Transactional
    public ExpenseAccrualAssign createExpenseReportAccrual(ExpenseReportAccrualRequestDTO expenseReportAccrualRequestDTO){
        ExpenseAccrualAssign applicationType = expenseReportAccrualRequestDTO.getExpenseAccrualAssign();
        //List<ApplicationTypeAssignUser> userInfos = typeDTO.getUserInfos();
        //List<ApplicationTypeAssignType> expenseTypeInfos = typeDTO.getExpenseTypeInfos();
        // 逻辑校验和逻辑赋值
       // checkUnique(applicationType, userInfos, expenseTypeInfos, true);
        // 保存类型
        applicationType.setId(null);
        this.insert(applicationType);
        // 适用人员 和申请类型
        //insertAssignUser(userInfos, applicationType,expenseTypeInfos);
        return applicationType;
    }
    /**
     * 修改 报账单类型
     * @param expenseReportAccrualRequestDTO
     * @return
     */
    @Transactional
    public ExpenseAccrualAssign updateExpenseReportAccrual(ExpenseReportAccrualRequestDTO expenseReportAccrualRequestDTO){
        //修改报账单类型表
        ExpenseAccrualAssign expenseIncomeAssign = expenseReportAccrualRequestDTO.getExpenseAccrualAssign();
        ExpenseAccrualAssign reportIncome = expenseAccrualMapper.selectById(expenseIncomeAssign.getId());
        if (reportIncome == null){
            throw new BizException(RespCode.EXPENSE_REPORT_TYPE_NOT_EXIST);
        }
        //expenseIncomeAssign.setReportTypeCode(reportIncome.getReportTypeCode());
        this.updateAllColumnById(expenseIncomeAssign);
        return  expenseIncomeAssign;
    }


    public ResponseEntity<List<ExpenseAccrualAssignDepartment>> getExpenseReportTypeDepartmentByCond(
            @ApiParam(value = "报账类型ID") @RequestParam(value = "reportTypeId") Long reportTypeId,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        //page page = pageutil.getpage(pageable);
        List<ExpenseAccrualAssignDepartment> list = expenseAccrualMapper.queryAccrualDepartmentInfo(reportTypeId);
        //HttpHeaders headers = PageUtil.getTotalHeader(page);
//        return new ResponseEntity(list,headers, HttpStatus.OK);
        return  new ResponseEntity(list, HttpStatus.OK);
    }

    public ResponseEntity<List<ExpenseAccrualAssignDepartment>> getExpenseDepartmentFilter(
            @ApiParam(value = "单据编号") @RequestParam(value = "reportTypeId",required = false) Long reportTypeId,
            @ApiParam(value = "部门代码") @RequestParam(value = "departmentCode",required = false) String departmentCode,
            @ApiParam(value = "部门名称") @RequestParam(value = "name",required = false) String name,
            @ApiParam(value = "部门代码从") @RequestParam(value = "departmentFrom",required = false) String departmentFrom,
            @ApiParam(value = "部门代码至") @RequestParam(value = "departmentTo",required = false) String departmentTo,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        List<ExpenseAccrualAssignDepartment> list = expenseAccrualMapper.queryAccrualDepartmentFilter(reportTypeId,departmentCode,name,departmentFrom,departmentTo);
        return  new ResponseEntity(list, HttpStatus.OK);
    }

    public void distributionDepartment(List<ExpenseAccrualAssignDepartment> list){
        for(int i=0;i<list.size();i++){
            expenseAccrualMapper.distributionDepartment(list.get(i));
        }
    }

    public void changeDepartmentStatus(ExpenseAccrualAssignDepartment expenseTypeAssignDepartment){
        expenseAccrualMapper.changeDepartmentStatus(expenseTypeAssignDepartment);
    }
}
