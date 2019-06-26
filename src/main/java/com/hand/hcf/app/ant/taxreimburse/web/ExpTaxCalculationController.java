package com.hand.hcf.app.ant.taxreimburse.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxCalculation;
import com.hand.hcf.app.ant.taxreimburse.service.ExpTaxCalculationService;
import com.hand.hcf.app.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description:
 * @date 2019/6/24 20:35
 */
@RestController
@RequestMapping("/api/exp/tax/calculation")
public class ExpTaxCalculationController {

    @Autowired
    private ExpTaxCalculationService expTaxCalculationService;

    /**
     * 详情页面税金明细信息显示
     *
     * @param reimburseHeaderId
     * @param pageable
     * @return
     */
    @GetMapping("/list/by/headId")
    public ResponseEntity<List<ExpTaxCalculation>> getTaxReportDetail(@RequestParam(required = false) String reimburseHeaderId, Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        List<ExpTaxCalculation> expTaxCalculationList = expTaxCalculationService.getTaxCalculationDetailList(reimburseHeaderId, page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(expTaxCalculationList, httpHeaders, HttpStatus.OK);
    }
}
