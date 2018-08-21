package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.Menu;
import com.helioscloud.atlantis.service.MenuService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    public MenuController(MenuService roleService) {
        this.menuService = roleService;
    }

    /**
     * @api {POST} /api/menu/create 【菜单权限】菜单创建
     * @apiDescription 创建菜单
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {String} menuCode 菜单代码
     * @apiParam (请求参数) {String} menuName 菜单名称
     * @apiParam (请求参数) {Integer} seqNumber 顺序号
     * @apiParam (请求参数) {Integer} menuTypeEnum 类型 1000：功能 1001：目录，1002：组件
     * @apiParam (请求参数) {Long} parentMenuId 上级菜单ID,没有上级时，传0，即0为根目录
     * @apiParam (请求参数) {String} menuIcon 菜单图标
     * @apiParam (请求参数) {String} menuUrl 菜单URL
     * @apiParamExample {json} 请求报文:
     * {
     * "menuCode":"M001",
     * "menuName":"费用管理",
     * "seqNumber":1,
     * "menuTypeEnum":1001,
     * "parentMenuId":0,
     * "menuIcon":"",
     * "menuUrl":""
     * }
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
     * "menuIcon": "",
     * "menuUrl": ""
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<Menu> createMenu(@RequestBody Menu menu) {
        return ResponseEntity.ok(menuService.createMenu(menu));
    }

    /**
     * @api {POST} /api/menu/update 【菜单权限】菜单更新
     * @apiDescription 更新菜单
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
     * "menuUrl": ""
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
     * "menuUrl": ""
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<Menu> updateMenu(@RequestBody Menu menu) {
        return ResponseEntity.ok(menuService.updateMenu(menu));
    }

    /**
     * @api {POST} /api/menu/delete 【菜单权限】菜单删除
     * @apiDescription 删除菜单
     * @apiGroup Auth2Service
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/menu/delete/1029916356543561729
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteRole(@PathVariable Long id) {
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
     * "menuUrl": null
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<Menu> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getMenuById(id));
    }

    /**
     * @api {GET} /api/menu/query/{id} 【角色权限】菜单查询【分页】
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
     * "menuUrl": null
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
     * "menuUrl": null
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<Menu>> getRoles( @RequestParam(required = false) Boolean isEnabled,
                                               Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<Menu> list = menuService.getMenus(isEnabled, page);
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
     * "menuUrl": null
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
     * "menuUrl": null
     * }
     * ]
     */
    @GetMapping("/query/byParentMenuId")
    public ResponseEntity<List<Menu>> getRolesByParentId(
            @RequestParam(required = true) Long parentMenuId,
            @RequestParam(required = false) Boolean isEnabled,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<Menu> list = menuService.getMenusByParentMenuId(parentMenuId, isEnabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/menu/query/byParentMenuId");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }


}
