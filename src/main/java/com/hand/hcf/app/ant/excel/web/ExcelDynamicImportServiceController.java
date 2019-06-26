package com.hand.hcf.app.ant.excel.web;

import com.hand.hcf.app.ant.excel.service.ExcelDynamicImportService;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/6/17
 */
@RestController
@RequestMapping("/api/excel")
public class ExcelDynamicImportServiceController {

    @Autowired
    private ExcelDynamicImportService excelDynamicImportService;

    /**
     * 导出excel模板
     *
     * @param expenseTypeId
     * @return
     */
    @GetMapping("/export/template")
    public ResponseEntity exportExcelTemp(@RequestParam String expenseTypeId) {

        return ResponseEntity.ok(excelDynamicImportService.exportTemp(expenseTypeId));
    }

    /**
     * 导入临时表
     *
     * @param file
     * @param headId
     * @return
     * @throws Exception
     */
    @PostMapping("/excel/import")
    public ResponseEntity importCustomEnumerationItems(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("id") Long headId) throws Exception {
        try (InputStream in = file.getInputStream()) {
            UUID transactionLogUUID = excelDynamicImportService.importexcelTemp(in, headId);
            return ResponseEntity.ok(transactionLogUUID);
        } catch (IOException e) {
            throw new BizException(RespCode.SYS_READ_FILE_ERROR);
        }
    }

    /**
     *
     * @param transactionOid
     * @return
     */
    @GetMapping("/import/query/result/{transactionOid}")
    public ResponseEntity queryResultInfo(@PathVariable("transactionOid") String transactionOid) {
        ImportResultDTO importResultDTO = excelDynamicImportService.queryImportResultInfo(transactionOid);
        return ResponseEntity.ok(importResultDTO);
    }

}
