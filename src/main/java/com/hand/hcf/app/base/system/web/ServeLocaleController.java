package com.hand.hcf.app.base.system.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.system.domain.ServeLocale;
import com.hand.hcf.app.base.system.dto.LocaleDTO;
import com.hand.hcf.app.base.system.service.ServeLocaleService;
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
 * @date: 2019/3/11
 */
@Api(tags = "服务端多语言")
@RestController
@RequestMapping("/api/serve/locale")
public class ServeLocaleController {
    private final ServeLocaleService serveLocaleService;

    public ServeLocaleController(ServeLocaleService serveLocaleService){
        this.serveLocaleService = serveLocaleService;
    }

    @PostMapping
    @ApiOperation(value = "新增服务端多语言", notes = "单个新增服务端多语言 开发:韩雪")
    public ResponseEntity<ServeLocale> createServeLocale(@ApiParam(value = "服务端多语言") @RequestBody ServeLocale serveLocale){
        return ResponseEntity.ok(serveLocaleService.createServeLocale(serveLocale));
    }

    @PostMapping("/batch")
    @ApiOperation(value = "批量新增服务端多语言", notes = "批量新增服务端多语言 开发:韩雪")
    public ResponseEntity<List<ServeLocale>> createServeLocaleBatch(@ApiParam(value = "服务端多语言") @RequestBody List<ServeLocale> list){
        return ResponseEntity.ok(serveLocaleService.createServeLocaleBatch(list));
    }

    @PutMapping
    @ApiOperation(value = "编辑服务端多语言", notes = "单个编辑服务端多语言 开发:韩雪")
    public ResponseEntity<ServeLocale> updateServeLocale(@ApiParam(value = "服务端多语言") @RequestBody ServeLocale serveLocale){
        return ResponseEntity.ok(serveLocaleService.updateServeLocale(serveLocale));
    }

    @PutMapping("/batch")
    @ApiOperation(value = "批量编辑服务端多语言", notes = "批量编辑服务端多语言 开发:韩雪")
    public ResponseEntity<List<ServeLocale>> updateServeLocaleBatch(@ApiParam(value = "服务端多语言") @RequestBody List<ServeLocale> list){
        return ResponseEntity.ok(serveLocaleService.updateServeLocaleBatch(list));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除服务端多语言", notes = "单个删除服务端多语言 开发:韩雪")
    public ResponseEntity deleteServeLocaleById(@ApiParam(value = "主键id") @PathVariable Long id){
        serveLocaleService.deleteServeLocaleById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "查询服务端多语言", notes = "单个查询服务端多语言 开发:韩雪")
    public ResponseEntity<ServeLocale> getServeLocaleById(@ApiParam(value = "主键id") @PathVariable Long id){
        return ResponseEntity.ok(serveLocaleService.getServeLocaleById(id));
    }

    @GetMapping("/query/by/cond")
    @ApiOperation(value = "分页查询服务端多语言", notes = "分页查询服务端多语言 开发:韩雪")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ServeLocale>> getServeLocaleByCond(
            @ApiParam(value = "语言") @RequestParam(value = "lang",required = false) String language,
            @ApiParam(value = "应用ID") @RequestParam(value = "applicationId",required = false) Long applicationId,
            @ApiParam(value = "key值") @RequestParam(value = "keyCode",required = false) String keyCode,
            @ApiParam(value = "key描述") @RequestParam(value = "keyDescription",required = false) String keyDescription,
            @ApiParam(value = "多语言类型") @RequestParam(value = "category",required = false) String category,
            @ApiIgnore Pageable pageable) throws URISyntaxException{
        Page page = PageUtil.getPage(pageable);
        List<ServeLocale> result = serveLocaleService.getServeLocaleByCond(language,applicationId,keyCode,keyDescription,category,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result,httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/query/map/by/cond")
    @ApiOperation(value = "不分页查询map形式的服务端多语言", notes = "不分页查询map形式的服务端多语言 开发:韩雪")
    public ResponseEntity<Map<String,String>> mapServeLocaleByCond(
            @ApiParam(value = "语言") @RequestParam(value = "lang") String language,
            @ApiParam(value = "应用ID")@RequestParam(value = "applicationId") Long applicationId){
        return ResponseEntity.ok(serveLocaleService.mapServeLocaleByCond(language,applicationId));
    }

    @GetMapping("/query/other/serve/locale/by/cond")
    @ApiOperation(value = "分页查询 服务端多语言(返回外文描述信息)", notes = "分页查询 服务端多语言(返回外文描述信息) 开发:韩雪")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<LocaleDTO>> getOtherServeLocaleByCond(
            @ApiParam(value = "应用ID") @RequestParam(value = "applicationId") Long applicationId,
            @ApiParam(value = "源语言") @RequestParam(value = "sourceLanguage") String sourceLanguage,
            @ApiParam(value = "目标语言") @RequestParam(value = "targetLanguage") String targetLanguage,
            @ApiParam(value = "key值") @RequestParam(value = "keyCode",required = false) String keyCode,
            @ApiIgnore Pageable pageable) throws URISyntaxException{
        Page page = PageUtil.getPage(pageable);
        List<LocaleDTO> result = serveLocaleService.getOtherServeLocaleByCond(applicationId,sourceLanguage,targetLanguage,keyCode,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result,httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/init/{tenantId}")
    @ApiOperation(value = "初始化后端多语言表", notes = "初始化后端多语言表 开发:程占华")
    public ResponseEntity initServeLocale(@ApiParam(value = "租户id") @PathVariable Long tenantId){
        serveLocaleService.initServeLocale(tenantId);
        return ResponseEntity.ok().build();
    }
}
