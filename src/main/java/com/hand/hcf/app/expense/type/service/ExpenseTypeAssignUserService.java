package com.hand.hcf.app.expense.type.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.common.co.DepartmentCO;
import com.hand.hcf.app.common.co.SetOfBooksInfoCO;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.init.dto.ExpenseTypeAssignUserInitDTO;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.domain.ExpenseTypeAssignUser;
import com.hand.hcf.app.expense.type.domain.enums.AssignUserEnum;
import com.hand.hcf.app.expense.type.persistence.ExpenseTypeAssignUserMapper;
import com.hand.hcf.app.expense.type.persistence.ExpenseTypeMapper;
import org.springframework.stereotype.Service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/12
 */
@Service
public class ExpenseTypeAssignUserService extends BaseService<ExpenseTypeAssignUserMapper, ExpenseTypeAssignUser> {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ExpenseTypeMapper expenseTypeMapper;

    public void checkExpenseTypeInitData(ExpenseTypeAssignUserInitDTO dto, int line) {
        Map<String,List<String>> errorMap = new HashMap<>(16);
        String lineNumber = "第"+line+"行";
        List<String> stringList = new ArrayList<>();
        if (TypeConversionUtils.isEmpty(dto.getCode()) || TypeConversionUtils.isEmpty(dto.getUserTypeCode())
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
            }else if(setOfBooksInfoCOList.size() > 1){
                stringList.add("账套code存在多个！");
            }
            dto.setSetOfBooksId(setOfBooksId);
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
            if(AssignUserEnum.USER_DEPARTMENT.getKey().equals(dto.getApplyType())){
                DepartmentCO departmentCO = organizationService.getDepartmentByCodeAndTenantId(dto.getUserTypeCode());
                if(departmentCO != null){
                    dto.setUserTypeId(departmentCO.getId());
                }else{
                    stringList.add("适用code不存在");
                }
            }else if(AssignUserEnum.USER_GROUP.getKey().equals(dto.getApplyType())){
                //todo 用户组无三方接口
            }
        }
        if(!CollectionUtils.isEmpty(stringList)){
            errorMap.put(lineNumber,stringList);
        }
        dto.setResultMap(errorMap);
    }
    public Map<String, List<String>> initExpenseTypeAssignUser(List<ExpenseTypeAssignUserInitDTO> dtoList){
        Map<String,  List<String>> resultMap = new HashMap<>();
        int line = 1;
        for(ExpenseTypeAssignUserInitDTO item :dtoList){
            checkExpenseTypeInitData(item,line);
            if(item.getResultMap().isEmpty()){
                List<ExpenseTypeAssignUser> temp = baseMapper.selectList(new EntityWrapper<ExpenseTypeAssignUser>()
                        .eq("expense_type_id", item.getExpenseTypeId())
                        .eq("user_type_id",item.getUserTypeId())
                        .eq("apply_type",item.getApplyType())
                );
                if(CollectionUtils.isEmpty(temp)) {
                    ExpenseTypeAssignUser assignUser = new ExpenseTypeAssignUser();
                    BeanUtils.copyProperties(item,assignUser);
                    baseMapper.insert(assignUser);
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
