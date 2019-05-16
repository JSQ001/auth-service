package com.hand.hcf.app.base.user.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.user.dto.PasswordUpdateDTO;
import com.hand.hcf.app.base.user.dto.UserDTO;
import com.hand.hcf.app.base.user.dto.UserQO;
import com.hand.hcf.app.base.user.service.UserService;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.PageUtil;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * REST controller for managing users.
 * <p/>
 * <p>This class accesses the User entity, and needs to fetch its collection of authorities.</p>
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * </p>
 * <p>
 * We use a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </p>
 * <p>Another option would be to have a specific JPA entity graph to handle this case.</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@Api(tags = "用户定义")
public class UserController {


    @Autowired
    private UserService userService;

    /**
     * 新增用户
     * @param userDTO
     * @return
     */
    @ApiOperation(value = "新增用户", notes = "新增用户")
    @RequestMapping( method = RequestMethod.POST)
    public ResponseEntity<UserDTO> createUserForControl(@RequestBody UserDTO userDTO)  {
        if (userDTO.getId() != null) {
            throw new BizException(RespCode.USER_ID_MUST_NULL);
        }
        return ResponseEntity.ok(userService.saveUserDto(userDTO, LoginInformationUtil.getCurrentTenantId()));
    }
    @ApiOperation(value = "修改用户", notes = "修改用户")
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<UserDTO> updateUserForControl(@RequestBody UserDTO userDTO)  {
        return ResponseEntity.ok(userService.saveUserDto(userDTO, LoginInformationUtil.getCurrentTenantId()));
    }

    /**
     * @api {get} /api/users/oid/{userOid} 获取用户详情
     * @apiGroup User
     * @apiVersion 0.1.0
     * @apiParam {String} userOid 用户Oid
     * @apiSuccessExample {json} 激活成功并修改密码
     * {
     * "customFormValues" : [],
     * "departmentPath" : "市场部",
     * "mobile" : "13645454545",
     * "fullName" : "测试人员",
     * "employeeId" : "A113",
     * "email" : "13645454545@163.com",
     * "title" : "技术员",
     * "corporation" : "上海xx有限公司",
     * "status" : 1001,
     * "departmentName" : "市场部",
     * "entryTime" : "2017-11-10T16:00:00Z",
     * "birthday" : "1995-11-10T16:00:00Z",
     * "userOid" : "c54c78d1-580b-48fa-ad59-3e332f4f66ee",
     * "departmentOid" : "2cbd0780-ca07-4a34-9827-d8da0132aea6",
     * "corporationOid" : "3479fd3f-103e-4282-a39d-2e6052a17522",
     * "companyOid" : "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
     * "manager" : false
     * }
     */
    @RequestMapping(value = "/oid/{userOid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserDTO> getUser(@PathVariable("userOid") UUID userOid)  {
        log.debug("REST request to save User : {}");
        return ResponseEntity.ok().body(userService.getDtoByUserOid(userOid));
    }


    /**
     * @api {get} /api/users/oids 获取用户概要信息
     * @apiGroup User
     * @apiVersion 0.1.0
     * @apiParam {String} userOids 用户Oid
     * @apiParamExample {json} 请求示例
     * ?userOids=9fd4f538-5d98-4ff4-932e-709ad5f497c3&userOids=9fd4f538-5d98-4ff4-932e-709ad5f497c4
     * @apiSuccessExample {json} 响应示例
     * [
     * {
     * "userOid": "9fd4f538-5d98-4ff4-932e-709ad5f497c3",
     * "login": "18616808523",
     * "fullName": "陈浩",
     * "employeeId": "8883",
     * "email": "hao.chen05@hand-china.com",
     * "title": "技术总监",
     * "activated": true,
     * "avatar": "http://huilianyi-uat-static.oss-cn-shanghai.aliyuncs.com//headPortrait/2351daab-cc6f-450d-99ee-c1c637e8d38a-protrait.jpeg",
     * "department": {
     * "id": null,
     * "departmentOid": null,
     * "parent": null,
     * "children": null,
     * "name": "技术部",
     * "path": "甄汇科技|技术部",
     * "companyId": null,
     * "manager": null,
     * "users": [],
     * "status": 0,
     * "departmentCode": null,
     * "lastUpdatedDate": "2017-12-04T09:14:15Z"
     * },
     * "corporationOid": "30acb3f9-6ee8-44c5-87e5-ca21a17cffbf",
     * "corporationName": "上海甄汇信息科技有限公司",
     * "leavingDate": null,
     * "status": null,
     * "relevanceCustomEnumerationItem": false,
     * "phones": [],
     * "role": null,
     * "financeRoleOid": null,
     * "defaultCertificateNo": null
     * }
     * ]
     */
    @RequestMapping(value = "/oids",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    //适用于大量查询
    public ResponseEntity<List<UserDTO>> getUsers(@RequestParam("userOids") Set<UUID> userOids)
             {
        return new ResponseEntity<>(userService.listDTOByOids(new ArrayList<>(userOids)), HttpStatus.OK);
    }


    /**
     * GET  /users/:login -> get the "login" user.
     */
    /**
     * @api {post} /api/users/{login} 根据Login获取登录用户信息
     * @apiGroup User
     * @apiVersion 0.1.0
     * @apiParam {String} login 手机号码
     * @apiSuccessExample {json} 响应示例
     * {
     * "id": 3,
     * "login": "18616808523",
     * "userOid": "9fd4f538-5d98-4ff4-932e-709ad5f497c3",
     * "companyOid": "cf2b3694-b4f8-4aca-b233-111748eb025b",
     * "password": null,
     * "fullName": "陈浩",
     * "firstName": null,
     * "lastName": null,
     * "email": "hao.chen05@hand-china.com",
     * "mobile": "18616808523",
     * "employeeId": "8883",
     * "title": "技术总监",
     * "activated": true,
     * "authorities": [
     * "ROLE_COMPANY_FINANCE_RECEIVED",
     * "ROLE_COMPANY_ADMIN",
     * "ROLE_COMPANY_API",
     * "ROLE_USER",
     * "ROLE_COMPANY_FINANCE_ADMIN"
     * ],
     * "departmentOid": "f541465b-73a1-484f-83c0-56270468e246",
     * "departmentName": "技术部",
     * "filePath": null,
     * "avatar": null,
     * "status": 1001,
     * "companyName": null,
     * "corporationOid": "30acb3f9-6ee8-44c5-87e5-ca21a17cffbf",
     * "language": null,
     * "pageRoles": [],
     * "financeRoleOid": null,
     * "createdDate": "2016-02-01T04:43:06Z",
     * "lastUpdatedBy": "5d0913bd-c9d6-3534-9c8b-e8b7df4b3511",
     * "lastUpdatedDate": "2017-11-23T08:36:43Z",
     * "deleted": false,
     * "senior": false
     * }
     */
    @RequestMapping(value = "/{login:[_'.@a-z0-9-]+}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserDTO> getUser(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        return new ResponseEntity<>(userService.getDTOByLogin(login), HttpStatus.OK);
    }


    /**
     * @api {post} /api/search/all/users?keyword=xxx 根据关键字搜索所有用户
     * @apiGroup User
     * @apiVersion 0.1.0
     * @apiParam {String} keyword 搜索关键字
     * @apiSuccessExample {json} 响应示例
     * [{
     * "userOid" : "c54c78d1-580b-48fa-ad59-3e332f4f66ee",
     * "login" : "rhtw3850A113",
     * "fullName" : "测试人员",
     * "employeeId" : "A113",
     * "email" : "13645454545@163.com",
     * "title" : "技术员",
     * "departmentName" : "市场部",
     * "activated" : false,
     * "avatar" : null,
     * "mobile" : "13645454545",
     * "status" : 1001,
     * "leavingDate" : "2017-12-04T09:31:29Z",
     * "duty" : null,
     * "rank" : null
     * }
     * ]
     */
    @RequestMapping(value = "/search/all", method = RequestMethod.GET)
    public ResponseEntity<List<UserDTO>> searchAllUser(@RequestParam(name = "login",required = false) String login,
                                                       @RequestParam(name = "username",required = false) String userName,
                                                       @RequestParam(name = "email",required = false) String email,
                                                       @RequestParam(name = "mobile",required = false) String mobile,
                                                       @RequestParam(name = "keyword",required = false) String keyword,
                                                       Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        List<UserDTO> users = userService.pageDTOByQO( page, UserQO.builder().login(login)
                .userName(userName)
                .email(email)
                .mobile(mobile)
                .fuzzy(true)
                .tenantId(LoginInformationUtil.getCurrentTenantId())
                .keyword(keyword).build());

        return new ResponseEntity<>(users, PageUtil.getTotalHeader(page), HttpStatus.OK);

    }


    @RequestMapping(value = "/language/{language}", method = RequestMethod.POST)
    public ResponseEntity updateUserLanguage(@PathVariable String language) throws SQLException {
        if (!language.trim().isEmpty()) {
            boolean result = userService.updateUserLanguage(language, LoginInformationUtil.getUser().getLogin());
            return ResponseEntity.ok(result);
        } else {
            throw new BizException(RespCode.SYS_USER_LANG_IS_NULL);
        }
    }


    @ApiOperation(value = "更改密码用户当前密码", notes = "更改密码用户当前密码 开发：谢宾")
    @PostMapping("/update/password")
    public ResponseEntity<Boolean> updatePassword(@Valid @RequestBody PasswordUpdateDTO dto){
        return ResponseEntity.ok(userService.updatePassword(dto));
    }

}

