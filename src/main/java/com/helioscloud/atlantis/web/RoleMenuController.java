package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.RoleMenu;
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
     * @api {POST} /api/roleMenu/create 【角色权限】创建角色菜单
     * @apiDescription 创建角色关联菜单
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} roleId 角色ID
     * @apiParam (请求参数) {Long} menuId 菜单ID
     * @apiParamExample {json} 请求报文:
     * {
     * "roleId":1029919265725378561,
     * "menuId":1029973242290647041
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Long} roleId 角色ID
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
     * "id": "1029987832156180482",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-16T15:06:50.094+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-16T15:06:50.094+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "roleId": "1029919265725378561",
     * "menuId": "1029973242290647041"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<RoleMenu> createRoleMenu(@RequestBody RoleMenu roleMenu) {
        return ResponseEntity.ok(roleMenuService.createRoleMenu(roleMenu));
    }

    /**
     * @api {POST} /api/roleMenu/update 【角色权限】更新角色菜单
     * @apiDescription 更新角色关联菜单 只允许修改isEnabled和isDeleted字段
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id ID字段
     * @apiParam (请求参数) {Boolean} isEnabled 启用标识
     * @apiParam (请求参数) {Boolean} isDeleted 删除标识
     * @apiParam (请求参数) {Long} versionNumber 版本号
     * @apiParamExample {json} 请求报文:
     * {
     * "id": "1029987832156180482",
     * "isEnabled": false,
     * "isDeleted": false,
     * "versionNumber": 1
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Long} roleId 角色ID
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
     * "id": "1029987832156180482",
     * "isEnabled": false,
     * "isDeleted": false,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 2,
     * "roleId": null,
     * "menuId": null
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<RoleMenu> updateRoleMenu(@RequestBody RoleMenu roleMenu) {
        return ResponseEntity.ok(roleMenuService.updateRole(roleMenu));
    }

    /**
     * @api {DELETE} /api/roleMenu/delete/{id} 【角色权限】删除角色菜单
     * @apiDescription 删除角色关联菜单[逻辑删除]
     * @apiGroup Auth2Service
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/roleMenu/delete/1029987832156180482
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteRoleMenu(@PathVariable Long id) {
        roleMenuService.deleteRoleMenu(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/roleMenu/batch/delete 【角色权限】批量删除角色菜单
     * @apiDescription 批量删除角色的菜单
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id ID
     * @apiParamExample {json} 请求报文
     * [1029991221233504257,1029991251763843074]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/delete")
    public ResponseEntity deleteRoleByIds(@RequestBody List<Long> ids) {
        roleMenuService.deleteBatchRoleMenu(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/roleMenu/query/{id} 【角色权限】查询角色菜单
     * @apiDescription 查询角色关联菜单
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id 角色ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/roleMenu/query/1029987832156180482
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1029987832156180482",
     * "isEnabled": true,
     * "isDeleted": true,
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
     * @api {GET} /api/roleMenu/query/role 【角色权限】查询角色菜单
     * @apiDescription 查询角色关联菜单【分页】
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} roleId 角色ID
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/roleMenu/query/role?roleId=1029919265725378561&isEnabled=true&page=0&size=2
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": 1029987832156180482,
     * "menuId": 1029973242290647041,
     * "roleId": 1029919265725378561,
     * "menu": {
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
     * },
     * {
     * "id": 1029991221233504257,
     * "menuId": 1029977215173144577,
     * "roleId": 1029919265725378561,
     * "menu": {
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
     * }
     * ]
     */
    @GetMapping("/query/role")
    public ResponseEntity<List<RoleMenuDTO>> getRoleMenusByRoleId(@RequestParam(required = true) Long roleId,
                                                                  @RequestParam(required = false) Boolean isEnabled,
                                                                  Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<RoleMenuDTO> list = roleMenuService.getRoleMenusByRoleId(roleId, isEnabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/roleMenu/query/role");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

}
