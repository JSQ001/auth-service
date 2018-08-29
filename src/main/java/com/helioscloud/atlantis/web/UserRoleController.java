package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.LoginInformationUtil;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.Menu;
import com.helioscloud.atlantis.domain.Role;
import com.helioscloud.atlantis.domain.UserRole;
import com.helioscloud.atlantis.dto.MenuTreeDTO;
import com.helioscloud.atlantis.dto.UserDTO;
import com.helioscloud.atlantis.dto.UserRoleDTO;
import com.helioscloud.atlantis.service.RoleMenuService;
import com.helioscloud.atlantis.service.UserRoleService;
import com.helioscloud.atlantis.service.UserService;
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
     * "isEnabled": true,
     * "isDeleted": false,
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
     * "isEnabled": true,
     * "isDeleted": false,
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
     * "isEnabled": true,
     * "isDeleted": false,
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
     * @api {PUT} /api/userRole/update 【角色权限】用户分配角色更新
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
     * @api {GET} /api/roleMenu/query/user 【角色权限】用户关联角色查询
     * @apiDescription 查询用户关联角色【分页】
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
        Long userId = LoginInformationUtil.getCurrentUserID();
        List<MenuTreeDTO> list = roleMenuService.getMenuTreeByUserId(userId);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * @api {GET} /query/user/menuList 【角色权限】用户获取菜单列表
     * @apiDescription 根据当前登录用户，取对应所有角色分配的菜单列表
     * @apiGroup Auth2Service
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/userRole/query/user/menuList
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1032635715402903554",
     * "isEnabled": null,
     * "isDeleted": null,
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
     * "isEnabled": null,
     * "isDeleted": null,
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
     * "isEnabled": null,
     * "isDeleted": null,
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
     * "isEnabled": null,
     * "isDeleted": null,
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
     * "isEnabled": null,
     * "isDeleted": null,
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
        Long userId = LoginInformationUtil.getCurrentUserID();
        List<Menu> list = roleMenuService.getMenuListByUserId(userId);
        return new ResponseEntity(list, HttpStatus.OK);
    }


    /**
     * @api {GET} /query/userList 【角色权限】用户列表查询分页
     * @apiDescription 根据租户ID，（帐套ID 或 公司 ID），取用户列表 分页
     * 如果传了帐套ID，则取帐套下的用户
     * 如果传了公司ID，则取公司下的用户
     * @apiGroup Auth2Service
     * @apiParam (请求参数) {Long} tenantId 租户ID
     * @apiParam (请求参数) {Long} [setOfBooksId] 帐套ID
     * @apiParam (请求参数) {Long} [companyId] 公司ID
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页条数
     * @apiSuccess (返回参数) {Long} id  用户ID
     * @apiSuccess (返回参数) {String} login  登录账号
     * @apiSuccess (返回参数) {String} fullName  姓名
     * @apiSuccess (返回参数) {String} email  邮箱
     * @apiSuccess (返回参数) {String} title  职务
     * @apiSuccess (返回参数) {String} mobile  手机号
     * @apiSuccess (返回参数) {String} employeeID  员工号
     * @apiSuccess (返回参数) {Boolean} activated  是否激活
     * @apiSuccess (返回参数) {Integer} status  状态 正常1001,待离职 1002，已离职 1003
     * @apiSuccess (返回参数) {String} companyName  公司名称
     * @apiSuccess (返回参数) {Long} companyId  公司ID
     * @apiSuccess (返回参数) {String} tenantName  租户名称
     * @apiSuccess (返回参数) {Long} tenantId  租户ID
     * @apiSuccess (返回参数) {String} setOfBooksName  帐套名称
     * @apiSuccess (返回参数) {Long} setOfBooksId  帐套ID
     * @apiSuccess (返回参数) {Integer} lockStatus  锁定状态 未锁定 2001 ,锁定 2002
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/userRole/query/userList?tenantId=1022057230117146625&page=0&size=2
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1013",
     * "login": "askn54095559",
     * "userOID": null,
     * "companyOID": null,
     * "password": null,
     * "fullName": "习大大",
     * "firstName": null,
     * "lastName": null,
     * "email": "daidai@qq.com",
     * "mobile": "18516008359",
     * "employeeID": "5559",
     * "title": null,
     * "activated": true,
     * "authorities": [
     * {
     * "name": "ROLE_USER",
     * "authority": "ROLE_USER"
     * }
     * ],
     * "departmentOID": null,
     * "departmentName": null,
     * "filePath": null,
     * "avatar": null,
     * "status": 1001,
     * "companyName": "上海汉得融晶信息科技有限公司3",
     * "corporationOID": null,
     * "language": "zh_CN",
     * "financeRoleOID": null,
     * "companyId": "1005",
     * "tenantId": "1022057230117146625",
     * "directManager": null,
     * "directManagerId": null,
     * "directManagerName": null,
     * "setOfBooksId": "1022057239839543298",
     * "setOfBooksName": "默认账套",
     * "passwordAttempt": 0,
     * "lockStatus": 2001,
     * "deviceVerificationStatus": null,
     * "tenantName": "上海汉得融晶信息科技有限公司3",
     * "senior": false,
     * "deleted": false,
     * "roleList": [
     * {
     * "id": "1029943470084898818",
     * "isEnabled": true,
     * "isDeleted": false,
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
     * "isEnabled": true,
     * "isDeleted": false,
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
     * "userOID": null,
     * "companyOID": null,
     * "password": null,
     * "fullName": "孙漂亮",
     * "firstName": null,
     * "lastName": null,
     * "email": "piaoliang@qq.com",
     * "mobile": "18516008355",
     * "employeeID": "5555",
     * "title": null,
     * "activated": true,
     * "authorities": [
     * {
     * "name": "ROLE_USER",
     * "authority": "ROLE_USER"
     * }
     * ],
     * "departmentOID": null,
     * "departmentName": null,
     * "filePath": null,
     * "avatar": null,
     * "status": 1001,
     * "companyName": "上海汉得融晶信息科技有限公司3",
     * "corporationOID": null,
     * "language": "zh_CN",
     * "financeRoleOID": null,
     * "companyId": "1005",
     * "tenantId": "1022057230117146625",
     * "directManager": null,
     * "directManagerId": null,
     * "directManagerName": null,
     * "setOfBooksId": "1022057239839543298",
     * "setOfBooksName": "默认账套",
     * "passwordAttempt": 0,
     * "lockStatus": 2001,
     * "deviceVerificationStatus": null,
     * "tenantName": "上海汉得融晶信息科技有限公司3",
     * "senior": false,
     * "deleted": false,
     * "roleList": [
     * {
     * "id": "1029943470084898818",
     * "isEnabled": true,
     * "isDeleted": false,
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
     * "isEnabled": true,
     * "isDeleted": false,
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
    public ResponseEntity<List<UserDTO>> getUserListByTenantAndBooksId(@RequestParam(required = true) Long tenantId,
                                                                       @RequestParam(required = false) Long setOfBooksId,
                                                                       @RequestParam(required = false) Long companyId,
                                                                       Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<UserDTO> list = userService.getUserListByTenantAndBooksId(tenantId, setOfBooksId, companyId, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/userRole/query/userList");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

}
