package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.Language;
import com.helioscloud.atlantis.service.LanguageService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/14.
 * 语言控制类
 */
@RestController
@RequestMapping("/api/language")
public class LanguageController {
    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    /**
     * @api {POST} /api/language/create 【系统框架】语言创建
     * @apiDescription 语言创建
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {String} language 语言代码
     * @apiParam (请求参数) {String} languageName 语言名称
     * @apiParamExample {json} 请求报文:
     * {
     * "language":"en_US",
     * "languageName":"英文"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} language 语言代码
     * @apiSuccess (返回参数) {String} languageName 语言名称
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": 1031817188029534209,
     * "language": "en_US",
     * "languageName": "英文"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<Language> createLanguage(@RequestBody Language language) {
        return ResponseEntity.ok(languageService.createLanguage(language));
    }

    /**
     * @api {GET} /api/language/query/{id} 【系统框架】语言查询
     * @apiDescription 语言查询
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 角色ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/language/query/1031817188029534209
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": 1031817188029534209,
     * "language": "en_US",
     * "languageName": "英文"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<Language> getLanguageById(@PathVariable Long id) {
        return ResponseEntity.ok(languageService.getLanguageById(id));
    }

    /**
     * @api {GET} /api/language/query 【系统框架】语言查询分页
     * @apiDescription 所有语言 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/language/query?page=0&size=10
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": 1031817188029534209,
     * "language": "en_US",
     * "languageName": "英文"
     * },
     * {
     * "id": 1031822658765099010,
     * "language": "zh_CN",
     * "languageName": "中文"
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<Language>> getLanguages(Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<Language> list = languageService.getLanguages(page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/language/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

}
