package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.InterfaceRequest;
import com.helioscloud.atlantis.service.InterfaceRequestService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 接口请求控制类
 */
@RestController
@RequestMapping("/api/interfaceRequest")
public class InterfaceRequestController {
    private final InterfaceRequestService interfaceRequestService;

    public InterfaceRequestController(InterfaceRequestService interfaceRequestService) {
        this.interfaceRequestService = interfaceRequestService;
    }

    /**
     * @api {POST} /api/interfaceRequest/create 【系统框架】接口请求创建
     * @apiDescription 创建接口请求
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {String} name 请求名称
     * @apiParam (请求参数) {String} key 请求代码
     * @apiParam (请求参数) {String} reqType 请求类型
     * @apiParam (请求参数) {String} position 位置
     * @apiParam (请求参数) {Long} parentId 上级ID
     * @apiParam (请求参数) {String} remark 备注说明
     * @apiParam (请求参数) {Long} interfaceId 接口ID
     * @apiParamExample {json} 请求报文:
     * {
     * "name":"Query模块",
     * "reqType":"GET",
     * "position":"header",
     * "key":"Query",
     * "parentId":"",
     * "remark":"测试111",
     * "interfaceId":"1031509686226259969"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} name 请求名称
     * @apiSuccess (返回参数) {String} key 请求代码
     * @apiSuccess (返回参数) {String} reqType 请求类型
     * @apiSuccess (返回参数) {String} position 位置
     * @apiSuccess (返回参数) {Long} parentId 上级ID
     * @apiSuccess (返回参数) {String} remark 备注说明
     * @apiSuccess (返回参数) {Long} interfaceId 接口ID
     * @apiSuccess (返回参数) {Boolean} isEnabled    启用标志
     * @apiSuccess (返回参数) {Boolean} isDeleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031552701984743426",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T22:45:04.15+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T22:45:04.15+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "name": "Query模块",
     * "reqType": "GET",
     * "position": "header",
     * "key": "Query",
     * "parentId": 0,
     * "remark": "测试111",
     * "interfaceId": "1031509686226259969"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<InterfaceRequest> createInterfaceRequest(@RequestBody InterfaceRequest interfaceRequest) {
        return ResponseEntity.ok(interfaceRequestService.createInterfaceRequest(interfaceRequest));
    }

    /**
     * @api {PUT} /api/interfaceRequest/update 【系统框架】接口请求更新
     * @apiDescription 更新接口请求
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 请求ID
     * @apiParam (请求参数) {String} name 请求名称
     * @apiParam (请求参数) {String} key 请求代码
     * @apiParam (请求参数) {String} reqType 请求类型
     * @apiParam (请求参数) {String} position 位置
     * @apiParam (请求参数) {Long} parentId 上级ID
     * @apiParam (请求参数) {String} remark 备注说明
     * @apiParam (请求参数) {Long} interfaceId 接口ID
     * @apiParam (请求参数) {Integer} versionNumber 版本号
     * @apiParam (请求参数) {String} [isEnabled] 启用标志
     * @apiParam (请求参数) {String} [isDeleted] 删除标志
     * @apiParamExample {json} 请求报文:
     * {
     * "id": "1031552701984743426",
     * "isEnabled": true,
     * "isDeleted": false,
     * "versionNumber": 1,
     * "name": "Query模块",
     * "reqType": "GET",
     * "position": "header",
     * "key": "Query",
     * "parentId": 0,
     * "remark": "测试1112121",
     * "interfaceId": "1031509686226259969"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} name 请求名称
     * @apiSuccess (返回参数) {String} key 请求代码
     * @apiSuccess (返回参数) {String} reqType 请求类型
     * @apiSuccess (返回参数) {String} position 位置
     * @apiSuccess (返回参数) {Long} parentId 上级ID
     * @apiSuccess (返回参数) {String} remark 备注说明
     * @apiSuccess (返回参数) {Long} interfaceId 接口ID
     * @apiSuccess (返回参数) {Boolean} isEnabled    启用标志
     * @apiSuccess (返回参数) {Boolean} isDeleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031552701984743426",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T22:45:04.15+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "name": "Query模块",
     * "reqType": "GET",
     * "position": "header",
     * "key": "Query",
     * "parentId": 0,
     * "remark": "测试1112121",
     * "interfaceId": "1031509686226259969"
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<InterfaceRequest> updateInterfaceRequest(@RequestBody InterfaceRequest interfaceRequest) {
        return ResponseEntity.ok(interfaceRequestService.updateInterfaceRequest(interfaceRequest));
    }

    /**
     * @api {DELETE} /api/interfaceRequest/delete/{id} 【系统框架】接口请求删除
     * @apiDescription 删除接口请求
     * @apiGroup SysFrameWork
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/interfaceRequest/delete/1031552701984743426
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteInterfaceRequest(@PathVariable Long id) {
        interfaceRequestService.deleteInterfaceRequest(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/interfaceRequest/batch/delete 【系统框架】接口请求批量删除
     * @apiDescription 批量删除接口请求
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 请求ID
     * @apiParamExample {json} 请求报文
     * [1031552701984743426,1031553979095785474]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/delete")
    public ResponseEntity deleteBatchInterfaceRequest(@RequestBody List<Long> ids) {
        interfaceRequestService.deleteBatchInterfaceRequest(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/interfaceRequest/query/{id} 【系统框架】接口请求查询
     * @apiDescription 查询接口请求
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 请求ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/interfaceRequest/query/1031554065301315585
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031554065301315585",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T22:50:29.179+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T22:50:29.179+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "name": "Query模块4",
     * "reqType": "GET",
     * "position": "header",
     * "key": "Query4",
     * "parentId": 0,
     * "remark": "测试444",
     * "interfaceId": "1031509686226259969"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<InterfaceRequest> getInterfaceRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(interfaceRequestService.getInterfaceRequestById(id));
    }

    /**
     * @api {GET} /api/interfaceRequest/query 【系统框架】接口请求查询分页
     * @apiDescription 根据接口Id，查询接口的所有请求 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} interfaceId 接口ID
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/interfaceRequest/query?interfaceId=1031509686226259969&isEnabled=true&page=0&size=10
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031554013409386498",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T22:50:16.807+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T22:50:16.807+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "name": "Query模块3",
     * "reqType": "GET",
     * "position": "header",
     * "key": "Query33",
     * "parentId": 0,
     * "remark": "测试2333",
     * "interfaceId": "1031509686226259969"
     * },
     * {
     * "id": "1031554065301315585",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T22:50:29.179+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T22:50:29.179+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "name": "Query模块4",
     * "reqType": "GET",
     * "position": "header",
     * "key": "Query4",
     * "parentId": 0,
     * "remark": "测试444",
     * "interfaceId": "1031509686226259969"
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<InterfaceRequest>> getInterfaceRequestsByInterfaceId(@RequestParam(required = true) Long interfaceId,
                                                                           @RequestParam(required = false) Boolean isEnabled,
                                                                           Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<InterfaceRequest> list = interfaceRequestService.getInterfaceRequestsByInterfaceId(interfaceId, isEnabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/interfaceRequest/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/interfaceRequest/query/parent 【系统框架】接口请求查询分页
     * @apiDescription 根据parentId，查询所有子接口请求 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} parentId 上级ID
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/interfaceRequest/query/parent?parentId=0&isEnabled=true&page=0&size=10
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031554013409386498",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T22:50:16.807+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T22:50:16.807+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "name": "Query模块3",
     * "reqType": "GET",
     * "position": "header",
     * "key": "Query33",
     * "parentId": 0,
     * "remark": "测试2333",
     * "interfaceId": "1031509686226259969"
     * },
     * {
     * "id": "1031554065301315585",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T22:50:29.179+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T22:50:29.179+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "name": "Query模块4",
     * "reqType": "GET",
     * "position": "header",
     * "key": "Query4",
     * "parentId": 0,
     * "remark": "测试444",
     * "interfaceId": "1031509686226259969"
     * }
     * ]
     */
    @GetMapping("/query/parent")
    public ResponseEntity<List<InterfaceRequest>> getInterfaceRequestsByParentId(@RequestParam(required = true) Long parentId,
                                                                        @RequestParam(required = false) Boolean isEnabled,
                                                                        Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<InterfaceRequest> list = interfaceRequestService.getInterfaceRequestsByParentId(parentId, isEnabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/interfaceRequest/query/parent");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }
}
