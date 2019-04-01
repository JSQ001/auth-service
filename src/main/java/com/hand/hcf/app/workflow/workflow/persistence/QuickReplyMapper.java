package com.hand.hcf.app.workflow.workflow.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.workflow.workflow.domain.QuickReply;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/13
 */
public interface QuickReplyMapper extends BaseMapper<QuickReply> {
    List<QuickReply> findByUserOid(@Param(value = "userOid") UUID userOid);

    void deleteByUserOid(@Param(value = "userOid") UUID userOid);

    void deleteByQuickReplyOids(@Param(value = "quickReplyOids") List<UUID> quickReplyOids);
}
