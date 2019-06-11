package com.hand.hcf.app.ant.attachment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.attachment.domain.AttachmentType;
import com.hand.hcf.app.ant.attachment.domain.ExpReportTypeAttchment;
import com.hand.hcf.app.ant.attachment.dto.DocumentType;
import com.hand.hcf.app.ant.attachment.service.ExpReportTypeAttchmentService;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualType;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 单据类型附件权限设置controller
 * @date 2019/5/16 15:40
 */


@RestController
@RequestMapping("/api/report/type/attachment")
public class ExpReportTypeAttchController {

    @Autowired
    private ExpReportTypeAttchmentService expReportTypeAttchmentService;



    /**
     * 新增 单据类型--单条头信息
     *
     * @param expReportTypeAttchment
     * @return
     */
    @PostMapping("/add/doctype")
    public ResponseEntity<ExpReportTypeAttchment> createNewSetting(@RequestBody ExpReportTypeAttchment expReportTypeAttchment) {
        return ResponseEntity.ok(expReportTypeAttchmentService.createAttachmentSetting(expReportTypeAttchment));
    }

    /**
     * 新增附件类型设置--行信息
     *
     * @param attchmentTypeList
     * @param id
     * @return
     */
    @PostMapping("/save/setting/{id}")
    public ResponseEntity<List<AttachmentType>> saveNewSetting(@PathVariable(value = "id") String id, @RequestBody List<AttachmentType> attchmentTypeList) {
        return ResponseEntity.ok(expReportTypeAttchmentService.createAttachmentType(attchmentTypeList, id));
    }

    /**
     * 根据单据类型Id更新附件类型设置
     * @param expReportTypeId
     * @param attchmentTypeList
     * @return
     */
    /* @PutMapping("/update/setting/{expReportTypeId}")
    public ResponseEntity<List<AttachmentType>> udateNewSetting(@PathVariable(value = "expReportTypeId") String expReportTypeId,@RequestBody List<AttachmentType> attchmentTypeList) {
        return ResponseEntity.ok(expReportTypeAttchmentService.updateAttachmentType(expReportTypeId,attchmentTypeList));
    }*/

    /**
     * 自定义条件查询 附件设置头页面List查询(分页)
     * url:api/report/type/attachment/query
     * @param docTypeCode
     * @param dcoTypeName
     * @param pageable
     * @return
     */
    @GetMapping("/query")
    public ResponseEntity<List<ExpReportTypeAttchment>> pageReportTypeAttchmentByCond(
            @RequestParam(value = "docTypeCode", required = false) String docTypeCode,
            @RequestParam(value = "docTypeName", required = false) String dcoTypeName,
            Pageable pageable) {
        Page queryPage = PageUtil.getPage(pageable);
        List<ExpReportTypeAttchment> expenseBookList = expReportTypeAttchmentService.pageAttachmentSettingByCond(docTypeCode,dcoTypeName,queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity(expenseBookList, httpHeaders, HttpStatus.OK);
    }

    /**
     * 根据ID查询 单据类型信息--单条头信息
     *
     * @param id
     * @return
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<ExpReportTypeAttchment> getExpReportTypeById(@PathVariable Long id) {
        ExpReportTypeAttchment expReportTypeAttchment = expReportTypeAttchmentService.selectById(id);
        return ResponseEntity.ok(expReportTypeAttchment);
    }

    /**
     * 根据ID查询 附件信息--单条行信息
     *
     * @param attachTypeId
     * @return
     */
    @GetMapping("/select/{id}")
    public ResponseEntity<AttachmentType> getAttchmentTypeById(@PathVariable(value = "id") String attachTypeId) {
        return ResponseEntity.ok(expReportTypeAttchmentService.getAttachmentTypeById(attachTypeId));
    }

    /**
     * 根据外键单据类型id查询 附件类型信息--全部行信息
     *
     * @param reportTypeId
     * @return
     */
    @GetMapping("/select/settings/{id}")
    public ResponseEntity<List<AttachmentType>> getAttchmentTypeListById(@PathVariable(value = "id") String reportTypeId) {
        return ResponseEntity.ok(expReportTypeAttchmentService.getAttachmentTypeListById(reportTypeId));
    }

    /**
     * 修改 单据类型--头信息
     *
     * @param expReportTypeAttchment
     * @return
     */
    @PutMapping("/update")
    public ResponseEntity updateExpenseBook(@RequestBody ExpReportTypeAttchment expReportTypeAttchment) {
        return ResponseEntity.ok(expReportTypeAttchmentService.updateAttachmentSetting(expReportTypeAttchment));
    }

    /**
     * 删除 根据id删除单据类型
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteExpenseBooks(@PathVariable(value = "id") Long id) {
        expReportTypeAttchmentService.deleteAttachmentSetting(id);
        return ResponseEntity.ok().build();
    }

    /**
     * LOV弹窗  返回code和name  获取当前账套下未创建的单据类型
     * @param setOfBooksId
     * @param dcoTypeName
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/query/document/type")
    public ResponseEntity<List<DocumentType>> getExpenseReportTypeByCond(
            @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @RequestParam(value = "docTypeCode", required = false) String dcoTypeCode,
            @RequestParam(value = "docTypeName", required = false) String dcoTypeName,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<DocumentType> list = new ArrayList<>();
        List<ExpReportTypeAttchment> expReportTypeAttchmentList = expReportTypeAttchmentService.getByTypeCode(page);
        List<ExpenseReportType> expenseReportTypeList = expReportTypeAttchmentService.getExpenseReportTypeByCond(setOfBooksId,dcoTypeCode,dcoTypeName,page);
        List<ExpenseAccrualType> expenseAccrualTypeList = expReportTypeAttchmentService.getExpenseAccrualTypeByCond(setOfBooksId,dcoTypeCode,dcoTypeName,page);
        for(ExpenseReportType expenseReportType: expenseReportTypeList){
            DocumentType documentType = new DocumentType();
            if(StringUtils.isNotEmpty(expenseReportType.getReportTypeCode()) && StringUtils.isNotEmpty(expenseReportType.getReportTypeName())) {
                documentType.setDocTypeCode(expenseReportType.getReportTypeCode());
                documentType.setDocTypeName(expenseReportType.getReportTypeName());
            }
            list.add(documentType);
        }
        for(ExpenseAccrualType expenseAccrualType: expenseAccrualTypeList){
            DocumentType documentType = new DocumentType();
            if(StringUtils.isNotEmpty(expenseAccrualType.getExpAccrualTypeCode()) && StringUtils.isNotEmpty(expenseAccrualType.getExpAccrualTypeName())) {
                documentType.setDocTypeCode(expenseAccrualType.getExpAccrualTypeCode());
                documentType.setDocTypeName(expenseAccrualType.getExpAccrualTypeName());
            }
            list.add(documentType);
        }
        for(ExpReportTypeAttchment expReportTypeAttchment :expReportTypeAttchmentList){
            String typeCode =  expReportTypeAttchment.getDocTypeCode();
            String typeName =  expReportTypeAttchment.getDocTypeName();
            DocumentType documentTypeone = new DocumentType();
            if(StringUtils.isNotEmpty(typeCode)&&StringUtils.isNotEmpty(typeName)){
                documentTypeone.setDocTypeCode(typeCode);
                documentTypeone.setDocTypeName(typeName);
                list.remove(documentTypeone);
            }
        }
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity(list,httpHeaders, HttpStatus.OK);
    }

}
