package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.web.dto.DomainObjectDTO;
import lombok.Data;


/**
 * @Author: bin.xie
 * @Description: 付款申请单类型实体类
 * @Date: Created in 10:50 2018/1/22
 * @Modified by
 */
@Data
public class PaymentRequisitionTypesCO extends DomainObjectDTO {

    private Long id; //主键ID


    private String acpReqTypeCode; //付款申请单代码

    private String description; //付款申请单名称


    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;//租户ID

    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId; //账套ID

    private Boolean isRelated;//是否关联报账单

    private String accordingAsRelated;//关联报账单的依据

    private String relatedType;//关联报账单的类型

    private String formOid;//关联表单OID

    private String formName;//关联表单名称

    private String formType;//关联表单类型

    private String applyEmployee; //适用人员

}
