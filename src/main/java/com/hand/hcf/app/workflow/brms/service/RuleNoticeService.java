package com.hand.hcf.app.workflow.brms.service;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.workflow.brms.domain.*;
import com.hand.hcf.app.workflow.brms.dto.RuleApproverDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleConditionDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleNoticeDTO;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.brms.persistence.RuleNoticeMapper;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mh.z
 * @date 2019/04/16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RuleNoticeService extends BaseService<RuleNoticeMapper, RuleNotice> {
    @Autowired
    private RuleNoticeActionService ruleNoticeActionService;

    @Autowired
    private RuleApproverService ruleApproverService;

    @Autowired
    private RuleConditionService ruleConditionService;

    @Autowired
    private RuleConditionRelationService ruleConditionRelationService;

    @Autowired
    private MapperFacade mapper;

    /**
     * 根据审批流通知oid获取
     * @author mh.z
     * @date 2019/04/16
     *
     * @param ruleNoticeOid
     * @return
     */
    public RuleNotice getByNodeOid(UUID ruleNoticeOid) {
        EntityWrapper<RuleNotice> wrapper = new EntityWrapper<RuleNotice>();
        wrapper.eq("rule_notice_oid", ruleNoticeOid);

        RuleNotice ruleNotice = selectOne(wrapper);
        return ruleNotice;
    }


    /**
     * 返回跟指定节点关联的审批流通知
     * @author mh.z
     * @date 2019/04/16
     *
     * @param ruleApprovalNodeId
     * @return
     */
    public List<RuleNotice> listByNodeId(Long ruleApprovalNodeId) {
        EntityWrapper<RuleNotice> wrapper = new EntityWrapper();
        wrapper.eq("rule_approval_node_id", ruleApprovalNodeId);
        wrapper.orderBy("created_date", true);

        List<RuleNotice> ruleNoticeList = selectList(wrapper);
        return ruleNoticeList;
    }


    /**
     * 级联删除审批流通知
     * @author mh.z
     * @date 2019/04/16
     *
     * @param ruleNoticeId
     */
    public void deleteRuleNoticeOnCascade(Long ruleNoticeId) {
        if (ruleNoticeId == null) {
            throw new IllegalArgumentException("ruleNoticeId null");
        }

        // 删除审批流通知动作
        ruleNoticeActionService.batchDeleteByNoticeId(ruleNoticeId);

        // 删除审批流通知人员
        deleteRuleNoticeUsersOnCascade(ruleNoticeId);

        // 删除审批流通知
        deleteById(ruleNoticeId);
    }

    /**
     * 级联删除审批流通知人员
     * @author mh.z
     * @date 2019/04/16
     *
     * @param ruleNoticeId
     */
    protected void deleteRuleNoticeUsersOnCascade(Long ruleNoticeId) {
        if (ruleNoticeId == null) {
            throw new IllegalArgumentException("ruleNoticeId null");
        }

        RuleNotice ruleNotice = selectById(ruleNoticeId);
        UUID ruleNoticeOid = ruleNotice.getRuleNoticeOid();

        // 删除审批流通知条件
        List<RuleConditionRelation> ruleConditionRelationList = ruleConditionRelationService
                .listByEntityTypeAndEntityOid(RuleApprovalEnum.CONDITION_RELATION_TYPE_NOTICE.getId(), ruleNoticeOid);
        if (ruleConditionRelationList.size() > 0) {
            for (RuleConditionRelation ruleConditionRelation : ruleConditionRelationList) {
                ruleConditionRelation.setStatus(RuleApprovalEnum.DELETED.getId());
            }
            ruleConditionRelationService.updateBatchById(ruleConditionRelationList);
        }

        // 删除审批流通知人员
        List<RuleApprover> ruleApproverList = ruleApproverService.listByNoticeId(ruleNoticeId);
        if (ruleApproverList.size() > 0) {
            for (RuleApprover ruleApprover : ruleApproverList) {
                ruleApprover.setStatus(RuleApprovalEnum.DELETED.getId());
            }
            ruleApproverService.updateBatchById(ruleApproverList);
        }
    }

    /**
     * 转换成RuleNoticeDTO
     * @author mh.z
     * @date 2019/04/16
     *
     * @param ruleNotice
     * @return
     */
    public RuleNoticeDTO toRuleNoticeDTO(RuleNotice ruleNotice) {
        if (ruleNotice == null) {
            return null;
        }

        Long ruleNoticeId = ruleNotice.getId();
        UUID ruleNoticeOid = ruleNotice.getRuleNoticeOid();

        // 通知动作
        List<RuleNoticeAction> ruleNoticeActionList = ruleNoticeActionService.listByNoticeId(ruleNoticeId);
        List<Integer> ruleNoticeActionOidList = new ArrayList<Integer>();

        for (RuleNoticeAction ruleNoticeAction : ruleNoticeActionList) {
            ruleNoticeActionOidList.add(ruleNoticeAction.getActionType());
        }

        // 通知人员
        List<RuleApprover> ruleApproverList = ruleApproverService.listByNoticeId(ruleNoticeId);
        List<RuleApproverDTO> ruleApproverDTOList = mapper.mapAsList(ruleApproverList, RuleApproverDTO.class);

        // 通知条件
        Map<Long, List<RuleConditionDTO>> ruleConditionDTOMap = groupingNoticeConditions(ruleNoticeOid);

        RuleNoticeDTO ruleNoticeDTO = new RuleNoticeDTO();
        ruleNoticeDTO.setRuleNoticeOid(ruleNotice.getRuleNoticeOid());
        ruleNoticeDTO.setRuleApprovalNodeOid(ruleNotice.getRuleNoticeOid());
        ruleNoticeDTO.setActions(ruleNoticeActionOidList);
        ruleNoticeDTO.setUsers(ruleApproverDTOList);
        ruleNoticeDTO.setConditions(ruleConditionDTOMap);
        return ruleNoticeDTO;
    }

    /**
     * 转换成RuleNoticeDTO列表
     * @author mh.z
     * @date 2019/04/16
     *
     * @param ruleNoticeList
     * @return
     */
    public List<RuleNoticeDTO> toRuleNoticeDTOList(List<RuleNotice> ruleNoticeList) {
        List<RuleNoticeDTO> ruleNoticeDTOList = new ArrayList<RuleNoticeDTO>();
        RuleNoticeDTO ruleNoticeDTO = null;

        for (RuleNotice ruleNotice : ruleNoticeList) {
            ruleNoticeDTO = toRuleNoticeDTO(ruleNotice);
            ruleNoticeDTOList.add(ruleNoticeDTO);
        }

        return ruleNoticeDTOList;
    }

    /**
     * 对通知条件分组
     * @author mh.z
     * @date 2019/04/16
     *
     * @param ruleNoticeOid
     * @return
     */
    protected Map<Long, List<RuleConditionDTO>> groupingNoticeConditions(UUID ruleNoticeOid) {
        List<RuleConditionRelation> ruleConditionRelationList = ruleConditionRelationService.findEntityOid(ruleNoticeOid);
        List<UUID> ruleConditionOidList = ruleConditionRelationList.stream().map(RuleConditionRelation::getRuleConditionOid).collect(Collectors.toList());
        List<RuleCondition> ruleConditionList = ruleConditionService.findByRuleConditionOidIn(ruleConditionOidList);

        Map<Long, List<RuleConditionDTO>> ruleConditionDTOMap = new HashMap<Long, List<RuleConditionDTO>>();
        Long batchId = null;
        List<RuleConditionDTO> batchList = null;

        for (RuleCondition ruleCondition : ruleConditionList) {
            batchId = ruleCondition.getBatchCode();
            batchList = ruleConditionDTOMap.get(batchId);

            if (batchList == null) {
                batchList = new ArrayList<RuleConditionDTO>();
                ruleConditionDTOMap.put(batchId, batchList);
            }

            batchList.add(ruleConditionService.toDTO(ruleCondition));
        }

        return ruleConditionDTOMap;
    }

}
