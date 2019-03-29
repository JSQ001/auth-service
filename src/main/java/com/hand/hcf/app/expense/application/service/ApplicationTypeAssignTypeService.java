package com.hand.hcf.app.expense.application.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.expense.application.domain.ApplicationType;
import com.hand.hcf.app.expense.application.domain.ApplicationTypeAssignType;
import com.hand.hcf.app.expense.application.persistence.ApplicationTypeAssignTypeMapper;
import com.hand.hcf.app.expense.type.web.dto.ExpenseTypeWebDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.core.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/8
 */
@Service
public class ApplicationTypeAssignTypeService extends BaseService<ApplicationTypeAssignTypeMapper, ApplicationTypeAssignType> {

    /**
     * 查询分配的申请类型
     * @param applicationType
     * @param categoryId
     * @param expenseTypeName
     * @param page
     * @return
     */
    public List<ExpenseTypeWebDTO> queryExpenseTypeByApplicationTypeId(ApplicationType applicationType, Long categoryId, String expenseTypeName, Page page) {
        List<ExpenseTypeWebDTO> list;
        // 如果是全部类型
        if (applicationType.getAllFlag()){
            list = baseMapper.queryAllExpenseBySetOfBooksId(applicationType.getSetOfBooksId(), null,categoryId, expenseTypeName,0, OrgInformationUtil.getCurrentLanguage(), page);
        }else{
            list = baseMapper.queryExpenseTypeByApplicationTypeId(applicationType.getId(), categoryId, expenseTypeName, OrgInformationUtil.getCurrentLanguage(), page);
        }
        return list;
    }

    public List<ExpenseTypeWebDTO> queryExpenseTypeBySetOfBooksIdAndId(Long setOfBooksId,Long id ,Long typeCategoryId, String expenseTypeName,Integer typeFlag, Page page) {
        return baseMapper.queryAllExpenseBySetOfBooksId(setOfBooksId,id, typeCategoryId,expenseTypeName, typeFlag,OrgInformationUtil.getCurrentLanguage(), page);
    }

    public List<ApplicationTypeAssignType> listByApplicationTypeId(Long applicationTypeId) {
        String currentLanguage = OrgInformationUtil.getCurrentLanguage();
        if (!StringUtils.hasText(currentLanguage)){
            currentLanguage = "zh_cn";
        }
        return baseMapper.listByApplicationTypeId(applicationTypeId, currentLanguage);
    }
}
