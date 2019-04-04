package com.hand.hcf.app.workflow.service;

import com.hand.hcf.app.workflow.util.RespCode;
import com.hand.hcf.app.workflow.domain.QuickReply;
import com.hand.hcf.app.workflow.enums.QuickReplyStatusEnum;
import com.hand.hcf.app.workflow.persistence.QuickReplyMapper;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/13
 */
@Transactional
@Service
public class QuickReplyService extends BaseService<QuickReplyMapper,QuickReply>{
    @Autowired
    private QuickReplyMapper quickReplyMapper;

    public List<QuickReply> findByUserOid (UUID userOid) {
        return quickReplyMapper.findByUserOid(userOid);
    }

    public QuickReply createOrUpdateQuickReply (QuickReply quickReply,UUID userOid) {
        if (quickReply.getReply().length() > 500) {
            throw new BizException(RespCode.QUICK_REPLY_REPLY_MORE_THAN_500);
        }
//        QuickReply quickReply = new QuickReply();
//        BeanUtils.copyProperties(quickReplyDTO, quickReply);
        Long id;
        //新建操作
        if (quickReply.getQuickReplyOid() == null) {
            Integer sequnce = 0;
            //查询当前人最大的序列
            List<QuickReply> existQuickReplyList = quickReplyMapper.findByUserOid(userOid);
            if (CollectionUtils.isNotEmpty(existQuickReplyList)) {
                sequnce = existQuickReplyList.get(existQuickReplyList.size() - 1).getSequenceNumber() + 1;
            } else {
                sequnce = 1;
            }
            quickReply.setUserOid(userOid);
            quickReply.setQuickReplyOid(UUID.randomUUID());
            quickReply.setSequenceNumber(sequnce);
            quickReply.setStatus(QuickReplyStatusEnum.NORMAL.getId());
            quickReplyMapper.insert(quickReply);
        } else {
            //更新操作
            quickReplyMapper.updateAllColumnById(quickReply);
        }
        return quickReply;
    }

    public void deleteByQuickReplyOids (List<UUID> quickReplyOids) {
        quickReplyMapper.deleteByQuickReplyOids(quickReplyOids);
    }
}
