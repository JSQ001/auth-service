package com.hand.hcf.app.prepayment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.prepayment.domain.CashPaymentRequisitionLine;
import com.hand.hcf.app.prepayment.web.dto.CashPaymentRequisitionHeadDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * Created by cbc on 2017/10/26.
 */
public interface CashPaymentRequisitionLineMapper extends BaseMapper<CashPaymentRequisitionLine> {
    List<CashPaymentRequisitionHeadDto> getLineByQueryfromApplication(RowBounds rowBounds,
                                                                      @Param("ew") Wrapper<CashPaymentRequisitionLine> eq,
                                                                      @Param("documentNumber") String documentNumber,
                                                                      @Param("typeId") Long typeId);


    List<CashPaymentRequisitionLine> queryCashPaymentReqLinePara(RowBounds rowBounds,
                                                                 @Param("contractId") Long contractId);
}
