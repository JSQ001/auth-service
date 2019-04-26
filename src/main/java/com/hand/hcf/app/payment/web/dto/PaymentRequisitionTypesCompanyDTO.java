package com.hand.hcf.app.payment.web.dto;

import com.hand.hcf.app.core.web.dto.DomainObjectDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 16:48 2018/1/23
 * @Modified by
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequisitionTypesCompanyDTO extends DomainObjectDTO {

    private Long acpReqTypesId;

    private List<Long> companyIds;
}
