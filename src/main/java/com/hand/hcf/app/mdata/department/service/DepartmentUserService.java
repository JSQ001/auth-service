package com.hand.hcf.app.mdata.department.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.department.domain.Department;
import com.hand.hcf.app.mdata.department.domain.DepartmentUser;
import com.hand.hcf.app.mdata.department.domain.DepartmentUserHistory;
import com.hand.hcf.app.mdata.department.dto.DepartmentAssignUserDTO;
import com.hand.hcf.app.mdata.department.dto.DepartmentUserSummaryDTO;
import com.hand.hcf.app.mdata.department.persistence.DepartmentUserMapper;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 部门用户业务逻辑层
 */
@Service
@Transactional
public class DepartmentUserService extends BaseService<DepartmentUserMapper, DepartmentUser> {
    @Autowired
    private DepartmentUserMapper departmentUserMapper;
    @Autowired
    private DepartmentUserHistoryService departmentUserHistoryService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;
    @Autowired
    private ContactService contactService;

    /**
     * 新增或修改用户部门关联信息
     */
    public void addOrUpdateUserDepartment(Long userId, Long departmentId) {
        if (userId != null && departmentId != null) {
            // 判断是否已存在关联关系
            DepartmentUser param = new DepartmentUser();
            param.setDepartmentId(departmentId);
            param.setUserId(userId);
            DepartmentUser oldDepartmentUser = departmentUserMapper.selectOne(param);
            if (oldDepartmentUser != null) { // 修改
                EntityWrapper<DepartmentUser> ew = new EntityWrapper<>();
                ew.where(" user_id = {0}", userId).and(" department_id = {0}", departmentId);
                super.update(oldDepartmentUser, ew);

            } else {  // 创建
                oldDepartmentUser = new DepartmentUser();
                oldDepartmentUser.setUserId(userId);
                oldDepartmentUser.setDepartmentId(departmentId);
                super.insert(oldDepartmentUser);
            }
        }
    }

    /**
     * 逻辑删除用户部门关联信息
     *
     * @param userIDs：用户id集合
     * @param departmentID：部门id
     */
    public void removeUserDepartment(List<Long> userIDs, Long departmentID) {
        if (CollectionUtils.isNotEmpty(userIDs) && departmentID != null) {
            EntityWrapper ew = new EntityWrapper();
            ew.where(" department_id = {0} ", departmentID).in("user_id", userIDs);
            List<DepartmentUser> departmentUsers = departmentUserMapper.selectList(ew);
            DepartmentUserHistory history = null;
            for (DepartmentUser departmentUser : departmentUsers) {
                history = new DepartmentUserHistory();
                history.setDepartmentId(departmentUser.getDepartmentId());
                history.setUserId(departmentUser.getUserId());
                departmentUserHistoryService.insert(history);
            }

            super.delete(ew);
        }
    }

    /**
     * 逻辑删除用户部门关联信息
     *
     * @param userIDs：用户id集合
     * @param departmentID：部门id
     */
    public void removeUserDepartment(List<Long> userIDs, Long departmentID, String currentUserOid) {
        if (CollectionUtils.isNotEmpty(userIDs) && departmentID != null) {
            EntityWrapper ew = new EntityWrapper();
            ew.where(" department_id = {0} ", departmentID).in("user_id", userIDs);
            List<DepartmentUser> departmentUsers = departmentUserMapper.selectList(ew);
            DepartmentUserHistory history = null;
            for (DepartmentUser departmentUser : departmentUsers) {
                history = new DepartmentUserHistory();
                history.setDepartmentId(departmentUser.getDepartmentId());
                history.setUserId(departmentUser.getUserId());
                history.setCreatedBy(OrgInformationUtil.getCurrentUserId());
                history.setCreatedDate(ZonedDateTime.now());
                departmentUserHistoryService.insert(history);
            }
            departmentUserMapper.delete(ew);
        }
    }

    /**
     * 根据用户id查询所在部门
     *
     * @param userId：用户id
     * @return
     */
    public Set<Department> findDepartmentByUserId(Long userId) {
        Set<Department> departments = new HashSet<>();
        EntityWrapper ew = new EntityWrapper();
        ew.where(" user_id = {0}", userId);
        List<DepartmentUser> departmentUsers = departmentUserMapper.selectList(ew);
        if (CollectionUtils.isNotEmpty(departmentUsers)) {
            departments.addAll(departmentService.findDepartmentByIds(departmentUsers.stream().map(DepartmentUser::getDepartmentId).collect(Collectors.toList())));
        }
        return departments;
    }

    public Set<UserDTO> findUsersByDepartmentId(Long departmentId) {
        Set<UserDTO> users = new HashSet<>();
        EntityWrapper ew = new EntityWrapper();
        ew.where("department_id = {0}", departmentId);
        List<DepartmentUser> departmentUsers = departmentUserMapper.selectList(ew);
        if (CollectionUtils.isNotEmpty(departmentUsers)) {
            users = departmentUsers.stream().map(u -> {
                    Long userId = u.getUserId();
                    return contactService.getUserDTOByUserId(userId);
                }
            ).collect(Collectors.toSet());
        }
        return users;
    }

    public UserDTO getDepartmentManager(Long departmentId) {
        Department department = departmentService.selectOnebyId(departmentId);
        if (department != null) {
            return contactService.getUserDTOByUserId(department.getManagerId());
        }
        return null;
    }

    /**
     * 移动部门和员工集合
     * @param dto
     */
    public void changeUsersDepartmentList(Long tenantId, DepartmentAssignUserDTO dto) {
        UUID departmentOid = dto.getDepartmentOid();
        List<UUID> oldDepartmentList = dto.getOldDepartmentList();
        List<UUID> userOids = dto.getUserOids();
        Set<UUID> userSet = new HashSet<>();
        userSet.addAll(userOids);
        if(CollectionUtils.isNotEmpty(oldDepartmentList)){
            for (UUID oldDepartmentOid:oldDepartmentList) {
                List<DepartmentUserSummaryDTO> departmentUser = departmentService.getDepartmentUser(tenantId, oldDepartmentOid);
                List<UUID> departmentUserOids = departmentUser.stream().map(u -> u.getUserOid()).collect(Collectors.toList());
                userSet.addAll(departmentUserOids);
            }
        }
        userOids = userSet.stream().collect(Collectors.toList());
        this.changeUsersDepartment(departmentOid,userOids);
    }

    @Transactional
    public void changeUsersDepartment(UUID departmentOid, List<UUID> userOids) {
        for (UUID userOid : userOids) {
            UserDTO ContactCO = contactService.getUserDTOByUserOid(userOid);
            Department department = departmentService.getByDepartmentOid(departmentOid);

            Long oldDepartmentId = this.selectOne(new EntityWrapper<DepartmentUser>().eq("user_id", ContactCO.getId())).getDepartmentId();
            this.removeUserDepartment(Arrays.asList(ContactCO.getId()), oldDepartmentId);
            this.addOrUpdateUserDepartment(ContactCO.getId(),department.getId());

//            User user = userService.getByUserOid(userOid);
//            userService.saveUserWithoutDB(user);//创建索引，更新缓存
        }
    }

    public boolean updateByUserId(DepartmentUser departmentUser) {
        Long userId = departmentUser.getUserId();
        return super.update(departmentUser,new EntityWrapper<DepartmentUser>()
                .eq("user_id",userId));
    }

    public Optional<Department> getDepartmentByUserId(Long userId) {
        Set<Department> departments = findDepartmentByUserId(userId);
        if (departments.size() > 0) {
            return Optional.of(departments.iterator().next());
        }
        return Optional.empty();
    }
}
