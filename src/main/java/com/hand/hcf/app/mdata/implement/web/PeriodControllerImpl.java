package com.hand.hcf.app.mdata.implement.web;

import com.hand.hcf.app.common.co.PeriodCO;
import com.hand.hcf.app.mdata.period.service.PeriodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PeriodControllerImpl {

    @Autowired
    private PeriodsService periodsService;

    /**
     * 根据账套ID与期间Name查询总账期间信息
     * @param setOfBooksId 账套id
     * @param periodName 期间名称
     * @return
     */
    public PeriodCO getPeriodBySetOfBooksIdAndName(@RequestParam("setOfBooksId") Long setOfBooksId,
                                                   @RequestParam("periodName") String periodName) {
        return periodsService.getPeriodBySetOfBooksIdAndName(setOfBooksId,periodName);
    }

    /**
     * 根据账套ID与DateTime查询总账期间信息
     * @param setOfBooksId 账套id
     * @param dateTime 期间Name
     * @return
     */
    public PeriodCO getPeriodBysetOfBooksIdAndDateTime(@RequestParam("setOfBooksId") Long setOfBooksId,
                                                       @RequestParam("dateTime") String dateTime) {
        return periodsService.getPeriodBysetOfBooksIdAndDateTime(setOfBooksId,dateTime);
    }
}
