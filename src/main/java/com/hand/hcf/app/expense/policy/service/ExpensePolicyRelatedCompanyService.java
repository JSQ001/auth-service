package com.hand.hcf.app.expense.policy.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.co.SetOfBooksInfoCO;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.init.dto.ExpensePolicyRelatedCompanyInitDTO;
import com.hand.hcf.app.expense.policy.domain.ExpensePolicy;
import com.hand.hcf.app.expense.policy.domain.ExpensePolicyRelatedCompany;
import com.hand.hcf.app.expense.policy.persistence.ExpensePolicyMapper;
import com.hand.hcf.app.expense.policy.persistence.ExpensePolicyRelatedCompanyMapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.persistence.ExpenseTypeMapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @description:
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/2/1 10:30
 */
@Transactional
@Service
public class ExpensePolicyRelatedCompanyService extends BaseService<ExpensePolicyRelatedCompanyMapper, ExpensePolicyRelatedCompany> {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ExpenseTypeMapper expenseTypeMapper;

    @Autowired
    private ExpensePolicyMapper expensePolicyMapper;

    /**
     * 根据费用政策id获得关联公司
     * @param expExpensePolicyId
     * @return
     */
    public List<ExpensePolicyRelatedCompany> getRelatedCompanyByPolicyId(Long expExpensePolicyId) {
        List<ExpensePolicyRelatedCompany> relatedCompanies = new ArrayList<ExpensePolicyRelatedCompany>();
        relatedCompanies = baseMapper.selectList(new EntityWrapper<ExpensePolicyRelatedCompany>()
                .eq("exp_expense_policy_id", expExpensePolicyId));
        return relatedCompanies;
    }

    public void checkExpensePolicyRelatedCompanyInitData(ExpensePolicyRelatedCompanyInitDTO dto, int line) {
        Map<String,List<String>> errorMap = new HashMap<>(16);
        String lineNumber = "第"+line+"行";
        List<String> stringList = new ArrayList<>();
        if (TypeConversionUtils.isEmpty(dto.getSetOfBooksCode()) ||
                TypeConversionUtils.isEmpty(dto.getCode()) ||
                TypeConversionUtils.isEmpty(dto.getPriority()) ||
                TypeConversionUtils.isEmpty(dto.getCompanyCode()) ||
                TypeConversionUtils.isEmpty(dto.getTypeFlag())) {
            stringList.add("必输字段为空！");
        } else {
            Long setOfBooksId = null;
            List<SetOfBooksInfoCO> setOfBooksInfoCOList =
                    organizationService.getSetOfBooksBySetOfBooksCode(dto.getSetOfBooksCode());
            if(CollectionUtils.isEmpty(setOfBooksInfoCOList)){
                stringList.add("账套code不存在！");
            }else if(setOfBooksInfoCOList.size() == 1){
                setOfBooksId = setOfBooksInfoCOList.get(0).getId();
                dto.setSetOfBooksId(setOfBooksId);
                if(!TypeConversionUtils.isEmpty(dto.getCompanyCode())) {
                    CompanyCO companyCO = organizationService.getByCompanyCode(dto.getCompanyCode());
                    if (companyCO == null) {
                        stringList.add("公司代码找不到！");
                    } else {
                        if(!companyCO.getSetOfBooksId().equals(dto.getSetOfBooksId())){
                            stringList.add("公司不在"+ dto.getSetOfBooksCode()+"账套中！");
                        }else {
                            dto.setCompanyId(companyCO.getId());
                        }
                    }
                }
            }else if(setOfBooksInfoCOList.size() > 1){
                stringList.add("账套code存在多个！");
            }
            List<ExpenseType> typelist = expenseTypeMapper.selectList(
                    new EntityWrapper<ExpenseType>()
                            .eq("set_of_books_id",setOfBooksId)
                            .eq("code",dto.getCode())
                            .eq("type_flag",dto.getTypeFlag())
            );
            if (CollectionUtils.isEmpty(typelist)) {
                stringList.add("类型代码找不到！");
            }else if(typelist.size() == 1){
                dto.setExpenseTypeId(typelist.get(0).getId());
                List<ExpensePolicy> policyList =  expensePolicyMapper.selectList(
                        new EntityWrapper<ExpensePolicy>()
                                .eq("set_of_books_id",setOfBooksId)
                                .eq("expense_type_id",dto.getExpenseTypeId())
                                .eq("priority",dto.getPriority())
                );
                if (CollectionUtils.isEmpty(policyList)) {
                    stringList.add("费用政策找不到！");
                }else if(policyList.size() == 1){
                    dto.setExpExpensePolicyId(policyList.get(0).getId());
                }else if(policyList.size() > 1){
                    stringList.add("费用政策在多个！");
                }
            }else if(typelist.size() > 1){
                stringList.add("类型代码存在多个！");
            }
        }
        if(!CollectionUtils.isEmpty(stringList)){
            errorMap.put(lineNumber,stringList);
        }
        dto.setResultMap(errorMap);
    }
    public Map<String, List<String>> initExpensePolicyRelatedCompany(List<ExpensePolicyRelatedCompanyInitDTO> dtoList){
        Map<String,  List<String>> resultMap = new HashMap<>();
        int line = 1;
        for(ExpensePolicyRelatedCompanyInitDTO item :dtoList){
            checkExpensePolicyRelatedCompanyInitData(item,line);
            String lineNumber = "第" + line + "行";
            if(item.getResultMap().isEmpty()){
                List<ExpensePolicyRelatedCompany> temp = baseMapper.selectList(new EntityWrapper<ExpensePolicyRelatedCompany>()
                        .eq("exp_expense_policy_id", item.getExpExpensePolicyId())
                        .eq("company_id",item.getCompanyId()));
                if(CollectionUtils.isEmpty(temp)) {
                    ExpensePolicyRelatedCompany relatedCompany = new ExpensePolicyRelatedCompany();
                    relatedCompany.setTenantId(item.getTenantId() != null ? item.getTenantId() : OrgInformationUtil.getCurrentTenantId());
                    relatedCompany.setSetOfBooksId(item.getSetOfBooksId() != null ? item.getSetOfBooksId() : OrgInformationUtil.getCurrentSetOfBookId());
                    relatedCompany.setExpExpensePolicyId(item.getExpExpensePolicyId());
                    relatedCompany.setCompanyId(item.getCompanyId());
                    baseMapper.insert(relatedCompany);
                    ExpensePolicy policy = expensePolicyMapper.selectById(relatedCompany.getExpExpensePolicyId());
                    policy.setAllCompanyFlag(Boolean.FALSE);
                    expensePolicyMapper.updateById(policy);
                }else{
                    //不更新，直接已存在
                    List<String> stringList = new ArrayList<>();
                    stringList.add("该费用政策已经分配过该公司");
                    if(!CollectionUtils.isEmpty(stringList)) {
                        item.getResultMap().put(lineNumber,stringList);
                        resultMap.putAll(item.getResultMap());
                    }
                }
            }else{
                resultMap.putAll(item.getResultMap());
            }
            line++;
        }
        if (resultMap.isEmpty()) {
            resultMap.put("success", Arrays.asList("导入成功"));
        }
        return resultMap;
    }
}
