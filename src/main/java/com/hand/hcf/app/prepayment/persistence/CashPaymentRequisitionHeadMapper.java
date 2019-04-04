package com.hand.hcf.app.prepayment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.prepayment.domain.CashPaymentRequisitionHead;
import com.hand.hcf.app.prepayment.web.dto.CurrencyDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by cbc on 2017/10/26.
 */
@Component
public interface CashPaymentRequisitionHeadMapper extends BaseMapper<CashPaymentRequisitionHead> {

    List<CurrencyDTO> getAmountByHeadIdAndRefHeadId(
            @Param("refHeadId") Long refHeadId,
            @Param("headId") Long headId
            );

    int getTotal(@Param("ew") Wrapper<CashPaymentRequisitionHead> wrapper);

    /**
     * 分页查询单据头信息和单据类型名称
     * @param rowBounds
     * @param wrapper
     * @return
     */
    List<CashPaymentRequisitionHead> listHeaderAndTypName(RowBounds rowBounds, @Param("ew") Wrapper<CashPaymentRequisitionHead> wrapper);

}
