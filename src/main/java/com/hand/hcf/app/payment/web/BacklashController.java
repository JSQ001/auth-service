package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.payment.domain.CashTransactionDetail;
import com.hand.hcf.app.payment.domain.DetailLog;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.service.BacklashService;
import com.hand.hcf.app.payment.service.CashTransactionDetailService;
import com.hand.hcf.app.payment.service.DetailLogService;
import com.hand.hcf.app.payment.utils.RespCode;
import com.hand.hcf.app.payment.web.dto.BacklashDTO;
import com.hand.hcf.app.payment.web.dto.BacklashUpdateDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by cbc on 2018/4/3.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/cash/backlash")
@Slf4j
public class BacklashController {

    private final BacklashService backlashService;
    private final CashTransactionDetailService detailService;
    private final DetailLogService logService;
    private PaymentOrganizationService organizationService;

    /**
     *  获取反冲数据
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {POST} /api/cash/backlash 【获取反冲数据】获取反冲数据
     * @apiDescription 该接口用于获取反冲数据
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {String} documentNumber 单据编号
     * @apiParam (请求参数) {String} documentTypeId 单据类型
     * @apiParam (请求参数) {Long} partnerId 收款方
     * @apiParam (请求参数) {String} partnerCategory 收款方类型
     * @apiParam (请求参数) {Boolean} isEnabled 是否启用
     * @apiParam (请求参数) {BigDecimal} amountFrom 金额从
     * @apiParam (请求参数) {BigDecimal} amountTo 金额至
     * @apiParam (请求参数) {String} payDateFrom 付款日期从
     * @apiParam (请求参数) {String} payDateTo 付款日期至
     * @apiParam (请求参数) {Long} applicant 申请人
     * @apiParam (请求参数) {String} partnerName 收款方名称
     * @apiParam (请求参数) {String} sign 供应商类型
     * @apiParam (请求参数) {Long} tenantId 租户ID
     * @apiSuccess (返回参数) {Long} id 主键ID
     * @apiSuccess (返回参数) {Boolean} isEnabled 是否启用
     * @apiSuccess (返回参数) {Boolean} isDeleted 是否删除
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate 创建日期
     * @apiSuccess (返回参数) {Long} createdBy 创建人
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (返回参数) {Long} lastUpdatedBy 最后更新人
     * @apiSuccess (返回参数) {Integer} versionNumber 版本号


     * @apiParamExample {json}
     * {
     *   "billcode": 1,
     *   "documentNumber": "1DW3232323",
     *   "documentTypeId": "130",
     *   "sign": "EMPLOYEE",
     * }
     * @apiSuccessExample {json} 成功返回值
     * [{
     *   "id": "905693428099588098",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-07T15:25:14.889+08:00",
     *   "createdBy": 11,
     *   "lastUpdatedDate": "2017-09-07T15:25:14.889+08:00",
     *   "lastUpdatedBy": 11,
     *   "versionNumber": 1
     * }]
     */
    @GetMapping
    public ResponseEntity getBacklash(@RequestParam(required = false) String billcode,
                                      @RequestParam(required = false) String documentNumber,
                                      @RequestParam(required = false) String documentTypeId,
                                      @RequestParam(required = false) Long partnerId,
                                      @RequestParam(required = false) String partnerCategory,
                                      @RequestParam(required = false) BigDecimal amountFrom,
                                      @RequestParam(required = false) BigDecimal amountTo,
                                      @RequestParam(required = false) String payDateFrom,
                                      @RequestParam(required = false) String payDateTo,
                                      @RequestParam(required = false) Long applicant,
                                      @RequestParam(required = false) String partnerName,
                                      @RequestParam(required = false) String sign,
                                      @RequestParam(required = false) Long tenantId,
                                      Pageable pageable
    ) throws URISyntaxException, ParseException {
        Page page = PageUtil.getPage(pageable);

        ZonedDateTime dateFrom = DateUtil.stringToZonedDateTime(payDateFrom);
        ZonedDateTime dateTo = DateUtil.stringToZonedDateTime(payDateTo);
        if (dateTo != null){
            dateTo = dateTo.plusDays(1);
        }

        Page<CashTransactionDetail> result = backlashService.getBacklash(billcode,documentNumber,documentTypeId,partnerId,amountFrom,amountTo,dateFrom,dateTo,applicant,partnerCategory,partnerName,sign,tenantId,page);
        HttpHeaders headers = PageUtil.generateHttpHeaders(result, "/api/cash/backlash");
        return new ResponseEntity(result.getRecords(),headers, HttpStatus.OK);
    }


    //发起反冲操作
    @GetMapping("/to/backlash")
    public ResponseEntity<BacklashDTO> toBacklash(@RequestParam Long detailId){
        //根据明细id查询明细信息
        BacklashDTO backlashDTO = backlashService.toBacklash(detailId);
        return ResponseEntity.ok(backlashDTO);
    }


    //反冲更新
    @PostMapping("/update/backlash")
    public ResponseEntity<BacklashDTO> saveBacklash(@RequestBody BacklashUpdateDTO backlashUpdateDTO){
        BacklashDTO dto = backlashService.updateBacklash(backlashUpdateDTO);
        return ResponseEntity.ok(dto);
    }

    //删除反冲
    @DeleteMapping("/delete/backlash")
    @Transactional
    public ResponseEntity<Boolean> deleteById(@RequestParam Long id){

        CashTransactionDetail detail = detailService.selectById(id);
        boolean deleteFlag = detailService.deleteById(id);

        //删除日志表里面的数据
        List<DetailLog> logList = logService.selectList(
                new EntityWrapper<DetailLog>()
                        .eq("detail_id", id)
        );
        if(!CollectionUtils.isEmpty(logList)){
            logService.deleteBatchIds(logList.stream().map(DetailLog::getId).collect(Collectors.toList()));
        }
        if (StringUtils.hasText(detail.getBackFlashAttachmentOids())){
            String [] ids = detail.getBackFlashAttachmentOids().split(",");
            try {
                organizationService.deleteByOids(Arrays.asList(ids));
            }catch (Exception e){
                log.error("删除关联附件失败！");
            }
        }
        return ResponseEntity.ok(deleteFlag);
    }


    //反冲提交
    @PostMapping("/submit/backlash")
    public ResponseEntity<Boolean> submitBacklash(@RequestParam Long id){
        try {
            CashTransactionDetail detail = detailService.selectById(id);
            backlashService.submitBacklash(detail);
        }catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(RespCode.SYS_ERROR);
        }
        return ResponseEntity.ok(true);
    }


    //根据反冲数据id查询单据信息，原支付明细信息，反冲单据信息
    @GetMapping("/get/by/backlash/id")
    public ResponseEntity<BacklashDTO> getBacklashDTOByBacklashId(@RequestParam Long id){
        BacklashDTO backlashDTO = backlashService.backInfo(detailService.selectById(id));
        return ResponseEntity.ok(backlashDTO);
    }


    //查询我发起的反冲单据
    @GetMapping("/get/backlash/by/user")
    public ResponseEntity<List<CashTransactionDetail>> getBacklashByUserId(
            @RequestParam(value = "userId",required = false) Long userId,
            @RequestParam(value = "backlashCode",required = false) String backlashCode,
            @RequestParam(value = "billCode",required = false) String billCode,
            @RequestParam(value = "backFlashDateFrom",required = false)  String backFlashDateFrom,
            @RequestParam(value = "backFlashDateTo",required = false) String backFlashDateTo,
            @RequestParam(value = "backlashAmountFrom",required = false) BigDecimal backlashAmountFrom,
            @RequestParam(value = "backlashAmountTo",required = false) BigDecimal backlashAmountTo,
            @RequestParam(value = "backlashStatus",required = false) String backlashStatus,
            Pageable pageable
    ) throws URISyntaxException, ParseException {
        Calendar c = Calendar.getInstance();
        if(backFlashDateTo!=null){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(backFlashDateTo);
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, 1);
            backFlashDateTo = sdf.format(c.getTime());
        }
        ZonedDateTime dateFrom = DateUtil.stringToZonedDateTime(backFlashDateFrom);
        ZonedDateTime dateTo = DateUtil.stringToZonedDateTime(backFlashDateTo);
        Page page = PageUtil.getPage(pageable);
        Page<CashTransactionDetail> result = backlashService.getBacklashByInput(userId==null? OrgInformationUtil.getCurrentUserId():userId, backlashCode, billCode, dateFrom, dateTo, backlashAmountFrom, backlashAmountTo, backlashStatus, page);
        HttpHeaders headers = PageUtil.generateHttpHeaders(result, "/api/cash/backlash/get/backlash/by/user");
        return new ResponseEntity(result.getRecords(),headers, HttpStatus.OK);
    }


    /**
     * 反冲复核通过&反冲复核驳回
     * @param detailId
     * @param remark
     * @param status
     * @return
     */
    @PostMapping("/update/status")
    public ResponseEntity<Boolean> updateStatusById(
            @RequestParam(value = "detailId") Long detailId,
            @RequestParam(value = "remark",required = false) String remark,
            @RequestParam(value = "status") String status,
            @RequestParam(value = "backlashRemark",required = false) String backlashRemark
    ){
        if ("undefined".equals(backlashRemark)){
            backlashRemark = "";
        }
        if ("undefined".equals(remark)){
            remark = null;
        }
        Boolean recheck = backlashService.notRecheck(remark == null ? "" : remark, status, detailId,backlashRemark);
        return ResponseEntity.ok(recheck);
    }



    @GetMapping("/get/recheck/by/input")
    public ResponseEntity<List<CashTransactionDetail>> getRecheckByInput(
            @RequestParam("status") String status,
            @RequestParam(value = "billCode",required = false) String billCode,
            @RequestParam(value = "refBillCode",required = false)String refBillCode,
            @RequestParam(value = "amountFrom",required = false) BigDecimal amountFrom,
            @RequestParam(value = "amountTo",required = false) BigDecimal amountTo,
            @RequestParam(value = "backFlashDateFrom",required = false)  String backFlashDateFrom,
            @RequestParam(value = "backFlashDateTo",required = false) String backFlashDateTo,
            Pageable pageable
     ) throws URISyntaxException, ParseException {
        Calendar c = Calendar.getInstance();
        if(StringUtils.hasText(backFlashDateTo)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(backFlashDateTo);
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, 1);
            backFlashDateTo = sdf.format(c.getTime());
        }
        ZonedDateTime dateFrom = DateUtil.stringToZonedDateTime(backFlashDateFrom);
        ZonedDateTime dateTo = DateUtil.stringToZonedDateTime(backFlashDateTo);
        Page page = PageUtil.getPage(pageable);
        Page<CashTransactionDetail> result = backlashService.getRecheck(billCode, refBillCode, dateFrom, dateTo, amountFrom, amountTo, status, page);
        HttpHeaders headers = PageUtil.generateHttpHeaders(result, "/api/cash/backlash/get/recheck/by/input");
        return new ResponseEntity(result.getRecords(),headers, HttpStatus.OK);

    }


    /**
     * 根据明细id查询反冲单据和原单据信息
     * @param id
     * @return
     */
    @GetMapping("/get/ready/by/detail/id")
    public ResponseEntity<BacklashDTO> getReadyByDetailId(@RequestParam Long id){
        return ResponseEntity.ok(backlashService.getReadyByDetailId(id,true));
    }


    /**
     * 根据反冲明细id查询具体信息
     * @param id
     * @return
     */
    @GetMapping("/get/by/backlash/detail/id")
    public ResponseEntity<BacklashDTO> getBacklashDTOByBacklashDetailId(@RequestParam Long id){
        CashTransactionDetail backlash = detailService.selectById(id);
        BacklashDTO backlashDTO = backlashService.getReadyByDetailId(backlash.getRefCashDetailId(),false);
        CashTransactionDetail detail = backlashService.addAttachmentInfo(id);
        backlashDTO.setBackDetail(detail);
        return ResponseEntity.ok(backlashDTO);
    }
}
