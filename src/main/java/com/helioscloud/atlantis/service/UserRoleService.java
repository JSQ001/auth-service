package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.UserRole;
import com.helioscloud.atlantis.dto.UserRoleDTO;
import com.helioscloud.atlantis.persistence.UserRoleMapper;
import com.helioscloud.atlantis.util.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 * 用户角色Service
 */
@Service
public class UserRoleService extends BaseService<UserRoleMapper, UserRole> {

    private final UserRoleMapper userRoleMapper;

    private final RoleService roleService;

    public UserRoleService(UserRoleMapper roleMapper, RoleService roleService) {
        this.userRoleMapper = roleMapper;
        this.roleService = roleService;
    }

    /**
     * 保存用户角色
     *
     * @param userRole
     * @return
     */
    @Transactional
    public UserRole createUserRole(UserRole userRole) {
        //校验
        if (userRole == null || userRole.getId() != null) {
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        //检查用户角色组合
        Integer count = getUserRoleCountByUserIdAndRoleId(userRole.getUserId(), userRole.getRoleId());
        if (count != null && count > 1) {
            throw new BizException(RespCode.USER_ROLE_EXISTS);
        }
        userRoleMapper.insert(userRole);
        return userRole;
    }

    /**
     * 更新用户角色
     *
     * @param userRole1
     * @return
     */
    @Transactional
    public UserRole updateRole(UserRole userRole1) {
        //校验
        if (userRole1 == null || userRole1.getId() == null) {
            throw new BizException(RespCode.ID_NULL);
        }
        //校验ID是否在数据库中存在
        UserRole userRole = userRoleMapper.selectById(userRole1.getId());
        if (userRole == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
        }
        this.updateById(userRole1);
        return userRole1;
    }

    /**
     * 检查用户和角色的组合是否已经存在
     *
     * @param userId
     * @param roleId
     * @return
     */
    public Integer getUserRoleCountByUserIdAndRoleId(Long userId, Long roleId) {
        return userRoleMapper.selectCount(new EntityWrapper<UserRole>()
                .eq("user_id", userId)
                .eq("role_id", roleId));
    }

    /**
     * @param id 删除用户角色（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteUserRole(Long id) {
        UserRole userRole = userRoleMapper.selectById(id);
        userRole.setIsDeleted(true);
        userRoleMapper.updateById(userRole);
    }

    /**
     * @param ids 批量删除用户角色（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchUserRole(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            List<UserRole> result = null;
            List<UserRole> list = userRoleMapper.selectBatchIds(ids);
            if (list != null && list.size() > 0) {
                result = list.stream().map(userRole -> {
                    userRole.setIsDeleted(true);
                    return userRole;
                }).collect(Collectors.toList());
            }
            if(result != null){
                this.updateBatchById(result);
            }
        }
    }


    /**
     * 根据用户Id，获取分配的所有角色
     *
     * @param userId    用户ID
     * @param isDeleted 如果不传，默认取所有未删除的
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @param page
     * @return
     */
    public List<UserRoleDTO> getUserRolesByUserId(Long userId, Boolean isDeleted, Boolean isEnabled, Page page) {
        List<UserRoleDTO> result =  new ArrayList<UserRoleDTO>();
        List<UserRole> list = new ArrayList<UserRole>();
        if (isDeleted == null) {
            list = userRoleMapper.selectPage(page, new EntityWrapper<UserRole>()
                    .eq("is_deleted", false)
                    .eq(isEnabled != null, "is_enabled", isEnabled)
                    .eq("user_id", userId));
        } else {
            list = userRoleMapper.selectPage(page, new EntityWrapper<UserRole>()
                    .eq("is_deleted", isDeleted)
                    .eq(isEnabled != null, "is_enabled", isEnabled)
                    .eq("user_id", userId));
        }
        if (CollectionUtils.isNotEmpty(list)) {
            list.stream().forEach(e -> {
                UserRoleDTO userRoleDTO = new UserRoleDTO();
                userRoleDTO.setId(e.getId());
                userRoleDTO.setUserId(e.getUserId());
                userRoleDTO.setRole(roleService.getRoleById(e.getRoleId()));
                result.add(userRoleDTO);
            });
        }
        return result;
    }

    /**
     * 根据ID，获取对应的用户角色信息
     *
     * @param id
     * @return
     */
    public UserRole getUserRoleById(Long id) {
        return userRoleMapper.selectById(id);
    }

}
