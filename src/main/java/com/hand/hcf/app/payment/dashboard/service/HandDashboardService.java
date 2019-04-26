package com.hand.hcf.app.payment.dashboard.service;

import com.hand.hcf.app.payment.dashboard.dto.PaymentSituationDTO;
import com.hand.hcf.app.payment.persistence.HandDashboardMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@Slf4j
@Transactional
public class HandDashboardService {
//    @Autowired
//    private ContractService contractService;
    @Autowired
    private HandDashboardMapper handDashboardMapper;


    public PaymentSituationDTO SearchPaymentSituation(Integer entityType, Long userId, ZonedDateTime startDate, ZonedDateTime endDate){
        PaymentSituationDTO paymentSituationDTO = handDashboardMapper.getPaymentSituation(entityType,userId,startDate,endDate);
        return paymentSituationDTO;
    }
}
