package com.hand.hcf.app.mdata.contact.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.common.co.UserGroupCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.contact.domain.*;
import com.hand.hcf.app.mdata.contact.dto.*;
import com.hand.hcf.app.mdata.contact.persistence.*;
import com.hand.hcf.app.mdata.department.domain.Department;
import com.hand.hcf.app.mdata.department.service.DepartmentService;
import com.hand.hcf.app.mdata.department.service.DepartmentUserService;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.system.enums.SystemCustomEnumerationTypeEnum;
import com.hand.hcf.app.mdata.utils.PatternMatcherUtil;
import com.hand.hcf.app.mdata.utils.RespCode;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserGroupService extends BaseService<UserGroupMapper, UserGroup> {

    //名称通用长度限制
    public static final int NAME_SIZE = 50;
    //描述通用长度限制
    public static final int DESCRIPTION_SIZE = 100;


    @Autowired
    @Lazy
    DepartmentService departmentService;
    @Autowired
    private BaseI18nService baseI18nService;

    @Autowired
    private UserGroupConditionMapper userGroupConditionMapper;

    @Autowired
    private ContactService contactService;

    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;

    @Autowired
    private UserGroupConditionDetailMapper userGroupConditionDetailMapper;

    @Autowired
    @Qualifier("taskExecutor")
    Executor executor;

    @Autowired
    private UserGroupPermissionMapper userGroupPermissionMapper;

    @Autowired
    private UserGroupMapper userGroupMapper;

    @Autowired
    private UserGroupUserMapper userGroupUserMapper;

    @Autowired
    private MapperFacade mapper;

    @Autowired
    private DepartmentUserService departmentUserService;


    public UserGroupDTO createUserGroupV2(UserGroupDTO userGroupDTO, boolean isTenant) {

        UserGroup userGroup = userGroupDTOToUserGroup(userGroupDTO);
        if (userGroup.getUserGroupOid() != null) {
            throw new BizException(RespCode.USER_GROUP_OID_NOT_NULL);
        }
        if (userGroup.getName().length() > NAME_SIZE) {
            throw new BizException(RespCode.USER_GROUP_NAME_LENGTH_LT_50);
        }
        if (!StringUtils.isEmpty(userGroup.getComments()) && userGroup.getComments().length() > DESCRIPTION_SIZE) {
            throw new BizException(RespCode.USER_GROUP_DESC_LENGTH_LT_100);
        }
        if (isTenant) {
            // 校验租户人员组属性
            UserGroup nameUserGroup = this.selectTenantUsergroupByName(userGroup.getName(), userGroup.getTenantId(), true);
            if (nameUserGroup != null) {
                throw new BizException(RespCode.USER_GROUP_EXIST);
            }
            userGroup.setCompanyOid(null);
            this.checkTenantUserGroupCode(userGroup);
        } else {
            //校验公司人员组属性
            UserGroup nameUserGroup = this.findTopOneByCompanyOidAndName(userGroup.getCompanyOid(), userGroup.getName());
            if (nameUserGroup != null) {
                throw new BizException(RespCode.USER_GROUP_EXIST);
            }
            this.checkUserGroupCode(userGroup, userGroup.getCompanyOid());
        }
        userGroup.setUserGroupOid(UUID.randomUUID());
        UserGroup group = this.saveUserGroup(userGroup);
        baseI18nService.insertOrUpdateI18n(group.getI18n(), group.getClass(), group.getId());
        return userGroupToUserGroupDTO(group);
    }



    public UserGroupDTO updateUserGroupV2(UserGroupDTO dto, boolean isTenant) {
        UserGroup userGroup = this.getUserGroup(dto.getUserGroupOid());
        if (userGroup == null) {
            throw new BizException(RespCode.USER_GROUP_NOT_EXIST);
        }
        userGroup.setCode(dto.getCode());
        userGroup.setName(dto.getName());
        userGroup.setComments(dto.getComment());
        userGroup.setEnabled(dto.getEnabled());
        if (dto.getI18n().containsKey("comment")){
            dto.getI18n().put("comments",dto.getI18n().get("comment"));
            dto.getI18n().remove("comment");
        }
        userGroup.setI18n(dto.getI18n());
        if (userGroup.getName().length() > NAME_SIZE) {
            throw new BizException(RespCode.USER_GROUP_NAME_LENGTH_LT_50);
        }
        if (!StringUtils.isEmpty(userGroup.getComments()) && userGroup.getComments().length() > DESCRIPTION_SIZE) {
            throw new BizException(RespCode.USER_GROUP_DESC_LENGTH_LT_100);
        }
        // 校验人员组编码
        if (isTenant) {
            this.checkTenantUserGroupCode(userGroup);
        } else {
            this.checkUserGroupCode(userGroup, userGroup.getCompanyOid());
        }


        userGroup.setI18n(userGroup.getI18n());
        this.updateUserGroupMybatis(userGroup);
        baseI18nService.insertOrUpdateI18n(userGroup.getI18n(), userGroup.getClass(), userGroup.getId());
        return userGroupToUserGroupDTO(userGroup);
    }

    public UserGroup getUserGroup(UUID userGroupOid) {
        UserGroup userGroup = this.findByUserGroupOid(userGroupOid);
        return baseI18nService.convertOneByLocale(userGroup);
    }

    public UserGroup getUserGroup(UUID userGroupOid, UUID companyOid) {
        UserGroup group = this.findOneByUserGroupAndCompanyOid(userGroupOid, companyOid);
        UserGroup userGroup = baseI18nService.convertOneByLocale(group);
        return userGroup;
    }

    public UserGroupDTO getUserGroup(UUID userGroupOid, boolean showDetail) {
        UserGroup userGroup = this.findByUserGroupOid(userGroupOid);
        UserGroup userGroupI18n = baseI18nService.selectOneBaseTableInfoWithI18n(userGroup.getId(), UserGroup.class);
        userGroup = baseI18nService.convertOneByLocale(userGroup);
        userGroup.setI18n(userGroupI18n.getI18n());
        if (showDetail) {
            UserGroupDTO userGroupWithConditions = this.getUserGroupWithConditions(userGroup);
            return userGroupWithConditions;
        } else {
            return userGroupToUserGroupDTO(userGroup);
        }
    }

    public UserGroup getUserGroup(UUID companyOid, String userGroupName) {
        UserGroup userGroup = this.findTopOneByCompanyOidAndName(companyOid, userGroupName);
        return baseI18nService.convertOneByLocale(userGroup);
    }

    public List<UserGroup> getUserGroupByOids(List<UUID> userGroupOids) {
        List<UserGroup> userGroups = this.findByUserGroupOidIn(userGroupOids);
        return baseI18nService.convertListByLocale(userGroups);
    }

    public List<UserGroupDTO> findUserGroupByCompanyOid(UUID companyOid, Boolean enabled, Page page) {
        List<UserGroup> userGroups = this.findByCompanyOidOrderBylastUpdatedDateDesc(companyOid, enabled, page);
        List<UserGroup> userGroupList = baseI18nService.convertListByLocale(userGroups);
        return userGroupList.stream().map(ug->userGroupToUserGroupDTO(ug)).collect(Collectors.toList());
    }

    public List<UserGroupDTO> findUserGroupByCompanyOid(UUID companyOid, Boolean enabled) {
        List<UserGroup> userGroups = this.findByCompanyOidOrderBylastUpdatedDateDesc(companyOid, enabled);
        List<UserGroup> userGroupList = baseI18nService.convertListByLocale(userGroups);
        return userGroupList.stream().map(ug->userGroupToUserGroupDTO(ug)).collect(Collectors.toList());
    }

    public UUID getUserGroupOid(String userGroupName, UUID companyOid) {
        return this.findTopOneByCompanyOidAndName(companyOid,userGroupName).getUserGroupOid();
    }

    public List<UserDTO> pageUserGroupUsersByGroupOid(UUID userGroupOid, String keyword, Page page) {
        return contactService.listUserDTOByQO(ContactQO.builder()
                .userGroupOid(userGroupOid)
                .keyContact(keyword)
                .exLeaved(true)
                .orderByEmployeeId(true)
                .build(),page);
    }



    public List<UserGroup> getUserGroupByUserOid(UUID userOid) {
        List<UserGroup> userGroups= userGroupMapper.listByQO(UserGroupQO.builder()
                .userOid(userOid)
                .enabled(true)
                .build());

        return baseI18nService.convertListByLocale(userGroups);
    }

    public List<UserGroupUser> batchGetUserGroupByUserOid(List<UUID> userOids) {
        return userGroupUserMapper.findUserGroupUsers(userOids);

    }

    public void insertAssociateUserGroup(List<UUID> userOids, UUID userGroupOid) {
        UserGroup userGroup = getUserGroup(userGroupOid);
        if (userGroup == null) {
            throw new ObjectNotFoundException(UserGroup.class, userGroupOid);
        }
        if (userOids.size() <= 0) {
            throw new BizException(RespCode.USER_GROUP_USER_NOT_NULL);
        }
        userOids.stream().forEach(userOid ->
                userGroupUserMapper.associateUsersToGroup(IdWorker.getId(),userGroup.getId(),null,userOid));
  }

    public void deleteAssociateUserGroup(List<UUID> userOids, UUID userGroupOid) {
        UserGroup userGroup = getUserGroup(userGroupOid);
        if (userGroup == null) {
            throw new ObjectNotFoundException(UserGroup.class, userGroupOid);
        }
        userGroupUserMapper.deleteUsersFromGroup(userGroup.getId(),null,userOids);

    }

    /**
     * 根据用户组oid list查询
     *
     * @param userGroupOids
     * @return
     */
    public List<UserGroupDTO> getUserGroupsByGroupOids(List<UUID> userGroupOids) {
        List<UserGroup> userGroups = this.findByUserGroupOidIn(userGroupOids);
        return baseI18nService.convertListByLocale(userGroups).stream().map(ug->userGroupToUserGroupDTO(ug)).collect(Collectors.toList());
    }

    /**
     * 根据用户组oid list查询
     *
     * @param userGroupOids
     * @return
     */
    public List<UserGroup> getUserGroupsByOids(List<UUID> userGroupOids) {
        return this.findByUserGroupOidIn(userGroupOids);
    }

    public List<UserGroup> getUserGroupsByIDs(List<Long> ids) {
        return this.findByIdIn(ids);
    }

    public List<UserGroupDTO> selectUserGroupsByIDs(List<Long> ids) {
        List<UserGroup> userGroups = this.findByIdIn(ids);
        userGroups = baseI18nService.convertListByLocale(userGroups);
        return userGroups.stream().map(ug->userGroupToUserGroupDTO(ug)).collect(Collectors.toList());
    }


    /**
     * 校验人员组编码方法
     *
     * @param userGroup：人员组对象
     * @param companyOid：公司oid
     * @return
     */
    public UserGroup checkUserGroupCode(UserGroup userGroup, UUID companyOid) {
        if (!StringUtils.isEmpty(userGroup.getCode())) {
            PatternMatcherUtil.commonCodeCheck(userGroup.getCode());

            UserGroup codeExist = this.findTopOneByCompanyOidAndCode(companyOid, userGroup.getCode());
            UserGroup originGroup = this.findByUserGroupOid(userGroup.getUserGroupOid());
            if (originGroup != null) {
                if (codeExist != null && !codeExist.getId().equals(originGroup.getId())) {
                    throw new BizException(RespCode.USER_GROUP_CODE_EXIST);
                } else {
                    if (originGroup != null && !StringUtils.isEmpty(originGroup.getCode()) && !originGroup.getCode().equals(userGroup.getCode())) {
                        throw new BizException(RespCode.CODE_CANT_MODIFIED);
                    }
                }
            } else {
                if (codeExist != null) {
                    throw new BizException(RespCode.USER_GROUP_CODE_EXIST);
                }
            }

        }
        return userGroup;
    }

    public void checkTenantUserGroupCode(UserGroup userGroup) {
        String code = userGroup.getCode();
        if (!StringUtils.isEmpty(code)) {
            PatternMatcherUtil.commonCodeCheck(code);

            UserGroup existCodeTenantUserGroup = this.selectTenantUserGroupByCode(code, userGroup.getTenantId(), true);
            if (userGroup.getUserGroupOid() == null) {
                if (existCodeTenantUserGroup != null) {
                    throw new BizException(RespCode.USER_GROUP_CODE_EXIST);
                }
            } else {
                //增加判断code是否修改
                UserGroup oldUserGroup = this.findByUserGroupOid(userGroup.getUserGroupOid());
                if ((oldUserGroup != null && !StringUtils.isEmpty(oldUserGroup.getCode()) && !oldUserGroup.getCode().equals(code))) {
                    throw new BizException(RespCode.USER_GROUP_CODE_NOT_MODIFIED);
                }
                if (existCodeTenantUserGroup != null && !existCodeTenantUserGroup.getId().equals(oldUserGroup.getId())) {
                    throw new BizException(RespCode.USER_GROUP_CODE_EXIST);
                }
            }
        }
    }

    public UserGroupDTO findUserGroupById(Long id) {
        UserGroup userGroup = selectById(id);
        if (userGroup != null) {
            userGroup = baseI18nService.selectOneBaseTableInfoWithI18n(userGroup.getId(), userGroup.getClass());
        }
        return userGroupToUserGroupDTO(userGroup);
    }

    /**
     * 创建单条人员组规则
     *
     * @param userGroupId
     * @param conditionViewDTO
     * @return
     */
    @Transactional
    public ConditionViewDTO createUserGroupCondition(Long userGroupId, ConditionViewDTO conditionViewDTO) {
        List<UserGroupConditionDTO> userGroupConditionDTOS = conditionViewDTO.getConditionDetails();
        UserGroup one = selectById(userGroupId);
        if (one == null) {
            throw new ObjectNotFoundException(UserGroup.class, userGroupId);
        }
        Integer sequence = userGroupConditionMapper.selectMaxSequenceOfUserGroup(one.getId());
        sequence = sequence == null ? 0 : sequence;
        //按条件保存
        for (UserGroupConditionDTO condition : userGroupConditionDTOS) {
            if (condition != null && CollectionUtils.isNotEmpty(condition.getConditionValues())) {

                UserGroupCondition userGroupCondition = new UserGroupCondition();
                userGroupCondition.setEnabled(condition.getEnabled());
                userGroupCondition.setUserGroupId(one.getId());
                userGroupCondition.setConditionSeq(sequence + 1);
                userGroupCondition.setConditionLogic(condition.getConditionLogic());
                userGroupCondition.setConditionProperty(condition.getConditionProperty());
                userGroupConditionMapper.insert(userGroupCondition);
                //保存条件的值细项
                List<UserGroupConditionDetailDTO> userGroupConditionDetails = condition.getConditionValues();
                for (UserGroupConditionDetailDTO detailDTO : userGroupConditionDetails) {
                    UserGroupConditionDetail groupDetail = new UserGroupConditionDetail();
                    groupDetail.setConditionId(userGroupCondition.getId());
                    groupDetail.setConditionValue(detailDTO.getConditionValue());
                    userGroupConditionDetailMapper.insert(groupDetail);
                }
            }
        }
        return this.getUserGroupConditionSeq(userGroupId, sequence + 1, true);
    }

    /**
     * 更新单条人员组规则
     *
     * @param userGroupId
     * @param conditionViewDTO
     * @return
     */
    @Transactional
    public ConditionViewDTO updateUserGroupCondition(Long userGroupId, ConditionViewDTO conditionViewDTO) {
        List<UserGroupConditionDTO> userGroupConditionDTOS = conditionViewDTO.getConditionDetails();
        UserGroup one = selectById(userGroupId);
        if (one == null) {
            throw new ObjectNotFoundException(UserGroup.class, userGroupId);
        }
        Integer conditionSeq = conditionViewDTO.getConditionSeq();
        if (conditionSeq == null) {
            throw new BizException(RespCode.USER_GROUP_CONDITION_NOSEQ);
        }
        for (UserGroupConditionDTO condition : userGroupConditionDTOS) {
            Long conditionId = condition.getId();
            if (conditionId == null) {
                //inset new
                //检查当前属性是否已经存在，存在没有id报错
                UserGroupCondition exist = this.checkSeqPropertyExist(userGroupId, condition.getConditionProperty(), conditionSeq);
                if (exist != null) {
                    throw new BizException(RespCode.USER_GROUP_CONDITION_EXIST);
                }
                if (condition != null && CollectionUtils.isNotEmpty(condition.getConditionValues())) {
                    UserGroupCondition userGroupCondition = new UserGroupCondition();
                    userGroupCondition.setUserGroupId(one.getId());
                    userGroupCondition.setConditionSeq(conditionSeq);
                    userGroupCondition.setConditionLogic(condition.getConditionLogic());
                    userGroupCondition.setConditionProperty(condition.getConditionProperty());
                    userGroupCondition.setEnabled(condition.getEnabled());
                    userGroupConditionMapper.insert(userGroupCondition);
                    //保存条件的值细项
                    List<UserGroupConditionDetailDTO> userGroupConditionDetails = condition.getConditionValues();
                    for (UserGroupConditionDetailDTO detailDTO : userGroupConditionDetails) {
                        UserGroupConditionDetail groupDetail = new UserGroupConditionDetail();
                        groupDetail.setConditionId(userGroupCondition.getId());
                        groupDetail.setConditionValue(detailDTO.getConditionValue());
                        userGroupConditionDetailMapper.insert(groupDetail);
                    }
                }
            } else {
                //update by id
                UserGroupCondition userGroupCondition =
                    userGroupCondtionDTOToUserGroupCondition(condition);
                userGroupConditionMapper.updateById(userGroupCondition);
                //原来关联数据解除关联
                List<UserGroupConditionDetail> existDetails = this.getConditionDetails(condition.getId());
                existDetails.stream().map(u -> {
                    userGroupConditionDetailMapper.deleteById(u.getId());
                    return u;
                }).collect(Collectors.toList());
                //添加新关联
                List<UserGroupConditionDetailDTO> newDetails = condition.getConditionValues();
                if (CollectionUtils.isEmpty(newDetails)) {
                    //如果新的关联子项为空，则将当前属性设置为disable
                    userGroupCondition.setEnabled(false);
                    userGroupConditionMapper.updateById(userGroupCondition);
                }
                newDetails.stream().map(u -> {
                    UserGroupConditionDetail groupDetail = new UserGroupConditionDetail();
                    groupDetail.setConditionId(condition.getId());
                    groupDetail.setConditionValue(u.getConditionValue());
                    userGroupConditionDetailMapper.insert(groupDetail);
                    return groupDetail;
                }).collect(Collectors.toList());
            }
        }
        return this.getUserGroupConditionSeq(userGroupId, conditionSeq, true);
    }

    private List<UserGroupConditionDetail> getConditionDetails(Long id) {
        Map<String, Object> map = new HashedMap();
        map.put("condition_id", id);
        map.put("enabled", true);
        List<UserGroupConditionDetail> userGroupConditionDetails = userGroupConditionDetailMapper.selectByMap(map);
        return userGroupConditionDetails;
    }

    public ConditionViewDTO getUserGroupConditionSeqSimple(Long userGroupId, Integer seq, Boolean showDisable) {
        EntityWrapper ew = new EntityWrapper(new UserGroupCondition());
        ew.eq("user_group_id", userGroupId);
        ew.eq("condition_seq", seq);
        if (!showDisable) {
            ew.eq("enabled", true);
        }
        List<UserGroupCondition> list = userGroupConditionMapper.selectList(ew);
        List<UserGroupConditionDTO> conditionDTOS = list.stream().map(u ->userGroupCondtionToUserGroupConditionDTO(u)).collect(Collectors.toList());
        conditionDTOS.stream().map(u -> {
            List<UserGroupConditionDetail> details = this.getConditionDetails(u.getId());
            List<UserGroupConditionDetailDTO> conditionDetailDTOS = details.stream().map(k -> detailToDetailDTO(k)).collect(Collectors.toList());
            u.setConditionValues(conditionDetailDTOS);
            return u;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(conditionDTOS)) {
            ConditionViewDTO conditionViewDTO = new ConditionViewDTO();
            conditionViewDTO.setConditionSeq(seq);
            conditionViewDTO.setConditionDetails(conditionDTOS);
            return conditionViewDTO;
        } else {
            return null;
        }
    }

    public ConditionViewDTO getUserGroupConditionSeq(Long userGroupId, Integer seq, Boolean showDisable) {
        UserGroup one = selectById(userGroupId);
        if (one == null) {
            throw new ObjectNotFoundException(UserGroup.class, userGroupId);
        }

        EntityWrapper ew = new EntityWrapper(new UserGroupCondition());
        ew.eq("user_group_id", userGroupId);
        ew.eq("condition_seq", seq);
        if (!showDisable) {
            ew.eq("enabled", true);
        }

        Long tenantId = one.getTenantId();
        List<UserGroupCondition> list = userGroupConditionMapper.selectList(ew);
        List<UserGroupConditionDTO> conditionDTOS = list.stream().map(u ->
            userGroupCondtionToUserGroupConditionDTO(u)).collect(Collectors.toList());
        conditionDTOS.stream().map(u -> {
            String conditionProperty = u.getConditionProperty();
            List<UserGroupConditionDetail> details = this.getConditionDetails(u.getId());
            List<UserGroupConditionDetailDTO> conditionDetailDTOS = details.stream().map(k -> detailToDetailDTO(k)).collect(Collectors.toList());
            switch (conditionProperty) {
                case "Department":
                    conditionDetailDTOS.stream().map(i -> {
                        Long departmentId = Long.parseLong(i.getConditionValue());
                        Department department = departmentService.selectOnebyId(departmentId);
                        if (department != null) {
                            i.setDescription(department.getName());
                        }
                        return i;
                    }).collect(Collectors.toList());
                    u.setConditionValues(conditionDetailDTOS);
                    break;
                case "EmployeeType":
                    conditionDetailDTOS.stream().map(i -> {
                        SysCodeValueCO typeItem = hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.EMPLOYEETYPE.getId().toString(), i.getConditionValue());
                        if (typeItem != null) {
                            i.setDescription(typeItem.getName());
                        }
                        return i;

                    }).collect(Collectors.toList());
                    u.setConditionValues(conditionDetailDTOS);
                    break;
                case "EmployeeDuty":
                    conditionDetailDTOS.stream().map(i -> {
                        SysCodeValueCO typeItem = hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.DUTY.getId().toString(), i.getConditionValue());
                        if (typeItem != null) {
                            i.setDescription(typeItem.getName());
                        }
                        return i;

                    }).collect(Collectors.toList());
                    u.setConditionValues(conditionDetailDTOS);
                    break;
                case "EmployeeRank":
                    conditionDetailDTOS.stream().map(i -> {
                        SysCodeValueCO typeItem = hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.LEVEL.getId().toString(), i.getConditionValue());
                        if (typeItem != null) {
                            i.setDescription(typeItem.getName());
                        }
                        return i;

                    }).collect(Collectors.toList());
                    u.setConditionValues(conditionDetailDTOS);
                    break;

            }
            return u;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(conditionDTOS)) {
            ConditionViewDTO conditionViewDTO = new ConditionViewDTO();
            conditionViewDTO.setConditionSeq(seq);
            conditionViewDTO.setConditionDetails(conditionDTOS);
            return conditionViewDTO;
        } else {
            return null;
        }

    }


    //获取规则人员组及所有条件显示
    public UserGroupDTO getUserGroupWithConditions(UserGroup userGroup) {
        if (userGroup == null) {
            throw new BizException(RespCode.USER_GROUP_NOT_EXIST);
        }
        Long userGroupId = userGroup.getId();
        UserGroupDTO userGroupDTO = userGroupToUserGroupDTO(userGroup);
        List<Integer> counts = userGroupConditionMapper.getConditionUserGroupSeqCountList(userGroupId);
        List<ConditionViewDTO> conditionViewDTOS = userGroupDTO.getConditionViewDTOS();
        if (CollectionUtils.isNotEmpty(counts)) {
            for (Integer i : counts) {
                ConditionViewDTO singelCondition = this.getUserGroupConditionSeq(userGroupId, i, true);
                if (singelCondition == null) {
                    continue;
                }
                conditionViewDTOS.add(singelCondition);
            }
        }
        return userGroupDTO;
    }

    public UserGroupCondition checkSeqPropertyExist(Long userGroupId, String conditionProperty, int conditionSeq) {
        Map<String, Object> map = new HashedMap();
        map.put("user_group_id", userGroupId);
        map.put("condition_property", conditionProperty);
        map.put("condition_seq", conditionSeq);
        List<UserGroupCondition> userGroupConditions = userGroupConditionMapper.selectByMap(map);
        if (CollectionUtils.isEmpty(userGroupConditions)) {
            return null;
        } else if (userGroupConditions.size() == 1) {
            return userGroupConditions.get(0);
        } else {
            throw new BizException(RespCode.USER_GROUP_MULTI_CONDITION);
        }
    }


    public void deleteUserGroupSeqCondition(Long userGroupId, Integer seq) {
        UserGroup one = selectById(userGroupId);
        if (one == null) {
            throw new ObjectNotFoundException(UserGroup.class, userGroupId);
        }
        EntityWrapper ew = new EntityWrapper(new UserGroupCondition());
        ew.eq("user_group_id", userGroupId);
        ew.eq("condition_seq", seq);
        List<UserGroupCondition> list = userGroupConditionMapper.selectList(ew);
        if (CollectionUtils.isNotEmpty(list)) {
            //删除子关联项
            list.stream().map(u -> {
                List<UserGroupConditionDetail> conditionDetails = this.getConditionDetails(u.getId());
                if (CollectionUtils.isNotEmpty(conditionDetails)) {
                    userGroupConditionDetailMapper.deleteBatchIds(conditionDetails.stream().map(i -> i.getId()).collect(Collectors.toList()));
                }
                return null;
            }).collect(Collectors.toList());
        }
        //删除人员规则组条件项
        userGroupConditionMapper.delete(ew);

    }


    public Map<UUID, Boolean> hasUserGroupPermission(List<UUID> userGroupOids, UUID userOid) {
        Map<UUID, Boolean> map = new HashedMap();
        CompletionService cs = new ExecutorCompletionService(executor);
        for (UUID userGroupOid : userGroupOids) {
            cs.submit(() -> {
                log.debug("current Thread name:{} with param:{}", Thread.currentThread().getName(), userGroupOid);
                Map<UUID, Boolean> inGroup = hasUserGroupPermissionForMuti(userGroupOid, userOid);
                return inGroup;
            });
        }
        for (int i = 1; i <= userGroupOids.size(); i++) {
            Map<UUID, Boolean> one = new HashedMap();
            try {
                one = (Map<UUID, Boolean>) cs.take().get();
            } catch (InterruptedException e) {
                log.error("InterruptedException:{}", e.getMessage());
                e.printStackTrace();
            } catch (ExecutionException e) {
                log.error("ExecutionException:{}", e.getMessage());
                e.printStackTrace();
            }
            map.putAll(one);
        }
        return map;
    }


    public Boolean hasUserGroupPermission(Long userGroupId, Long userId) {
        UserDTO user = contactService.getUserDTOByUserId(userId);
        if (user == null) {
            throw new BizException(RespCode.USER_NOT_EXIST);
        }
        UserGroup userGroup = this.findById(userGroupId);
        if (userGroup == null) {
            throw new BizException(RespCode.USER_GROUP_NOT_EXIST);
        }
        return this.hasUserGroupPermission(user, userGroup);
    }

    public Boolean hasUserGroupPermissionForMuti(List<Long> userGroupIds, Long userId) {
        CompletionService cs = new ExecutorCompletionService(executor);
        for (Long userGroupId : userGroupIds) {
            cs.submit(() -> {
                log.debug("current Thread name:{} with param:{}", Thread.currentThread().getName(), userGroupId);
                Boolean inGroup = hasUserGroupPermission(userGroupId, userId);
                return inGroup;
            });
        }
        for (int i = 1; i <= userGroupIds.size(); i++) {
            Boolean one = null;
            try {
                one = (Boolean) cs.take().get();
                if(one){
                    return one;
                }
            } catch (InterruptedException e) {
                log.error("InterruptedException:{}", e.getMessage());
                e.printStackTrace();
            } catch (ExecutionException e) {
                log.error("ExecutionException:{}", e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }




    public Map<UUID, Boolean> hasUserGroupPermissionForMuti(UUID userGroupOid, UUID userOid) {
        UserDTO user = contactService.getUserDTOByUserOid(userOid);
        if (user == null) {
            throw new BizException(RespCode.USER_NOT_EXIST);
        }
        UserGroup userGroup = this.findByUserGroupOid(userGroupOid);
        if (userGroup == null) {
            throw new BizException(RespCode.USER_GROUP_NOT_EXIST);
        }
        return this.hasUserGroupPermissionForMuti(user, userGroup);
    }

    @Transactional
    public Map<UUID, Boolean> hasUserGroupPermissionForMuti(UserDTO user, UserGroup userGroup) {
        Boolean inGroup = this.hasUserGroupPermission(user, userGroup);
        Map<UUID, Boolean> map = new HashedMap();
        map.put(userGroup.getUserGroupOid(), inGroup);
        return map;
    }

    @Transactional
    public Boolean hasUserGroupPermission(UserDTO user, UserGroup userGroup) {
        Long userId = user.getId();
        Long userGroupId = userGroup.getId();
        Contact contact = contactService.findOne(user.getContactId());
        boolean collectionBelong = this.checkUserWithGroupCollectionBelong(userId, userGroupId);
        if (collectionBelong) {
            return collectionBelong;
        }

        List<Integer> counts = userGroupConditionMapper.getEnableConditionUserGroupSeqCountList(userGroupId);
        for (Integer i : counts) {
            //人员单个条件归属关系
            Boolean conditionBelong = true;
            //从第一条规则开始循环判断
            ConditionViewDTO conditionView = this.getUserGroupConditionSeqSimple(userGroupId, i, false);
            if (conditionView != null){
                for (UserGroupConditionDTO conditionDTO : conditionView.getConditionDetails()) {
                    String conditionProperty = conditionDTO.getConditionProperty();
                    String conditionLogic = conditionDTO.getConditionLogic();
                    List<UserGroupConditionDetailDTO> conditionValues = conditionDTO.getConditionValues();
                    if ("I".equalsIgnoreCase(conditionLogic)) {
                        switch (conditionProperty) {
                            case "Department":
                                Long departmentId = departmentUserService.getDepartmentByUserId(user.getId()).get().getId();
                                List<Long> parentDepartments = conditionValues.stream().map(u -> Long.parseLong(u.getConditionValue())).collect(Collectors.toList());
//                            || conditionValues.stream().noneMatch(u -> departmentId.toString().equals(u.getConditionValue()))
                                if (departmentId == null || CollectionUtils.isEmpty(conditionValues) || !departmentService.checkIfChildDepartments(parentDepartments, departmentId)) {
                                    conditionBelong = false;
                                }
                                break;
                            case "EmployeeType":
                                String type = contact.getEmployeeTypeCode();
//                            List<SysCodeValue> typeItems = customEnumerationService.findByCompanyAndSysTypeAndCodeIn(companyId, SystemCustomEnumerationTypeEnum.EMPLOYEETYPE.getId(), typeList);
                                if (StringUtils.isEmpty(type) || CollectionUtils.isEmpty(conditionValues) || conditionValues.stream().noneMatch(u -> type.equals(u.getConditionValue()))) {
                                    conditionBelong = false;
                                }
                                break;
                            case "EmployeeDuty":
                                String duty = contact.getDutyCode();
//                            List<SysCodeValue> dutyItems = customEnumerationService.findByCompanyAndSysTypeAndCodeIn(companyId, SystemCustomEnumerationTypeEnum.DUTY.getId(), dutyList);
                                if (StringUtils.isEmpty(duty) || CollectionUtils.isEmpty(conditionValues) || conditionValues.stream().noneMatch(u -> duty.equals(u.getConditionValue()))) {
                                    conditionBelong = false;
                                }
                                break;
                            case "EmployeeRank":
//                            String[] ranks = conditionValue.split(",");
//                            List<String> rankList = Arrays.asList(ranks);
                                String rank = contact.getRankCode();
//                            List<SysCodeValue> rankItems = customEnumerationService.findByCompanyAndSysTypeAndCodeIn(companyId, SystemCustomEnumerationTypeEnum.LEVEL.getId(), rankList);
                                if (StringUtils.isEmpty(rank) || CollectionUtils.isEmpty(conditionValues) || conditionValues.stream().noneMatch(u -> rank.equals(u.getConditionValue()))) {
                                    conditionBelong = false;
                                }
                                break;
                        }
                    } else {
                        switch (conditionProperty) {
                            case "Department":
                                Long departmentId = departmentUserService.getDepartmentByUserId(user.getId()).get().getId();
                                List<Long> parentDepartments = conditionValues.stream().map(u -> Long.parseLong(u.getConditionValue())).collect(Collectors.toList());
//                            conditionValues.stream().anyMatch(u -> departmentId.toString().equals(u.getConditionValue()))
                                if (departmentId != null && CollectionUtils.isNotEmpty(conditionValues) && departmentService.checkIfChildDepartments(parentDepartments, departmentId)) {
                                    conditionBelong = false;
                                }
                                break;
                            case "EmployeeType":
                                String type = contact.getEmployeeTypeCode();
                                if (!StringUtils.isEmpty(type) && CollectionUtils.isNotEmpty(conditionValues) && conditionValues.stream().anyMatch(u -> type.equals(u.getConditionValue()))) {
                                    conditionBelong = false;
                                }
                                break;
                            case "EmployeeDuty":
                                String duty = contact.getDutyCode();
                                if (!StringUtils.isEmpty(duty) && CollectionUtils.isNotEmpty(conditionValues) && conditionValues.stream().anyMatch(u -> duty.equals(u.getConditionValue()))) {
                                    conditionBelong = false;
                                }
                                break;
                            case "EmployeeRank":
                                String rank = contact.getRankCode();
                                if (!StringUtils.isEmpty(rank) && CollectionUtils.isNotEmpty(conditionValues) && conditionValues.stream().anyMatch(u -> rank.equals(u.getConditionValue()))) {
                                    conditionBelong = false;
                                }
                                break;
                        }
                    }
                    //当任何条件细项不满足，当前整个条件不满足，跳过
                    if (!conditionBelong) {
                        break;
                    }
                }
            }else {
                continue;
            }
            //当任何一个条件满足，整个条件即满足，返回true,否则循环，直到所有条件不满足，返回false
            if (conditionBelong) {
                return conditionBelong;
            }
        }
        return false;
    }


    public UserGroup saveUserGroup(UserGroup userGroup) {
        super.insert(userGroup);
        return userGroup;
    }

    public void updateUserGroupMybatis(UserGroup userGroup) {
        super.updateById(userGroup);
    }

    public UserGroup findByUserGroupOid(UUID userGroupOid) {
        return super.selectOne(new EntityWrapper<UserGroup>().eq("user_group_oid",userGroupOid));
    }

    public UserGroup findOneByUserGroupAndCompanyOid(UUID userGroupOid, UUID companyOid) {
        return super.selectOne(new EntityWrapper<UserGroup>()
                .eq("user_group_oid",userGroupOid)
                .eq("company_oid",companyOid)
        );
    }

    public List<UserGroup> findByCompanyOidOrderBylastUpdatedDateDesc(UUID companyOid, Boolean enabled) {
        Wrapper<UserGroup> wrapper = new EntityWrapper<UserGroup>().eq("company_oid", companyOid).orderBy("last_updated_date", false);
        if (enabled != null) {
            wrapper.eq("enabled", enabled);
        }
        return super.selectList(wrapper);

    }

    public List<UserGroup> findByTenantOrderBylastUpdatedDateDesc(Long tenantId, Boolean enabled) {
        Wrapper<UserGroup> wrapper = new EntityWrapper<UserGroup>().eq("tenant_id", tenantId).orderBy("last_updated_date", false);
        if (enabled != null) {
            wrapper.eq("enabled", enabled);
        }
        return super.selectList(wrapper);

    }

    public List<UserGroup> findByCompanyOidOrderBylastUpdatedDateDesc(UUID companyOid, Boolean enabled, Page page) {
        Wrapper<UserGroup> wrapper = new EntityWrapper<UserGroup>().eq("company_oid", companyOid).orderBy("last_updated_date", false);
        if (enabled != null) {
            wrapper.eq("enabled", enabled);
        }
        return super.selectPage(page, wrapper).getRecords();
    }

    public List<UserGroup> findByUserGroupOidIn(List<UUID> userGroupOids) {
        Wrapper<UserGroup> wrapper = new EntityWrapper<UserGroup>().in(CollectionUtils.isNotEmpty(userGroupOids), "user_group_oid", userGroupOids);
        return super.selectList(wrapper);
    }

    public UserGroup findTopOneByCompanyOidAndName(UUID companyOid, String userGroupName) {
        return super.selectOne(new EntityWrapper<UserGroup>()
                .eq("company_oid",companyOid)
                .eq("name",userGroupName)
        );
    }

    public UserGroup findTopOneByCompanyOidAndCode(UUID companyOid, String userGroupCode) {
        return super.selectOne(new EntityWrapper<UserGroup>()
                .eq("company_oid",companyOid)
                .eq("code",userGroupCode)
        );
    }

    public List<UserGroupDTO> searchUserGroupByName(String name, UUID companyOid, Boolean enabled, Page page) {
        Wrapper<UserGroup> wrapper = new EntityWrapper<UserGroup>().eq("company_oid", companyOid).orderBy("enabled",false).orderBy("last_updated_date", false);
        if (!StringUtils.isEmpty(name)) {
            wrapper.like("name", "%".concat(name).concat("%"));
        }
        if (enabled != null) {
            wrapper.eq("enabled", enabled);
        }
        List<UserGroup> userGroupList = super.selectPage(page, wrapper).getRecords();
        userGroupList = baseI18nService.convertListByLocale(userGroupList);
        return userGroupList.stream().map(ug->userGroupToUserGroupDTO(ug)).collect(Collectors.toList());
    }

    public List<UserGroupDTO> searchTenantUserGroupByName(String name, Long tenantId, Boolean enabled, Page page) {
        Wrapper<UserGroup> wrapper = new EntityWrapper<UserGroup>().eq("tenant_id", tenantId).isNull("company_oid");
        if (!StringUtils.isEmpty(name)) {
            wrapper.like("name", "%".concat(name).concat("%"));
        }
        if (enabled != null) {
            wrapper.eq("enabled", enabled);
        }
        wrapper.orderBy("enabled",false);
        wrapper.orderBy("code",true);
        List<UserGroup> userGroupList = super.selectPage(page, wrapper).getRecords();
        userGroupList = baseI18nService.convertListByLocale(userGroupList);
        return userGroupList.stream().map(ug->userGroupToUserGroupDTO(ug)).collect(Collectors.toList());
    }


    public UserGroup findById(Long userGroupId) {
        UserGroup userGroup = super.selectById(userGroupId);
        return userGroup;
    }

    public List<UserGroup> findByIdIn(List<Long> ids) {
        List<UserGroup> userGroupList = super.selectBatchIds(ids);
        return userGroupList;
    }


    /**
     * 查询对象所在用户组
     *
     * @param tenantId   租户ID
     * @param objectId   对象ID
     * @param objectType 对象类型
     * @return 对象用户组
     */
    public List<UserGroupPermission> getObjectUserGroup(Long tenantId, Long objectId, String objectType) {
        return userGroupPermissionMapper.selectList(new EntityWrapper<UserGroupPermission>()
            .eq("tenant_id", tenantId)
            .eq("object_id", objectId)
            .eq("object_type", objectType)
            .eq("enabled", true)
            .eq("deleted", false));
    }

    /**
     * 提升为租户级
     * @param userOid
     * @param companyOid
     * @return
     */
    public List<UUID> getUserGroupByUserOid(UUID userOid, UUID companyOid) {
        log.debug("开始进行动态/静态人员组权限检查");
        Long startTime = System.currentTimeMillis();
        List<UUID> result = new ArrayList<UUID>();
        UserDTO user = contactService.getUserDTOByUserOid(userOid);
        if (user != null) {
            //通过人员查询默认人员组，表示用户存在于这些人员组中
            List<UUID> defaultUserGroup = userGroupMapper.findDefaultUserGroupV2(user.getTenantId(),null, user.getId());
            Long defaultTime = System.currentTimeMillis();
            log.debug("查询默认人员组耗时:{}ms", defaultTime - startTime);
            result.addAll(defaultUserGroup);
            //查询有哪些可用的规则组
            List<UserGroup> conditionUserGroup = userGroupMapper.listByTenantIdAndUserGroupOids(user.getTenantId(), defaultUserGroup);
            Long conditionTime = System.currentTimeMillis();
            log.debug("查询条件人员组耗时：{}ms", conditionTime - defaultTime);
            //检查当前人员是否具有这些人员规则组的权限
            Map<UUID, Boolean> conditionGroupList = this.hasUserGroupPermission(conditionUserGroup.stream().map(u -> u.getUserGroupOid()).collect(Collectors.toList()), userOid);
            result.addAll(conditionGroupList.keySet().stream().filter(u -> conditionGroupList.get(u)).collect(Collectors.toList()));
            Long permissionTime = System.currentTimeMillis();
            log.debug("完成人员组权限检查耗时:{}", permissionTime - conditionTime);
            log.debug("总共耗时:{}ms", permissionTime - startTime);
            return result;
        } else {
            log.warn("人员信息不存在");
            return null;
        }

    }

    public boolean checkUserWithGroupCollectionBelong(Long userId, Long userGroupId) {
        Integer usuCount = userGroupMapper.getUserGroupUser(userGroupId, userId);
        if (usuCount != null && usuCount >= 1) {
            return true;
        }
        return false;
    }

    public UserGroup selectTenantUsergroupByName(String name, Long tenantId, Boolean enabled) {
        return userGroupMapper.selectTenantGroupByName(name, tenantId, enabled);
    }


    public UserGroup selectTenantUserGroupByCode(String code, Long tenantId, boolean enabled) {
        return userGroupMapper.selectTenantGroupByCode(code, tenantId, enabled);
    }

    public List<UserGroupDTO> findTenantGroups(Long currentTenantID, Boolean enabled, Page page) {
        Wrapper<UserGroup> wrapper = new EntityWrapper<UserGroup>().eq("tenant_id", currentTenantID).isNull("company_oid").orderBy("last_updated_date", false);
        if (enabled != null) {
            wrapper.eq("enabled", enabled);
        }
        List<UserGroup> userGroupList = userGroupMapper.selectPage(page, wrapper);
        if (CollectionUtils.isNotEmpty(userGroupList)) {
            userGroupList = baseI18nService.convertListByLocale(userGroupList);
        }
        return userGroupList.stream().map(ug->userGroupToUserGroupDTO(ug)).collect(Collectors.toList());
    }

    public List<UserGroup> findGroupsByUserId(Long userId){
        return userGroupMapper.listByQO(UserGroupQO.builder().userId(userId).build());
    }





    public UserGroupDTO userGroupToUserGroupDTO(UserGroup userGroup) {
        if (userGroup == null) {
            return null;
        }
        UserGroupDTO userGroupDTO = new UserGroupDTO();
        BeanUtils.copyProperties(userGroup, userGroupDTO);
        userGroupDTO.setComment(userGroup.getComments());
        return userGroupDTO;
    }

    public UserGroup userGroupDTOToUserGroup(UserGroupDTO userGroupDTO) {
        if (userGroupDTO == null) {
            return null;
        }
        UserGroup userGroup = new UserGroup();
        BeanUtils.copyProperties(userGroupDTO,userGroup);
        if (userGroup.getI18n().containsKey("comment")){
            userGroup.getI18n().put("comments",userGroupDTO.getI18n().get("comment"));
            userGroup.getI18n().remove("comment");
        }
        userGroup.setComments(userGroupDTO.getComment());
        return userGroup;
    }

    public  UserGroupConditionDTO userGroupCondtionToUserGroupConditionDTO(UserGroupCondition userGroupCondition) {
        UserGroupConditionDTO userGroupConditionDTO = new UserGroupConditionDTO();
        if (null != userGroupCondition) {
            mapper.map(userGroupCondition, userGroupConditionDTO);
        }
        return userGroupConditionDTO;
    }

    public UserGroupCondition userGroupCondtionDTOToUserGroupCondition(UserGroupConditionDTO userGroupConditionDTO) {
        UserGroupCondition userGroupCondition = new UserGroupCondition();
        if (null != userGroupConditionDTO) {
            mapper.map(userGroupConditionDTO, userGroupCondition);
        }
        return userGroupCondition;
    }

    public  UserGroupConditionDetailDTO detailToDetailDTO(UserGroupConditionDetail detail) {
        UserGroupConditionDetailDTO userGroupConditionDetailDTO = new UserGroupConditionDetailDTO();
        if (null != detail) {
            mapper.map(detail, userGroupConditionDetailDTO);
        }
        return userGroupConditionDetailDTO;
    }

    public UserGroupConditionDetail detailDTOToDetail(UserGroupConditionDetailDTO detailDTO) {
        UserGroupConditionDetail userGroupConditionDetail = new UserGroupConditionDetail();
        if (null != userGroupConditionDetail) {
            mapper.map(detailDTO, userGroupConditionDetail);
        }
        return userGroupConditionDetail;
    }

    public List<UserGroupCO> listUserGroupByIds(List<Long> ids) {
        List<UserGroup> userGroups = this.selectBatchIds(ids);
        return userGroup2UserGroupCO(userGroups, false);
    }

    public UserGroupCO getUserGroupByUserGroupId(Long userGroupId) {
        UserGroup userGroup = this.selectById(userGroupId);
        return userGroup2UserGroupCO(userGroup);
    }

    private UserGroupCO userGroup2UserGroupCO(UserGroup userGroup){
        if (userGroup != null){
            UserGroupCO userGroupCO = new UserGroupCO();
            userGroupCO.setId(userGroup.getId());
            userGroupCO.setCode(userGroup.getCode());
            userGroupCO.setName(userGroup.getName());
            userGroupCO.setComment(userGroup.getComments());
            userGroupCO.setUserGroupOid(userGroup.getUserGroupOid());
            userGroupCO.setType(userGroup.getType());
            return userGroupCO;
        }
        return null;
    }

    private List<UserGroupCO> userGroup2UserGroupCO(List<UserGroup> userGroups, boolean isSetUserIds){
        if (CollectionUtils.isNotEmpty(userGroups)){
            List<UserGroupCO> collect = userGroups.stream().map(e -> {
                UserGroupCO userGroupCO = new UserGroupCO();
                userGroupCO.setId(e.getId());
                userGroupCO.setCode(e.getCode());
                userGroupCO.setName(e.getName());
                userGroupCO.setComment(e.getComments());
                userGroupCO.setUserGroupOid(e.getUserGroupOid());
                userGroupCO.setType(e.getType());
                if (isSetUserIds){
                    List<UserGroupUser> groupUsers = userGroupUserMapper.selectList(new EntityWrapper<UserGroupUser>().eq("user_group_id", e.getId()));
                    if (CollectionUtils.isNotEmpty(groupUsers)){
                        List<Long> userIds = groupUsers.stream().map(UserGroupUser::getUserId).collect(Collectors.toList());
                        userGroupCO.setUserIds(userIds);
                    }
                }
                userGroupCO.setUserIds(new ArrayList<>());
                return userGroupCO;
            }).collect(Collectors.toList());
            return collect;
        }
        return new ArrayList<>();
    }

    public UserGroupCO getUserGroupByUserGroupOid(String userGroupOid) {
        List<UserGroup> userGroups = this.selectList(new EntityWrapper<UserGroup>().eq("user_group_oid", userGroupOid));
        List<UserGroupCO> userGroupCOS = userGroup2UserGroupCO(userGroups, false);
        if (CollectionUtils.isNotEmpty(userGroupCOS)){
            return userGroupCOS.get(0);
        }
        return null;
    }

    public List<UserGroupCO> listUserGroupByUserGroupOids(List<String> userGroupOids) {
        List<UserGroup> userGroups = this.selectList(new EntityWrapper<UserGroup>().in("user_group_oid", userGroupOids));
        return userGroup2UserGroupCO(userGroups, false);
    }

    public Page<ContactCO> pageUserCOByUserGroupOid(UUID userGroupOid, Page<ContactCO> mybatisPage) {
        UserGroup userGroup = this.findByUserGroupOid(userGroupOid);
        if (userGroup == null){
            return mybatisPage;
        }
        List<UserGroupUser> groupUsers = userGroupUserMapper.selectPage(mybatisPage, new EntityWrapper<UserGroupUser>().eq("user_group_id", userGroup.getId()));
        if (CollectionUtils.isNotEmpty(groupUsers)){
            List<Long> userIds = groupUsers.stream().map(UserGroupUser::getUserId).collect(Collectors.toList());
            List<ContactCO> userCOS = contactService.listByUserIdsConditionByKeyWord(userIds, null);
            mybatisPage.setRecords(userCOS);
            return mybatisPage;
        }
        return mybatisPage;
    }

    public List<ContactCO> listUserCOByUserGroupId(Long userGroupId) {
        UserGroup userGroup = this.selectById(userGroupId);
        if (userGroup == null){
            return new ArrayList<>();
        }
        List<UserGroupUser> groupUsers = userGroupUserMapper.selectList(new EntityWrapper<UserGroupUser>().eq("user_group_id", userGroup.getId()));
        if (CollectionUtils.isNotEmpty(groupUsers)){
            List<Long> userIds = groupUsers.stream().map(UserGroupUser::getUserId).collect(Collectors.toList());
            return contactService.listByUserIdsConditionByKeyWord(userIds, null);
        }
        return new ArrayList<>();
    }

    public List<UserGroupCO> listUserGroupAndUserIdByGroupIds(List<Long> ids) {
        List<UserGroup> userGroups = this.selectBatchIds(ids);
        return userGroup2UserGroupCO(userGroups, true);
    }

    public List<UserGroupCO> listUserGroupByUserId(Long userId, Boolean enabled) {
        List<UserGroupUser> groupUsers = userGroupUserMapper.selectList(new EntityWrapper<UserGroupUser>()
                .eq("user_id", userId));
        if (CollectionUtils.isEmpty(groupUsers)){
            return new ArrayList<>();
        }
        List<Long> collect = groupUsers.stream().map(UserGroupUser::getUserGroupId).collect(Collectors.toList());
        List<UserGroup> userGroups = this.selectBatchIds(collect);
        return userGroup2UserGroupCO(userGroups, false);
    }

    public List<UserGroupCO> listUserGroupAndUserIdsBySetOfBooksId(Long setOfBooksId, Boolean enabled) {
        List<UserGroup> userGroups = this.selectList(new EntityWrapper<UserGroup>()
                .where("company_oid in (select sc.company_oid from sys_company sc where  sc.set_of_books_id = {0}", setOfBooksId)
                .eq(enabled != null, "enabled", enabled));

        return userGroup2UserGroupCO(userGroups, true);
    }


    /**
     * 根据条件获取用户信息 筛选掉已添加用户
     * @param keyword
     * @param userGroupId
     * @param tenantId
     * @param status
     * @param page
     * @return
     */
    public List<UserDTO> pageUserByConditionByIgnoreIds(String keyword,
                                                        Long userGroupId,
                                                        Long tenantId,
                                                        String status,
                                                        Page page) {
        List<Long> ids = userGroupUserMapper.selectList(
                new EntityWrapper<UserGroupUser>()
                        .eq("user_group_id",userGroupId))
                .stream()
                .map(UserGroupUser::getUserId)
                .collect(Collectors.toList());
        List<UserDTO> list = contactService.listUserDTOByCondition(keyword == null ? null : keyword.trim(),
                tenantId,
                null,
                status,
                null,
                null,
                page).stream()
                .filter(e-> !ids.contains(e.getId()))
                .collect(Collectors.toList());
        return list;

    }
}
