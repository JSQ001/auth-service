package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.InterfaceRequest;
import com.helioscloud.atlantis.domain.InterfaceResponse;
import com.helioscloud.atlantis.service.InterfaceResponseService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 接口响应控制类
 */
@RestController
@RequestMapping("/api/interfaceResponse")
public class InterfaceResponseController {
    private final InterfaceResponseService interfaceResponseService;

    public InterfaceResponseController(InterfaceResponseService interfaceResponseService) {
        this.interfaceResponseService = interfaceResponseService;
    }

    /**
     * @api {POST} /api/interfaceResponse/create 【系统框架】接口响应创建
     * @apiDescription 创建接口响应
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {String} name 响应名称
     * @apiParam (请求参数) {String} keyCode 响应代码
     * @apiParam (请求参数) {String} reqType 响应类型
     * @apiParam (请求参数) {Long} parentId 上级ID
     * @apiParam (请求参数) {String} remark 备注说明
     * @apiParam (请求参数) {Long} interfaceId 接口ID
     * @apiParamExample {json} 请求报文:
     * {
     * "name":"响应测试1",
     * "reqType":"GET",
     * "keyCode":"Query",
     * "parentId":"",
     * "remark":"响应测试1",
     * "interfaceId":"1031509686226259969"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} name 响应名称
     * @apiSuccess (返回参数) {String} keyCode 响应代码
     * @apiSuccess (返回参数) {String} reqType 响应类型
     * @apiSuccess (返回参数) {Long} parentId 上级ID
     * @apiSuccess (返回参数) {String} remark 备注说明
     * @apiSuccess (返回参数) {Long} interfaceId 接口ID
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031557007467147265",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T23:02:10.655+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T23:02:10.655+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "name": "响应测试1",
     * "respType": null,
     * "keyCode": "Query",
     * "parentId": 0,
     * "remark": "响应测试1",
     * "interfaceId": "1031509686226259969"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<InterfaceResponse> createInterfaceResponse(@RequestBody InterfaceResponse interfaceResponse) {
        return ResponseEntity.ok(interfaceResponseService.createInterfaceResponse(interfaceResponse));
    }

    /**
     * @api {PUT} /api/interfaceResponse/update 【系统框架】接口响应更新
     * @apiDescription 更新接口响应
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 请求ID
     * @apiParam (请求参数) {String} name 响应名称
     * @apiParam (请求参数) {String} keyCode 响应代码
     * @apiParam (请求参数) {String} reqType 响应类型
     * @apiParam (请求参数) {Long} parentId 上级ID
     * @apiParam (请求参数) {String} remark 备注说明
     * @apiParam (请求参数) {Long} interfaceId 接口ID
     * @apiParam (请求参数) {Integer} versionNumber 版本号
     * @apiParam (请求参数) {String} enabled 启用标志
     * @apiParam (请求参数) {String} deleted 删除标志
     * @apiParamExample {json} 请求报文:
     * {
     * "id": "1031557527476957185",
     * "enabled": true,
     * "deleted": false,
     * "versionNumber": 1,
     * "name": "响应测试3213214",
     * "respType": null,
     * "keyCode": "Query4",
     * "parentId": 0,
     * "remark": "响应测试321324",
     * "interfaceId": "1031509686226259969"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} name 响应名称
     * @apiSuccess (返回参数) {String} keyCode 响应代码
     * @apiSuccess (返回参数) {String} reqType 响应类型
     * @apiSuccess (返回参数) {Long} parentId 上级ID
     * @apiSuccess (返回参数) {String} remark 备注说明
     * @apiSuccess (返回参数) {Long} interfaceId 接口ID
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031557527476957185",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T23:04:14.631+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "name": "响应测试3213214",
     * "respType": null,
     * "keyCode": "Query4",
     * "parentId": 0,
     * "remark": "响应测试321324",
     * "interfaceId": "1031509686226259969"
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<InterfaceResponse> updateInterfaceResponse(@RequestBody InterfaceResponse interfaceResponse) {
        return ResponseEntity.ok(interfaceResponseService.updateInterfaceResponse(interfaceResponse));
    }

    /**
     * @api {DELETE} /api/interfaceResponse/delete/{id} 【系统框架】接口响应删除
     * @apiDescription 删除接口响应
     * @apiGroup SysFrameWork
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/interfaceResponse/delete/1031557007467147265
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteInterfaceResponse(@PathVariable Long id) {
        interfaceResponseService.deleteInterfaceResponse(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/interfaceResponse/batch/delete 【系统框架】接口响应批量删除
     * @apiDescription 批量删除接口响应
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 请求ID
     * @apiParamExample {json} 请求报文
     * [1031557490659356673,1031557007467147265]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/delete")
    public ResponseEntity deleteBatchInterfaceResponse(@RequestBody List<Long> ids) {
        interfaceResponseService.deleteBatchInterfaceResponse(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/interfaceResponse/query/{id} 【系统框架】接口响应查询
     * @apiDescription 查询接口响应
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 响应ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/interfaceResponse/query/1031557490659356673
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031557490659356673",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T23:04:05.855+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T23:04:05.856+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "name": "响应测试3",
     * "respType": null,
     * "keyCode": "Query3",
     * "parentId": 0,
     * "remark": "响应测试3",
     * "interfaceId": "1031509686226259969"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<InterfaceResponse> getInterfaceResponseById(@PathVariable Long id) {
        return ResponseEntity.ok(interfaceResponseService.getInterfaceResponseById(id));
    }

    /**
     * @api {GET} /api/interfaceResponse/query 【系统框架】接口响应查询分页
     * @apiDescription 根据接口Id，查询接口下所有接口响应 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} interfaceId 接口ID
     * @apiParam (请求参数) {Boolean} [enabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/interfaceResponse/query?interfaceId=1031509686226259969&enabled=true&page=0&size=10
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031557449832001537",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T23:03:56.117+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T23:03:56.118+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "name": "响应测试2",
     * "respType": null,
     * "keyCode": "Query2",
     * "parentId": 0,
     * "remark": "响应测试2",
     * "interfaceId": "1031509686226259969"
     * },
     * {
     * "id": "1031557490659356673",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T23:04:05.855+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T23:04:05.856+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "name": "响应测试3",
     * "respType": null,
     * "keyCode": "Query3",
     * "parentId": 0,
     * "remark": "响应测试3",
     * "interfaceId": "1031509686226259969"
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<InterfaceResponse>> getInterfaceResponsesByInterfaceId(@RequestParam(required = true) Long interfaceId,
                                                                             @RequestParam(required = false) Boolean enabled,
                                                                             Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<InterfaceResponse> list = interfaceResponseService.getInterfaceResponsesByInterfaceId(interfaceId, enabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/interfaceResponse/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/interfaceResponse/query/parent 【系统框架】接口响应查询分页
     * @apiDescription 根据parentId，查询所有子接口响应 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} parentId 上级ID
     * @apiParam (请求参数) {Boolean} [enabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/interfaceResponse/query/parent?parentId=0&enabled=true&page=0&size=10
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031557449832001537",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T23:03:56.117+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T23:03:56.118+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "name": "响应测试2",
     * "respType": null,
     * "keyCode": "Query2",
     * "parentId": 0,
     * "remark": "响应测试2",
     * "interfaceId": "1031509686226259969"
     * },
     * {
     * "id": "1031557490659356673",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T23:04:05.855+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T23:04:05.856+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "name": "响应测试3",
     * "respType": null,
     * "keyCode": "Query3",
     * "parentId": 0,
     * "remark": "响应测试3",
     * "interfaceId": "1031509686226259969"
     * }
     * ]
     */
    @GetMapping("/query/parent")
    public ResponseEntity<List<InterfaceRequest>> getInterfaceResponsesByParentId(@RequestParam(required = true) Long parentId,
                                                                         @RequestParam(required = false) Boolean enabled,
                                                                         Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<InterfaceResponse> list = interfaceResponseService.getInterfaceResponsesByParentId(parentId, enabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/interfaceResponse/query/parent");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }
    /**
     * @api {POST} /api/interfaceResponse/batch/saveOrUpdate 【系统框架】接口响应批量保存和更新
     * @apiDescription 如果ID有值，则认为是更新,没有，则认为是保存
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} [id] 响应Id ,如果ID有值，则认为是更新
     * @apiParam (请求参数) {String} name 响应名称
     * @apiParam (请求参数) {String} keyCode 响应代码
     * @apiParam (请求参数) {String} reqType 响应类型
     * @apiParam (请求参数) {Long} parentId 上级ID
     * @apiParam (请求参数) {String} remark 备注说明
     * @apiParam (请求参数) {Long} interfaceId 接口ID
     * @apiParam (请求参数) {Boolean} enabledSearch 是否启用搜索
     * @apiParamExample {json} 请求报文
     * [
     * {
     * "name":"响应测试11",
     * "reqType":"GET",
     * "keyCode":"Query1",
     * "parentId":"",
     * "remark":"响应测试1",
     * "interfaceId":"1039725656710897665",
     * "enabledSearch":false,
     * "id": "1039776322100203522"
     * },
     * {
     * "name":"响应测试2",
     * "reqType":"GET",
     * "keyCode":"Query2",
     * "parentId":"",
     * "remark":"响应测试1",
     * "enabledSearch":false,
     * "interfaceId":"1039725656710897665"
     * }
     * ]
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1039776322100203522",
     * "createdDate": "2018-09-12T15:22:48+08:00",
     * "createdBy": "177605",
     * "lastUpdatedDate": "2018-09-12T15:23:37.064+08:00",
     * "lastUpdatedBy": "177605",
     * "versionNumber": null,
     * "deleted": false,
     * "enabled": true,
     * "name": "响应测试11",
     * "respType": null,
     * "keyCode": null,
     * "parentId": "0",
     * "remark": "响应测试1",
     * "interfaceId": "1039725656710897665",
     * "enabledSearch": false
     * },
     * {
     * "id": "1039776528292188162",
     * "createdDate": "2018-09-12T15:23:37.116+08:00",
     * "createdBy": "177605",
     * "lastUpdatedDate": "2018-09-12T15:23:37.116+08:00",
     * "lastUpdatedBy": "177605",
     * "versionNumber": 1,
     * "deleted": false,
     * "enabled": true,
     * "name": "响应测试2",
     * "respType": null,
     * "keyCode": null,
     * "parentId": "0",
     * "remark": "响应测试1",
     * "interfaceId": "1039725656710897665",
     * "enabledSearch": false
     * }
     * ]
     */
    @PostMapping("/batch/saveOrUpdate")
    public ResponseEntity<List<InterfaceResponse>> batchSaveOrUpdateInterfaceResponse(@RequestBody List<InterfaceResponse> responseList) {
        List<InterfaceResponse> list = interfaceResponseService.batchSaveOrUpdateInterfaceResponse(responseList);
        return new ResponseEntity(list, HttpStatus.OK);
    }
}
