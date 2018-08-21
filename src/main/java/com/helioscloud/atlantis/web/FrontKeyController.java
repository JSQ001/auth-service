package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.FrontKey;
import com.helioscloud.atlantis.service.FrontKeyService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    public FrontKeyController(FrontKeyService frontKeyService) {
        this.frontKeyService = frontKeyService;
    }

    /**
     * @api {POST} /api/frontKey/create 【系统框架】界面Title创建
     * @apiDescription 创建界面Title
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {String} key 界面Title代码
     * @apiParam (请求参数) {String} lang 中文/英文 zh_CN 中文，en 英文
     * @apiParam (请求参数) {String} descriptions 描述
     * @apiParam (请求参数) {Long} moduleId 模块ID
     * @apiParamExample {json} 请求报文:
     * {
     * "key":"common.save",
     * "lang":"zh_CN",
     * "descriptions":"保存",
     * "moduleId":"1031479997352935426"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} key 界面Title代码
     * @apiSuccess (返回参数) {String} lang 中文/英文 zh_CN 中文，en 英文
     * @apiSuccess (返回参数) {String} descriptions 描述
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
     * "key":"common.save",
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
     * @apiParam (请求参数) {String} key 界面Title代码 不允许更新
     * @apiParam (请求参数) {String} lang 中文/英文 zh_CN 中文，en 英文
     * @apiParam (请求参数) {String} descriptions 描述
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
     * "key":"common.save",
     * "lang":"zh_CN",
     * "descriptions":"保存",
     * "moduleId": "1031479997352935426"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} key 界面Title代码
     * @apiSuccess (返回参数) {String} lang 中文/英文 zh_CN 中文，en 英文
     * @apiSuccess (返回参数) {String} descriptions 描述
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
     * "key":"common.save",
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
    public ResponseEntity deleteFrontKey(@PathVariable Long id) {
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
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-21T17:05:58.473+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-21T17:05:58.473+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "key": "common.save",
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
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/frontKey/query?isEnabled=false&page=0&size=10
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
     * "key":"common.save",
     * "lang":"zh_CN",
     * "descriptions":"保存",
     * "moduleId": "1031479997352935426"
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<FrontKey>> getFrontKeys(@RequestParam(required = false) Boolean isEnabled,
                                                       Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<FrontKey> list = frontKeyService.getFrontKeys(isEnabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/frontKey/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/frontKey/query/module 【系统框架】界面Title查询分页
     * @apiDescription 根据模块Id， 查询所有界面Title 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} moduleId 模块ID
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/frontKey/query/module?moduleId=1031479997352935426&isEnabled=false&page=0&size=10
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
     * "key":"common.save",
     * "lang":"zh_CN",
     * "descriptions":"保存",
     * "moduleId": "1031479997352935426"
     * }
     * ]
     */
    @GetMapping("/query/module")
    public ResponseEntity<List<FrontKey>> getFrontKeysByModuleId(@RequestParam(required = true) Long moduleId,
                                                                 @RequestParam(required = false) Boolean isEnabled,
                                                                 Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<FrontKey> list = frontKeyService.getFrontKeysByModuleId(moduleId, isEnabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/frontKey/query/module");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }
}
