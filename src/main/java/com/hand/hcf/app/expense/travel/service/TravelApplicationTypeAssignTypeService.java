package com.hand.hcf.app.expense.travel.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationType;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationTypeAssignType;
import com.hand.hcf.app.expense.travel.persistence.TravelApplicationTypeAssignTypeMapper;
import com.hand.hcf.app.expense.type.web.dto.ExpenseTypeWebDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.core.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author shouting.cheng
 * @date 2019/3/4
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TravelApplicationTypeAssignTypeService extends BaseService<TravelApplicationTypeAssignTypeMapper, TravelApplicationTypeAssignType> {
    public List<ExpenseTypeWebDTO> queryExpenseTypeByApplicationTypeId(TravelApplicationType applicationType, Long categoryId, String expenseTypeName, Page page) {
        List<ExpenseTypeWebDTO> list;
        // 如果是全部类型
        if (applicationType.getAllTypeFlag()){
            list = baseMapper.queryAllExpenseBySetOfBooksId(applicationType.getSetOfBooksId(), null,categoryId, expenseTypeName,0, OrgInformationUtil.getCurrentLanguage(), page);
        }else{
            list = baseMapper.queryExpenseTypeByApplicationTypeId(applicationType.getId(), categoryId, expenseTypeName, OrgInformationUtil.getCurrentLanguage(), page);
        }
        return list;
    }
}
