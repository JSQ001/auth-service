package com.hand.hcf.app.workflow.dto;

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
    //申请关联报销单 表单
    private UUID referenceOid;
    private Boolean submitFlag = false;
    //用户可见范围，1001，全部可见；1002，自定义用户组可见
    private Integer VisibleUserScope;
    //表单编码
    private String formCode;
    private Long companyId;
    private UUID companyOid;
    //private String companyName;

    private Long tenantId;

    private Long setOfBooksId;
    //private Long legalEntityId;

    //是否被分配
    private Boolean assigned;
    private Integer fromType;
    private Integer visibleCompanyScope;
    private Integer approvalMode;
    private List<FormFieldDTO> formFieldList;
    private RuleApprovalChainDTO ruleApprovalChain;
    private List<RuleTransferDTO> ruleTransfers;
    private Map<String, List<Map<String, String>>> i18n;
}
