package com.hand.hcf.app.expense.accrual.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualCompany;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualType;
import com.hand.hcf.app.expense.accrual.persistence.ExpenseAccrualCompanyMapper;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 费用预提单分配公司
 * @version: 1.0
 * @author: liguo.zhao@hand-china.com
 * @date: 2019/5/15
 */
@Service
public class ExpenseAccrualCompanyService extends ServiceImpl<ExpenseAccrualCompanyMapper,ExpenseAccrualCompany> {

    @Autowired
    private ExpenseAccrualCompanyMapper expenseAccrualCompanyMapper;

    @Autowired
    private ExpenseAccrualTypeService expenseAccrualTypeService;

    @Autowired
    private OrganizationService organizationService;

    /**
     * 批量新增 费用预提单类型关联的公司表
     *
     * @param list
     * @return
     */
    @Transactional
    public List<ExpenseAccrualCompany> createExpenseAccrualTypeAssignCompanyBatch(List<ExpenseAccrualCompany> list){
        list.stream().forEach(expenseAccrualTypeAssignCompany -> {
            if (expenseAccrualTypeAssignCompany.getId() != null){
                throw new BizException(RespCode.SYS_ID_IS_NOT_NULL);
            }

            //设置条件
            if (expenseAccrualCompanyMapper.selectList(
                    new EntityWrapper<ExpenseAccrualCompany>()
                            .eq("exp_accrual_type_id",expenseAccrualTypeAssignCompany.getExpAccrualTypeId())
                            .eq("company_id",expenseAccrualTypeAssignCompany.getCompanyId())
            ).size() > 0){
                throw new BizException(RespCode.EXPENSE_ADJUST_TYPE_ASSIGN_COMPANY_IS_EXISTS);
            }

            expenseAccrualCompanyMapper.insert(expenseAccrualTypeAssignCompany);
        });
        return list;
    }

    public boolean updateCompanyEnbaled(List<ExpenseAccrualCompany> list){
        list.stream().forEach(expenseAccrualCompany -> {
            ExpenseAccrualCompany assignCompany = this.selectById(expenseAccrualCompany.getId());
            assignCompany.setEnableFlag(expenseAccrualCompany.getEnableFlag());this.updateById(assignCompany);

        });
        return true;
    }

    /**
     * 根据费用预提单类型ID->expAccrualTypeId 查询出与之对应的公司表中的数据，前台显示公司代码以及公司名称(分页)
     *
     * @param expAccrualTypeId
     * @param enableFlag
     * @param page
     * @return
     */
    public Page<ExpenseAccrualCompany> getExpenseAccrualTypeAssignCompanyByCond(Long expAccrualTypeId,
                                                                                Boolean enableFlag,
                                                                                Page page){
        List<ExpenseAccrualCompany> list = expenseAccrualCompanyMapper.selectPage(page,
                new EntityWrapper<ExpenseAccrualCompany>()
                        .eq("exp_accrual_type_id",expAccrualTypeId)
                        .eq(enableFlag != null,"enable_flag",enableFlag)
                        //.orderBy("company_code")
        );
        if (!CollectionUtils.isEmpty(list)){
            Map<Long, CompanyCO> comMap = organizationService
                    .getCompanyMapByCompanyIds(list.stream().map(ExpenseAccrualCompany::getCompanyId).collect(Collectors.toList()));
            list.stream().forEach(e -> {
                if (comMap.containsKey(e.getCompanyId())){
                    CompanyCO companyCO = comMap.get(e.getCompanyId());
                    e.setCompanyCode(companyCO.getCompanyCode());
                    e.setCompanyName(companyCO.getName());
                    e.setCompanyType(companyCO.getCompanyTypeName());
                }
            });
            page.setRecords(list);
            return page;
        }
        return page;
    }

    /**
     * 分配页面的公司筛选查询
     *
     * @param expAccrualTypeId
     * @param companyCode
     * @param companyName
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param page
     * @return
     */
    public Page<CompanyCO> assignCompanyQuery(Long expAccrualTypeId,
                                              String companyCode,
                                              String companyName,
                                              String companyCodeFrom,
                                              String companyCodeTo,
                                              Page page) {
        List<Long> companyIdList = expenseAccrualCompanyMapper.selectList(new EntityWrapper<ExpenseAccrualCompany>()
                .eq("exp_accrual_type_id", expAccrualTypeId)
        ).stream().map(ExpenseAccrualCompany::getCompanyId).collect(Collectors.toList());
        ExpenseAccrualType expenseAccrualType = expenseAccrualTypeService.selectById(expAccrualTypeId);
        if (expenseAccrualType != null){
            Page<CompanyCO> coms = organizationService
                    .pageCompanyByCond(expenseAccrualType.getSetOfBooksId(),
                                       companyCode,
                                       companyName,
                                       companyCodeFrom,
                                       companyCodeTo,
                                       companyIdList,
                                       page);
            return coms;
        }
        return null;
    }
}
