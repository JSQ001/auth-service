package com.hand.hcf.app.base.system.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.system.domain.FrontKey;
import com.hand.hcf.app.base.system.dto.FrontKeyDTO;
import com.hand.hcf.app.base.system.service.FrontKeyService;
import com.hand.hcf.app.base.user.service.UserService;
import com.hand.hcf.core.util.PageUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 界面Title控制类
 */
@RestController
@RequestMapping("/api/frontKey")
public class FrontKeyController {
    private final FrontKeyService frontKeyService;
    private final UserService userService;
    public FrontKeyController(FrontKeyService frontKeyService, UserService userService) {
        this.frontKeyService = frontKeyService;
        this.userService = userService;
    }

    /**
     * @api {POST} /api/frontKey/create 【系统框架】界面Title创建
     * @apiDescription 创建界面Title
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {String} keyCode 界面Title代码
     * @apiParam (请求参数) {String} lang 中文/英文 zh_CN 中文，en 英文
     * @apiParam (请求参数) {String} descriptions 描述
     * @apiParam (请求参数) {Long} moduleId 模块ID
     * @apiParamExample {json} 请求报文:
     * {
     * "keyCode":"common.save",
     * "lang":"zh_CN",
     * "descriptions":"保存",
     * "moduleId":"1031479997352935426"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} keyCode 界面Title代码
     * @apiSuccess (返回参数) {String} lang 中文/英文 zh_CN 中文，en 英文
     * @apiSuccess (返回参数) {String} descriptions 描述
     * @apiSuccess (返回参数) {Long} moduleId 模块ID
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031505621731799041",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T19:37:59.33+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T19:37:59.33+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "keyCode":"common.save",
     * "lang":"zh_CN",
     * "descriptions":"保存",
     * "moduleId": "1031479997352935426"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<FrontKey> createFrontKey(@RequestBody FrontKey frontKey) {
        return ResponseEntity.ok(frontKeyService.createFrontKey(frontKey));
    }

    /**
     * @api {PUT} /api/frontKey/update 【系统框架】界面Title更新
     * @apiDescription 更新界面Title
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 界面TitleID
     * @apiParam (请求参数) {String} keyCode 界面Title代码 不允许更新
     * @apiParam (请求参数) {String} lang 中文/英文 zh_CN 中文，en 英文
     * @apiParam (请求参数) {String} descriptions 描述
     * @apiParam (请求参数) {Long} moduleId 模块ID
     * @apiParam (请求参数) {Integer} versionNumber 版本号
     * @apiParam (请求参数) {String} enabled 启用标志
     * @apiParam (请求参数) {String} deleted 删除标志
     * @apiParamExample {json} 请求报文:
     * {
     * "id": "1031506174008389633",
     * "enabled": false,
     * "deleted": false,
     * "versionNumber": 1,
     * "keyCode":"common.save",
     * "lang":"zh_CN",
     * "descriptions":"保存",
     * "moduleId": "1031479997352935426"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} keyCode 界面Title代码
     * @apiSuccess (返回参数) {String} lang 中文/英文 zh_CN 中文，en 英文
     * @apiSuccess (返回参数) {String} descriptions 描述
     * @apiSuccess (返回参数) {Long} moduleId 模块ID
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031506174008389633",
     * "enabled": false,
     * "deleted": false,
     * "createdDate": "2018-08-20T19:40:11+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "keyCode":"common.save",
     * "lang":"zh_CN",
     * "descriptions":"保存",
     * "moduleId": "1031479997352935426"
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<FrontKey> updateFrontKey(@RequestBody FrontKey frontKey) {
        return ResponseEntity.ok(frontKeyService.updateFrontKey(frontKey));
    }

    /**
     * @api {DELETE} /api/frontKey/delete/{id} 【系统框架】界面Title删除
     * @apiDescription 删除界面Title
     * @apiGroup SysFrameWork
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/frontKey/delete/1031505621731799041
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteFrontKey(@PathVariable Long id) throws Exception {
        frontKeyService.deleteFrontKey(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/frontKey/batch/delete 【系统框架】界面Title批量删除
     * @apiDescription 批量删除界面Title
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 界面TitleID
     * @apiParamExample {json} 请求报文
     * [1031505621731799041,1031506123899039746]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/delete")
    public ResponseEntity deleteBatchFrontKey(@RequestBody List<Long> ids) {
        frontKeyService.deleteBatchFrontKey(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/frontKey/query/{id} 【系统框架】界面Title查询
     * @apiDescription 查询界面Title
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 界面TitleID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/frontKey/query/1031829753933651970
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031829753933651970",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-21T17:05:58.473+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-21T17:05:58.473+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "keyCode": "common.save",
     * "lang": "zh_CN",
     * "descriptions": "保存",
     * "moduleId": "1031479997352935426"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<FrontKey> getFrontKeyById(@PathVariable Long id) {
        return ResponseEntity.ok(frontKeyService.getFrontKeyById(id));
    }

    /**
     * @api {GET} /api/frontKey/query 【系统框架】界面Title查询分页
     * @apiDescription 查询所有界面Title 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Boolean} [enabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/frontKey/query?enabled=false&page=0&size=10
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031506174008389633",
     * "enabled": false,
     * "deleted": false,
     * "createdDate": "2018-08-20T19:40:11+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T19:40:57.212+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 2,
     * "keyCode":"common.save",
     * "lang":"zh_CN",
     * "descriptions":"保存",
     * "moduleId": "1031479997352935426"
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<FrontKey>> getFrontKeys(@RequestParam(required = false) Boolean enabled,
                                                       Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<FrontKey> list = frontKeyService.getFrontKeys(enabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/frontKey/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/frontKey/query/module 【系统框架】界面Title查询分页
     * @apiDescription 根据模块Id， 查询所有界面Title 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} moduleId 模块ID
     * @apiParam (请求参数) {Boolean} [enabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/frontKey/query/module?moduleId=1031479997352935426&enabled=false&page=0&size=10
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031506123899039746",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T19:39:59.053+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T19:39:59.053+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "keyCode":"common.save",
     * "lang":"zh_CN",
     * "descriptions":"保存",
     * "moduleId": "1031479997352935426"
     * }
     * ]
     */
    @GetMapping("/query/module")
    public ResponseEntity<List<FrontKey>> getFrontKeysByModuleId(@RequestParam(required = true) Long moduleId,
                                                                 @RequestParam(required = false) Boolean enabled,
                                                                 Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<FrontKey> list = frontKeyService.getFrontKeysByModuleId(moduleId, enabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/frontKey/query/module");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/frontKey/query/module/lang 【系统框架】界面Title查询分页
     * @apiDescription 根据模块Id，语言lang， 查询所有界面Title 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} moduleId 模块ID
     * @apiParam (请求参数) {String} lang 语言 zh_CN 中文，en 英文
     * @apiParam (请求参数) {Boolean} [enabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/frontKey/query/module/lang?moduleId=1031479997352935426&lang=zh_CN&enabled=false&page=0&size=10
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031506123899039746",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T19:39:59.053+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T19:39:59.053+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "keyCode":"common.save",
     * "lang":"zh_CN",
     * "descriptions":"保存",
     * "moduleId": "1031479997352935426"
     * }
     * ]
     */
    @GetMapping("/query/module/lang")
    public ResponseEntity<List<FrontKey>> getFrontKeysByModuleIdAndLang(@RequestParam(required = true) Long moduleId,
                                                                        @RequestParam(required = true) String lang,
                                                                        @RequestParam(required = false) Boolean enabled,
                                                                        @RequestParam(required = false) String key,
                                                                        @RequestParam(required = false) String descriptions,
                                                                        Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<FrontKey> list = frontKeyService.getFrontKeysByModuleIdAndLang(moduleId, lang, enabled, key, descriptions, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/frontKey/query/module/lang");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/frontKey/query/lang 【系统框架】界面Title查询所有
     * @apiDescription 根据语言lang， 查询所有界面Title 不分页，用于切换多语言
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {String} lang 语言 zh_CN 中文，en 英文
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/frontKey/query/lang?lang=en_US
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1033005159936630785",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-24T22:56:37.094+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": "2018-08-24T22:58:31.903+08:00",
     * "lastUpdatedBy": 1013,
     * "versionNumber": 2,
     * "keyCode": "common.create",
     * "lang": "en_US",
     * "descriptions": "CREATE",
     * "moduleId": "1031479997352935426"
     * },
     * {
     * "id": "1033276764090011650",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-25T16:55:52.575+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-27T10:45:57.769+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 6,
     * "keyCode": "delete",
     * "lang": "en_US",
     * "descriptions": "delete",
     * "moduleId": "1032887003941675010"
     * },
     * {
     * "id": "1033276764161314817",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-25T16:55:52.587+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-27T10:45:57.78+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 6,
     * "keyCode": "save",
     * "lang": "en_US",
     * "descriptions": "Save",
     * "moduleId": "1032887003941675010"
     * },
     * {
     * "id": "1033908447921356802",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-27T10:45:57.734+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-27T10:45:57.734+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "keyCode": "test",
     * "lang": "en_US",
     * "descriptions": "test",
     * "moduleId": "1032887003941675010"
     * }
     * ]
     */
    @GetMapping("/query/lang")
    public ResponseEntity<List<FrontKey>> getFrontKeysByLang(@RequestParam(required = true) String lang) throws URISyntaxException {
        List<FrontKey> list = frontKeyService.getFrontKeysByLang(lang);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/frontKey/sync/{language} 【系统框架】界面Title同步
     * @apiDescription 根据language，将所有中文下未同步到language的界面Title，同步到language里去。
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {String} language 语言代码
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/frontKey/sync/en
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @GetMapping("/sync/{language}")
    public ResponseEntity syncFrontKeyByLanguage(@PathVariable String language) {
        frontKeyService.syncFrontKeyByLanguage(language);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/frontKey/batch/update 【系统框架】界面Title批量更新
     * @apiDescription 批量更新界面的Title的描述信息
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 界面Title的ID
     * @apiParam (请求参数) {String} descriptions 需要更新的描述
     * @apiParamExample {json} 请求报文:
     * [
     * {
     * "id":1032975640932036609,
     * "descriptions":"保存1"
     * },{
     * "id":1032987141747159042,
     * "descriptions":"编辑1"
     * }
     * ]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/update")
    public ResponseEntity batchUpdateFrontKey(@RequestBody List<FrontKeyDTO> list) {
        frontKeyService.batchUpdateFrontKey(list);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/frontKey/batch/create 【系统框架】界面Title批量保存
     * @apiDescription 批量保存界面的Title的描述信息
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {String} keyCode 界面Title代码
     * @apiParam (请求参数) {String} lang 中文/英文 zh_CN 中文，en 英文
     * @apiParam (请求参数) {String} descriptions 描述
     * @apiParam (请求参数) {Long} moduleId 模块ID
     * @apiParamExample {json} 请求报文:
     * [
     * {
     * "keyCode":"common.create",
     * "lang":"zh_CN",
     * "descriptions":"创建",
     * "moduleId":"1031479997352935426"
     * },
     * {
     * "keyCode":"common.create",
     * "lang":"en_US",
     * "descriptions":"CREATE",
     * "moduleId":"1031479997352935426"
     * }
     * ]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/create")
    public ResponseEntity batchCreateFrontKey(@RequestBody List<FrontKey> list) {
        frontKeyService.batchCreateFrontKey(list);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/frontKey/query/keyCode 【系统框架】界面Title查询所有
     * @apiDescription 根据KeyCode，查询界面Title
     * 1) lang 语言，不传则不控制，传了则按传入的值进行控制
     * 2) enabled 启用标识，不传则不控制，传了则按传入的值进行控制
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {String} keyCode 界面Title的keyCode代码
     * @apiParam (请求参数) {String} [lang] 语言 zh_CN 中文，en_US 英文 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Boolean} [enabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/frontKey/query/keyCode?keyCode=common.create&lang=zh_CN&enabled=true
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1033005159668195330",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-24T22:56:37.035+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": "2018-08-24T22:56:37.035+08:00",
     * "lastUpdatedBy": 1013,
     * "versionNumber": 1,
     * "keyCode": "common.create",
     * "lang": "zh_CN",
     * "descriptions": "创建",
     * "moduleId": "1031479997352935426"
     * },
     * {
     * "id": "1033005159936630785",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-24T22:56:37.094+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": "2018-08-24T22:58:31.903+08:00",
     * "lastUpdatedBy": 1013,
     * "versionNumber": 2,
     * "keyCode": "common.create",
     * "lang": "en_US",
     * "descriptions": "CREATE",
     * "moduleId": "1031479997352935426"
     * }
     * ]
     */
    @GetMapping("/query/keyCode")
    public ResponseEntity<List<FrontKey>> getFrontKeyByKeyCodeAndLang(@RequestParam(required = true) String keyCode,
                                                                      @RequestParam(required = false) String lang,
                                                                      @RequestParam(required = false) Boolean enabled) {
        List<FrontKey> list = frontKeyService.getFrontKeyByKeyCodeAndLang(keyCode, lang, enabled);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * @api {POST} /api/frontKey/query/keyword 【系统框架】界面Title查询分页
     * @apiDescription 模糊查询接口，只查未删除且启用状态的数据, 按key_code排序
     * 1) lang 语言，不传则不控制，传了则按传入的值进行控制
     * 2) keyCode 界面Title的keyCode代码，不传则不控制，传了则按传入的值进行控制 模糊查询
     * 3) descriptions 界面Title的描述，不传则不控制，传了则按传入的值进行控制 模糊查询
     * 4) moduleId 模块ID，不传则不控制，传了则按传入的值进行控制
     * 5) keyword 语言，不传则不控制，传了则匹配keyCode或descriptions字段，模糊查询
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {String} [keyCode] 界面Title的keyCode代码 模糊查询
     * @apiParam (请求参数) {String} [descriptions] 界面Title的描述 模糊查询
     * @apiParam (请求参数) {Long} [moduleId] 模块ID
     * @apiParam (请求参数) {String} [keyword] 用于匹配keyCode或descriptions字段，模糊查询
     * @apiParam (请求参数) {String} [lang] 语言 zh_CN 中文，en_US 英文 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} [page] 当前页
     * @apiParam (请求参数) {Integer} [size] 每页条数
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/frontKey/query/keyword?page=0&size=5&keyword=save&moduleId=1031479997352935426&descriptions=保
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031829753933651970",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "keyCode": "common.save",
     * "lang": "zh_CN",
     * "descriptions": "保存",
     * "moduleId": "1031479997352935426"
     * }
     * ]
     */
    @GetMapping("/query/keyword")
    public ResponseEntity<List<FrontKey>> getFrontKeysByCond(@RequestParam(required = false) String keyCode,
                                                             @RequestParam(required = false) String descriptions,
                                                             @RequestParam(required = false) String moduleId,
                                                             @RequestParam(required = false) String lang,
                                                             @RequestParam(required = false) String keyword,
                                                             Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<FrontKey> list = frontKeyService.getFrontKeysByCond(keyCode, descriptions, moduleId, lang, keyword, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/frontKey/query/keyword");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {DELETE} /api/frontKey/es/remove/all 【系统框架】界面Title-移除索引库中全部数据
     * @apiDescription 菜单-移除索引库中全部数据
     * @apiGroup Auth2Service
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/frontKey/es/remove/all
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @RequestMapping(value = "/es/remove/all", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removeAllMenu() throws IOException {
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/frontKey/es/init/all 【系统框架】界面Title-初始化索引库全部数据
     * @apiDescription 菜单-初始化索引库全部数据
     * @apiGroup Auth2Service
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/frontKey/es/init/all
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @RequestMapping(value = "/es/init/all", method = RequestMethod.GET)
    public ResponseEntity<Void> initAllMenu() {
        return ResponseEntity.ok().build();
    }
}
