package com.hand.hcf.app.expense.adjust.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustType;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustTypeAssignCompany;
import com.hand.hcf.app.expense.adjust.persistence.ExpenseAdjustTypeAssignCompanyMapper;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by 韩雪 on 2018/3/15.
 */
@Service
public class ExpenseAdjustTypeAssignCompanyService extends ServiceImpl<ExpenseAdjustTypeAssignCompanyMapper,ExpenseAdjustTypeAssignCompany> {
    @Autowired
    private  ExpenseAdjustTypeAssignCompanyMapper expenseAdjustTypeAssignCompanyMapper;

    @Autowired
    private  ExpenseAdjustTypeService expenseAdjustTypeService;

    @Autowired
    private OrganizationService organizationService;

    /**
     * 批量新增 费用调整单类型关联的公司表
     *
     * @param list
     * @return
     */
    @Transactional
    public List<ExpenseAdjustTypeAssignCompany> createExpenseAdjustTypeAssignCompanyBatch(List<ExpenseAdjustTypeAssignCompany> list){
        list.stream().forEach(expenseAdjustTypeAssignCompany -> {
            if (expenseAdjustTypeAssignCompany.getId() != null){
                throw new BizException(RespCode.SYS_ID_IS_NOT_NULL);
            }

            //设置条件
            if (expenseAdjustTypeAssignCompanyMapper.selectList(
                new EntityWrapper<ExpenseAdjustTypeAssignCompany>()
                    .eq("exp_adjust_type_id",expenseAdjustTypeAssignCompany.getExpAdjustTypeId())
                    .eq("company_id",expenseAdjustTypeAssignCompany.getCompanyId())
            ).size() > 0){
                throw new BizException(RespCode.EXPENSE_ADJUST_TYPE_ASSIGN_COMPANY_IS_EXISTS);
            }

            expenseAdjustTypeAssignCompanyMapper.insert(expenseAdjustTypeAssignCompany);
        });
        return list;
    }


    public boolean updateCompanyEnbaled(List<ExpenseAdjustTypeAssignCompany> list){
        list.stream().forEach(expenseAdjustTypeAssignCompany -> {
            ExpenseAdjustTypeAssignCompany assignCompany = this.selectById(expenseAdjustTypeAssignCompany.getId());
            assignCompany.setEnabled(expenseAdjustTypeAssignCompany.getEnabled());this.updateById(assignCompany);

        });
        return true;
    }


    /**
     * 根据费用调整单类型ID->expAdjustTypeId 查询出与之对应的公司表中的数据，前台显示公司代码以及公司名称(分页)
     *
     * @param expAdjustTypeId
     * @param enabled
     * @param page
     * @return
     */
    public Page<ExpenseAdjustTypeAssignCompany> getExpenseAdjustTypeAssignCompanyByCond(Long expAdjustTypeId, Boolean enabled, Page page){
        List<ExpenseAdjustTypeAssignCompany> list = expenseAdjustTypeAssignCompanyMapper.selectPage(page,
            new EntityWrapper<ExpenseAdjustTypeAssignCompany>()
                .eq("exp_adjust_type_id",expAdjustTypeId)
                .eq(enabled != null,"enabled",enabled)
                .orderBy("company_code")
        );
        if (!CollectionUtils.isEmpty(list)){
            Map<Long, CompanyCO> comMap = organizationService.getCompanyMapByCompanyIds(list.stream().map(ExpenseAdjustTypeAssignCompany::getCompanyId).collect(Collectors.toList()));
            list.stream().forEach(e -> {
                if (comMap.containsKey(e.getCompanyId())){
                    CompanyCO companyCO = comMap.get(e.getCompanyId());
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
     * @param expAdjustTypeId
     * @param companyCode
     * @param companyName
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param page
     * @return
     */
    public Page<CompanyCO> assignCompanyQuery(Long expAdjustTypeId, String companyCode, String companyName, String companyCodeFrom, String companyCodeTo, Page page) {
        List<Long> companyIdList = expenseAdjustTypeAssignCompanyMapper.selectList(new EntityWrapper<ExpenseAdjustTypeAssignCompany>()
                .eq("exp_adjust_type_id", expAdjustTypeId)
        ).stream().map(ExpenseAdjustTypeAssignCompany::getCompanyId).collect(Collectors.toList());
        ExpenseAdjustType expenseAdjustType = expenseAdjustTypeService.selectById(expAdjustTypeId);
        if (expenseAdjustType != null){
            Page<CompanyCO> coms = organizationService.pageCompanyByCond(expenseAdjustType.getSetOfBooksId(),companyCode,companyName, companyCodeFrom,companyCodeTo, companyIdList, page);
            return coms;
        }
        return null;
    }
}
