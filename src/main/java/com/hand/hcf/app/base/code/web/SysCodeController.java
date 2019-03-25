package com.hand.hcf.app.base.code.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.code.domain.SysCode;
import com.hand.hcf.app.base.code.domain.SysCodeValue;
import com.hand.hcf.app.base.code.service.SysCodeService;
import com.hand.hcf.app.base.code.service.SysCodeValueService;
import com.hand.hcf.app.base.code.service.SysCodeValueTempService;
import com.hand.hcf.app.base.system.enums.SysCodeEnum;
import com.hand.hcf.app.base.util.FileUtil;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.domain.ExportConfig;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.util.LoginInformationUtil;
import com.hand.hcf.core.util.PageUtil;
import com.hand.hcf.core.web.dto.ImportResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/25
 */
@RestController
@RequestMapping("/api/custom")
public class SysCodeController {
    public final static String IMPORT_TEMPLATE_PATH = "/templates/customenumerationItemTemplate.xlsx";

    public final static String ERROR_TEMPLATE_PATH = "/templates/customenumerationItemErrorTemplate.xlsx";
    @Autowired
    private SysCodeService sysCodeService;
    @Autowired
    private SysCodeValueService itemService;
    @Autowired
    private SysCodeValueTempService tempService;

    /**
     * 条件查询值列表展示
     * @param enabled
     * @param name
     * @param code
     * @param typeFlag
     * @param pageable
     * @return
     */
    @GetMapping("/enumerations")
    public ResponseEntity queryPageByIsCustom(@RequestParam(value = "enabled",required = false) Boolean enabled,
                                              @RequestParam(value = "name",required = false) String name,
                                              @RequestParam(value = "code",required = false) String code,
                                              @RequestParam(value = "typeFlag",required = false) SysCodeEnum typeFlag,
                                              Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        List<SysCode> sysCodes = sysCodeService.pageByCondition(page, code, name, enabled, typeFlag);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/custom/enumerations");
        return new ResponseEntity<>(sysCodes, httpHeaders, HttpStatus.OK);
    }

    /**
     * 更改值列表
     * @param sysCode
     * @return
     */
    @PutMapping("/enumerations")
    public ResponseEntity updateSysCode(@RequestBody SysCode sysCode,
                                        @RequestHeader(value = "X-Menu-Params", required = false) String systemFlag){
        return ResponseEntity.ok(sysCodeService.updateSysCode(sysCode, systemFlag));
    }

    /**
     * 创建值列表
     * @param sysCode
     * @return
     */
    @PostMapping("/enumerations")
    public ResponseEntity createSysCode(@RequestBody SysCode sysCode,
                                        @RequestHeader(value = "X-Menu-Params", required = false) String systemFlag){

        return ResponseEntity.ok(sysCodeService.createSysCode(sysCode, systemFlag));
    }

    /**
     * 根据id获取值列表信息
     * @param id
     * @return
     */
    @GetMapping("/enumerations/{id}")
    public ResponseEntity getById(@PathVariable("id") Long id){
        return ResponseEntity.ok(sysCodeService.getById(id));
    }

    /**
     * 根据值列表Id获取其下的所有值
     * @param id
     * @param keyword
     * @param pageable
     * @return
     */
    @GetMapping("/enumerations/{id}/items")
    public ResponseEntity queryPageItemsById(@PathVariable("id") Long id,
                                              @RequestParam(value = "keyword",required = false) String keyword,
                                              Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        List<SysCodeValue> result = sysCodeService.pageSysCodeValueByCodeId(page, id, keyword);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/custom/enumerations/" + id + "/items");
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    /**
     * 更改值列表的值
     * @param sysCodeValue
     * @return
     */
    @PutMapping("/enumerations/items")
    public ResponseEntity updateSysCodeValue(@RequestBody @Validated SysCodeValue sysCodeValue){
        return ResponseEntity.ok(sysCodeService.updateSysCodeValue(sysCodeValue));
    }

    /**
     * 创建一个值列表的值
     * @param sysCodeValue
     * @return
     */
    @PostMapping("/enumerations/items")
    public ResponseEntity createSysCodeValue(@RequestBody @Validated SysCodeValue sysCodeValue){
        return ResponseEntity.ok(sysCodeService.createSysCodeValue(sysCodeValue));
    }

    /**
     * 根据值列表的值ID获取值信息
     * @param id
     * @return
     */
    @GetMapping("/enumerations/items/{id}")
    public ResponseEntity getSysCodeValueById(@PathVariable("id") Long id){
        return ResponseEntity.ok(itemService.getById(id));
    }

    /**
     * 兼容前端原值列表获取的api,  通过值列代码获取其下所有的值信息
     * @param type
     * @param all
     * @return
     */
    @GetMapping("/enumerations/template/by/type")
    public ResponseEntity listItemsByType(@RequestParam("type") String type,
                                          @RequestParam(value = "all", required = false) Boolean all){
        if (Boolean.TRUE.equals(all)){
            return ResponseEntity.ok(sysCodeService.listAllSysCodeValueBySysCode(type));
        }else{
            return ResponseEntity.ok(sysCodeService.listEnabledSysCodeValueBySysCodeAnd(type));
        }
    }

    @PostMapping("/enumerations/items/export")
    public void exportItem(@RequestParam("id") Long id,
                           @RequestBody ExportConfig exportConfig,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
        sysCodeService.exportItem(id, exportConfig, request, response);
    }

    @PostMapping("/enumeration/items/batch/enable/or/disable")
    public ResponseEntity updateItemStatus(@RequestBody List<Long> ids,
                                           @RequestParam("enable") Boolean enable){
        return ResponseEntity.ok(sysCodeService.updateValueStatusByValueIds(ids, enable));
    }

    @GetMapping("/enumerations/items/template")
    public ResponseEntity<byte[]> exportEnumerationItemTemplate() {
        byte[] bytes = FileUtil.getFileBinaryForDownload(FileUtil.getTemplatePath(IMPORT_TEMPLATE_PATH, LoginInformationUtil.getCurrentLanguage()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @PostMapping("/enumerations/items/import")
    public ResponseEntity importCustomEnumerationItems(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("id") Long id) throws Exception {
        try(InputStream in = file.getInputStream()) {
            UUID transactionOid = sysCodeService.importSysCodeValue(in, id);
            Map<String, UUID> result = new HashMap<>();
            result.put("transactionOid", transactionOid);
            return ResponseEntity.ok(result);
        }catch (IOException e){
            throw new BizException(RespCode.SYS_READ_FILE_ERROR);
        }
    }

    @GetMapping("/enumerations/items/import/query/result/{transactionOid}")
    public ResponseEntity queryResultInfo(@PathVariable("transactionOid") String transactionOid){
        ImportResultDTO importResultDTO = sysCodeService.queryImportResultInfo(transactionOid);
        return ResponseEntity.ok(importResultDTO);
    }

    @DeleteMapping("/enumerations/items/import/delete/{transactionOid}")
    public ResponseEntity deleteTemp(@PathVariable("transactionOid") String transactionOid){
        return ResponseEntity.ok(tempService.deleteTemp(transactionOid));
    }

    @PostMapping("/enumerations/items/import/confirm/{transactionOid}")
    public ResponseEntity confirmTemp(@PathVariable("transactionOid") String transactionOid){
        return ResponseEntity.ok(itemService.confirmTemp(transactionOid));
    }

    @GetMapping("/enumerations/items/import/error/export/{transactionOid}")
    public ResponseEntity exportFailedData(@PathVariable("transactionOid") UUID transactionOid){
        String path = ERROR_TEMPLATE_PATH;
        return ResponseEntity.ok(tempService.exportFailedData(path, transactionOid));
    }

    /**
     * 该方法为兼容原lov查询值列表
     * @param pageable
     * @param code
     * @param codeFrom
     * @param codeTo
     * @param value
     * @return
     */
    @GetMapping("/enumeration/system/by/type/condition")
    public ResponseEntity pageByCondition(Pageable pageable,
                                          @RequestParam(value = "systemCustomEnumerationType") String code,
                                          @RequestParam(value = "codeFrom",required = false) String codeFrom,
                                          @RequestParam(value = "codeTo",required = false) String codeTo,
                                          @RequestParam(value = "value",required = false) String value){
        Page page = PageUtil.getPage(pageable);
        List<SysCodeValue> result = sysCodeService.pageSysCodeValueByCondition(page, code, codeFrom, codeTo, value);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/custom/enumeration/system/by/type/condition");
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    /**
     * lov查询
     * @param pageable
     * @param code
     * @param valueFrom
     * @param valueTo
     * @param value
     * @return
     */
    @GetMapping("/enumeration/lov/by/type/condition")
    public ResponseEntity pageLovByCondition(Pageable pageable,
                                          @RequestParam(value = "code") String code,
                                          @RequestParam(value = "valueFrom",required = false) String valueFrom,
                                          @RequestParam(value = "valueTo",required = false) String valueTo,
                                          @RequestParam(value = "value",required = false) String value){
        Page page = PageUtil.getPage(pageable);
        List<SysCodeValue> result = sysCodeService.pageSysCodeValueByCondition(page, code, valueFrom, valueTo, value);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/custom/enumeration/lov/by/type/condition");
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }


    @GetMapping("/enumerations/init")
    public ResponseEntity initSysCode(){
        return ResponseEntity.ok(sysCodeService.init());
    }
}
