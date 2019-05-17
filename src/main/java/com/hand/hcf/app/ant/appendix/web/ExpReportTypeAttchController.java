package com.hand.hcf.app.ant.appendix.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.appendix.domain.ExpReportTypeAttchment;
import com.hand.hcf.app.ant.appendix.service.ExpReportTypeAttchmentService;
import com.hand.hcf.app.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:  单据类型附件权限设置controller
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @date 2019/5/16 15:40
 */


@RestController
@RequestMapping("/api/report/type/attachment")
public class ExpReportTypeAttchController {

    @Autowired
    private ExpReportTypeAttchmentService expReportTypeAttchmentService;

    /**
     * 新增 单据类型
     *
     * @param expReportTypeAttchment
     * @return
     */
    @PostMapping("/add/doctype")
    public ResponseEntity<ExpReportTypeAttchment>  createNewSetting(@RequestBody ExpReportTypeAttchment expReportTypeAttchment){
        return ResponseEntity.ok(expReportTypeAttchmentService.createAttachmentSetting(expReportTypeAttchment));
    }

    /**
     * 新增 单据类型附件设置
     *
     * @param expReportTypeAttchment
     * @return
     */
    @PostMapping("/add/setting")
    public ResponseEntity<ExpReportTypeAttchment>  createAttachmentSetting(@RequestBody ExpReportTypeAttchment expReportTypeAttchment){
        return ResponseEntity.ok(expReportTypeAttchmentService.createAttachmentSetting(expReportTypeAttchment));
    }

    /**
     * 自定义条件查询 单据类型附件设置查询(分页)
     */
    @GetMapping("/query")
    public ResponseEntity<List<ExpReportTypeAttchment>>  pageReportTypeAttchmentByCond(
                                                                        @RequestParam(value = "reportTypeCode", required = false) String reportTypeId,
                                                                         Pageable pageable){
        Page queryPage = PageUtil.getPage(pageable);
        List<ExpReportTypeAttchment> expenseBookList = expReportTypeAttchmentService.pageAttachmentSettingByCond(reportTypeId,queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity(expenseBookList,httpHeaders, HttpStatus.OK);
    }

    /**
     * 根据ID查询 单据类型附件设置
     *
     * @param id
     * @return
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<ExpReportTypeAttchment> getExpenseBookById(@PathVariable Long id){
        return ResponseEntity.ok(expReportTypeAttchmentService.selectById(id));
    }

    /**
     * 修改 单据类型附件设置
     *
     * @param expReportTypeAttchment
     * @return
     */
    @PutMapping("/update")
    public ResponseEntity updateExpenseBook(@RequestBody ExpReportTypeAttchment expReportTypeAttchment){
        return ResponseEntity.ok(expReportTypeAttchmentService.updateAttachmentSetting(expReportTypeAttchment));
    }

    /**
     * 删除 根据单据类型附件设置id
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteExpenseBooks(@PathVariable(value = "id") Long id){
        expReportTypeAttchmentService.deleteAttachmentSetting(id);
        return ResponseEntity.ok().build();
    }

}
