package com.hand.hcf.app.base.userRole.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.user.dto.UserRoleListDTO;
import com.hand.hcf.app.base.user.service.UserService;
import com.hand.hcf.app.base.userRole.domain.Menu;
import com.hand.hcf.app.base.userRole.domain.Role;
import com.hand.hcf.app.base.userRole.domain.UserRole;
import com.hand.hcf.app.base.userRole.dto.MenuTreeDTO;
import com.hand.hcf.app.base.userRole.dto.RoleAssignMenuButtonDTO;
import com.hand.hcf.app.base.userRole.dto.UserAssignRoleDataAuthority;
import com.hand.hcf.app.base.userRole.dto.UserRoleDTO;
import com.hand.hcf.app.base.userRole.service.RoleMenuService;
import com.hand.hcf.app.base.userRole.service.UserRoleService;
import com.hand.hcf.core.util.DateUtil;
import com.hand.hcf.core.util.LoginInformationUtil;
import com.hand.hcf.core.util.PageUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/14.
 * 用户角色控制类
 */
@RestController
@RequestMapping("/api/userRole")
public class UserRoleController {
    private final UserRoleService userRoleService;
    private final RoleMenuService roleMenuService;
    private final UserService userService;

    public UserRoleController(UserRoleService userRoleService, RoleMenuService roleMenuService, UserService userService) {
        this.userRoleService = userRoleService;
        this.roleMenuService = roleMenuService;
        this.userService = userService;
    }

    /**
     * @api {POST} /api/userRole/assign/role 【角色权限】用户批量分配角色
     * @apiDescription 用户批量分配角色
     * flag：创建:1001，删除:1002, 该删除为物理删除关联表的数据
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} userId 用户ID
     * @apiParam (请求参数) {UserAssignRoleDTO} assignRoleList 集合
     * @apiParam (请求参数RoleMenuList的属性) {Long} roleId 角色ID
     * @apiParam (请求参数RoleMenuList的属性) {String} flag  1001 表示 新增，1002 表示删除
     * @apiParamExample {json} 请求报文:
     * {
     * "userId":1013,
     * "assignRoleList":[
     * {
     * "roleId":1029943470084898818,
     * "flag":1001
     * },{
     * "roleId":1029919265725378561,
     * "flag":1001
     * },{
     * "roleId":1029919290434023426,
     * "flag":1001
     * }
     * ]
     * }
     * @apiSuccessExample {json} 返回报文:
     * {
     * }
     */
    @PostMapping("/assign/role")
    public ResponseEntity userAssignRole(@RequestBody UserRoleDTO userRoleDTO) {
        userRoleService.userAssignRole(userRoleDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/userRole/query/roles 【角色权限】用户分配角色查询
     * @apiDescription 用户分配角色查询
     * queryFlag: ALL 查当前租户下所有启用的角色，ASSIGNED查 当前租户下租户已分配的启用的角色
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} userId 用户ID
     * @apiParam (请求参数) {String} [roleCode] 角色代码 模糊查询
     * @apiParam (请求参数) {String} [roleName] 角色名称 模糊查询
     * @apiParam (请求参数) {String} [queryFlag] 查询范围: ALL 查所有，ASSIGNED查用户已分配的角色
     * @apiParam (请求参数) {Integer} page 当前页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/userRole/query/roles?userId=1013&queryFlag=ASSIGNED&roleCode=R00&roleName=测试&page=0&size=20
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1029943470084898818",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "roleCode": "R001",
     * "roleName": "测试角色null",
     * "tenantId": "1022057230117146625"
     * },
     * {
     * "id": "1029919265725378561",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "roleCode": "R002",
     * "roleName": "测试角色2",
     * "tenantId": "1022057230117146625"
     * },
     * {
     * "id": "1029919290434023426",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "roleCode": "R003",
     * "roleName": "测试角色3",
     * "tenantId": "1022057230117146625"
     * }
     * ]
     */
    @GetMapping("/query/roles")
    public ResponseEntity<List<Role>> getRolesByCond(@RequestParam(required = true) Long userId,
                                                     @RequestParam(required = false) String roleCode,
                                                     @RequestParam(required = false) String roleName,
                                                     @RequestParam(required = true) String queryFlag,
                                                     Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<Role> list = userRoleService.getRolesByCond(userId, roleCode, roleName, queryFlag, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/userRole/query/roles");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {POST} /api/userRole/create 【角色权限】用户分配角色
     * @apiDescription 用户分配角色
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} userId 用户ID
     * @apiParam (请求参数) {Long} roleId 角色ID
     * @apiParam (请求参数) {Long} dataAuthorityId    数据权限id
     * @apiParam (请求参数) {ZonedDateTime} validDateFrom    有效日期从
     * @apiParam (请求参数) {ZonedDateTime} validDateTo    有效日期至
     * @apiParamExample {json} 请求报文:
     * {
        "userId": 3,
        "roleId": 1051670921947246594,
        "dataAuthorityId": 1082616210917146625,
        "validDateFrom":"2019-01-08T20:34:01.424+08:00",
        "validDateTo":"2019-01-08T20:34:01.424+08:00"
        }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Long} userId 用户ID
     * @apiSuccess (返回参数) {Long} roleId 角色ID
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccess (返回参数) {Long} dataAuthorityId    数据权限id
     * @apiSuccess (返回参数) {ZonedDateTime} validDateFrom    有效日期从
     * @apiSuccess (返回参数) {ZonedDateTime} validDateTo    有效日期至
     * @apiSuccessExample {json} 返回报文:
     * {
        "id": "1082898167499980801",
        "createdDate": "2019-01-09T15:13:37.065+08:00",
        "createdBy": "1",
        "lastUpdatedDate": "2019-01-09T15:13:37.065+08:00",
        "lastUpdatedBy": "1",
        "versionNumber": 1,
        "enabled": true,
        "userId": "3",
        "roleId": "1051670921947246594",
        "dataAuthorityId": "1082616210917146625",
        "validDateFrom": "2019-01-08T20:34:01.424+08:00",
        "validDateTo": "2019-01-08T20:34:01.424+08:00"
        }
     */
    @PostMapping("/create")
    public ResponseEntity<UserRole> createUserRole(@RequestBody UserRole userRole) {
        return ResponseEntity.ok(userRoleService.createUserRole(userRole));
    }

    /**
     * @api {PUT} /api/userRole/update 【角色权限】用户分配角色更新
     * @apiDescription 更新用户关联角色 只允许修改enabled和deleted字段
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id ID字段
     * @apiParam (请求参数) {Boolean} enabled 启用标识
     * @apiParam (请求参数) {Boolean} deleted 删除标识
     * @apiParam (请求参数) {Long} versionNumber 版本号
     * @apiParam (请求参数) {Long} dataAuthorityId    数据权限id
     * @apiParam (请求参数) {ZonedDateTime} validDateFrom    有效日期从
     * @apiParam (请求参数) {ZonedDateTime} validDateTo    有效日期至
     * @apiParamExample {json} 请求报文:
     * {
        "id": "1082898167499980801",
        "createdDate": "2019-01-09T15:13:37.065+08:00",
        "createdBy": "1",
        "lastUpdatedDate": "2019-01-09T15:13:37.065+08:00",
        "lastUpdatedBy": "1",
        "versionNumber": 1,
        "enabled": true,
        "userId": "3",
        "roleId": "1051670921947246594",
        "dataAuthorityId": "1082616210917146625",
        "validDateFrom": "2019-01-08T20:34:01.424+08:00",
        "validDateTo": "2019-01-08T20:34:01.424+08:00"
        }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Long} roleId 角色ID
     * @apiSuccess (返回参数) {Long} userId 用户ID
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccess (返回参数) {Long} dataAuthorityId    数据权限id
     * @apiSuccess (返回参数) {ZonedDateTime} validDateFrom    有效日期从
     * @apiSuccess (返回参数) {ZonedDateTime} validDateTo    有效日期至
     * @apiSuccessExample {json} 返回报文:
     * {
        "id": "1082898167499980801",
        "createdDate": "2019-01-09T15:13:37.065+08:00",
        "createdBy": "1",
        "lastUpdatedDate": "2019-01-09T15:13:37.065+08:00",
        "lastUpdatedBy": "1",
        "versionNumber": 1,
        "enabled": true,
        "userId": "3",
        "roleId": "1051670921947246594",
        "dataAuthorityId": "1082616210917146625",
        "validDateFrom": "2019-01-08T20:34:01.424+08:00",
        "validDateTo": "2019-01-08T20:34:01.424+08:00"
        }
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
     * "enabled": false,
     * "deleted": true,
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
     * @api {GET} /api/roleMenu/query/user 【角色权限】用户关联角色查询
     * @apiDescription 查询用户关联角色【分页】
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} userId 用户ID
     * @apiParam (请求参数) {Boolean} [enabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/userRole/query/user?userId=1005&enabled=true&page=0&size=2
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": 1029999294023069698,
     * "userId": 1005,
     * "roleId": null,
     * "role": {
     * "id": "1029943470084898818",
     * "enabled": true,
     * "deleted": false,
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
     * }
     * }
     * ]
     */
    @GetMapping("/query/user")
    public ResponseEntity<List<UserRoleDTO>> getUserRolesByUserId(@RequestParam(required = true) Long userId,
                                                                  @RequestParam(required = false) Boolean enabled,
                                                                  Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<UserRoleDTO> list = userRoleService.getUserRolesByUserId(userId, enabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/userRole/query/user");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /query/user/menuTree 【角色权限】用户获取菜单树
     * @apiDescription 根据用户ID，取对应所有角色分配的菜单树
     * 20180827 去掉前端传的userId参数，修改为后端取当前登录用户的菜单树
     * @apiGroup Auth2Service
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/userRole/query/user/menuTree
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
    @GetMapping("/query/user/menuTree")
    public ResponseEntity<List<MenuTreeDTO>> getMenuTreeByUserId() throws URISyntaxException {
        //修改为取当前登录用户的菜单
        Long userId = LoginInformationUtil.getCurrentUserId();
        List<MenuTreeDTO> list = roleMenuService.getMenuTreeByUserId(userId);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * @api {GET} /query/user/menuList 【角色权限】用户获取菜单列表【登录】
     * @apiDescription 根据当前登录用户，取对应所有角色分配的菜单列表，用于登录获取菜单
     * @apiGroup Auth2Service
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/userRole/query/user/menuList
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1032635715402903554",
     * "enabled": null,
     * "deleted": null,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "menuCode": "setting",
     * "menuName": "设置",
     * "seqNumber": 0,
     * "menuTypeEnum": 1001,
     * "parentMenuId": "0",
     * "menuIcon": "setting",
     * "menuUrl": "demo",
     * "hasChildCatalog": null
     * },
     * {
     * "id": "1032827031878868993",
     * "enabled": null,
     * "deleted": null,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "menuCode": "1",
     * "menuName": "1",
     * "seqNumber": 0,
     * "menuTypeEnum": 1000,
     * "parentMenuId": "1032635715402903554",
     * "menuIcon": "dot-chart",
     * "menuUrl": "demo",
     * "hasChildCatalog": null
     * },
     * {
     * "id": "1032642040533954562",
     * "enabled": null,
     * "deleted": null,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "menuCode": "role",
     * "menuName": "人员管理",
     * "seqNumber": 0,
     * "menuTypeEnum": 1001,
     * "parentMenuId": "1032635715402903554",
     * "menuIcon": "user",
     * "menuUrl": "demo",
     * "hasChildCatalog": null
     * },
     * {
     * "id": "1032826975104770050",
     * "enabled": null,
     * "deleted": null,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "menuCode": "user",
     * "menuName": "user",
     * "seqNumber": 0,
     * "menuTypeEnum": 1001,
     * "parentMenuId": "1032642040533954562",
     * "menuIcon": "bars",
     * "menuUrl": "demo",
     * "hasChildCatalog": null
     * },
     * {
     * "id": "1032667046353645570",
     * "enabled": null,
     * "deleted": null,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "menuCode": "ceshi",
     * "menuName": "测试",
     * "seqNumber": 0,
     * "menuTypeEnum": 1000,
     * "parentMenuId": "1032642040533954562",
     * "menuIcon": "book",
     * "menuUrl": "demo",
     * "hasChildCatalog": null
     * }
     * ]
     */
    @GetMapping("/query/user/menuList")
    public ResponseEntity<List<Menu>> getMenuListByLoginUserId() throws URISyntaxException {
        Long userId = LoginInformationUtil.getCurrentUserId();
        List<Menu> list = roleMenuService.getMenuListByUserId(userId);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * @api {GET} /query/user/menuAndButtonList 【角色权限】菜单查询所有及其按钮
     * @apiDescription 角色点开分配菜单的按钮，显示所有菜单及菜单按钮的
     * 个人申请 -》笔记本相关申请-》笔记本购买申请 (common.save，common.delete,common.query 三个按钮)
       人事申请 -》我的个人信息 (common.save，common.delete,common.query 三个按钮)
     * @apiGroup Auth2Service
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/userRole/query/user/menuAndButtonList
     * @apiSuccess (返回参数) {String} type 类型：DIRECTORY为菜单目录，BUTTON为菜单按钮
     * @apiSuccess (返回参数) {Long} id 当type为DIRECTORY时，表示菜单的ID，为BUTTON表示菜单按钮ID
     * @apiSuccess (返回参数) {String} code 当type为DIRECTORY时，表示菜单的代码，为BUTTON表示菜单按钮代码
     * @apiSuccess (返回参数) {String} name 当type为DIRECTORY时，表示菜单的名称，为BUTTON表示菜单按钮名称
     * @apiSuccess (返回参数) {String} parentId 当type为DIRECTORY时，表示菜单的上级菜单ID，为BUTTON表示菜单按钮对应的菜单ID
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1035532779615899650",
     * "code": "M1000",
     * "name": "个人申请",
     * "parentId": "0",
     * "type": "DIRECTORY",
     * "flag": null
     * },
     * {
     * "id": "1035535048168132610",
     * "code": "M1001",
     * "name": "人事申请",
     * "parentId": "0",
     * "type": "DIRECTORY",
     * "flag": null
     * },
     * {
     * "id": "1035550093967077377",
     * "code": "M1002",
     * "name": "笔记本相关申请",
     * "parentId": "1035532779615899650",
     * "type": "DIRECTORY",
     * "flag": null
     * },
     * {
     * "id": "1035550274938712065",
     * "code": "M100201",
     * "name": "笔记本购买申请",
     * "parentId": "1035550093967077377",
     * "type": "DIRECTORY",
     * "flag": null
     * },
     * {
     * "id": "1035541525687676929",
     * "code": "M100101",
     * "name": "我的个人信息",
     * "parentId": "1035535048168132610",
     * "type": "DIRECTORY",
     * "flag": null
     * },
     * {
     * "id": "1035550277669203970",
     * "code": "100201-query",
     * "name": "common.query",
     * "parentId": "1035550274938712065",
     * "type": "BUTTON",
     * "flag": null
     * },
     * {
     * "id": "1035550277824393218",
     * "code": "100201-save",
     * "name": "common.save",
     * "parentId": "1035550274938712065",
     * "type": "BUTTON",
     * "flag": null
     * },
     * {
     * "id": "1035550277937639425",
     * "code": "100201-delete",
     * "name": "common.delete",
     * "parentId": "1035550274938712065",
     * "type": "BUTTON",
     * "flag": null
     * },
     * {
     * "id": "1035541549842673665",
     * "code": "100101-query",
     * "name": "common.query",
     * "parentId": "1035541525687676929",
     * "type": "BUTTON",
     * "flag": null
     * },
     * {
     * "id": "1035541551004495874",
     * "code": "100101-save",
     * "name": "common.save",
     * "parentId": "1035541525687676929",
     * "type": "BUTTON",
     * "flag": null
     * },
     * {
     * "id": "1035541551927242753",
     * "code": "100101-delete",
     * "name": "common.delete",
     * "parentId": "1035541525687676929",
     * "type": "BUTTON",
     * "flag": null
     * }
     * ]
     */
    @GetMapping("/query/user/menuAndButtonList")
    public ResponseEntity<List<RoleAssignMenuButtonDTO>> getAllMenuAndButton() throws URISyntaxException {
        List<RoleAssignMenuButtonDTO> list = roleMenuService.getAllMenuAndButton();
        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * @deprecated 不调用该接口，调用下面getUserList。
     * @api {GET} /query/userList 【角色权限】用户列表查询分页
     * @apiDescription 根据租户ID，（帐套ID 或 公司 ID），取用户列表 分页
     * 如果传了帐套ID，则取帐套下的用户
     * 如果传了公司ID，则取公司下的用户
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} tenantId 租户ID
     * @apiParam (请求参数) {Long} [companyId] 公司ID
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页条数
     * @apiSuccess (返回参数) {Long} id  用户ID
     * @apiSuccess (返回参数) {String} login  登录账号
     * @apiSuccess (返回参数) {String} fullName  姓名
     * @apiSuccess (返回参数) {String} email  邮箱
     * @apiSuccess (返回参数) {String} title  职务
     * @apiSuccess (返回参数) {String} mobile  手机号
     * @apiSuccess (返回参数) {String} employeeId  员工号
     * @apiSuccess (返回参数) {Boolean} activated  是否激活
     * @apiSuccess (返回参数) {Integer} status  状态 正常1001,待离职 1002，已离职 1003
     * @apiSuccess (返回参数) {String} companyName  公司名称
     * @apiSuccess (返回参数) {Long} companyId  公司ID
     * @apiSuccess (返回参数) {String} tenantName  租户名称
     * @apiSuccess (返回参数) {Long} tenantId  租户ID
     * @apiSuccess (返回参数) {Integer} lockStatus  锁定状态 未锁定 2001 ,锁定 2002
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/userRole/query/userList?tenantId=1022057230117146625&page=0&size=2
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1013",
     * "login": "askn54095559",
     * "userOid": null,
     * "companyOid": null,
     * "password": null,
     * "fullName": "习大大",
     * "firstName": null,
     * "lastName": null,
     * "email": "daidai@qq.com",
     * "mobile": "18516008359",
     * "employeeId": "5559",
     * "title": null,
     * "activated": true,
     * "authorities": [
     * {
     * "name": "ROLE_USER",
     * "authority": "ROLE_USER"
     * }
     * ],
     * "departmentOid": null,
     * "departmentName": null,
     * "filePath": null,
     * "avatar": null,
     * "status": 1001,
     * "companyName": "上海汉得融晶信息科技有限公司3",
     * "corporationOid": null,
     * "language": "zh_cn",
     * "financeRoleOid": null,
     * "companyId": "1005",
     * "tenantId": "1022057230117146625",
     * "directManager": null,
     * "directManagerId": null,
     * "directManagerName": null,
     * "passwordAttempt": 0,
     * "lockStatus": 2001,
     * "deviceVerificationStatus": null,
     * "tenantName": "上海汉得融晶信息科技有限公司3",
     * "senior": false,
     * "deleted": false,
     * "roleList": [
     * {
     * "id": "1029943470084898818",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "roleCode": "R001",
     * "roleName": "测试角色null",
     * "tenantId": "1022057230117146625"
     * },
     * {
     * "id": "1029919265725378561",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "roleCode": "R002",
     * "roleName": "测试角色2",
     * "tenantId": "1022057230117146625"
     * }
     * ]
     * },
     * {
     * "id": "1008",
     * "login": "askn54095555",
     * "userOid": null,
     * "companyOid": null,
     * "password": null,
     * "fullName": "孙漂亮",
     * "firstName": null,
     * "lastName": null,
     * "email": "piaoliang@qq.com",
     * "mobile": "18516008355",
     * "employeeId": "5555",
     * "title": null,
     * "activated": true,
     * "authorities": [
     * {
     * "name": "ROLE_USER",
     * "authority": "ROLE_USER"
     * }
     * ],
     * "departmentOid": null,
     * "departmentName": null,
     * "filePath": null,
     * "avatar": null,
     * "status": 1001,
     * "companyName": "上海汉得融晶信息科技有限公司3",
     * "corporationOid": null,
     * "language": "zh_cn",
     * "financeRoleOid": null,
     * "companyId": "1005",
     * "tenantId": "1022057230117146625",
     * "directManager": null,
     * "directManagerId": null,
     * "directManagerName": null,
     * "passwordAttempt": 0,
     * "lockStatus": 2001,
     * "deviceVerificationStatus": null,
     * "tenantName": "上海汉得融晶信息科技有限公司3",
     * "senior": false,
     * "deleted": false,
     * "roleList": [
     * {
     * "id": "1029943470084898818",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "roleCode": "R001",
     * "roleName": "测试角色null",
     * "tenantId": "1022057230117146625"
     * },
     * {
     * "id": "1029919265725378561",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "roleCode": "R002",
     * "roleName": "测试角色2",
     * "tenantId": "1022057230117146625"
     * }
     * ]
     * }
     * ]
     */
    @GetMapping("/query/userList")
    public ResponseEntity<List<UserRoleListDTO>> getUserListByCond(@RequestParam(required = true) Long tenantId,
                                                                       @RequestParam(required = false) Long companyId,
                                                                       @RequestParam(required = false) String login,
                                                                       @RequestParam(required = false) String fullName,
                                                                       @RequestParam(required = false) String mobile,
                                                                       @RequestParam(required = false) String email,
                                                                       @RequestParam(required = false) List<UUID> departmentOid,
                                                                       Pageable pageable)  {
        Page page = PageUtil.getPage(pageable);
        List<UserRoleListDTO> list = userService.getUserListByCond(tenantId, login, fullName, mobile, email, page);
        return new ResponseEntity(list, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    /**
     * @api {get} /api/query/usersList 根据搜索条件进行人员查询 (员工管理)
     * @apiDescription   原接口artemis模块 api/users/v3/search  ControlSearchUserV3 (参考)
     * @apiGroup  Auth2Service
     * @apiParam (请求参数){String} Long tenantID 套账id
     * @apiParam (请求参数){String} keyword 工号/姓名/手机号/邮箱
     * @apiParam (请求参数){Integer} status 员工状态 在职1001/待离职1002/离职1003
     * @apiParam (请求参数){String} departmentOid 部门
     * @apiParam (请求参数){List} corporationOid 公司ID
     * @apiParam (请求参数){Pageable}pageable 分页
     * @apiSuccess (返回参数) {Long} id  用户ID
     * @apiSuccess (返回参数) {String} login  登录账号
     * @apiSuccess (返回参数) {String} fullName  姓名
     * @apiSuccess (返回参数) {String} email  邮箱
     * @apiSuccess (返回参数) {String} title  职务
     * @apiSuccess (返回参数) {String} mobile  手机号
     * @apiSuccess (返回参数) {String} employeeId  员工号
     * @apiSuccess (返回参数) {Boolean} activated  是否激活
     * @apiSuccess (返回参数) {Integer} status  状态 正常1001,待离职 1002，已离职 1003
     * @apiSuccess (返回参数) {String} companyName  公司名称
     * @apiSuccess (返回参数) {Long} companyId  公司ID
     * @apiSuccess (返回参数) {String} tenantName  租户名称
     * @apiSuccess (返回参数) {Long} tenantId  租户ID
     * @apiParamExample {json} 请求报文
     * http://localhost:8000/auth/api/userRole/query/usersList?tenantId=1050629004792754178&sort=status&page=0
     * &size=10&keyword=&status=all&roleType=TENANT
     *   [
     *       {
     *       "id": "1065",
     *       "login": "ikzx020610000_LEAVED_1541600409400",
     *       "userOid": "fe3c1d51-0e33-42f2-b376-f8ef41bdac8f",
     *       "companyOid": null,
     *       "password": null,
     *       "fullName": "TEST",
     *      "firstName": null,
     *        "lastName": null,
     *       "email": "133210300061@qq.com_LEAVED_1541600409400",
     *       "mobile": null,
     *       "employeeId": "10000_LEAVED",
     *       "title": null,
     *       "activated": false,
     *       "authorities": [
     *       {
     *       "name": "ROLE_USER",
     *       "authority": "ROLE_USER"
     *       }
     *       ],
     *       "departmentOid": null,
     *       "departmentName": null,
     *       "filePath": null,
     *       "avatar": null,
     *       "status": 1003,
     *       "companyName": "上海清浅信息科技有限公司",
     *       "corporationOid": null,
     *       "language": null,
     *       "financeRoleOid": null,
     *       "companyId": "1053",
     *       "tenantId": "1050629004792754178",
     *       "directManager": null,
     *       "directManagerId": null,
     *       "directManagerName": null,
     *       "passwordAttempt": 0,
     *       "lockStatus": 2001,
     *       "deviceVerificationStatus": null,
     *       "tenantName": null,
     *       "roleList": [
     *       {
     *       "id": "1051774468940492802",
     *       "createdDate": null,
     *       "createdBy": null,
     *       "lastUpdatedDate": null,
     *       "lastUpdatedBy": null,
     *       "versionNumber": null,
     *       "deleted": false,
     *       "enabled": true,
     *       "roleCode": "admin007",
     *       "roleName": "管理员",
     *       "tenantId": "1050629004792754178"
     *       }
     *       ],
     *   "deleted": false,
     *  "senior": false
     *   },
     *   ]
     *
     */
    @GetMapping("/query/usersList")
    public ResponseEntity<List<UserRoleListDTO>> getUserList(@RequestParam(required = false) String keyword,
                                                             @RequestParam(required = true) Long tenantId,
                                                             @RequestParam(required = false) String status,
                                                             @RequestParam(required = false, defaultValue = "false") Boolean isInactiveSearch,
                                                             Pageable pageable) throws URISyntaxException, UnsupportedEncodingException {
        Page page = PageUtil.getPage(pageable);
        List<UserRoleListDTO> list = userService.listWithRoleByCondition(keyword == null ? null : keyword.trim(),tenantId,status,isInactiveSearch,page);
        String keywordValue = "";
        if (StringUtils.hasText(keyword)) {
            keywordValue = URLEncoder.encode(keyword.trim(), "UTF-8");
        }
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders( page, "/api/userRole/query/usersList");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/userRole/query/roles/dataAuthority 【角色权限】用户分配角色数据权限查询
     * @apiDescription 用户分配角色查询
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} userId 用户ID
     * @apiParam (请求参数) {String} [roleCode] 角色代码 模糊查询
     * @apiParam (请求参数) {String} [roleName] 角色名称 模糊查询
     * @apiParam (请求参数) {Integer} page 当前页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/userRole/query/roles/dataAuthority?userId=1013&roleCode=R00&roleName=测试&page=0&size=20
     * @apiSuccessExample {json} 返回报文:
     * [
        {
        "id": "1077102998342643713",
        "createdDate": "2018-12-24T15:25:41.067+08:00",
        "createdBy": "1",
        "lastUpdatedDate": "2018-12-24T15:25:41.068+08:00",
        "lastUpdatedBy": "1",
        "versionNumber": 1,
        "enabled": true,
        "userId": "3",
        "roleId": "1051670921947246594",
        "dataAuthorityId": null,
        "validDateFrom": null,
        "validDateTo": null,
        "roleCode": "admin",
        "roleName": "管理员",
        "dataAuthorityCode": null,
        "dataAuthorityName": null
        }
        ]
     */
    @GetMapping("/query/roles/dataAuthority")
    public ResponseEntity<List<UserAssignRoleDataAuthority>> listSelectedUserRolesByCond(@RequestParam(required = true) Long userId,
                                                     @RequestParam(required = false) String roleCode,
                                                     @RequestParam(required = false) String roleName,
                                                     @RequestParam(required = false) String dataAuthorityName,
                                                     @RequestParam(required = false) String validDateFrom,
                                                     @RequestParam(required = false) String validDateTo,
                                                     Pageable pageable) throws URISyntaxException {
        ZonedDateTime dateFrom = DateUtil.stringToZonedDateTime(validDateFrom);
        ZonedDateTime dateTo = DateUtil.stringToZonedDateTime(validDateTo);
        Page page = PageUtil.getPage(pageable);
        List<UserAssignRoleDataAuthority> list = userRoleService.listSelectedUserRolesByCond(userId, roleCode, roleName,dataAuthorityName,dateFrom,dateTo, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/userRole/query/roles/dataAuthority");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/userRole/query/all/roles 【角色权限】用户分配角色查询
     * @apiDescription 用户分配角色查询
     * queryFlag: ALL 查当前租户下所有启用的角色，ASSIGNED查 当前租户下租户已分配的启用的角色
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} userId 用户ID
     * @apiParam (请求参数) {String} [roleCode] 角色代码 模糊查询
     * @apiParam (请求参数) {String} [roleName] 角色名称 模糊查询
     * @apiParam (请求参数) {String} [queryFlag] 查询范围: ALL 查所有，ASSIGNED查用户已分配的角色
     * @apiParam (请求参数) {Integer} page 当前页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/userRole/query/roles?userId=1013&queryFlag=ASSIGNED&roleCode=R00&roleName=测试&page=0&size=20
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1029943470084898818",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "roleCode": "R001",
     * "roleName": "测试角色null",
     * "tenantId": "1022057230117146625"
     * },
     * {
     * "id": "1029919265725378561",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "roleCode": "R002",
     * "roleName": "测试角色2",
     * "tenantId": "1022057230117146625"
     * },
     * {
     * "id": "1029919290434023426",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": null,
     * "createdBy": null,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "roleCode": "R003",
     * "roleName": "测试角色3",
     * "tenantId": "1022057230117146625"
     * }
     * ]
     */
    @GetMapping("/query/all/roles")
    public ResponseEntity<List<Role>> listAllRolesByCond(@RequestParam(required = false) String roleCode,
                                                     @RequestParam(required = false) String roleName) throws URISyntaxException {
        List<Role> list = userRoleService.listAllRolesByCond(roleCode, roleName);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * @api {POST} /api/userRole/batch/create 【角色权限】用户分配角色批量
     * @apiDescription 用户分配角色
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} userId 用户ID
     * @apiParam (请求参数) {Long} roleId 角色ID
     * @apiParam (请求参数) {Long} dataAuthorityId    数据权限id
     * @apiParam (请求参数) {ZonedDateTime} validDateFrom    有效日期从
     * @apiParam (请求参数) {ZonedDateTime} validDateTo    有效日期至
     * @apiParamExample {json} 请求报文:
     * [
            {
            "userId": 3,
            "roleId": 1051670921947246594,
            "dataAuthorityId": 1082616210917146625,
            "validDateFrom":"2019-01-08T20:34:01.424+08:00",
            "validDateTo":"2019-01-08T20:34:01.424+08:00"
            }
        ]
     * @apiSuccessExample {json} 返回报文:
     * {
     *     true
     * }
     */
    @PostMapping("/batch/create")
    public ResponseEntity createUserRole(@RequestBody List<UserRole> list) {
        return ResponseEntity.ok(userRoleService.createUserRoleBatch(list));
    }

    /**
     * @api {PUT} /api/userRole/batch/update 【角色权限】用户分配角色更新批量
     * @apiDescription 更新用户关联角色 只允许修改enabled和deleted字段
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} id ID字段
     * @apiParam (请求参数) {Boolean} enabled 启用标识
     * @apiParam (请求参数) {Boolean} deleted 删除标识
     * @apiParam (请求参数) {Long} versionNumber 版本号
     * @apiParam (请求参数) {Long} dataAuthorityId    数据权限id
     * @apiParam (请求参数) {ZonedDateTime} validDateFrom    有效日期从
     * @apiParam (请求参数) {ZonedDateTime} validDateTo    有效日期至
     * @apiParamExample {json} 请求报文:
     * [
     *  {
        "id": "1082838246270189570",
        "createdDate": "2019-01-09T11:15:30.735+08:00",
        "createdBy": "1",
        "lastUpdatedDate": "2019-01-09T11:15:30.735+08:00",
        "lastUpdatedBy": "1",
        "versionNumber": 1,
        "enabled": true,
        "userId": "3",
        "roleId": "1051670921947246594",
        "dataAuthorityId": "1082616210917146625",
        "validDateFrom": "2019-01-08T20:34:01.424+08:00",
        "validDateTo": "2019-02-08T20:34:01.424+08:00"
        }
     * ]
     * @apiSuccessExample {json} 返回报文:
     * {
     *  true
     * }
     */
    @PutMapping("/batch/update")
    public ResponseEntity updateUserRole(@RequestBody List<UserRole> list) {
        return ResponseEntity.ok(userRoleService.updateUserRoleBatch(list));
    }

}
