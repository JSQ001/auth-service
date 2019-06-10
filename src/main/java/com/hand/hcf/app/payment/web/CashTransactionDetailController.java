package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.PaginationUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.payment.domain.CashTransactionDetail;
import com.hand.hcf.app.payment.service.CashTransactionDetailService;
import com.hand.hcf.app.payment.web.dto.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by cbc on 2017/9/29.
 */
@Api(tags = "支付数据明细API")
@RestController
@RequestMapping("/api/cash/transaction/details")
public class CashTransactionDetailController {
    private final CashTransactionDetailService cashTransactionDetailService;

    public CashTransactionDetailController(CashTransactionDetailService cashTransactionDetailService) {
        this.cashTransactionDetailService = cashTransactionDetailService;
    }

    /**
     * 点击确认支付时-----
     * <p>
     * 根据通用信息表的List<dto>，批量插入到明细表里面去
     *
     * @return
     */

    /**
     * @api {POST} /api/cash/transaction/details/insertBatch 【支付平台】根据通用信息表的List，批量插入到明细表里面去
     * @apiDescription 根据报账单ID查询通用支付数据
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} headerId 报账单id
     * @apiParam (请求参数) {String} partnerCategory 收款方类型
     * @apiParam (请求参数) {Long} partnerId 收款方
     * @apiParam (请求参数) {BigDecimal} amountFrom 金额从
     * @apiParam (请求参数) {BigDecimal} amountTo 金额至
     * @apiSuccess (返回参数) {InsertDetailDTO} insertDetailDTO
     * @apiParamExample {json} 请求参数
     *             {
     *                 "dataIds":[1],
     *                 "versionNumbers":[2],
     *                 "currentAmount":"20",
     *                 "cashPayDTO":{
     *                     "payDate":"20170101",
     *                     "payCompanyBankId":"1",
     *                     "payCompanyBankName":"公司银行付款账户名称",
     *                     "payCompanyBankNumber":"公司银行付款账户账号",
     *                     "paymentTypeId":"1",
     *                     "paymentTypeCode":"11111",
     *                     "paymentMethodCategory":"ONLINE_PAYMENT",
     *                     "paymentDescription":"线上支付",
     *                     "currency":"RMB",
     *                     "exchangeRate":"2",
     *                     "remark":"备注"
     *                 }
     *
     *             }
     *
     */

    @ApiOperation(value = "根据通用信息表的List批量插入到明细表", notes = "根据通用信息表的List批量插入到明细表 开发:")
    @PostMapping("/insertBatch")
    public ResponseEntity<Boolean> insertCashTransactionDetail(@ApiParam(value = "插入支付详情") @RequestBody InsertDetailDTO insertDetailDTO) {
        cashTransactionDetailService.insertDetailBatch(insertDetailDTO);
        return ResponseEntity.ok(true);
    }

    public static File createFile(String destFileSource){
        File file  = new File(destFileSource);
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 点击确认支付时-----
     * <p>
     * 根据通用信息表的List<dto>，批量插入到明细表里面去
     *
     * @return
     */

    /**
     * @api {POST} /api/cash/transaction/details/insertBatch/down 【支付平台】落地文件
     * @apiDescription 根据报账单ID查询通用支付数据
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} headerId 报账单id
     * @apiParam (请求参数) {String} partnerCategory 收款方类型
     * @apiParam (请求参数) {Long} partnerId 收款方
     * @apiParam (请求参数) {BigDecimal} amountFrom 金额从
     * @apiParam (请求参数) {BigDecimal} amountTo 金额至
     * @apiSuccess (返回参数) {InsertDetailDTO} insertDetailDTO
     * @apiParamExample {json} 请求参数
     *             {
     *                 "dataIds":[1],
     *                 "versionNumbers":[2],
     *                 "currentAmount":"20",
     *                 "cashPayDTO":{
     *                     "payDate":"20170101",
     *                     "payCompanyBankId":"1",
     *                     "payCompanyBankName":"公司银行付款账户名称",
     *                     "payCompanyBankNumber":"公司银行付款账户账号",
     *                     "paymentTypeId":"1",
     *                     "paymentTypeCode":"11111",
     *                     "paymentMethodCategory":"ONLINE_PAYMENT",
     *                     "paymentDescription":"线上支付",
     *                     "currency":"RMB",
     *                     "exchangeRate":"2",
     *                     "remark":"备注"
     *                 }
     *
     *             }
     *
     */

    @ApiOperation(value = "支付平台落地文件", notes = "支付平台落地文件 开发:")
    @PostMapping("/insertBatch/down")
    public void down(@ApiParam(value = "插入支付详情") @RequestBody InsertDetailDTO insertDetailDTO, HttpServletResponse response, HttpServletRequest request) throws Exception {
        cashTransactionDetailService.insertDetailBatch(insertDetailDTO);
        ServletContext sc = request.getSession().getServletContext();
        String path = sc.getRealPath("/");
        File file  = createFile(path + "/落地文件.txt");
        InputStream in = new FileInputStream(file);
        OutputStreamWriter osw = null;
        try {
            osw=new OutputStreamWriter(new FileOutputStream(file));
            osw.write("落地文件生成成功！");
            osw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String name = "落地文件.txt";
        String contentType = "application/octet-stream";
        String ENC = "utf-8";
        response.reset();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(name, ENC));
        response.addHeader("Pragma","No-cache");
        response.setHeader("Accept-Ranges", "bytes");

        response.setContentType(contentType + ";charset=" + ENC);

        OutputStream out = null;
        try {
            out = response.getOutputStream();
            byte[] bs = new byte[1024];
            BufferedInputStream bf = new BufferedInputStream(in);
            int n = 0;
            while ((n = bf.read(bs)) != -1) {
                out.write(bs, 0, n);
            }
            out.flush();
            bf.close();
        } catch (Exception e){

        } finally {
            osw.close();
            out.close();
            file.delete();
        }
    }
    /**
     * 支付失败或者退票处理查询
     *
     * @param billcode
     * @param documentCategory
     * @param documentNumber
     * @param employeeId
     * @param partnerCategory
     * @param partnerId
     * @param customerBatchNo
     * @param paymentStatus
     * @param refundStatus
     * @param pageable
     * @return
     * @throws URISyntaxException
     */

    /**
     * @api {GET} /api/cash/transaction/details//payFailOrRefund/query 【支付平台】落地文件
     * @apiDescription 根据报账单ID查询通用支付数据
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {String} documentNumber 单据编号
     * @apiParam (请求参数) {String} documentCategory 单据类型
     * @apiParam (请求参数) {Long} employeeId 申请人id
     * @apiParam (请求参数) {Date} payDate 付款日期
     * @apiParam (请求参数) {String } customerBatchNo 批次号
     * @apiParam (请求参数) {String } paymentStatus 支付状态
     * @apiParam (请求参数) {String } refundStatus 退票状态
     * @apiParam (请求参数) {String } paymentMethodCategory 付款方式类型
     * @apiParam (请求参数) {String } partnerCategory 收款方类型
     * @apiParam (请求参数) {Long } partnerId 收款方id
     * @apiParam (请求参数) {int } page 当前页码
     * @apiParam (请求参数) {int } size 大小
     * @apiParamExample {json} 请求参数
     * /api/cash/transaction/details//payFailOrRefund/query?page=1&size=1
     * @apiSuccessExample {json} 成功返回值
     *[
     *             {
     *                 "documentNumber": "101",
     *                 "documentCategory": "EXP_REPORT",
     *                 "employeeName": "cbc",
     *                 "requisitionDate": "2017-09-21T18:33:03+08:00",
     *                 "amount": 1000,
     *                 "payableAmount": 980,
     *                 "currentPayAmount": 980,
     *                 "paymentMethodCategory": "ONLINE_PAYMENT",
     *                 "partnerCategory": "VENDER",
     *                 "partnerName": "cbc",
     *                 "accountNumber": "11111111111",
     *                 "versionNumber": 1,
     *                 "billcode": null,
     *                 "customerBatchNo": null,
     *                 "payDate": null,
     *                 "detailId": null,
     *                 "id": "11",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-12T18:39:32+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-12T18:39:32+08:00",
     *                 "lastUpdatedBy": 1
     *             },
     *             {
     *                 "documentNumber": "101",
     *                 "documentCategory": "ACP_REQUISITION",
     *                 "employeeName": "cbc",
     *                 "requisitionDate": "2017-09-21T18:33:03+08:00",
     *                 "amount": 1000,
     *                 "payableAmount": 980,
     *                 "currentPayAmount": 980,
     *                 "paymentMethodCategory": "OFFLINE_PAYMENT",
     *                 "partnerCategory": "EMPLOYEE",
     *                 "partnerName": "cbc",
     *                 "accountNumber": "11111111111",
     *                 "versionNumber": 1,
     *                 "billcode": null,
     *                 "customerBatchNo": null,
     *                 "payDate": null,
     *                 "detailId": null,
     *                 "id": "12",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-12T18:39:32+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-12T18:39:32+08:00",
     *                 "lastUpdatedBy": 1
     *             },
     *             {
     *                 "documentNumber": "101",
     *                 "documentCategory": "PAYMENT_REQUISITION",
     *                 "employeeName": "cbc",
     *                 "requisitionDate": "2017-09-21T18:33:03+08:00",
     *                 "amount": 1000,
     *                 "payableAmount": 980,
     *                 "currentPayAmount": 980,
     *                 "paymentMethodCategory": "EBANK_PAYMENT",
     *                 "partnerCategory": "VENDER",
     *                 "partnerName": "cbc",
     *                 "accountNumber": "11111111111",
     *                 "versionNumber": 1,
     *                 "billcode": null,
     *                 "customerBatchNo": null,
     *                 "payDate": null,
     *                 "detailId": null,
     *                 "id": "13",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-12T18:39:32+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-12T18:39:32+08:00",
     *                 "lastUpdatedBy": 1
     *             }
     *         ]
     */

    @ApiOperation(value = "支付失败或者退票处理查询", notes = "支付失败或者退票处理查询 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/payFailOrRefund/query")
    public ResponseEntity getCashTransactionDetail(
            @ApiParam(value = "支付类型code") @RequestParam(value = "paymentTypeCode",required = false) String paymentTypeCode,
            @ApiParam(value = "流水号") @RequestParam(required = false) String billcode,
            @ApiParam(value = "单据类型") @RequestParam(required = false) String documentCategory,
            @ApiParam(value = "单据编号") @RequestParam(required = false) String documentNumber,
            @ApiParam(value = "申请人id") @RequestParam(required = false) Long employeeId,
            @ApiParam(value = "申请日期从") @RequestParam(required = false) Date requisitionDateFrom,
            @ApiParam(value = "申请日期至") @RequestParam(required = false) Date requisitionDateTo,
            @ApiParam(value = "支付金额从") @RequestParam(required = false) BigDecimal amountFrom,
            @ApiParam(value = "支付金额至") @RequestParam(required = false) BigDecimal amountTo,
            @ApiParam(value = "收款方类型") @RequestParam(required = false) String partnerCategory,
            @ApiParam(value = "收款方") @RequestParam(required = false) Long partnerId,
            @ApiParam(value = "付款日期从") @RequestParam(required = false) Date payDateFrom,
            @ApiParam(value = "付款日期至") @RequestParam(required = false) Date payDateTo,
            @ApiParam(value = "批次号") @RequestParam(required = false) String customerBatchNo,
            @ApiParam(value = "支付状态") @RequestParam(required = false) String paymentStatus,
            @ApiParam(value = "退票状态") @RequestParam(required = false) String refundStatus,
            @ApiParam(value = "付款公司id") @RequestParam(required = false) List<Long> paymentCompanyId,
            @ApiParam(value = "付款方式类型") @RequestParam(required = false) String paymentMethodCategory,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Calendar c = Calendar.getInstance();
        Calendar d = Calendar.getInstance();
        if(payDateTo!=null){
            c.setTime(payDateTo);
            c.add(Calendar.DAY_OF_MONTH, 1);
        }else {
            c.setTime(new Date());
        }
        if(requisitionDateTo!=null){
            d.setTime(requisitionDateTo);
            d.add(Calendar.DAY_OF_MONTH, 1);
        }else {
            d.setTime(new Date());
        }
        Page page = PageUtil.getPage(pageable);

        Page<CashTransactionDetail> result = cashTransactionDetailService.getCashTransactionDetail(
                paymentCompanyId,
                billcode,
                documentCategory,
                documentNumber,
                employeeId,
                requisitionDateFrom == null ? null : ZonedDateTime.ofInstant(requisitionDateFrom.toInstant(), ZoneId.systemDefault()),
                requisitionDateTo == null ? ZonedDateTime.now() : ZonedDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault()),
                amountFrom,
                amountTo,
                partnerCategory,
                partnerId,
                payDateFrom == null ? null : ZonedDateTime.ofInstant(payDateFrom.toInstant(), ZoneId.systemDefault()),
                payDateTo == null ? ZonedDateTime.now() : ZonedDateTime.ofInstant(c.toInstant(), ZoneId.systemDefault()),
                customerBatchNo,
                page,
                paymentStatus,
                refundStatus,
                paymentTypeCode,
                paymentMethodCategory
        );
        HttpHeaders headers = PageUtil.generateHttpHeaders(result, "/api/cash/transaction/details/payFailOrRefund/query");
        return new ResponseEntity(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 支付中页签点击确认支付
     *
     * @return
     */

    /**
     * @api {POST} /api/cash/transaction/details/paySuccess/{payDate} 【支付平台】支付成功
     * @apiDescription 支付中页签点击确认支付
     * @apiGroup PaymentService
     * @apiParam (请求参数) {CashPayingDTO} cashPayingDTO 现金支付dto
     * @apiParam (请求参数) {Date } payDate 付款日期
     * @apiParamExample {json} 请求参数
     *   {
     *       "detailIds": [1,2,3],
     *       "versionNumbers": [1,2,3]
     *   }
     *
     */

    @ApiOperation(value = "支付中页签点击确认支付", notes = "支付中页签点击确认支付 开发:")
    @PostMapping("/paying/paySuccess/{payDate}")
    public ResponseEntity<Boolean> PaySuccess(@ApiParam(value = "支付dto") @RequestBody CashPayingDTO cashPayingDTO,
                                              @PathVariable("payDate") String payDate) {
        return ResponseEntity.ok(cashTransactionDetailService.PaySuccess(cashPayingDTO, DateUtil.stringToZonedDateTime(payDate)));
    }

    /**
     * 支付中页签点击支付失败页签
     *
     * @return
     */


    /**
     * @api {POST} /api/cash/transaction/details/paying/PayFail 【支付平台】支付失败
     * @apiDescription 支付中页签点击支付失败页签
     * @apiGroup PaymentService
     * @apiParam (请求参数) {CashPayingDTO} cashPayingDTO 现金支付dto
     * @apiParamExample {json} 请求参数
     *   {
     *       "detailIds": [1,2,3],
     *       "versionNumbers": [1,2,3]
     *   }
     *
     */

    @ApiOperation(value = "支付中页签点击支付失败页签", notes = "支付中页签点击支付失败页签 开发:")
    @PostMapping("/paying/PayFail")
    public ResponseEntity<Boolean> FailPay(@ApiParam(value = "支付dto") @RequestBody CashPayingDTO cashPayingDTO) {
        return ResponseEntity.ok(cashTransactionDetailService.PayFail(cashPayingDTO));
    }

    /**
     * 支付中页签条件查询
     *
     * @param billcode
     * @param documentCategory
     * @param documentNumber
     * @param employeeId
     * @param partnerCategory
     * @param partnerId
     * @param payDateFrom
     * @param customerBatchNo
     * @param pageable
     * @return
     * @throws URISyntaxException
     */

    /**
     * @api {GET} /api/cash/transaction/details/paying/query 【支付平台】支付中页签条件查询
     * @apiDescription 支付中页签条件查询
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {String} documentNumber 单据编号
     * @apiParam (请求参数) {String} documentCategory 单据类型
     * @apiParam (请求参数) {Long} employeeId 申请人id
     * @apiParam (请求参数) {Date } payDate 付款日期
     * @apiParam (请求参数) {String } customerBatchNo 批次号
     * @apiParam (请求参数) {String } paymentStatus 支付状态
     * @apiParam (请求参数) {String } refundStatus 退票状态
     * @apiParam (请求参数) {String } paymentMethodCategory 付款方式类型
     * @apiParam (请求参数) {String } partnerCategory 收款方类型
     * @apiParam (请求参数) {Long } partnerId 收款方id
     * @apiParam (请求参数) {int } page 当前页码
     * @apiParam (请求参数) {int } size 大小
     * @apiParamExample {json} 请求参数
     * /api/cash/transaction/details/paying/query?page=1&size=1
     * @apiSuccessExample {json} 成功返回值
     *[
     *             {
     *                 "documentNumber": "101",
     *                 "documentCategory": "EXP_REPORT",
     *                 "employeeName": "cbc",
     *                 "requisitionDate": "2017-09-21T18:33:03+08:00",
     *                 "amount": 1000,
     *                 "payableAmount": 980,
     *                 "currentPayAmount": 980,
     *                 "paymentMethodCategory": "ONLINE_PAYMENT",
     *                 "partnerCategory": "VENDER",
     *                 "partnerName": "cbc",
     *                 "accountNumber": "11111111111",
     *                 "versionNumber": 1,
     *                 "billcode": null,
     *                 "customerBatchNo": null,
     *                 "payDate": null,
     *                 "detailId": null,
     *                 "id": "11",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-12T18:39:32+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-12T18:39:32+08:00",
     *                 "lastUpdatedBy": 1
     *             },
     *             {
     *                 "documentNumber": "101",
     *                 "documentCategory": "ACP_REQUISITION",
     *                 "employeeName": "cbc",
     *                 "requisitionDate": "2017-09-21T18:33:03+08:00",
     *                 "amount": 1000,
     *                 "payableAmount": 980,
     *                 "currentPayAmount": 980,
     *                 "paymentMethodCategory": "OFFLINE_PAYMENT",
     *                 "partnerCategory": "EMPLOYEE",
     *                 "partnerName": "cbc",
     *                 "accountNumber": "11111111111",
     *                 "versionNumber": 1,
     *                 "billcode": null,
     *                 "customerBatchNo": null,
     *                 "payDate": null,
     *                 "detailId": null,
     *                 "id": "12",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-12T18:39:32+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-12T18:39:32+08:00",
     *                 "lastUpdatedBy": 1
     *             },
     *             {
     *                 "documentNumber": "101",
     *                 "documentCategory": "PAYMENT_REQUISITION",
     *                 "employeeName": "cbc",
     *                 "requisitionDate": "2017-09-21T18:33:03+08:00",
     *                 "amount": 1000,
     *                 "payableAmount": 980,
     *                 "currentPayAmount": 980,
     *                 "paymentMethodCategory": "EBANK_PAYMENT",
     *                 "partnerCategory": "VENDER",
     *                 "partnerName": "cbc",
     *                 "accountNumber": "11111111111",
     *                 "versionNumber": 1,
     *                 "billcode": null,
     *                 "customerBatchNo": null,
     *                 "payDate": null,
     *                 "detailId": null,
     *                 "id": "13",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-12T18:39:32+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-12T18:39:32+08:00",
     *                 "lastUpdatedBy": 1
     *             }
     *         ]
     */

    @ApiOperation(value = "支付中页签条件查询", notes = "支付中页签条件查询 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/paying/query")
    public ResponseEntity payingGetCashTransactionDetail(
            @ApiParam(value = "支付类型code") @RequestParam(value = "paymentTypeCode",required = false) String paymentTypeCode,
            @ApiParam(value = "流水号") @RequestParam(required = false) String billcode,
            @ApiParam(value = "单据类型") @RequestParam(required = false) String documentCategory,
            @ApiParam(value = "单据编号") @RequestParam(required = false) String documentNumber,
            @ApiParam(value = "申请人id") @RequestParam(required = false) Long employeeId,
            @ApiParam(value = "申请日期从") @RequestParam(required = false) Date requisitionDateFrom,
            @ApiParam(value = "申请日期至") @RequestParam(required = false) Date requisitionDateTo,
            @ApiParam(value = "支付金额从") @RequestParam(required = false) BigDecimal amountFrom,
            @ApiParam(value = "支付金额至") @RequestParam(required = false) BigDecimal amountTo,
            @ApiParam(value = "收款方类型") @RequestParam(required = false) String partnerCategory,
            @ApiParam(value = "收款方") @RequestParam(required = false) Long partnerId,
            @ApiParam(value = "支付日期从") @RequestParam(required = false) String payDateFrom,
            @ApiParam(value = "支付日期至") @RequestParam(required = false) String payDateTo,
            @ApiParam(value = "批次号") @RequestParam(required = false) String customerBatchNo,
            @ApiParam(value = "付款公司id") @RequestParam(required = false) List<Long> paymentCompanyId,
            @ApiParam(value = "支付方式类型") @RequestParam(value = "paymentMethodCategory",required = false) String paymentMethodCategory,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Date payDateFroms = null;
        Date payDateTos = null;
        if(StringUtils.isNotBlank(payDateFrom)) {
            payDateFroms = DateUtil.stringToDate(payDateFrom);
        }
        if(StringUtils.isNotBlank(payDateTo)) {
            payDateTos = DateUtil.stringToDate(payDateTo);
        }
        Calendar c = Calendar.getInstance();
        Calendar d = Calendar.getInstance();
        if(payDateTo!=null){
            c.setTime(payDateTos);
            c.add(Calendar.DAY_OF_MONTH, 1);
        }else {
            c.setTime(new Date());
        }
        if(requisitionDateTo!=null){
            d.setTime(requisitionDateTo);
            d.add(Calendar.DAY_OF_MONTH, 1);
        }else {
            d.setTime(new Date());
        }

        Page page = PageUtil.getPage(pageable);
        Page<CashTransactionDetail> result = cashTransactionDetailService.payingGetCashTransactionDetail(
                paymentCompanyId,
                billcode,
                documentCategory,
                documentNumber,
                employeeId,
                requisitionDateFrom == null ? null : ZonedDateTime.ofInstant(requisitionDateFrom.toInstant(), ZoneId.systemDefault()),
                requisitionDateTo == null ? ZonedDateTime.now() : ZonedDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault()),
                amountFrom,
                amountTo,
                partnerCategory,
                partnerId,
                payDateFrom == null ? null : ZonedDateTime.ofInstant(payDateFroms.toInstant(), ZoneId.systemDefault()),
                payDateTo == null ? ZonedDateTime.now() : ZonedDateTime.ofInstant(c.toInstant(), ZoneId.systemDefault()),
                customerBatchNo,
                page,
                paymentTypeCode,
                paymentMethodCategory
        );
        HttpHeaders headers = PageUtil.generateHttpHeaders(result, "/api/cash/transaction/details/paying/query");
        return new ResponseEntity(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 已付查询
     * 已付查询页签只显示明细表中支付状态为“支付成功”，且退票状态为“未退票”的数据。
     *
     * @param paymentTypeCode     //付款方式类型代码
     * @param billcode            //付款流水号
     * @param documentCategory    //业务大类(单据类型)
     * @param documentNumber      //单据编号
     * @param employeeId          //申请人id
     * @param partnerCategory     //收款方类型
     * @param partnerId           //收款方id
     * @param customerBatchNo     //付款批次号
     * @param requisitionDateFrom //申请日期从
     * @param requisitionDateTo   //申请日期至
     * @param amountFrom          //支付金额从
     * @param amountTo            //支付金额至
     * @param payDateFrom         //支付日期从
     * @param payDateTo           //支付日期至
     * @param pageable
     * @return
     * @throws URISyntaxException
     */

    /**
     * @api {GET} /api/cash/transaction/details/getAlreadyPaid 【支付平台】已付查询
     * @apiDescription 已付查询
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {String} documentNumber 单据编号
     * @apiParam (请求参数) {String} documentCategory 单据类型
     * @apiParam (请求参数) {Long} employeeId 申请人id
     * @apiParam (请求参数) {Date } payDate 付款日期
     * @apiParam (请求参数) {String } customerBatchNo 批次号
     * @apiParam (请求参数) {String } paymentStatus 支付状态
     * @apiParam (请求参数) {String } refundStatus 退票状态
     * @apiParam (请求参数) {String } paymentMethodCategory 付款方式类型
     * @apiParam (请求参数) {String } partnerCategory 收款方类型
     * @apiParam (请求参数) {Long } partnerId 收款方id
     * @apiParam (请求参数) {int } page 当前页码
     * @apiParam (请求参数) {int } size 大小
     * @apiParamExample {json} 请求参数
     * /api/cash/transaction/details/paying/query?page=1&size=1
     * @apiSuccessExample {json} 成功返回值
     *[
     *             {
     *                 "documentNumber": "101",
     *                 "documentCategory": "EXP_REPORT",
     *                 "employeeName": "cbc",
     *                 "requisitionDate": "2017-09-21T18:33:03+08:00",
     *                 "amount": 1000,
     *                 "payableAmount": 980,
     *                 "currentPayAmount": 980,
     *                 "paymentMethodCategory": "ONLINE_PAYMENT",
     *                 "partnerCategory": "VENDER",
     *                 "partnerName": "cbc",
     *                 "accountNumber": "11111111111",
     *                 "versionNumber": 1,
     *                 "billcode": null,
     *                 "customerBatchNo": null,
     *                 "payDate": null,
     *                 "detailId": null,
     *                 "id": "11",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-12T18:39:32+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-12T18:39:32+08:00",
     *                 "lastUpdatedBy": 1
     *             },
     *             {
     *                 "documentNumber": "101",
     *                 "documentCategory": "ACP_REQUISITION",
     *                 "employeeName": "cbc",
     *                 "requisitionDate": "2017-09-21T18:33:03+08:00",
     *                 "amount": 1000,
     *                 "payableAmount": 980,
     *                 "currentPayAmount": 980,
     *                 "paymentMethodCategory": "OFFLINE_PAYMENT",
     *                 "partnerCategory": "EMPLOYEE",
     *                 "partnerName": "cbc",
     *                 "accountNumber": "11111111111",
     *                 "versionNumber": 1,
     *                 "billcode": null,
     *                 "customerBatchNo": null,
     *                 "payDate": null,
     *                 "detailId": null,
     *                 "id": "12",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-12T18:39:32+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-12T18:39:32+08:00",
     *                 "lastUpdatedBy": 1
     *             },
     *             {
     *                 "documentNumber": "101",
     *                 "documentCategory": "PAYMENT_REQUISITION",
     *                 "employeeName": "cbc",
     *                 "requisitionDate": "2017-09-21T18:33:03+08:00",
     *                 "amount": 1000,
     *                 "payableAmount": 980,
     *                 "currentPayAmount": 980,
     *                 "paymentMethodCategory": "EBANK_PAYMENT",
     *                 "partnerCategory": "VENDER",
     *                 "partnerName": "cbc",
     *                 "accountNumber": "11111111111",
     *                 "versionNumber": 1,
     *                 "billcode": null,
     *                 "customerBatchNo": null,
     *                 "payDate": null,
     *                 "detailId": null,
     *                 "id": "13",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-12T18:39:32+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-12T18:39:32+08:00",
     *                 "lastUpdatedBy": 1
     *             }
     *         ]
     */

    @ApiOperation(value = "已付查询", notes = "已付查询 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/getAlreadyPaid")
    public ResponseEntity<List<CashTransactionDetail>> getAlreadyPaid(
            @ApiParam(value = "支付类型code")@RequestParam(required = false) String paymentTypeCode,
            @ApiParam(value = "流水号") @RequestParam(required = false) String billcode,
            @ApiParam(value = "单据类型") @RequestParam(required = false) String documentCategory,
            @ApiParam(value = "单据编号") @RequestParam(required = false) String documentNumber,
            @ApiParam(value = "申请人id") @RequestParam(required = false) Long employeeId,
            @ApiParam(value = "收款方类型") @RequestParam(value = "partnerCategory", required = false) String partnerCategory,
            @ApiParam(value = "收款方") @RequestParam(value = "partnerId", required = false) Long partnerId,
            @ApiParam(value = "批次号") @RequestParam(value = "customerBatchNo", required = false) String customerBatchNo,
            @ApiParam(value = "申请日期从") @RequestParam(required = false) Date requisitionDateFrom,
            @ApiParam(value = "申请日期至") @RequestParam(required = false) Date requisitionDateTo,
            @ApiParam(value = "支付金额从") @RequestParam(required = false) BigDecimal amountFrom,
            @ApiParam(value = "支付金额至") @RequestParam(required = false) BigDecimal amountTo,
            @ApiParam(value = "支付日期从") @RequestParam(required = false) String payDateFrom,
            @ApiParam(value = "支付日期至") @RequestParam(required = false) String payDateTo,
            @ApiParam(value = "银行回单") @RequestParam(required = false) String returnNumber,
            @ApiParam(value = "付款公司id") @RequestParam(required = false) List<Long> paymentCompanyId,
            @ApiParam(value = "支付方式类型") @RequestParam(value = "paymentMethodCategory",required = false) String paymentMethodCategory,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Date payDateFroms = null;
        Date payDateTos = null;
        if(StringUtils.isNotBlank(payDateFrom)) {
            payDateFroms = DateUtil.stringToDate(payDateFrom);
        }
        if(StringUtils.isNotBlank(payDateTo)) {
            payDateTos = DateUtil.stringToDate(payDateTo);
        }
        Calendar c = Calendar.getInstance();
        Calendar d = Calendar.getInstance();
        if(payDateTo!=null){
            c.setTime(payDateTos);
            c.add(Calendar.DAY_OF_MONTH, 1);
        }else {
            c.setTime(new Date());
        }
        if(requisitionDateTo!=null){
            d.setTime(requisitionDateTo);
            d.add(Calendar.DAY_OF_MONTH, 1);
        }else {
            d.setTime(new Date());
        }
        Page page = PageUtil.getPage(pageable);
        List<CashTransactionDetail> list = cashTransactionDetailService.getAlreadyPaid(
                paymentCompanyId,
                paymentTypeCode,
                billcode,
                documentCategory,
                documentNumber,
                employeeId,
                partnerCategory,
                partnerId,
                customerBatchNo,
                page,
                requisitionDateFrom == null ? null : ZonedDateTime.ofInstant(requisitionDateFrom.toInstant(), ZoneId.systemDefault()),
                requisitionDateTo == null ? ZonedDateTime.now() : ZonedDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault()),
                amountFrom,
                amountTo,
                payDateFrom == null ? null : ZonedDateTime.ofInstant(payDateFroms.toInstant(), ZoneId.systemDefault()),
                payDateTo == null ? ZonedDateTime.now() : ZonedDateTime.ofInstant(c.toInstant(), ZoneId.systemDefault()),
                returnNumber,
                paymentMethodCategory
        );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cash/transaction/details/getAlreadyPaid");
        return new ResponseEntity<>(list, headers, HttpStatus.OK);
    }

    /**
     * 修改支付失败与退票处理界面数据
     *
     * @param list
     * @return
     */

    /**
     * @api {POST} /api/cash/transaction/details/payFailOrRefund 【支付平台】已付查询
     * @apiDescription 该接口用于修改支付失败与退票处理界面数据。
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {String} documentNumber 单据编号
     * @apiParam (请求参数) {String} documentCategory 单据类型
     * @apiParam (请求参数) {Long} employeeId 申请人id
     * @apiParam (请求参数) {Date } payDate 付款日期
     * @apiParam (请求参数) {String } customerBatchNo 批次号
     * @apiParam (请求参数) {String } paymentStatus 支付状态
     * @apiParam (请求参数) {String } refundStatus 退票状态
     * @apiParam (请求参数) {String } paymentMethodCategory 付款方式类型
     * @apiParam (请求参数) {String } partnerCategory 收款方类型
     * @apiParam (请求参数) {Long } partnerId 收款方id
     * @apiParamExample {json} 请求参数
     *   [
     *                 {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     *         ]
     *
     */

    @ApiOperation(value = "修改支付失败与退票处理界面数据", notes = "修改支付失败与退票处理界面数据 开发:")
    @PutMapping("/payFailOrRefund")
    public ResponseEntity updateCashTransactionDetail(@ApiParam(value = "支付明细") @RequestBody List<CashTransactionDetail> list) {
        List<CashTransactionDetail> result = cashTransactionDetailService.updateCashTransactionDetail(list);
        return ResponseEntity.ok(result);
    }


    /**
     * 支付失败与退票中 取消支付
     *
     * @param list
     * @return
     */


    /**
     * @api {POST} /api/cash/transaction/details/payFailOrRefund/cancel 【支付平台】该接口用于支付失败与退票中 取消支付。
     * @apiDescription 该接口用于支付失败与退票中 取消支付。
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {String} documentNumber 单据编号
     * @apiParam (请求参数) {String} documentCategory 单据类型
     * @apiParam (请求参数) {Long} employeeId 申请人id
     * @apiParam (请求参数) {Date } payDate 付款日期
     * @apiParam (请求参数) {String } customerBatchNo 批次号
     * @apiParam (请求参数) {String } paymentStatus 支付状态
     * @apiParam (请求参数) {String } refundStatus 退票状态
     * @apiParam (请求参数) {String } paymentMethodCategory 付款方式类型
     * @apiParam (请求参数) {String } partnerCategory 收款方类型
     * @apiParam (请求参数) {Long } partnerId 收款方id
     * @apiParamExample {json} 请求参数
     *   [
     *                 {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     *         ]
     *
     */

    @ApiOperation(value = "支付失败与退票中取消支付", notes = "支付失败与退票中取消支付 开发:")
    @PostMapping("/payFailOrRefund/cancel")
    public ResponseEntity cancelPay(@ApiParam(value = "支付明细") @RequestBody List<CashTransactionDetail> list) {
        List<CashTransactionDetail> result = cashTransactionDetailService.cancelPay(list);
        return ResponseEntity.ok(result);
    }

    /**
     * 支付失败与退票中 重新支付
     *
     * @param rePayDTO
     * @return
     */

    /**
     * @api {POST} /api/cash/transaction/details/payFailOrRefund 【支付平台】该接口用于支付失败与退票中 重新支付。
     * @apiDescription 该接口用于支付失败与退票中 重新支付。
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {String} documentNumber 单据编号
     * @apiParam (请求参数) {String} documentCategory 单据类型
     * @apiParam (请求参数) {Long} employeeId 申请人id
     * @apiParam (请求参数) {Date } payDate 付款日期
     * @apiParam (请求参数) {String } customerBatchNo 批次号
     * @apiParam (请求参数) {String } paymentStatus 支付状态
     * @apiParam (请求参数) {String } refundStatus 退票状态
     * @apiParam (请求参数) {String } paymentMethodCategory 付款方式类型
     * @apiParam (请求参数) {String } partnerCategory 收款方类型
     * @apiParam (请求参数) {Long } partnerId 收款方id
     * @apiParamExample {json} 请求参数
     *   [
     *                 {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     *         ]
     *
     */

    @ApiOperation(value = "支付失败与退票中重新支付", notes = "支付失败与退票中重新支付 开发:")
    @PostMapping("/payFailOrRefund")
    public ResponseEntity RePay(@ApiParam(value = "支付") @RequestBody RePayDTO rePayDTO) {
        cashTransactionDetailService.RePay(rePayDTO);
        return ResponseEntity.ok().build();
    }
    /**
     * @Author: bin.xie
     * @Description: 落地文件重新支付
     * @param: rePayDTO
     * @return: org.springframework.http.ResponseEntity
     * @Date: Created in 2018/3/13 12:02
     * @Modified by
     */

    /**
     * @api {POST} /api/cash/transaction/details/payFailOrRefund/down 【支付平台】落地文件重新支付
     * @apiDescription 落地文件重新支付
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {String} documentNumber 单据编号
     * @apiParam (请求参数) {String} documentCategory 单据类型
     * @apiParam (请求参数) {Long} employeeId 申请人id
     * @apiParam (请求参数) {Date } payDate 付款日期
     * @apiParam (请求参数) {String } customerBatchNo 批次号
     * @apiParam (请求参数) {String } paymentStatus 支付状态
     * @apiParam (请求参数) {String } refundStatus 退票状态
     * @apiParam (请求参数) {String } paymentMethodCategory 付款方式类型
     * @apiParam (请求参数) {String } partnerCategory 收款方类型
     * @apiParam (请求参数) {Long } partnerId 收款方id
     * @apiParamExample {json} 请求参数
     *   [
     *                 {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     *         ]
     *
     */

    @ApiOperation(value = "落地文件重新支付", notes = "落地文件重新支付 开发:")
    @PostMapping("/payFailOrRefund/down")
    public void RePayDown(@ApiParam(value = "支付") @RequestBody RePayDTO rePayDTO, HttpServletResponse response, HttpServletRequest request) throws Exception {
        cashTransactionDetailService.RePay(rePayDTO);
        ServletContext sc = request.getSession().getServletContext();
        String path = sc.getRealPath("/");
        File file  = createFile(path + "/落地文件.txt");
        InputStream in = new FileInputStream(file);
        OutputStreamWriter osw = null;
        try {
            osw=new OutputStreamWriter(new FileOutputStream(file));
            osw.write("落地文件生成成功！");
            osw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String name = "落地文件.txt";
        String contentType = "application/octet-stream";
        String ENC = "utf-8";
        response.reset();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(name, ENC));
        response.addHeader("Pragma","No-cache");
        response.setHeader("Accept-Ranges", "bytes");

        response.setContentType(contentType + ";charset=" + ENC);

        OutputStream out = null;
        try {
            out = response.getOutputStream();
            byte[] bs = new byte[1024];
            BufferedInputStream bf = new BufferedInputStream(in);
            int n = 0;
            while ((n = bf.read(bs)) != -1) {
                out.write(bs, 0, n);
            }
            out.flush();
            bf.close();
        } catch (Exception e){

        } finally {
            osw.close();
            out.close();
            file.delete();
        }
    }
    /**
     * 查询对应币种的总金额和 总单据数
     *
     * @return
     */

    /**
     * @api {POST} /api/cash/transaction/details/select/totalAmountAndDocumentNum 【支付平台】该接口用于查询对应币种的总金额和 总单据数。
     * @apiDescription 该接口用于查询对应币种的总金额和 总单据数。
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {String} documentNumber 单据编号
     * @apiParam (请求参数) {String} documentCategory 单据类型
     * @apiParam (请求参数) {Long} employeeId 申请人id
     * @apiParam (请求参数) {Date } payDate 付款日期
     * @apiParam (请求参数) {String } customerBatchNo 批次号
     * @apiParam (请求参数) {String } paymentStatus 支付状态
     * @apiParam (请求参数) {String } refundStatus 退票状态
     * @apiParam (请求参数) {String } paymentMethodCategory 付款方式类型
     * @apiParam (请求参数) {String } partnerCategory 收款方类型
     * @apiParam (请求参数) {Long } partnerId 收款方id
     * @apiParamExample {json} 请求参数
     * http://localhost:9997/api/cash/transaction/details/select/totalAmountAndDocumentNum
     * @apiSuccessExample {json} 成功返回值
     *        [
     *             {
     *                 "curreny": "CNY",
     *                 "totalAmount": 302697,
     *                 "documentNumber": 5
     *             }
     *         ]
     */

    @ApiOperation(value = "查询对应币种的总金额和总单数据", notes = "查询对应币种的总金额和总单数据 开发:")
    @GetMapping("/select/totalAmountAndDocumentNum")
    public ResponseEntity getTotalAmountAndDocumentNum(@ApiParam(value = "流水号") @RequestParam(required = false) String billcode,
                                                       @ApiParam(value = "单据类型") @RequestParam(required = false) String documentCategory,
                                                       @ApiParam(value = "单据编号") @RequestParam(required = false) String documentNumber,
                                                       @ApiParam(value = "申请人id") @RequestParam(required = false) Long employeeId,
                                                       @ApiParam(value = "申请日期从") @RequestParam(required = false) String requisitionDateFrom,
                                                       @ApiParam(value = "申请日期至") @RequestParam(required = false) String requisitionDateTo,
                                                       @ApiParam(value = "支付金额从") @RequestParam(required = false) BigDecimal amountFrom,
                                                       @ApiParam(value = "支付金额至") @RequestParam(required = false) BigDecimal amountTo,
                                                       @ApiParam(value = "收款方类型") @RequestParam(required = false) String partnerCategory,
                                                       @ApiParam(value = "收款方") @RequestParam(required = false) Long partnerId,
                                                       @ApiParam(value = "支付类型code") @RequestParam(required = false) String paymentTypeCode,
                                                       @ApiParam(value = "支付日期从") @RequestParam(required = false) String payDateFrom,
                                                       @ApiParam(value = "支付日期至") @RequestParam(required = false) String payDateTo,
                                                       @ApiParam(value = "批次号") @RequestParam(required = false) String customerBatchNo,
                                                       @ApiParam(value = "支付状态") @RequestParam(required = false) String paymentStatus,
                                                       @ApiParam(value = "付款公司id") @RequestParam(required = false) List<Long> paymentCompanyId,
                                                       @ApiParam(value = "退票是否成功") @RequestParam Boolean isRefundOrFail,
                                                       @ApiParam(value = "支付方式类型") @RequestParam(required = false) String paymentMethodCategory)  {
        List<AmountAndDocumentNumberDTO> result = cashTransactionDetailService.getTotalAmountAndDocumentNum(
                paymentCompanyId,
                billcode,
                documentNumber,
                documentCategory,
                employeeId,
                requisitionDateFrom == null ? null : TypeConversionUtils.getStartTimeForDayYYMMDD(requisitionDateFrom) ,
                requisitionDateTo == null? null :  TypeConversionUtils.getEndTimeForDayYYMMDD(requisitionDateTo),
                paymentTypeCode,
                partnerCategory,
                partnerId,
                amountFrom,
                amountTo,
                payDateFrom == null ? null : TypeConversionUtils.getStartTimeForDayYYMMDD(payDateFrom),
                payDateTo == null ? null : TypeConversionUtils.getEndTimeForDayYYMMDD(payDateTo),
                customerBatchNo,
                paymentStatus,
                isRefundOrFail,
                paymentMethodCategory
        );
        return ResponseEntity.ok(result);
    }


    /*
    * 根据通用表id查询支付历史数据
    * */


    /**
     * @api {GET} /api/cash/transaction/details/getHistoryByDateId 【支付平台】根据通用表id查询支付历史数据
     * @apiDescription 根据通用表id查询支付历史数据
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} id 通用表id
     * @apiParamExample {json} 请求参数
     * http://localhost:9997/api/cash/transaction/details/getHistoryByDateId
     * @apiSuccessExample {json} 成功返回值
    {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     */

    @ApiOperation(value = "根据通用表id查询支付历史数据", notes = "根据通用表id查询支付历史数据 开发:")
    @RequestMapping("/getHistoryByDateId")
    public ResponseEntity getHistoryByDateId(@ApiParam(value = "id") @RequestParam Long id) {
        return ResponseEntity.ok(cashTransactionDetailService.getDetailsByDataId(id));
    }


    /*
    * 根据明细表id查询明细信息--供支付流水详情使用
    * */


    /**
     * @api {GET} /api/cash/transaction/details/getDetailById 【支付平台】根据通用表id查询支付历史数据
     * @apiDescription 根据通用表id查询支付历史数据
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} id 明细表id
     * @apiParamExample {json} 请求参数
     * http://localhost:9997/api/cash/transaction/details/getDetailById
     * @apiSuccessExample {json} 成功返回值
    {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     */

    @ApiOperation(value = "根据明细表id查询明细信息", notes = "根据明细表id查询明细信息 开发:")
    @RequestMapping("/getDetailById")
    public ResponseEntity<PaymentOfFlowDetail> getCashTransactionDetailById(@ApiParam(value = "id") @RequestParam Long id) {
        return ResponseEntity.ok(cashTransactionDetailService.getDetailFlowById(id));
    }

    /**
     * @Author: bin.xie
     * @Description: 退票
     * @param: cashTransactionDetail
     * @param: refundDate   退票日期
     * @return
     * @Date: Created in 2018/5/14 11:06
     * @Modified by
     */

    /**
     * @api {POST} /api/cash/transaction/details/refund 【支付平台】退票
     * @apiDescription 退票
     * @apiGroup PaymentService
     * @apiParam (请求参数) {CashTransactionDetail} cashTransactionDetail 明细表
     * @apiParam (请求参数) {String} refundDate 退票日期
     * @apiParamExample {json} 请求参数
     *      {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     * @apiSuccessExample {json} 成功返回值
     *        {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     */

    @ApiOperation(value = "退票", notes = "退票 开发:")
    @PostMapping("/refund")
    public ResponseEntity<CashTransactionDetail> refund(@ApiParam(value = "支付明细") @RequestBody CashTransactionDetail cashTransactionDetail, @RequestParam String refundDate) {
        Date date = new Date();
        if (refundDate != null) {
            date = DateUtil.stringToDate(refundDate);
        }
        return ResponseEntity.ok(cashTransactionDetailService.refund(cashTransactionDetail, date));
    }

    /**
     * @Author: bin.xie
     * @Description: 根据合同头ID查询支付明细
     * @param: contractHeaderId 合同头ID
     * @param: pageable
     * @return: org.springframework.http.ResponseEntity<java.util.List<com.hand.hcf.app.payment.domain.CashTransactionDetail>>
     * @Date: Created in 2018/3/8 11:43
     * @Modified by
     */

    /**
     * @api {GET} /api/cash/transaction/details/getDetailByContractHeaderId 【支付平台】根据合同头ID查询支付明细
     * @apiDescription 根据合同头ID查询支付明细
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} contractHeaderId 合同头ID
     * @apiParam (请求参数) {pageable} pageable 分页
     * @apiSuccessExample {json} 成功返回值
     *        {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     */

    @ApiOperation(value = "根据合同头ID查询支付明细", notes = "根据合同头ID查询支付明细 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/getDetailByContractHeaderId")
    public ResponseEntity<List<CashTransactionDetail>> getDetailByContractHeaderId(@ApiParam(value = "合同头id") @RequestParam Long contractHeaderId,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashTransactionDetail> list = cashTransactionDetailService.getDetailByContractHeaderId(page,contractHeaderId);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cash/transaction/details/getDetailByContractHeaderId");
        return new ResponseEntity<>(list, headers, HttpStatus.OK);
    }



    /**
     *
     * @param sourceId
     * @param reverseCode
     * @param pageable
     * @return
     */
    /**
     * @apiDescription 根据原报账单查支付信息
     * @api {GET} {paymentURL}/api/cash/transaction/details/getDeatilByPublicHeaderId 【财务查询】根据原报账单查支付信息
     * @apiGroup PaymentService
     * @apiParam headerId 报账单id
     * @apiParam billCode 付款流水号
     * @apiParam pageable 分页信息
     * @apiSuccess (返回值) billcode 支付流水号
     * @apiSuccess (返回值) operationType 操作类型
     *  @apiSuccess (返回值) draweeAccountName 付款账户户名
     * @apiSuccess (返回值) draweeAccountNumber 付款账户账号
     *  @apiSuccess (返回值) scheduleDate 付款日期
     * @apiSuccess (返回值) currency 币种
     *  @apiSuccess (返回值) amount  付款金额
     * @apiSuccess (返回值) payeeAccountName 收款方账户户名
     *  @apiSuccess (返回值) payeeAccountNumber 收款账户账号
     * @apiSuccess (返回值) partnerCategoryName 收款方
     *  @apiSuccess (返回值)  draweeName 付款人
     * @apiSuccessExample {json} 成功返回值:
     *  [
    {
    "id": "1000978632409153538",
    "tenantId": "937506219191881730",
    "setOfBooksId": "937515627984846850",
    "reportReverseNumber": "FC18050088",
    "companyId": "928",
    "companyName": null,
    "departmentId": "625575",
    "departmentOid": null,
    "departmentName": null,
    "employeeId": "177601",
    "employeeCode": null,
    "employeeName": null,
    "reverseDate": "2018-05-28T05:54:38Z",
    "applyDate": null,
    "description": "3",
    "currencyCode": "CNY",
    "currencyName": null,
    "currency": null,
    "rate": 1,
    "amount": -77,
    "taxAmount": 0,
    "sourceReportType": "PUBLIC_REPORT",
    "sourceReportTypeId": "6627",
    "sourceReportTypeName": null,
    "sourceReportHeaderId": "992228207591301121",
    "sourceReportHeaderCode": null,
    "status": 1001,
    "auditFlag": "N",
    "auditDate": null,
    "jeCreationStatus": false,
    "jeCreationDate": null,
    "lastModifiedDate": "2018-05-28T05:54:38Z",
    "lastModifiedBy": "177601",
    "createdDate": "2018-05-28T05:54:38Z",
    "createdBy": "177601",
    "createdByName": null,
    "businessClassName": null,
    "businessClass": null,
    "oid": "6a0e44fc-73f3-4c3a-8c53-9d9ea48403e3"
    }
    ]
     *
     */

    @ApiOperation(value = "根据原报账单查支付信息", notes = "根据原报账单查支付信息 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/getDeatilByPublicHeaderId")
    public ResponseEntity<List<CashTransactionDetail>> getDeatilByPublicHeaderId(@ApiParam(value = "头id") @RequestParam Long headerId,
                                                                                 @ApiParam(value = "流水号") @RequestParam(required = false) String billCode,
                                                                                 @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashTransactionDetail> list = cashTransactionDetailService.getDetailByPublicHeaderId(page,headerId,billCode);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cash/transaction/details/getDeatilByPublicHeaderId");
        return new ResponseEntity<>(list, headers, HttpStatus.OK);
    }


    /**
     * @apiDescription 根据原单据ID查询支付明细
     * @api {GET} {paymentURL}/api/cash/transaction/details/getDeatils/by/documentId 【财务查询】根据原单据ID查询支付明细
     * @apiGroup PaymentService
     * @apiParam headerId 单据ID (预付款头id)
     * @apiParam documentCategory 单据大类 (预付款大类：PREPAYMENT_REQUISITION)
     * @apiParam billCode 付款流水号
     * @apiParam pageable 分页信息
     * @apiSuccess (返回值) billcode 支付流水号
     * @apiSuccess (返回值) operationType 操作类型
     *  @apiSuccess (返回值) draweeAccountName 付款账户户名
     * @apiSuccess (返回值) draweeAccountNumber 付款账户账号
     *  @apiSuccess (返回值) scheduleDate 付款日期
     * @apiSuccess (返回值) currency 币种
     *  @apiSuccess (返回值) amount  付款金额
     * @apiSuccess (返回值) payeeAccountName 收款方账户户名
     *  @apiSuccess (返回值) payeeAccountNumber 收款账户账号
     * @apiSuccess (返回值) partnerCategoryName 收款方
     *  @apiSuccess (返回值)  draweeName 付款人
     * @apiSuccessExample {json} 成功返回值:
     *  [
    {
    "id": "1000978632409153538",
    "tenantId": "937506219191881730",
    "setOfBooksId": "937515627984846850",
    "reportReverseNumber": "FC18050088",
    "companyId": "928",
    "companyName": null,
    "departmentId": "625575",
    "departmentOid": null,
    "departmentName": null,
    "employeeId": "177601",
    "employeeCode": null,
    "employeeName": null,
    "reverseDate": "2018-05-28T05:54:38Z",
    "applyDate": null,
    "description": "3",
    "currencyCode": "CNY",
    "currencyName": null,
    "currency": null,
    "rate": 1,
    "amount": -77,
    "taxAmount": 0,
    "sourceReportType": "PUBLIC_REPORT",
    "sourceReportTypeId": "6627",
    "sourceReportTypeName": null,
    "sourceReportHeaderId": "992228207591301121",
    "sourceReportHeaderCode": null,
    "status": 1001,
    "auditFlag": "N",
    "auditDate": null,
    "jeCreationStatus": false,
    "jeCreationDate": null,
    "lastModifiedDate": "2018-05-28T05:54:38Z",
    "lastModifiedBy": "177601",
    "createdDate": "2018-05-28T05:54:38Z",
    "createdBy": "177601",
    "createdByName": null,
    "businessClassName": null,
    "businessClass": null,
    "oid": "6a0e44fc-73f3-4c3a-8c53-9d9ea48403e3"
    }
    ]
     *
     */

    @ApiOperation(value = "根据原单据ID查询支付明细", notes = "根据原单据ID查询支付明细 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/getDeatils/by/documentId")
    public ResponseEntity<List<CashTransactionDetail>> getPaymentDetailsByDocumentId(@ApiParam(value = "头id") @RequestParam Long headerId,
                                                                                     @ApiParam(value = "流水号") @RequestParam(required = false) String billCode,
                                                                                     @ApiParam(value = "单据类型") @RequestParam("documentCategory") String documentCategory,
                                                                                     @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashTransactionDetail> list = cashTransactionDetailService.getPaymentDetailsByDocumentId(page, headerId, billCode, documentCategory);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cash/transaction/details/getDeatilByPublicHeaderId");
        return new ResponseEntity<>(list, headers, HttpStatus.OK);
    }

}
