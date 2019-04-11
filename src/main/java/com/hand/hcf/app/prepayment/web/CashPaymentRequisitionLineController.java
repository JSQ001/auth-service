package com.hand.hcf.app.prepayment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.prepayment.service.CashPaymentRequisitionLineService;
import com.hand.hcf.app.prepayment.web.dto.CashPaymentRequisitionHeadDto;
import com.hand.hcf.core.util.PageUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by cbc on 2017/10/26.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/cash/prepayment/requisitionLine")
public class CashPaymentRequisitionLineController {

    @Autowired
    private CashPaymentRequisitionLineService cashPaymentRequisitionLineService;
    @GetMapping("/get/line/by/query")
    public ResponseEntity<List<CashPaymentRequisitionHeadDto>> getHeadByQuery(
            @RequestParam(value = "requisitionNumber", required = false) String requisitionNumber, // 预付款编号
            @RequestParam(value = "documentNumber", required = false) String documentNumber,  // 申请单编号
            @RequestParam(value = "typeId", required = false) Long typeId,
            @RequestParam(value = "reptypeId",required = false) Long reptypeId,  //行上 现金事务类型
            Pageable pageable
    ) throws URISyntaxException {

        Page page = PageUtil.getPage(pageable);

        Page<CashPaymentRequisitionHeadDto>lineByQuery = cashPaymentRequisitionLineService.getLineByQueryfromApplication(page,requisitionNumber,documentNumber,typeId,reptypeId);

        HttpHeaders headers = PageUtil.generateHttpHeaders(page, "/api/cash/prepayment/requisitionLine/get/line/by/query");
//        headers.add("X-Total-Count", "" + page.getTotal());
        return new ResponseEntity<>(lineByQuery.getRecords(), headers, HttpStatus.OK);
    }
}
