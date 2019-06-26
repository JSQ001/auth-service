package com.hand.hcf.app.expense.invoice.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.invoice.domain.InvoiceExpenseTypeRules;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @description: 发票费用映射规则表Mapper
 * @version: 1.0
 * @author: shuqiang.luo@hand-china.com
 * @date: 2019/4/12
 */

public interface InvoiceExpenseTypeRulesMapper extends BaseMapper<InvoiceExpenseTypeRules> {


    /**
     * 页面搜索框，支持模糊查询
     * @author sq.l
     * @date 2019/04/22
     *
     * @param tenantId
     * @param setOfBooksId
     * @param goodsName
     * @param ExpenseTypeCode
     * @param ExpenseTypeName
     * @param enabled
     * @param startDate
     * @param endDate
     * @param rowBounds
     * @return
     */
    List<InvoiceExpenseTypeRules> selectInvoiceExpenseRules(@Param("tenantId") Long tenantId,
                                                            @Param("setOfBooksId") Long setOfBooksId,
                                                             @Param("goodsName") String goodsName,
                                                            @Param("ExpenseTypeCode") String ExpenseTypeCode,
                                                            @Param("ExpenseTypeName") String ExpenseTypeName,
                                                            @Param("enabled") Boolean enabled,
                                                            @Param("startDate") ZonedDateTime startDate,
                                                            @Param("endDate") ZonedDateTime endDate,
                                                            @Param("dataAuthLabel") String dataAuthLabel,
                                                            RowBounds rowBounds);

}
