package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.Menu;
import com.helioscloud.atlantis.service.MenuService;
import com.helioscloud.atlantis.service.es.EsMenuInfoSerivce;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/14.
 * 菜单控制类
 */
@RestController
@RequestMapping("/api/menu")
public class MenuController {
    private final MenuService menuService;
    private final EsMenuInfoSerivce esMenuInfoSerivce;

    public MenuController(MenuService roleService, EsMenuInfoSerivce esMenuInfoSerivce) {
        this.menuService = roleService;
        this.esMenuInfoSerivce = esMenuInfoSerivce;
    }

    /**
     * @api {POST} /api/menu/create 【菜单权限】菜单创建
     * @apiDescription 创建菜单
     * 1)hasChildCatalog 是否有子目录，默认为false,当添加目录时，会把上级目录的该属性设置为true,
     * 2)如果上级是功能，则不允许再添加子功能
     * 3)添加功能时，校验其上级是否有下级目录，如果有，则不允许功能，功能只能添加到最底级的目录、
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {String} menuCode 菜单代码
     * @apiParam (请求参数) {String} menuName 菜单名称
     * @apiParam (请求参数) {Integer} seqNumber 顺序号
     * @apiParam (请求参数) {Integer} menuTypeEnum 类型 1000：功能 1001：目录，1002：组件
     * @apiParam (请求参数) {Long} parentMenuId 上级菜单ID,没有上级时，传0，即0为根目录
     * @apiParam (请求参数) {String} menuIcon 菜单图标
     * @apiParam (请求参数) {String} menuUrl 菜单URL
     * @apiParam (请求参数) {String} fromSource 来源 DB 为数据库，FILE为文件
     * @apiParam (请求参数) {List} [buttonList] 按钮的List 当目录有按钮时，传该集合
     * @apiParam (请求参数buttonList的属性) {String} buttonCode 按钮的代码
     * @apiParam (请求参数buttonList的属性) {String} buttonName 按钮的名称
     * @apiSuccess (返回参数) {Long} id 菜单ID
     * @apiSuccess (返回参数) {String} menuCode 菜单代码
     * @apiSuccess (返回参数) {String} menuName 菜单名称
     * @apiSuccess (返回参数) {Integer} seqNumber 顺序号
     * @apiSuccess (返回参数) {Integer} menuTypeEnum 类型 1000：功能 1001：目录，1002：组件
     * @apiSuccess (返回参数) {Long} parentMenuId 上级菜单ID,没有上级时，传0，即0为根目录
     * @apiSuccess (返回参数) {String} menuIcon 菜单图标
     * @apiSuccess (返回参数) {String} menuUrl 菜单URL
     * @apiSuccess (返回参数) {String} fromSource 来源 DB 表示数据库，FILE表示文件
     * @apiSuccess (返回参数) {List} [buttonList] 按钮的List 当目录有按钮时，传该集合
     * @apiSuccess (返回参数buttonList的属性) {Long} id 按钮ID
     * @apiSuccess (返回参数buttonList的属性) {String} buttonCode 按钮的代码
     * @apiSuccess (返回参数buttonList的属性) {String} buttonName 按钮的名称
     * @apiSuccess (返回参数) {Boolean} hasChildCatalog 是否有子目录，默认为false,当添加目录时，会把上级目录的该属性设置为true
     * @apiSuccess (返回参数) {Boolean} isEnabled    启用标志
     * @apiSuccess (返回参数) {Boolean} isDeleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiParamExample {json} 请求报文:
     * {
     * "menuCode": "M100101",
     * "menuName": "我的个人信息",
     * "seqNumber": 1,
     * "menuTypeEnum": 1001,
     * "parentMenuId": 1035535048168132610,
     * "menuIcon": "item",
     * "menuUrl": "http://item.com",
     * "fromSource":"DB",
     * "buttonList":[
     * {"buttonCode":"100101-query",
     * "buttonName":"common.query",
     * "flag":1001
     * },
     * {"buttonCode":"100101-save",
     * "buttonName":"common.save" ,
     * "flag":1001
     * },
     * {"buttonCode":"100101-delete",
     * "buttonName":"common.delete",
     * "flag":1001
     * }
     * ]
     * }
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1035541525687676929",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-31T22:55:13.817+08:00",
     * "createdBy": 1012,
     * "lastUpdatedDate": "2018-08-31T22:55:13.817+08:00",
     * "lastUpdatedBy": 1012,
     * "versionNumber": 1,
     * "menuCode": "M100101",
     * "menuName": "我的个人信息",
     * "seqNumber": 1,
     * "menuTypeEnum": 1001,
     * "parentMenuId": "1035535048168132610",
     * "menuIcon": "item",
     * "menuUrl": "http://item.com",
     * "hasChildCatalog": null,
     * "fromSource": "DB",
     * "buttonList": [
     * {
     * "id": "1035541549842673665",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-31T22:55:19.573+08:00",
     * "createdBy": 1012,
     * "lastUpdatedDate": "2018-08-31T22:55:19.573+08:00",
     * "lastUpdatedBy": 1012,
     * "versionNumber": 1,
     * "menuId": "1035541525687676929",
     * "buttonCode": "100101-query",
     * "buttonName": "common.query",
     * "flag": "1001"
     * },
     * {
     * "id": "1035541551004495874",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-31T22:55:19.853+08:00",
     * "createdBy": 1012,
     * "lastUpdatedDate": "2018-08-31T22:55:19.853+08:00",
     * "lastUpdatedBy": 1012,
     * "versionNumber": 1,
     * "menuId": "1035541525687676929",
     * "buttonCode": "100101-save",
     * "buttonName": "common.save",
     * "flag": "1001"
     * },
     * {
     * "id": "1035541551927242753",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-31T22:55:20.069+08:00",
     * "createdBy": 1012,
     * "lastUpdatedDate": "2018-08-31T22:55:20.069+08:00",
     * "lastUpdatedBy": 1012,
     * "versionNumber": 1,
     * "menuId": "1035541525687676929",
     * "buttonCode": "100101-delete",
     * "buttonName": "common.delete",
     * "flag": "1001"
     * }
     * ]
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<Menu> createMenu(@RequestBody Menu menu) {
        return ResponseEntity.ok(menuService.createMenu(menu));
    }

    /**
     * @api {PUT} /api/menu/update 【菜单权限】菜单更新
     * @apiDescription 更新菜单
     * 1) 由目录 修改为 功能 时，判断是否还有除了当前菜单之后的目录，如果不存在，则需要修改hasChildCatalog的值为false
     * 2) 禁用上级菜单的时候，把其所有子菜单都禁用掉，但启用的时候，只启用它自己，不递归处理子菜单
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id 菜单ID
     * @apiParam (请求参数) {String} menuCode 菜单代码 不允许修改
     * @apiParam (请求参数) {String} menuName 菜单名称
     * @apiParam (请求参数) {Integer} seqNumber 顺序号
     * @apiParam (请求参数) {Integer} versionNumber 版本号
     * @apiParam (请求参数) {Integer} menuTypeEnum 类型 1000：功能 1001：目录，1002：组件
     * @apiParam (请求参数) {Long} parentMenuId 上级菜单ID,没有上级时，传0，即0为根目录
     * @apiParam (请求参数) {String} menuIcon 菜单图标
     * @apiParam (请求参数) {String} menuUrl 菜单URL
     * @apiParamExample {json} 请求报文:
     * {
     * "id": "1029974951649882113",
     * "isEnabled": true,
     * "isDeleted": false,
     * "menuCode": "M00201",
     * "menuName": "我的申请单",
     * "versionNumber":2,
     * "seqNumber": 2,
     * "menuTypeEnum": 1000,
     * "parentMenuId": 1029973941745364994,
     * "menuIcon": "",
     * "menuUrl": "",
     * "fromSource":"DB"
     * }
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1029974951649882113",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 2,
     * "menuCode": "M00201",
     * "menuName": "我的申请单",
     * "seqNumber": 2,
     * "menuTypeEnum": 1000,
     * "parentMenuId": 1029973941745364994,
     * "menuIcon": "",
     * "menuUrl": "",
     * "fromSource":"DB"
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<Menu> updateMenu(@RequestBody Menu menu) {
        return ResponseEntity.ok(menuService.updateMenu(menu));
    }

    /**
     * @api {DELETE} /api/menu/delete 【菜单权限】菜单删除
     * @apiDescription 删除菜单
     * @apiGroup Auth2Service
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/menu/delete/1029916356543561729
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteMenu(@PathVariable Long id) throws Exception {
        menuService.deleteMenu(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/menu/batch/delete 【角色权限】菜单批量删除
     * @apiDescription 批量删除菜单
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id 角色ID
     * @apiParamExample {json} 请求报文
     * [1029977215173144577,1029974951649882113]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/delete")
    public ResponseEntity deleteMenuByIds(@RequestBody List<Long> ids) {
        menuService.deleteBatchMenu(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/menu/query/{id} 【角色权限】菜单查询
     * @apiDescription 查询菜单
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id 菜单ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/menu/query/1029973242290647041
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1029973242290647041",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-16T14:08:51.597+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-16T14:08:51.597+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "menuCode": "M001",
     * "menuName": "费用管理",
     * "seqNumber": 1,
     * "menuTypeEnum": 1001,
     * "parentMenuId": 0,
     * "menuIcon": null,
     * "menuUrl": null,
     * "hasChildCatalog":false,
     * "fromSource":"DB"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<Menu> getMenuById(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getMenuById(id));
    }

    /**
     * @api {GET} /api/menu/query 【角色权限】菜单查询分页
     * @apiDescription 查询所有菜单 分页
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/menu/query?isEnabled=false&page=0&size=2
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1029977144029360129",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-16T14:24:21.843+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-16T14:24:21.843+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "menuCode": "M00202",
     * "menuName": "申请单2",
     * "seqNumber": 2,
     * "menuTypeEnum": 1000,
     * "parentMenuId": 1029973941745364994,
     * "menuIcon": null,
     * "menuUrl": null,
     * "hasChildCatalog":false,
     * "fromSource":"DB"
     * },
     * {
     * "id": "1029973242290647041",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-16T14:08:51.597+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-16T14:08:51.597+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "menuCode": "M001",
     * "menuName": "费用管理",
     * "seqNumber": 1,
     * "menuTypeEnum": 1001,
     * "parentMenuId": 0,
     * "menuIcon": null,
     * "menuUrl": null,
     * "hasChildCatalog":false,
     * "fromSource":"DB"
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<Menu>> getMenus(@RequestParam(required = false) Boolean isEnabled,
                                               Pageable pageable) throws URISyntaxException {
        List<Menu> list = menuService.getMenus(isEnabled, pageable);
        Page page = PageUtil.getPage(pageable);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/menu/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/menu/query/byParentMenuId 【角色权限】菜单查询【分页】
     * @apiDescription 查询父菜单对应的所有子菜单 分页
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} parentMenuId 父菜单ID
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/menu/query/byParentMenuId?parentMenuId=1029973941745364994&isEnabled=true&page=0&size=2
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1029977183732641793",
     * "isEnabled": true,
     * "isDeleted": true,
     * "createdDate": "2018-08-16T14:24:31.31+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-16T14:36:56.05+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 2,
     * "menuCode": "M00203",
     * "menuName": "申请单3",
     * "seqNumber": 3,
     * "menuTypeEnum": 1000,
     * "parentMenuId": 1029973941745364994,
     * "menuIcon": null,
     * "menuUrl": null,
     * "hasChildCatalog":false,
     * "fromSource":"DB"
     * },
     * {
     * "id": "1029977215173144577",
     * "isEnabled": true,
     * "isDeleted": true,
     * "createdDate": "2018-08-16T14:24:38.805+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-16T14:48:49.225+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 2,
     * "menuCode": "M00204",
     * "menuName": "申请单4",
     * "seqNumber": 4,
     * "menuTypeEnum": 1000,
     * "parentMenuId": 1029973941745364994,
     * "menuIcon": null,
     * "menuUrl": null,
     * "hasChildCatalog":false,
     * "fromSource":"DB"
     * }
     * ]
     */
    @GetMapping("/query/byParentMenuId")
    public ResponseEntity<List<Menu>> getMenusByParentId(
            @RequestParam(required = true) Long parentMenuId,
            @RequestParam(required = false) Boolean isEnabled,
            Pageable pageable) throws URISyntaxException {
        List<Menu> list = menuService.getMenusByParentMenuId(parentMenuId, isEnabled, pageable);
        Page page = PageUtil.getPage(pageable);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/menu/query/byParentMenuId");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {DELETE} /api/menu/es/remove/all 【角色权限】菜单-移除索引库中全部数据
     * @apiDescription 菜单-移除索引库中全部数据
     * @apiGroup Auth2Service
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/menu/es/remove/all
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @RequestMapping(value = "/es/remove/all", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removeAllMenu() throws IOException {
        esMenuInfoSerivce.removeAll();
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/menu/es/init/all 【角色权限】菜单-初始化索引库全部数据
     * @apiDescription 菜单-初始化索引库全部数据
     * @apiGroup Auth2Service
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/menu/es/init/all
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @RequestMapping(value = "/es/init/all", method = RequestMethod.GET)
    public ResponseEntity<Void> initAllMenu() {
        esMenuInfoSerivce.doIndexTransaction();
        return ResponseEntity.ok().build();
    }
}
