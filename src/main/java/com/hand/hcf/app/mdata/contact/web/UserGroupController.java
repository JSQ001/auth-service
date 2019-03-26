package com.hand.hcf.app.mdata.contact.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.contact.domain.UserGroup;
import com.hand.hcf.app.mdata.contact.dto.ConditionViewDTO;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.contact.dto.UserGroupDTO;
import com.hand.hcf.app.mdata.contact.dto.UserGroupMappingDTO;
import com.hand.hcf.app.mdata.contact.service.UserGroupService;
import com.hand.hcf.core.security.AuthoritiesConstants;
import com.hand.hcf.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by markfredchen on 16/10/6.
 */
@RestController
@RequestMapping("/api/user/groups")
public class UserGroupController {

    @Autowired
    UserGroupService service;

    /**
     * 新建公司或租户人员组
     * @param dto
     * @param roleType
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(value = AuthoritiesConstants.COMPANY_ADMIN)
    public ResponseEntity<UserGroupDTO> createUserGroup(@RequestBody UserGroupDTO dto,
                                                        @RequestParam(value = "roleType", required = false) String roleType) {
        boolean isTenant = true;
        dto.setTenantId(OrgInformationUtil.getCurrentTenantId());
        if (!OrgInformationUtil.hasTenantAuthority(roleType)) {
            isTenant = false;
            dto.setCompanyOid(OrgInformationUtil.getCurrentCompanyOid());
        }
        UserGroupDTO result = service.createUserGroupV2(dto, isTenant);
        return ResponseEntity.ok(result);
    }

    /**
     * 修改公司或者租户人员组
     * @param dto
     * @param roleType
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(value = AuthoritiesConstants.COMPANY_ADMIN)
    public ResponseEntity<UserGroupDTO> updateUserGroup(@RequestBody UserGroupDTO dto,
                                                        @RequestParam(value = "roleType", required = false) String roleType) {

        boolean isTenant = true;
        dto.setTenantId(OrgInformationUtil.getCurrentTenantId());
        if (!OrgInformationUtil.hasTenantAuthority(roleType)) {
            isTenant = false;
            dto.setCompanyOid(OrgInformationUtil.getCurrentCompanyOid());
        }
        UserGroupDTO result = service.updateUserGroupV2(dto, isTenant);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据人员组Oid查询人员组
     * @param userGroupOid
     * @param showDetail
     * @return
     */
    @RequestMapping(value = "/{userGroupOid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserGroupDTO> getUserGroup(@PathVariable("userGroupOid") UUID userGroupOid,
                                                     @RequestParam(value = "showDetail", required = false) boolean showDetail) {
        UserGroupDTO userGroup = service.getUserGroup(userGroupOid, showDetail);
        return ResponseEntity.ok(userGroup);
    }

    /**
     * 根据Oids 查询
     * @param userGroupOids
     * @return
     */
    @RequestMapping(value = "/oids", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserGroupDTO> getUserGroups(@RequestBody List<UUID> userGroupOids) {
        List<UserGroup> userGroupList = service.getUserGroupByOids(userGroupOids);
        return userGroupList.stream().map(ug->service.userGroupToUserGroupDTO(ug)).collect(Collectors.toList());
    }

    /**
     * 查询公司或者租户下所有人员组
     * @param roleType
     * @param enabled
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/company", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserGroupDTO>> getCompanyUserGroup(@RequestParam(value = "roleType", required = false) String roleType,
                                                                  @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                                  Pageable pageable)  {
        Page page = PageUtil.getPage(pageable);
        List<UserGroupDTO> userGroupDTOS=null;
       if(OrgInformationUtil.hasTenantAuthority(roleType)){
            userGroupDTOS= service.findTenantGroups(OrgInformationUtil.getCurrentTenantId(),enabled,page);
       }else{
            userGroupDTOS = service.findUserGroupByCompanyOid(OrgInformationUtil.getCurrentCompanyOid(), enabled, page);
       }
        return new ResponseEntity<>(userGroupDTOS, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }


    //重复方法，确认无用后删除 TODO  DELETE
    @RequestMapping(value = "/company/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserGroupDTO>> getCompanyAllUserGroups(@RequestParam(value = "type", required = false) String type) {
        return ResponseEntity.ok(service.findUserGroupByCompanyOid(OrgInformationUtil.getCurrentCompanyOid(),true));
    }

    /**
     * 根据名称模糊查询公司或租户人员组
     * @param name
     * @param roleType
     * @param enabled
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserGroupDTO>> searchCompanyUserGroup(@RequestParam(name = "name", required = false) String name,
                                                                     @RequestParam(value = "roleType", required = false) String roleType,
                                                                     @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                                     Pageable pageable) {
        Page page= PageUtil.getPage(pageable);
        List<UserGroupDTO> userGroupDTOS = null;
        if(OrgInformationUtil.hasTenantAuthority(roleType)){
            userGroupDTOS = service.searchTenantUserGroupByName(name,OrgInformationUtil.getCurrentTenantId(),enabled,page);
        }else{
            userGroupDTOS = service.searchUserGroupByName(name, OrgInformationUtil.getCurrentCompanyOid(), enabled, page);
        }
        return new ResponseEntity<>(userGroupDTOS, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/id", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserGroupDTO> findOnebyId(@RequestParam(name = "id", required = false) Long id) throws URISyntaxException {
        UserGroupDTO userGroupDTO = service.findUserGroupById(id);
        return ResponseEntity.ok(userGroupDTO);
    }

    @RequestMapping(value = "/check", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(value = AuthoritiesConstants.COMPANY_ADMIN)
    public Boolean checkUserGroup(@RequestBody UserGroupDTO dto,
                                  @RequestParam(value = "roleType", required = false) String roleType) {
        UserGroup exist;
        if(OrgInformationUtil.hasTenantAuthority(roleType)){
            exist = service.selectTenantUsergroupByName(dto.getName(),OrgInformationUtil.getCurrentTenantId(),null);
        }else{
            exist = service.getUserGroup(OrgInformationUtil.getCurrentCompanyOid(),dto.getName());
        }
        return StringUtils.isEmpty(exist);
    }

    @RequestMapping(value = "/status", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(value = AuthoritiesConstants.COMPANY_ADMIN)
    public ResponseEntity<UserGroupDTO> updateUserGroupEnabled(@RequestBody UserGroupDTO dto,
                                                               @RequestParam(value = "roleType", required = false) String roleType) {
        boolean isTenant = true;
        dto.setTenantId(OrgInformationUtil.getCurrentTenantId());
        if (!OrgInformationUtil.hasTenantAuthority(roleType)) {
            isTenant = false;
            dto.setCompanyOid(OrgInformationUtil.getCurrentCompanyOid());
        }
        UserGroupDTO userGroupDTO = service.updateUserGroupV2(dto, isTenant);
        return ResponseEntity.ok(userGroupDTO);
    }


    /**
     * @apiDefine ConditionViewDTO 规则视图对象
     * @apiSuccess {Integer} conditionSeq  规则序号
     * @apiSuccess {List} conditionDetails 规则详情列表
     * @apiSuccess {Long} conditionDetails.UserGroupConditionDTO.id 规则细项Id
     * @apiSuccess {Long} conditionDetails.UserGroupConditionDTO.userGroupId 人员组Id
     * @apiSuccess {Integer} conditionDetails.UserGroupConditionDTO.conditionSeq 规则细项序号
     * @apiSuccess {String} conditionDetails.UserGroupConditionDTO.conditionLogic 规则细项逻辑符号（I(包含),E（不包含））
     * @apiSuccess {String} conditionDetails.UserGroupConditionDTO.conditionProperty 规则细项属性
     * @apiSuccess {boolean} conditionDetails.UserGroupConditionDTO.enabled 规则细项是否启用
     * @apiSuccess {List} conditionDetails.UserGroupConditionDTO.conditionValues 规则细项值列表
     * @apiSuccess {Long} conditionDetails.UserGroupConditionDTO.conditionValues.id 规则细项值id
     * @apiSuccess {Long} conditionDetails.UserGroupConditionDTO.conditionValues.conditionId 规则细项id
     * @apiSuccess {String} conditionDetails.UserGroupConditionDTO.conditionValues.conditionValue 规则细项值
     * @apiSuccess {String} conditionDetails.UserGroupConditionDTO.conditionValues.description 规则细项描述
     */

    /**
     * @api {POST} /api/user/groups/conditions/{userGroupId} 创建适用人员组规则，一次只能添加一个条件
     * @apiGroup UserGroup
     * @apiParam {Object} ConditionViewDTO 规则视图对象
     * @apiParam {Integer} ConditionViewDTO.conditionSeq  规则组序号
     * @apiParam {List} ConditionViewDTO.conditionDetails 详情列表
     * @apiParam {Long} ConditionViewDTO.UserGroupConditionDTO.userGroupId 人员组Id
     * @apiParam {Integer} ConditionViewDTO.UserGroupConditionDTO.conditionSeq 规则详情序号
     * @apiParam {String} UConditionViewDTO.serGroupConditionDTO.conditionLogic 规则详情逻辑符号（I(包含),E（不包含））
     * @apiParam {String} ConditionViewDTO.UserGroupConditionDTO.conditionProperty 规则详情属性
     * @apiParam {boolean} ConditionViewDTO.UserGroupConditionDTO.enabled 规则详情是否启用
     * @apiParam {List} ConditionViewDTO.UserGroupConditionDTO.conditionValues 规则详情值列表
     * @apiParam {Long} ConditionViewDTO.UserGroupConditionDetailDTO.conditionValues.conditionId 规则id
     * @apiParam {String} ConditionViewDTO.UserGroupConditionDetailDTO.conditionValues.conditionValue 规则细项值
     * @apiParam {String} ConditionViewDTO.UserGroupConditionDetailDTO.conditionValues.description 规则细项描述
     * @apiUse ConditionViewDTO
     * @apiSuccessExample {json} 成功响应示例
     * {
     * "conditionSeq": 3,
     * "conditionDetails": [
     * {
     * "id": "956161400748331009",
     * "userGroupId": "490",
     * "conditionSeq": 3,
     * "conditionLogic": "I",
     * "conditionProperty": "Department",
     * "conditionValues": [
     * {
     * "id": "956161400769302529",
     * "conditionId": "956161400748331009",
     * "conditionValue": "42496",
     * "description": "E部门"
     * }
     * ],
     * "enabled": true
     * }
     * ]
     * }
     */
    @RequestMapping(value = "/conditions/{userGroupId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConditionViewDTO> createUserCondition(@PathVariable("userGroupId") Long userGroupId,
                                                                @RequestBody ConditionViewDTO conditionViewDTO) {
        ConditionViewDTO view = service.createUserGroupCondition(userGroupId, conditionViewDTO);
        return ResponseEntity.ok(view);
    }

    /**
     * @api {PUT} /api/user/groups/conditions/{userGroupId} 修改人员组规则，一次只能修改一项
     * @apiGroup UserGroup
     * @apiParam {Object} ConditionViewDTO 规则视图对象
     * @apiParam {Integer} ConditionViewDTO.conditionSeq  规则组序号
     * @apiParam {List} ConditionViewDTO.conditionDetails 详情列表
     * @apiParam {Long} ConditionViewDTO.UserGroupConditionDTO.userGroupId 人员组Id
     * @apiParam {Integer} ConditionViewDTO.UserGroupConditionDTO.conditionSeq 规则详情序号
     * @apiParam {String} UConditionViewDTO.serGroupConditionDTO.conditionLogic 规则详情逻辑符号（I(包含),E（不包含））
     * @apiParam {String} ConditionViewDTO.UserGroupConditionDTO.conditionProperty 规则详情属性
     * @apiParam {boolean} ConditionViewDTO.UserGroupConditionDTO.enabled 规则详情是否启用
     * @apiParam {List} ConditionViewDTO.UserGroupConditionDTO.conditionValues 规则详情值列表
     * @apiParam {Long} ConditionViewDTO.UserGroupConditionDetailDTO.conditionValues.conditionId 规则id
     * @apiParam {String} ConditionViewDTO.UserGroupConditionDetailDTO.conditionValues.conditionValue 规则细项值
     * @apiParam {String} ConditionViewDTO.UserGroupConditionDetailDTO.conditionValues.description 规则细项描述
     * @apiUse ConditionViewDTO
     * @apiSuccessExample {json} 成功响应示例
     * {
     * "conditionSeq": 3,
     * "conditionDetails": [
     * {
     * "id": "956161400748331009",
     * "userGroupId": "490",
     * "conditionSeq": 3,
     * "conditionLogic": "I",
     * "conditionProperty": "Department",
     * "conditionValues": [
     * {
     * "id": "956161400769302529",
     * "conditionId": "956161400748331009",
     * "conditionValue": "42496",
     * "description": "E部门"
     * }
     * ],
     * "enabled": true
     * }
     * ]
     * }
     * }
     */
    @RequestMapping(value = "/conditions/{userGroupId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConditionViewDTO> updateUserCondition(@PathVariable("userGroupId") Long userGroupId,
                                                                @RequestBody ConditionViewDTO conditionViewDTO) {
        ConditionViewDTO view = service.updateUserGroupCondition(userGroupId, conditionViewDTO);
        return ResponseEntity.ok(view);
    }

    /**
     * @api {DELETE} /api/user/groups/conditions/{userGroupId}?seq={seq} 删除人员组固定序号规则项
     * @apiGroup UserGroup
     * @apiParam {Long} userGroupId 人员组Id
     * @apiParam {Integer} seq  序号
     * @apiSuccessExample {Boolean} 成功响应示例
     * true
     */
    @RequestMapping(value = "/conditions/{userGroupId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteUserCondition(@PathVariable("userGroupId") Long userGroupId,
                                                       @RequestParam("seq") int seq) {
        service.deleteUserGroupSeqCondition(userGroupId, seq);
        return ResponseEntity.ok(Boolean.TRUE);
    }

    /**
     * @api {GET} /api/user/groups/conditions?userGroupId={userGroupId}&userId={userId} 检验员工和人员组的归属关系
     * @apiGroup UserGroup
     * @apiParam {Long} userGroupId 人员组Id
     * @apiParam {Long} userId  员工Id
     * @apiSuccessExample {Boolean} 成功响应示例
     * true
     */
    @RequestMapping(value = "/conditions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> checkUserGroupAssociation(@RequestParam("userGroupId") Long userGroupId,
                                                             @RequestParam("userId") Long userId) {
        Boolean isPermission = service.hasUserGroupPermission(userGroupId, userId);
        return ResponseEntity.ok(isPermission);
    }

    /**
     * @api /api/user/groups/user/insert 【人员组-添加人员】
     * @apiGroup UserGroup
     * @apiParam {List<UUID>}userOids 人员oid
     * @apiParam {UUID} userGroupOid 人员组oid
     */
    @RequestMapping(value = "/user/insert",method = RequestMethod.POST)
    public void insertAssociateUserGroup(@RequestBody UserGroupMappingDTO userGroupMappingDTO) {
        service.insertAssociateUserGroup(userGroupMappingDTO.getUserOids(), userGroupMappingDTO.getUserGroupOid());
    }

    /**
     * @api /api/user/groups/user/{userGroupOid} 【人员组查询-人员信息】
     * @apiParam userGroupOid 人员组oid
     * @apiParam keyword
     * @apiParam pageable
     */
    @RequestMapping(value = "/user/{userGroupOid}", method = RequestMethod.GET)
    public ResponseEntity<List<UserDTO>> pageUserGroupUsersByUserGroupOID(@PathVariable("userGroupOid") UUID userGroupOid,
                                                                          @RequestParam(value = "keyword",required = false)String keyword, Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        List<UserDTO> result = service.pageUserGroupUsersByGroupOid(userGroupOid,keyword, page);
        HttpHeaders headers = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    /**
     * @api /api/user/groups/user/delete 【人员组-删除人员】
     * @apiGroup UserGroup
     * @apiParam {List<UUID>}userOids 人员oid
     * @apiParam {UUID} userGroupOid 人员组oid
     */
    @RequestMapping(value = "user/delete",method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteAssociatedUserGroup(@RequestBody UserGroupMappingDTO mapping) {
        service.deleteAssociateUserGroup(mapping.getUserOids(), mapping.getUserGroupOid());
    }

    /**
     * @api {}
     * @apiDescription
     * @apiGroup UserGroupController
     * @apiParam {String} userGroupId 人员组Id
     * @apiParam {String} status 状态
     * @apiParam {String} keyword 员工工号、姓名、手机号、邮箱
     * @apiParamExample json} Request-Param:
     * http://127.0.0.1:9083/api/user/groups/user/filter?page=0&size=10&systemCustomEnumerationType=&status=1001&userGroupId=1084704464138919937&roleType=TENANT
     * @apiSuccessExample {json} Success-Response:
     * [
            {
            "id": "1084663397902643202",
            "login": "admin0114",
            "userOid": "bae41550-9898-45f3-a250-819b40e1dbc8",
            "companyOid": "ae49acbc-51b4-48d5-8ede-645347b826a3",
            "password": "{bcrypt}$2a$10$SK0d05qPhUdBWuyZYU6ehuqNWN/bDSIjRyHFoJodFpLxgbbLqREr6",
            "entryDate": "2019-01-14 00:00:00",
            "birthday": "2000-01-14 00:00:00",
            "fullName": "Ashimo",
            "firstName": null,
            "lastName": null,
            "email": "ashimo@rj.com",
            "mobile": "17501143990",
            "mobileStatus": null,
            "countryCode": null,
            "phones": null,
            "employeeId": "0114",
            "title": null,
            "gender": 2,
            "genderCode": null,
            "employeeType": null,
            "employeeTypeCode": null,
            "rankCode": null,
            "dutyCode": null,
            "activated": true,
            "departmentOid": "08caaee1-db3a-4dd7-9eea-d4b1857d8edf",
            "departmentId": "1084707068063506434",
            "departmentName": "产品部",
            "departmentPath": "产品部",
            "senior": null,
            "filePath": null,
            "avatar": null,
            "deleted": false,
            "status": 1001,
            "companyName": "汉得融晶",
            "companyAccountCode": null,
            "corporationOid": "eee481ab-a585-4cc8-b5c6-615129d5c48f",
            "legalEntityName": null,
            "language": "zh_cn",
            "companyId": "1084660685513355265",
            "tenantId": "1083751703623680001",
            "directManager": null,
            "directManagerId": null,
            "directManagerName": null,
            "setOfBooksId": "1084658256482856961",
            "setOfBooksName": null,
            "passwordAttempt": 0,
            "lockStatus": 2001,
            "deviceVerificationStatus": null,
            "deviceValidate": null,
            "resetPassword": null,
            "tenantName": null,
            "roleList": null,
            "lastUpdatedBy": null,
            "lastUpdatedDate": null
            }
    ]
     */
    @GetMapping(value = "/user/filter",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDTO>> pageUserByConditionByIgnoreIds(@RequestParam(value = "userGroupId") Long userGroupId,
                                                                        @RequestParam(value = "keyword",required = false) String keyword,
                                                                        @RequestParam(required = false) String status,
                                                                        Pageable pageable){
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        Page page = PageUtil.getPage(pageable);
        List<UserDTO> list = service.pageUserByConditionByIgnoreIds(keyword == null ? null : keyword.trim(),
                userGroupId,
                tenantId,
                status,
                page);
        return new ResponseEntity<>(list, PageUtil.getTotalHeader(page), HttpStatus.OK);

    }

}
