package com.hand.hcf.app.base.system.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.system.domain.FrontLocale;
import com.hand.hcf.app.base.system.dto.LocaleDTO;
import com.hand.hcf.app.base.system.service.FrontLocaleService;
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
 * @date: 2019/3/12
 */
@RestController
@RequestMapping("/api/front/locale")
public class FrontLocaleController {
    private final FrontLocaleService frontLocaleService;

    public FrontLocaleController(FrontLocaleService frontLocaleService){
        this.frontLocaleService = frontLocaleService;
    }

    /**
     * 单个新增 中控多语言
     * @param frontLocale
     * @return
     */
    @PostMapping
    public ResponseEntity<FrontLocale> createFrontLocale(@RequestBody FrontLocale frontLocale){
        return ResponseEntity.ok(frontLocaleService.createFrontLocale(frontLocale));
    }

    /**
     * 批量新增 中控多语言
     * @param list
     * @return
     */
    @PostMapping("/batch")
    public ResponseEntity<List<FrontLocale>> createFrontLocaleBatch(@RequestBody List<FrontLocale> list){
        return ResponseEntity.ok(frontLocaleService.createFrontLocaleBatch(list));
    }

    /**
     * 单个编辑 中控多语言
     * @param frontLocale
     * @return
     */
    @PutMapping
    public ResponseEntity<FrontLocale> updateFrontLocale(@RequestBody FrontLocale frontLocale){
        return ResponseEntity.ok(frontLocaleService.updateFrontLocale(frontLocale));
    }

    /**
     * 批量编辑 中控多语言
     * @param list
     * @return
     */
    @PutMapping("/batch")
    public ResponseEntity<List<FrontLocale>> updateFrontLocaleBatch(@RequestBody List<FrontLocale> list){
        return ResponseEntity.ok(frontLocaleService.updateFrontLocaleBatch(list));
    }

    /**
     * 单个删除 中控多语言
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteFrontLocaleById(@PathVariable Long id){
        frontLocaleService.deleteFrontLocaleById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 单个查询 中控多语言
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<FrontLocale> getFrontLocaleById(@PathVariable Long id){
        return ResponseEntity.ok(frontLocaleService.getFrontLocaleById(id));
    }

    /**
     * 分页查询 中控多语言
     * @param language 语言
     * @param applicationId 应用ID
     * @param keyCode 界面key值
     * @param keyDescription key描述
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/query/by/cond")
    public ResponseEntity<List<FrontLocale>> getFrontLocaleByCond(
            @RequestParam(value = "lang") String language,
            @RequestParam(value = "applicationId") Long applicationId,
            @RequestParam(value = "keyCode",required = false) String keyCode,
            @RequestParam(value = "keyDescription",required = false) String keyDescription,
            Pageable pageable) throws URISyntaxException{
        Page page = PageUtil.getPage(pageable);
        List<FrontLocale> result = frontLocaleService.getFrontLocaleByCond(language,applicationId,keyCode,keyDescription,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * 不分页查询 map形式的中控多语言
     * @param language
     * @param applicationId
     * @return
     */
    @GetMapping("/query/map/by/cond")
    public ResponseEntity<Map<String,String>> mapFrontLocaleByCond(
            @RequestParam(value = "lang") String language,
            @RequestParam(value = "applicationId") Long applicationId){
        return ResponseEntity.ok(frontLocaleService.mapFrontLocaleByCond(language,applicationId));
    }

    /**
     * 分页查询 中控多语言(返回外文描述信息)
     * @param applicationId 应用ID
     * @param sourceLanguage 源语言
     * @param targetLanguage 目标语言
     * @param keyCode 界面key值
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/query/other/front/locale/by/cond")
    public ResponseEntity<List<LocaleDTO>> getOtherFrontLocaleByCond(
            @RequestParam(value = "applicationId") Long applicationId,
            @RequestParam(value = "sourceLanguage") String sourceLanguage,
            @RequestParam(value = "targetLanguage") String targetLanguage,
            @RequestParam(value = "keyCode",required = false) String keyCode,
            Pageable pageable) throws URISyntaxException{
        Page page = PageUtil.getPage(pageable);
        List<LocaleDTO> result = frontLocaleService.getOtherFrontLocaleByCond(applicationId,sourceLanguage,targetLanguage,keyCode,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result,httpHeaders, HttpStatus.OK);
    }
}
