package com.hand.hcf.app.payment.dashboard.web;

import com.hand.hcf.app.payment.dashboard.service.HandDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class HandDashboardController {

    @Autowired
    private HandDashboardService dashboardService;

    /**
     * 【仪表盘】-付款状况
     * @param startDate
     * @param endDate
     * @return
     *//*
    @RequestMapping(value = "/my/report/payment/situation", method = RequestMethod.GET)
    public ResponseEntity<PaymentSituationDTO> getPaymentSituation(@RequestParam(value = "entityType", required = false) Integer entityType,
                                                                   @RequestParam(value = "startDate", required = false) String startDate,
                                                                   @RequestParam(value = "endDate", required = false) String endDate){
        Long currentUserID = OrgInformationUtil.getCurrentUserId();
        ZonedDateTime dateFrom = null;
        if (startDate != null) {
            String[] startDateArr = startDate.split("-");
            dateFrom = DateUtil.stringToZonedDateTime(startDateArr[0]+"-"+String.format("%02d", Integer.valueOf(startDateArr[1]))+"-01");
        }
        ZonedDateTime dateTo = null;
        if(endDate != null) {
            String[] endDateArr = endDate.split("-");
            dateTo = DateUtil.stringToZonedDateTime(endDateArr[0]+"-"+String.format("%02d", Integer.valueOf(endDateArr[1]))+"-01").plus(1, ChronoUnit.MONTHS);
        }
        PaymentSituationDTO paymentSituationDTO = dashboardService.SearchPaymentSituation(entityType,currentUserID,dateFrom,dateTo);
        return ResponseEntity.ok(paymentSituationDTO);
    }*/


}
