package com.hand.hcf.app.payment.web.dto;

import com.hand.hcf.app.payment.domain.PaymentRequisitionTypes;
import lombok.Data;

import java.util.List;

/**
 * <p>
 *     付款申请单类型和关联的人员组ID DTO
 * </p>
 *
 * @Author: bin.xie
 * @Date: Created in 9:36 2018/7/18
 */
@Data
public class PaymentRequisitionTypesAndUserGroupDTO extends PaymentRequisitionTypes {
    private List<Long> userGroupIds;
}
