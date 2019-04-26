package com.hand.hcf.app.payment.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.payment.dashboard.dto.PaymentSituationDTO;
import org.apache.ibatis.annotations.Param;

import java.time.ZonedDateTime;


public interface HandDashboardMapper extends BaseMapper<PaymentSituationDTO> {

    PaymentSituationDTO getPaymentSituation(@Param("entityType") Integer entityType,
                                            @Param("userId") Long userId,
                                            @Param("startDate") ZonedDateTime startDate,
                                            @Param("endDate") ZonedDateTime endDate);
}
