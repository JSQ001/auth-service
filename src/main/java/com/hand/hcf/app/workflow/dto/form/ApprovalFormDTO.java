package com.hand.hcf.app.workflow.dto.form;

import com.hand.hcf.app.workflow.brms.dto.RuleApprovalChainDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleTransferDTO;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by houyin.zhang@hand-china.com on 2018/9/5.
 * 表单信息
 */
@Data
public class ApprovalFormDTO {
    private UUID formOid;

    private Long id;
    private String formName;
    private String iconName;
    private String iconUrl;
    private String messageKey;
    private Integer formType;
    private Boolean asSystem;
    private Boolean valid;
    private ZonedDateTime createdDate;
    private ZonedDateTime lastUpdatedDate;
    private Long parentId;
    private UUID parentOid;
    //备注
    private String remark;
    /*
    单据大类
     */
    private String formTypeName;
    //申请关联报销单 表单
    private UUID referenceOid;
    private Boolean submitFlag = false;
    //表单编码
    private String formCode;

    private Long tenantId;

    //是否被分配
    private Boolean assigned;
    private Integer fromType;
    private Integer approvalMode;
    private List<FormFieldDTO> formFieldList;
    private RuleApprovalChainDTO ruleApprovalChain;
    private List<RuleTransferDTO> ruleTransfers;
    private Map<String, List<Map<String, String>>> i18n;

    /** 允许撤回 */
    private Boolean withdrawFlag;

    /** 撤回模式 */
    private Integer withdrawRule;
}
