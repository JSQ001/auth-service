package com.hand.hcf.app.mdata.dashboard.web;

import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.mdata.dashboard.dto.*;
import com.hand.hcf.app.mdata.dashboard.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;



    /**
     * 获取两个日期之间所有的月份集合
     * @param startTime
     * @param endTime
     * @return：YYYY-MM
     */
    public static List<String> getMonthBetweenDate(String startTime, String endTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        // 声明保存日期集合
        List<String> list = new ArrayList<>();
        try {
            // 转化成日期类型
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);

            //用Calendar 进行日期比较判断
            Calendar calendar = Calendar.getInstance();
            while (startDate.getTime()<=endDate.getTime()){
                // 把日期添加到集合
                list.add(sdf.format(startDate));
                // 设置日期
                calendar.setTime(startDate);
                //把日期增加一个月
                calendar.add(Calendar.MONTH, 1);
                // 获取增加后的日期
                startDate=calendar.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 【仪表盘】-费用趋势
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/my/report/cost/trend", method = RequestMethod.GET)
    public ResponseEntity<CostTrendObjDTO> getCostTrendObj(@RequestParam(value = "startDate", required = false) String startDate,
                                                           @RequestParam(value = "endDate", required = false) String endDate){
        List<CostTrendDTO> costTrendDTOList = new ArrayList<>();
        List<TempCostTrendDTO> tempCostTrendDTOList;
        Long currentUserID = LoginInformationUtil.getCurrentUserId();
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
        tempCostTrendDTOList = dashboardService.listCostTrend(currentUserID,2002,dateFrom,dateTo);
        List<String> monthList = getMonthBetweenDate(startDate,endDate);
        for(int i = 0; i < monthList.size(); i++) {
            CostTrendDTO costTrendDTO = new CostTrendDTO();
            costTrendDTO.setDate(monthList.get(i));
            BigDecimal value = new BigDecimal(0);
            for (TempCostTrendDTO tempCostTrendDTO : tempCostTrendDTOList) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
                if (tempCostTrendDTO.getReportDate() != null) {
                    String date = tempCostTrendDTO.getReportDate().format(dateTimeFormatter);
                    if(monthList.get(i).equals(date)){
                        value = value.add(tempCostTrendDTO.getFunctionalAmount());
                    }
                }
            }
            costTrendDTO.setValue(value.setScale(2));
            costTrendDTOList.add(costTrendDTO);
        }
        CostTrendObjDTO costTrendObjDTO = new CostTrendObjDTO();
        BigDecimal maxValue = new BigDecimal(0);
        BigDecimal minValue = new BigDecimal(0);
        BigDecimal avgValue = new BigDecimal(0);
        BigDecimal totalValue = new BigDecimal(0);
        for(int i = 0; i < costTrendDTOList.size(); i++) {
            if(maxValue.compareTo(costTrendDTOList.get(i).getValue()) == -1){
                maxValue = costTrendDTOList.get(i).getValue();
            }
            if(minValue.compareTo(costTrendDTOList.get(i).getValue()) == 1){
                minValue = costTrendDTOList.get(i).getValue();
            }
            totalValue = totalValue.add(costTrendDTOList.get(i).getValue());
        }
        if(totalValue.compareTo(new BigDecimal(0)) == 1) {
            avgValue = totalValue.divide(new BigDecimal(costTrendDTOList.size()), 2, BigDecimal.ROUND_HALF_DOWN);
        }
        costTrendObjDTO.setMaxValue(maxValue);
        costTrendObjDTO.setMinValue(minValue);
        costTrendObjDTO.setAvgValue(avgValue);
        costTrendObjDTO.setList(costTrendDTOList);
        return ResponseEntity.ok(costTrendObjDTO);
    }

    /**
     * 【仪表盘】-费用占比
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/my/report/cost/ratio", method = RequestMethod.GET)
    public ResponseEntity<List<CostRatioDTO>> listCostRatio(@RequestParam(value = "startDate", required = false) String startDate,
                                                            @RequestParam(value = "endDate", required = false) String endDate){
        List<CostRatioDTO> list;
        Long currentUserID = LoginInformationUtil.getCurrentUserId();
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
        list = dashboardService.listCostRatio(currentUserID,2002,dateFrom,dateTo);
        return ResponseEntity.ok(list);
    }

    /**
     * 【仪表盘】-付款状况
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/my/report/payment/situation", method = RequestMethod.GET)
    public ResponseEntity<PaymentSituationDTO> getPaymentSituation(@RequestParam(value = "entityType", required = false) Integer entityType,
                                                                   @RequestParam(value = "startDate", required = false) String startDate,
                                                                   @RequestParam(value = "endDate", required = false) String endDate){
        Long currentUserID = LoginInformationUtil.getCurrentUserId();
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
        PaymentSituationDTO paymentSituationDTO = dashboardService.getPaymentSituation(entityType,currentUserID,dateFrom,dateTo);
        return ResponseEntity.ok(paymentSituationDTO);
    }


}
