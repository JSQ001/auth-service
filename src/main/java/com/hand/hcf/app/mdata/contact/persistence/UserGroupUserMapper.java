package com.hand.hcf.app.mdata.contact.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.contact.domain.UserGroupUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface UserGroupUserMapper extends BaseMapper<UserGroupUser> {
    Integer associateUsersToGroup(@Param("id") Long Id,
                                  @Param("userGroupId") Long userGroupId,
                                  @Param("userGroupOid") UUID userGroupOid,
                                  @Param("userOid") UUID userOid);

     Integer deleteUsersFromGroup(@Param("userGroupId") Long userGroupId,
                                  @Param("userGroupOid") UUID userGroupOid,
                                  @Param("userOids") List<UUID> userOids);

     List<UserGroupUser> findUserGroupUsers(@Param("userOids") List<UUID> userOids);
}
