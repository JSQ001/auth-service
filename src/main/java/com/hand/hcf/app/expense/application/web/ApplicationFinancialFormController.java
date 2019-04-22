package com.hand.hcf.app.expense.application.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.application.service.ApplicationHeaderService;
import com.hand.hcf.app.expense.application.web.dto.ApplicationFinancRequsetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity getApplicationFDList(@RequestParam(value = "companyId", required =  false)Long companyId,
                                     @RequestParam(value = "typeId",required = false)Long typeId,
                                     @RequestParam(value = "applyId",required = false)Long applyId,
                                     @RequestParam(value = "status",required = false)Long status,
                                     @RequestParam(value = "unitId",required = false)Long unitId,
                                     @RequestParam(value = "applyDateFrom",required = false)String applyDateFrom,
                                     @RequestParam(value = "applyDateTo",required = false) String applyDateTo,
                                     @RequestParam (value = "currencyCode",required = false)String currencyCode,
                                     @RequestParam(value = "amountFrom",required = false)BigDecimal amountFrom,
                                     @RequestParam(value = "amountTo",required = false)BigDecimal amountTo,
                                     @RequestParam(value = "associatedAmountFrom",required = false)BigDecimal associatedAmountFrom,
                                     @RequestParam(value = "associatedAmountTo",required = false)BigDecimal associatedAmountTo,
                                     @RequestParam(value = "relevanceAmountFrom",required = false)BigDecimal relevanceAmountFrom,
                                     @RequestParam(value = "relevanceAmountTo",required = false)BigDecimal relevanceAmountTo,
                                     @RequestParam(value = "closedFlag",required = false)Long closedFlag,
                                     @RequestParam(value = "remark",required = false)String  remark,
                                     @RequestParam(value = "documentNumber",required = false) String documentNumber,
                                     @RequestParam(value = "tenantId",required = false)Long tenantId,
                                     Pageable pageable) {
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
    public ResponseEntity getApplicationFDListDataAuth(@RequestParam(value = "companyId", required =  false)Long companyId,
                                                       @RequestParam(value = "typeId",required = false)Long typeId,
                                                       @RequestParam(value = "applyId",required = false)Long applyId,
                                                       @RequestParam(value = "status",required = false)Long status,
                                                       @RequestParam(value = "unitId",required = false)Long unitId,
                                                       @RequestParam(value = "applyDateFrom",required = false)String applyDateFrom,
                                                       @RequestParam(value = "applyDateTo",required = false) String applyDateTo,
                                                       @RequestParam (value = "currencyCode",required = false)String currencyCode,
                                                       @RequestParam(value = "amountFrom",required = false)BigDecimal amountFrom,
                                                       @RequestParam(value = "amountTo",required = false)BigDecimal amountTo,
                                                       @RequestParam(value = "associatedAmountFrom",required = false)BigDecimal associatedAmountFrom,
                                                       @RequestParam(value = "associatedAmountTo",required = false)BigDecimal associatedAmountTo,
                                                       @RequestParam(value = "relevanceAmountFrom",required = false)BigDecimal relevanceAmountFrom,
                                                       @RequestParam(value = "relevanceAmountTo",required = false)BigDecimal relevanceAmountTo,
                                                       @RequestParam(value = "closedFlag",required = false)Long closedFlag,
                                                       @RequestParam(value = "remark",required = false)String  remark,
                                                       @RequestParam(value = "documentNumber",required = false) String documentNumber,
                                                       @RequestParam(value = "tenantId",required = false)Long tenantId,
                                                       Pageable pageable) {
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
    public void export(@RequestParam(value = "companyId", required =  false)Long companyId,
                       @RequestParam(value = "typeId",required = false)Long typeId,
                       @RequestParam(value = "applyId",required = false)Long applyId,
                       @RequestParam(value = "status",required = false)Long status,
                       @RequestParam(value = "unitId",required = false)Long unitId,
                       @RequestParam(value = "applyDateFrom",required = false)String applyDateFrom,
                       @RequestParam(value = "applyDateTo",required = false) String applyDateTo,
                       @RequestParam (value = "currencyCode",required = false)String currencyCode,
                       @RequestParam(value = "amountFrom",required = false)BigDecimal amountFrom,
                       @RequestParam(value = "amountTo",required = false)BigDecimal amountTo,
                       @RequestParam(value = "associatedAmountFrom",required = false)BigDecimal associatedAmountFrom,
                       @RequestParam(value = "associatedAmountTo",required = false)BigDecimal associatedAmountTo,
                       @RequestParam(value = "relevanceAmountFrom",required = false)BigDecimal relevanceAmountFrom,
                       @RequestParam(value = "relevanceAmountTo",required = false)BigDecimal relevanceAmountTo,
                       @RequestParam(value = "closed_flag",required = false)Integer closed_flag,
                       @RequestParam(value = "remark",required = false)String  remark,
                       @RequestParam(value = "documentNumber",required = false) String documentNumber,
                       @RequestParam(value = "tenantId",required = false)Long tenantId,
                       Pageable pageable,
                       @RequestBody ExportConfig exportConfig,
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
