package com.hand.hcf.app.mdata.contact.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.contact.domain.UserGroupCondition;

import java.util.List;

public interface UserGroupConditionMapper extends BaseMapper<UserGroupCondition> {
    Integer selectMaxSequenceOfUserGroup(Long userGroupId);

    List<Integer> getConditionUserGroupSeqCountList(Long userGroupId);

    List<Integer> getEnableConditionUserGroupSeqCountList(Long userGroupId);
}
