package com.hand.hcf.app.common.co;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by cbc on 2017/10/27.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CashPaymentParamCO {

    @NotNull
    private CashPaymentRequisitionHeaderCO head;

    private List<CashPaymentRequisitionLineCO> line;
}
