package com.hand.hcf.app.expense.type.service;

import com.hand.hcf.app.common.co.DimensionCO;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import com.hand.hcf.app.expense.type.persistence.ExpenseDimensionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/22
 */
@Service
public class ExpenseDimensionService extends BaseService<ExpenseDimensionMapper, ExpenseDimension> {

    @Autowired
    private OrganizationService orgService;

    /**
     * 根据单据头ID查询是位置为单据头的维度
     * @param headerId
     * @param documentType
     * @param headerFlag
     * @return
     */
    public List<ExpenseDimension> listDimensionByHeaderIdAndType(Long headerId,
                                                                 Integer documentType,
                                                                 Boolean headerFlag){
        List<ExpenseDimension> dimensions = baseMapper.listDimensionByHeaderIdAndType(headerId, documentType, headerFlag);
        dimensions.forEach(dimension -> {
            DimensionCO dimensionCO = orgService.getDimensionById(dimension.getDimensionId());
            dimension.setName(dimensionCO.getDimensionName());
            dimension.setSequence(dimensionCO.getDimensionSequence());
        });
        return dimensions;
    }

}
