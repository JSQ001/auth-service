package com.hand.hcf.app.mdata.contact.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.contact.domain.UserGroup;
import com.hand.hcf.app.mdata.contact.dto.UserGroupQO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface UserGroupMapper extends BaseMapper<UserGroup> {


    List<UserGroup> listByQO(UserGroupQO userGroupQO);

    List<UserGroup> listByQO(Page page, UserGroupQO userGroupQO);

    List<UserGroup> listByTenantIdAndUserGroupOids(@Param("tenantId") Long tenantId, @Param("userGroupOids") List<UUID> userGroupOids);

    List<UUID> findDefaultUserGroupV2(@Param("tenantId") Long tenantId,
                                      @Param("companyOid") UUID companyOid,
                                      @Param("userId") Long userId);

    Integer getUserGroupUser(@Param("userGroupId") Long userGroupId, @Param("userId") Long userId);

    List<Long> selectUserIdsByGroupId(@Param("userGroupId") Long userGroupId);

    UserGroup selectTenantGroupByName(@Param("name") String name, @Param("tenantId") Long tenantId, @Param("enabled") Boolean enabled);

    UserGroup selectTenantGroupByCode(@Param("code") String code, @Param("tenantId") Long tenantId, @Param("enabled") Boolean enabled);
}
