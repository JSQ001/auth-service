package com.hand.hcf.app.expense.type.service;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.domain.ExpenseTypeCategory;
import com.hand.hcf.app.expense.type.persistence.ExpenseTypeCategoryMapper;
import com.hand.hcf.app.expense.type.web.dto.SortBySequenceDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/5
 */
@Service
@Slf4j
public class ExpenseTypeCategoryService extends BaseService<ExpenseTypeCategoryMapper, ExpenseTypeCategory> {

    @Autowired
    private BaseI18nService baseI18nService;
    @Autowired
    private ExpenseTypeService expenseTypeService;

    /**
     *  创建新的费用大类
     * @param expenseTypeCategory
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean createTypeCategory(ExpenseTypeCategory expenseTypeCategory) {
        if (expenseTypeCategory.getTravelTypeFlag() && baseMapper.selectCount(new EntityWrapper<ExpenseTypeCategory>()
                .eq("set_of_books_id", expenseTypeCategory.getSetOfBooksId())
                .eq("travel_type_flag", true)
        ) > 0) {
            throw new BizException(RespCode.EXPENSE_EXPENSE_TYPE_CATEGORY_ALREADY_EXIST_TRAVEL_TYPE);
        }
        if (expenseTypeCategory.getDeleted() == null){
            expenseTypeCategory.setDeleted(false);
        }
        if (expenseTypeCategory.getEnabled() == null){
            expenseTypeCategory.setEnabled(true);
        }
        // 设置租户为当前登陆人的租户ID
        expenseTypeCategory.setTenantId(OrgInformationUtil.getCurrentTenantId());
        return this.insert(expenseTypeCategory);
    }

    public List<ExpenseTypeCategory> listResult(Long setOfBooksId, Integer typeFlag) {
        List<ExpenseTypeCategory> result = baseMapper.listCategoryAndType(setOfBooksId, typeFlag);
        result  =  baseI18nService.selectListTranslatedTableInfoWithI18nByEntity(result, ExpenseTypeCategory.class);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean sort(List<SortBySequenceDTO> list) {
        Map<Long, Integer> map = list.stream().collect(Collectors.toMap(SortBySequenceDTO::getId, SortBySequenceDTO::getSequence, (k1, k2) -> k1));
        List<ExpenseTypeCategory> expenseTypeCategories = this.selectBatchIds(list.stream().map(SortBySequenceDTO::getId).collect(Collectors.toList()));
        expenseTypeCategories.stream().forEach(e -> {
            if (map.containsKey(e.getId())){
                e.setSequence(map.get(e.getId()));
            }
        });
        return this.updateBatchById(expenseTypeCategories);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateTypeCategory(ExpenseTypeCategory dto) {
        if (dto.getId() == null){
            throw new BizException(RespCode.SYS_ID_IS_NULL);
        }
        ExpenseTypeCategory expenseTypeCategory = this.selectById(dto.getId());
        expenseTypeCategory.setName(dto.getName());
        expenseTypeCategory.setI18n(dto.getI18n());
        return this.updateById(expenseTypeCategory);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTypeCategory(Long id) {
        int count = expenseTypeService.selectCount(new EntityWrapper<ExpenseType>().eq("type_category_id", id));
        if (count > 0) {
            throw new BizException(RespCode.EXPENSE_CATEGORY_EXISTS_TYPE);
        }
        boolean b = this.deleteById(id);
        return b ? b : false;
    }
}
