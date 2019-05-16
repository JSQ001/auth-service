package com.hand.hcf.app.expense.invoice.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.service.ExcelExportService;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.invoice.domain.InvoiceExpenseTypeRules;
import com.hand.hcf.app.expense.invoice.domain.enums.InvoiceExpenseTypeRulesImportCode;
import com.hand.hcf.app.expense.invoice.service.InvoiceExpenseTypeRulesService;
import com.hand.hcf.app.expense.invoice.service.InvoiceExpenseTypeRulesTempService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.itextpdf.text.io.StreamUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @description:
 * @version: 1.0
 * @author: shuqiang.luo@hand-china.com
 * @date: 2019/4/12
 * @apiDefine InvoiceExpenseTypeRulesService 发票费用映射规则
 */
@RestController
@RequestMapping("/api/invoice/head")
public class InvoiceExpenseTypeRulesController {

    @Autowired
    private InvoiceExpenseTypeRulesService invoiceExpenseTypeRulesService;

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private InvoiceExpenseTypeRulesTempService invoiceExpenseTypeRulesTempService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "新建发票费用映射规则表", notes = "新建发票费用映射规则表 开发:罗书强")
    public ResponseEntity<InvoiceExpenseTypeRules> insertInvoiceExpenseRules(@ApiParam(value = "新建数据") @RequestBody InvoiceExpenseTypeRules invoiceexpensetyperules) {
        return ResponseEntity.ok(invoiceExpenseTypeRulesService.insertInvoiceExpenseRules(invoiceexpensetyperules));
    }

    @GetMapping("/select/by/id")
    @ApiOperation(value = "发票费用类型映射规则-修改", notes = "发票费用类型映射规则-修改 开发:罗书强")
    public ResponseEntity selectInvoiceExpenseRules(
            //@ApiParam(value = "租户id") @RequestParam(value = "tenant_id", required = false) String tenantId,
            @ApiParam(value = "账套id") @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @ApiParam(value = "货物名称") @RequestParam(value = "goodsName", required = false) String goodsName,
            @ApiParam(value = "费用类型代码") @RequestParam(value = "ExpenseTypeCode", required = false) String ExpenseTypeCode,
            @ApiParam(value = "费用类型名称") @RequestParam(value = "ExpenseTypeName", required = false) String ExpenseTypeName,
            @ApiParam(value = "状态") @RequestParam(value = "enabled", required = false) Boolean enabled,
            @ApiParam(value = "开始时间从")@RequestParam(value = "startDate", required = false) String startDate,
            @ApiParam(value = "结束时间至") @RequestParam(value = "endDate", required = false) String endDate,
            @ApiParam(value = "页码") @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @ApiParam(value = "页数")@RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Page myPage = PageUtil.getPage(page, size);
        List<InvoiceExpenseTypeRules> result = invoiceExpenseTypeRulesService.selectInvoiceExpenseRules(myPage ,
                setOfBooksId, goodsName, ExpenseTypeCode, ExpenseTypeName,
                enabled, startDate, endDate,false);

        HttpHeaders httpHeaders = PageUtil.getTotalHeader(myPage);
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "发票费用类型映射规则-修改", notes = "发票费用类型映射规则-修改 开发:罗书强")
    public InvoiceExpenseTypeRules updateInvoiceExpenseById(@ApiParam(value = "修改数据") @RequestBody InvoiceExpenseTypeRules invoiceExpenseTypeRules) {
        return invoiceExpenseTypeRulesService.updateInvoiceExpebseById(invoiceExpenseTypeRules);
    }


    @PostMapping("/select/export")
    @ApiOperation(value = "发票费用类型映射规则-导出", notes = "发票费用类型映射规则-导出 开发:罗书强")
    public void exportInvoiceExpenseRules(HttpServletRequest request,
                                          @ApiParam(value = "导出配置") @RequestBody ExportConfig exportConfig,
                                          HttpServletResponse response,
                                          @ApiParam(value = "账套id")@RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
                                          Pageable mypage) throws IOException {
        Page page = PageUtil.getPage(mypage);
        int total = TypeConversionUtils.parseInt(page.getTotal());
        int threadNumber = total > 100000 ? 8 : 2;

        excelExportService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<InvoiceExpenseTypeRules, InvoiceExpenseTypeRules>() {
            @Override
            public int getTotal() {
                return total;
            }

            @Override
            public List<InvoiceExpenseTypeRules> queryDataByPage(Page page) {
                List<InvoiceExpenseTypeRules> invoiceHeadPage = invoiceExpenseTypeRulesService.selectInvoiceExpenseRules(page ,setOfBooksId,
                        null, null, null,
                        null, null,
                        null, false);
                return invoiceHeadPage;
            }

            @Override
            public InvoiceExpenseTypeRules toDTO(InvoiceExpenseTypeRules invoiceExpenseTypeRules) {
                invoiceExpenseTypeRules.setStringInvoiceStartDate(DateUtil.ZonedDateTimeToString(invoiceExpenseTypeRules.getStartDate()));
                invoiceExpenseTypeRules.setStringInvoiceEndDate(DateUtil.ZonedDateTimeToString(invoiceExpenseTypeRules.getEndDate()));

                return invoiceExpenseTypeRules;
            }

            @Override
            public Class<InvoiceExpenseTypeRules> getEntityClass() {
                return InvoiceExpenseTypeRules.class;
            }
        }, threadNumber, request, response);
    }

    @GetMapping(value = "/template")
    @ApiOperation(value = "发票费用类型-导入模板下载", notes = "发票费用类型-导入模板下载 开发:罗书强")
    public byte[] exportResponsibilityCenterTemplate() {
        InputStream inputStream = null;
        ByteArrayOutputStream bos = null;

        try {
            inputStream = StreamUtil.getResourceStream(InvoiceExpenseTypeRulesImportCode.IMPORT_TEMPLATE_PATH);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            bos = new ByteArrayOutputStream();
            workbook.write(bos);
            bos.flush();
            workbook.close();

            return bos.toByteArray();
        } catch (Exception e) {
            throw new BizException(RespCode.READ_FILE_FAILED);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                throw new BizException(RespCode.READ_FILE_FAILED);
            }
        }
    }


    @GetMapping("/import/error/export/{transactionId}")
    @ApiOperation(value = "发票费用类型-导入", notes = "发票费用类型-导入 开发:罗书强")
    public ResponseEntity errorExport(@PathVariable("transactionId") String transactionId) throws IOException {
        return ResponseEntity.ok(invoiceExpenseTypeRulesService.exportFailedData(transactionId));
    }

    @PostMapping(value = "/import", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "发票费用类型-导入确定发票费用类型", notes = "发票费用类型-导入确定发票费用类型 开发:罗书强")
    public ResponseEntity<Map<String, UUID>> importresponsibilityCenters(@RequestParam("file") MultipartFile file)
            throws Exception {
        // 获取账套id
        Long setOfBooksId = OrgInformationUtil.getCurrentSetOfBookId();

        try (InputStream in = file.getInputStream()) {
            UUID transactionOid = invoiceExpenseTypeRulesService.importExpenseMappingRules(in, setOfBooksId);
            Map<String, UUID> result = new HashMap<>();
            result.put("transactionOid", transactionOid);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            throw new BizException(RespCode.READ_FILE_FAILED);
        }
    }

    @GetMapping(value = "/import/query/result/{transactionOid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "发票费用类型-导入查询导入结果", notes = "发票费用类型-导入查询导入结果 开发:罗书强")
    public ResponseEntity queryResultInfo(@PathVariable("transactionOid") String transactionOid) throws IOException {
        ImportResultDTO importResultDTO = invoiceExpenseTypeRulesTempService.queryResultInfo(transactionOid);
        return ResponseEntity.ok(importResultDTO);
    }

    @PostMapping(value = "/import/confirm/{transactionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "发票费用类型-导入 确定导入", notes = "发票费用类型-导入 确定导入果 开发:罗书强")
    public ResponseEntity confirmImport(@PathVariable("transactionId") String transactionId) {
        return ResponseEntity.ok(invoiceExpenseTypeRulesTempService.confirmImport(transactionId));
    }

    @DeleteMapping(value = "/import/delete/{transactionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "发票费用类型-导入 取消导入", notes = "发票费用类型-导入 取消导入 开发:罗书强")
    public ResponseEntity deleteImportData(@PathVariable("transactionId") String transactionId) {
        return ResponseEntity.ok(invoiceExpenseTypeRulesService.deleteImportData(transactionId));
    }

}

