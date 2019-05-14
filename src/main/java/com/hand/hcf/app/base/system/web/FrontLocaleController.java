package com.hand.hcf.app.base.system.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.system.domain.FrontLocale;
import com.hand.hcf.app.base.system.dto.LocaleDTO;
import com.hand.hcf.app.base.system.service.FrontLocaleService;
import com.hand.hcf.app.core.util.PageUtil;
import io.swagger.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/3/12
 */
@Api(tags = "中控多语言")
@RestController
@RequestMapping("/api/front/locale")
public class FrontLocaleController {
    private final FrontLocaleService frontLocaleService;

    public FrontLocaleController(FrontLocaleService frontLocaleService){
        this.frontLocaleService = frontLocaleService;
    }

    @PostMapping
    @ApiOperation(value = "新增中控多语言", notes = "单个新增中控多语言 开发:韩雪")
    public ResponseEntity<FrontLocale> createFrontLocale(@ApiParam(value = "中控多语言") @RequestBody FrontLocale frontLocale){
        return ResponseEntity.ok(frontLocaleService.createFrontLocale(frontLocale));
    }

    @PostMapping("/batch")
    @ApiOperation(value = "批量新增中控多语言", notes = "批量新增中控多语言 开发:韩雪")
    public ResponseEntity<List<FrontLocale>> createFrontLocaleBatch(@ApiParam(value = "中控多语言") @RequestBody List<FrontLocale> list){
        return ResponseEntity.ok(frontLocaleService.createFrontLocaleBatch(list));
    }

    @PutMapping
    @ApiOperation(value = "编辑中控多语言", notes = "单个编辑中控多语言 开发:韩雪")
    public ResponseEntity<FrontLocale> updateFrontLocale(@ApiParam(value = "中控多语言") @RequestBody FrontLocale frontLocale){
        return ResponseEntity.ok(frontLocaleService.updateFrontLocale(frontLocale));
    }

    @PutMapping("/batch")
    @ApiOperation(value = "批量编辑中控多语言", notes = "批量编辑中控多语言 开发:韩雪")
    public ResponseEntity<List<FrontLocale>> updateFrontLocaleBatch(@ApiParam(value = "中控多语言") @RequestBody List<FrontLocale> list){
        return ResponseEntity.ok(frontLocaleService.updateFrontLocaleBatch(list));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "单个删除中控多语言", notes = "单个删除中控多语言 开发:韩雪")
    public ResponseEntity deleteFrontLocaleById(@ApiParam(value = "主键id") @PathVariable Long id){
        frontLocaleService.deleteFrontLocaleById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "单个查询中控多语言", notes = "单个查询中控多语言 开发:韩雪")
    public ResponseEntity<FrontLocale> getFrontLocaleById(@ApiParam(value = "主键id") @PathVariable Long id){
        return ResponseEntity.ok(frontLocaleService.getFrontLocaleById(id));
    }

    @GetMapping("/query/by/cond")
    @ApiOperation(value = "分页查询中控多语言", notes = "分页查询中控多语言 开发:韩雪")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<FrontLocale>> getFrontLocaleByCond(
            @ApiParam(value = "语言") @RequestParam(value = "lang") String language,
            @ApiParam(value = "应用ID") @RequestParam(value = "applicationId") Long applicationId,
            @ApiParam(value = "key值") @RequestParam(value = "keyCode",required = false) String keyCode,
            @ApiParam(value = "key描述") @RequestParam(value = "keyDescription",required = false) String keyDescription,
            @ApiIgnore Pageable pageable) throws URISyntaxException{
        Page page = PageUtil.getPage(pageable);
        List<FrontLocale> result = frontLocaleService.getFrontLocaleByCond(language,applicationId,keyCode,keyDescription,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result,httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/query/map/by/cond")
    @ApiOperation(value = "不分页查询中控多语言", notes = "不分页查询中控多语言 开发:韩雪")
    public ResponseEntity<Map<String,String>> mapFrontLocaleByCond(
            @ApiParam(value = "语言") @RequestParam(value = "lang") String language,
            @ApiParam(value = "应用ID") @RequestParam(value = "applicationId") Long applicationId){
        return ResponseEntity.ok(frontLocaleService.mapFrontLocaleByCond(language,applicationId));
    }

    @GetMapping("/query/other/front/locale/by/cond")
    @ApiOperation(value = "分页查询中控多语言(返回外文描述信息)", notes = "分页查询中控多语言(返回外文描述信息) 开发:韩雪")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<LocaleDTO>> getOtherFrontLocaleByCond(
            @ApiParam(value = "应用ID") @RequestParam(value = "applicationId") Long applicationId,
            @ApiParam(value = "源语言") @RequestParam(value = "sourceLanguage") String sourceLanguage,
            @ApiParam(value = "目标语言") @RequestParam(value = "targetLanguage") String targetLanguage,
            @ApiParam(value = "key值") @RequestParam(value = "keyCode",required = false) String keyCode,
            @ApiIgnore Pageable pageable) throws URISyntaxException{
        Page page = PageUtil.getPage(pageable);
        List<LocaleDTO> result = frontLocaleService.getOtherFrontLocaleByCond(applicationId,sourceLanguage,targetLanguage,keyCode,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result,httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/init/{tenantId}")
    @ApiOperation(value = "初始化前端多语言表", notes = "初始化前端多语言表 开发:程占华")
    public ResponseEntity initFrontLocale(@ApiParam(value = "租户id") @PathVariable Long tenantId){
        frontLocaleService.initFrontLocale(tenantId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/batch/insert/all/tenant")
    @ApiOperation(value = "新增多语言给全部租户", notes = "新增多语言给全部租户 开发:谢宾")
    public ResponseEntity batchInsertAllTenant(@RequestBody List<FrontLocale> list){
        frontLocaleService.batchInsertAllTenant(list);
        return ResponseEntity.ok().build();
    }
}
