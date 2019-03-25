package com.hand.hcf.app.base.system.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.system.domain.LanguageEnabled;
import com.hand.hcf.app.base.system.service.LanguageEnabledService;
import com.hand.hcf.core.util.PageUtil;
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
@RequestMapping("/api/languageEnabled")
public class LanguageEnabledController {
    private final LanguageEnabledService languageEnabledService;

    public LanguageEnabledController(LanguageEnabledService languageEnabledService) {
        this.languageEnabledService = languageEnabledService;
    }

    /**
     * @api {POST} /api/languageEnabled/create 【系统框架】语言启用
     * @apiDescription 语言启用
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {String} language 语言代码
     * @apiParamExample {json} 请求报文:
     * {
     * "language":"en_US"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} language 语言代码
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031820520131432450",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-21T16:29:16.959+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-21T16:29:16.959+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "language": "en_US"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<LanguageEnabled> createLanguageEnabled(@RequestBody LanguageEnabled language) {
        return ResponseEntity.ok(languageEnabledService.createLanguageEnabled(language));
    }

    /**
     * @api {GET} /api/languageEnabled/query/{id} 【系统框架】语言启用查询
     * @apiDescription 查询启用语言
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 启用语言ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/languageEnabled/query/1031820520131432450
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031820520131432450",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-21T16:29:16.959+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-21T16:29:16.959+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "language": "en_US"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<LanguageEnabled> getLanguageEnabledById(@PathVariable Long id) {
        return ResponseEntity.ok(languageEnabledService.getLanguageEnabledById(id));
    }

    /**
     * @api {GET} /api/languageEnabled/query 【系统框架】语言启用查询分页
     * @apiDescription 所有启用语言 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/languageEnabled/query?page=0&size=10
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-21T16:09:32+08:00",
     * "createdBy": 1,
     * "lastUpdatedDate": "2018-08-21T16:09:32+08:00",
     * "lastUpdatedBy": 1,
     * "versionNumber": 1,
     * "language": "zh_cn"
     * },
     * {
     * "id": "1031820520131432450",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-21T16:29:16.959+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-21T16:29:16.959+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "language": "en_US"
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<LanguageEnabled>> getLanguageEnabled(Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<LanguageEnabled> list = languageEnabledService.getAllLanguageEnabled(page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/languageEnabled/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

}
