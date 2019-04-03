package com.hand.hcf.app.expense.travel.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.WorkFlowDocumentRefCO;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationLineDetail;
import com.hand.hcf.app.expense.travel.service.TravelApplicationHeaderService;
import com.hand.hcf.app.expense.travel.web.dto.TravelApplicationHeaderWebDTO;
import com.hand.hcf.app.expense.travel.web.dto.TravelApplicationLineWebDTO;
import com.hand.hcf.core.util.DateUtil;
import com.hand.hcf.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;


/**
 * 差旅申请单头前端控制器
 * @author zhu.zhao
 * @date 2019/3/11
 */
@RestController
@RequestMapping("/api/travel/application")
public class TravelApplicationDocumentController {
    @Autowired
    private TravelApplicationHeaderService service;


    @PostMapping("/header")
    public ResponseEntity createHeader(@RequestBody @Validated TravelApplicationHeaderWebDTO dto) {

        return ResponseEntity.ok(service.createHeader(dto));
    }

    /**
     * 根据ID查询申请单头信息，编辑时用
     *
     * @param id
     * @return
     */
    @GetMapping("/header/query")
    public ResponseEntity<TravelApplicationHeaderWebDTO> getHeaderInfoById(@RequestParam("id") Long id) {
        return ResponseEntity.ok(service.getHeaderInfoById(id));
    }

    /**
     * 我的申请单条件查询
     *
     * @param documentNumber
     * @param typeId
     * @param dateFrom
     * @param dateTo
     * @param amountFrom
     * @param amountTo
     * @param status
     * @param currencyCode
     * @param remarks
     * @param employeeId
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/header/query/condition")
    public ResponseEntity listByCondition(@RequestParam(value = "documentNumber", required = false) String documentNumber,
                                          @RequestParam(value = "typeId", required = false) Long typeId,
                                          @RequestParam(value = "dateFrom", required = false) String dateFrom,
                                          @RequestParam(value = "dateTo", required = false) String dateTo,
                                          @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
                                          @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
                                          @RequestParam(value = "status", required = false) Integer status,
                                          @RequestParam(value = "currencyCode", required = false) String currencyCode,
                                          @RequestParam(value = "remarks", required = false) String remarks,
                                          @RequestParam(value = "employeeId", required = false) Long employeeId,
                                          Pageable pageable) throws URISyntaxException {

        ZonedDateTime requisitionDateFrom = DateUtil.stringToZonedDateTime(dateFrom);
        ZonedDateTime requisitionDateTo = DateUtil.stringToZonedDateTime(dateTo);
        if (requisitionDateTo != null) {
            requisitionDateTo = requisitionDateTo.plusDays(1);
        }
        Page page = PageUtil.getPage(pageable);
        List<TravelApplicationHeaderWebDTO> result = service.listHeaderDTOsByCondition(page,
                documentNumber,
                typeId,
                requisitionDateFrom,
                requisitionDateTo,
                amountFrom,
                amountTo,
                status,
                currencyCode,
                remarks,
                employeeId);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);

        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    @PutMapping("/header")
    public ResponseEntity updateHeader(@RequestBody TravelApplicationHeaderWebDTO dto) {
        return ResponseEntity.ok(service.updateHeader(dto));
    }

    @DeleteMapping("/header/{id}")
    public ResponseEntity deleteHeader(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.deleteHeader(id));
    }

    @GetMapping("/line/query/info")
    public ResponseEntity queryOtherInfo(@RequestParam("headerId") Long headerId,
                                         @RequestParam(value = "lineId", required = false) Long id,
                                         @RequestParam(value = "isNew", defaultValue = "true") Boolean isNew) {
        return ResponseEntity.ok(service.queryLineInfo(headerId, id, isNew));
    }

    /**
     * 创建行
     *
     * @param dto
     * @return
     */
    @PostMapping("/line")
    public ResponseEntity createLine(@RequestBody TravelApplicationLineWebDTO dto) {

        return ResponseEntity.ok(service.createLine(dto));
    }

    /**
     * 更新行
     *
     * @param dto
     * @return
     */
    @PutMapping("/line")
    public ResponseEntity updateLine(@RequestBody TravelApplicationLineWebDTO dto) {

        return ResponseEntity.ok(service.updateLine(dto));
    }

    /**
     * 删除行
     *
     * @param id
     * @return
     */
    @DeleteMapping("/line/{id}")
    public ResponseEntity deleteLine(@PathVariable("id") Long id) {

        return ResponseEntity.ok(service.deleteLineByLineId(id));
    }

    /**
     * 点击详情查询单据头信息
     *
     * @param id
     * @return
     */
    @GetMapping("/header/{id}")
    public ResponseEntity getHeaderDetail(@PathVariable("id") Long id) {

        return ResponseEntity.ok(service.getHeaderDetailInfo(id));
    }

    @GetMapping("/line/query/{id}")
    public ResponseEntity<List<TravelApplicationLineWebDTO>> getLinesByHeaderId(@PathVariable("id") Long id,
                                                                                     Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        List<TravelApplicationLineWebDTO> result = service.getLinesByHeaderId(id, page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    /**
     * 查询动态维度列信息
     *
     * @param id
     * @return
     */
    @GetMapping("/line/column/{id}")
    public ResponseEntity queryDimensionColumn(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.queryDimensionColumn(id));
    }

    /**
     * @apiDescription 提交工作流
     * @api {POST} /api/expense/application/submit
     * @apiGroup ExpenseService
     * @apiParam {UUID} applicantOid 申请人OID
     * @apiParam {UUID} userOid 用户OID
     * @apiParam {UUID} formOid 表单OID
     * @apiParam {UUID} documentOid 单据OID
     * @apiParam {Integer} documentCategory 单据大类 （如801003)
     * @apiParam {List} countersignApproverOIDs 加签审批人OID
     * @apiParam {String} documentNumber 单据编号
     * @apiParam {String} remark 描述说明
     * @apiParam {Long} companyId 公司ID
     * @apiParam {UUID} unitOid 部门OID
     * @apiParam {String} remark 描述说明
     * @apiParam {Bigdecimal} amount 金额
     * @apiParam {String} currencyCode 币种
     * @apiParam {Long} documentTypeId 单据类型ID
     * @apiSuccessExample {json} 成功返回值:
     * [true]
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public ResponseEntity submit(@RequestBody WorkFlowDocumentRefCO workFlowDocumentRef) {
        return ResponseEntity.ok(service.submit(workFlowDocumentRef));
    }

    /**
     * 更新行明细
     *
     * @param lineDetail
     * @return
     */
    @PutMapping("/line/detail")
    public ResponseEntity updateLineDetail(@RequestBody TravelApplicationLineDetail lineDetail) {

        return ResponseEntity.ok(service.updateLineDetail(lineDetail));
    }
}
