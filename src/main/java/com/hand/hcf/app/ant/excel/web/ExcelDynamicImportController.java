package com.hand.hcf.app.ant.excel.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.excel.domain.ExcelTemplate;
import com.hand.hcf.app.ant.excel.domain.ExcelTemplateField;
import com.hand.hcf.app.ant.excel.domain.ExcelTemplateMapping;
import com.hand.hcf.app.ant.excel.domain.ExcelTemplateMappingField;
import com.hand.hcf.app.ant.excel.dto.ExcelTemplateMappingDTO;
import com.hand.hcf.app.ant.excel.service.*;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/6/4
 */
@RestController
@RequestMapping("/api/excel/import")
public class ExcelDynamicImportController {

    @Autowired
    private ExpenseTypeExcelService expenseTypeExcelService;
    @Autowired
    private ExcelTemplateService excelTemplateService;
    @Autowired
    private ExcelTemplateFieldService excelTemplateFieldService;
    @Autowired
    private ExcelTemplateMappingFieldService excelTemplateMappingFieldService;
    @Autowired
    private ExcelTemplateMappingService excelTemplateMappingService;


    /**
     * 自定义条件查询 业务小类(分页)
     *
     * @param setOfBooksId
     * @param code
     * @param name
     * @param enabled
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/expense/type/query")
    public ResponseEntity<List<ExpenseType>> getExpenseReportTypeByCond(
            @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "enabled", required = false) Boolean enabled,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        setOfBooksId = OrgInformationUtil.getCurrentSetOfBookId();
        List<ExpenseType> list = expenseTypeExcelService.getExpenseTypeByCond(setOfBooksId, code, name, enabled, page);
        HttpHeaders headers = new HttpHeaders();
        PageUtil.getTotalHeader(page);
        return new ResponseEntity(list, headers, HttpStatus.OK);
    }

    /**
     * 新增Excel导入模板
     *
     * @param excelTemplate
     * @return
     */
    @PostMapping
    public ResponseEntity insertExcelTemplate(@RequestBody ExcelTemplate excelTemplate) {
        return ResponseEntity.ok(excelTemplateService.insertExcelTemplate(excelTemplate));
    }

    /**
     * 查询Excel导入模板
     *
     * @param expenseTypeId
     * @param expenseAttribute
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/template/query")
    public ResponseEntity pageExcelTemplateByCond(@RequestParam(value = "expenseTypeId", required = false) Long expenseTypeId,
                                                  @RequestParam(value = "expense_attribute", required = false) String expenseAttribute,
                                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size) {
        Page queryPage = PageUtil.getPage(page, size);
        List<ExcelTemplate> excelTemplates = excelTemplateService.pageExcelTemplateByCond(expenseTypeId, expenseAttribute, queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity<>(excelTemplates, httpHeaders, HttpStatus.OK);
    }

    /**
     * 查询模板字段
     *
     * @param excelTemplateId
     * @return
     */
    @GetMapping("/{excelTemplateId}/template/field/query")
    public ResponseEntity queryExcelTemplateField(@PathVariable("excelTemplateId") Long excelTemplateId) {

        return ResponseEntity.ok(excelTemplateFieldService.queryExcelTemplateField(excelTemplateId));

    }

    /**
     * 保存模板字段
     *
     * @param excelTemplateFields
     * @param excelTemplateId
     * @return
     */
    @PostMapping("/{excelTemplateId}/template/field")
    public ResponseEntity<List<ExcelTemplateField>> saveExcelTemplateInfo(@RequestBody List<ExcelTemplateField> excelTemplateFields,
                                                                          @PathVariable("excelTemplateId") Long excelTemplateId) {
        for (int i = 0; i < excelTemplateFields.size(); i++) {
            excelTemplateFieldService.saveExcelTemplateInfo(excelTemplateFields.get(i), excelTemplateId);
        }
        return ResponseEntity.ok(excelTemplateFields);
    }


    /**
     * 底稿映射模板查询
     *
     * @param expenseTypeId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/template/mapping/query")
    public ResponseEntity pageExcelTemplateMappingByCond(@RequestParam(value = "expenseTypeId", required = false) Long expenseTypeId,
                                                         @RequestParam(value = "expense_attribute", required = false) String expenseAttribute,
                                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                                         @RequestParam(value = "size", defaultValue = "10") int size) {
        Page queryPage = PageUtil.getPage(page, size);
        List<ExcelTemplateMapping> excelTemplateMappingFields = excelTemplateMappingService.pageExcelTemplateMappingByCond(expenseTypeId, expenseAttribute, queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity<>(excelTemplateMappingFields, httpHeaders, HttpStatus.OK);
    }

    /**
     * 底稿映射模板保存
     *
     * @param excelTemplateMapping
     * @return
     */
    @PostMapping("/template/mapping/save")
    public ResponseEntity insertExcelTemplateMapping(@RequestBody ExcelTemplateMapping excelTemplateMapping) {
        return ResponseEntity.ok(excelTemplateMappingService.insertExcelTemplateMapping(excelTemplateMapping));
    }

    /**
     * 删除底稿映射模板
     *
     * @param id
     */
    @DeleteMapping("/mapping/field/delete/{id}")
    public void deleteExcelTemplateMapping(@PathVariable Long id) {
        excelTemplateMappingService.deleteExcelTemplateMapping(id);
    }

    /**
     * 映射字段查询
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}/template/mapping/field/query")
    public ResponseEntity queryExcelTemplateMappingField(@PathVariable("id") Long id) {

        return ResponseEntity.ok(excelTemplateMappingFieldService.queryExcelTemplateMappingField(id));

    }

    /**
     * 映射字段保存
     *
     * @param excelTemplateMappingField
     * @param id
     * @return
     */
    @PostMapping("/{id}/template/mapping/field")
    public ResponseEntity<ExcelTemplateMappingField> saveExcelTemplateMappingInfo(@RequestBody ExcelTemplateMappingField excelTemplateMappingField,
                                                                                  @PathVariable("id") Long id) {

        return ResponseEntity.ok(excelTemplateMappingFieldService.saveExcelTemplateInfo(excelTemplateMappingField, id));

    }

    /**
     * 删除映射字段
     *
     * @param id
     */
    @DeleteMapping("/{id}/template/mapping/field/delete")
    public void deleteExcelTemplateMappingInfo(@PathVariable Long id) {
        excelTemplateMappingFieldService.deleteExcelTemplateMappingField(id);
    }

    /**
     * 映射字段取值查询
     */
    @GetMapping("/mapping/field/query")
    public ResponseEntity<List<ExcelTemplateMappingDTO>> getMappingFieldByCond(
            @RequestParam(value = "id", required = false) Long id,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExcelTemplateMappingDTO> list = excelTemplateMappingFieldService.getMappingFieldByCond(id, page);
        HttpHeaders headers = PageUtil.getTotalHeader(page);
        return new ResponseEntity(list, headers, HttpStatus.OK);
    }

    /**
     * 查询映射页面模板字段
     */
    @GetMapping("/template/fields/query")
    public ResponseEntity<List<ExcelTemplateField>> queryTemplateField(
            @RequestParam(value = "id", required = false) Long expenseTypeId,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExcelTemplateField> list = excelTemplateMappingFieldService.getTemplateFieldByCond(expenseTypeId, page);
        HttpHeaders headers = PageUtil.getTotalHeader(page);
        return new ResponseEntity(list, headers, HttpStatus.OK);

    }

}
