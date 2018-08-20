package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.Version;
import com.helioscloud.atlantis.service.VersionService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 版本控制类
 */
@RestController
@RequestMapping("/api/version")
public class VersionController {
    private final VersionService versionService;

    public VersionController(VersionService versionService) {
        this.versionService = versionService;
    }

    /**
     * @api {POST} /api/version/create 【系统框架】创建版本
     * @apiDescription 创建版本
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} componentId 组件ID
     * @apiParam (请求参数) {String} remark 备注说明
     * @apiParam (请求参数) {String} contents 内容信息
     * @apiParamExample {json} 请求报文:
     * {
     * "componentId":"1031480667845984258",
     * "remark":"测试版本1",
     * "contents":"测试测试1"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (请求参数) {Long} componentId 组件ID
     * @apiSuccess (请求参数) {String} remark 备注说明
     * @apiSuccess (请求参数) {String} contents 内容信息
     * @apiSuccess (返回参数) {Boolean} isEnabled    启用标志
     * @apiSuccess (返回参数) {Boolean} isDeleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031484643240869889",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T18:14:37.663+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T18:14:37.663+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "remark": "测试版本1",
     * "contents": "测试测试1",
     * "componentId": "1031480667845984258"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<Version> createVersion(@RequestBody Version version) {
        return ResponseEntity.ok(versionService.createVersion(version));
    }

    /**
     * @api {PUT} /api/version/update 【系统框架】更新版本
     * @apiDescription 更新版本
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 版本ID
     * @apiSuccess (请求参数) {Long} componentId 组件ID
     * @apiSuccess (请求参数) {String} remark 备注说明
     * @apiSuccess (请求参数) {String} contents 内容信息
     * @apiParam (请求参数) {Integer} versionNumber 版本号
     * @apiParam (请求参数) {String} isEnabled 启用标志
     * @apiParam (请求参数) {String} isDeleted 删除标志
     * @apiParamExample {json} 请求报文:
     * {
     * "id": "1031484643240869889",
     * "isEnabled": false,
     * "isDeleted": false,
     * "versionNumber": 1,
     * "remark": "测试版本11",
     * "contents": "测试测试1",
     * "componentId": "1031480667845984258"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Long} componentId 组件ID
     * @apiSuccess (返回参数) {String} remark 备注说明
     * @apiSuccess (返回参数) {String} contents 内容信息
     * @apiSuccess (返回参数) {Boolean} isEnabled    启用标志
     * @apiSuccess (返回参数) {Boolean} isDeleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031484643240869889",
     * "isEnabled": false,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T18:14:37.663+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "remark": "测试版本11",
     * "contents": "测试测试1",
     * "componentId": "1031480667845984258"
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<Version> updateVersion(@RequestBody Version version) {
        return ResponseEntity.ok(versionService.updateVersion(version));
    }

    /**
     * @api {DELETE} /api/version/delete/{id} 【系统框架】删除版本
     * @apiDescription 删除版本
     * @apiGroup SysFrameWork
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/version/delete/1031484643240869889
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteVersion(@PathVariable Long id) {
        versionService.deleteVersion(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/version/batch/delete 【系统框架】批量删除版本
     * @apiDescription 批量删除版本
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 版本ID
     * @apiParamExample {json} 请求报文
     * [1031484643240869889,1031485782829072385]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/delete")
    public ResponseEntity deleteVersionByIds(@RequestBody List<Long> ids) {
        versionService.deleteBatchVersion(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/version/query/{id} 【系统框架】查询版本
     * @apiDescription 查询版本
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 版本ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/version/query/1031485826063958018
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031485826063958018",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T18:19:19.671+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T18:19:19.671+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "remark": "测试版本4",
     * "contents": "测试测试4",
     * "componentId": "1031480667845984258"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<Version> getVersionById(@PathVariable Long id) {
        return ResponseEntity.ok(versionService.getVersionById(id));
    }

    /**
     * @api {GET} /api/version/query 【系统框架】查询版本分页
     * @apiDescription 根据组件Id，查询所有版本 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} componentId 组件ID
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/version/query?componentId=1031480667845984258&isEnabled=true&page=0&size=10
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031485802370334721",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T18:19:14.022+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T18:19:14.022+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "remark": "测试版本3",
     * "contents": "测试测试3",
     * "componentId": "1031480667845984258"
     * },
     * {
     * "id": "1031485826063958018",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T18:19:19.671+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T18:19:19.671+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "remark": "测试版本4",
     * "contents": "测试测试4",
     * "componentId": "1031480667845984258"
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<Version>> getVersionsByComponentId(@RequestParam Long componentId,
                                                                  @RequestParam(required = false) Boolean isEnabled,
                                                                  Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<Version> list = versionService.getVersionsByComponentId(componentId, isEnabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/version/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

}
