package com.hand.hcf.app.mdata.dashboard.service;

import com.hand.hcf.app.mdata.dashboard.dto.CostRatioDTO;
import com.hand.hcf.app.mdata.dashboard.dto.PaymentSituationDTO;
import com.hand.hcf.app.mdata.dashboard.dto.TempCostTrendDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
public class DashboardService {

    public List<TempCostTrendDTO> listCostTrend(Long userId, Integer status, ZonedDateTime startDate, ZonedDateTime endDate){
        List<TempCostTrendDTO> list = new ArrayList<>();
        return list;
    }

    public List<CostRatioDTO> listCostRatio(Long userId, Integer status, ZonedDateTime startDate, ZonedDateTime endDate){
        List<CostRatioDTO> list = new ArrayList<>();
        return list;
    }
    public PaymentSituationDTO getPaymentSituation(Integer entityType, Long userId, ZonedDateTime startDate, ZonedDateTime endDate){
        PaymentSituationDTO paymentSituationDTO = new PaymentSituationDTO();
        return null;
    }

}
