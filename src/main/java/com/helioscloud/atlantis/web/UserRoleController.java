package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.UserRole;
import com.helioscloud.atlantis.dto.UserRoleDTO;
import com.helioscloud.atlantis.service.UserRoleService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/14.
 * 用户角色控制类
 */
@RestController
@RequestMapping("/api/userRole")
public class UserRoleController {
    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    /**
     * @api {POST} /api/userRole/create 【角色权限】用户角色创建
     * @apiDescription 创建用户角色
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} userId 用户ID
     * @apiParam (请求参数) {Long} roleId 角色ID
     * @apiParamExample {json} 请求报文:
     * {
     * "userId":1005,
     * "roleId":1029943470084898818
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Long} userId 用户ID
     * @apiSuccess (返回参数) {Long} roleId 角色ID
     * @apiSuccess (返回参数) {Boolean} isEnabled    启用标志
     * @apiSuccess (返回参数) {Boolean} isDeleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1029999294023069698",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-16T15:52:22.816+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-16T15:52:22.816+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "userId": "1005",
     * "roleId": "1029943470084898818"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<UserRole> createUserRole(@RequestBody UserRole userRole) {
        return ResponseEntity.ok(userRoleService.createUserRole(userRole));
    }

    /**
     * @api {POST} /api/userRole/update 【角色权限】用户角色更新
     * @apiDescription 更新用户关联角色 只允许修改isEnabled和isDeleted字段
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id ID字段
     * @apiParam (请求参数) {Boolean} isEnabled 启用标识
     * @apiParam (请求参数) {Boolean} isDeleted 删除标识
     * @apiParam (请求参数) {Long} versionNumber 版本号
     * @apiParamExample {json} 请求报文:
     * {
     * "id": "1029999294023069698",
     * "isEnabled": false,
     * "isDeleted": false,
     * "versionNumber": 1
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Long} roleId 角色ID
     * @apiSuccess (返回参数) {Long} userId 用户ID
     * @apiSuccess (返回参数) {Boolean} isEnabled    启用标志
     * @apiSuccess (返回参数) {Boolean} isDeleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1029999294023069698",
     * "isEnabled": false,
     * "isDeleted": false,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "userId": null,
     * "roleId": null
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<UserRole> updateUserRole(@RequestBody UserRole userRole) {
        return ResponseEntity.ok(userRoleService.updateRole(userRole));
    }

    /**
     * @api {DELETE} /api/userRole/delete/{id} 【角色权限】用户角色删除
     * @apiDescription 删除用户关联角色[逻辑删除]
     * @apiGroup Auth2Service
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/userRole/delete/1029999294023069698
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteUserRole(@PathVariable Long id) {
        userRoleService.deleteUserRole(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/userRole/batch/delete 【角色权限】用户角色批量删除
     * @apiDescription 批量删除用户关联角色[逻辑删除]
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id ID
     * @apiParamExample {json} 请求报文
     * [1029999294023069698,1030000119722143746]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/delete")
    public ResponseEntity deleteRoleByIds(@RequestBody List<Long> ids) {
        userRoleService.deleteBatchUserRole(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/userRole/query/{id} 【角色权限】用户角色查询
     * @apiDescription 查询用户关联角色
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/userRole/query/1029999294023069698
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1029999294023069698",
     * "isEnabled": false,
     * "isDeleted": true,
     * "createdDate": "2018-08-16T15:52:22.816+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-16T16:07:07.617+08:00",
     * "lastUpdatedBy": 0,
     * "versionNumber": 4,
     * "userId": "1005",
     * "roleId": "1029943470084898818"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<UserRole> getUserRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(userRoleService.getUserRoleById(id));
    }

    /**
     * @api {GET} /api/roleMenu/query/role 【角色权限】角色菜单查询
     * @apiDescription 查询角色关联菜单【分页】
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} userId 用户ID
     * @apiParam (请求参数) {Boolean} [isEnabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/userRole/query/user?userId=1005&isEnabled=true&page=0&size=2
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": 1029999294023069698,
     * "userId": 1005,
     * "roleId": null,
     * "role": {
     * "id": "1029943470084898818",
     * "isEnabled": true,
     * "isDeleted": false,
     * "createdDate": "2018-08-16T12:10:33.354+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-16T12:44:58.15+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 2,
     * "roleCode": "R001",
     * "roleName": "测试角色2",
     * "tenantId": "1022057230117146625"
     * }
     * },
     * {
     * "id": 1030000119722143746,
     * "userId": 1005,
     * "roleId": null,
     * "role": {
     * "id": "1029919265725378561",
     * "isEnabled": true,
     * "isDeleted": true,
     * "createdDate": "2018-08-16T10:34:22.582+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-16T12:31:20.15+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 5,
     * "roleCode": "R002",
     * "roleName": "测试角色2",
     * "tenantId": "1022057230117146625"
     * }
     * }
     * ]
     */
    @GetMapping("/query/user")
    public ResponseEntity<List<UserRoleDTO>> getUserRolesByUserId(@RequestParam(required = true) Long userId,
                                                                  @RequestParam(required = false) Boolean isEnabled,
                                                                  Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<UserRoleDTO> list = userRoleService.getUserRolesByUserId(userId, isEnabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/userRole/query/user");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }


}
