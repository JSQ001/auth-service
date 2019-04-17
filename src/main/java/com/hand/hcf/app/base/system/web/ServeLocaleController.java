package com.hand.hcf.app.base.system.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.system.domain.ServeLocale;
import com.hand.hcf.app.base.system.dto.LocaleDTO;
import com.hand.hcf.app.base.system.service.ServeLocaleService;
import com.hand.hcf.app.core.util.PageUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/3/11
 */
@RestController
@RequestMapping("/api/serve/locale")
public class ServeLocaleController {
    private final ServeLocaleService serveLocaleService;

    public ServeLocaleController(ServeLocaleService serveLocaleService){
        this.serveLocaleService = serveLocaleService;
    }

    /**
     * 单个新增 服务端多语言
     * @param serveLocale
     * @return
     */
    @PostMapping
    public ResponseEntity<ServeLocale> createServeLocale(@RequestBody ServeLocale serveLocale){
        return ResponseEntity.ok(serveLocaleService.createServeLocale(serveLocale));
    }

    /**
     * 批量新增 服务端多语言
     * @param list
     * @return
     */
    @PostMapping("/batch")
    public ResponseEntity<List<ServeLocale>> createServeLocaleBatch(@RequestBody List<ServeLocale> list){
        return ResponseEntity.ok(serveLocaleService.createServeLocaleBatch(list));
    }

    /**
     * 单个编辑 服务端多语言
     * @param serveLocale
     * @return
     */
    @PutMapping
    public ResponseEntity<ServeLocale> updateServeLocale(@RequestBody ServeLocale serveLocale){
        return ResponseEntity.ok(serveLocaleService.updateServeLocale(serveLocale));
    }

    /**
     * 批量编辑 服务端多语言
     * @param list
     * @return
     */
    @PutMapping("/batch")
    public ResponseEntity<List<ServeLocale>> updateServeLocaleBatch(@RequestBody List<ServeLocale> list){
        return ResponseEntity.ok(serveLocaleService.updateServeLocaleBatch(list));
    }

    /**
     * 单个删除 服务端多语言
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteServeLocaleById(@PathVariable Long id){
        serveLocaleService.deleteServeLocaleById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 单个查询 服务端语言
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServeLocale> getServeLocaleById(@PathVariable Long id){
        return ResponseEntity.ok(serveLocaleService.getServeLocaleById(id));
    }

    /**
     * 分页查询 服务端多语言
     * @param language 语言
     * @param applicationId 应用ID
     * @param keyCode 界面key值
     * @param keyDescription key描述
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/query/by/cond")
    public ResponseEntity<List<ServeLocale>> getServeLocaleByCond(
            @RequestParam(value = "lang",required = false) String language,
            @RequestParam(value = "applicationId",required = false) Long applicationId,
            @RequestParam(value = "keyCode",required = false) String keyCode,
            @RequestParam(value = "keyDescription",required = false) String keyDescription,
            @RequestParam(value = "category",required = false) String category,
            Pageable pageable) throws URISyntaxException{
        Page page = PageUtil.getPage(pageable);
        List<ServeLocale> result = serveLocaleService.getServeLocaleByCond(language,applicationId,keyCode,keyDescription,category,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * 不分页查询 map形式的服务端多语言
     * @param language
     * @param applicationId
     * @return
     */
    @GetMapping("/query/map/by/cond")
    public ResponseEntity<Map<String,String>> mapServeLocaleByCond(
            @RequestParam(value = "lang") String language,
            @RequestParam(value = "applicationId") Long applicationId){
        return ResponseEntity.ok(serveLocaleService.mapServeLocaleByCond(language,applicationId));
    }

    /**
     * 分页查询 服务端多语言(返回外文描述信息)
     * @param applicationId 应用ID
     * @param sourceLanguage 源语言
     * @param targetLanguage 目标语言
     * @param keyCode 界面key值
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/query/other/serve/locale/by/cond")
    public ResponseEntity<List<LocaleDTO>> getOtherServeLocaleByCond(
            @RequestParam(value = "applicationId") Long applicationId,
            @RequestParam(value = "sourceLanguage") String sourceLanguage,
            @RequestParam(value = "targetLanguage") String targetLanguage,
            @RequestParam(value = "keyCode",required = false) String keyCode,
            Pageable pageable) throws URISyntaxException{
        Page page = PageUtil.getPage(pageable);
        List<LocaleDTO> result = serveLocaleService.getOtherServeLocaleByCond(applicationId,sourceLanguage,targetLanguage,keyCode,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result,httpHeaders, HttpStatus.OK);
    }
}
