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
     * @api {POST} /api/component/create 【系统框架】组件创建
     * @apiDescription 创建组件, 如果componentType为1时，即组件时，不需要传menuId 和 buttonList，
     * 当componentType为2时，如果界面上有按钮，则需要传buttonList参数，将按钮一起保存到后端
     * buttonCode  全局唯一
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
     * "menuId":"1034791757754834946",
     * "buttonList":[
     * {
     * "buttonCode":"contract.save",
     * "buttonName":"contract.save",
     * "flag":1001
     * },
     * {
     * "buttonCode":"contract.query",
     * "buttonName":"contract.query",
     * "flag":1001
     * },
     * {
     * "buttonCode":"contract.delete",
     * "buttonName":"contract.delete",
     * "flag":1001
     * }
     * ]
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} componentType 组件类型 1 为组件，2为界面
     * @apiSuccess (返回参数) {String} componentName 组件名称
     * @apiSuccess (返回参数) {Long} menuId 菜单ID
     * @apiSuccess (返回参数) {Boolean} isEnabled    启用标志
     * @apiSuccess (返回参数) {Boolean} isDeleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1034846577319292929",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T00:53:45.209+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": "2018-08-30T00:53:45.209+08:00",
     * "lastUpdatedBy": 1013,
     * "versionNumber": 1,
     * "componentType": "2",
     * "componentName": "我的合同",
     * "menuId": "1034791757754834946",
     * "buttonList": [
     * {
     * "id": "1034846576912445441",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T00:53:45.116+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": "2018-08-30T00:53:45.117+08:00",
     * "lastUpdatedBy": 1013,
     * "versionNumber": 1,
     * "menuId": "1034791757754834946",
     * "buttonCode": "contract.save",
     * "buttonName": "contract.save",
     * "flag": "1001"
     * },
     * {
     * "id": "1034846577130549249",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T00:53:45.164+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": "2018-08-30T00:53:45.164+08:00",
     * "lastUpdatedBy": 1013,
     * "versionNumber": 1,
     * "menuId": "1034791757754834946",
     * "buttonCode": "contract.query",
     * "buttonName": "contract.query",
     * "flag": "1001"
     * },
     * {
     * "id": "1034846577239601153",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T00:53:45.191+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": "2018-08-30T00:53:45.191+08:00",
     * "lastUpdatedBy": 1013,
     * "versionNumber": 1,
     * "menuId": "1034791757754834946",
     * "buttonCode": "contract.delete",
     * "buttonName": "contract.delete",
     * "flag": "1001"
     * }
     * ]
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<Component> createComponent(@RequestBody Component component) {
        return ResponseEntity.ok(componentService.createComponent(component));
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
     * @apiParam (请求参数) {String} isEnabled 启用标志
     * @apiParam (请求参数) {String} isDeleted 删除标志
     * @apiParam (请求参数) {List} [buttonList] 菜单按钮集合List MenuButton
     * @apiParam (请求参数buttonList的属性) {String} buttonCode 菜单按钮的代码 全局唯一
     * @apiParam (请求参数buttonList的属性) {String} buttonName 菜单按钮的名称
     * @apiParam (请求参数buttonList的属性) {String} flag 标识 1001为创建，1002为删除
     * @apiParam (请求参数buttonList的属性) {Long} [id]  菜单按钮id
     * @apiParam (请求参数buttonList的属性) {Long} menuId 菜单ID
     * @apiParam (请求参数buttonList的属性) {Boolean} isEnabled    启用标志
     * @apiParam (请求参数buttonList的属性) {Integer} versionNumber    版本号
     * @apiParamExample {json} 请求报文(示例1):
     * {
     * "id": "1034846577319292929",
     * "isEnabled": true,
     * "isDeleted": false,
     * "versionNumber": 2,
     * "componentType": "2",
     * "componentName": "我的合同",
     * "menuId": "1034791757754834946"
     * }
     * @apiParamExample {json} 请求报文(示例2):
     * {
     * "id": "1034846577319292929",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T00:53:45.209+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 3,
     * "componentType": "2",
     * "componentName": "我的合同",
     * "menuId": "1034791757754834946",
     * "buttonList": [
     * {
     * "id": "1034846577239601153",
     * "isEnabled": true,
     * "isDeleted": false,
     * "versionNumber": 1,
     * "menuId": "1034791757754834946",
     * "buttonCode": "contract.delete",
     * "buttonName": "contract.delete",
     * "flag": 1001
     * },
     * {
     * "buttonCode": "contract.update",
     * "buttonName": "contract.update",
     * "flag": 1001
     * },
     * {
     * "id": "1034846576912445441",
     * "isEnabled": true,
     * "isDeleted": false,
     * "versionNumber": 1,
     * "menuId": "1034791757754834946",
     * "buttonCode": "contract.save",
     * "buttonName": "contract.save",
     * "flag": 1002
     * }
     * ]
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} componentType 组件类型 1 为组件，2为界面
     * @apiSuccess (返回参数) {String} componentName 组件名称
     * @apiSuccess (返回参数) {Long} menuId 菜单ID
     * @apiSuccess (返回参数) {Boolean} isEnabled    启用标志
     * @apiSuccess (返回参数) {Boolean} isDeleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文(示例1):
     * {
     * "id": "1034846577319292929",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T00:53:45.209+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 3,
     * "componentType": "2",
     * "componentName": "我的合同",
     * "menuId": "1034791757754834946",
     * "buttonList": [
     * {
     * "id": "1034846577239601153",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T00:53:45.191+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": "2018-08-30T00:53:45.191+08:00",
     * "lastUpdatedBy": 1013,
     * "versionNumber": 1,
     * "menuId": "1034791757754834946",
     * "buttonCode": "contract.delete",
     * "buttonName": "contract.delete",
     * "flag": null
     * },
     * {
     * "id": "1034846577130549249",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T00:53:45.164+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": "2018-08-30T00:53:45.164+08:00",
     * "lastUpdatedBy": 1013,
     * "versionNumber": 1,
     * "menuId": "1034791757754834946",
     * "buttonCode": "contract.query",
     * "buttonName": "contract.query",
     * "flag": null
     * },
     * {
     * "id": "1034846576912445441",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T00:53:45.116+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": "2018-08-30T00:53:45.117+08:00",
     * "lastUpdatedBy": 1013,
     * "versionNumber": 1,
     * "menuId": "1034791757754834946",
     * "buttonCode": "contract.save",
     * "buttonName": "contract.save",
     * "flag": null
     * }
     * ]
     * }
     * @apiSuccessExample {json} 返回报文(示例2):
     * {
     * "id": "1034846577319292929",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T00:53:45.209+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 4,
     * "componentType": "2",
     * "componentName": "我的合同",
     * "menuId": "1034791757754834946",
     * "buttonList": [
     * {
     * "id": "1034846577239601153",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T00:53:45.191+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "menuId": "1034791757754834946",
     * "buttonCode": "contract.delete",
     * "buttonName": "contract.delete",
     * "flag": "1001"
     * },
     * {
     * "id": "1034850253505110017",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T01:08:21.681+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": "2018-08-30T01:08:21.681+08:00",
     * "lastUpdatedBy": 1013,
     * "versionNumber": 1,
     * "menuId": "1034791757754834946",
     * "buttonCode": "contract.update",
     * "buttonName": "contract.update",
     * "flag": "1001"
     * }
     * ]
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<Component> updateModule(@RequestBody Component component) {
        return ResponseEntity.ok(componentService.updateComponent(component));
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
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteComponent(@PathVariable Long id) {
        componentService.deleteComponent(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/component/batch/delete 【系统框架】组件批量删除
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
     * @api {GET} /api/component/query/{id} 【系统框架】组件查询
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
     * "moduleId": "1031479997352935426",
     * "menuId": "1",
     * "buttonList": [
     * {
     * "id": "1034846577239601153",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T00:53:45.191+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "menuId": "1034791757754834946",
     * "buttonCode": "contract.delete",
     * "buttonName": "contract.delete",
     * "flag": "1001"
     * },
     * {
     * "id": "1034850253505110017",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T01:08:21.681+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": "2018-08-30T01:08:21.681+08:00",
     * "lastUpdatedBy": 1013,
     * "versionNumber": 1,
     * "menuId": "1034791757754834946",
     * "buttonCode": "contract.update",
     * "buttonName": "contract.update",
     * "flag": "1001"
     * }
     * ]
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<Component> getComponentById(@PathVariable Long id) {
        return ResponseEntity.ok(componentService.getComponentById(id));
    }

    /**
     * @api {GET} /api/component/query 【系统框架】组件查询分页
     * @apiDescription 查询所有组件 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/component/query?isEnabled=true&page=0&size=10
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
     * "menuId": "1",
     * "buttonList": [
     * {
     * "id": "1034846577239601153",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T00:53:45.191+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "menuId": "1034791757754834946",
     * "buttonCode": "contract.delete",
     * "buttonName": "contract.delete",
     * "flag": "1001"
     * },
     * {
     * "id": "1034850253505110017",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T01:08:21.681+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": "2018-08-30T01:08:21.681+08:00",
     * "lastUpdatedBy": 1013,
     * "versionNumber": 1,
     * "menuId": "1034791757754834946",
     * "buttonCode": "contract.update",
     * "buttonName": "contract.update",
     * "flag": "1001"
     * }
     * ]
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
     * "menuId": "1",
     * "buttonList": [
     * {
     * "id": "1034846577239601153",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T00:53:45.191+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "menuId": "1034791757754834946",
     * "buttonCode": "contract.delete",
     * "buttonName": "contract.delete",
     * "flag": "1001"
     * },
     * {
     * "id": "1034850253505110017",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T01:08:21.681+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": "2018-08-30T01:08:21.681+08:00",
     * "lastUpdatedBy": 1013,
     * "versionNumber": 1,
     * "menuId": "1034791757754834946",
     * "buttonCode": "contract.update",
     * "buttonName": "contract.update",
     * "flag": "1001"
     * }
     * ]
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<Component>> getComponentsByIsEnabledId(@RequestParam(required = false) Boolean isEnabled,
                                                                      Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<Component> list = componentService.getComponentsByIsEnabled(isEnabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/component/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/component/query/menu/{id} 【系统框架】组件查询
     * @apiDescription 根据菜单ID, 查询组件
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} menuId 菜单ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/component/query/menu/1031480144728195074
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
     * "moduleId": "1031479997352935426",
     * "menuId": "1031480144728195074",
     * "buttonList": [
     * {
     * "id": "1034846577239601153",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T00:53:45.191+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "menuId": "1034791757754834946",
     * "buttonCode": "contract.delete",
     * "buttonName": "contract.delete",
     * "flag": "1001"
     * },
     * {
     * "id": "1034850253505110017",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-30T01:08:21.681+08:00",
     * "createdBy": 1013,
     * "lastUpdatedDate": "2018-08-30T01:08:21.681+08:00",
     * "lastUpdatedBy": 1013,
     * "versionNumber": 1,
     * "menuId": "1034791757754834946",
     * "buttonCode": "contract.update",
     * "buttonName": "contract.update",
     * "flag": "1001"
     * }
     * ]
     * }
     */
    @GetMapping("/query/menu/{id}")
    public ResponseEntity<Component> getComponentByMenuId(@PathVariable Long menuId) {
        return ResponseEntity.ok(componentService.getComponentByMenuId(menuId));
    }

}
