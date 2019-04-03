package com.hand.hcf.app.prepayment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.prepayment.domain.CashPaymentRequisitionLine;
import org.apache.ibatis.session.RowBounds;

/**
 * Created by cbc on 2017/10/26.
 */
public interface CashPaymentRequisitionLineMapper extends BaseMapper<CashPaymentRequisitionLine> {
    List<CashPaymentRequisitionHeadDto> getLineByQueryfromApplication(RowBounds rowBounds,
                                                                      @Param("ew") Wrapper<CashPaymentRequisitionLine> eq,
                                                                      @Param("documentNumber") String documentNumber,
                                                                      @Param("typeId") Long typeId);
}
