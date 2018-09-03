package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.Role;
import com.helioscloud.atlantis.service.RoleService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/14.
 * 角色控制类
 */
@RestController
@RequestMapping("/api/role")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * @api {POST} /api/role/create 【角色权限】角色创建
     * @apiDescription 创建角色
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {String} roleCode 角色代码
     * @apiParam (请求参数) {String} roleName 角色名称
     * @apiParamExample {json} 请求报文:
     * {
     * "roleCode":"R001",
     * "roleName":"测试角色"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} roleCode 角色代码
     * @apiSuccess (返回参数) {String} roleName 角色名称
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1029545792423387138",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-15T09:50:20.074+08:00",
     * "createdBy": 1,
     * "lastUpdatedDate": "2018-08-15T09:50:20.074+08:00",
     * "lastUpdatedBy": 1,
     * "versionNumber": 1,
     * "roleCode": "R001",
     * "roleName": "测试角色",
     * "tenantId": "1022057230117146625"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(roleService.createRole(role));
    }

    /**
     * @api {PUT} /api/role/update 【角色权限】角色更新
     * @apiDescription 更新角色
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id 角色ID
     * @apiParam (请求参数) {String} [roleCode] 角色代码不允许修改
     * @apiParam (请求参数) {String} roleName 角色名称
     * @apiParam (请求参数) {Integer} versionNumber 版本号
     * @apiParam (请求参数) {String} enabled 启用标志
     * @apiParam (请求参数) {String} deleted 删除标志
     * @apiParamExample {json} 请求报文:
     * {
     * "id": "1029916356543561729",
     * "enabled": true,
     * "deleted": false,
     * "versionNumber": 1,
     * "roleCode": "R001",
     * "roleName": "测试角色2"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} roleCode 角色代码
     * @apiSuccess (返回参数) {String} roleName 角色名称
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1029916356543561729",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-16T10:22:48.987+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "roleCode": "R001",
     * "roleName": "测试角色2",
     * "tenantId": "1022057230117146625"
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<Role> updateRole(@RequestBody Role role) {
        return ResponseEntity.ok(roleService.updateRole(role));
    }

    /**
     * @api {DELETE} /api/role/delete/{id} 【角色权限】角色删除
     * @apiDescription 删除角色
     * @apiGroup Auth2Service
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/role/delete/1029916356543561729
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/role/batch/delete 【角色权限】角色批量删除
     * @apiDescription 批量删除角色
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id 角色ID
     * @apiParamExample {json} 请求报文
     * [1029919265725378561,1029919290434023426]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/delete")
    public ResponseEntity deleteRoleByIds(@RequestBody List<Long> ids) {
        roleService.deleteBatchRole(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/role/query/{id} 【角色权限】角色查询
     * @apiDescription 查询角色
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id 角色ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/role/query/1029919265725378561
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1029919265725378561",
     * "enabled": true,
     * "deleted": true,
     * "createdDate": "2018-08-16T10:34:22.582+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-16T10:34:22.582+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "roleCode": "R002",
     * "roleName": "测试角色2",
     * "tenantId": "1022057230117146625"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    /**
     * @api {GET} /api/role/query/tenant 【角色权限】角色查询分页
     * @apiDescription 查询租户下的所有角色 分页
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} tenantId 租户ID
     * @apiParam (请求参数) {Boolean} [enabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/role/query/tenant?tenantId=1022057230117146625&enabled=true&page=0&size=2
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1029919265725378561",
     * "enabled": true,
     * "deleted": true,
     * "createdDate": "2018-08-16T10:34:22.582+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-16T12:31:20.15+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 5,
     * "roleCode": "R002",
     * "roleName": "测试角色2",
     * "tenantId": "1022057230117146625"
     * },
     * {
     * "id": "1029919290434023426",
     * "enabled": true,
     * "deleted": true,
     * "createdDate": "2018-08-16T10:34:28.472+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-16T12:31:20.162+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 5,
     * "roleCode": "R003",
     * "roleName": "测试角色3",
     * "tenantId": "1022057230117146625"
     * }
     * ]
     */
    @GetMapping("/query/tenant")
    public ResponseEntity<List<Role>> getRolesByTenantId(@RequestParam(required = true) Long tenantId,
                                                         @RequestParam(required = false) Boolean enabled,
                                                         Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<Role> list = roleService.getRolesByTenantId(tenantId, enabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/role/query/tenant");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

}
