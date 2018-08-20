package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.Component;
import com.helioscloud.atlantis.service.ComponentService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 组件控制类
 */
@RestController
@RequestMapping("/api/component")
public class ComponentController {
    private final ComponentService componentService;

    public ComponentController(ComponentService componentService) {
        this.componentService = componentService;
    }

    /**
     * @api {POST} /api/component/create 【系统框架】创建组件
     * @apiDescription 创建组件
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {String} componentType 组件类型 1 为组件，2为界面
     * @apiParam (请求参数) {String} componentName 组件名称
     * @apiParam (请求参数) {Long} moduleId 模块ID
     * @apiParamExample {json} 请求报文:
     * {
     * "componentType":1,
     * "componentName":"测试组件1",
     * "moduleId":"1031479997352935426"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} componentType 组件类型 1 为组件，2为界面
     * @apiSuccess (返回参数) {String} componentName 组件名称
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
     * "id": "1031480144728195074",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T17:56:45.135+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T17:56:45.135+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "componentType": "1",
     * "componentName": "测试组件1",
     * "moduleId": "1031479997352935426"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<Component> createComponent(@RequestBody Component component) {
        return ResponseEntity.ok(componentService.createComponent(component));
    }

    /**
     * @api {PUT} /api/component/update 【系统框架】更新组件
     * @apiDescription 更新组件
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 组件ID
     * @apiParam (请求参数) {String} componentType 组件类型 1 为组件，2为界面
     * @apiParam (请求参数) {String} componentName 组件名称
     * @apiParam (请求参数) {Long} moduleId 模块ID
     * @apiParam (请求参数) {Integer} versionNumber 版本号
     * @apiParam (请求参数) {String} isEnabled 启用标志
     * @apiParam (请求参数) {String} isDeleted 删除标志
     * @apiParamExample {json} 请求报文:
     * {
     * "id": "1031480700163096577",
     * "isEnabled": false,
     * "isDeleted": false,
     * "versionNumber": 1,
     * "componentType": "2",
     * "componentName": "测试组件4",
     * "moduleId": "1031479997352935426"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} componentType 组件类型 1 为组件，2为界面
     * @apiSuccess (返回参数) {String} componentName 组件名称
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
     * "id": "1031480700163096577",
     * "isEnabled": false,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T17:58:57.561+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "componentType": "2",
     * "componentName": "测试组件4",
     * "moduleId": "1031479997352935426"
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<Component> updateModule(@RequestBody Component component) {
        return ResponseEntity.ok(componentService.updateComponent(component));
    }

    /**
     * @api {DELETE} /api/component/delete/{id} 【系统框架】删除组件
     * @apiDescription 删除组件
     * @apiGroup SysFrameWork
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/component/delete/1031480700163096577
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteComponent(@PathVariable Long id) {
        componentService.deleteComponent(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/component/batch/delete 【系统框架】批量删除组件
     * @apiDescription 批量删除组件
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 组件ID
     * @apiParamExample {json} 请求报文
     * [1031480667845984258,1031480700163096577]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/delete")
    public ResponseEntity deleteComponentByIds(@RequestBody List<Long> ids) {
        componentService.deleteBatchComponent(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/component/query/{id} 【系统框架】查询组件
     * @apiDescription 查询组件
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 组件ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/component/query/1031480144728195074
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031480144728195074",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T17:56:45.135+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T17:56:45.135+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "componentType": "1",
     * "componentName": "测试组件1",
     * "moduleId": "1031479997352935426"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<Component> getComponentById(@PathVariable Long id) {
        return ResponseEntity.ok(componentService.getComponentById(id));
    }

    /**
     * @api {GET} /api/component/query 【系统框架】查询组件分页
     * @apiDescription 根据模块Id，查询所有组件 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} moduleId 模块ID
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/component/query?moduleId=1031479997352935426&isEnabled=true&page=0&size=10
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031480144728195074",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T17:56:45.135+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T17:56:45.135+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "componentType": "1",
     * "componentName": "测试组件1",
     * "moduleId": "1031479997352935426"
     * },
     * {
     * "id": "1031480637256925185",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-20T17:58:42.564+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T17:58:42.564+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "componentType": "2",
     * "componentName": "测试组件2",
     * "moduleId": "1031479997352935426"
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<Component>> getComponentsByModuleId(@RequestParam Long moduleId,
                                                                   @RequestParam(required = false) Boolean isEnabled,
                                                                   Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<Component> list = componentService.getComponentsByModuleId(moduleId, isEnabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/component/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

}
