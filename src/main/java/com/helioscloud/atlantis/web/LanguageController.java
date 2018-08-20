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
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 多语言控制类
 */
@RestController
@RequestMapping("/api/language")
public class LanguageController {
    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    /**
     * @api {POST} /api/language/create 【系统框架】创建多语言
     * @apiDescription 创建多语言
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {String} code 多语言代码
     * @apiParam (请求参数) {String} zhCn 中文
     * @apiParam (请求参数) {String} enUs 英文
     * @apiParam (请求参数) {Long} moduleId 模块ID
     * @apiParamExample {json} 请求报文:
     * {
     * "code":"save",
     * "zhCn":"保存",
     * "enUs":"Save",
     * "moduleId":"1031479997352935426"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} code 多语言代码
     * @apiSuccess (返回参数) {String} zhCn 中文
     * @apiSuccess (返回参数) {String} enUs 英文
     * @apiSuccess (返回参数) {Long} moduleId 模块ID
     * @apiSuccess (返回参数) {Boolean} isEnabled    启用标志
     * @apiSuccess (返回参数) {Boolean} isDeleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031505621731799041",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T19:37:59.33+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T19:37:59.33+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "code": "save",
     * "zhCn": "保存",
     * "enUs": "Save",
     * "moduleId": "1031479997352935426"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<Language> createLanguage(@RequestBody Language language) {
        return ResponseEntity.ok(languageService.createLanguage(language));
    }

    /**
     * @api {PUT} /api/language/update 【系统框架】更新多语言
     * @apiDescription 更新多语言
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 多语言ID
     * @apiParam (请求参数) {String} code 多语言代码 不允许更新
     * @apiParam (请求参数) {String} zhCn 中文
     * @apiParam (请求参数) {String} enUs 英文
     * @apiParam (请求参数) {Long} moduleId 模块ID
     * @apiParam (请求参数) {Integer} versionNumber 版本号
     * @apiParam (请求参数) {String} isEnabled 启用标志
     * @apiParam (请求参数) {String} isDeleted 删除标志
     * @apiParamExample {json} 请求报文:
     * {
     * "id": "1031506174008389633",
     * "isEnabled": false,
     * "isDeleted": false,
     * "versionNumber": 1,
     * "code": "delete",
     * "zhCn": "删除1",
     * "enUs": "Delete",
     * "moduleId": "1031479997352935426"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} code 多语言代码
     * @apiSuccess (返回参数) {String} zhCn 中文
     * @apiSuccess (返回参数) {String} enUs 英文
     * @apiSuccess (返回参数) {Long} moduleId 模块ID
     * @apiSuccess (返回参数) {Boolean} isEnabled    启用标志
     * @apiSuccess (返回参数) {Boolean} isDeleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031506174008389633",
     * "isEnabled": false,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T19:40:11+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "code": "delete",
     * "zhCn": "删除1",
     * "enUs": "Delete",
     * "moduleId": "1031479997352935426"
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<Language> updateLanguage(@RequestBody Language language) {
        return ResponseEntity.ok(languageService.updateLanguage(language));
    }

    /**
     * @api {DELETE} /api/language/delete/{id} 【系统框架】删除多语言
     * @apiDescription 删除多语言
     * @apiGroup SysFrameWork
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/language/delete/1031505621731799041
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteLanguage(@PathVariable Long id) {
        languageService.deleteLanguage(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/language/batch/delete 【系统框架】批量删除多语言
     * @apiDescription 批量删除多语言
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 多语言ID
     * @apiParamExample {json} 请求报文
     * [1031505621731799041,1031506123899039746]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/delete")
    public ResponseEntity deleteLanguageByIds(@RequestBody List<Long> ids) {
        languageService.deleteBatchLanguage(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/language/query/{id} 【系统框架】查询多语言
     * @apiDescription 查询多语言
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 多语言ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/language/query/1031506174008389633
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031506174008389633",
     * "isEnabled": false,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T19:40:11+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T19:40:57.212+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 2,
     * "code": "delete",
     * "zhCn": "删除1",
     * "enUs": "Delete",
     * "moduleId": "1031479997352935426"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<Language> getLanguageById(@PathVariable Long id) {
        return ResponseEntity.ok(languageService.getLanguageById(id));
    }

    /**
     * @api {GET} /api/language/query 【系统框架】查询多语言分页
     * @apiDescription 查询所有多语言 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/language/query?isEnabled=false&page=0&size=10
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031506174008389633",
     * "isEnabled": false,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T19:40:11+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T19:40:57.212+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 2,
     * "code": "delete",
     * "zhCn": "删除1",
     * "enUs": "Delete",
     * "moduleId": "1031479997352935426"
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<Language>> getLanguages(@RequestParam(required = false) Boolean isEnabled,
                                                       Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<Language> list = languageService.getLanguages(isEnabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/language/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/language/query/module 【系统框架】查询多语言分页
     * @apiDescription 根据模块Id， 查询所有多语言 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} moduleId 模块ID
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/language/query/module?moduleId=1031479997352935426&isEnabled=false&page=0&size=10
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031506123899039746",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T19:39:59.053+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T19:39:59.053+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "code": "create",
     * "zhCn": "创建",
     * "enUs": "Create",
     * "moduleId": "1031479997352935426"
     * }
     * ]
     */
    @GetMapping("/query/module")
    public ResponseEntity<List<Language>> getLanguagesByModuleId(@RequestParam(required = true) Long moduleId,
                                                                 @RequestParam(required = false) Boolean isEnabled,
                                                                 Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<Language> list = languageService.getLanguagesByModuleId(moduleId, isEnabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/language/query/module");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }
}
