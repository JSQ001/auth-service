package com.hand.hcf.app.workflow.brms.service;

import com.hand.hcf.app.workflow.brms.domain.DroolsRuleDetail;
import com.hand.hcf.app.workflow.brms.persistence.DroolsRuleDetailMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Service
public class DroolsRuleDetailService {
    Logger logger = LoggerFactory.getLogger(DroolsRuleDetailService.class);
    @Inject
    private DroolsRuleDetailMapper droolsRuleDetailMapper;

    public List<DroolsRuleDetail> findByRuleConditionAndApprover(UUID ruleConditionOid, UUID approverOid) {
        List<DroolsRuleDetail> droolsRuleDetails =
            droolsRuleDetailMapper.findByRuleConditionAndApprover(ruleConditionOid, approverOid);
        return droolsRuleDetails;
    }

    public List<DroolsRuleDetail> findByApprover(UUID approverOid) {
        long startTime = System.currentTimeMillis();
        List<DroolsRuleDetail> droolsRuleDetails =
            droolsRuleDetailMapper.findByApprover(approverOid);
        logger.info("JPA findByApprover execute: {}ms", (System.currentTimeMillis()-startTime));
        return droolsRuleDetails;
    }

}
