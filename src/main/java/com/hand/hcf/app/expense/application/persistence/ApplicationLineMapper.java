package com.hand.hcf.app.expense.application.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.application.domain.ApplicationLine;
import com.hand.hcf.app.expense.application.web.dto.ApplicationLineWebDTO;
import com.hand.hcf.app.expense.common.dto.CurrencyAmountDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.security.core.parameters.P;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/26
 */
public interface ApplicationLineMapper extends BaseMapper<ApplicationLine> {

    /**
     * 获取单据的总金额
     * @param headerId
     * @return
     */
    ApplicationLine getTotalAmount(Long headerId);

    /**
     * 根据单据头Id查询行信息
     * @param headerId
     * @param rowBounds
     * @return
     */
    List<ApplicationLineWebDTO> getLinesByHeaderId(@Param("headerId") Long headerId,
                                                   RowBounds rowBounds,
                                                   @Param("closeFlag") boolean closeFlag);

    /**
     * 根据单据头ID获取行的总金额(根据币种分组)
     * @param headerId
     * @return
     */
    CurrencyAmountDTO getCurrencyAndAmount(@Param("headerId") Long headerId);
}
