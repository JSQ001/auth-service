package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.RoleMenu;
import com.helioscloud.atlantis.dto.MenuTreeDTO;
import com.helioscloud.atlantis.dto.RoleMenuButtonDTO;
import com.helioscloud.atlantis.dto.RoleMenuDTO;
import com.helioscloud.atlantis.service.RoleMenuService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/14.
 * 角色菜单控制类
 */
@RestController
@RequestMapping("/api/roleMenu")
public class RoleMenuController {
    private final RoleMenuService roleMenuService;

    public RoleMenuController(RoleMenuService roleMenuService) {
        this.roleMenuService = roleMenuService;
    }

    /**
     * @api {POST} /api/roleMenu/assign/menu 【角色权限】角色分配菜单
     * @apiDescription 角色分配菜单
     * 保存时，前端只传hasChildCatalog 为 false的数据
     * flag：创建:1001，删除:1002, 该删除为物理删除关联表的数据
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} roleId 角色ID
     * @apiParam (请求参数) {RoleMenuButtonDTO} assignMenuButtonList 菜单集合
     * @apiParam (请求参数assignMenuButtonList的属性) {String} type 类型：DIRECTORY为菜单目录，BUTTON为菜单按钮
     * @apiParam (请求参数assignMenuButtonList的属性) {Long} id 当type为DIRECTORY时，表示菜单的ID，为BUTTON表示菜单按钮ID
     * @apiParam (请求参数assignMenuButtonList的属性) {String} code 当type为DIRECTORY时，表示菜单的代码，为BUTTON表示菜单按钮代码
     * @apiParam (请求参数assignMenuButtonList的属性) {String} name 当type为DIRECTORY时，表示菜单的名称，为BUTTON表示菜单按钮名称
     * @apiParam (请求参数assignMenuButtonList的属性) {Long} parentId 当type为DIRECTORY时，表示菜单的上级菜单ID，为BUTTON表示菜单按钮对应的菜单ID
     * @apiParamExample {json} 请求报文:
     * {
     * "roleId":1034792064245211137,
     * "assignMenuButtonList":[
     * {
     * "id":1035535048168132610,
     * "code":"M1001",
     * "name":"人事申请",
     * "type":"DIRECTORY",
     * "parentId":0,
     * "flag":1001
     * },{
     * "id":1035541525687676929,
     * "code":"M100101",
     * "name":"我的个人信息",
     * "type":"DIRECTORY",
     * "parentId":1035535048168132610,
     * "flag":1001
     * },{
     * "id":1035550274938712065,
     * "code":"M100201",
     * "name":"笔记本购买申请",
     * "type":"DIRECTORY",
     * "parentId":1035550093967077377,
     * "flag":1001
     * },{
     * "id":1035550277669203970,
     * "code":"100201-query",
     * "name":"common.query",
     * "type":"BUTTON",
     * "parentId":1035550274938712065,
     * "flag":1001
     * },{
     * "id":1035550277824393218,
     * "code":"100201-save",
     * "name":"common.save",
     * "type":"BUTTON",
     * "parentId":1035550274938712065,
     * "flag":1001
     * },{
     * "id":1035550277937639425,
     * "code":"100201-delete",
     * "name":"common.delete",
     * "type":"BUTTON",
     * "parentId":1035550274938712065,
     * "flag":1001
     * }
     * ]
     * }
     * @apiSuccessExample {json} 返回报文:
     * {
     * }
     */
    @PostMapping("/assign/menu")
    public ResponseEntity roleAssignMenu(@RequestBody RoleMenuButtonDTO roleMenu) {
        roleMenuService.roleAssignMenu(roleMenu);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {PUT} /api/roleMenu/update 【角色权限】角色菜单更新
     * @apiDescription 更新角色关联菜单 只允许修改enabled和deleted字段  已弃用
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id ID字段
     * @apiParam (请求参数) {Boolean} enabled 启用标识
     * @apiParam (请求参数) {Boolean} deleted 删除标识
     * @apiParam (请求参数) {Long} versionNumber 版本号
     * @apiParamExample {json} 请求报文:
     * {
     * "id": "1029987832156180482",
     * "enabled": false,
     * "deleted": false,
     * "versionNumber": 1
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Long} roleId 角色ID
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
     * "id": "1029987832156180482",
     * "enabled": false,
     * "deleted": false,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 2,
     * "roleId": null,
     * "menuId": null
     * }
     *//*
    @PutMapping("/update")
    public ResponseEntity<RoleMenu> updateRoleMenu(@RequestBody RoleMenu roleMenu) {
        return ResponseEntity.ok(roleMenuService.updateRole(roleMenu));
    }
*/
   /* *//**
     * @api {DELETE} /api/roleMenu/delete/{id} 【角色权限】角色菜单删除
     * @apiDescription 删除角色关联菜单[逻辑删除]  已弃用
     * @apiGroup Auth2Service
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/roleMenu/delete/1029987832156180482
     * @apiSuccessExample {json} 返回报文:
     * []
     *//*
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteRoleMenu(@PathVariable Long id) {
        roleMenuService.deleteRoleMenu(id);
        return ResponseEntity.ok().build();
    }*/

    /**
     * 弃用
     * @api {POST} /api/roleMenu/batch/delete 【角色权限】角色菜单批量删除
     * @apiDescription 批量删除角色的菜单
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id ID
     * @apiParamExample {json} 请求报文
     * [1029991221233504257,1029991251763843074]
     * @apiSuccessExample {json} 返回报文:
     * []
     *//*
    @PostMapping("/batch/delete")
    public ResponseEntity deleteRoleByIds(@RequestBody List<Long> ids) {
        roleMenuService.deleteBatchRoleMenu(ids);
        return ResponseEntity.ok().build();
    }
*/

    /**
     * @api {GET} /api/roleMenu/query/{id} 【角色权限】角色菜单查询
     * @apiDescription 查询角色关联菜单
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id 角色ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/roleMenu/query/1029987832156180482
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1029987832156180482",
     * "enabled": true,
     * "deleted": true,
     * "createdDate": "2018-08-16T15:06:50.094+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-16T15:18:46.374+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 5,
     * "roleId": "1029919265725378561",
     * "menuId": "1029973242290647041"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<RoleMenu> getRoleMenuById(@PathVariable Long id) {
        return ResponseEntity.ok(roleMenuService.getRoleMenuById(id));
    }

    /**
     * @api {GET} /api/roleMenu/query/role 【角色权限】角色菜单查询
     * @apiDescription 查询角色关联菜单【分页】
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} roleId 角色ID
     * @apiParam (请求参数) {Boolean} [enabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/roleMenu/query/role?roleId=1029919265725378561&enabled=true&page=0&size=2
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": 1029987832156180482,
     * "menuId": 1029973242290647041,
     * "roleId": 1029919265725378561,
     * "menu": {
     * "id": "1029973242290647041",
     * "enabled": true,
     * "deleted": false,
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
     * },
     * {
     * "id": 1029991221233504257,
     * "menuId": 1029977215173144577,
     * "roleId": 1029919265725378561,
     * "menu": {
     * "id": "1029977215173144577",
     * "enabled": true,
     * "deleted": true,
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
     * }
     * ]
     */
    @GetMapping("/query/role")
    public ResponseEntity<List<RoleMenuDTO>> getRoleMenusByRoleId(@RequestParam(required = true) Long roleId,
                                                                  @RequestParam(required = false) Boolean enabled,
                                                                  Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<RoleMenuDTO> list = roleMenuService.getRoleMenusByRoleId(roleId, enabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/roleMenu/query/role");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {POST} /api/roleMenu/query/roles 【角色权限】角色获取菜单
     * @apiDescription 根据角色ID集合，取对应所有角色分配的菜单树结构
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} roleId 角色ID集合
     * @apiParamExample {json} 请求报文
     * [1032110573802041345,1032110626130178050]
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "children": [],
     * "menuCode": "M001",
     * "menuName": "费用管理",
     * "seqNumber": 1,
     * "menuTypeEnum": 1001,
     * "parentMenuId": 0,
     * "menuIcon": null,
     * "menuUrl": null,
     * "id": 1029973242290647041
     * },
     * {
     * "children": [
     * {
     * "children": [
     * {
     * "children": [],
     * "menuCode": "M91000101",
     * "menuName": "实习生返校申请",
     * "seqNumber": 1,
     * "menuTypeEnum": 1000,
     * "parentMenuId": 1032111883234390017,
     * "menuIcon": "TIcon001",
     * "menuUrl": "http://backschool.com",
     * "id": 1032112529471778817
     * },
     * {
     * "children": [],
     * "menuCode": "M91000102",
     * "menuName": "旅游申请",
     * "seqNumber": 2,
     * "menuTypeEnum": 1000,
     * "parentMenuId": 1032111883234390017,
     * "menuIcon": "TIcon002",
     * "menuUrl": "http://travel.com",
     * "id": 1032112786570031105
     * }
     * ],
     * "menuCode": "M910001",
     * "menuName": "人事相关申请",
     * "seqNumber": 1,
     * "menuTypeEnum": 1001,
     * "parentMenuId": 1032111556967870466,
     * "menuIcon": "TIcon",
     * "menuUrl": null,
     * "id": 1032111883234390017
     * },
     * {
     * "children": [
     * {
     * "children": [],
     * "menuCode": "M91000201",
     * "menuName": "笔记本更换申请",
     * "seqNumber": 1,
     * "menuTypeEnum": 1000,
     * "parentMenuId": 1032113214791692290,
     * "menuIcon": "TIcon3",
     * "menuUrl": "http://test3.com",
     * "id": 1032113411911397378
     * },
     * {
     * "children": [],
     * "menuCode": "M91000202",
     * "menuName": "笔记本购买申请",
     * "seqNumber": 2,
     * "menuTypeEnum": 1000,
     * "parentMenuId": 1032113214791692290,
     * "menuIcon": "TIcon4",
     * "menuUrl": "http://test4.com",
     * "id": 1032113517226176513
     * }
     * ],
     * "menuCode": "M910002",
     * "menuName": "笔记本相关申请",
     * "seqNumber": 2,
     * "menuTypeEnum": 1001,
     * "parentMenuId": 1032111556967870466,
     * "menuIcon": "TIcon2",
     * "menuUrl": null,
     * "id": 1032113214791692290
     * }
     * ],
     * "menuCode": "M9100",
     * "menuName": "个人申请",
     * "seqNumber": 1,
     * "menuTypeEnum": 1001,
     * "parentMenuId": 0,
     * "menuIcon": "NEW",
     * "menuUrl": null,
     * "id": 1032111556967870466
     * }
     * ]
     */
    @PostMapping("/query/roles")
    public ResponseEntity<List<MenuTreeDTO>> getMenusByRoleIds(@RequestBody List<Long> roleIds) throws URISyntaxException {
        List<MenuTreeDTO> list = roleMenuService.getMenuTreeByRolesId(roleIds);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/roleMenu/query/menuIds/{roleId} 【角色权限】角色已分配菜单查询
     * @apiDescription 根据角色ID，查询已分配的菜单的ID的集合。（只取功能，不取目录）
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} roleId 角色ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/roleMenu/query/menuIds/1029987832156180482
     * @apiSuccessExample {json} 返回报文:
     * [
     * 1029973242290647041,
     * 1032111556967870466
     * ]
     */
    @GetMapping("/query/menuIds/{roleId}")
    public ResponseEntity<List<String>> getMenuIdsAndButtonIdsByRoleId(@PathVariable Long roleId) {
        return ResponseEntity.ok(roleMenuService.getMenuIdsAndButtonIdsByRoleId(roleId));
    }

    /**
     * @api {GET} /api/roleMenu/query/buttonIds/{roleId} 【角色权限】角色已分配菜单按钮查询
     * @apiDescription 根据角色ID，查询已分配的菜单按钮的ID的集合
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} roleId 角色ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/roleMenu/query/buttonIds/1029987832156180482
     * @apiSuccessExample {json} 返回报文:
     * [
     * 1029973242290647041,
     * 1032111556967870466
     * ]
     */
    @GetMapping("/query/buttonIds/{roleId}")
    public ResponseEntity<List<String>> getMenuButtonIdsByRoleId(@PathVariable Long roleId) {
        return ResponseEntity.ok(roleMenuService.getMenuButtonIdsByRoleId(roleId));
    }

}
