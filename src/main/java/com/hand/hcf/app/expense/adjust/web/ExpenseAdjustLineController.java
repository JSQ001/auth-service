package com.hand.hcf.app.expense.adjust.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.expense.adjust.service.ExpenseAdjustLineService;
import com.hand.hcf.app.expense.adjust.web.dto.ExpenseAdjustLineWebDTO;
import com.hand.hcf.app.expense.adjust.web.dto.ExpenseAdjustLinesBean;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.util.PageUtil;
import com.hand.hcf.core.web.dto.ImportResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/10
 */
@RestController
@RequestMapping("/api/expense/adjust/lines")
public class ExpenseAdjustLineController {
    @Autowired
    private ExpenseAdjustLineService expenseAdjustLineService;

    @PostMapping
    public ResponseEntity<ExpenseAdjustLinesBean> insertExpenseAdjustLines(@RequestBody ExpenseAdjustLinesBean adjustLinesBean,
                                                                           @RequestParam(value = "transactionNumber",required = false) String transactionNumber) throws URISyntaxException {

        return ResponseEntity.ok( expenseAdjustLineService.insertExpenseAdjustLinesList(adjustLinesBean, transactionNumber) );
    }

    @GetMapping(value = "/query/dto/by/header/id")
    public ResponseEntity<List<ExpenseAdjustLineWebDTO>> findExpenseAdjustLinesDTOByHeaderId(@RequestParam Long expAdjustHeaderId, Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseAdjustLineWebDTO> result = expenseAdjustLineService.listExpenseAdjustLinesDTOByHeaderId(expAdjustHeaderId, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + page.getTotal());
        headers.add("Link", "/api/expense/adjust/lines/query/dto/by/header/id");
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLineById(@PathVariable("id") Long id){

        return ResponseEntity.ok(expenseAdjustLineService.deleteLineById(id));
    }


    @GetMapping("/export/template")
    public ResponseEntity exportExpenseAdjustLinesTemplate(@RequestParam Long expenseAdjustHeaderId,@RequestParam(defaultValue = "true",required = false) boolean external){
        return ResponseEntity.ok(expenseAdjustLineService.exportExpenseAdjustLinesTemplate(expenseAdjustHeaderId,external));
    }


    @PostMapping("/import")
    public ResponseEntity importExpenseAdjustLinesTemplateNew(@RequestParam MultipartFile file, @RequestParam Long expenseAdjustHeaderId, @RequestParam(required = false) Long sourceAdjustLineId) throws Exception {
        try(InputStream in = file.getInputStream()) {
            String transactionLogUUID = expenseAdjustLineService.importExpenseAdjustLineTemplate(in, expenseAdjustHeaderId,sourceAdjustLineId);
            return ResponseEntity.ok(transactionLogUUID);
        }catch (IOException e){
            throw new BizException("read File error", "读取文件失败！");
        }
    }

    /**
     * 查询导入临时表数据
     * @param transactionUUID
     * @return
     */
    @GetMapping("/import/log/{transactionUUID}")
    public ResponseEntity queryResultInfo(@PathVariable("transactionUUID") String transactionUUID){
        ImportResultDTO importResulDTO = expenseAdjustLineService.queryImportResultInfo(transactionUUID);
        return ResponseEntity.ok(importResulDTO);
    }

    /**
     * 导出错误信息  导出错误信息excel
     * @param transactionID
     * @throws IOException
     */
    @GetMapping("/import/error/export/{headerId}/{external}/{transactionID}")
    public ResponseEntity errorExport(@PathVariable("headerId") Long headerId,
                                      @PathVariable("external") Boolean external,
                                      @PathVariable("transactionID") String transactionID) throws IOException {
        return ResponseEntity.ok(expenseAdjustLineService.exportFailedData(headerId, transactionID, external));
    }

    /**
     * 删除导入的数据 点击取消时删除当前导入的数据（删除临时表数据)
     * @param transactionID
     * @return
     */
    @DeleteMapping("/import/delete/{transactionID}")
    public ResponseEntity deleteImportData(@PathVariable("transactionID") String transactionID){
        return ResponseEntity.ok(expenseAdjustLineService.deleteImportData(transactionID));
    }

    /**
     *  点击确定时 把临时表数据新增到正式表中
     * @param transactionID
     * @return
     */
    @PostMapping("/import/confirm/{headerId}/{transactionID}")
    public ResponseEntity confirmImport(@PathVariable("transactionID") String transactionID,
                                        @PathVariable("headerId") Long headerId){
        return ResponseEntity.ok(expenseAdjustLineService.confirmImport(transactionID, headerId));
    }

    /**
     * 分摊行导入查询
     * @param transactionNumber
     * @return
     */
    @GetMapping(value = "/query/temp/by/{transactionNumber}")
    public ResponseEntity<List<ExpenseAdjustLineWebDTO>> findExpenseAdjustLinesDTOByTempId(@PathVariable("transactionNumber") String transactionNumber){
        List<ExpenseAdjustLineWebDTO> result = expenseAdjustLineService.listTempResult(transactionNumber);
        return ResponseEntity.ok(result);
    }
}
