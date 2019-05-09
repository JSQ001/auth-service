package com.hand.hcf.app.expense.input.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.input.domain.ExpInputTaxHeader;
import com.hand.hcf.app.expense.input.dto.ExpInputTaxHeaderDTO;
import com.hand.hcf.app.expense.input.service.ExpInputTaxHeaderService;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * @description
 * @Version: 1.0
 * @author: ShilinMao
 * @date: 2019/2/28 15:40
 */
@Api(tags = "进项税业务单头控制器")
@RestController
@RequestMapping("/api/input/header")
public class ExpInputTaxHeaderController {

    @Autowired
    private ExpInputTaxHeaderService expInputTaxHeaderService;

    @GetMapping("/query")
    @ApiOperation(value = "获取头", notes = "获取头 开发:ShilinMao")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity getHeaders(
            @ApiParam(value = "申请人ID") @RequestParam(value = "applicantId", required = false) Long applicantId,
            @ApiParam(value = "业务大类") @RequestParam(value = "transferType", required = false) String transferType,
            @ApiParam(value = "用户类型") @RequestParam(value = "useType", required = false) String useType,
            @ApiParam(value = "业务日期从") @RequestParam(value = "transferDateFrom", required = false) String transferDateFrom,
            @ApiParam(value = "业务日期到") @RequestParam(value = "transferDateTo", required = false) String transferDateTo,
            @ApiParam(value = "状态") @RequestParam(value = "status", required = false) String status,
            @ApiParam(value = "金额从") @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
            @ApiParam(value = "金额到") @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
            @ApiParam(value = "描述") @RequestParam(value = "description", required = false) String description,
            @ApiParam(value = "文档编号") @RequestParam(value = "documentNumber", required = false)String documentNumber,
            @ApiParam(value = "公司ID") @RequestParam(value = "companyId", required = false)Long companyId,
            @ApiParam(value = "部门ID") @RequestParam(value = "departmentId", required = false)Long departmentId,
            @ApiIgnore Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        Page result = expInputTaxHeaderService.queryHeader(applicantId, transferType, useType, transferDateFrom,
                transferDateTo, status, amountFrom, amountTo, description,documentNumber,companyId,departmentId, false, page);
        HttpHeaders headers = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    @GetMapping("/query/enable/dataAuth")
    @ApiOperation(value = "获取头", notes = "获取头 开发:ShilinMao")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity getHeadersEnableDataAuth(
            @ApiParam(value = "申请人ID") @RequestParam(value = "applicantId", required = false) Long applicantId,
            @ApiParam(value = "业务大类") @RequestParam(value = "transferType", required = false) String transferType,
            @ApiParam(value = "用户类型") @RequestParam(value = "useType", required = false) String useType,
            @ApiParam(value = "业务日期从")@RequestParam(value = "transferDateFrom", required = false) String transferDateFrom,
            @ApiParam(value = "业务日期到")@RequestParam(value = "transferDateTo", required = false) String transferDateTo,
            @ApiParam(value = "状态") @RequestParam(value = "status", required = false) String status,
            @ApiParam(value = "金额从") @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
            @ApiParam(value = "金额到") @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
            @ApiParam(value = "描述") @RequestParam(value = "description", required = false) String description,
            @ApiParam(value = "文档编号") @RequestParam(value = "documentNumber", required = false)String documentNumber,
            @ApiParam(value = "公司ID") @RequestParam(value = "companyId", required = false)Long companyId,
            @ApiParam(value = "部门ID") @RequestParam(value = "departmentId", required = false)Long departmentId,
            @ApiIgnore Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        Page result = expInputTaxHeaderService.queryHeader(applicantId, transferType, useType, transferDateFrom,
                transferDateTo, status, amountFrom, amountTo, description,documentNumber,companyId,departmentId, true, page);
        HttpHeaders headers = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    @GetMapping("/queryById")
    @ApiOperation(value = "根据ID获取头", notes = "根据ID获取头 开发:ShilinMao")
    public ResponseEntity getHeaderById(@ApiParam(value = "ID") @RequestParam(value = "id") Long id) {
        return ResponseEntity.ok(expInputTaxHeaderService.queryById(id));
    }

    @PostMapping("/insertOrUpdate")
    @ApiOperation(value = "根据ID获取头", notes = "根据ID获取头 开发:ShilinMao")
    public ResponseEntity insertOrUpdateHeader(@ApiParam(value = "进项税业务单头表") @RequestBody ExpInputTaxHeader expInputTaxHeader) {
        return ResponseEntity.ok(expInputTaxHeaderService.insertOrUpdateHeader(expInputTaxHeader));
    }

    @PostMapping("/updateStatus")
    @ApiOperation(value = "更新状态", notes = "更新状态 开发:ShilinMao")
    public ResponseEntity updateStatus(@ApiParam(value = "id") @RequestParam(value = "id") Long id,
                                       @ApiParam(value = "状态") @RequestParam(value = "status")int status,
                                       @ApiParam(value = "许可证") @RequestBody Map<String, String> approvalRemark) {
        String remark = "";
        if(approvalRemark!=null){
            remark =  approvalRemark.get("approvalRemark");
        }
        return ResponseEntity.ok(expInputTaxHeaderService.updateStatus(id,status,remark));
    }

    @GetMapping("/delete")
    @ApiOperation(value = "根据ID删除", notes = "根据ID删除 开发:ShilinMao")
    public ResponseEntity deleteById(@ApiParam(value = "id") @RequestParam(value = "id") Long id) {
        return ResponseEntity.ok(expInputTaxHeaderService.delete(id));
    }

    /**
     *  进项税业务单财务查询
     * @param companyId
     * @param unitId
     * @param applyId
     * @param status
     * @param transferType
     * @param useType
     * @param applyDateFrom
     * @param applyDateTo
     * @param currencyCode
     * @param amountFrom
     * @param amountTo
     * @param reverseFlag
     * @param auditorDateFrom
     * @param auditorDateTo
     * @param remark
     * @return
     */
    @GetMapping("/query/expinput/finance")
    @ApiOperation(value = "进项税业务单财务查询", notes = "进项税业务单财务查询 开发:ShilinMao")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity queryExpInputFinance(@ApiParam(value = "公司id") @RequestParam(value = "companyId",required = false)Long companyId,
                                              @ApiParam(value = "部门id") @RequestParam(value = "unitId",required = false)Long unitId,
                                              @ApiParam(value = "申请人id") @RequestParam(value = "applyId",required = false)Long applyId,
                                              @ApiParam(value = "状态") @RequestParam(value = "status",required = false)Long status,
                                              @ApiParam(value = "业务大类") @RequestParam(value = "transferType",required = false)String transferType,
                                              @ApiParam(value = "用户类型") @RequestParam(value = "useType",required = false)String useType,
                                              @ApiParam(value = "申请日期从") @RequestParam(value = "applyDateFrom",required = false)String applyDateFrom,
                                              @ApiParam(value = "申请日期到") @RequestParam(value = "applyDateTo",required = false)String applyDateTo,
                                              @ApiParam(value = "币种") @RequestParam(value = "currencyCode",required = false)String currencyCode,
                                              @ApiParam(value = "金额从") @RequestParam(value = "amountFrom",required = false)BigDecimal amountFrom,
                                              @ApiParam(value = "金额到") @RequestParam(value = "amountTo",required = false)BigDecimal amountTo,
                                              @ApiParam(value = "相反标志") @RequestParam(value = "reverseFlag",required = false)String reverseFlag,
                                              @ApiParam(value = "审计日期从") @RequestParam(value = "auditorDateFrom",required = false)String auditorDateFrom,
                                              @ApiParam(value = "审计日期到") @RequestParam(value = "auditorDateTo",required = false)String auditorDateTo,
                                              @ApiParam(value = "备注") @RequestParam(value = "remark",required = false)String remark,
                                              @ApiParam(value = "租户id") @RequestParam(value = "tenantId",required = false)Long tenantId,
                                              @ApiParam(value = "文档编号") @RequestParam(value = "documentNumber",required = false) String documentNumber,
                                               @ApiIgnore Pageable pageable){

        ZonedDateTime creatDateFrom = DateUtil.stringToZonedDateTime(applyDateFrom);
        ZonedDateTime creatDateTo = DateUtil.stringToZonedDateTime(applyDateTo);
        ZonedDateTime auditDateFrom = DateUtil.stringToZonedDateTime(auditorDateFrom);
        ZonedDateTime auditDateTo = DateUtil.stringToZonedDateTime(auditorDateTo);
        if (creatDateTo != null){
            creatDateTo = creatDateTo.plusDays(1);
        }
        if (auditDateTo != null){
            auditDateTo = auditDateTo.plusDays(1);
        }
        Page page = PageUtil.getPage(pageable);
        List<ExpInputTaxHeaderDTO> result =expInputTaxHeaderService.queryExpInputFinance(page,companyId,unitId,applyId,status,
                    transferType,useType,currencyCode,amountFrom,amountTo,
                        reverseFlag,remark,creatDateFrom,creatDateTo,auditDateFrom,auditDateTo,tenantId,documentNumber, false);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return  new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    /**
     *  进项税业务单财务查询
     * @param companyId
     * @param unitId
     * @param applyId
     * @param status
     * @param transferType
     * @param useType
     * @param applyDateFrom
     * @param applyDateTo
     * @param currencyCode
     * @param amountFrom
     * @param amountTo
     * @param reverseFlag
     * @param auditorDateFrom
     * @param auditorDateTo
     * @param remark
     * @return
     */
    @GetMapping("/query/expinput/finance/enable/dataAuth")
    @ApiOperation(value = "进项税业务单财务查询", notes = "进项税业务单财务查询 开发:ShilinMao")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity queryExpInputFinanceEnableDataAuth(@ApiParam(value = "公司id") @RequestParam(value = "companyId",required = false)Long companyId,
                                               @ApiParam(value = "部门id") @RequestParam(value = "unitId",required = false)Long unitId,
                                               @ApiParam(value = "申请人id") @RequestParam(value = "applyId",required = false)Long applyId,
                                               @ApiParam(value = "状态") @RequestParam(value = "status",required = false)Long status,
                                               @ApiParam(value = "业务大类")  @RequestParam(value = "transferType",required = false)String transferType,
                                               @ApiParam(value = "用户类型")  @RequestParam(value = "useType",required = false)String useType,
                                               @ApiParam(value = "申请日期从") @RequestParam(value = "applyDateFrom",required = false)String applyDateFrom,
                                               @ApiParam(value = "申请日期到") @RequestParam(value = "applyDateTo",required = false)String applyDateTo,
                                               @ApiParam(value = "币种") @RequestParam(value = "currencyCode",required = false)String currencyCode,
                                               @ApiParam(value = "金额从") @RequestParam(value = "amountFrom",required = false)BigDecimal amountFrom,
                                               @ApiParam(value = "金额到") @RequestParam(value = "amountTo",required = false)BigDecimal amountTo,
                                               @ApiParam(value = "相反标志")  @RequestParam(value = "reverseFlag",required = false)String reverseFlag,
                                               @ApiParam(value = "审计日期从") @RequestParam(value = "auditorDateFrom",required = false)String auditorDateFrom,
                                               @ApiParam(value = "审计日期到") @RequestParam(value = "auditorDateTo",required = false)String auditorDateTo,
                                               @ApiParam(value = "备注") @RequestParam(value = "remark",required = false)String remark,
                                               @ApiParam(value = "租户id")  @RequestParam(value = "tenantId",required = false)Long tenantId,
                                               @ApiParam(value = "文档编号")  @RequestParam(value = "documentNumber",required = false) String documentNumber,
                                                @ApiIgnore Pageable pageable){

        ZonedDateTime creatDateFrom = DateUtil.stringToZonedDateTime(applyDateFrom);
        ZonedDateTime creatDateTo = DateUtil.stringToZonedDateTime(applyDateTo);
        ZonedDateTime auditDateFrom = DateUtil.stringToZonedDateTime(auditorDateFrom);
        ZonedDateTime auditDateTo = DateUtil.stringToZonedDateTime(auditorDateTo);
        if (creatDateTo != null){
            creatDateTo = creatDateTo.plusDays(1);
        }
        if (auditDateTo != null){
            auditDateTo = auditDateTo.plusDays(1);
        }
        Page page = PageUtil.getPage(pageable);
        List<ExpInputTaxHeaderDTO> result =expInputTaxHeaderService.queryExpInputFinance(page,companyId,unitId,applyId,status,
                transferType,useType,currencyCode,amountFrom,amountTo,
                reverseFlag,remark,creatDateFrom,creatDateTo,auditDateFrom,auditDateTo,tenantId,documentNumber, true);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return  new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    /**
     *  进项税财务查询导出
     * @param companyId
     * @param unitId
     * @param applyId
     * @param status
     * @param transferType
     * @param useType
     * @param applyDateFrom
     * @param applyDateTo
     * @param currencyCode
     * @param amountFrom
     * @param amountTo
     * @param reverseFlag
     * @param auditorDateFrom
     * @param auditorDateTo
     * @param remark
     * @param tenantId
     * @param documentNumber
     * @param exportConfig
     * @param response
     * @param request
     * @throws IOException
     */
    @PostMapping("/query/expinput/finance/export")
    @ApiOperation(value = "进项税财务查询导出", notes = "进项税财务查询导出 开发:ShilinMao")
    public void export(@ApiParam(value = "公司id") @RequestParam(value = "companyId",required = false)Long companyId,
                       @ApiParam(value = "部门id") @RequestParam(value = "unitId",required = false)Long unitId,
                       @ApiParam(value = "申请人id")@RequestParam(value = "applyId",required = false)Long applyId,
                       @ApiParam(value = "状态") @RequestParam(value = "status",required = false)Long status,
                       @ApiParam(value = "业务大类") @RequestParam(value = "transferType",required = false)String transferType,
                       @ApiParam(value = "用户类型") @RequestParam(value = "useType",required = false)String useType,
                       @ApiParam(value = "申请日期从")@RequestParam(value = "applyDateFrom",required = false)String applyDateFrom,
                       @ApiParam(value = "申请日期到")@RequestParam(value = "applyDateTo",required = false)String applyDateTo,
                       @ApiParam(value = "币种") @RequestParam(value = "currencyCode",required = false)String currencyCode,
                       @ApiParam(value = "金额从") @RequestParam(value = "amountFrom",required = false)BigDecimal amountFrom,
                       @ApiParam(value = "金额到") @RequestParam(value = "amountTo",required = false)BigDecimal amountTo,
                       @ApiParam(value = "相反标志") @RequestParam(value = "reverseFlag",required = false)String reverseFlag,
                       @ApiParam(value = "审计日期从")@RequestParam(value = "auditorDateFrom",required = false)String auditorDateFrom,
                       @ApiParam(value = "审计日期到")@RequestParam(value = "auditorDateTo",required = false)String auditorDateTo,
                       @ApiParam(value = "备注") @RequestParam(value = "remark",required = false)String remark,
                       @ApiParam(value = "租户id") @RequestParam(value = "tenantId",required = false)Long tenantId,
                       @ApiParam(value = "文档编号") @RequestParam(value = "documentNumber",required = false) String documentNumber,
                       @ApiParam(value = "导出配置") @RequestBody ExportConfig exportConfig,
                       HttpServletResponse response,
                       HttpServletRequest request) throws IOException {
        ZonedDateTime creatDateFrom = DateUtil.stringToZonedDateTime(applyDateFrom);
        ZonedDateTime creatDateTo = DateUtil.stringToZonedDateTime(applyDateTo);
        ZonedDateTime auditDateFrom = DateUtil.stringToZonedDateTime(auditorDateFrom);
        ZonedDateTime auditDateTo = DateUtil.stringToZonedDateTime(auditorDateTo);
        if (creatDateTo != null){
            creatDateTo = creatDateTo.plusDays(1);
        }
        if (auditDateTo != null){
            auditDateTo = auditDateTo.plusDays(1);
        }
        expInputTaxHeaderService.exportFormExcel(companyId,unitId,applyId,status,transferType,useType,currencyCode,
                amountFrom,amountTo, reverseFlag,remark,creatDateFrom,creatDateTo,auditDateFrom, auditDateTo, tenantId,
                documentNumber,response, request, exportConfig);
    }


    @PostMapping(value = "/create/accounting")
    @ApiOperation(value = "创建凭证", notes = "创建凭证 开发:ShilinMao")
    public ResponseEntity saveInitializeExpInputTaxGeneralLedgerJournalLine(@ApiParam(value = "进项税单头ID") @RequestParam("inputTaxHeaderId") Long inputTaxHeaderId,
                                                                            @ApiParam(value = "财务日期") @RequestParam("accountingDate") String accountingDate){
        String reuslt = expInputTaxHeaderService.saveInitializeExpInputTaxGeneralLedgerJournalLine(inputTaxHeaderId,accountingDate);
        return ResponseEntity.ok(reuslt);
    }
}
