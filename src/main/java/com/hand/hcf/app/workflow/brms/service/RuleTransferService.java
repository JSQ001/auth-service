package com.hand.hcf.app.workflow.brms.service;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.workflow.brms.domain.RuleCondition;
import com.hand.hcf.app.workflow.brms.domain.RuleConditionRelation;
import com.hand.hcf.app.workflow.brms.domain.RuleTransfer;
import com.hand.hcf.app.workflow.brms.dto.RuleTransferDTO;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.brms.persistence.RuleTransferMapper;
import com.hand.hcf.app.workflow.constant.RuleConstants;
import com.hand.hcf.app.workflow.dto.ApprovalFormDTO;
import com.hand.hcf.core.exception.core.ValidationError;
import com.hand.hcf.core.exception.core.ValidationException;
import com.hand.hcf.core.service.BaseService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RuleTransferService extends BaseService<RuleTransferMapper, RuleTransfer> {

    @Inject
    RuleConditionService ruleConditionService;
    @Inject
    RuleConditionRelationService ruleConditionRelationService;

    @Inject
    RuleService ruleService;

    @Autowired
    MapperFacade mapper;

    @Transactional(readOnly = true)
    public List<RuleTransferDTO> listDTOAllBySourceOid(UUID sourceOid, Page page) {
        Page<RuleTransfer> list = selectPage(page, new EntityWrapper<RuleTransfer>()
                .eq("source_oid", sourceOid)
                .in("status",
                        Arrays.asList(RuleApprovalEnum.VALID.getId(), RuleApprovalEnum.INVALID.getId())));
        List<RuleTransferDTO> result = mapper.mapAsList(list.getRecords(), RuleTransferDTO.class);
        consummateRuleTransfer(result, true, true);
        return result;
    }

    public RuleTransfer getByOid(UUID ruleTransferOid) {
        return selectOne(new EntityWrapper<RuleTransfer>()
                .eq("rule_transfer_oid", ruleTransferOid));

    }

    /**
     * get one RuleTransfer by id.
     *
     * @return the entity
     */
    @Transactional(readOnly = true)
    public RuleTransferDTO getRuleTransfer(UUID ruleTransferOid) {
        Optional<RuleTransfer> opt = Optional.ofNullable(getByOid(ruleTransferOid));
        if (opt.isPresent()) {
            RuleTransferDTO ruleTransferDTO = mapper.map(opt.get(), RuleTransferDTO.class);
            consummateRuleTransfer(ruleTransferDTO, true, true);
            return ruleTransferDTO;
        }
        return null;
    }

    public int delete(UUID ruleTransferOid) {
        Optional<RuleTransfer> opt = Optional.ofNullable(getByOid(ruleTransferOid));
        if (opt.isPresent() && opt.get().getStatus().equals(RuleApprovalEnum.VALID.getId())) {
            RuleTransfer ruleTransfer = opt.get();
            ruleTransfer.setStatus(RuleApprovalEnum.DELETED.getId());
            insertOrUpdate(ruleTransfer);
            return 1;
        }
        return 0;
    }

    /**
     * 检查表单是否已使用
     *
     * @param ruleTransferDTO
     */
    private void checkForm(RuleTransferDTO ruleTransferDTO, UUID userOid) {
        List<ApprovalFormDTO> forms = getFormByDate(ruleTransferDTO.getStartDate(), ruleTransferDTO.getStartDate(), userOid);
        Map<UUID, ApprovalFormDTO> formOidMap = forms.stream().collect(Collectors.toMap((k) -> k.getFormOid(), (v) -> v));
        ruleTransferDTO.getFormOids().stream()
                .filter(formOid -> formOidMap.containsKey(formOid))
                .forEach(
                        formOid -> {
                            if (!CollectionUtils.isEmpty(formOidMap.get(formOid).getRuleTransfers())) {
                                throw new ValidationException(new ValidationError("ruleTransfer.save", "formOid already used , formOid : " + formOid));
                            }
                        }
                );
    }

    public RuleTransferDTO ctreateRuleTransfer(RuleTransferDTO ruleTransferDTO, UUID userOid) {
        if (StringUtils.isEmpty(ruleTransferDTO.getStartDate())) {
            throw new ValidationException(new ValidationError("ruleTransfer.save", "startDate is null"));
        }
        if (CollectionUtils.isEmpty(ruleTransferDTO.getFormOids())) {
            throw new ValidationException(new ValidationError("ruleTransfer.save", "formOids is null"));
        }
        //check form
        checkForm(ruleTransferDTO, userOid);


        ruleTransferDTO.setSourceOid(userOid);
        RuleTransfer ruleTransfer = mapper.map(ruleTransferDTO, RuleTransfer.class);
        ruleTransfer.setRuleTransferOid(UUID.randomUUID());
        ruleTransfer.setStatus(RuleApprovalEnum.VALID.getId());
        ruleTransfer.setStartDateTime(parseDate(ruleTransferDTO.getStartDate()));
        ruleTransfer.setEndDateTime(parseDate(ruleTransferDTO.getEndDate()));
        insert(ruleTransfer);
        for (UUID formOid : ruleTransferDTO.getFormOids()) {
            //创建条件
            RuleCondition ruleCondition = ruleConditionService.save(
                    RuleCondition.builder()
                            .batchCode(RuleConstants.RULE_BATCH_CODE_DEFAULT)
                            .typeNumber(RuleConstants.CONDITION_TYPE_FORM_TRANFORM)
                            .symbol(RuleConstants.SYMBOL_EQ)
                            .ruleValue(formOid.toString())
                            .build()
            );

            //审批链关联场景
            ruleConditionRelationService.save(
                    RuleConditionRelation.builder()
                            .ruleConditionOid(ruleCondition.getRuleConditionOid())
                            .entityType(RuleApprovalEnum.CONDITION_RELATION_TYPE_TRANSFER.getId())
                            .entityOid(ruleTransfer.getRuleTransferOid())
                            .build()
            );
        }

        return mapper.map(ruleTransfer, RuleTransferDTO.class);
    }

    public RuleTransferDTO update(RuleTransferDTO ruleTransferDTO, UUID userOid) {
        if (StringUtils.isEmpty(ruleTransferDTO.getRuleTransferOid())) {
            throw new ValidationException(new ValidationError("ruleTransfer.save", "ruleTransferOid is null"));
        }
        if (StringUtils.isEmpty(ruleTransferDTO.getSourceOid())) {
            throw new ValidationException(new ValidationError("ruleTransfer.save", "sourceOid is null"));
        }
        if (StringUtils.isEmpty(ruleTransferDTO.getStartDate())) {
            throw new ValidationException(new ValidationError("ruleTransfer.save", "startDate is null"));
        }
        if (CollectionUtils.isEmpty(ruleTransferDTO.getFormOids())) {
            throw new ValidationException(new ValidationError("ruleTransfer.save", "formOids is null"));
        }

        Optional<RuleTransfer> opt = Optional.ofNullable(getByOid(ruleTransferDTO.getRuleTransferOid()));

        if (opt.isPresent() && opt.get().getStatus().equals(RuleApprovalEnum.VALID.getId())) {
            RuleTransfer ruleTransfer = opt.get();
            if (!userOid.equals(ruleTransfer.getSourceOid())) {
                throw new ValidationException(new ValidationError("ruleTransfer.update", "user error"));
            }
            //delete ruleConditionRelation
            ruleConditionRelationService.deleteByEntityOid(ruleTransfer.getRuleTransferOid());
            //checkForm
            checkForm(ruleTransferDTO, userOid);

            //create ruleConditionRelation
            for (UUID formOid : ruleTransferDTO.getFormOids()) {
                //创建条件
                RuleCondition ruleCondition = ruleConditionService.save(
                        RuleCondition.builder()
                                .batchCode(RuleConstants.RULE_BATCH_CODE_DEFAULT)
                                .typeNumber(RuleConstants.CONDITION_TYPE_FORM_TRANFORM)
                                .symbol(RuleConstants.SYMBOL_EQ)
                                .ruleValue(formOid.toString())
                                .build()
                );

                //审批链关联场景
                ruleConditionRelationService.save(
                        RuleConditionRelation.builder()
                                .ruleConditionOid(ruleCondition.getRuleConditionOid())
                                .entityType(RuleApprovalEnum.CONDITION_RELATION_TYPE_TRANSFER.getId())
                                .entityOid(ruleTransfer.getRuleTransferOid())
                                .build()
                );
            }


            ruleTransfer.setSourceOid(ruleTransferDTO.getSourceOid());
            ruleTransfer.setTargetOid(ruleTransferDTO.getTargetOid());
            ruleTransfer.setRemark(ruleTransferDTO.getRemark());
            ruleTransfer.setStartDateTime(parseDate(ruleTransferDTO.getStartDate()));
            ruleTransfer.setEndDateTime(parseDate(ruleTransferDTO.getEndDate()));
            insert(ruleTransfer);
            return mapper.map(ruleTransfer, RuleTransferDTO.class);
        }
        return null;
    }

    public void consummateRuleTransfer(RuleTransferDTO ruleTransferDTO, boolean cascadeUserName, boolean cascadeForm) {
        if (ruleTransferDTO == null) {
            return;
        }
        consummateRuleTransfer(Arrays.asList(ruleTransferDTO), cascadeUserName, cascadeForm);
    }

    public void consummateRuleTransfer(List<RuleTransferDTO> ruleTransferDTOs, boolean cascadeUserName, boolean cascadeForm) {
        if (CollectionUtils.isEmpty(ruleTransferDTOs)) {
            return;
        }
        if (cascadeForm) {
            List<UUID> ruleTransferOids = ruleTransferDTOs.stream().map(RuleTransferDTO::getRuleTransferOid).collect(Collectors.toList());
            List<RuleConditionRelation> ruleConditionRelations = ruleConditionRelationService.findEntityOidIn(ruleTransferOids);
            if (CollectionUtils.isEmpty(ruleConditionRelations)) {
                return;
            }
            List<UUID> ruleConditionOids = ruleConditionRelations.stream().map(RuleConditionRelation::getRuleConditionOid).collect(Collectors.toList());
            Map<UUID, List<RuleConditionRelation>> ruleConditionRelationMap = ruleConditionRelations.stream().collect(Collectors.groupingBy(RuleConditionRelation::getEntityOid));

            List<RuleCondition> ruleConditions = ruleConditionService.findByRuleConditionOidIn(ruleConditionOids);
            Map<UUID, RuleCondition> ruleConditionOidMap = ruleConditions.stream().collect(Collectors.toMap((k) -> k.getRuleConditionOid(), (v) -> v));

            Map<UUID, List<UUID>> ruleTransferOidFormOidListMap = new HashMap<>();
            ruleConditionRelationMap.forEach((ruleTransferOid, ruleConditionRelationList) -> {
                ruleTransferOidFormOidListMap.put(ruleTransferOid,
                        ruleConditionRelationList.stream()
                                .filter(v -> ruleConditionOidMap.containsKey(v.getRuleConditionOid()))
                                .map(v -> UUID.fromString(ruleConditionOidMap.get(v.getRuleConditionOid()).getRuleValue()))
                                .collect(Collectors.toList())
                );
            });
            ruleTransferDTOs.stream()
                    .filter(ruleTransferDTO -> ruleTransferOidFormOidListMap.containsKey(ruleTransferDTO.getRuleTransferOid()))
                    .forEach(ruleTransferDTO -> {
                        ruleTransferDTO.setFormOids(ruleTransferOidFormOidListMap.get(ruleTransferDTO.getRuleTransferOid()));
                    });
        }


        if (cascadeUserName) {
            Set<UUID> userOids = new HashSet<>();
            ruleTransferDTOs.forEach(ruleTransferDTO -> {
                userOids.add(ruleTransferDTO.getSourceOid());
                userOids.add(ruleTransferDTO.getTargetOid());
            });
            //List<ManagedUserDTO> users=userService.findByUserOidsIn(userOids);

        }

        ruleTransferDTOs.stream()
                .forEach(ruleTransferDTO -> {
                    ruleTransferDTO.setStartDate(format(ruleTransferDTO.getStartDateTime()));
                    ruleTransferDTO.setEndDate(format(ruleTransferDTO.getEndDateTime()));
                    if (cascadeUserName) {
                        ruleTransferDTO.setSourceName("张三");
                        ruleTransferDTO.setTargetName("李四");
                    }
                });
    }

    public List<ApprovalFormDTO> getFormByDate(String startDate, String endDate, UUID userOid) {
        return getFormByDate(parseDate(startDate), parseDate(endDate), userOid);
    }


    public List<ApprovalFormDTO> getFormByDate(ZonedDateTime startDateTime, ZonedDateTime endDateTime, UUID userOid) {
        List<ApprovalFormDTO> approvalFormDTOS = ruleService.getAllCustomForm(userOid, false);
        List<RuleTransfer> ruleTransfers = selectList(new EntityWrapper<RuleTransfer>()
                .eq(userOid != null, "source_oid", userOid)
                .ge("end_date_time", startDateTime)
                .le("start_date_time", endDateTime)
                .eq("status", RuleApprovalEnum.VALID.getId())
        );

        if (!CollectionUtils.isEmpty(ruleTransfers)) {
            Map<UUID, List<RuleTransferDTO>> formOidRuleTransferOidsMap = new HashMap<>();
            List<UUID> ruleTransferOids = ruleTransfers.stream().map(RuleTransfer::getRuleTransferOid).collect(Collectors.toList());
            Map<UUID, RuleTransferDTO> ruleTransferOidMap = ruleTransfers.stream().collect(
                    Collectors.toMap((k) -> k.getRuleTransferOid(), (v) -> mapper.map(v, RuleTransferDTO.class))
            );
            List<RuleConditionRelation> ruleConditionRelations = ruleConditionRelationService.findEntityOidIn(ruleTransferOids);
            if (CollectionUtils.isEmpty(ruleConditionRelations)) {
                return approvalFormDTOS;
            }
            List<UUID> ruleConditionOids = ruleConditionRelations.stream().map(RuleConditionRelation::getRuleConditionOid).collect(Collectors.toList());
            //Map<UUID, List<RuleConditionRelation>> ruleConditionRelationMap = ruleConditionRelations.stream().collect(Collectors.groupingBy(RuleConditionRelation::getEntityOid));

            List<RuleCondition> ruleConditions = ruleConditionService.findByRuleConditionOidIn(ruleConditionOids);
            if (CollectionUtils.isEmpty(ruleConditions)) {
                return approvalFormDTOS;
            }
            Map<UUID, RuleCondition> ruleConditionOidMap = ruleConditions.stream().collect(Collectors.toMap((k) -> k.getRuleConditionOid(), (v) -> v));

            ruleConditionRelations.stream().forEach(ruleConditionRelation -> {
                        UUID formOid = UUID.fromString(ruleConditionOidMap.get(ruleConditionRelation.getRuleConditionOid()).getRuleValue());
                        UUID ruleTransferOid = ruleConditionRelation.getEntityOid();
                        if (formOidRuleTransferOidsMap.containsKey(formOid)) {
                            formOidRuleTransferOidsMap.get(formOid).add(ruleTransferOidMap.get(ruleTransferOid));
                        } else {
                            List<RuleTransferDTO> ruleTransferOidList = new ArrayList();
                            ruleTransferOidList.add(ruleTransferOidMap.get(ruleTransferOid));
                            formOidRuleTransferOidsMap.put(formOid, ruleTransferOidList);
                        }
                    }
            );

            approvalFormDTOS.stream()
                    //.filter(approvalFormDTO -> ruleConditionRelations.contains(approvalFormDTO.getFormOid()))
                    .forEach(customFormDTO -> {
                        if (formOidRuleTransferOidsMap.containsKey(customFormDTO.getFormOid())) {
                            customFormDTO.setRuleTransfers(formOidRuleTransferOidsMap.get(customFormDTO.getFormOid()));
                        }
                    });

        }
        return approvalFormDTOS;
    }

    public int enabledRuleTransfer(UUID ruleTransferOid, UUID userOid) {
        Optional<RuleTransfer> opt = Optional.ofNullable(getByOid(ruleTransferOid));
        if (opt.isPresent() && opt.get().getStatus().equals(RuleApprovalEnum.INVALID.getId())) {
            // check form
            RuleTransferDTO ruleTransferDTO = mapper.map(opt.get(), RuleTransferDTO.class);
            consummateRuleTransfer(ruleTransferDTO, false, true);
            checkForm(ruleTransferDTO, userOid);

            RuleTransfer ruleTransfer = opt.get();
            ruleTransfer.setStatus(RuleApprovalEnum.VALID.getId());
            insertOrUpdate(ruleTransfer);
            return 1;
        }
        return 0;
    }

    public int disabledRuleTransfer(UUID ruleTransferOid, UUID userOid) {
        Optional<RuleTransfer> opt = Optional.ofNullable(getByOid(ruleTransferOid));
        if (opt.isPresent() && opt.get().getStatus().equals(RuleApprovalEnum.VALID.getId())) {
            RuleTransfer ruleTransfer = opt.get();
            ruleTransfer.setStatus(RuleApprovalEnum.INVALID.getId());
            insertOrUpdate(ruleTransfer);
            return 1;
        }
        return 0;
    }

    private ZonedDateTime parseDate(String date) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        try {
            return ZonedDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            throw new ValidationException(new ValidationError("ruleTransfer", "format date error " + e.getMessage() + ", date:" + date));
        }
    }


    private String format(ZonedDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
