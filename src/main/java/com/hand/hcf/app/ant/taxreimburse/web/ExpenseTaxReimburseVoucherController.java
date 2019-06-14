package com.hand.hcf.app.ant.taxreimburse.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpenseTaxReimburseVoucher;
import com.hand.hcf.app.ant.taxreimburse.service.ExpenseTaxReimburseVoucherService;
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
 * @description:国内税金缴纳报账单凭证信息controller
 * @date 2019/6/14 16:22
 */
@RestController
@RequestMapping("/api/exp/tax/reimburse/voucher")
public class ExpenseTaxReimburseVoucherController {

    @Autowired
    private ExpenseTaxReimburseVoucherService expenseTaxReimburseVoucherService;

    /**
     * 详情页面凭证信息显示-api/exp/tax/reimburse/voucher/list/by/headId
     * @param reimburseHeaderId
     * @param pageable
     * @return
     */
    @GetMapping("list/by/headId")
    public ResponseEntity<List<ExpenseTaxReimburseVoucher>> getTaxReportDetail(@RequestParam(required = false) String reimburseHeaderId, Pageable pageable ){
        Page page = PageUtil.getPage(pageable);
        List<ExpenseTaxReimburseVoucher> taxReimburseVoucherList = expenseTaxReimburseVoucherService.getTaxReportDetailList(reimburseHeaderId,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(taxReimburseVoucherList, httpHeaders, HttpStatus.OK);
    }


}
