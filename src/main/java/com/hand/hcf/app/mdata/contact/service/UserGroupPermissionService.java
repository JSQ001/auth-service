package com.hand.hcf.app.mdata.contact.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.contact.domain.UserGroupPermission;
import com.hand.hcf.app.mdata.contact.persistence.UserGroupPermissionMapper;
import com.hand.hcf.app.mdata.utils.RespCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class UserGroupPermissionService extends BaseService<UserGroupPermissionMapper, UserGroupPermission> {

    @Autowired
    private UserGroupPermissionMapper userGroupPermissionMapper;
    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    @Qualifier("taskExecutor")
    Executor executor;

    public List<UserGroupPermission> upsertPermissions(List<UserGroupPermission> permissions, UUID userOid) {
        return permissions.stream().map(u -> this.upsertPermission(u, userOid)).collect(Collectors.toList());
    }

    public UserGroupPermission upsertPermission(UserGroupPermission permission, UUID userOid) {
        Long permissionId = permission.getId();
        if (permissionId == null) {
            //create
            insert(permission);
            permissionId = permission.getId();
        } else {
            UserGroupPermission exist = selectById(permissionId);
            if (exist == null) {
                throw new BizException(RespCode.USER_GROUP_PERMISSSION_NOT_EXIST);
            }

            updateById(permission);
        }
        return selectById(permissionId);
    }

    public List<UserGroupPermission> findUserGroupPermissions(Long currentTenantID, Long objectId, String objectType, Boolean isEnabled) {
        Wrapper<UserGroupPermission> wp = new EntityWrapper<UserGroupPermission>()
                .eq("tenant_id", currentTenantID)
                .eq("object_id", objectId)
                .eq("object_type", objectType)
                .eq("deleted", false);
        if (isEnabled != null) {
            wp.eq("enabled", isEnabled);
        }
        return userGroupPermissionMapper.selectList(wp);
    }

    public void deleteGroupPermission(Long permissionId) {
        UserGroupPermission userGroupPermission = selectById(permissionId);
        if (userGroupPermission == null) {
            throw new BizException(RespCode.USER_GROUP_PERMISSSION_NOT_EXIST);
        }
        userGroupPermission.setDeleted(true);
        userGroupPermissionMapper.updateById(userGroupPermission);
    }

    public Map<Long, Boolean> getUserObjectBelong(List<Long> objectIds, String objectType, Long userId, Long tenantId) throws InterruptedException {
        Map<Long, Boolean> belongList = new HashMap<>();
        CompletionService cs = new ExecutorCompletionService(executor);
        for (Long objectId : objectIds) {
            cs.submit(() -> {
                log.info("启动线程查询object->user objectId:" + objectId);
                Map<Long, Boolean> userObjectBelongSingle = this.getUserObjectBelongSingle(objectId, objectType, userId, tenantId);
                return userObjectBelongSingle;
            });
        }
        for (int i = 1; i <= objectIds.size(); i++) {
            try {
                Map<Long, Boolean> oneBelong = (Map<Long, Boolean>) cs.take().get();
                belongList.putAll(oneBelong);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return belongList;
    }

    public Map<Long, Boolean> getUserObjectBelongSingle(Long objectId, String objectType, Long userId, Long tenantId) {
        List<UserGroupPermission> userGroupPermissions = this.findUserGroupPermissions(tenantId, objectId, objectType, true);
        List<Long> userGroupIds = userGroupPermissions.stream().map(u -> u.getUserGroupId()).collect(Collectors.toList());
        Boolean aBoolean = userGroupService.hasUserGroupPermissionForMuti(userGroupIds, userId);
        Boolean inGroup = aBoolean;
        Map<Long, Boolean> belong = new HashMap<>();
        belong.put(objectId, inGroup);
        return belong;
    }
}
