package com.hand.hcf.app.workflow.brms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.hand.hcf.app.workflow.brms.domain.*;
import com.hand.hcf.app.workflow.brms.dto.*;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.brms.impl.RuleManagerImpl;
import com.hand.hcf.app.workflow.brms.model.Account;
import com.hand.hcf.app.workflow.brms.persistence.DroolsRuleDetailMapper;
import com.hand.hcf.app.workflow.brms.persistence.DroolsRuleDetailResultMapper;
import com.hand.hcf.app.workflow.brms.util.event.Event;
import com.hand.hcf.app.workflow.brms.util.event.RuleApproverEvent;
import com.hand.hcf.app.workflow.brms.util.rule.RuleGenerator;
import com.hand.hcf.app.workflow.dto.FormValueDTO;
import com.hand.hcf.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.core.exception.core.ValidationError;
import com.hand.hcf.core.exception.core.ValidationException;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.drools.template.ObjectDataCompiler;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DroolsService {
    private static final String GROUPS = "com.hand.hcf.app";
    private static final String ARTIFACTID = "brms";
    private static final String VERSION = "0.0.1-SNAPSHOT";
    private static final String PACKAGE = "package com.hand.hcf.app.brms;\n ";
    private static final String IMPORTS = "import Account; \n";
    private static final String GLOBALS =
        "global org.slf4j.Logger logger;\n" +
            "global CustomMessagesDTO messages; \n\n";
    private static final String DYNAMICRULE = "rule \"Dynamic Rule\"\n" +
        "when\n" +
        " account:Account(name !=null)\n" +
        "then\n" +
        " logger.info(\"Your name is \"+account.getName()); \n messages.addMessage(\" Your Name\", \"You Name is: " + "account.getName()" + "!!!\" );" + "end \n";
    private static final String RULE1 = "rule \"Your Dynamic Rule\" \n "
        + " when \n" + "    fact : AcmeFactA (something == null) \n"
        + " then \n"
        + "    rulesLogger.info(\"Something is unset, will set it.\"); \n"
        + "    fact.setSomething(\"Nothing\");\n" + "end \n";
    private static final String RULE2 = "rule \"Your Other Dynamic Rule\" \n "
        + " when \n" + "    fact : AcmeFactA (counter == 0) \n"
        + " then \n"
        + "    rulesLogger.info(\"Counter is 0, will increase \"); \n"
        + "    fact.setCounter(1);\n" + "end \n";
    private static KieServices kieServices = KieServices.Factory.get();
    private final Logger log = LoggerFactory.getLogger(RuleService.class);
    //    @Autowired
//    KieSession kieSession;
    @Autowired
    StatelessKieSession statelessKieSession;


    @Inject
    RuleApproverService ruleApproverService;

    @Inject
    RuleApprovalNodeService ruleApprovalNodeService;

    // the dynamic rule to inject must be a complete DRL resource incl. package
    // and imports
    @Inject
    RuleConditionRelationService ruleConditionRelationService;

    @Inject
    DroolsRuleDetailService droolsRuleDetailService;

    @Autowired
    ObjectMapper objectMapper;

    @Inject
    private DroolsRuleDetailResultMapper droolsRuleDetailResultMapper;
    ;
    @Inject
    private DroolsRuleDetailMapper droolsRuleDetailMapper;
    private String droolsTemplateName = "rule-template.drt";
    @Inject
    private RuleManagerImpl ruleManager;

    @Autowired
    private DroolKieService droolKieService;

    @Autowired
    MapperFacade mapper;

    private Logger logger = LoggerFactory.getLogger(DroolsService.class);

    private void addGlobalObjectsToSession(StatelessKieSession statelessKieSession, CustomMessagesDTO messages) {
        statelessKieSession.setGlobal("messages", messages);
        statelessKieSession.setGlobal("logger", logger);
    }

    private void addGlobalObjectsToSession(StatelessKieSession kieSession, CustomMessagesDTO messages,
                                           DroolsRuleDetail droolsRuleDetail) {
        kieSession.setGlobal("messages", messages);
        kieSession.setGlobal("logger", logger);
        kieSession.setGlobal("droolsRuleDetail", droolsRuleDetail);
    }


    /**
     * simple test to check if the static rules can get accessed. As some kind
     * of regression test for the default rules execution.
     */
    public CustomMessagesDTO simpleRuleFunctionTest(Account account) {
        CustomMessagesDTO messages = new CustomMessagesDTO();
        addGlobalObjectsToSession(statelessKieSession, messages);
        List<ExecutableCommand<?>> commandList = new ArrayList<ExecutableCommand<?>>();
        commandList.add(new InsertObjectCommand(account));
        BatchExecutionCommand batchCommand = new BatchExecutionCommandImpl(commandList);
        statelessKieSession.execute(batchCommand);
        if (!messages.hasRuleFired()) {
            messages.addMessage("Success", "Account Validation Successfull!!!");
        }
        System.out.println(messages);
        return messages;
    }

    public CustomMessagesDTO dynamicRuleFunctionTest(Account account) {
        // in a real business scenario you would clone the facts first to see on
        // how the different rule set behave.
        // short cut here: create multiple facts with the same content.
        // test production rules first
        List<ExecutableCommand<?>> commandList = new ArrayList<ExecutableCommand<?>>();
        commandList.add(new InsertObjectCommand(account));
        CustomMessagesDTO messages = new CustomMessagesDTO();
        addGlobalObjectsToSession(statelessKieSession, messages);
        BatchExecutionCommand batchCommand = new BatchExecutionCommandImpl(
            commandList);
        statelessKieSession.execute(batchCommand);
        if (!messages.hasRuleFired()) {
            messages.addMessage("Success", "Account Validation Successfull!!!");
        }
        return messages;
    }

    /**
     * 调用规则引擎
     *
     * @param droolsRuleApprovalNodeDTO
     * @param userOid                  not used
     * @return
     */
    public RuleApprovalNodeDTO invokeDroolsRuleForApprovalNode(DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO, UUID userOid) {
        //Find the ruleApprovalNode from ruleApprovalChain
        RuleApprovalNodeDTO ruleApprovalNodeDTO = new RuleApprovalNodeDTO();
        UUID ruleApprovalNodeOid = droolsRuleApprovalNodeDTO.getRuleApprovalNodeOid();
        RuleApprovalNode ruleApprovalNode = ruleApprovalNodeService.getRuleApprovalNode(ruleApprovalNodeOid);
        // 调试暂时去掉开始
        log.info("invokeDroolsRuleForApprovalNode->ruleApprovalNode：{}", ruleApprovalNode);
        // 结束
        if (ruleApprovalNode == null) {
            throw new ObjectNotFoundException(RuleApprovalNode.class, ruleApprovalNodeOid);
        }
        switch (RuleApprovalEnum.parse(ruleApprovalNode.getTypeNumber())) {
            case NODE_TYPE_APPROVAL:
            case NODE_TYPE_NOTICE:
            case NODE_TYPE_PRINT:
            case NODE_TYPE_EED:
                ruleApprovalNodeDTO = invokeDroolsRuleForNormalApprovalNode(droolsRuleApprovalNodeDTO);
                break;
            case NODE_TYPE_ROBOT:
                ruleApprovalNodeDTO = invokeDroolsRuleForRobotApprovalNode(droolsRuleApprovalNodeDTO);
                break;
        }
        return ruleApprovalNodeDTO;
    }


    /**
     * 执行机器人审批规则
     *
     * @param droolsRuleApprovalNodeDTO
     * @return
     */
    public RuleApprovalNodeDTO invokeDroolsRuleForRobotApprovalNode(DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO) {
        //Find the ruleApprovalNode from ruleApprovalChain
        UUID ruleApprovalNodeOid = droolsRuleApprovalNodeDTO.getRuleApprovalNodeOid();
        RuleApprovalNode ruleApprovalNode = ruleApprovalNodeService.getRuleApprovalNode(ruleApprovalNodeOid);
        if (ruleApprovalNode == null) {
            throw new ObjectNotFoundException(RuleApprovalNode.class, ruleApprovalNodeOid);
        }
        //Get the entire approverList from that node
        List<RuleApprover> ruleApprovers = ruleApproverService.findByRuleApprovalNodeOid(droolsRuleApprovalNodeDTO.getRuleApprovalNodeOid());
        // 调试暂时去掉开始
        log.info("临时调试信息->invokeDroolsRuleForRobotApprovalNode->ruleApprovers：{}", ruleApprovers);
        // 结束
        RuleApprovalNodeDTO resultDTO = new RuleApprovalNodeDTO();
        resultDTO.setRuleApprovalNodeOid(ruleApprovalNodeOid);
        resultDTO.setTypeNumber(ruleApprovalNode.getTypeNumber());
        resultDTO.setApprovalActions(ruleApprovalNode.getApprovalActions());
        resultDTO.setName(ruleApprovalNode.getName());
        resultDTO.setCode(ruleApprovalNode.getCode());
        resultDTO.setCountersignRule(ruleApprovalNode.getCountersignRule());

        //resultDTO.setCreatedDate(ruleApprovalNode.getCreatedDate());
        resultDTO.setNullableRule(ruleApprovalNode.getNullableRule());
        resultDTO.setRemark(ruleApprovalNode.getRemark());
        resultDTO.setRepeatRule(ruleApprovalNode.getRepeatRule());
        resultDTO.setRuleApprovalChainOid(ruleApprovalNode.getRuleApprovalChainOid());
        resultDTO.setSelfApprovalRule(ruleApprovalNode.getSelfApprovalRule());
        resultDTO.setSequenceNumber(ruleApprovalNode.getSequenceNumber());
        resultDTO.setStatus(ruleApprovalNode.getStatus());
        resultDTO.setComments(ruleApprovalNode.getComments());
        if (ruleApprovers.size() == 0) {
            return resultDTO;
        }
        return processDroolDetail(ruleApprovers, resultDTO, droolsRuleApprovalNodeDTO, ruleApprovalNodeOid);
    }


    /**
     * @param droolsRuleApprovalNodeDTO
     * @return
     */
    public RuleApprovalNodeDTO invokeDroolsRuleForNormalApprovalNode(DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO) {
        //Find the ruleApprovalNode from ruleApprovalChain
        UUID ruleApprovalNodeOid = droolsRuleApprovalNodeDTO.getRuleApprovalNodeOid();
        RuleApprovalNode ruleApprovalNode = ruleApprovalNodeService.getRuleApprovalNode(ruleApprovalNodeOid);
        if (ruleApprovalNode == null) {
            throw new ObjectNotFoundException(RuleApprovalNode.class, ruleApprovalNodeOid);
        }
        //Get the entire approverList from that node
        List<RuleApprover> ruleApprovers = ruleApproverService.findByRuleApprovalNodeOid(droolsRuleApprovalNodeDTO.getRuleApprovalNodeOid());
        RuleApprovalNodeDTO ruleApprovalNodeDTO = new RuleApprovalNodeDTO();
        ruleApprovalNodeDTO.setRuleApprovalNodeOid(ruleApprovalNodeOid);
        ruleApprovalNodeDTO.setTypeNumber(ruleApprovalNode.getTypeNumber());
        ruleApprovalNodeDTO.setName(ruleApprovalNode.getName());
        ruleApprovalNodeDTO.setCode(ruleApprovalNode.getCode());
        ruleApprovalNodeDTO.setCountersignRule(ruleApprovalNode.getCountersignRule());
        ruleApprovalNodeDTO.setEntityOid(droolsRuleApprovalNodeDTO.getEntityOid());

        ruleApprovalNodeDTO.setNullableRule(ruleApprovalNode.getNullableRule());
        ruleApprovalNodeDTO.setRemark(ruleApprovalNode.getRemark());
        ruleApprovalNodeDTO.setRepeatRule(ruleApprovalNode.getRepeatRule());
        ruleApprovalNodeDTO.setRuleApprovalChainOid(ruleApprovalNode.getRuleApprovalChainOid());
        ruleApprovalNodeDTO.setSelfApprovalRule(ruleApprovalNode.getSelfApprovalRule());
        ruleApprovalNodeDTO.setSequenceNumber(ruleApprovalNode.getSequenceNumber());
        ruleApprovalNodeDTO.setStatus(ruleApprovalNode.getStatus());
        ruleApprovalNodeDTO.setPrintFlag(ruleApprovalNode.getPrintFlag());
        ruleApprovalNodeDTO.setInvoiceAllowUpdateType(ruleApprovalNode.getInvoiceAllowUpdateType());

        try {
            if (ruleApprovalNode.getNotifyInfo() != null) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                ruleApprovalNodeDTO.setNotifyInfo(mapper.readValue(ruleApprovalNode.getNotifyInfo(), NotifyInfo.class));
            }
        } catch (Exception e) {
            throw new ValidationException(new ValidationError("approvalRule.getRuleApprovalNode", "反序列化失败"));
        }
        if (ruleApprovers.size() == 0) {
            return ruleApprovalNodeDTO;
        }
        return processDroolDetail(ruleApprovers, ruleApprovalNodeDTO, droolsRuleApprovalNodeDTO, ruleApprovalNodeOid);
    }

    private RuleApprovalNodeDTO processDroolDetail(List<RuleApprover> ruleApprovers, RuleApprovalNodeDTO ruleApprovalNodeDTO, DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO, UUID ruleApprovalNodeOid) {
        try {
            for (RuleApprover ruleApprover : ruleApprovers) {
                /**
                 *
                 *   Get the rule condition under the approver list
                 *   The logic is : if the entire rule conditions from that rule approver has passed, then return the rule approver Oid
                 */
                List<RuleConditionRelation> ruleConditionRelationList = ruleConditionRelationService.findEntityOid(ruleApprover.getRuleApproverOid());
                logger.info("ruleApproverOid is: " + ruleApprover.getRuleApproverOid());

                if (CollectionUtils.isEmpty(ruleConditionRelationList)) {
                    ruleApprovalNodeDTO.getRuleApprovers().add(mapper.map(ruleApprover, RuleApproverDTO.class));
                } else {
                    List<DroolsRuleDetail> droolsRuleDetailList =
                        droolsRuleDetailService.findByApprover(ruleApprover.getRuleApproverOid());

                     /*
                        区分审批者的多个审批条件组，即是batchCode
                      */
                    Map<Long, List<RuleCondition>> collect = droolsRuleDetailList.stream().map(c -> {
                        return c.getRuleCondition();
                    })
                        .collect(Collectors.toList())
                        .stream()
                        .collect(Collectors.groupingBy(RuleCondition::getBatchCode));

                    collect.forEach((a, b) -> {
                        List<DroolsRuleDetail> filterDroolsRuleDetails = droolsRuleDetailList.stream().filter(h -> b.contains(h.getRuleCondition())).collect(Collectors.toList());

                        DroolsRuleDetailResult droolsRuleDetailResult = droolKieService.doBatchExecuteV2(droolsRuleApprovalNodeDTO,
                            droolsRuleApprovalNodeDTO.getFormValues().stream().map(c -> {
                                return c;
                            }).collect(Collectors.toList()),
                            filterDroolsRuleDetails,
                            ruleApprovalNodeOid
                        );

                        //默认，当前审批人所有条件都满足返回
                        if (null != droolsRuleDetailResult && null != droolsRuleDetailResult.getDroolsRuleDetailResultFlg() && droolsRuleDetailResult.getDroolsRuleDetailResultFlg()) {
                            ruleApprovalNodeDTO.getRuleApprovers().add(mapper.map(ruleApprover, RuleApproverDTO.class));
                        } else {
                            droolsRuleDetailResult.setDroolsRuleDetailResultFlg(false);
                        }

                        droolsRuleDetailResult.setDroolsRuleDetailResultOid(UUID.randomUUID());
                        droolsRuleDetailResultMapper.insert(droolsRuleDetailResult);
                        ruleApprovalNodeDTO.getDroolsBatchCodeRuleResultMap().put(a, droolsRuleDetailResult);
                    });
                }
            }
            return ruleApprovalNodeDTO;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Drools Service Executing Error: ", e);
            return null;
        }
    }


    private boolean doBatchExecute(StatelessKieSession statelessKieSession, List<ExecutableCommand<?>> commandList, String drl) {
        boolean resultflg = false;
        try {
            for (ExecutableCommand<?> insertObjectCommand : commandList) {
                DrlParser parser = new DrlParser();
                PackageDescr p = null;
                p = parser.parse(false, drl);
                PatternDescr pd = (PatternDescr) p.getRules().get(0).getLhs().getDescrs().get(0);
                CustomMessagesDTO myMessage = new CustomMessagesDTO();
                StatelessKieSession statelessKieSession1 = ruleManager.createKieSessionFromDRL(drl);
                addGlobalObjectsToSession(statelessKieSession1, myMessage);
                statelessKieSession1.execute(insertObjectCommand);

//                doExecuteRule(statelessKieSession, insertObjectCommand, messages);

                if (pd.getConstraint().getDescrs().get(0).getText().contains("!=") || pd.getConstraint().getDescrs().get(0).getText().contains("not contains")) {
                    //mean not equals 6110
                    if (myMessage.hasRuleFired()) {
                        resultflg = true;
                    } else {
                        return false;
                    }
                } else {
                    if (myMessage.hasRuleFired()) {
                        return true;
                    }
                }
            }
        } catch (DroolsParserException e) {
            logger.error("DroolsParserException: {}", e);
        } catch (Exception e) {
            logger.error("Exception: {}", e);
        }
        return resultflg;
    }


    public DroolsRuleDetail save(DroolsRuleDetail droolsRuleDetail,
                                 RuleApprover ruleApprover,
                                 RuleConditionDTO ruleConditionDTO) {
        // added by mh.z 20190301 因为调用createDrl生成RuleGenerator会用到RuleCondition.ruleValue值（当前值不是最新），
        // 所以会导致生成的brms表达式错误。
        RuleCondition ruleCondition = droolsRuleDetail.getRuleCondition();
        if (ruleCondition != null) {
            ruleCondition.setRuleValue(ruleConditionDTO.getValue());
        }
        // END added by mh.z

        RuleGenerator rule = createDrl(droolsRuleDetail.getRuleCondition(), ruleApprover, ruleConditionDTO);
        droolsRuleDetail.setDroolsRuleDetailValue(rule.getRuleDrl());
        droolsRuleDetail.setExpectedResultMessage("");
        if(droolsRuleDetail.getRuleCondition() != null){
            droolsRuleDetail.setRuleConditionId(droolsRuleDetail.getRuleCondition().getId());
        }
        if(droolsRuleDetail.getId() == null){
            droolsRuleDetailMapper.insert(droolsRuleDetail);
        }else{
            droolsRuleDetailMapper.updateById(droolsRuleDetail);
        }

        return droolsRuleDetail;
    }

    private String applyRuleTemplate(Event event, RuleGenerator rule) throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        ObjectDataCompiler objectDataCompiler = new ObjectDataCompiler();
        RuleApproverEvent ruleApproverEvent = (RuleApproverEvent) event;
        data.put("rule", rule);
        data.put("eventType", event.getKey());
        data.put("ruleConditionOid", rule.getRuleCondition().getRuleConditionOid());
        data.put("formValueClass", FormValueDTO.class.getName());
        return objectDataCompiler.compile(Arrays.asList(data), Thread.currentThread().getContextClassLoader().getResourceAsStream(droolsTemplateName));
    }

    private RuleGenerator createDrl(RuleCondition ruleCondition, RuleApprover ruleApprover, RuleConditionDTO ruleConditionDTO) {
        RuleGenerator rule = new RuleGenerator();
        RuleApproverEvent processEvent = new RuleApproverEvent(ruleApprover);
        processEvent.setRuleConditionList(Arrays.asList(ruleCondition));
        processEvent.setRuleConditionDTO(ruleConditionDTO);
        processEvent.setRuleApprover(ruleApprover);
        rule.setEventType(RuleGenerator.eventType.ORDER);
        rule.setRuleCondition(ruleCondition);
        rule.setRuleConditionDTO(ruleConditionDTO);
        String drl = null;
        try {
            drl = applyRuleTemplate(processEvent, rule);
            ruleManager.verifyRuleFromDRL(drl);
            rule.setRuleDrl(drl);
        } catch (ValidationException validationException) {
            throw new RuntimeException("Create Rule Failed!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rule;
    }

    public DroolsRuleDetailResultDTO getDroolsRuleDetailResultByOid(UUID droolsRuleDetailResultOid) {
        if (Strings.isNullOrEmpty(droolsRuleDetailResultOid.toString())) {
        }

        DroolsRuleDetailResult droolsRuleDetailResult = droolsRuleDetailResultMapper.findByDroolsRuleDetailResultOid(droolsRuleDetailResultOid);
        if (droolsRuleDetailResult == null) {
            throw new ObjectNotFoundException(DroolsRuleDetailResult.class, droolsRuleDetailResultOid);
        }

        DroolsRuleDetail droolsRuleDetail = droolsRuleDetailResult.getDroolsRuleDetail();
        CustomMessageDTO customMessage = new CustomMessageDTO();
        customMessage.setMessage(droolsRuleDetailResult.getDroolsRuleDetailResultMessage());
        DroolsRuleDetailResultDTO droolsRuleDetailResultDTO = new DroolsRuleDetailResultDTO();
        CustomMessagesDTO customMessages = new CustomMessagesDTO();
        customMessages.addMessage(customMessage);
        log.info(new Gson().toJson(customMessage));
//        droolsRuleDetailResultDTO.getCustomMessagesList().add(customMessages);
        droolsRuleDetailResultDTO.setDroolsRuleDetailOid(droolsRuleDetail.getDroolsRuleDetailOid());
        droolsRuleDetailResultDTO.setDroolsRuleDetailValue(droolsRuleDetail.getDroolsRuleDetailValue());
        droolsRuleDetailResultDTO.setDroolsRuleExpectedMessage(droolsRuleDetail.getExpectedResultMessage());
        return droolsRuleDetailResultDTO;

    }

    public DroolsRuleDetail findByRuleConditionOid(RuleCondition ruleCondition) {
        DroolsRuleDetail droolsRuleDetail = droolsRuleDetailMapper.findByRuleConditionOid(ruleCondition.getRuleConditionOid());
        return droolsRuleDetail;
    }
}
