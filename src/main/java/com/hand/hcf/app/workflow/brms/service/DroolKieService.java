package com.hand.hcf.app.workflow.brms.service;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.hand.hcf.app.workflow.brms.domain.DroolsRuleDetail;
import com.hand.hcf.app.workflow.brms.domain.DroolsRuleDetailResult;
import com.hand.hcf.app.workflow.brms.dto.CustomMessagesDTO;
import com.hand.hcf.app.workflow.brms.dto.DroolsRuleApprovalNodeDTO;
import com.hand.hcf.app.workflow.brms.enums.FieldType;
import com.hand.hcf.app.workflow.brms.impl.RuleManagerImpl;
import com.hand.hcf.app.workflow.brms.impl.TrackingAgendaEventListener;
import com.hand.hcf.app.workflow.constant.RuleConstants;
import com.hand.hcf.app.workflow.dto.FormValueDTO;
import com.hand.hcf.app.core.util.AsciiUtil;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class DroolKieService {

    @Autowired
    private RuleManagerImpl ruleManager;

    private Logger logger = LoggerFactory.getLogger(DroolKieService.class);

    private void addGlobalObjectsToSession(StatelessKieSession kieSession, CustomMessagesDTO messages,
                                           DroolsRuleDetail droolsRuleDetail) {
        kieSession.setGlobal("messages", messages);
        kieSession.setGlobal("logger", logger);
        kieSession.setGlobal("droolsRuleDetail", droolsRuleDetail);
    }

    private CustomMessagesDTO doExecute(BatchExecutionCommandImpl batchCommand, String drl, CustomMessagesDTO customMessages, DroolsRuleDetail droolsRuleDetail) {
        StatelessKieSession statelessKieSession = ruleManager.createKieSessionFromDRL(drl);
        addGlobalObjectsToSession(statelessKieSession, customMessages, droolsRuleDetail);
        AgendaEventListener agendaEventListener = new TrackingAgendaEventListener();
        statelessKieSession.addEventListener(agendaEventListener);
        statelessKieSession.execute(batchCommand);
        if (!customMessages.hasRuleFired()) {
            logger.info("Rule_Not_Fired");
        } else {
            logger.info("Rule_Fired");
        }
        logger.info("execute_result: " + customMessages);
        return customMessages;
    }


    private CustomMessagesDTO doBatchExecute(BatchExecutionCommandImpl myBach, String drl, DroolsRuleDetail droolsRuleDetail) {
        CustomMessagesDTO customMessages = new CustomMessagesDTO();
        try {
            StatelessKieSession statelessKieSession = ruleManager.createKieSessionFromDRL(drl);
            addGlobalObjectsToSession(statelessKieSession, customMessages, droolsRuleDetail);
            AgendaEventListener agendaEventListener = new TrackingAgendaEventListener();
            statelessKieSession.addEventListener(agendaEventListener);

            ExecutionResults execute = statelessKieSession.execute(myBach);
            if (!customMessages.hasRuleFired()) {
                logger.info("Rule_Not_Fired");
            } else {
                logger.info("Rule_Fired");
            }
            logger.info("execute_result: " + customMessages);
        } catch (Exception e) {
            logger.error("error occurs executing rules: " + e);
        }
        return customMessages;
    }

    public DroolsRuleDetailResult doBatchExecuteV2(DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO, List<FormValueDTO> customFormValueDTOS, List<DroolsRuleDetail> filterDroolsRuleDetails, UUID ruleApprovalNodeOid) {
        Retryer<DroolsRuleDetailResult> retryer = RetryerBuilder.<DroolsRuleDetailResult>newBuilder()
                .retryIfExceptionOfType(RuntimeException.class)
                .withStopStrategy(StopStrategies.stopAfterAttempt(1))
                .build();
        try {
            return retryer.call(() -> droolExecute(droolsRuleApprovalNodeDTO, customFormValueDTOS, filterDroolsRuleDetails, ruleApprovalNodeOid));
        } catch (Exception e) {
            logger.error("执行drool引擎 fail", e);
            e.printStackTrace();
        }
        return new DroolsRuleDetailResult();
    }

    private DroolsRuleDetailResult droolExecute(DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO, List<FormValueDTO> formValueDTOS, List<DroolsRuleDetail> filterDroolsRuleDetails, UUID ruleApprovalNodeOid) {
        //找到对应的drool条件
        List<String> drlList = filterDroolsRuleDetails.stream().map(c -> c.getDroolsRuleDetailValue()).collect(Collectors.toList());

        DroolsRuleDetailResult droolsRuleDetailResult = new DroolsRuleDetailResult();
        droolsRuleDetailResult.setDroolsRuleDetailResultOid(UUID.randomUUID());
        droolsRuleDetailResult.setDroolsRuleDetailResultFlg(false);
        BatchExecutionCommandImpl batchExecutionCommand = new BatchExecutionCommandImpl();

        //过滤没有用到的FormValue，只匹配规则中的FormValue
        List<FormValueDTO> filterdCustomFormValue = formValueDTOS.stream().filter(e -> (filterDroolsRuleDetails.stream().filter(
                c -> c.getRuleCondition().getRuleField().equals(e.getFieldOid().toString())
                        && (c.getRuleCondition().getRemark() != null)
        ).count() == 1)).collect(Collectors.toList());

        filterdCustomFormValue.forEach((FormValueDTO c) -> {
            try {
                if (null != c.getValue()) {
                    String customFormValue = new String(AsciiUtil.sbc2dbcCase(c.getValue()).getBytes(), "UTF-8");
                    c.setValue(customFormValue);
                }

                if (c.getFieldType().getId().equals(FieldType.LIST.getId()) && c.getValue() != null) {
                    Arrays.stream(c.getValue().split(RuleConstants.RULE_CONDITION_VALUE_SPLIT)).forEach(a -> {
                        FormValueDTO copyOfFormValueDTO = new FormValueDTO();
                        copyOfFormValueDTO.setValue(a);
                        copyOfFormValueDTO.setFieldOid(c.getFieldOid());
                        batchExecutionCommand.addCommand(new InsertObjectCommand(copyOfFormValueDTO));
                    });
                }
                batchExecutionCommand.addCommand(new InsertObjectCommand(c));

            } catch (Exception e) {
                e.printStackTrace();
                logger.error("batchExecutionCommand fail:", e);
            }
        });

        List<DroolsRuleDetail> successFlg = new ArrayList<>();
        droolsRuleApprovalNodeDTO.setDrlContentList(new ArrayList<String>());

        filterDroolsRuleDetails.forEach(d -> {
            try {
                String utf8DrlContent = new String(AsciiUtil.sbc2dbcCase(d.getDroolsRuleDetailValue()).getBytes(), "UTF-8");
//                droolsRuleApprovalNodeDTO.getDrlContentList().add(utf8DrlContent);
                /*
                 * 不包含现在在List情况下会有问题，现单节点处理， 满足所有才返回
                 */
                DrlParser parser = new DrlParser();
                String drl = d.getDroolsRuleDetailValue();
                PackageDescr p = null;
                p = parser.parse(false, drl);
                PatternDescr pd = (PatternDescr) p.getRules().get(0).getLhs().getDescrs().get(0);
                if (pd.getConstraint().getDescrs().get(0).getText().contains("!=") || pd.getConstraint().getDescrs().get(0).getText().contains("not contains")) {
                    boolean doSingleResult = doSingleRuleExecutor(filterdCustomFormValue, drl, d);
                    if (doSingleResult) {
                        successFlg.add(d);
                    } else {
                        //处理执行失败的规则
                    }
                } else {
                    StatelessKieSession statelessKieSession = ruleManager.createKieSessionFromDRL(utf8DrlContent);
                    CustomMessagesDTO customMessages = new CustomMessagesDTO();
                    DroolsRuleDetail droolsRuleDetail = new DroolsRuleDetail();
                    addGlobalObjectsToSession(statelessKieSession, customMessages, droolsRuleDetail);
                    TrackingAgendaEventListener agendaEventListener = new TrackingAgendaEventListener();
                    statelessKieSession.addEventListener(agendaEventListener);
                    ExecutionResults execute = statelessKieSession.execute(batchExecutionCommand);
                    String logOid = UUID.randomUUID().toString();
                    if (agendaEventListener.getMatchList().size() == 0) {
                        logger.info("DroolsRuleDetail Oid :{}, batchExecutionCommand :{}, utf8DrlContent :{}, execute result -> failed", droolsRuleDetail.getDroolsRuleDetailOid(), batchExecutionCommand.toString(), utf8DrlContent);
                        logger.info("日志Oid {}， 执行规则失败, 节点Oid: {}", logOid, ruleApprovalNodeOid);
//                      throw new RuleFailedException();
                    } else {
                        logger.info("日志Oid {}， 执行规则成功, 节点Oid: {}", logOid, ruleApprovalNodeOid);
                        successFlg.add(d);
                    }

                }
            } catch (DroolsParserException e) {
                logger.error("执行规则报错", e);
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                logger.error("", e);
                e.printStackTrace();
            }
        });

        droolsRuleDetailResult.setDroolsRuleDetailResultFlg(successFlg.size() == drlList.size() ? true : false);
        return droolsRuleDetailResult;
    }

    private boolean doSingleRuleExecutor(List<FormValueDTO> formValueDTOS, String drl, DroolsRuleDetail droolsRuleDetail) {

        AtomicBoolean resultflg = new AtomicBoolean(true);
        try {
            formValueDTOS.stream().filter(c -> (c.getFieldOid().toString().equals(droolsRuleDetail.getRuleCondition().getRuleField()))).collect(Collectors.toList()).forEach(d -> {
                //List情况下要全部满足
                if (d.getFieldType().equals(FieldType.LIST)) {
                    Arrays.stream(d.getValue().split(RuleConstants.RULE_CONDITION_VALUE_SPLIT)).forEach(f -> {
                        FormValueDTO newFormValueDTO = new FormValueDTO();
                        BeanUtils.copyProperties(d, newFormValueDTO);
                        newFormValueDTO.setValue(f);
                        boolean flg = execute(newFormValueDTO, drl, droolsRuleDetail);
                        if (!flg) {
                            resultflg.set(flg);
                        }
                    });
                } else {
                    resultflg.set(execute(d, drl, droolsRuleDetail));
                }
            });
        } catch (Exception e) {
            logger.error("Exception: {}", e);
        }

        return resultflg.get();
    }

    private boolean execute(FormValueDTO formValueDTO, String drl, DroolsRuleDetail droolsRuleDetail) {
        StatelessKieSession statelessKieSession = ruleManager.createKieSessionFromDRL(drl);
        CustomMessagesDTO myMessage = new CustomMessagesDTO();
        TrackingAgendaEventListener agendaEventListener = new TrackingAgendaEventListener();
        statelessKieSession.addEventListener(agendaEventListener);
        addGlobalObjectsToSession(statelessKieSession, myMessage, droolsRuleDetail);
        statelessKieSession.execute(new InsertObjectCommand(formValueDTO));
        if (agendaEventListener.getMatchList().size() > 0) {
            return true;
        }
        logger.info("DroolsRuleDetail Oid :{}, CustomFormValue :{}, drl :{}, execute result -> failed", droolsRuleDetail.getDroolsRuleDetailOid(), formValueDTO.toString(), drl);
        return false;
    }
}
