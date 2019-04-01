package com.hand.hcf.app.mdata.implement.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.mdata.contact.domain.Contact;
import com.hand.hcf.app.mdata.contact.domain.ContactBankAccount;
import com.hand.hcf.app.mdata.contact.service.ContactBankAccountService;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.contact.service.UserGroupService;
import com.hand.hcf.app.mdata.contact.web.adapter.UserAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;

@Slf4j
@RestController
public class ContactControllerImpl {

    @Autowired
    private ContactService contactService;
    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private ContactBankAccountService contactBankAccountService;

    /**
     * 根据员工id集合查询员工信息
     * @param ids 员工Id集合
     * @param keyWord 关键字
     * @return
     */
    public List<ContactCO> listByUserIdsConditionByKeyWord(@RequestBody List<Long> ids,
                                                           @RequestParam(value = "keyWord",required = false) String keyWord){
        return contactService.listByUserIdsConditionByKeyWord(ids, keyWord);
    }

    //jiu.zhao 修改三方接口 20190329
    public List<ContactCO> listByUserIds(List<Long> ids) {
        return (List)(CollectionUtils.isEmpty(ids) ? new ArrayList() : this.contactService.listByUserIdsConditionByKeyWord(ids, (String)null));
    }

    public List<UserGroupCO> listUserGroupByUserGroupIds(List<Long> userGroupIds) {
        return (List)(CollectionUtils.isEmpty(userGroupIds) ? new ArrayList() : this.userGroupService.listUserGroupByIds(userGroupIds));
    }

    /**
     * 根据用户ID查询用户信息
     * @param id
     * @return
     */
    public ContactCO getById(@RequestParam("id") Long id){
        return UserAdapter.getUserCOByUserDTO(contactService.getUserDTOByUserId(id));
    }
    /**
     * 根据用户Oid获取用户信息
     *
     * @param userOid
     * @return
     */
    public ContactCO getByUserOid(@PathVariable("userOid") UUID userOid){

        return UserAdapter.getUserCOByUserDTO(contactService.getUserDTOByUserOid(userOid));
    }

    /**
     * 根据人员Oid集合查询人员信息
     * @param userOids
     * @return
     */
    public List<ContactCO> listByUserOids(@RequestParam("userOids") List<String> userOids){
        return contactService.listUserDTOByUserOids(userOids.stream().map(e -> UUID.fromString(e)).collect(Collectors.toSet()))
                .stream().map(e -> UserAdapter.getUserCOByUserDTO(e)).collect(Collectors.toList());
    }

    /**
     * 通过员工代码查询用户信息
     *
     * @param userCode
     * @return
     */
    public ContactCO getByUserCode(@RequestParam("userCode") String userCode){

        return UserAdapter.getUserCOByUserDTO(contactService.getUserDTOByUserCode(userCode));
    }


    /**
     * 根据员工组Id集合获取员工组信息
     * @param ids
     * @return
     */
    public List<UserGroupCO> listUserGroupByIds(@RequestBody List<Long> ids){
        return userGroupService.listUserGroupByIds(ids);
    }



    /**
     * 判断该用户是否属于所传的人员组
     *
     * @param: judgeUserCO
     * @return
     */
    public Boolean judgeUserInUserGroups(@RequestBody JudgeUserCO judgeUserCO){
        Boolean permission = userGroupService.hasUserGroupPermissionForMuti(judgeUserCO.getIdList(), judgeUserCO.getUserId());
        return permission;
    }


    /**
     * 通过员工工号或姓名，模糊查询租户下的员工
     * @param key
     * @return
     */
    public List<ContactCO> listByKeyWord(@RequestParam(value = "key",required = false) String key){
        return contactService.listCOByKeyWord(key);
    }

    /**
     * 分页条件获取当前租户下的用户信息
     * @param page
     * @param size
     * @return
     */
    public Page<ContactCO> pageConditionNameAndIgnoreIds(@RequestParam(value = "employeeId",required = false) String employeeId,
                                                         @RequestParam(value = "fullName",required = false) String fullName,
                                                         @RequestParam(value = "keyWord",required = false) String keyWord,
                                                         @RequestBody(required = false) List<Long> ignoreIds,
                                                         @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                         @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        Page<ContactCO> mybatisPage = new Page<>(page + 1, size);

        return contactService.pageCOConditionNameAndIgnoreIds(employeeId, fullName, keyWord, ignoreIds, mybatisPage);
    }

    /**
     * 分页条件获取当前租户下特定范围的用户信息
     * @param page
     * @param size
     * @return
     */
    public Page<ContactCO> pageConditionNameAndIds(@RequestParam(value = "employeeId",required = false) String employeeId,
                                                         @RequestParam(value = "fullName",required = false) String fullName,
                                                         @RequestParam(value = "keyWord",required = false) String keyWord,
                                                         @RequestBody(required = false) List<Long> ids,
                                                         @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                         @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        Page<ContactCO> mybatisPage = new Page<>(page + 1, size);

        return contactService.pageCOConditionNameAndIds(employeeId, fullName, keyWord, ids, mybatisPage);
    }

    /**
     * 根据员工ID和银行账号获取银行账户信息
     *
     * @param userId
     * @param number
     * @return
     */
    public UserBankAccountCO getUserBankAccountByUserIdAndAccountNumber(@RequestParam("userId") Long userId,
                                                                        @RequestParam("number") String number){
        return contactBankAccountService.getUserBankAccountByUserIdAndAccountNumber(userId, number);
    }

    /**
     * 根据员工组Id获取员工组信息
     * @param userGroupId
     * @return
     */
    public UserGroupCO getUserGroupByUserGroupId(@RequestParam("userGroupId") Long userGroupId){
        return userGroupService.getUserGroupByUserGroupId(userGroupId);
    }

    /**
     * 根据员工组Oid获取员工组信息
     * @param userGroupOid
     * @return
     */
    public UserGroupCO getUserGroupByUserGroupOid(@RequestParam("userGroupOid") String userGroupOid){
        return userGroupService.getUserGroupByUserGroupOid(userGroupOid);
    }

    /**
     * 根据员工组Oid集合获取员工组信息
     * @param userGroupOids
     * @return
     */
    public List<UserGroupCO> listUserGroupByUserGroupOids(@RequestBody List<String> userGroupOids){
        return userGroupService.listUserGroupByUserGroupOids(userGroupOids);
    }

    /**
     * 根据用户组oid获取用户信息
     *
     * @param userGroupOid
     * @param page
     * @param size
     * @return
     */
    public Page<ContactCO> pageByUserGroupOid(@PathVariable("userGroupOid") UUID userGroupOid,
                                           @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                           @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        Page<ContactCO> mybatisPage = new Page<>(page + 1, size);
        return userGroupService.pageUserCOByUserGroupOid(userGroupOid,mybatisPage);
    }


    /**
     * 根据用户id查询其组织架构Id信息
     * @param userId
     * @return
     */
    public OrganizationUserCO getOrganizationCOByUserId(@RequestParam("userId") Long userId){
        return contactService.getOrganizationCOByUserId(userId);
    }


    /**
     * 根据人员组ID查询其固定分配的人员
     * @param userGroupId
     * @return
     */
    public List<ContactCO> listByUserGroupId(@RequestParam("userGroupId") Long userGroupId){
        return userGroupService.listUserCOByUserGroupId(userGroupId);
    }

    /**
     * 根据人员组id集合查询人员组信息及其固定分配的人员id
     * @param ids
     * @return
     */
    public List<UserGroupCO> listUserGroupAndUserIdByGroupIds(@RequestBody List<Long> ids){
        return userGroupService.listUserGroupAndUserIdByGroupIds(ids);
    }

    public List<UserGroupCO> listUserGroupByUserId(@RequestParam("userId") Long userId,
                                                   @RequestParam(value = "enabled",required = false) Boolean enabled) {
        return userGroupService.listUserGroupByUserId(userId, enabled);
    }

    public List<UserGroupCO> listUserGroupAndUserIdsBySetOfBooksId(@RequestParam("setOfBooksId") Long setOfBooksId,
                                                                   @RequestParam(value = "enabled",required = false) Boolean enabled) {
        return userGroupService.listUserGroupAndUserIdsBySetOfBooksId(setOfBooksId, enabled);
    }

    public List<ContactCO> listByEmployeeCodeConditionCompanyIdAndDepartId(@RequestParam(value = "companyId",required = false) Long companyId,
                                                                        @RequestParam(value = "departmentId",required = false) Long departmentId,
                                                                        @RequestParam("employeeCode") String employeeCode) {
        return contactService.listByEmployeeCodeConditionCompanyIdAndDepartId(companyId, departmentId, employeeCode);
    }

    /**
     * 根据用户oid和部门级别递归查询主管oid
     * @param userOid
     * @param level
     * @return
     */
    public UUID getDirectManager(@RequestParam("userOid") UUID userOid,
                                 @RequestParam("level") Integer level) {
        return contactService.recursiveSearchDirectManager(userOid,level);
    }

    public List<ContactCO> listUserByTenantId(Long tenantId) {
        return contactService.listUserByTenantId(tenantId);
    }

    public List<ContactCO> listUsersByEmployeeCodes(@RequestBody List<String> employeeCodes) {
        return contactService.listUsersByEmployeeCodes(employeeCodes);
    }

    public List<ContactCO> listUsersByDepartmentId(@RequestParam("departmentId") Long departmentId) {
        return contactService.listUsersByDepartmentId(departmentId);
    }

    /**
     * 根据用户ID获取员工主银行账号信息
     * @param userId
     * @return
     */
    public UserBankAccountCO getContactPrimaryBankAccountByUserId(@RequestParam(value = "userId") Long userId) {
        Contact contactByUserId = contactService.getContactByUserId(userId);
        if(contactByUserId != null){
            ContactBankAccount oneByUserOidAndIsPrimary = contactBankAccountService.findOneByUserOidAndIsPrimary(contactByUserId.getUserOid(), true);
            return contactBankAccountService.toUserBankAccountCO(userId, oneByUserOidAndIsPrimary);
        }
        return null;
    }

}

