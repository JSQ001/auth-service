package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.Request;
import com.helioscloud.atlantis.domain.Response;
import com.helioscloud.atlantis.service.ResponseService;
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
@RequestMapping("/api/response")
public class ResponseController {
    private final ResponseService responseService;

    public ResponseController(ResponseService responseService) {
        this.responseService = responseService;
    }

    /**
     * @api {POST} /api/response/create 【系统框架】创建响应
     * @apiDescription 创建响应
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {String} name 响应名称
     * @apiParam (请求参数) {String} code 响应代码
     * @apiParam (请求参数) {String} reqType 响应类型
     * @apiParam (请求参数) {Long} parentId 上级ID
     * @apiParam (请求参数) {String} remark 备注说明
     * @apiParam (请求参数) {Long} interfaceId 接口ID
     * @apiParamExample {json} 请求报文:
     * {
     * "name":"响应测试1",
     * "reqType":"GET",
     * "code":"Query",
     * "parentId":"",
     * "remark":"响应测试1",
     * "interfaceId":"1031509686226259969"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} name 响应名称
     * @apiSuccess (返回参数) {String} code 响应代码
     * @apiSuccess (返回参数) {String} reqType 响应类型
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
     * "id": "1031557007467147265",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T23:02:10.655+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T23:02:10.655+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "name": "响应测试1",
     * "respType": null,
     * "code": "Query",
     * "parentId": 0,
     * "remark": "响应测试1",
     * "interfaceId": "1031509686226259969"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<Response> createResponse(@RequestBody Response response) {
        return ResponseEntity.ok(responseService.createResponse(response));
    }

    /**
     * @api {PUT} /api/response/update 【系统框架】更新响应
     * @apiDescription 更新响应
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 请求ID
     * @apiParam (请求参数) {String} name 响应名称
     * @apiParam (请求参数) {String} code 响应代码
     * @apiParam (请求参数) {String} reqType 响应类型
     * @apiParam (请求参数) {Long} parentId 上级ID
     * @apiParam (请求参数) {String} remark 备注说明
     * @apiParam (请求参数) {Long} interfaceId 接口ID
     * @apiParam (请求参数) {Integer} versionNumber 版本号
     * @apiParam (请求参数) {String} isEnabled 启用标志
     * @apiParam (请求参数) {String} isDeleted 删除标志
     * @apiParamExample {json} 请求报文:
     * {
     * "id": "1031557527476957185",
     * "isEnabled": true,
     * "isDeleted": false,
     * "versionNumber": 1,
     * "name": "响应测试3213214",
     * "respType": null,
     * "code": "Query4",
     * "parentId": 0,
     * "remark": "响应测试321324",
     * "interfaceId": "1031509686226259969"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} name 响应名称
     * @apiSuccess (返回参数) {String} code 响应代码
     * @apiSuccess (返回参数) {String} reqType 响应类型
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
     * "id": "1031557527476957185",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T23:04:14.631+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "name": "响应测试3213214",
     * "respType": null,
     * "code": "Query4",
     * "parentId": 0,
     * "remark": "响应测试321324",
     * "interfaceId": "1031509686226259969"
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<Response> updateResponse(@RequestBody Response response) {
        return ResponseEntity.ok(responseService.updateResponse(response));
    }

    /**
     * @api {DELETE} /api/response/delete/{id} 【系统框架】删除响应
     * @apiDescription 删除响应
     * @apiGroup SysFrameWork
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/response/delete/1031557007467147265
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteResponse(@PathVariable Long id) {
        responseService.deleteResponse(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/response/batch/delete 【系统框架】批量删除响应
     * @apiDescription 批量删除响应
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 请求ID
     * @apiParamExample {json} 请求报文
     * [1031557490659356673,1031557007467147265]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/delete")
    public ResponseEntity deleteResponseByIds(@RequestBody List<Long> ids) {
        responseService.deleteBatchResponse(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/response/query/{id} 【系统框架】查询响应
     * @apiDescription 查询响应
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 响应ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/response/query/1031557490659356673
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031557490659356673",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T23:04:05.855+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T23:04:05.856+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "name": "响应测试3",
     * "respType": null,
     * "code": "Query3",
     * "parentId": 0,
     * "remark": "响应测试3",
     * "interfaceId": "1031509686226259969"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<Response> getResponseById(@PathVariable Long id) {
        return ResponseEntity.ok(responseService.getResponseById(id));
    }

    /**
     * @api {GET} /api/response/query/{id} 【系统框架】查询响应分页
     * @apiDescription 根据接口Id，查询接口下所有响应 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} interfaceId 接口ID
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/response/query?interfaceId=1031509686226259969&isEnabled=true&page=0&size=10
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031557449832001537",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T23:03:56.117+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T23:03:56.118+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "name": "响应测试2",
     * "respType": null,
     * "code": "Query2",
     * "parentId": 0,
     * "remark": "响应测试2",
     * "interfaceId": "1031509686226259969"
     * },
     * {
     * "id": "1031557490659356673",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T23:04:05.855+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T23:04:05.856+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "name": "响应测试3",
     * "respType": null,
     * "code": "Query3",
     * "parentId": 0,
     * "remark": "响应测试3",
     * "interfaceId": "1031509686226259969"
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<Response>> getResponsesByInterfaceId(@RequestParam(required = true) Long interfaceId,
                                                                    @RequestParam(required = false) Boolean isEnabled,
                                                                    Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<Response> list = responseService.getResponsesByInterfaceId(interfaceId, isEnabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/response/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/response/query/parent 【系统框架】查询响应分页
     * @apiDescription 根据parentId，查询所有子响应 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} parentId 上级ID
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/response/query/parent?parentId=0&isEnabled=true&page=0&size=10
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031557449832001537",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T23:03:56.117+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T23:03:56.118+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "name": "响应测试2",
     * "respType": null,
     * "code": "Query2",
     * "parentId": 0,
     * "remark": "响应测试2",
     * "interfaceId": "1031509686226259969"
     * },
     * {
     * "id": "1031557490659356673",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T23:04:05.855+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T23:04:05.856+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "name": "响应测试3",
     * "respType": null,
     * "code": "Query3",
     * "parentId": 0,
     * "remark": "响应测试3",
     * "interfaceId": "1031509686226259969"
     * }
     * ]
     */
    @GetMapping("/query/parent")
    public ResponseEntity<List<Request>> getResponsesByParentId(@RequestParam(required = true) Long parentId,
                                                                @RequestParam(required = false) Boolean isEnabled,
                                                                Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<Response> list = responseService.getResponsesByParentId(parentId, isEnabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/response/query/parent");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }
}
