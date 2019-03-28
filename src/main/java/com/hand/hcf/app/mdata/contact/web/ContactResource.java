package com.hand.hcf.app.mdata.contact.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.contact.domain.Contact;
import com.hand.hcf.app.mdata.contact.dto.*;
import com.hand.hcf.app.mdata.contact.enums.EmployeeStatusEnum;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.utils.HeaderUtil;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.domain.ExportConfig;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.core.exception.core.ValidationError;
import com.hand.hcf.core.exception.core.ValidationException;
import com.hand.hcf.core.handler.ExcelExportHandler;
import com.hand.hcf.core.service.ExcelExportService;
import com.hand.hcf.core.util.PageUtil;
import com.hand.hcf.core.util.TypeConversionUtils;
import com.hand.hcf.core.web.dto.ImportResultDTO;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/*import com.hand.hcf.app.client.attachment.AttachmentCO;*/

/**
 * REST controller for managing Contact.
 */
@RestController
@RequestMapping("/api")
public class ContactResource {

    private final Logger log = LoggerFactory.getLogger(ContactResource.class);

    @Autowired
    private ContactService contactService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ExcelExportService excelExportService;

    /**
     * GET  /contacts/:id -> get the "id" contact.
     */
    @RequestMapping(value = "/contacts/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ContactDTO> getContact(@PathVariable Long id) {
        log.debug("REST request to get Contact : {}", id);
        ContactDTO contactDTO = contactService.contactToContactDTO(contactService.findOne(id));
        return Optional.ofNullable(contactDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /contacts/:id -> delete the "id" contact.
     */
    @RequestMapping(value = "/contacts/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        log.debug("REST request to delete Contact : {}", id);
        contactService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contact", id.toString())).build();
    }

    @RequestMapping(value = "/contact/add/headPortrait", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<AttachmentCO> addHeadPortrait(@RequestParam("file") MultipartFile file) {
        if (file == null) {
            throw new ObjectNotFoundException(MultipartFile.class, "file not found");
        }
        return ResponseEntity.ok(contactService.updateHeadPortraitByEmployeeId(file));
    }

    /**
     * GET  get headPortrait
     */
    @RequestMapping(value = "/contacts/headPortrait",
        method = RequestMethod.GET)
    @Timed
    public ResponseEntity<AttachmentCO> getHeadPortrait() {
        AttachmentCO AttachmentCO = contactService.getHeadPortraitByUserOid(OrgInformationUtil.getCurrentUserOid());
        return ResponseEntity.ok(AttachmentCO);
    }


    //以下为用户接口迁移
    /**
     * POST  /users -> Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     * </p>
     */
    /**
     *
     * @api {post} /api/refactor/users/v2 创建用户V2
     * @apiGroup User
     * @apiVersion 0.1.0
     * @apiParamExample {json} 请求参数
    {
    "departmentPath" : null,
    "mobile" : "13645454545",
    "fullName" : "测试人员",
    "employeeID" : "A113",
    "email" : "13645454545@163.com",
    "title" : "技术员",
    "corporation" : null,
    "leavingDate" : null,
    "status" : 1001,
    "companyOID" : "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
    "departmentOID" : "2cbd0780-ca07-4a34-9827-d8da0132aea6",
    "departmentName" : "市场部",
    "userOID" : null,
    "employeeType" : null,
    "duty" : null,
    "birthday" : "1995-11-11",
    "rank" : null,
    "customFormValues" : [],
    "corporationOID" : "3479fd3f-103e-4282-a39d-2e6052a17522",
    "entryTime" : "2017-11-11"
    }
     * @apiSuccessExample {json} 响应结果
    {
    "customFormValues" : [],
    "departmentPath" : "市场部",
    "mobile" : "13645454545",
    "fullName" : "测试人员",
    "employeeID" : "A113",
    "email" : "13645454545@163.com",
    "title" : "技术员",
    "corporation" : "上海xx有限公司",
    "status" : 1001,
    "departmentName" : "市场部",
    "entryTime" : "2017-11-10T16:00:00Z",
    "birthday" : "1995-11-10T16:00:00Z",
    "userOID" : "c54c78d1-580b-48fa-ad59-3e332f4f66ee",
    "departmentOID" : "2cbd0780-ca07-4a34-9827-d8da0132aea6",
    "corporationOID" : "3479fd3f-103e-4282-a39d-2e6052a17522",
    "companyOID" : "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
    "manager" : false
    }
     *
     */
    @RequestMapping(value = "/refactor/users/v2", method = RequestMethod.POST)
    public ResponseEntity<UserDTO> createUserForControl(@RequestBody UserDTO userDTO){
        if (userDTO.getUserOid() != null) {
            throw new ValidationException(new ValidationError("User", "userOID must be null"));
        }
        return ResponseEntity.ok(contactService.upsertUserForControl(userDTO,OrgInformationUtil.getCurrentUserOid(),OrgInformationUtil.getCurrentTenantId()));
    }

    @RequestMapping(value = "/refactor/users/v2", method = RequestMethod.PUT)
    public ResponseEntity<UserDTO> updateUserForControl(@RequestBody UserDTO userDTO){
        return ResponseEntity.ok(contactService.upsertUserForControl(userDTO,OrgInformationUtil.getCurrentUserOid(),OrgInformationUtil.getCurrentTenantId()));
    }

    /**
     * @api {get} /api/users/oid/{userOid} 获取手用户详情
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
    @RequestMapping(value = "/users/oid/{userOid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserDTO> getUser(@PathVariable("userOid") UUID userOid) {
        log.debug("REST request to save User : {}");
        return ResponseEntity.ok().body(contactService.getUserDTOWithAuthorityAndDepartment(userOid));
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
    @RequestMapping(value = "/users/oids",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    //适用于大量查询
    public ResponseEntity<List<UserDTO>> getUsers(@RequestParam("userOids") Set<UUID> userOids)
            throws URISyntaxException {
        return new ResponseEntity<>(contactService.listUserDTOByUserOids(userOids), HttpStatus.OK);
    }

    /**
     * @api {post} /api/users/oids 获取用户概要信息
     * @apiGroup User
     * @apiVersion 0.1.0
     * @apiParam {String} userOids 用户Oid
     * @apiParamExample {json} 请求示例
     * ["9fd4f538-5d98-4ff4-932e-709ad5f497c3"]
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
    @RequestMapping(value = "/users/oids",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<UserDTO>> getUsersByOids(@RequestBody Set<UUID> userOids)
            throws URISyntaxException {
        return new ResponseEntity<>(contactService.listUserDTOByUserOids(userOids), HttpStatus.OK);
    }


    /**
     * @api {post} /api/users 分页获取当前用户所在公司所有有效用户
     * @apiGroup User
     * @apiVersion 0.1.0
     * @apiParamExample {json} 请求样例
     * ?page=1&size=10
     * @apiSuccessExample {json} 响应示例
     * [
     * {
     * "id": 174405,
     * "login": "bxui67628694222",
     * "userOid": "9fd4ef13-3a64-4fa3-a97a-32be555b3637",
     * "companyOid": "cf2b3694-b4f8-4aca-b233-111748eb025b",
     * "password": null,
     * "fullName": "345",
     * "firstName": null,
     * "lastName": null,
     * "email": "ningvvxxchao.liu@hand-china.com",
     * "mobile": null,
     * "employeeId": "8694222",
     * "title": null,
     * "activated": true,
     * "authorities": [
     * "ROLE_USER"
     * ],
     * "departmentOid": "d94a14e6-0de7-4577-b51d-1ff96ae0284e",
     * "departmentName": "华润项目组",
     * "filePath": null,
     * "avatar": null,
     * "status": 1001,
     * "companyName": null,
     * "corporationOid": "03a6f9c8-bd18-4e32-9615-c05f2de212fd",
     * "language": null,
     * "pageRoles": [],
     * "financeRoleOid": null,
     * "createdDate": "2017-09-21T02:21:04Z",
     * "lastUpdatedBy": "9b887ecd-37d2-388f-be52-26d232659d62",
     * "lastUpdatedDate": "2017-09-21T02:24:48Z",
     * "deleted": false,
     * "senior": false
     * },
     * {
     * "id": 126086,
     * "login": "0162150374",
     * "userOid": "57a738e2-3271-4686-b969-70029dbfcc89",
     * "companyOid": "cf2b3694-b4f8-4aca-b233-111748eb025b",
     * "password": null,
     * "fullName": "Aigie Heng",
     * "firstName": null,
     * "lastName": null,
     * "email": "aigie.heng@hand-sg.com",
     * "mobile": "0162150374",
     * "employeeId": "13816",
     * "title": "Senior Consultant",
     * "activated": false,
     * "authorities": [
     * "ROLE_USER"
     * ],
     * "departmentOid": "92e935a3-cd58-4e7d-82a2-7c2c464b5028",
     * "departmentName": "汉得新加坡分公司",
     * "filePath": null,
     * "avatar": null,
     * "status": 1001,
     * "companyName": null,
     * "corporationOid": null,
     * "language": null,
     * "pageRoles": [],
     * "financeRoleOid": null,
     * "createdDate": "2017-03-09T23:30:02Z",
     * "lastUpdatedBy": "5d0913bd-c9d6-3534-9c8b-e8b7df4b3511",
     * "lastUpdatedDate": "2017-03-09T23:30:02Z",
     * "deleted": false,
     * "senior": false
     * }
     * ]
     */
    @RequestMapping(value = "/users",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<UserDTO>> getAllValidUsers(Pageable pageable)
            throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<UserDTO> users = contactService.getValidUsersByCompanyId(OrgInformationUtil.getCurrentCompanyOid(), page);

        return new ResponseEntity<>(users, PageUtil.getTotalHeader(page), HttpStatus.OK);
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
    @RequestMapping(value = "/search/users/all", method = RequestMethod.GET)
    public ResponseEntity<List<UserDTO>> searchAllUser(@RequestParam(name = "keyword") String keyword, Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        List<UserDTO> users = contactService.listUserDTOByKeyword(keyword.trim(), page);
        if (page == null) {
            throw new ValidationException(new ValidationError("keyword", "keyword.must.more.than.2.characters"));
        }
        return new ResponseEntity<>(users, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    /**
     * @api {post} /api/search/users/entire?keyword=xxx 根据关键字搜索所有用户(带部门信息)
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
    @Transactional
    @RequestMapping(value = "/search/users/entire", method = RequestMethod.GET)
    public ResponseEntity<List<UserDTO>> searchAllUserWithDepartment(@RequestParam(name = "keyword") String keyword, Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        List<UserDTO> users = contactService.findAvaliableUserDTO(keyword.trim(), page);
        if (page == null) {
            throw new ValidationException(new ValidationError("keyword", "keyword.must.more.than.2.characters"));
        }

        return new ResponseEntity<>(users, PageUtil.getTotalHeader(page), HttpStatus.OK);

    }

    /**
     * @api {delete} /api/users/delete/{userOid} 根据用户Oid删除用户
     * @apiGroup User
     * @apiVersion 0.1.0
     * @apiParam {String} userOid 用户Oid
     */

    @Transactional
    @RequestMapping(value = "/search/users/by/{keyword}", method = RequestMethod.GET)
    public ResponseEntity<List<UserDTO>> searchUserByKeyword(@PathVariable("keyword") String keyword, @RequestParam(value = "isCompany", required = false) boolean isCompany, @RequestParam(name = "operationCode", required = false) Integer operationCode, Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        List<UserDTO> users = contactService.listUserDTOByKeywordAndCompany(keyword.trim(), isCompany, page);
        return new ResponseEntity<>(users, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "/search/users/by", method = RequestMethod.GET)
//    @Deprecated
    public ResponseEntity<List<UserDTO>> searchUserAll(@RequestParam(value = "isCompany", required = false) boolean isCompany, @RequestParam(name = "operationCode", required = false) Integer operationCode, Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        List<UserDTO> users = contactService.listUserDTOByKeywordAndCompany(null, isCompany, page);
        return new ResponseEntity<>(users, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    /**
     * @api {post} /api/users/set/leaved/{userOid} 立即离职
     * @apiGroup User
     * @apiVersion 0.1.0
     * @apiParam {String} userOid 用户Oid
     */
    @RequestMapping(value = "users/set/leaved/{userOid}", method = RequestMethod.POST)
    public ResponseEntity<Void> setLeavingDate(@PathVariable("userOid") UUID userOid) {
        contactService.leaveOffice(userOid);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {post} /api/users/set/cancel/leaved/{userOid} 取消待离职
     * @apiGroup User
     * @apiVersion 0.1.0
     * @apiParam {String} userOid 用户Oid
     */
    @RequestMapping(value = "users/set/cancel/leaved/{userOid}", method = RequestMethod.POST)
    public ResponseEntity<Void> cancelLeaving(@PathVariable("userOid") UUID userOid) {
        contactService.cancelLeaveOffice(userOid);
        return ResponseEntity.ok().build();
    }


    /**
     * 根据公司Oid、输入文本、法人实体Oid、部门Oid、职务查询用户信息
     * @param customEnumerationItemOid：值列表项Oid
     * @param keyword：输入文本
     * @param corporationOids：法人实体Oid
     * @param departmentOids：部门Oid
     * @param title：职务
     * @param pageable：分页对象
     * @return
     */
    /**
     * @api {get} /api/users/search/companyId/term 根据公司Oid、输入文本、法人实体Oid、部门Oid、职务查询用户信息
     * @apiGroup User
     * @apiVersion 0.1.0
     * @apiParam {String} customEnumerationItemOid 值列表项Oid
     * @apiParam {String} keyword 输入文本
     * @apiParam {String[]} corporationOids 法人实体Oid
     * @apiParam {String[]} departmentOids 部门Oid
     * @apiParam {Object} pageable 分页对象
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
    @RequestMapping(value = "/users/search/company/term", method = RequestMethod.GET)
    public ResponseEntity<List<UserDTO>> searchUsersByCompanyOidAndTerm(
            @RequestParam(name = "customEnumerationItemOid", required = false) UUID customEnumerationItemOid,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "corporationOids", required = false) List<UUID> corporationOids,
            @RequestParam(name = "departmentOids", required = false) List<UUID> departmentOids,
            @RequestParam(name = "title", required = false) String title,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<UserDTO> users = contactService.findByCompanyOidAndTerm(customEnumerationItemOid, OrgInformationUtil.getCurrentCompanyOid(), keyword, corporationOids, departmentOids, title, page);
        return new ResponseEntity<>(users, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }


    /**
     * @api {get} /api/users/v2/{userOid} 人员信息查询V2
     * @apiGroup User
     * @apiVersion 0.1.0
     * @apiParam {String} userOid 用户Oid
     * @apiSuccessExample {json} 响应示例
     * {
     * "customFormValues": [],
     * "departmentPath": "销售部啊",
     * "mobile": "13323454321",
     * "fullName": "李佳易",
     * "employeeId": "a1_DELETED",
     * "email": "yunfei.ma@huilianyi.com",
     * "title": "222",
     * "corporation": "甄汇科技股份有限公",
     * "status": 1001,
     * "departmentName": "销售部啊",
     * "entryTime": "2000-09-08T16:00:00Z",
     * "birthday": "2017-10-29T16:00:00Z",
     * "gender": 1,
     * "employeeType": "1级工程师",
     * "duty": "职务002",
     * "rank": "大boss",
     * "userOid": "363d8ebf-28f8-48d9-aae7-c0e37a46e682",
     * "departmentOid": "6c92e975-da3c-4b42-87f2-f3fd1bc66087",
     * "corporationOid": "ffc9340a-59cf-46ab-a14c-8308a9cf11e4",
     * "companyOid": "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
     * "manager": false
     * }
     */
    @RequestMapping(value = "/users/v2/{userOid}", method = RequestMethod.GET)
    public ResponseEntity<UserDTO> getUserInfoV2(@PathVariable UUID userOid, @RequestParam(name = "app", required = false, defaultValue = "false") boolean isApp) {
        return ResponseEntity.ok(contactService.getUserInfoV2ByUserOid(userOid, isApp, OrgInformationUtil.getCurrentTenantId(), true));
    }


    /**
     * 根据公司oid和用户oid将用户移到对应公司
     * @param companyOidFrom：公司oid
     * @param companyOidTo：新公司oid
     * @param userOids：用户oid集合
     * @param selectMode：选择模式
     * @return
     */
    /**
     * @api {get} /api/users/move 根据公司oid和用户oid将用户移到对应公司
     * @apiGroup User
     * @apiVersion 0.1.0
     * @apiParam {String} companyOidFrom 公司oid
     * @apiParam {String} companyOidTo 新公司oid
     * @apiParam {String[]} userOids 用户oid集合
     * @apiParam {String} selectMode 选择模式
     */

    @RequestMapping(value = "/users/move",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    // @PreAuthorize("hasRole('" + AuthoritiesConstants.USER + "')")
    public ResponseEntity<Void> moveCompanyUsers(@RequestBody UserMoveDTO userMoveDTO) {
        log.debug("REST request to move CompanyReceipted users from : {}, to: {}", userMoveDTO.getCompanyOidFrom(), userMoveDTO.getCompanyOidTo());
        contactService.moveCompanyUsers(userMoveDTO.getCompanyOidFrom(), userMoveDTO.getUserOids(), userMoveDTO.getCompanyOidTo(), userMoveDTO.getSelectMode());
        return ResponseEntity.ok(null);
    }

    /*通过员工工号或姓名，模糊查询租户下的员工*/
    @RequestMapping("/select/user/by/name/or/code")
    public ResponseEntity<List<UserDTO>> selectByInfoLike(
            @RequestParam(value = "keyword", required = false) String key,
            @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        List<UserDTO> result = contactService.listUserDTOByQO(ContactQO.builder()
                .hasDepartment(true)
                .tenantId(OrgInformationUtil.getCurrentTenantId())
                .setOfBooksId(setOfBooksId)
                .keyContact(key)
                .build(),page);

        return new ResponseEntity<>(result, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }


    /*通过员工工号或姓名，模糊查询公司下的员工*/
    @RequestMapping("/select/user/by/name/or/code/and/company")
    public ResponseEntity<List<UserDTO>> selectByInfoLikeAndCompany(@RequestParam(value = "keyword", required = false) String key, @RequestParam(value = "companyId") Long companyId, Pageable pageable) {

        Page page = PageUtil.getPage(pageable);
        List<UserDTO> result = contactService.listUserDTOByQO(ContactQO.builder()
                .tenantId(OrgInformationUtil.getCurrentTenantId())
                .companyId(companyId)
                .keyContact(key)
                .build(),page);
        return new ResponseEntity<>(result, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    /**
     * 通过员工工号或姓名或公司名称，模糊查询公司下的员工
     * @param code
     * @param name
     * @param companyName
     * @param setOfBooksId
     * @param pageable
     * @return
     */
    @GetMapping("/select/user/by/name/code/companyname")
    public ResponseEntity<List<UserDTO>> selectByCodeOrNameOrCompany(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            Pageable pageable) {
        Page page = PageUtil.getPage(pageable);

        List<Long> companyIds = companyService.selectList(
                new EntityWrapper<Company>().like("name", companyName)
        ).stream().map(Company::getId).collect(Collectors.toList());

        if (setOfBooksId == null) {
            setOfBooksId = OrgInformationUtil.getCurrentSetOfBookId();
        }

        List<UserDTO> result = contactService.listUserDTOByQO(ContactQO.builder()
                .tenantId(OrgInformationUtil.getCurrentTenantId())
                .setOfBooksId(setOfBooksId)
                .fullName(name)
                .employeeId(code)
                .companyIds(companyIds)
                .build(),page);

        return new ResponseEntity<>(result, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    /*通过公司,部门,code和姓名查询员工*/
    @RequestMapping("/select/user/by/company/and/department")
    public ResponseEntity<List<UserDTO>> selectByCondition(
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "employeeId", required = false) String employeeId,
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "companyId", required = false) Long companyId,
            Pageable pageable) {

        Page page = PageUtil.getPage(pageable);
        List<UserDTO> result = contactService.listUserDTOByQO(ContactQO.builder()
                .companyId(companyId)
                .departmentId(departmentId)
                .fullName(fullName)
                .employeeId(employeeId)
                .build(),page);
        return new ResponseEntity<>(result, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }


    /**
     * @api {get} /api/users/v3/search 根据搜索条件进行人员查询
     * @apiGroup User
     * @apiVersion 0.1.0
     * @apiParam {String} keyword 工号/姓名/手机号/邮箱
     * @apiParam {Long} userGroupId 员工组id
     * @apiParam {Integer} status 员工状态 在职1001/待离职1002/离职1003
     * @apiParam {String} departmentOid 部门
     * @apiParam {String} corporationOid 公司，老公司表示法人实体，新公司表示公司
     * @apiParam {String} roleType 租户模式
     * @apiParam {Boolean} isInactiveSearch 是否搜索未激活用户
     * * @apiSuccessExample {json} 响应示例
     * {
     * "userOid": "4a52436c-b80e-422d-986c-e4d8bac51536",
     * "login": "12345678888",
     * "fullName": "oe / Potter Jerad",
     * "employeeId": "44444444",
     * "email": "qasas@qqa.com",
     * "title": null,
     * "departmentName": "E部门",
     * "activated": true,
     * "avatar": null,
     * "mobile": "12345678888",
     * "status": 1001,
     * "leavingDate": null,
     * "duty": null,
     * "rank": null,
     * "companyName": "上海xx有限公司",
     * "id": 174086
     * }
     */
    @RequestMapping(value = "/users/v3/search", method = RequestMethod.GET)
    public ResponseEntity<List<UserDTO>> ControlSearchUserV3(@RequestParam(required = false) String keyword,
                                                             @RequestParam(required = false) String status,
                                                             @RequestParam(required = false) List<UUID> departmentOid,
                                                             @RequestParam(required = false) List<UUID> corporationOid,
                                                             @RequestParam(required = false, defaultValue = "false") Boolean isInactiveSearch,
                                                             @RequestParam(required = false) UUID currentUserOid,
                                                             Pageable pageable)  {
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        Page page = PageUtil.getPage(pageable);
        List<UserDTO> list = contactService.listUserDTOByCondition(keyword == null ? null : keyword.trim(),
                tenantId,departmentOid,
                status,corporationOid,
                currentUserOid,
                page);
        return new ResponseEntity<>(list, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    //设置员工离职日期
    /**
     * @api {post} /api/users/{userOID}/set/leaving/date/{leavingDate} 设置员工离职日期
     * @apiGroup User
     * @apiVersion 0.1.0
     * @apiParam {String} userOID 用户OID
     * @apiParam {Date} leavingDate 离职日期,格式:yyyy-MM-dd HH:mm:ss
     */
    @RequestMapping(value = "users/{userOID}/set/leaving/date/{leavingDate}", method = RequestMethod.POST)
    public ResponseEntity<Void> setLeavingDate(@PathVariable("userOID") UUID userOID, @PathVariable("leavingDate") String leavingDate) {
        contactService.setLeavingDate(OrgInformationUtil.getCurrentTenantId(),userOID, leavingDate);
        return ResponseEntity.ok().build();
    }

    /**
     * 恢复入职

     * @api {post} /api/refactor//users/rehire/{userOID} 恢复入职
     * @apiGroup User
     * @apiVersion 0.1.0
     * @apiParam {String} userOID 用户OID
     *
     */
    @RequestMapping(value = "/refactor/users/rehire/{userOID}", method = RequestMethod.POST)
    public ResponseEntity<Void> rehireUsers(@PathVariable UUID userOID){
        Long currentUserId= OrgInformationUtil.getCurrentUserId();
        log.info("start to rehireUsers and this user's oid = {}",userOID);
        Contact contact = contactService.selectOne(new EntityWrapper<Contact>().eq("user_oid",userOID));
        if(contact == null){
            throw new BizException("6040015");
        }
        if(!contact.getTenantId().equals(OrgInformationUtil.getCurrentTenantId())){
            throw new BizException("6040015");
        }
        //不是离职状态的员工不能恢复入职
        if(!contact.getStatus().equals(EmployeeStatusEnum.LEAVED.getId())){
            throw new BizException("6040015");
        }
        contactService.recoverEntry(contact);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/user/info/export/template", method = RequestMethod.GET)
    @Timed
    public ResponseEntity exportUserInfoTemplate() {
        return ResponseEntity.ok(contactService.exportUserInfoImportTemplate());
    }

    @RequestMapping(value = "/user/bankcard/export/template", method = RequestMethod.GET)
    @Timed
    public ResponseEntity exportUserBankcardTemplate() {
        return ResponseEntity.ok(contactService.exportContactBankAccountImportTemplate());
    }

    @RequestMapping(value = "/user/idcard/export/template", method = RequestMethod.GET)
    @Timed
    public ResponseEntity exportUserIdcardTemplate() {
        return ResponseEntity.ok(contactService.exportContactCardImportTemplate());
    }

    /**
     * 导入
     */
    @PostMapping("/user/import/new")
    public ResponseEntity importWorkOrderNew(@RequestParam("file") MultipartFile file) throws Exception {
        try{
            UUID transactionLogUUID = contactService.importUserPublic(file);
            return ResponseEntity.ok(transactionLogUUID);
        }catch (IOException e){
            throw new BizException(RespCode.SYS_READ_FILE_ERROR);
        }
    }

    /**
     * 查询导入结果 导入第二步 查询导入成功多少，失败多少，失败的数据有哪些
     * @param transactionID
     * @return
     * @throws IOException
     */
    @GetMapping("/user/import/new/query/result/{transactionID}")
    public ResponseEntity queryResultInfo(@PathVariable("transactionID") String transactionID) throws IOException {
        ImportResultDTO importResultDTO = contactService.queryResultInfo(transactionID);
        return ResponseEntity.ok(importResultDTO);
    }

    /**
     * 导出错误信息  导出错误信息excel
     * @param transactionID
     * @throws IOException
     */
    @GetMapping("/user/import/new/error/export/{transactionID}")
    public ResponseEntity errorExport(
            @PathVariable("transactionID") String transactionID) throws IOException {
        return ResponseEntity.ok(contactService.exportFailedData(transactionID));
    }

    /**
     * 删除导入的数据 点击取消时删除当前导入的数据（删除临时表数据)
     * @param transactionID
     * @return
     */
    @DeleteMapping("/user/import/new/delete/{transactionID}")
    public ResponseEntity deleteImportData(@PathVariable("transactionID") String transactionID){
        return ResponseEntity.ok(contactService.deleteImportData(transactionID));
    }

    /**
     *  点击确定时 把临时表数据新增到正式表中
     * @param transactionID
     * @return
     */
    @PostMapping("/user/import/new/confirm/{transactionID}")
    public ResponseEntity confirmImport(@PathVariable("transactionID") String transactionID){
        return ResponseEntity.ok(contactService.confirmImport(transactionID));
    }

    @RequestMapping(value = "/export/user/info/new")
    public void exportUserInfoNew(HttpServletRequest request,
                                  @RequestBody ExportConfig exportConfig,
                                  HttpServletResponse response,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) String status,
                                  @RequestParam(required = false) List<UUID> departmentOid,
                                  @RequestParam(required = false) List<UUID> corporationOid,
                                  Pageable pageable) throws IOException {
        Page page = PageUtil.getPage(pageable);
        List<UserDTO> list = contactService.listWithRoleByCondition(keyword == null ? null : keyword.trim(),OrgInformationUtil.getCurrentTenantId(),departmentOid,status,corporationOid,false,page);
        int total = TypeConversionUtils.parseInt(page.getTotal());
        int threadNumber = total > 100000 ? 8 : 2;
        excelExportService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<UserDTO, UserDTO>() {
            @Override
            public int getTotal() {
                return total;
            }

            @Override
            public List<UserDTO> queryDataByPage(Page page) {
                List<UserDTO> list = contactService.exportUserDTO(keyword == null ? null : keyword.trim(),OrgInformationUtil.getCurrentTenantId(),departmentOid,status,corporationOid,page);
                return list;
            }

            @Override
            public UserDTO toDTO(UserDTO t) {
                return t;
            }

            @Override
            public Class<UserDTO> getEntityClass() {
                return UserDTO.class;
            }
        },threadNumber, request, response);
    }

    /**
     * 获取员工简易信息信息
     *
     * @param userSimpleInfoNotExcludeIdsDTO 请求body
     * @param pageable 分页
     * @return 员工简易信息
     */
    @PostMapping("/user/simpleInfo/query")
    public ResponseEntity listUsersByCond(@RequestBody UserSimpleInfoNotExcludeIdsDTO userSimpleInfoNotExcludeIdsDTO,
                                          Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        List<UserSimpleInfoDTO> list = contactService.listUsersByCond(userSimpleInfoNotExcludeIdsDTO.getEmployeeCode(), userSimpleInfoNotExcludeIdsDTO.getName(), userSimpleInfoNotExcludeIdsDTO.getCompanyId(), userSimpleInfoNotExcludeIdsDTO.getUnitId(), userSimpleInfoNotExcludeIdsDTO.getIds(),page);
        return new ResponseEntity<>(list, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

}
