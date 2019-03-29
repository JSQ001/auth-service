package com.hand.hcf.app.expense.report.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import com.hand.hcf.app.expense.report.domain.ExpenseReportTypeCompany;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportTypeCompanyMapper;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/26
 */
@Service
@Transactional
public class ExpenseReportTypeCompanyService extends BaseService<ExpenseReportTypeCompanyMapper,ExpenseReportTypeCompany>{

    @Autowired
    private ExpenseReportTypeCompanyMapper expenseReportTypeCompanyMapper;

    @Autowired
    private ExpenseReportTypeService expenseReportTypeService;

    @Autowired
    private OrganizationService organizationService;

    /**
     * 批量新增 报账单类型关联公司表
     *
     * @param list
     * @return
     */
    @Transactional
    public List<ExpenseReportTypeCompany> createExpenseReportTypeCompanyBatch(List<ExpenseReportTypeCompany> list){
        list.stream().forEach(expenseReportTypeCompany -> {
            if (expenseReportTypeCompany.getId() != null){
                throw new BizException(RespCode.EXPENSE_REPORT_TYPE_COMPANY_ALREADY_EXISTS);
            }
            //设置条件
            if (expenseReportTypeCompanyMapper.selectList(
                    new EntityWrapper<ExpenseReportTypeCompany>()
                            .eq("report_type_id",expenseReportTypeCompany.getReportTypeId())
                            .eq("company_id",expenseReportTypeCompany.getCompanyId())
            ).size() > 0){
                throw new BizException(RespCode.EXPENSE_REPORT_TYPE_COMPANY_NOT_ALLOWED_TO_REPEAT);
            }

            this.insert(expenseReportTypeCompany);
        });
        return list;
    }

    /**
     * 单个修改 报账单类型关联公司表
     *
     * @param expenseReportTypeCompany
     * @return
     */
    @Transactional
    public ExpenseReportTypeCompany updateExpenseReportTypeCompany(ExpenseReportTypeCompany expenseReportTypeCompany){
        if (expenseReportTypeCompany.getId() == null){
            throw new BizException(RespCode.EXPENSE_REPORT_TYPE_COMPANY_NOT_EXIST);
        }

        this.updateAllColumnById(expenseReportTypeCompany);
        return expenseReportTypeCompany;
    }

    /**
     * 根据报账单类型ID->sobPayReqTypeId 查询出与之对应的公司表中的数据，前台显示公司代码以及公司名称(分页)
     *
     * @param reportTypeId
     * @param page
     * @return
     */
    public List<ExpenseReportTypeCompany> getExpenseReportTypeCompanyByCond(Long reportTypeId, Page page){
        List<ExpenseReportTypeCompany> list = expenseReportTypeCompanyMapper.selectPage(page,
                new EntityWrapper<ExpenseReportTypeCompany>()
                        .eq("report_type_id",reportTypeId)
                        .orderBy("company_code")
        );
        list.stream().forEach(expenseReportTypeCompany -> {
            CompanyCO company = organizationService.getCompanyById(expenseReportTypeCompany.getCompanyId());
            if (company != null){
                expenseReportTypeCompany.setCompanyName(company.getName());
                expenseReportTypeCompany.setCompanyType(company.getCompanyTypeName());
            }
        });
        return list;
    }

    /**
     * 分配页面的公司筛选查询
     *
     * @param reportTypeId
     * @param companyCode
     * @param companyName
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param page
     * @return
     */
    public Page<CompanyCO> assignCompanyQuery(Long reportTypeId, String companyCode, String companyName, String companyCodeFrom, String companyCodeTo, Page page) {
        List<Long> collect = expenseReportTypeCompanyMapper.selectList(
                new EntityWrapper<ExpenseReportTypeCompany>()
                .eq("report_type_id", reportTypeId)
        ).stream().map(ExpenseReportTypeCompany::getCompanyId).collect(Collectors.toList());
        ExpenseReportType expenseReportType = expenseReportTypeService.selectById(reportTypeId);

        if (expenseReportType != null){
            Page<CompanyCO> companyByCond = organizationService.pageCompanyByCond(expenseReportType.getSetOfBooksId(), companyCode, companyName,
                    companyCodeFrom, companyCodeTo, collect, page);
            return companyByCond;
        }
        return page;
    }

    /**
     * 根据公司id查询报账单类型关联公司
     * @param companyId
     * @return
     */
    public List<ExpenseReportTypeCompany> getReportTypeByCompanyId(Long companyId){
        return baseMapper.selectList(new EntityWrapper<ExpenseReportTypeCompany>()
                .eq("company_id", companyId));
    }
}
