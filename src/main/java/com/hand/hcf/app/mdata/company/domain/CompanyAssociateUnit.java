package com.hand.hcf.app.mdata.company.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainEnable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *  公司部门关联关系表
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/4/15
 */
@TableName("sys_company_associate_unit")
@Data
@EqualsAndHashCode(callSuper = true)
public class CompanyAssociateUnit extends DomainEnable {
    private Long companyId;
    private Long departmentId;

}
