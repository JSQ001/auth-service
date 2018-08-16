package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.MenuButton;
import com.helioscloud.atlantis.dto.RoleMenuDTO;
import com.helioscloud.atlantis.service.MenuButtonService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/14.
 * 按钮控制类
 */
@RestController
@RequestMapping("/api/menuButton")
public class MenuButtonController {
    private final MenuButtonService menuButtonService;

    public MenuButtonController(MenuButtonService menuButtonService) {
        this.menuButtonService = menuButtonService;
    }

    /**
     * @api {POST} /api/menuButton/create 【角色权限】创建按钮
     * @apiDescription 给菜单创建按钮
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {String} buttonCode 按钮代码
     * @apiParam (请求参数) {Long} menuId 菜单ID
     * @apiParam (请求参数) {Boolean} hide 是否隐藏
     * @apiParamExample {json} 请求报文:
     * {
     * "buttonCode":1001,
     * "menuId":1029977144029360129,
     * "hide":false
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Long} buttonCode 按钮代码
     * @apiSuccess (返回参数) {Long} menuId 菜单ID
     * @apiSuccess (返回参数) {Boolean} hide    是否隐藏
     * @apiSuccess (返回参数) {Boolean} isEnabled    启用标志
     * @apiSuccess (返回参数) {Boolean} isDeleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1030012847077457921",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-16T16:46:14.132+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-16T16:46:14.132+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "menuId": "1029977144029360129",
     * "buttonCode": "1001",
     * "hide": false
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<MenuButton> createRoleMenu(@RequestBody MenuButton menuButton) {
        return ResponseEntity.ok(menuButtonService.createMenuButton(menuButton));
    }

    /**
     * @api {POST} /api/menuButton/update 【角色权限】更新按钮
     * @apiDescription 更新角色关联菜单 只允许修改hide,isEnabled和isDeleted字段
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id ID字段
     * @apiParam (请求参数) {Boolean} hide 是否隐藏
     * @apiParam (请求参数) {Boolean} isEnabled 启用标识
     * @apiParam (请求参数) {Boolean} isDeleted 删除标识
     * @apiParam (请求参数) {Long} versionNumber 版本号
     * @apiParamExample {json} 请求报文:
     * {
     * "id": "1030013201496145921",
     * "isEnabled": false,
     * "isDeleted": false,
     * "versionNumber": 1,
     * "menuId": "1029977144029360129",
     * "hide": true
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Long} buttonCode 按钮代码
     * @apiSuccess (返回参数) {Long} menuId 菜单ID
     * @apiSuccess (返回参数) {Boolean} hide    是否隐藏
     * @apiSuccess (返回参数) {Boolean} isEnabled    启用标志
     * @apiSuccess (返回参数) {Boolean} isDeleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1030013201496145921",
     * "isEnabled": false,
     * "isDeleted": false,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "menuId": "1029977144029360129",
     * "buttonCode": "1004",
     * "hide": true
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<MenuButton> updateRoleMenu(@RequestBody MenuButton menuButton) {
        return ResponseEntity.ok(menuButtonService.updateMenuButton(menuButton));
    }

    /**
     * @api {DELETE} /api/menuButton/delete/{id} 【角色权限】删除按钮
     * @apiDescription 删除按钮[逻辑删除]
     * @apiGroup Auth2Service
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/menuButton/delete/1030012847077457921
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteMenuButton(@PathVariable Long id) {
        menuButtonService.deleteMenuButton(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/menuButton/batch/delete 【角色权限】批量删除按钮
     * @apiDescription 批量删除按钮
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id ID
     * @apiParamExample {json} 请求报文
     * [1030013157661474817,1030013173474000898]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/delete")
    public ResponseEntity deleteRoleByIds(@RequestBody List<Long> ids) {
        menuButtonService.deleteBatchMenuButton(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/menuButton/query/{id} 【角色权限】查询按钮
     * @apiDescription 查询按钮
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id 角色ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/menuButton/query/1030013201496145921
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1030013201496145921",
     * "isEnabled": false,
     * "isDeleted": false,
     * "createdDate": "2018-08-16T16:47:38.618+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-16T16:49:05.759+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 2,
     * "menuId": "1029977144029360129",
     * "buttonCode": "1004",
     * "hide": true
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<MenuButton> getRoleMenuById(@PathVariable Long id) {
        return ResponseEntity.ok(menuButtonService.getMenuButtonById(id));
    }

    /**
     * @api {GET} /api/menuButton/query/menu 【角色权限】查询菜单的按钮
     * @apiDescription 查询菜单关联按钮【分页】
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} menuId 菜单ID
     * @apiParam (请求参数) {Boolean} [isDeleted] 删除标识 如果不传，默认取所有未删除的
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/menuButton/query/menu?menuId=1029977144029360129&isDeleted=true&page=0&size=2
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1030012847077457921",
     * "isEnabled": true,
     * "isDeleted": true,
     * "createdDate": "2018-08-16T16:46:14.132+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-16T16:50:48.029+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 2,
     * "menuId": "1029977144029360129",
     * "buttonCode": "1001",
     * "hide": false
     * },
     * {
     * "id": "1030013157661474817",
     * "isEnabled": true,
     * "isDeleted": true,
     * "createdDate": "2018-08-16T16:47:28.169+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-16T16:52:07.754+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 2,
     * "menuId": "1029977144029360129",
     * "buttonCode": "1002",
     * "hide": false
     * }
     * ]
     */
    @GetMapping("/query/menu")
    public ResponseEntity<List<RoleMenuDTO>> getRoleMenusByRoleId(@RequestParam(required = true) Long menuId,
                                                                  @RequestParam(required = false) Boolean isDeleted,
                                                                  @RequestParam(required = false) Boolean isEnabled,
                                                                  Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<MenuButton> list = menuButtonService.getMenuButtons(menuId, isDeleted, isEnabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/menuButton/query/menu");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

}
