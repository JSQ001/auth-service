package com.hand.hcf.app.expense.adjust.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustLine;
import com.hand.hcf.app.expense.adjust.web.dto.ExpenseAdjustLineWebDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/10
 */
public interface ExpenseAdjustLineMapper extends BaseMapper<ExpenseAdjustLine> {

    List<ExpenseAdjustLineWebDTO> listLineDTOByHeaderId(@Param("expAdjustHeaderId") Long expAdjustHeaderId,
                                                        RowBounds rowBounds);

    List<Long> getCompanyId(@Param("headerId") Long headerId);
    List<Long> getUnitId(@Param("headerId") Long headerId);

    BigDecimal getAmount(@Param("headerId") Long id,
                         @Param("sourceLineId") Long lineId);
}
