package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.Request;
import com.helioscloud.atlantis.service.RequestService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 请求控制类
 */
@RestController
@RequestMapping("/api/request")
public class RequestController {
    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    /**
     * @api {POST} /api/request/create 【系统框架】创建请求
     * @apiDescription 创建请求
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {String} name 请求名称
     * @apiParam (请求参数) {String} code 请求代码
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
     * "code":"Query",
     * "parentId":"",
     * "remark":"测试111",
     * "interfaceId":"1031509686226259969"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} name 请求名称
     * @apiSuccess (返回参数) {String} code 请求代码
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
     * "code": "Query",
     * "parentId": 0,
     * "remark": "测试111",
     * "interfaceId": "1031509686226259969"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<Request> createRequest(@RequestBody Request request) {
        return ResponseEntity.ok(requestService.createRequest(request));
    }

    /**
     * @api {PUT} /api/request/update 【系统框架】更新请求
     * @apiDescription 更新请求
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 请求ID
     * @apiParam (请求参数) {String} name 请求名称
     * @apiParam (请求参数) {String} code 请求代码
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
     * "code": "Query",
     * "parentId": 0,
     * "remark": "测试1112121",
     * "interfaceId": "1031509686226259969"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} name 请求名称
     * @apiSuccess (返回参数) {String} code 请求代码
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
     * "code": "Query",
     * "parentId": 0,
     * "remark": "测试1112121",
     * "interfaceId": "1031509686226259969"
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<Request> updateRequest(@RequestBody Request request) {
        return ResponseEntity.ok(requestService.updateRequest(request));
    }

    /**
     * @api {DELETE} /api/request/delete/{id} 【系统框架】删除请求
     * @apiDescription 删除请求
     * @apiGroup SysFrameWork
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/request/delete/1031552701984743426
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteRequest(@PathVariable Long id) {
        requestService.deleteRequest(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/request/batch/delete 【系统框架】批量删除请求
     * @apiDescription 批量删除请求
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 请求ID
     * @apiParamExample {json} 请求报文
     * [1031552701984743426,1031553979095785474]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/delete")
    public ResponseEntity deleteRequestByIds(@RequestBody List<Long> ids) {
        requestService.deleteBatchRequest(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/request/query/{id} 【系统框架】查询请求
     * @apiDescription 查询请求
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 请求ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/request/query/1031554065301315585
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
     * "code": "Query4",
     * "parentId": 0,
     * "remark": "测试444",
     * "interfaceId": "1031509686226259969"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<Request> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.getRequestById(id));
    }

    /**
     * @api {GET} /api/request/query 【系统框架】查询请求分页
     * @apiDescription 根据接口Id，查询接口的所有请求 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} interfaceId 接口ID
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/request/query?interfaceId=1031509686226259969&isEnabled=true&page=0&size=10
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
     * "code": "Query33",
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
     * "code": "Query4",
     * "parentId": 0,
     * "remark": "测试444",
     * "interfaceId": "1031509686226259969"
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<Request>> getRequestsByInterfaceId(@RequestParam(required = true) Long interfaceId,
                                                                  @RequestParam(required = false) Boolean isEnabled,
                                                                  Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<Request> list = requestService.getRequestsByInterfaceId(interfaceId, isEnabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/request/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/request/query/parent 【系统框架】查询请求分页
     * @apiDescription 根据parentId，查询所有子请求 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} parentId 上级ID
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/request/query/parent?parentId=0&isEnabled=true&page=0&size=10
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
     * "code": "Query33",
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
     * "code": "Query4",
     * "parentId": 0,
     * "remark": "测试444",
     * "interfaceId": "1031509686226259969"
     * }
     * ]
     */
    @GetMapping("/query/parent")
    public ResponseEntity<List<Request>> getRequestsByParentId(@RequestParam(required = true) Long parentId,
                                                               @RequestParam(required = false) Boolean isEnabled,
                                                               Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<Request> list = requestService.getRequestsByParentId(parentId, isEnabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/request/query/parent");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }
}
