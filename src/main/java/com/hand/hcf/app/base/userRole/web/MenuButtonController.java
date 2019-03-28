package com.hand.hcf.app.base.userRole.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.userRole.domain.MenuButton;
import com.hand.hcf.app.base.userRole.dto.RoleMenuDTO;
import com.hand.hcf.app.base.userRole.service.MenuButtonService;
import com.hand.hcf.core.util.LoginInformationUtil;
import com.hand.hcf.core.util.PageUtil;
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
     * @api {POST} /api/menuButton/create 【角色权限】按钮创建
     * @apiDescription 给菜单创建按钮
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {String} buttonCode 按钮代码
     * @apiParam (请求参数) {String} buttonName 按钮名称
     * @apiParam (请求参数) {Long} menuId 菜单ID
     * @apiParamExample {json} 请求报文:
     * {
     * "buttonCode":1001,
     * "buttonName":"common.save",
     * "menuId":1029977144029360129
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} buttonCode 按钮代码
     * @apiSuccess (返回参数) {String} buttonName 按钮名称
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
     * "id": "1030012847077457921",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-16T16:46:14.132+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-16T16:46:14.132+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "menuId": "1029977144029360129",
     * "buttonName":"common.save",
     * "buttonCode": "1001"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<MenuButton> createRoleMenu(@RequestBody MenuButton menuButton) {
        return ResponseEntity.ok(menuButtonService.createMenuButton(menuButton));
    }

    /**
     * @api {PUT} /api/menuButton/update 【角色权限】按钮更新
     * @apiDescription 更新角色关联菜单 只允许修改buttonName,enabled和deleted字段
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id ID字段
     * @apiParam (请求参数) {Boolean} enabled 启用标识
     * @apiParam (请求参数) {Boolean} deleted 删除标识
     * @apiParam (请求参数) {Long} versionNumber 版本号
     * @apiParamExample {json} 请求报文:
     * {
     * "id": "1030013201496145921",
     * "enabled": false,
     * "deleted": false,
     * "versionNumber": 1,
     * "menuId": "1029977144029360129",
     * "buttonName":"common.save"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} buttonCode 按钮代码
     * @apiSuccess (返回参数) {Long} menuId 菜单ID
     * @apiSuccess (返回参数) {String} buttonName    按钮名称
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1030013201496145921",
     * "enabled": false,
     * "deleted": false,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "menuId": "1029977144029360129",
     * "buttonCode": "1004",
     * "buttonName": "common.save"
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<MenuButton> updateRoleMenu(@RequestBody MenuButton menuButton) {
        return ResponseEntity.ok(menuButtonService.updateMenuButton(menuButton));
    }

    /**
     * @api {DELETE} /api/menuButton/delete/{id} 【角色权限】按钮删除
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
     * @api {POST} /api/menuButton/batch/delete 【角色权限】按钮批量删除
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
     * @api {GET} /api/menuButton/query/{id} 【角色权限】按钮查询
     * @apiDescription 查询按钮
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id 角色ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/menuButton/query/1030013201496145921
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1030013201496145921",
     * "enabled": false,
     * "deleted": false,
     * "createdDate": "2018-08-16T16:47:38.618+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-16T16:49:05.759+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 2,
     * "menuId": "1029977144029360129",
     * "buttonCode": "1004",
     * "buttonName": "common.save"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<MenuButton> getRoleMenuById(@PathVariable Long id) {
        return ResponseEntity.ok(menuButtonService.getMenuButtonById(id));
    }

    /**
     * @api {GET} /api/menuButton/query/menu 【角色权限】菜单的按钮查询
     * @apiDescription 查询菜单关联按钮【分页】
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} menuId 菜单ID
     * @apiParam (请求参数) {Boolean} [enabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/menuButton/query/menu?menuId=1029977144029360129&enabled=true&page=0&size=2
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1030012847077457921",
     * "enabled": true,
     * "deleted": true,
     * "createdDate": "2018-08-16T16:46:14.132+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-16T16:50:48.029+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 2,
     * "menuId": "1029977144029360129",
     * "buttonCode": "1001",
     * "buttonName": "common.save"
     * },
     * {
     * "id": "1030013157661474817",
     * "enabled": true,
     * "deleted": true,
     * "createdDate": "2018-08-16T16:47:28.169+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-16T16:52:07.754+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 2,
     * "menuId": "1029977144029360129",
     * "buttonCode": "1002",
     * "buttonName": "common.save"
     * }
     * ]
     */
    @GetMapping("/query/menu")
    public ResponseEntity<List<RoleMenuDTO>> getRoleMenusByRoleId(@RequestParam(required = true) Long menuId,
                                                                  @RequestParam(required = false) Boolean enabled,
                                                                  Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<MenuButton> list = menuButtonService.getMenuButtons(menuId, enabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/menuButton/query/menu");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/menuButton/query/selectedMenu 【角色权限】菜单ID取登录人分配的按钮
     * @apiDescription 根据菜单ID，返回当前登录用户所有角色分配的菜单按钮集合， 用于界面菜单的按钮显示控制
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} menuId 菜单ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/menuButton/query/selectedButton?menuId=1029977144029360129
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1030013157661474817",
     * "enabled": true,
     * "menuId": "1029977144029360129",
     * "buttonCode": "1002",
     * "buttonName": "common.save"
     * },
     * {
     * "id": "1030013157661474817",
     * "enabled": true,
     * "menuId": "1029977144029360129",
     * "buttonCode": "1002",
     * "buttonName": "common.save"
     * }
     * ]
     */
    @GetMapping("/query/selectedButton")
    public ResponseEntity<List<MenuButton>> getMenuButtonsByMenuIdAndUserId(@RequestParam(required = true) Long menuId){
        //获取登录用户的ID
        Long userId = LoginInformationUtil.getCurrentUserId();
        List<MenuButton> list =  menuButtonService.getMenuButtonsByMenuIdAndUserId(menuId,userId);
        return new ResponseEntity(list, HttpStatus.OK);
    }

}
