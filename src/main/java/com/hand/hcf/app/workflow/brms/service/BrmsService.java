package com.hand.hcf.app.workflow.brms.service;

import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.brms.dto.*;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.util.RespCode;
import com.hand.hcf.app.workflow.domain.ApprovalForm;
import com.hand.hcf.app.workflow.dto.FormValueDTO;
import com.hand.hcf.app.workflow.enums.ApprovalMode;
import com.hand.hcf.app.workflow.enums.FieldType;
import com.hand.hcf.app.workflow.service.ApprovalFormService;
import com.hand.hcf.core.exception.BizException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Nick on 17/3/13.
 */
@Service
public class BrmsService {

    private final Logger log = LoggerFactory.getLogger(BrmsService.class);
    private final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter YMDHMS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private final DateTimeFormatter YMDHMSS = DateTimeFormatter.ofPattern(("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

    @Autowired
    private RuleService ruleService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private ApprovalFormService approvalFormService;


    public RuleNextApproverResult getNextApprovalNode(List<FormValueDTO> customFormValueDTOs, UUID formOid, UUID lastApprovalNodeOid, UUID applicantOid, Integer entityType, UUID entityOid, Map<String, Object> entityData) {

        customFormValueDTOs = customFormValueDTOs.stream().filter(x -> x.getFieldOid() != null).collect(Collectors.toList());
        customFormValueDTOs.forEach(y -> {
            if (y.getFieldOid() != null &&
                    (y.getFieldType() == FieldType.DATE || y.getFieldType() == FieldType.DATETIME)
            ) {
                y.setValue(formatBrmsDate(y.getValue()));
            }
        });
        DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO = DroolsRuleApprovalNodeDTO.builder()
                .ruleApprovalNodeOid(lastApprovalNodeOid)
                .formValues(customFormValueDTOs)
                .applicantOid(applicantOid)
                .formOid(formOid)
                .entityType(entityType)
                .entityOid(entityOid)
                .entityData(entityData)
                .build();

        try {
            RuleNextApproverResult nextApprovalNode = ruleService.getNextApprovalNode(droolsRuleApprovalNodeDTO);
            return nextApprovalNode;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException(RespCode.SYS_APPROVAL_CHAIN_GET_ERROR, "invoke brms service error");
        }
    }

    /**
     * 统一转成日期格式
     *
     * @param str
     * @return
     */
    private String formatBrmsDate(String str) {
        try {
            if (StringUtils.isNotEmpty(str) && str.length() > 10) {
                if (str.length() == 20) {
                    ZonedDateTime d = ZonedDateTime.parse(str, YMDHMS).plusHours(8);
                    str = d.format(YMD);
                } else if (str.length() == 24) {
                    ZonedDateTime d = ZonedDateTime.parse(str, YMDHMSS).plusHours(8);
                    str = d.format(YMD);
                } else {
                    str = str.substring(0, 10);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return str;
    }

    /**
     * 创建审批链(更改审批模式)
     * 自动为新建表单改为自选审批流模式
     *
     * @param ruleApprovalChain
     * @return
     */

    @Transactional
    public RuleApprovalChainDTO changeApprovalMode(RuleApprovalChainDTO ruleApprovalChain) {
        return ruleService.createRuleApprovalChain(ruleApprovalChain);
    }

    /**
     * 新增结束节点
     *
     * @param ruleApprovalNode
     * @return
     */

    @Transactional
    public RuleApprovalNodeDTO addAnEndNode(RuleApprovalNodeDTO ruleApprovalNode) {
        RuleApprovalNodeDTO ruleApprovalNodeDTO = ruleService.createRuleApprovalNodeMapping(ruleApprovalNode, OrgInformationUtil.getCurrentUserOid());
        return ruleApprovalNodeDTO;
    }

    /**
     * 根据节点对象标志号获取审批节点详情
     *
     * @param ruleApprovalNodeOid
     * @return
     */
    public RuleApprovalNodeDTO getApprovalNode(UUID ruleApprovalNodeOid, UUID userOid) {

        RuleApprovalNodeDTO ruleApprovalNodeDTO = ruleService.getRuleApprovalNode(
                ruleApprovalNodeOid, userOid, false, false);
        return ruleApprovalNodeDTO;

    }


    /**
     * 判断是否启用规则审批
     * 若不启用按原规则走
     *
     * @param formOid
     * @return
     */
    public boolean isEnableRule( UUID formOid) {
        if (formOid == null) {
            return false;
        }

        ApprovalForm approvalForm = approvalFormService.getByOid(formOid);
        return approvalForm != null && approvalForm.getApprovalMode() != null;
    }

    /**
     * 判断是否启用自定义模式
     * 若不启用不开起加签
     *
     * @param companyOid
     * @param formOid
     * @return
     */
    public boolean isEnableCustomeApprovalMode(UUID companyOid, UUID formOid) {
        if (formOid == null) {
            return false;
        }
        boolean enableRule = baseClient.getApprovalRuleEnabled(companyOid);

        if (!enableRule) {
            return false;
        }
        //RuleApprovalChainDTO ruleApprovalChainDTO = this.getApprovalChain(formOid);
        ApprovalForm approvalForm = approvalFormService.getByOid(formOid);
        if (approvalForm != null) {
            //不是自定义模式 返回false
            if (!ApprovalMode.CUSTOM.getId().equals(approvalForm.getApprovalMode())) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public CustomFormApprovalModeDTO getCustomFormApprovalMode() {

        CustomFormApprovalModeDTO customFormApproverMode = ruleService.getCustomFormApproverMode();
        return customFormApproverMode;

    }
}
