package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.cloudhelios.atlantis.util.LoginInformationUtil;
import com.helioscloud.atlantis.domain.Role;
import com.helioscloud.atlantis.persistence.RoleMapper;
import com.helioscloud.atlantis.util.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 * 角色Service
 */
@Service
public class RoleService extends BaseService<RoleMapper, Role> {

    private final RoleMapper roleMapper;

    public RoleService(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    /**
     * 创建角色
     *
     * @param role
     * @return
     */
    @Transactional
    public Role createRole(Role role) {
        //校验
        if (role == null || role.getId() != null) {
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        if (role.getRoleCode() == null || "".equals(role.getRoleCode())) {
            throw new BizException(RespCode.ROLE_CODE_NULL);
        }
        if (role.getRoleName() == null || "".equals(role.getRoleName())) {
            throw new BizException(RespCode.ROLE_NAME_NULL);
        }
        //检查租户下是否已经存在该角色代码
        Integer count = getRoleCountByTenantIdAndRoleCode(LoginInformationUtil.getCurrentTenantID(), role.getRoleCode());
        if (count != null && count > 0) {
            throw new BizException(RespCode.CODE_NOT_UNION_IN_TENANT);
        }
        Long tenantId = LoginInformationUtil.getCurrentTenantID();
        role.setTenantId(tenantId);
        roleMapper.insert(role);
        return role;
    }

    /**
     * 更新角色
     *
     * @param role
     * @return
     */
    @Transactional
    public Role updateRole(Role role) {
        //校验
        if (role == null || role.getId() == null) {
            throw new BizException(RespCode.ID_NULL);
        }
        if (role.getRoleName() == null || "".equals(role.getRoleName())) {
            throw new BizException(RespCode.ROLE_NAME_NULL);
        }
        //校验ID是否在数据库中存在
        Role rr = roleMapper.selectById(role.getId());
        if (rr == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
        }
        if (role.getEnabled() == null || "".equals(role.getEnabled())) {
            role.setEnabled(rr.getEnabled());
        }
        if (role.getDeleted() == null || "".equals(role.getDeleted())) {
            role.setDeleted(rr.getDeleted());
        }
        role.setCreatedBy(rr.getCreatedBy());
        role.setCreatedDate(rr.getCreatedDate());
        role.setRoleCode(rr.getRoleCode());
        role.setTenantId(rr.getTenantId());
        this.updateById(role);
        return role;
    }

    /**
     * 根据租户ID和角色代码，检查租户下是否存在相同的角色代码
     *
     * @param tenantId
     * @param roleCode
     * @return
     */
    public Integer getRoleCountByTenantIdAndRoleCode(Long tenantId, String roleCode) {
        return roleMapper.selectCount(new EntityWrapper<Role>()
                .eq("tenant_id", tenantId)
                .eq("role_code", roleCode));
    }

    /**
     * @param id 删除角色（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteRole(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
        /*Role role = roleMapper.selectById(id);
        role.setDeleted(true);
        roleMapper.updateById(role);*/
        //this.deleteById(id);
    }

    /**
     * @param ids 批量删除角色（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchRole(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
            /*List<Role> result = null;
            List<Role> list = roleMapper.selectBatchIds(ids);
            if (list != null && list.size() > 0) {
                result = list.stream().map(role -> {
                    role.setDeleted(true);
                    return role;
                }).collect(Collectors.toList());
            }
            if(result != null){
                this.updateBatchById(result);
            }*/
        }
    }


    /**
     * 当租户下所有角色 分页
     *
     * @param tenantId
     * @param page
     * @param enabled  如果不传，则不控制，如果传了，则根据传的值控制
     * @param roleCode 角色代码 如果不传，则不控制，如果传了，则根据传的值模糊查询
     * @param roleName 角色名称 如果不传，则不控制，如果传了，则根据传的值模糊查询
     * @return
     */
    public List<Role> getRolesByTenantIdCond(Long tenantId, String roleCode, String roleName, Boolean enabled, Page page) {
        return roleMapper.selectPage(page, new EntityWrapper<Role>()
                .eq(enabled != null, "enabled", enabled)
                .like(roleCode != null, "role_code", roleCode, SqlLike.DEFAULT)
                .like(roleName != null, "role_name", roleName, SqlLike.DEFAULT)
                .eq("tenant_id", tenantId)
                .orderBy("enabled", false)
                .orderBy("role_code")
        );

        /* if (isDeleted == null || "".equals(isDeleted)) {
            return roleMapper.selectPage(page, new EntityWrapper<Role>()
                    .eq("deleted", false)
                    .eq(enabled != null, "enabled", enabled)
                    .eq("tenant_id", tenantId));
        } else {
            return roleMapper.selectPage(page, new EntityWrapper<Role>()
                    .eq("deleted", isDeleted)
                    .eq(enabled != null, "enabled", enabled)
                    .eq("tenant_id", tenantId));
        }*/
    }

    /**
     * 根据ID，获取对应的角色信息
     *
     * @param id
     * @return
     */
    public Role getRoleById(Long id) {
        return roleMapper.selectById(id);
    }
}
