package com.hand.hcf.app.expense.adjust.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustHeader;
import com.hand.hcf.app.expense.adjust.web.dto.ExpenseAdjustHeaderWebDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/5
 */
public interface ExpenseAdjustHeaderMapper extends BaseMapper<ExpenseAdjustHeader> {
    List<ExpenseAdjustHeaderWebDTO> listHeaderWebDTOByCondition(@Param("expAdjustHeaderNumber") String expAdjustHeaderNumber,
                                                                @Param("setOfBooksId") Long setOfBooksId,
                                                                @Param("expAdjustTypeId") Long expAdjustTypeId,
                                                                @Param("status") String status,
                                                                @Param("requisitionDateFrom") ZonedDateTime requisitionDateFrom,
                                                                @Param("requisitionDateTo") ZonedDateTime requisitionDateTo,
                                                                @Param("amountMin") BigDecimal amountMin,
                                                                @Param("amountMax") BigDecimal amountMax,
                                                                @Param("employeeId") Long employeeId,
                                                                @Param("description") String description,
                                                                @Param("adjustTypeCategory") String adjustTypeCategory,
                                                                @Param("currencyCode") String currencyCode,
                                                                @Param("createdBy") Long createdBy,
                                                                @Param("unitId") Long unitId,
                                                                @Param("companyId") Long companyId,
                                                                @Param("dataAuthLabel") String dataAuthLabel,
                                                                RowBounds rowBounds);

    /**
     * 根据ID删除行信息
     * @param id
     */
    void deleteLinesByHeaderId(Long id);
}
