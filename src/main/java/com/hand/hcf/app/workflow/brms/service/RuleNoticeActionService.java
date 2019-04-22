package com.hand.hcf.app.workflow.brms.service;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.workflow.brms.domain.RuleNotice;
import com.hand.hcf.app.workflow.brms.domain.RuleNoticeAction;
import com.hand.hcf.app.workflow.brms.persistence.RuleNoticeActionMapper;
import com.hand.hcf.app.workflow.brms.persistence.RuleNoticeMapper;
import com.hand.hcf.app.core.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author mh.z
 * @date 2019/04/16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RuleNoticeActionService extends BaseService<RuleNoticeActionMapper, RuleNoticeAction> {

    /**
     * 返回跟指定审批流通知关联的动作
     * @author mh.z
     * @date 2019/04/16
     *
     * @param ruleNoticeId
     * @return
     */
    public List<RuleNoticeAction> listByNoticeId(Long ruleNoticeId) {
        EntityWrapper<RuleNoticeAction> wrapper = new EntityWrapper<RuleNoticeAction>();
        wrapper.eq("rule_notice_id", ruleNoticeId);

        List<RuleNoticeAction> ruleNoticeActionList = selectList(wrapper);
        return ruleNoticeActionList;
    }

    /**
     * 批量删除审批流通知关联的动作
     * @author mh.z
     * @date 2019/04/16
     *
     * @param ruleNoticeId
     */
    public void batchDeleteByNoticeId(Long ruleNoticeId) {
        EntityWrapper<RuleNoticeAction> wrapper = new EntityWrapper<RuleNoticeAction>();
        wrapper.eq("rule_notice_id", ruleNoticeId);
        delete(wrapper);
    }

}
