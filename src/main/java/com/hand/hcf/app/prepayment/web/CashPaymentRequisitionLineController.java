package com.hand.hcf.app.prepayment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.prepayment.service.CashPaymentRequisitionLineService;
import com.hand.hcf.app.prepayment.web.dto.CashPaymentRequisitionHeadDto;
import com.hand.hcf.app.prepayment.web.dto.CashPaymentRequisitionLineAssoReqDTO;
import io.swagger.annotations.*;
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
import springfox.documentation.annotations.ApiIgnore;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by cbc on 2017/10/26.
 */
@Api(tags = "预付款单行控制")
@RestController
@AllArgsConstructor
@RequestMapping("/api/cash/prepayment/requisitionLine")
public class CashPaymentRequisitionLineController {

    @Autowired
    private CashPaymentRequisitionLineService cashPaymentRequisitionLineService;
    @GetMapping("/get/line/by/query")
    @ApiOperation(value = "根据查询获取头", notes = "根据查询获取头 开发:cbc")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<CashPaymentRequisitionHeadDto>> getHeadByQuery(
            @ApiParam(value = "预付款编号") @RequestParam(value = "requisitionNumber", required = false) String requisitionNumber, // 预付款编号
            @ApiParam(value = "申请单编号") @RequestParam(value = "documentNumber", required = false) String documentNumber,  // 申请单编号
            @ApiParam(value = "类型ID")@RequestParam(value = "typeId", required = false) Long typeId,
            @ApiParam(value = "行上 现金事务类型") @RequestParam(value = "reptypeId",required = false) Long reptypeId,  //行上 现金事务类型
            @ApiIgnore Pageable pageable
    ) throws URISyntaxException {

        Page page = PageUtil.getPage(pageable);

        Page<CashPaymentRequisitionHeadDto>lineByQuery = cashPaymentRequisitionLineService.getLineByQueryfromApplication(page,requisitionNumber,documentNumber,typeId,reptypeId);

        HttpHeaders headers = PageUtil.generateHttpHeaders(page, "/api/cash/prepayment/requisitionLine/get/line/by/query");
//        headers.add("X-Total-Count", "" + page.getTotal());
        return new ResponseEntity<>(lineByQuery.getRecords(), headers, HttpStatus.OK);
    }

    /**
     *
     * @param documentNumber 申请单单号
     * @param reqTypeName 申请单类型
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/req/query")
    @ApiOperation(value = "根据条件记录预付款申请单行关联请求", notes = "分页查询地点级别信息 开发:程占华")
    public ResponseEntity pageCashPaymentRequisitionLineAssoReqByCond(
            @RequestParam("prepaymentHeaderId") Long prepaymentHeaderId,
            @RequestParam(value = "documentNumber",required = false) String documentNumber,
            @RequestParam(value = "reqTypeName",required = false) String reqTypeName,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashPaymentRequisitionLineAssoReqDTO> result = cashPaymentRequisitionLineService.pageCashPaymentRequisitionLineAssoReqByCond(prepaymentHeaderId,documentNumber, reqTypeName ,page);
        HttpHeaders headers = PageUtil.getTotalHeader(page);
        return new ResponseEntity(result, headers, HttpStatus.OK);
    }

}
