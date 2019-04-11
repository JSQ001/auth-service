package com.hand.hcf.app.expense.type.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.expense.type.bo.ExpenseBO;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.web.dto.ExpenseTypeWebDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/6
 */
public interface ExpenseTypeMapper extends BaseMapper<ExpenseType> {

    List<ExpenseType> listByCategoryIdAndTypeFlag(@Param("categoryId") Long categoryId,
                                                  @Param("typeFlag") Integer typeFlag);

    List<ExpenseType> queryByCondition(RowBounds rowBounds, @Param("ew") Wrapper<ExpenseType> wrapper);

    ExpenseType getTypeById(@Param("id") Long id);


    List<ExpenseType> queryLovByDocumentTypeAssign(@Param("setOfBooksId") Long setOfBooksId,
                                                   @Param("range") String range,
                                                   @Param("documentType") Integer documentType,
                                                   @Param("documentTypeId") Long documentTypeId,
                                                   @Param("code") String code,
                                                   @Param("name") String name,
                                                   @Param("typeCategoryId") Long typeCategoryId,
                                                   @Param("typeFlag") Integer typeFlag,
                                                   RowBounds rowBounds);

    List<ExpenseType> queryLovByDocumentTypeAssign(@Param("setOfBooksId") Long setOfBooksId,
                                                   @Param("range") String range,
                                                   @Param("documentType") Integer documentType,
                                                   @Param("documentTypeId") Long documentTypeId,
                                                   @Param("code") String code,
                                                   @Param("name") String name,
                                                   @Param("typeCategoryId") Long typeCategoryId,
                                                   @Param("typeFlag") Integer typeFlag);

    List<BasicCO> listByExpenseTypesAndCond(@Param("tenantId") Long tenantId,
                                            @Param("setOfBooksId") Long setOfBooksId,
                                            @Param("enabled") Boolean enabled,
                                            @Param("code") String code,
                                            @Param("name") String name,
                                            RowBounds rowBounds);

    /**
     *  创建单据行选择有权限的费用体系信息
     * @param expenseBO 查询条件
     * @return List<ExpenseTypeWebDTO>
     */
    List<ExpenseTypeWebDTO> listByDocumentLov(ExpenseBO expenseBO);
}
