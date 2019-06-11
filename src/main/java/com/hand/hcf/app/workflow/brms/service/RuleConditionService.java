package com.hand.hcf.app.workflow.brms.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.RespCode;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.brms.constant.RuleConstants;
import com.hand.hcf.app.workflow.brms.domain.DroolsRuleDetail;
import com.hand.hcf.app.workflow.brms.domain.RuleApprover;
import com.hand.hcf.app.workflow.brms.domain.RuleCondition;
import com.hand.hcf.app.workflow.brms.domain.RuleConditionRelation;
import com.hand.hcf.app.workflow.brms.dto.RuleConditionDTO;
import com.hand.hcf.app.workflow.brms.dto.SimpleValueDetailDTO;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.brms.persistence.RuleConditionMapper;
import com.hand.hcf.app.workflow.brms.util.cache.CacheNames;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@CacheConfig(cacheNames = CacheNames.BRMS_ENTITY_RULE_CONDITION)
public class RuleConditionService extends BaseService<RuleConditionMapper, RuleCondition> {


    @Inject
    private ObjectMapper objectMapper;

    @Autowired
    MapperFacade mapper;

    @Autowired
    RuleConditionRelationService ruleConditionRelationService;

    @Autowired
    RuleApproverService ruleApproverService;

    @Autowired
    DroolsService droolsService;

    /**
     * get one RuleCondition by id.
     *
     * @return the entity
     */
    @Transactional(readOnly = true)
    //@Cacheable(key = "#ruleConditionOid.toString()")
    public RuleCondition getRuleCondition(UUID ruleConditionOid) {

        return selectOne(new EntityWrapper<RuleCondition>()
                .eq("rule_condition_oid", ruleConditionOid));
    }

    @Transactional(readOnly = true)
    public List<RuleCondition> findByRuleConditionOidIn(List<UUID> ruleConditionOids) {
        if (CollectionUtils.isEmpty(ruleConditionOids)) {
            return new ArrayList<RuleCondition>();
        }

        return selectList(new EntityWrapper<RuleCondition>()
                .in("rule_condition_oid", ruleConditionOids)
                .eq("status", RuleApprovalEnum.VALID.getId()));
    }

    @Transactional(readOnly = true)
    public RuleCondition getFormRuleCondition(UUID formOid) {
        return selectOne(new EntityWrapper<RuleCondition>()
                .eq("type_number", RuleConstants.CONDITION_TYPE_FORM)
                .eq("rule_value", formOid)
                .eq("status", RuleApprovalEnum.VALID.getId()));
    }

    @Transactional(readOnly = true)
    public List<RuleCondition> getFormRuleCondition(List<UUID> formOids) {
        if (CollectionUtils.isEmpty(formOids)) {
            return null;
        }
        List<String> values = formOids.stream().map(UUID::toString).collect(Collectors.toList());
        return selectList(new EntityWrapper<RuleCondition>()
                .eq("type_number", RuleConstants.CONDITION_TYPE_FORM)
                .in("rule_value", values)
                .eq("status", RuleApprovalEnum.VALID.getId()));
    }

    @Transactional(readOnly = true)
    public RuleCondition findTopOneByStatusOrderByBatchCodeDesc() {

        return selectOne(new EntityWrapper<RuleCondition>()
                .eq("status", RuleApprovalEnum.VALID.getId())
                .orderBy("batch_code", false));

    }

    @CacheEvict(key = "#ruleConditionOid.toString()")
    public int delete(UUID ruleConditionOid) {
        return delete(Arrays.asList(ruleConditionOid));
    }

    public int delete(List<UUID> ruleConditionOids) {
        return updateRuleConditionStatus(ruleConditionOids, RuleApprovalEnum.DELETED.getId());
    }

    public void deleteData(List<RuleCondition> ruleConditionOids) {
        super.deleteBatchIds(ruleConditionOids);
    }

    private int updateRuleConditionStatus(List<UUID> ruleConditionOids, Integer status) {
        List<RuleCondition> ruleConditions = selectList(new EntityWrapper<RuleCondition>()
                .in("rule_condition_oid", ruleConditionOids)
                .eq("status", RuleApprovalEnum.VALID.getId()));

        if (CollectionUtils.isEmpty(ruleConditions)) {
            return 0;
        }
        ruleConditions.stream().forEach(ruleCondition -> {
            ruleCondition.setStatus(status);
            ruleCondition.setCreatedDate(ZonedDateTime.now());
            conditionSave(ruleCondition);
        });
        return ruleConditions.size();
    }

    @CacheEvict(key = "#ruleCondition.ruleConditionOid.toString()")
    public void conditionSave(RuleCondition ruleCondition) {
        insertOrUpdate(ruleCondition);
    }

    @CacheEvict(key = "#ruleCondition.ruleConditionOid.toString()")
    public RuleCondition save(RuleCondition ruleCondition) {
        if (StringUtils.isEmpty(ruleCondition.getRuleConditionOid())) {
            //insert
            ruleCondition.setRuleConditionOid(UUID.randomUUID());
            ruleCondition.setStatus(RuleApprovalEnum.VALID.getId());
//            return ruleConditionMapper.save(ruleCondition);
        } else {
            //update
            RuleCondition opt = getRuleCondition(ruleCondition.getRuleConditionOid());
            if (opt != null) {
                if (!opt.getStatus().equals(RuleApprovalEnum.VALID.getId())) {
                    throw new ValidationException(new ValidationError("approvalRule.saveRuleCondition", "status error ruleConditionOid : " + ruleCondition.getRuleConditionOid().toString()));
                }
                ruleCondition.setId(opt.getId());
                ruleCondition.setStatus(opt.getStatus());
            } else {
                throw new ValidationException(new ValidationError("approvalRule.saveRuleCondition", "not exist : " + ruleCondition.getRuleConditionOid().toString()));
            }
        }

        //Create Rule Based on the rule template
//        String drl = droolsService.generateDrlFromTemplate(ruleCondition);
//        ruleCondition.setDroolsRuleValue(drl);
        ruleCondition.setCreatedDate(ZonedDateTime.now());
        insertOrUpdate(ruleCondition);
        return ruleCondition;
    }


    private void handleDepartmentPath(RuleConditionDTO ruleConditionDTO) {
        if (ruleConditionDTO == null || !RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_PATH.equals(ruleConditionDTO.getRemark()) || ruleConditionDTO.getValueDetail() == null || ruleConditionDTO.getValueDetail().getValue() == null) {
            return;
        }
        ArrayList<String> newValue = new ArrayList<>();
        ruleConditionDTO.getValueDetail().getValue().stream().forEach(v -> {
            if (!v.endsWith("|")) {
                v = v + "|";
            }
            newValue.add(v);
        });
        ruleConditionDTO.getValueDetail().setValue(newValue);
    }




    /**
     * 规则条件相关接口 ***********************
     */
    public RuleConditionDTO createRuleCondition(RuleConditionDTO ruleConditionDTO) {
        if (ruleConditionDTO.getEntityType() == null || ruleConditionDTO.getEntityOid() == null) {
            throw new BizException("approvalRule.createRuleCondition", "entityType or entityOid is null");
        }
        //对于部门路径数据特殊处理
        handleDepartmentPath(ruleConditionDTO);
        //TODO 公司数据隔离
        RuleApprovalEnum ruleApprovalEnum = RuleApprovalEnum.parse(ruleConditionDTO.getEntityType());
        switch (ruleApprovalEnum) {
            case CONDITION_RELATION_TYPE_SCENE:
                break;
            case CONDITION_RELATION_TYPE_NOTICE:
            case CONDITION_RELATION_TYPE_APPROVER:
                RuleApprover ruleApprover = null;
                UUID ruleApproverOid = null;

                if (RuleApprovalEnum.CONDITION_RELATION_TYPE_APPROVER.getId().equals(ruleApprovalEnum.getId())) {
                    ruleApprover = ruleApproverService.getRuleApprover(ruleConditionDTO.getEntityOid());
                    if (ruleApprover == null) {
                        throw new BizException("approvalRule.createRuleCondition", "can not find entity , entityType : " + ruleConditionDTO.getEntityType() + " , entityOid : " + ruleConditionDTO.getEntityOid());
                    }

                    ruleApproverOid = ruleApprover.getRuleApproverOid();
                }

                RuleCondition newRuleCondition = fromDTO(ruleConditionDTO);


                //TODO
                if (newRuleCondition.getBatchCode() == null || newRuleCondition.getBatchCode() == 0L) {
                    RuleCondition lastCondition = findTopOneByStatusOrderByBatchCodeDesc();
                    if (lastCondition == null) {
                        newRuleCondition.setBatchCode(RuleConstants.RULE_BATCH_CODE_DEFAULT);
                    } else {
                        newRuleCondition.setBatchCode(lastCondition.getBatchCode() + RuleConstants.RULE_BATCH_CODE_INCREMENT);
                    }
                }
                newRuleCondition = save(newRuleCondition);
                ///关联
                ruleConditionRelationService.save(
                        RuleConditionRelation.builder()
                                .ruleConditionOid(newRuleCondition.getRuleConditionOid())
                                .entityType(ruleApprovalEnum.getId())
                                .entityOid(ruleConditionDTO.getEntityOid())
                                .build()
                );

                //创建DroolsRule相关
                DroolsRuleDetail droolsRuleDetail = new DroolsRuleDetail();
                droolsRuleDetail.setRuleCondition(newRuleCondition);
                droolsRuleDetail.setDroolsRuleDetailOid(UUID.randomUUID());
                droolsRuleDetail.setRuleConditionOid(newRuleCondition.getRuleConditionOid());
                droolsRuleDetail.setRuleConditionApproverOid(ruleApproverOid);
                droolsService.save(droolsRuleDetail, ruleApprover, ruleConditionDTO);

                return toDTO(newRuleCondition);
            //break;
            case CONDITION_RELATION_TYPE_TRANSFER:
                break;
            default:
                throw new BizException("approvalRule.createRuleCondition", "entityType error");
        }
        throw new RuntimeException("not implement yet");
        //return null;
    }


    /**
     * 规则条件相关接口 ***********************
     */
    public List<RuleConditionDTO> createRuleCondition(List<RuleConditionDTO> ruleConditionDTOList) {
        UUID userOid = OrgInformationUtil.getCurrentUserOid();
        List<RuleConditionDTO> ruleConditionDTOs = new ArrayList<>();
        Long batchCode = null;
        for (RuleConditionDTO ruleConditionDTO : ruleConditionDTOList) {
            if (ruleConditionDTO.getBatchCode() == null || ruleConditionDTO.getBatchCode() == 0L) {
                if (batchCode != null) {
                    ruleConditionDTO.setBatchCode(batchCode);
                }
            }
            RuleConditionDTO newRuleConditionDTO = createRuleCondition(ruleConditionDTO);

            if (newRuleConditionDTO!=null) {
                batchCode = newRuleConditionDTO.getBatchCode();
                ruleConditionDTOs.add(newRuleConditionDTO);
            }
        }
        return ruleConditionDTOs;
    }


    public RuleConditionDTO updateRuleCondition(RuleConditionDTO ruleConditionDTO, UUID userOid) {
        if (ruleConditionDTO.getRuleConditionOid() == null) {
            throw new BizException("approvalRule.updateRuleCondition", "ruleConditionOid is null");
        }
        RuleCondition existRuleCondition = getRuleCondition(ruleConditionDTO.getRuleConditionOid());
        if (existRuleCondition == null) {
            throw new BizException("approvalRule.updateRuleCondition", "ruleCondition not exist , ruleConditionOid : " + ruleConditionDTO.getRuleConditionOid());
        }
        //对于部门路径数据特殊处理
        handleDepartmentPath(ruleConditionDTO);
        //TODO 公司数据隔离

        existRuleCondition.setRuleField(ruleConditionDTO.getField());
        existRuleCondition.setFieldTypeId(ruleConditionDTO.getFieldTypeId());
        existRuleCondition.setSymbol(ruleConditionDTO.getSymbol());
        existRuleCondition.setCode(ruleConditionDTO.getCode());
        existRuleCondition.setName(ruleConditionDTO.getName());
        existRuleCondition.setRemark(ruleConditionDTO.getRemark());
        existRuleCondition.setRuleValue(ruleConditionDTO.getValue());

        //更新DroolsRule相关
        DroolsRuleDetail droolsRuleDetail = droolsService.findByRuleConditionOid(existRuleCondition);
        droolsRuleDetail.setRuleCondition(this.selectById(droolsRuleDetail.getRuleConditionId()));
        RuleConditionRelation ruleConditionRelations = ruleConditionRelationService.findByRuleConditionOid(ruleConditionDTO.getRuleConditionOid());

        RuleApprover ruleApprover = null;
        if (RuleApprovalEnum.CONDITION_RELATION_TYPE_APPROVER.equals(ruleConditionRelations.getEntityOid())) {
            ruleApprover = ruleApproverService.getRuleApprover(ruleConditionRelations.getEntityOid());
        }

        droolsService.save(droolsRuleDetail, ruleApprover, ruleConditionDTO);

        StringWriter sw = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(sw, ruleConditionDTO.getValueDetail());
            existRuleCondition.setValueDetail(sw.toString());
            sw.close();
        } catch (IOException e) {
            throw new BizException(RespCode.SYS_FAILED, e.getMessage());
        }
        existRuleCondition.setBatchCode(ruleConditionDTO.getBatchCode());
        //TODO 增加可修改字段时需增加
        existRuleCondition = save(existRuleCondition);
        return toDTO(existRuleCondition);

    }

    public List<RuleConditionDTO> updateRuleCondition(List<RuleConditionDTO> ruleConditionDTOList, UUID userOid) {
        List<RuleConditionDTO> ruleConditionDTOS = new ArrayList<>();
        for (RuleConditionDTO ruleConditionDTO : ruleConditionDTOList) {
            ruleConditionDTOS.add(updateRuleCondition(ruleConditionDTO, userOid));
        }
        return ruleConditionDTOS;
    }

    public RuleConditionDTO getRuleCondition(UUID ruleConditionOid, UUID userOid) {
        if (ruleConditionOid == null) {
            throw new BizException("approvalRule.getRuleCondition", "ruleConditionOid is null");
        }
        //TODO 公司数据隔离
        RuleCondition existRuleCondition = getRuleCondition(ruleConditionOid);
        if (existRuleCondition == null) {
            throw new ObjectNotFoundException(RuleCondition.class, ruleConditionOid);
        }
        return toDTO(existRuleCondition);
    }

    public int deleteRuleCondition(UUID ruleConditionOid, UUID userOid) {
        if (ruleConditionOid == null) {
            return 0;
        }
        return deleteRuleCondition(Arrays.asList(ruleConditionOid), userOid);
    }

    public int deleteRuleConditionBatch(List<UUID> ruleConditionOidList, UUID userOid) {
        if (org.apache.commons.collections.CollectionUtils.isEmpty(ruleConditionOidList)) {
            return 0;
        }
        return deleteRuleCondition(ruleConditionOidList, userOid);
    }

    public int deleteRuleCondition(List<UUID> ruleConditionOids, UUID userOid) {
        //TODO 公司数据隔离
        ruleConditionRelationService.deleteByRuleConditionOid(ruleConditionOids);
        return delete(ruleConditionOids);
    }


    public RuleCondition fromDTO(RuleConditionDTO ruleConditionDTO) {
        RuleCondition ruleCondition = mapper.map(ruleConditionDTO, RuleCondition.class);
        ruleCondition.setRuleValue(ruleConditionDTO.getValue());
        ruleCondition.setRuleField(ruleConditionDTO.getField());
        ruleCondition.setTypeNumber(ruleConditionDTO.getType());

        if (ruleConditionDTO.getValueDetail() != null) {
            try {
                StringWriter sw = new StringWriter();
                objectMapper.writeValue(sw, ruleConditionDTO.getValueDetail());
                ruleCondition.setValueDetail(sw.toString());
                sw.close();
            } catch (IOException e) {

                throw new BizException(RespCode.SYS_FAILED, e.getMessage());
            }
        }

        return ruleCondition;
    }


    public RuleConditionDTO toDTO(RuleCondition ruleCondition) {
        RuleConditionDTO ruleConditionDTO = mapper.map(ruleCondition, RuleConditionDTO.class);
        ruleConditionDTO.setValue(ruleCondition.getRuleValue());
        ruleConditionDTO.setField(ruleCondition.getRuleField());
        ruleConditionDTO.setType(ruleCondition.getTypeNumber());
        if (ruleCondition.getValueDetail() != null) {
            try {
                SimpleValueDetailDTO simpleValueDetailDTO = objectMapper.readValue(ruleCondition.getValueDetail(), SimpleValueDetailDTO.class);

                ruleConditionDTO.setValueDetail(simpleValueDetailDTO);
            } catch (IOException e) {

                throw new BizException(RespCode.SYS_FAILED, e.getMessage());
            }

        }
        return ruleConditionDTO;
    }
}
