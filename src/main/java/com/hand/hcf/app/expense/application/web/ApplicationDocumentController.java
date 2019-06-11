package com.hand.hcf.app.expense.application.web;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.WorkFlowDocumentRefCO;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.application.domain.ApplicationHeader;
import com.hand.hcf.app.expense.application.enums.ClosedTypeEnum;
import com.hand.hcf.app.expense.application.service.ApplicationHeaderService;
import com.hand.hcf.app.expense.application.web.dto.*;
import com.hand.hcf.app.expense.common.dto.BudgetCheckResultDTO;
import com.hand.hcf.app.expense.common.dto.DocumentLineDTO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
@Api(tags = "费用申请单")
public class ApplicationDocumentController {

    @Autowired
    private ApplicationHeaderService service;


    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @ApiOperation(value = "提交工作流", notes = "提交工作流 开发:bin.xie")
    public ResponseEntity<BudgetCheckResultDTO> submit(@ApiParam(value = "工作流文档参数") @RequestBody WorkFlowDocumentRefCO workFlowDocumentRef,
                                                       @ApiParam(value = "忽略警告标志") @RequestParam(value = "ignoreWarningFlag", required = false) Boolean ignoreWarningFlag) {
        return ResponseEntity.ok(service.submit(workFlowDocumentRef, ignoreWarningFlag));
    }

    @PostMapping("/header")
    @ApiOperation(value = "创建一个申请单头信息", notes = "创建一个申请单头信息 开发:bin.xie")
    public ResponseEntity createHeader(@ApiParam(value = "申请单头信息") @RequestBody @Validated ApplicationHeaderWebDTO dto) {

        return ResponseEntity.ok(service.createHeader(dto));
    }

    /**
     * 根据ID查询申请单头信息，编辑时用
     *
     * @param id
     * @return
     */
    @GetMapping("/header/query")
    @ApiOperation(value = "根据ID查询申请单头信息，编辑时用", notes = "根据ID查询申请单头信息，编辑时用 开发:bin.xie")
    public ResponseEntity<ApplicationHeaderWebDTO> getHeaderInfoById(@ApiParam(value = "id") @RequestParam("id") Long id) {

        return ResponseEntity.ok(service.getHeaderInfoById(id));
    }

    /**
     * 我的申请单条件查询
     */
    @GetMapping("/header/query/condition")
    @ApiOperation(value = "我的申请单条件查询", notes = "我的申请单条件查询 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity listByCondition(@ApiParam(value = "文档编号") @RequestParam(value = "documentNumber", required = false) String documentNumber,
                                          @ApiParam(value = "申请单类型Id") @RequestParam(value = "typeId", required = false) Long typeId,
                                          @ApiParam(value = "日期从") @RequestParam(value = "dateFrom", required = false) String dateFrom,
                                          @ApiParam(value = "日期到") @RequestParam(value = "dateTo", required = false) String dateTo,
                                          @ApiParam(value = "金额从") @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
                                          @ApiParam(value = "金额到") @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
                                          @ApiParam(value = "状态") @RequestParam(value = "status", required = false) Integer status,
                                          @ApiParam(value = "币种") @RequestParam(value = "currencyCode", required = false) String currencyCode,
                                          @ApiParam(value = "描述") @RequestParam(value = "remarks", required = false) String remarks,
                                          @ApiParam(value = "关闭标志") @RequestParam(value = "closedFlag", required = false) ClosedTypeEnum closedFlag,
                                          @ApiParam(value = "员工Id") @RequestParam(value = "employeeId", required = false) Long employeeId,
                                          @ApiParam(value = "编者") @RequestParam(required = false,defaultValue = "false") Boolean editor,
                                          @ApiIgnore Pageable pageable){

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
                closedFlag,
                editor);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);

        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    @PutMapping("/header")
    @ApiOperation(value = "更新头信息", notes = "更新头信息 开发:bin.xie")
    public ResponseEntity updateHeader(@ApiParam(value = "申请单头表") @RequestBody ApplicationHeaderWebDTO dto) {

        return ResponseEntity.ok(service.updateHeader(dto));
    }

    @DeleteMapping("/header/{id}")
    @ApiOperation(value = "删除头信息", notes = "删除头信息 开发:bin.xie")
    public ResponseEntity deleteHeader(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.deleteHeader(id));
    }

    @GetMapping("/line/query/info")
    @ApiOperation(value = "查询其他信息", notes = "查询其他信息 开发:bin.xie")
    public ResponseEntity queryOtherInfo(@ApiParam(value = "头ID") @RequestParam("headerId") Long headerId,
                                         @ApiParam(value = "行ID") @RequestParam(value = "lineId", required = false) Long id,
                                         @ApiParam(value = "是否新建") @RequestParam(value = "isNew", defaultValue = "true") Boolean isNew) {
        return ResponseEntity.ok(service.queryLineInfo(headerId, id, isNew));
    }

    /**
     * 创建行
     *
     * @param dto
     * @return
     */
    @PostMapping("/line")
    @ApiOperation(value = "创建行", notes = "创建行 开发:bin.xie")
    public ResponseEntity createLine(@ApiParam(value = "申请单行表") @RequestBody ApplicationLineWebDTO dto) {

        return ResponseEntity.ok(service.createLine(dto));
    }

    @ApiOperation(value = "根据行id复制一行数据", notes = "根据行id复制一行数据 开发:毛仕林")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lineId", value = "复制的行id", dataType = "Long")
    })
    @GetMapping("/copyLineByLineId")
    public ResponseEntity copyLineByLineId(@ApiParam(value = "行ID") @RequestParam("lineId") Long lineId) {
        return  ResponseEntity.ok(service.copyLine(lineId));
    }
    /**
     * 更新行
     *
     * @param dto
     * @return
     */
    @PutMapping("/line")
    @ApiOperation(value = "更新行", notes = "更新行 开发:bin.xie")
    public ResponseEntity updateLine(@ApiParam(value = "申请单行表") @RequestBody ApplicationLineWebDTO dto) {

        return ResponseEntity.ok(service.updateLine(dto));
    }

    /**
     * 删除行
     *
     * @param id
     * @return
     */
    @DeleteMapping("/line/{id}")
    @ApiOperation(value = "删除行", notes = "删除行 开发:bin.xie")
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
    @ApiOperation(value = "查询动态维度列信息", notes = "查询动态维度列信息 开发:bin.xie")
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
    @ApiOperation(value = "点击详情查询单据头信息", notes = "点击详情查询单据头信息 开发:bin.xie")
    public ResponseEntity getHeaderDetail(@PathVariable("id") Long id) {

        return ResponseEntity.ok(service.getHeaderDetailInfo(id));
    }

    @GetMapping("/line/query/{id}")
    @ApiOperation(value = "根据头ID获取行信息", notes = "根据头ID获取行信息 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<DocumentLineDTO<ApplicationLineWebDTO>> getLinesByHeaderId(@PathVariable("id") Long id,
                                                                                     @ApiIgnore Pageable pageable) {
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
    @ApiOperation(value = "申请单关闭查询", notes = "申请单关闭查询 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity listClosedByCondition(@ApiParam(value = "文档编号") @RequestParam(value = "documentNumber", required = false) String documentNumber,
                                                @ApiParam(value = "账套ID")@RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
                                                @ApiParam(value = "部门ID")@RequestParam(value = "unitId", required = false) Long unitId,
                                                @ApiParam(value = "类型ID") @RequestParam(value = "typeId", required = false) Long typeId,
                                                @ApiParam(value = "日期从") @RequestParam(value = "dateFrom", required = false) String dateFrom,
                                                @ApiParam(value = "日期到") @RequestParam(value = "dateTo", required = false) String dateTo,
                                                @ApiParam(value = "金额从") @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
                                                @ApiParam(value = "金额到") @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
                                                @ApiParam(value = "关闭标识") @RequestParam(value = "closedFlag", required = false) ClosedTypeEnum closedFlag,
                                                @ApiParam(value = "币种") @RequestParam(value = "currencyCode", required = false) String currencyCode,
                                                @ApiParam(value = "备注") @RequestParam(value = "remarks", required = false) String remarks,
                                                @ApiParam(value = "公司ID") @RequestParam(value = "companyId", required = false) List<Long> companyId,
                                                @ApiParam(value = "员工ID") @RequestParam(value = "employeeId", required = false) Long employeeId,
                                                @ApiIgnore Pageable pageable) {

        ZonedDateTime requisitionDateFrom = DateUtil.stringToZonedDateTime(dateFrom);
        ZonedDateTime requisitionDateTo = DateUtil.stringToZonedDateTime(dateTo);
        if (requisitionDateTo != null) {
            requisitionDateTo = requisitionDateTo.plusDays(1);
        }
        Page page = PageUtil.getPage(pageable);
        // 获取查询条件SQL
        Wrapper<ApplicationHeader> wrapper = service.getClosedQueryWrapper(documentNumber,setOfBooksId,unitId, typeId, requisitionDateFrom,
                requisitionDateTo, amountFrom, amountTo, closedFlag, currencyCode, remarks, employeeId, companyId, false);
        //查询
        List<ApplicationHeaderWebDTO> result = service.listClosedByCondition(page, wrapper);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

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
    @GetMapping("/header/query/closed/condition/enable/dataAuth")
    @ApiOperation(value = "申请单关闭查询", notes = "申请单关闭查询 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity listClosedByConditionEnableDataAuth(@ApiParam(value = "文档编号") @RequestParam(value = "documentNumber", required = false) String documentNumber,
                                                @ApiParam(value = "账套ID")@RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
                                                @ApiParam(value = "部门ID")@RequestParam(value = "unitId", required = false) Long unitId,
                                                @ApiParam(value = "类型ID") @RequestParam(value = "typeId", required = false) Long typeId,
                                                @ApiParam(value = "日期从")  @RequestParam(value = "dateFrom", required = false) String dateFrom,
                                                @ApiParam(value = "日期到")  @RequestParam(value = "dateTo", required = false) String dateTo,
                                                @ApiParam(value = "金额从")  @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
                                                @ApiParam(value = "金额到")  @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
                                                @ApiParam(value = "关闭标识") @RequestParam(value = "closedFlag", required = false) ClosedTypeEnum closedFlag,
                                                @ApiParam(value = "币种")  @RequestParam(value = "currencyCode", required = false) String currencyCode,
                                                @ApiParam(value = "备注")  @RequestParam(value = "remarks", required = false) String remarks,
                                                @ApiParam(value = "公司ID") @RequestParam(value = "companyId", required = false) List<Long> companyId,
                                                @ApiParam(value = "员工ID") @RequestParam(value = "employeeId", required = false) Long employeeId,
                                                              @ApiIgnore Pageable pageable) {

        ZonedDateTime requisitionDateFrom = DateUtil.stringToZonedDateTime(dateFrom);
        ZonedDateTime requisitionDateTo = DateUtil.stringToZonedDateTime(dateTo);
        if (requisitionDateTo != null) {
            requisitionDateTo = requisitionDateTo.plusDays(1);
        }
        Page page = PageUtil.getPage(pageable);
        // 获取查询条件SQL
        Wrapper<ApplicationHeader> wrapper = service.getClosedQueryWrapper(documentNumber, typeId,setOfBooksId,unitId, requisitionDateFrom,
                requisitionDateTo, amountFrom, amountTo, closedFlag, currencyCode, remarks, employeeId, companyId, true);
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
    @ApiOperation(value = "导出", notes = "导出 开发:bin.xie")
    public void export(@ApiParam(value = "文档编号") @RequestParam(value = "documentNumber", required = false) String documentNumber,
                       @ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
                       @ApiParam(value = "类型ID") @RequestParam(value = "typeId", required = false) Long typeId,
                       @ApiParam(value = "日期从") @RequestParam(value = "dateFrom", required = false) String dateFrom,
                       @ApiParam(value = "日期到") @RequestParam(value = "dateTo", required = false) String dateTo,
                       @ApiParam(value = "金额从") @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
                       @ApiParam(value = "金额到") @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
                       @ApiParam(value = "关闭标识") @RequestParam(value = "closedFlag", required = false) ClosedTypeEnum closedFlag,
                       @ApiParam(value = "币种") @RequestParam(value = "currencyCode", required = false) String currencyCode,
                       @ApiParam(value = "备注") @RequestParam(value = "remarks", required = false) String remarks,
                       @ApiParam(value = "公司ID") @RequestParam(value = "companyId", required = false) List<Long> companyId,
                       @ApiParam(value = "员工ID") @RequestParam(value = "employeeId", required = false) Long employeeId,
                       @ApiParam(value = "导出配置") @RequestBody ExportConfig exportConfig,
                       HttpServletResponse response,
                       HttpServletRequest request) throws IOException {
        ZonedDateTime requisitionDateFrom = DateUtil.stringToZonedDateTime(dateFrom);
        ZonedDateTime requisitionDateTo = DateUtil.stringToZonedDateTime(dateTo);
        if (requisitionDateTo != null) {
            requisitionDateTo = requisitionDateTo.plusDays(1);
        }
        service.exportClosedExcel(documentNumber, setOfBooksId,typeId, requisitionDateFrom, requisitionDateTo, amountFrom, amountTo,
                closedFlag, currencyCode, remarks, employeeId, companyId, response, request, exportConfig);

    }

    /**
     * 申请单关闭行查询
     * @param id
     * @param pageable
     * @return
     */
    @GetMapping("/line/close/query/{id}")
    @ApiOperation(value = "申请单关闭行查询", notes = "申请单关闭行查询 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<DocumentLineDTO<ApplicationLineWebDTO>> getCloseLinesByHeaderId(@PathVariable("id") Long id,
                                                                                          @ApiIgnore Pageable pageable) {
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
    @ApiOperation(value = "关闭申请单", notes = "关闭申请单 开发:bin.xie")
    public ResponseEntity closedHeader(@ApiParam(value = "关闭DTO") @RequestBody ClosedDTO closedDTO) {
        return ResponseEntity.ok(service.closedHeader(closedDTO));
    }

    @PostMapping("/line/closed/{id}")
    @ApiOperation(value = "关闭申请单", notes = "关闭申请单 开发:bin.xie")
    public ResponseEntity closedLine(@ApiParam(value = "信息") @RequestParam String message,
                                     @PathVariable("id") Long id,
                                     @ApiParam(value = "头id") @RequestParam("headerId") Long headerId) {
        return ResponseEntity.ok(service.closedLine(id, message, headerId));
    }

    /**
     * @api {GET} /api/expense/application/header/submit/check/policy 校验费用政策
     * @apiParam (请求参数) {Long} id 申请单头Id
     */
    @GetMapping("/header/submit/check/policy")
    @ApiOperation(value = "校验费用政策", notes = "校验费用政策 开发:bin.xie")
    public ResponseEntity checkPolicy(@ApiParam(value = "申请单头Id") @RequestParam("id") Long id) {

        return ResponseEntity.ok(service.checkPolicy(id));
    }


    /**
     * @api {GET} /api/expense/application/header/query/created 查询已创建申请单的申请人
     */
    @GetMapping("/header/query/created")
    @ApiOperation(value = "查询已创建申请单的申请人", notes = "查询已创建申请单的申请人 开发:bin.xie")
    public List<ContactCO> listUsersByCreatedApplications() {

        return service.listUsersByCreatedApplications();
    }

    /**
     * @Api {get} 费用申请单财务查询
     */
    @GetMapping("/header/query/financial")
    @ApiOperation(value = "费用申请单财务查询", notes = "费用申请单财务查询 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity listByFinancial(@ApiParam(value = "公司ID") @RequestParam(value = "companyId", required = false) Long companyId,
                                          @ApiParam(value = "类型ID") @RequestParam(value = "typeId", required = false) Long typeId,
                                          @ApiParam(value = "员工ID") @RequestParam(value = "employeeId", required = false) Long employeeId,
                                          @ApiParam(value = "状态") @RequestParam(value = "status", required = false) Integer status,
                                          @ApiParam(value = "部门ID") @RequestParam(value = "departmentId", required = false) Long departmentId,
                                          @ApiParam(value = "日期从") @RequestParam(value = "dateFrom", required = false) String dateFrom,
                                          @ApiParam(value = "日期到") @RequestParam(value = "dateTo", required = false) String dateTo,
                                          @ApiParam(value = "币种") @RequestParam(value = "currencyCode", required = false) String currencyCode,
                                          @ApiParam(value = "金额从") @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
                                          @ApiParam(value = "金额到") @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
                                          @ApiParam(value = "报告数量从") @RequestParam(value = "reportAmountFrom", required = false) BigDecimal reportAmountFrom,
                                          @ApiParam(value = "报告数量到") @RequestParam(value = "reportAmountTo", required = false) BigDecimal reportAmountTo,
                                          @ApiParam(value = "报告可用数量从") @RequestParam(value = "reportAbleAmountFrom", required = false) BigDecimal reportAbleAmountFrom,
                                          @ApiParam(value = "报告可用数量到") @RequestParam(value = "reportAbleAmountTo", required = false) BigDecimal reportAbleAmountTo,
                                          @ApiParam(value = "关闭标识") @RequestParam(value = "closedFlag", required = false) ClosedTypeEnum closedFlag,
                                          @ApiParam(value = "备注") @RequestParam(value = "remarks", required = false) String remarks,
                                          @ApiIgnore Pageable pageable) {
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
    @ApiOperation(value = "预付款关联申请单", notes = "预付款关联申请单 开发:bin.xie")
    public ResponseEntity listInfoByCondition(
            @ApiParam(value = "预付款类型ID") @RequestParam Long prepaymentTypeId,
            @ApiParam(value = "公司ID") @RequestParam Long companyId,
            @ApiParam(value = "部门ID") @RequestParam Long unitId,
            @ApiParam(value = "申请人ID") @RequestParam Long applicantId,
            @ApiParam(value = "币种") @RequestParam String currencyCode,
            @ApiParam(value = "申请编号") @RequestParam(required = false) String applicationNumber,
            @ApiParam(value = "申请类型") @RequestParam(required = false) String applicationType,
            @ApiParam(value = "页数") @RequestParam(value = "page",defaultValue = "0") int page,
            @ApiParam(value = "每页大小") @RequestParam(value = "size",defaultValue = "10") int size
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
    @ApiOperation(value = "关联合同", notes = "关联合同 开发:bin.xie")
    public ResponseEntity getContractById(@ApiParam(value = "合同头ID") @RequestParam(value = "contractHeaderId") Long contractHeaderId,
                                          @ApiParam(value = "页数") @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                          @ApiParam(value = "每页大小") @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        Page myPage = PageUtil.getPage(page,size);
        List<ApplicationHeaderWebDTO> result = service.getContractById(contractHeaderId, myPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(myPage);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }


    @GetMapping("/release")
    @ApiOperation(value = "【报账单】关联申请单", notes = "【报账单】关联申请单 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ApplicationHeaderAbbreviateDTO>> selectApplicationAndApportionment(@ApiParam(value = "费用类型ID") @RequestParam Long expenseTypeId,
                                                                                                  @ApiParam(value = "币种") @RequestParam String currencyCode,
                                                                                                  @ApiParam(value = "报账单头ID") @RequestParam Long expReportHeaderId,
                                                                                                  @ApiParam(value = "申请单编号") @RequestParam(required = false) String documentNumber,
                                                                                                  @ApiIgnore Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        List<ApplicationHeaderAbbreviateDTO> applicationHeaderAbbreviateDTOS =
                service.selectApplicationAndApportionment(expenseTypeId, currencyCode, expReportHeaderId, documentNumber, page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity(applicationHeaderAbbreviateDTOS,totalHeader,HttpStatus.OK);
    }

    @RequestMapping("get/release/by/reportId")
    @ApiOperation(value = "根据报告查询发布", notes = "根据报告查询发布 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ApplicationHeaderWebDTO>>  queryReleaseByReport(@ApiParam(value = "报告编号") @RequestParam(value = "businessCode") String reportNumber,
                                                                               @ApiParam(value = "表格名称") @RequestParam (required = false)String formName,
                                                                               @ApiParam(value = "发布编码") @RequestParam(required = false)String releaseCode,
                                                                               @ApiParam(value = "费用类型名称") @RequestParam(required = false)String expenseTypeName,
                                                                               @ApiIgnore Pageable pageable){

        Page page = PageUtil.getPage(pageable);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        List<ApplicationHeaderWebDTO> applicationHeaderWebDTOS = service.queryReleaseByReport(reportNumber,releaseCode,expenseTypeName,page);
        return new ResponseEntity(applicationHeaderWebDTOS,totalHeader,HttpStatus.OK);
    }
}
