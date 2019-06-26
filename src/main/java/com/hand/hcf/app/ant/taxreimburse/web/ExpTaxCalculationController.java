package com.hand.hcf.app.ant.taxreimburse.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxCalculation;
import com.hand.hcf.app.ant.taxreimburse.service.ExpTaxCalculationService;
import com.hand.hcf.app.ant.taxreimburse.service.ExpTaxCalculationTempDomainService;
import com.hand.hcf.app.ant.taxreimburse.utils.TaxReimburseConstans;
import com.hand.hcf.app.base.util.FileUtil;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.mdata.utils.RespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description:
 * @date 2019/6/24 20:35
 */
@RestController
@RequestMapping("/api/exp/tax/calculation")
public class ExpTaxCalculationController {

    @Autowired
    private ExpTaxCalculationService expTaxCalculationService;

    @Autowired
    private ExpTaxCalculationTempDomainService expTaxCalculationTempDomainService;

    /**
     * 详情页面税金明细信息显示
     *
     * @param reimburseHeaderId
     * @param pageable
     * @return
     */
    @GetMapping("/list/by/headId")
    public ResponseEntity<List<ExpTaxCalculation>> getTaxReportDetail(@RequestParam(required = false) String reimburseHeaderId, Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        List<ExpTaxCalculation> expTaxCalculationList = expTaxCalculationService.getTaxCalculationDetailList(reimburseHeaderId, page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(expTaxCalculationList, httpHeaders, HttpStatus.OK);
    }

    /**
     * 下载计提明细数据导入模板-url:/api/exp/tax/calculation/download/template
     *
     * @return
     */
    @GetMapping("/download/template")
    public ResponseEntity<byte[]> exportTaxReportTemplate() {
        byte[] bytes = FileUtil.getFileBinaryForDownload(FileUtil.getTemplatePath(TaxReimburseConstans.TAX_CALCULATION_IMPORT_TEMPLATE_PATH, LoginInformationUtil.getCurrentLanguage()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    /**
     * 使用模板导入数据---导入第一步
     * url:/api/exp/tax/calculation/import/template/data
     *
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/import/template/data")
    public ResponseEntity<Map<String, UUID>> importPublicData(@RequestParam("file") MultipartFile file) throws Exception {
        try {
            UUID transactionOid = expTaxCalculationService.importTaxCalculation(file);
            Map<String, UUID> result = new HashMap<>();
            result.put("transactionOid", transactionOid);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            throw new BizException(RespCode.SYS_READ_FILE_ERROR);
        }
    }

    /**
     * 查询导入结果 导入第二步 查询导入成功多少，失败多少，失败的数据有哪些
     * url:/api/exp/tax/calculation/query/result/Info/{transactionID}
     *
     * @param transactionID
     * @return
     * @throws IOException
     */
    @GetMapping("/query/result/Info/{transactionID}")
    public ResponseEntity queryResultInfo(@PathVariable("transactionID") String transactionID) throws IOException {
        ImportResultDTO importResultDTO = expTaxCalculationTempDomainService.queryResultInfo(transactionID);
        return ResponseEntity.ok(importResultDTO);
    }

    /**
     * 导出错误信息  导出错误信息excel
     * url:/api/exp/tax/calculation/import/export/error/data/{transactionID}
     *
     * @param transactionID
     * @throws IOException
     */
    @GetMapping("/export/error/data/{transactionID}")
    public ResponseEntity exportErrorData(
            @PathVariable("transactionID") String transactionID) throws IOException {
        return ResponseEntity.ok(expTaxCalculationTempDomainService.exportFailedData(transactionID));
    }

    /**
     * 删除导入的数据 点击取消时删除当前导入的数据（删除临时表数据)
     * url:/api/exp/tax/calculation/delete/import/data/{transactionID}
     *
     * @param transactionID
     * @return
     */
    @DeleteMapping("/delete/import/data/{transactionID}")
    public ResponseEntity deleteImportData(@PathVariable("transactionID") String transactionID) {
        return ResponseEntity.ok(expTaxCalculationTempDomainService.deleteImportData(transactionID));
    }

    /**
     * 点击确定时 把临时表数据新增到正式表中
     * url:/api/exp/tax/calculation/confirm/import/{transactionID}
     *
     * @param transactionID
     * @return
     */
    @PostMapping("/confirm/import/{transactionID}")
    public ResponseEntity confirmImport(@PathVariable(value = "transactionID") String transactionID, @RequestParam Long headId) {
        return ResponseEntity.ok(expTaxCalculationTempDomainService.taxCalaulationConfirmImport(transactionID, headId, null));
    }

    /**
     * 批量-报账单删除-只有编辑中的才可删除，并且修改税金/银行数据状态
     * url:/api/exp/tax/calculation/head/batch/delete
     *
     * @param ids
     */
    @DeleteMapping("/head/batch/delete")
    public boolean deleteBatch(@RequestParam String ids) {
        return expTaxCalculationService.deleteTaxCalcuations(ids);
    }


    /**
     * 批量更新计提明细信息 url:api/exp/tax/calculation/update/tax/data (保存功能）
     * @param expTaxCalculationList
     * @return
     */
    @PostMapping("/update/tax/data")
    public ResponseEntity<List<ExpTaxCalculation>>  saveTaxCalculation(@RequestBody List<ExpTaxCalculation> expTaxCalculationList){
        return ResponseEntity.ok(expTaxCalculationService.saveTaxCalculation(expTaxCalculationList));
    }

}
