package com.hand.hcf.app.expense.type.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.co.SetOfBooksInfoCO;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.init.dto.ExpenseTypeAssignCompanyInitDTO;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.domain.ExpenseTypeAssignCompany;
import com.hand.hcf.app.expense.type.persistence.ExpenseTypeAssignCompanyMapper;
import com.hand.hcf.app.expense.type.persistence.ExpenseTypeMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/12
 */
@Service
public class ExpenseTypeAssignCompanyService extends BaseService<ExpenseTypeAssignCompanyMapper, ExpenseTypeAssignCompany> {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ExpenseTypeMapper expenseTypeMapper;

    public void checkExpenseTypeInitData(ExpenseTypeAssignCompanyInitDTO dto,int line) {
        Map<String,List<String>> errorMap = new HashMap<>(16);
        String lineNumber = "第"+line+"行";
        List<String> stringList = new ArrayList<>();
        if (TypeConversionUtils.isEmpty(dto.getCode()) || TypeConversionUtils.isEmpty(dto.getCompanyCode())
                || TypeConversionUtils.isEmpty(dto.getTypeFlag())) {
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
            }else if(typelist.size() > 1){
                stringList.add("类型代码存在多个！");
            }
        }
        if(!CollectionUtils.isEmpty(stringList)){
            errorMap.put(lineNumber,stringList);
        }
        dto.setResultMap(errorMap);
    }
    public Map<String, List<String>> initExpenseTypeAssignCompany(List<ExpenseTypeAssignCompanyInitDTO> dtoList){
        Map<String,  List<String>> resultMap = new HashMap<>();
        int line = 1;
        for(ExpenseTypeAssignCompanyInitDTO item :dtoList){
            checkExpenseTypeInitData(item,line);
            String lineNumber = "第" + line + "行";
            if(item.getResultMap().isEmpty()){
                List<ExpenseTypeAssignCompany> temp = baseMapper.selectList(new EntityWrapper<ExpenseTypeAssignCompany>()
                        .eq("expense_type_id", item.getExpenseTypeId())
                        .eq("company_id",item.getCompanyId()));
                if(CollectionUtils.isEmpty(temp)) {
                    ExpenseTypeAssignCompany assignCompany = new ExpenseTypeAssignCompany();
                    BeanUtils.copyProperties(item,assignCompany);
                    baseMapper.insert(assignCompany);
                }else{
                    //不更新，直接已存在
                    List<String> stringList = new ArrayList<>();
                    stringList.add("该类型已经分配过该公司");
                    if(!org.apache.commons.collections4.CollectionUtils.isEmpty(stringList)) {
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
