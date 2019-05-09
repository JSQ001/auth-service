package com.hand.hcf.app.expense.application.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.application.service.ApplicationHeaderService;
import com.hand.hcf.app.expense.application.web.dto.ApplicationFinancRequsetDTO;
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

/**
 * <p>
 *  费用申请单财务查询
 * </p>
 * ApplicationFDformController
 *
 * @author hao.yi
 * @date 2019/3/6
 */
@Api(tags = "申请头服务")
@RestController
@RequestMapping("/api/expense/application/form")
public class ApplicationFinancialFormController {

    @Autowired
    private ApplicationHeaderService applicationHeaderService;


    /**
     *  费用申请单财务条件查询
     * @param companyId
     * @param typeId
     * @param applyId
     * @param status
     * @param unitId
     * @param applyDateFrom
     * @param applyDateTo
     * @param currencyCode
     * @param amountFrom
     * @param amountTo
     * @param associatedAmountFrom
     * @param associatedAmountTo
     * @param relevanceAmountFrom
     * @param relevanceAmountTo
     * @param closedFlag
     * @param remark
     * @param pageable
     * @return
     */
    @GetMapping("/query/applicationFinancaiaList")
    @ApiOperation(value = "费用申请单财务条件查询", notes = "费用申请单财务条件查询 开发:hao.yi")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity getApplicationFDList(@ApiParam(value = "公司ID") @RequestParam(value = "companyId", required =  false)Long companyId,
                                     @ApiParam(value = "类型ID") @RequestParam(value = "typeId",required = false)Long typeId,
                                     @ApiParam(value = "申请ID") @RequestParam(value = "applyId",required = false)Long applyId,
                                     @ApiParam(value = "状态") @RequestParam(value = "status",required = false)Long status,
                                     @ApiParam(value = "部门ID") @RequestParam(value = "unitId",required = false)Long unitId,
                                     @ApiParam(value = "申请日期从") @RequestParam(value = "applyDateFrom",required = false)String applyDateFrom,
                                     @ApiParam(value = "申请日期到") @RequestParam(value = "applyDateTo",required = false) String applyDateTo,
                                     @ApiParam(value = "币种") @RequestParam (value = "currencyCode",required = false)String currencyCode,
                                     @ApiParam(value = "数量从") @RequestParam(value = "amountFrom",required = false)BigDecimal amountFrom,
                                     @ApiParam(value = "数量到") @RequestParam(value = "amountTo",required = false)BigDecimal amountTo,
                                     @ApiParam(value = "被关联总计从") @RequestParam(value = "associatedAmountFrom",required = false)BigDecimal associatedAmountFrom,
                                     @ApiParam(value = "被关联总计到") @RequestParam(value = "associatedAmountTo",required = false)BigDecimal associatedAmountTo,
                                     @ApiParam(value = "关联总计从") @RequestParam(value = "relevanceAmountFrom",required = false)BigDecimal relevanceAmountFrom,
                                     @ApiParam(value = "关联总计到") @RequestParam(value = "relevanceAmountTo",required = false)BigDecimal relevanceAmountTo,
                                     @ApiParam(value = "关闭标识") @RequestParam(value = "closedFlag",required = false)Long closedFlag,
                                     @ApiParam(value = "备注") @RequestParam(value = "remark",required = false)String  remark,
                                     @ApiParam(value = "文档编号") @RequestParam(value = "documentNumber",required = false) String documentNumber,
                                     @ApiParam(value = "租户ID") @RequestParam(value = "tenantId",required = false)Long tenantId,
                                               @ApiIgnore Pageable pageable) {
        ZonedDateTime requisitionDateFrom = DateUtil.stringToZonedDateTime(applyDateFrom);
        ZonedDateTime requisitionDateTo = DateUtil.stringToZonedDateTime(applyDateTo);
        if (requisitionDateTo != null){
            requisitionDateTo = requisitionDateTo.plusDays(1);
        }
        Page page = PageUtil.getPage(pageable);
        //用对象接受前端传过来的 查询条件，
        List<ApplicationFinancRequsetDTO> result = applicationHeaderService.listHeaderDTOsByfincancies(page,
                documentNumber,
                companyId,
                typeId,
                requisitionDateFrom,
                requisitionDateTo,
                amountFrom,
                amountTo,
                status,
                currencyCode,
                remark,
                applyId,
                unitId,
                closedFlag,
                associatedAmountFrom,
                associatedAmountTo,
                relevanceAmountFrom,
                relevanceAmountTo,
                tenantId,
                false);

        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return  new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    /**
     *  费用申请单财务条件查询 (数据权限控制)
     * @param companyId
     * @param typeId
     * @param applyId
     * @param status
     * @param unitId
     * @param applyDateFrom
     * @param applyDateTo
     * @param currencyCode
     * @param amountFrom
     * @param amountTo
     * @param associatedAmountFrom
     * @param associatedAmountTo
     * @param relevanceAmountFrom
     * @param relevanceAmountTo
     * @param closedFlag
     * @param remark
     * @param pageable
     * @return
     */
    @GetMapping("/query/applicationFinancaiaList/enable/dataAuth")
    @ApiOperation(value = "费用申请单财务条件查询 (数据权限控制)", notes = "费用申请单财务条件查询 (数据权限控制) 开发:hao.yi")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity getApplicationFDListDataAuth(@ApiParam(value = "公司ID") @RequestParam(value = "companyId", required =  false)Long companyId,
                                                       @ApiParam(value = "类型ID") @RequestParam(value = "typeId",required = false)Long typeId,
                                                       @ApiParam(value = "申请ID")  @RequestParam(value = "applyId",required = false)Long applyId,
                                                       @ApiParam(value = "状态")  @RequestParam(value = "status",required = false)Long status,
                                                       @ApiParam(value = "部门ID")  @RequestParam(value = "unitId",required = false)Long unitId,
                                                       @ApiParam(value = "申请日期从")  @RequestParam(value = "applyDateFrom",required = false)String applyDateFrom,
                                                       @ApiParam(value = "申请日期到")  @RequestParam(value = "applyDateTo",required = false) String applyDateTo,
                                                       @ApiParam(value = "币种")  @RequestParam (value = "currencyCode",required = false)String currencyCode,
                                                       @ApiParam(value = "金额从") @RequestParam(value = "amountFrom",required = false)BigDecimal amountFrom,
                                                       @ApiParam(value = "金额到")  @RequestParam(value = "amountTo",required = false)BigDecimal amountTo,
                                                       @ApiParam(value = "被关联总计从") @RequestParam(value = "associatedAmountFrom",required = false)BigDecimal associatedAmountFrom,
                                                       @ApiParam(value = "被关联总计到") @RequestParam(value = "associatedAmountTo",required = false)BigDecimal associatedAmountTo,
                                                       @ApiParam(value = "关联总计从") @RequestParam(value = "relevanceAmountFrom",required = false)BigDecimal relevanceAmountFrom,
                                                       @ApiParam(value = "关联总计到") @RequestParam(value = "relevanceAmountTo",required = false)BigDecimal relevanceAmountTo,
                                                       @ApiParam(value = "关闭标识") @RequestParam(value = "closedFlag",required = false)Long closedFlag,
                                                       @ApiParam(value = "备注")  @RequestParam(value = "remark",required = false)String  remark,
                                                       @ApiParam(value = "文档编号") @RequestParam(value = "documentNumber",required = false) String documentNumber,
                                                       @ApiParam(value = "租户ID") @RequestParam(value = "tenantId",required = false)Long tenantId,
                                                       @ApiIgnore Pageable pageable) {
        ZonedDateTime requisitionDateFrom = DateUtil.stringToZonedDateTime(applyDateFrom);
        ZonedDateTime requisitionDateTo = DateUtil.stringToZonedDateTime(applyDateTo);
        if (requisitionDateTo != null){
            requisitionDateTo = requisitionDateTo.plusDays(1);
        }
        Page page = PageUtil.getPage(pageable);
        //用对象接受前端传过来的 查询条件，
        List<ApplicationFinancRequsetDTO> result = applicationHeaderService.listHeaderDTOsByfincancies(page,
                documentNumber,
                companyId,
                typeId,
                requisitionDateFrom,
                requisitionDateTo,
                amountFrom,
                amountTo,
                status,
                currencyCode,
                remark,
                applyId,
                unitId,
                closedFlag,
                associatedAmountFrom,
                associatedAmountTo,
                relevanceAmountFrom,
                relevanceAmountTo,
                tenantId,
                true);

        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return  new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    /**
     *  导出
     * @param companyId
     * @param typeId
     * @param applyId
     * @param status
     * @param unitId
     * @param applyDateFrom
     * @param applyDateTo
     * @param currencyCode
     * @param amountFrom
     * @param amountTo
     * @param associatedAmountFrom
     * @param associatedAmountTo
     * @param relevanceAmountFrom
     * @param relevanceAmountTo
     * @param closed_flag
     * @param remark
     * @param documentNumber
     * @param pageable
     * @param exportConfig
     * @param response
     * @param request
     * @throws IOException
     */
    @RequestMapping("/header/applicationFinancaia/export")
    @ApiOperation(value = "导出", notes = "导出 开发:hao.yi")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public void export(@ApiParam(value = "公司ID") @RequestParam(value = "companyId", required =  false)Long companyId,
                       @ApiParam(value = "类型ID")  @RequestParam(value = "typeId",required = false)Long typeId,
                       @ApiParam(value = "申请ID")   @RequestParam(value = "applyId",required = false)Long applyId,
                       @ApiParam(value = "状态")   @RequestParam(value = "status",required = false)Long status,
                       @ApiParam(value = "部门ID")   @RequestParam(value = "unitId",required = false)Long unitId,
                       @ApiParam(value = "申请日期从")  @RequestParam(value = "applyDateFrom",required = false)String applyDateFrom,
                       @ApiParam(value = "申请日期到")  @RequestParam(value = "applyDateTo",required = false) String applyDateTo,
                       @ApiParam(value = "币种")  @RequestParam (value = "currencyCode",required = false)String currencyCode,
                       @ApiParam(value = "金额从") @RequestParam(value = "amountFrom",required = false)BigDecimal amountFrom,
                       @ApiParam(value = "金额到")  @RequestParam(value = "amountTo",required = false)BigDecimal amountTo,
                       @ApiParam(value = "被关联总计从") @RequestParam(value = "associatedAmountFrom",required = false)BigDecimal associatedAmountFrom,
                       @ApiParam(value = "被关联总计到") @RequestParam(value = "associatedAmountTo",required = false)BigDecimal associatedAmountTo,
                       @ApiParam(value = "关联总计从")  @RequestParam(value = "relevanceAmountFrom",required = false)BigDecimal relevanceAmountFrom,
                       @ApiParam(value = "关联总计到")  @RequestParam(value = "relevanceAmountTo",required = false)BigDecimal relevanceAmountTo,
                       @ApiParam(value = "关闭标识") @RequestParam(value = "closed_flag",required = false)Integer closed_flag,
                       @ApiParam(value = "备注") @RequestParam(value = "remark",required = false)String  remark,
                       @ApiParam(value = "文档编号") @RequestParam(value = "documentNumber",required = false) String documentNumber,
                       @ApiParam(value = "租户ID") @RequestParam(value = "tenantId",required = false)Long tenantId,
                       @ApiIgnore Pageable pageable,
                       @ApiParam(value = "导出配置") @RequestBody ExportConfig exportConfig,
                       HttpServletResponse response,
                       HttpServletRequest request) throws IOException {
        ZonedDateTime requisitionDateFrom = DateUtil.stringToZonedDateTime(applyDateFrom);
        ZonedDateTime requisitionDateTo = DateUtil.stringToZonedDateTime(applyDateTo);
        if (requisitionDateTo != null){
            requisitionDateTo = requisitionDateTo.plusDays(1);
        }
        applicationHeaderService.exportFormExcel(documentNumber, typeId, requisitionDateFrom, requisitionDateTo, amountFrom, amountTo,
                closed_flag, currencyCode, remark, applyId, companyId,associatedAmountFrom,associatedAmountTo,relevanceAmountFrom,relevanceAmountTo,tenantId, response, request, exportConfig);
    }
}
