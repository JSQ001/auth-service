package com.hand.hcf.app.common.co;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Created by liuzhiyu on 2018/2/1.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalHistoryCO {

    private Long id;


    private Integer entityType;


    private String entityOid;


    private Integer operationType;


    private Integer operation;


    private Integer countersignType;



    private String operatorOid;


    private String currentApplicantOid;


    private String operationDetail;

    private Long stepID;


    private String remark;


    private String createdDate;


    private String lastModifiedDate;


    private String ruleApprovalNodeOid;


    private Long refApprovalChainId;
}
