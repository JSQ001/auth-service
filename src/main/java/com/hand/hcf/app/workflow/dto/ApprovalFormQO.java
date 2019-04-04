package com.hand.hcf.app.workflow.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Created by houyin.zhang@hand-china.com on 2018/9/5.
 * 表单信息
 */
@Data
@Builder
public class ApprovalFormQO {
    private UUID formOid;
    private List<UUID> formOids;
    private List<UUID> excludedFormOids;
    private Long id;
    private String formCode;
    private Integer fromType;
    private UUID companyOid;
    private List<UUID> companyOids;
    private Long companyId;
    private List<Long> companyIds;
    private Long tenantId;
    private List<Long> tenantIds;
    private Integer formTypeId;
    private List<Integer> formTypeList;
}
