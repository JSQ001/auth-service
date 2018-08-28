package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.Interface;
import com.helioscloud.atlantis.dto.InterfaceTreeDTO;
import com.helioscloud.atlantis.service.InterfaceService;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 接口控制类
 */
@RestController
@RequestMapping("/api/interface")
public class InterfaceController {
    private final InterfaceService interfaceService;

    public InterfaceController(InterfaceService interfaceService) {
        this.interfaceService = interfaceService;
    }

    /**
     * @api {POST} /api/interface/create 【系统框架】接口创建
     * @apiDescription 创建接口
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {String} interfaceName 接口名称
     * @apiParam (请求参数) {String} requestProtocol 请求协议
     * @apiParam (请求参数) {String} requestMethod 请求方法
     * @apiParam (请求参数) {String} requestFormat 请求格式
     * @apiParam (请求参数) {Long} moduleId 模块Id
     * @apiParam (请求参数) {String} reqUrl 请求URL
     * @apiParam (请求参数) {String} responseFormat 响应格式
     * @apiParam (请求参数) {String} remark 备注
     * @apiParamExample {json} 请求报文:
     * {
     * "interfaceName":"查询模块",
     * "requestProtocol":"http",
     * "requestMethod":"GET",
     * "requestFormat":"JSON",
     * "moduleId":"1031479997352935426",
     * "reqUrl":"http://localhost:9082/api/module/query",
     * "responseFormat":"JSON",
     * "remark":"test"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} interfaceName 接口名称
     * @apiSuccess (返回参数) {String} requestProtocol 请求协议
     * @apiSuccess (返回参数) {String} requestMethod 请求方法
     * @apiSuccess (返回参数) {String} requestFormat 请求格式
     * @apiSuccess (返回参数) {Long} moduleId 模块Id
     * @apiSuccess (返回参数) {String} reqUrl 请求URL
     * @apiSuccess (返回参数) {String} responseFormat 响应格式
     * @apiSuccess (返回参数) {String} remark 备注
     * @apiSuccess (返回参数) {Boolean} isEnabled    启用标志
     * @apiSuccess (返回参数) {Boolean} isDeleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031509686226259969",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T19:54:08.383+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T19:54:08.383+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "interfaceName": "查询模块",
     * "requestProtocol": "http",
     * "requestMethod": "GET",
     * "requestFormat": "JSON",
     * "reqUrl": "http://localhost:9082/api/module/query",
     * "responseFormat": "JSON",
     * "remark": "test",
     * "moduleId": "1031479997352935426"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<Interface> createInterface(@RequestBody Interface anInterface) {
        return ResponseEntity.ok(interfaceService.createInterface(anInterface));
    }

    /**
     * @api {PUT} /api/interface/update 【系统框架】接口更新
     * @apiDescription 更新接口
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 接口ID
     * @apiParam (请求参数) {String} interfaceName 接口名称
     * @apiParam (请求参数) {String} requestProtocol 请求协议
     * @apiParam (请求参数) {String} requestMethod 请求方法
     * @apiParam (请求参数) {String} requestFormat 请求格式
     * @apiParam (请求参数) {Long} moduleId 模块Id
     * @apiParam (请求参数) {String} reqUrl 请求URL
     * @apiParam (请求参数) {String} responseFormat 响应格式
     * @apiParam (请求参数) {String} remark 备注
     * @apiParam (请求参数) {Integer} versionNumber 版本号
     * @apiParam (请求参数) {String} [isEnabled] 启用标志
     * @apiParam (请求参数) {String} [isDeleted] 删除标志
     * @apiParamExample {json} 请求报文:
     * {
     * "id": "1031548086757134337",
     * "isEnabled": true,
     * "isDeleted": false,
     * "versionNumber": 1,
     * "interfaceName": "查询模块12",
     * "requestProtocol": "http",
     * "requestMethod": "GET",
     * "requestFormat": "JSON",
     * "reqUrl": "http://localhost:9082/api/module/query",
     * "responseFormat": "JSON",
     * "remark": "test124",
     * "moduleId": "1031479997352935426"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} interfaceName 接口名称
     * @apiSuccess (返回参数) {String} requestProtocol 请求协议
     * @apiSuccess (返回参数) {String} requestMethod 请求方法
     * @apiSuccess (返回参数) {String} requestFormat 请求格式
     * @apiSuccess (返回参数) {Long} moduleId 模块Id
     * @apiSuccess (返回参数) {String} reqUrl 请求URL
     * @apiSuccess (返回参数) {String} responseFormat 响应格式
     * @apiSuccess (返回参数) {String} remark 备注
     * @apiSuccess (返回参数) {Boolean} isEnabled    启用标志
     * @apiSuccess (返回参数) {Boolean} isDeleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1029916356543561729",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-16T10:22:48.987+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "roleCode": "R001",
     * "roleName": "测试接口2",
     * "tenantId": "1022057230117146625"
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<Interface> updateInterface(@RequestBody Interface anInterface) {
        return ResponseEntity.ok(interfaceService.updateInterface(anInterface));
    }

    /**
     * @api {DELETE} /api/interface/delete/{id} 【系统框架】接口删除
     * @apiDescription 删除接口
     * @apiGroup SysFrameWork
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/interface/delete/1031548020889784321
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteInterface(@PathVariable Long id) {
        interfaceService.deleteInterface(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/interface/batch/delete 【系统框架】接口批量删除
     * @apiDescription 批量删除接口
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 接口ID
     * @apiParamExample {json} 请求报文
     * [1031548020889784321,1031548086757134337]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/delete")
    public ResponseEntity deleteInterfaceByIds(@RequestBody List<Long> ids) {
        interfaceService.deleteBatchInterface(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/interface/query/{id} 【系统框架】接口查询
     * @apiDescription 查询接口
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 接口ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/interface/query/1031509686226259969
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031509686226259969",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T19:54:08.383+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T19:54:08.383+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "interfaceName": "查询模块",
     * "requestProtocol": "http",
     * "requestMethod": "GET",
     * "requestFormat": "JSON",
     * "reqUrl": "http://localhost:9082/api/module/query",
     * "responseFormat": "JSON",
     * "remark": "test",
     * "moduleId": "1031479997352935426"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<Interface> getInterfaceById(@PathVariable Long id) {
        return ResponseEntity.ok(interfaceService.getInterfaceById(id));
    }

    /**
     * @api {GET} /api/interface/query 【系统框架】接口查询分页
     * @apiDescription 根据模块Id，查询模块下所有接口 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} moduleId 模块ID
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/interface/query?moduleId=1031479997352935426&page=0&size=2
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031509686226259969",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T19:54:08.383+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T19:54:08.383+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "interfaceName": "查询模块",
     * "requestProtocol": "http",
     * "requestMethod": "GET",
     * "requestFormat": "JSON",
     * "reqUrl": "http://localhost:9082/api/module/query",
     * "responseFormat": "JSON",
     * "remark": "test",
     * "moduleId": "1031479997352935426"
     * },
     * {
     * "id": "1031548055836725249",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T22:26:36.416+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T22:26:36.417+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "interfaceName": "查询模块3",
     * "requestProtocol": "http",
     * "requestMethod": "GET",
     * "requestFormat": "JSON",
     * "reqUrl": "http://localhost:9082/api/module/query",
     * "responseFormat": "JSON",
     * "remark": "test3",
     * "moduleId": "1031479997352935426"
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<Interface>> getInterfacesByModuleId(@RequestParam(required = true) Long moduleId,
                                                                   @RequestParam(required = false) Boolean isEnabled,
                                                                   Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<Interface> list = interfaceService.getInterfacesByModuleId(moduleId, isEnabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/interface/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/interface/queryAll 【系统框架】接口查询不分页
     * @apiDescription 根据模块Id，查询模块下所有接口 不分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} moduleId 模块ID
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/interface/queryAll?moduleId=1031479997352935426
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031509686226259969",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T19:54:08.383+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T19:54:08.383+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "interfaceName": "查询模块",
     * "requestProtocol": "http",
     * "requestMethod": "GET",
     * "requestFormat": "JSON",
     * "reqUrl": "http://localhost:9082/api/module/query",
     * "responseFormat": "JSON",
     * "remark": "test",
     * "moduleId": "1031479997352935426"
     * },
     * {
     * "id": "1031548055836725249",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T22:26:36.416+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-20T22:26:36.417+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "interfaceName": "查询模块3",
     * "requestProtocol": "http",
     * "requestMethod": "GET",
     * "requestFormat": "JSON",
     * "reqUrl": "http://localhost:9082/api/module/query",
     * "responseFormat": "JSON",
     * "remark": "test3",
     * "moduleId": "1031479997352935426"
     * }
     * ]
     */
    @GetMapping("/queryAll")
    public ResponseEntity<List<Interface>> getInterfacesAllByModuleId(@RequestParam(required = true) Long moduleId,
                                                                      @RequestParam(required = false) Boolean isEnabled) throws URISyntaxException {
        List<Interface> list = interfaceService.getInterfacesByModuleId(moduleId, isEnabled);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/interface/query/keyword 【系统框架】接口keyword查询不分页
     * @apiDescription 接口查询 模糊查询所有未删除的数据 按 module_id,req_url排序，不分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} [moduleId] 模块ID 不传则不控，传了则按其控制
     * @apiParam (请求参数) {String} keyword 模糊匹配 interfaceName或reqUrl字段
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/interface/query/keyword?keyword=查询
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "moduleName": "公共",
     * "moduleId": "1032105431320297474",
     * "listInterface": [
     * {
     * "id": "1034348505392799745",
     * "isEnabled": true,
     * "isDeleted": null,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "interfaceName": "查询模块1",
     * "requestProtocol": null,
     * "requestMethod": null,
     * "requestFormat": null,
     * "reqUrl": "http://localhost:9082/api/module/query1",
     * "responseFormat": null,
     * "remark": "test1",
     * "moduleId": "1032105431320297474"
     * },
     * {
     * "id": "1034348544152363010",
     * "isEnabled": true,
     * "isDeleted": null,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "interfaceName": "查询模块2",
     * "requestProtocol": null,
     * "requestMethod": null,
     * "requestFormat": null,
     * "reqUrl": "http://localhost:9082/api/module/query2",
     * "responseFormat": null,
     * "remark": "test2",
     * "moduleId": "1032105431320297474"
     * }
     * ]
     * },
     * {
     * "moduleName": "预算",
     * "moduleId": "1032887003941675010",
     * "listInterface": [
     * {
     * "id": "1034348997464350722",
     * "isEnabled": true,
     * "isDeleted": null,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "interfaceName": "预算查询",
     * "requestProtocol": null,
     * "requestMethod": null,
     * "requestFormat": null,
     * "reqUrl": "/api/budget/query",
     * "responseFormat": null,
     * "remark": "budget query",
     * "moduleId": "1032887003941675010"
     * }
     * ]
     * }
     * ]
     */
    @GetMapping("/query/keyword")
    public ResponseEntity<List<InterfaceTreeDTO>> getInterfacesByKeyword(@RequestParam(required = false) String moduleId,
                                                                         @RequestParam(required = true) String keyword) throws URISyntaxException {
        if (StringUtils.isEmpty(keyword)) {
            // 当keyword为空时，不执行查询,直接返回
            return new ResponseEntity(null, HttpStatus.OK);
        }
        List<InterfaceTreeDTO> list = interfaceService.getInterfacesByKeyword(moduleId, keyword);
        return new ResponseEntity(list, HttpStatus.OK);
    }

}
