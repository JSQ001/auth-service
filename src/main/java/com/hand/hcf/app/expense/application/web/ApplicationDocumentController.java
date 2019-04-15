package com.hand.hcf.app.expense.application.web;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.WorkFlowDocumentRefCO;
import com.hand.hcf.app.expense.application.domain.ApplicationHeader;
import com.hand.hcf.app.expense.application.enums.ClosedTypeEnum;
import com.hand.hcf.app.expense.application.service.ApplicationHeaderService;
import com.hand.hcf.app.expense.application.web.dto.*;
import com.hand.hcf.app.expense.common.dto.BudgetCheckResultDTO;
import com.hand.hcf.app.expense.common.dto.DocumentLineDTO;
import com.hand.hcf.core.domain.ExportConfig;
import com.hand.hcf.core.util.DateUtil;
import com.hand.hcf.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * <p>
 * 费用申请单头前端控制器
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/22
 */
@RestController
@RequestMapping("/api/expense/application")
public class ApplicationDocumentController {

    @Autowired
    private ApplicationHeaderService service;

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
    public ResponseEntity<BudgetCheckResultDTO> submit(@RequestBody WorkFlowDocumentRefCO workFlowDocumentRef,
                                                       @RequestParam(value = "ignoreWarningFlag", required = false) Boolean ignoreWarningFlag) {
        return ResponseEntity.ok(service.submit(workFlowDocumentRef, ignoreWarningFlag));
    }

    /**
     * @api {POST} /api/expense/application/type 【申请单】创建
     * @apiDescription 创建一个申请单头信息
     * @apiGroup ExpenseService
     * @apiParam (请求对象) {Long} typeId  申请单类型Id
     * @apiParam (请求对象) {Long} employeeId  员工Id
     * @apiParam (请求对象) {String} currencyCode  币种
     * @apiParam (请求对象) {String} remarks  描述
     * @apiParam (请求对象) {Long} companyId  公司Id
     * @apiParam (请求对象) {Long} departmentId  部门Id
     * @apiParam (请求对象) {Long} [contractHeaderId]  关联的合同头Id,合同必填时必填
     * @apiParam (请求对象) {String} [attachmentOid]  附件Oid，多个附件用,拼接
     * @apiParam (请求对象) {Dimensions} [dimensions]  分配的维度对象
     * @apiParam (Dimensions) {Long} value  所选的值
     * @apiParam (Dimensions) {Boolean} name  维度名称
     * @apiParam (Dimensions) {String} dimensionId  维度Id
     * @apiParam (Dimensions) {String} dimensionFiled  字段代码
     * @apiParamExample {json} 请求报文:
     * {
     * "typeId": 1060457723043524609,
     * "employeeId": 1005,
     * "currencyCode": "CNY",
     * "remarks": "天知道",
     * "companyId": 1005,
     * "departmentId": 1005,
     * "contractHeaderId": null,
     * "attachmentOid": null,
     * "dimensions": [
     * {
     * "value": "35525",
     * "name": "区域",
     * "dimensionId": "1032",
     * "dimensionFiled": "dimension1Id",
     * "headerFlag": true,
     * "sequence": 10
     * },
     * {
     * "value": "4444",
     * "name": "项目",
     * "dimensionId": "1033",
     * "dimensionFiled": "dimension2Id",
     * "headerFlag": true,
     * "sequence": 20
     * }]
     * }
     * @apiSuccess (返回对象) {Long} id 申请单Id
     * @apiSuccess (返回对象) {String} documentNumber 单据编号
     * @apiSuccessExample {json} 返回报文:
     * true
     */
    @PostMapping("/header")
    public ResponseEntity createHeader(@RequestBody @Validated ApplicationHeaderWebDTO dto) {

        return ResponseEntity.ok(service.createHeader(dto));
    }

    /**
     * 根据ID查询申请单头信息，编辑时用
     *
     * @param id
     * @return
     */
    @GetMapping("/header/query")
    public ResponseEntity<ApplicationHeaderWebDTO> getHeaderInfoById(@RequestParam("id") Long id) {

        return ResponseEntity.ok(service.getHeaderInfoById(id));
    }

    /**
     * 我的申请单条件查询
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
                                          @RequestParam(value = "closedFlag", required = false) ClosedTypeEnum closedFlag,
                                          @RequestParam(value = "employeeId", required = false) Long employeeId,
                                          Pageable pageable){

        ZonedDateTime requisitionDateFrom = DateUtil.stringToZonedDateTime(dateFrom);
        ZonedDateTime requisitionDateTo = DateUtil.stringToZonedDateTime(dateTo);
        if (requisitionDateTo != null) {
            requisitionDateTo = requisitionDateTo.plusDays(1);
        }
        Page page = PageUtil.getPage(pageable);
        List<ApplicationHeaderWebDTO> result = service.listHeaderDTOsByCondition(page,
                documentNumber,
                typeId,
                requisitionDateFrom,
                requisitionDateTo,
                amountFrom,
                amountTo,
                status,
                currencyCode,
                remarks,
                employeeId,
                closedFlag);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);

        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    @PutMapping("/header")
    public ResponseEntity updateHeader(@RequestBody ApplicationHeaderWebDTO dto) {

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
    public ResponseEntity createLine(@RequestBody ApplicationLineWebDTO dto) {

        return ResponseEntity.ok(service.createLine(dto));
    }

    /**
     * 更新行
     *
     * @param dto
     * @return
     */
    @PutMapping("/line")
    public ResponseEntity updateLine(@RequestBody ApplicationLineWebDTO dto) {

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
    public ResponseEntity<DocumentLineDTO<ApplicationLineWebDTO>> getLinesByHeaderId(@PathVariable("id") Long id,
                                                                                     Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        DocumentLineDTO<ApplicationLineWebDTO> result = service.getLinesByHeaderId(id, page, false);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    /*@PostMapping("/header/submit/check/budget")
    public ResponseEntity checkBudget(@RequestParam("id") Long id){

        return ResponseEntity.ok(service.submitCheckBudget(id));
    }*/

    /**
     * 申请单关闭查询
     *
     * @param documentNumber
     * @param typeId
     * @param dateFrom
     * @param dateTo
     * @param amountFrom
     * @param amountTo
     * @param closedFlag
     * @param currencyCode
     * @param remarks
     * @param companyId
     * @param employeeId
     * @param pageable
     * @return
     */
    @GetMapping("/header/query/closed/condition")
    public ResponseEntity listClosedByCondition(@RequestParam(value = "documentNumber", required = false) String documentNumber,
                                                @RequestParam(value = "typeId", required = false) Long typeId,
                                                @RequestParam(value = "dateFrom", required = false) String dateFrom,
                                                @RequestParam(value = "dateTo", required = false) String dateTo,
                                                @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
                                                @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
                                                @RequestParam(value = "closedFlag", required = false) ClosedTypeEnum closedFlag,
                                                @RequestParam(value = "currencyCode", required = false) String currencyCode,
                                                @RequestParam(value = "remarks", required = false) String remarks,
                                                @RequestParam(value = "companyId", required = false) List<Long> companyId,
                                                @RequestParam(value = "employeeId", required = false) Long employeeId,
                                                Pageable pageable) {

        ZonedDateTime requisitionDateFrom = DateUtil.stringToZonedDateTime(dateFrom);
        ZonedDateTime requisitionDateTo = DateUtil.stringToZonedDateTime(dateTo);
        if (requisitionDateTo != null) {
            requisitionDateTo = requisitionDateTo.plusDays(1);
        }
        Page page = PageUtil.getPage(pageable);
        // 获取查询条件SQL
        Wrapper<ApplicationHeader> wrapper = service.getClosedQueryWrapper(documentNumber, typeId, requisitionDateFrom,
                requisitionDateTo, amountFrom, amountTo, closedFlag, currencyCode, remarks, employeeId, companyId);
        //查询
        List<ApplicationHeaderWebDTO> result = service.listClosedByCondition(page, wrapper);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    /**
     * 导出
     *
     * @param documentNumber
     * @param typeId
     * @param dateFrom
     * @param dateTo
     * @param amountFrom
     * @param amountTo
     * @param closedFlag
     * @param currencyCode
     * @param remarks
     * @param companyId
     * @param employeeId
     * @param exportConfig
     * @param response
     * @param request
     * @throws IOException
     */
    @PostMapping("/header/closed/export")
    public void export(@RequestParam(value = "documentNumber", required = false) String documentNumber,
                       @RequestParam(value = "typeId", required = false) Long typeId,
                       @RequestParam(value = "dateFrom", required = false) String dateFrom,
                       @RequestParam(value = "dateTo", required = false) String dateTo,
                       @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
                       @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
                       @RequestParam(value = "closedFlag", required = false) ClosedTypeEnum closedFlag,
                       @RequestParam(value = "currencyCode", required = false) String currencyCode,
                       @RequestParam(value = "remarks", required = false) String remarks,
                       @RequestParam(value = "companyId", required = false) List<Long> companyId,
                       @RequestParam(value = "employeeId", required = false) Long employeeId,
                       @RequestBody ExportConfig exportConfig,
                       HttpServletResponse response,
                       HttpServletRequest request) throws IOException {
        ZonedDateTime requisitionDateFrom = DateUtil.stringToZonedDateTime(dateFrom);
        ZonedDateTime requisitionDateTo = DateUtil.stringToZonedDateTime(dateTo);
        if (requisitionDateTo != null) {
            requisitionDateTo = requisitionDateTo.plusDays(1);
        }
        service.exportClosedExcel(documentNumber, typeId, requisitionDateFrom, requisitionDateTo, amountFrom, amountTo,
                closedFlag, currencyCode, remarks, employeeId, companyId, response, request, exportConfig);

    }

    /**
     * 申请单关闭行查询
     * @param id
     * @param pageable
     * @return
     */
    @GetMapping("/line/close/query/{id}")
    public ResponseEntity<DocumentLineDTO<ApplicationLineWebDTO>> getCloseLinesByHeaderId(@PathVariable("id") Long id,
                                                                                     Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        DocumentLineDTO<ApplicationLineWebDTO> result = service.getLinesByHeaderId(id, page, true);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }
    /**
     * 关闭申请单
     *
     * @param closedDTO
     * @return
     */
    @PostMapping("/header/closed")
    public ResponseEntity closedHeader(@RequestBody ClosedDTO closedDTO) {
        return ResponseEntity.ok(service.closedHeader(closedDTO));
    }

    @PostMapping("/line/closed/{id}")
    public ResponseEntity closedLine(@RequestParam String message,
                                     @PathVariable("id") Long id,
                                     @RequestParam("headerId") Long headerId) {
        return ResponseEntity.ok(service.closedLine(id, message, headerId));
    }

    /**
     * @api {GET} /api/expense/application/header/submit/check/policy 校验费用政策
     * @apiParam (请求参数) {Long} id 申请单头Id
     */
    @GetMapping("/header/submit/check/policy")
    public ResponseEntity checkPolicy(@RequestParam("id") Long id) {

        return ResponseEntity.ok(service.checkPolicy(id));
    }


    /**
     * @api {GET} /api/expense/application/header/query/created 查询已创建申请单的申请人
     */
    @GetMapping("/header/query/created")
    public List<ContactCO> listUsersByCreatedApplications() {

        return service.listUsersByCreatedApplications();
    }

    /**
     * @Api {get} 费用申请单财务查询
     */
    @GetMapping("/header/query/financial")
    public ResponseEntity listByFinancial(@RequestParam(value = "companyId", required = false) Long companyId,
                                          @RequestParam(value = "typeId", required = false) Long typeId,
                                          @RequestParam(value = "employeeId", required = false) Long employeeId,
                                          @RequestParam(value = "status", required = false) Integer status,
                                          @RequestParam(value = "departmentId", required = false) Long departmentId,
                                          @RequestParam(value = "dateFrom", required = false) String dateFrom,
                                          @RequestParam(value = "dateTo", required = false) String dateTo,
                                          @RequestParam(value = "currencyCode", required = false) String currencyCode,
                                          @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
                                          @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
                                          @RequestParam(value = "reportAmountFrom", required = false) BigDecimal reportAmountFrom,
                                          @RequestParam(value = "reportAmountTo", required = false) BigDecimal reportAmountTo,
                                          @RequestParam(value = "reportAbleAmountFrom", required = false) BigDecimal reportAbleAmountFrom,
                                          @RequestParam(value = "reportAbleAmountTo", required = false) BigDecimal reportAbleAmountTo,
                                          @RequestParam(value = "closedFlag", required = false) ClosedTypeEnum closedFlag,
                                          @RequestParam(value = "remarks", required = false) String remarks,
                                          Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        Page result = service.listByFinancial(companyId,
                typeId,
                employeeId,
                status,
                departmentId,
                dateFrom,
                dateTo,
                currencyCode,
                amountFrom,
                amountTo,
                reportAmountFrom,
                reportAmountTo,
                reportAbleAmountFrom,
                reportAbleAmountTo,
                closedFlag,
                remarks,
                page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/expense/application/header/query/financial");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 预付款关联申请单
     * @param prepaymentTypeId
     * @param companyId
     * @param unitId
     * @param applicantId
     * @param currencyCode
     * @param applicationNumber
     * @param applicationType
     * @param page
     * @param size
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/associated/by/prepayment")
    public ResponseEntity listInfoByCondition(
            @RequestParam Long prepaymentTypeId,
            @RequestParam Long companyId,
            @RequestParam Long unitId,
            @RequestParam Long applicantId,
            @RequestParam String currencyCode,
            @RequestParam(required = false) String applicationNumber,
            @RequestParam(required = false) String applicationType,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size
    ) throws URISyntaxException {

        Page queryPage = PageUtil.getPage(page, size);
        List<ApplicationAssociatePrepaymentDTO> result = service.listInfoByCondition(
                prepaymentTypeId,
                companyId,
                unitId,
                applicantId,
                currencyCode,
                applicationNumber,
                applicationType,
                queryPage
        );
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return  new ResponseEntity<>(result,httpHeaders, HttpStatus.OK);
    }


    /**
     * 关联合同
     * @param contractHeaderId
     * @return
     */
    @GetMapping("/contract")
    public ResponseEntity getContractById(@RequestParam(value = "contractHeaderId") Long contractHeaderId,
                                                                         @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                         @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        Page myPage = PageUtil.getPage(page,size);
        List<ApplicationHeaderWebDTO> result = service.getContractById(contractHeaderId, myPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(myPage);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/expense/application/release 【报账单】关联申请单
     * @apiDescription 关联申请单
     * @apiGroup ExpenseReport
     *
     * @apiParam (请求参数){Long} expenseTypeId 费用类型ID
     * @apiParam (请求参数){String} currencyCode 币种
     * @apiParam (请求参数){Long} expReportHeaderId 报账单头ID
     * @apiParam (请求参数){String} [documentNumber] 申请单编号
     * @apiParam (请求参数){Integer} [page] 页数
     * @apiParam (请求参数){Integer} [size] 每页大小

     * @apiParamExample {json} 请求参数:
    /api/expense/application/release?expenseTypeId=1084663637303009282&currencyCode=CNY&expReportHeaderId=1106225896027717633
     */
    @GetMapping("/release")
    public ResponseEntity<List<ApplicationHeaderAbbreviateDTO>> selectApplicationAndApportionment(@RequestParam Long expenseTypeId,
                                                                                                  @RequestParam String currencyCode,
                                                                                                  @RequestParam Long expReportHeaderId,
                                                                                                  @RequestParam(required = false) String documentNumber,
                                                                                                  Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        List<ApplicationHeaderAbbreviateDTO> applicationHeaderAbbreviateDTOS =
                service.selectApplicationAndApportionment(expenseTypeId, currencyCode, expReportHeaderId, documentNumber, page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity(applicationHeaderAbbreviateDTOS,totalHeader,HttpStatus.OK);
    }

    @RequestMapping("get/release/by/reportId")
    public ResponseEntity<List<ApplicationHeaderWebDTO>>  queryReleaseByReport(@RequestParam(value = "businessCode") String reportNumber,
                                                                               @RequestParam (required = false)String formName,
                                                                                @RequestParam(required = false)String releaseCode,
                                                                                @RequestParam(required = false)String expenseTypeName,
                                                                                Pageable pageable){

        Page page = PageUtil.getPage(pageable);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        List<ApplicationHeaderWebDTO> applicationHeaderWebDTOS = service.queryReleaseByReport(reportNumber,page);
        return new ResponseEntity(applicationHeaderWebDTOS,totalHeader,HttpStatus.OK);
    }
}
