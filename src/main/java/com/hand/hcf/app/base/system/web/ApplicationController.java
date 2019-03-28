package com.hand.hcf.app.base.system.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.system.domain.Application;
import com.hand.hcf.app.base.system.service.ApplicationService;
import com.hand.hcf.core.util.PageUtil;
import lombok.AllArgsConstructor;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by weishan on 2019/3/5.
 * 应用控制类
 */
@RestController
@RequestMapping("/api/application")
@AllArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    private DiscoveryClient discoveryClient;


    /**
     * @api {POST} /api/application 【系统框架】应用创建
     * @apiDescription 创建应用
     * appCode  全局唯一
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {String} componentType 组件类型 1 为组件，2为界面
     * @apiParam (请求参数) {String} componentName 组件名称
     * @apiParam (请求参数) {Long} [menuId] 菜单ID
     * @apiParam (请求参数) {List} [buttonList] 菜单按钮集合List MenuButton
     * @apiParam (请求参数buttonList的属性) {String} buttonCode 菜单按钮的代码 全局唯一
     * @apiParam (请求参数buttonList的属性) {String} buttonName 菜单按钮的名称
     * @apiParam (请求参数buttonList的属性) {String} flag 标识 1001为创建，1002为删除
     * @apiParamExample {json} 请求报文:
     * {
     * "componentType":2,
     * "componentName":"我的合同",
     * "menuId":"1034791757754834946"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} componentType 组件类型 1 为组件，2为界面
     * @apiSuccess (返回参数) {String} componentName 组件名称
     * @apiSuccess (返回参数) {Long} menuId 菜单ID
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1034846577319292929",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-30T00:53:45.209+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": "2018-08-30T00:53:45.209+08:00",
     * "lastUpdatedBy": 1013,
     * "versionNumber": 1,
     * "componentType": "2",
     * "componentName": "我的合同",
     * "menuId": "1034791757754834946",
     * }
     */
    @PostMapping
    public ResponseEntity<Application> createApplication(@RequestBody Application application) {
        return ResponseEntity.ok(applicationService.createApplication(application));
    }

    /**
     * @api {PUT} /api/component/update 【系统框架】组件更新
     * @apiDescription 更新组件, 当buttonList传空时，表示不更新菜单的按钮
     * 当buttonList不为空时，
     * 如果集合对象中flag为 1001, 菜单按钮id 为空，则会保存，菜单按钮id不为空，则会更新
     * 如果集合对象中flag为 1002, 将删除菜单与该菜单按钮ID的关联
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 组件ID
     * @apiParam (请求参数) {String} componentType 组件类型 1 为组件，2为界面
     * @apiParam (请求参数) {String} componentName 组件名称
     * @apiParam (请求参数) {Long} menuId 菜单ID
     * @apiParam (请求参数) {Integer} versionNumber 版本号
     * @apiParam (请求参数) {String} enabled 启用标志
     * @apiParam (请求参数) {String} deleted 删除标志
     * @apiParam (请求参数) {List} [buttonList] 菜单按钮集合List MenuButton
     * @apiParam (请求参数buttonList的属性) {String} buttonCode 菜单按钮的代码 全局唯一
     * @apiParam (请求参数buttonList的属性) {String} buttonName 菜单按钮的名称
     * @apiParam (请求参数buttonList的属性) {String} flag 标识 1001为创建，1002为删除
     * @apiParam (请求参数buttonList的属性) {Long} [id]  菜单按钮id
     * @apiParam (请求参数buttonList的属性) {Long} menuId 菜单ID
     * @apiParam (请求参数buttonList的属性) {Boolean} enabled    启用标志
     * @apiParam (请求参数buttonList的属性) {Integer} versionNumber    版本号
     * @apiParamExample {json} 请求报文(示例1):
     * {
     * "id": "1034846577319292929",
     * "enabled": true,
     * "deleted": false,
     * "versionNumber": 2,
     * "componentType": "2",
     * "componentName": "我的合同",
     * "menuId": "1034791757754834946"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} componentType 组件类型 1 为组件，2为界面
     * @apiSuccess (返回参数) {String} componentName 组件名称
     * @apiSuccess (返回参数) {Long} menuId 菜单ID
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文(示例1):
     * {
     * "id": "1034846577319292929",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-30T00:53:45.209+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 3,
     * "componentType": "2",
     * "componentName": "我的合同",
     * "menuId": "1034791757754834946"
     * }
     */
    @PutMapping
    public ResponseEntity<Application> updateApplication(@RequestBody Application application) {
        return ResponseEntity.ok(applicationService.updateApplication(application));
    }

    /**
     * @api {DELETE} /api/component/delete/{id} 【系统框架】组件删除
     * @apiDescription 删除组件
     * @apiGroup SysFrameWork
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/component/delete/1031480700163096577
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteApplication(@PathVariable Long id) {
        applicationService.delete(id);
        return ResponseEntity.ok().build();
    }


    /**
     * @api {GET} /api/component/query/{id} 【系统框架】组件查询
     * @apiDescription 查询组件
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 组件ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/component/query/1031480144728195074
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031480144728195074",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T17:56:45.135+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T17:56:45.135+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "componentType": "1",
     * "componentName": "测试组件1",
     * "appId": "1031479997352935426",
     * "menuId": "1"
     * }
     */
    @GetMapping("/{id}")
    public ResponseEntity<Application> getById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.selectById(id));
    }

    /**
     * @api {GET} /api/component/query 【系统框架】组件查询分页
     * @apiDescription 查询所有组件 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Boolean} [enabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/component/query?enabled=true&page=0&size=10
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031480144728195074",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T17:56:45.135+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T17:56:45.135+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "componentType": "1",
     * "componentName": "测试组件1",
     * "menuId": "1"
     * },
     * {
     * "id": "1031480637256925185",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T17:58:42.564+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T17:58:42.564+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "componentType": "2",
     * "componentName": "测试组件2",
     * "menuId": "1"
     * }
     * ]
     */
    @GetMapping
    public ResponseEntity<List<Application>> pageAll(
            Pageable pageable,
            @RequestParam(required = false) String appCode,
            @RequestParam(required = false) String appName) {
        Page page = PageUtil.getPage(pageable);
        List<Application> list = applicationService.pageApps(page, appCode, appName);
        return new ResponseEntity(list, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Application>> listAll(
            @RequestParam(required = false) String appCode,
            @RequestParam(required = false) String appName) {
        List<Application> list = applicationService.listAll(appCode,appName);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/services")
    public ResponseEntity<List<String>> getAllService() {
        List<String> services = discoveryClient.getServices();
        services.sort(String::compareTo);
        return ResponseEntity.ok(services);
    }

}
