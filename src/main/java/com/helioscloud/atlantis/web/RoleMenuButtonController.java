package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.RoleMenuButton;
import com.helioscloud.atlantis.service.RoleMenuButtonService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/14.
 * 角色菜单按钮控制类
 */
@RestController
@RequestMapping("/api/roleMenuButton")
public class RoleMenuButtonController {
    private final RoleMenuButtonService roleMenuButtonService;

    public RoleMenuButtonController(RoleMenuButtonService roleMenuButtonService) {
        this.roleMenuButtonService = roleMenuButtonService;
    }

    /**
     * @api {POST} /api/roleMenuButton/create 【角色权限】角色菜单按钮创建
     * @apiDescription 创建角色关联菜单按钮
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} roleId 角色ID
     * @apiParam (请求参数) {Long} buttonId 菜单按钮ID
     * @apiParamExample {json} 请求报文:
     * {
     * "roleId":1029919265725378561,
     * "buttonId":1030013157661474817
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Long} roleId 角色ID
     * @apiSuccess (返回参数) {Long} buttonId 菜单按钮ID
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1030015747216375809",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-16T16:57:45.562+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-16T16:57:45.562+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 1,
     * "buttonId": "1030013157661474817",
     * "roleId": "1029919265725378561"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<RoleMenuButton> createRoleMenuButton(@RequestBody RoleMenuButton roleMenuButton) {
        return ResponseEntity.ok(roleMenuButtonService.createRoleMenuButton(roleMenuButton));
    }

    /**
     * @api {PUT} /api/roleMenuButton/update 【角色权限】角色菜单按钮更新
     * @apiDescription 更新角色关联菜单按钮 只允许修改enabled和deleted字段
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id ID字段
     * @apiParam (请求参数) {Boolean} enabled 启用标识
     * @apiParam (请求参数) {Boolean} deleted 删除标识
     * @apiParam (请求参数) {Long} versionNumber 版本号
     * @apiParamExample {json} 请求报文:
     * {
     * "id": "1030015747216375809",
     * "enabled": false,
     * "deleted": false,
     * "versionNumber": 1
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Long} roleId 角色ID
     * @apiSuccess (返回参数) {Long} buttonId 菜单按钮ID
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1030015747216375809",
     * "enabled": false,
     * "deleted": false,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "buttonId": null,
     * "roleId": null
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<RoleMenuButton> updateRoleMenuButton(@RequestBody RoleMenuButton roleMenuButton) {
        return ResponseEntity.ok(roleMenuButtonService.updateRoleMenuButton(roleMenuButton));
    }

    /**
     * @api {DELETE} /api/roleMenuButton/delete 【角色权限】角色菜单按钮删除
     * @apiGroup Auth2Service
     * @apiDescription 删除角色关联菜单按钮[逻辑删除]
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/roleMenuButton/delete/1030015747216375809
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteRoleMenuButton(@PathVariable Long id) {
        roleMenuButtonService.deleteRoleMenuButton(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/roleMenuButton/batch/delete 【角色权限】角色菜单按钮批量删除
     * @apiDescription 批量删除角色菜单按钮
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id ID
     * @apiParamExample {json} 请求报文
     * [1030018041836208129,1030018099507888130]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/delete")
    public ResponseEntity deleteRoleMenuButtonByIds(@RequestBody List<Long> ids) {
        roleMenuButtonService.deleteBatchRoleMenuButton(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/roleMenuButton/query/{id} 【角色权限】角色菜单按钮查询
     * @apiDescription 查询角色关联菜单按钮
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/roleMenuButton/query/1030015747216375809
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1030015747216375809",
     * "enabled": false,
     * "deleted": true,
     * "createdDate": "2018-08-16T16:57:45.562+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-16T17:05:04.427+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 3,
     * "buttonId": "1030013157661474817",
     * "roleId": "1029919265725378561"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<RoleMenuButton> getRoleMenuButtonById(@PathVariable Long id) {
        return ResponseEntity.ok(roleMenuButtonService.getRoleMenuButtonById(id));
    }

    /**
     * @api {GET} /api/roleMenuButton/query/role 【角色权限】角色菜单按钮查询分页
     * @apiDescription 查询角色关联菜单菜单【分页】
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} roleId 角色ID
     * @apiParam (请求参数) {Boolean} [enabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/roleMenuButton/query/role?roleId=1029919265725378561&enabled=true&page=0&size=2
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1030015747216375809",
     * "enabled": false,
     * "deleted": true,
     * "createdDate": "2018-08-16T16:57:45.562+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-16T17:05:04.427+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 3,
     * "buttonId": "1030013157661474817",
     * "roleId": "1029919265725378561"
     * },
     * {
     * "id": "1030018041836208129",
     * "enabled": true,
     * "deleted": true,
     * "createdDate": "2018-08-16T17:06:52.644+08:00",
     * "createdBy": 0,
     * "lastUpdatedDate": "2018-08-16T17:08:40.138+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 2,
     * "buttonId": "1030013173474000898",
     * "roleId": "1029919265725378561"
     * }
     * ]
     */
    @GetMapping("/query/role")
    public ResponseEntity<List<RoleMenuButton>> getRoleMenuButtonByRoleId(@RequestParam(required = true) Long roleId,
                                                                          @RequestParam(required = false) Boolean enabled,
                                                                          Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<RoleMenuButton> list = roleMenuButtonService.getRoleMenuButtonByRoleId(roleId, enabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/roleMenuButton/query/role");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

}
