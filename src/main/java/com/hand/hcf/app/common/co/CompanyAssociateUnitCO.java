package com.hand.hcf.app.common.co;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/4/28
 */
@Data
@NoArgsConstructor
public class CompanyAssociateUnitCO implements Serializable {
    private Long companyId;
    private Long departmentId;

    public CompanyAssociateUnitCO(Long companyId, Long departmentId){
        this.companyId = companyId;
        this.departmentId = departmentId;
    }
}
