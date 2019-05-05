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


    /**
     * 查询费用申请单
     * @author sq.l
     * @date 2019/04/22
     *
     * @param code
     * @param name
     * @param categoryName
     * @param setOfBooksId
     * @param rowBounds
     * @return
     */
    List<ExpenseType>  selectExpenseByCode( @Param("code") String code,
                                           @Param("name") String name,
                                           @Param("categoryName") String categoryName,
                                           @Param("setOfBooksId") Long setOfBooksId,
                                            RowBounds rowBounds);

    /**
     * 报账单类型定义关联费用类型-根据费用类型代码查询费用类型id
     * @param setOfBooksId
     * @param expenseTypeCode 费用类型代码
     * @return
     */
    Long queryExpenseTypeIdByExpenseTypeCode(@Param("setOfBooksId") Long setOfBooksId,
                                             @Param("expenseTypeCode") String expenseTypeCode);


    /**
     * 费用申请单类型定义关联申请类型-根据申请类型代码查询申请类型id
     * @param setOfBooksId
     * @param applicationTypeCode 费用类型代码
     * @return
     */
    Long queryApplicationTypeIdByApplicationTypeCode(@Param("setOfBooksId") Long setOfBooksId,
                                                     @Param("applicationTypeCode") String applicationTypeCode);

}
