package com.hand.hcf.app.mdata.company.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainEnable;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by fanfuqiang 2018/11/21
 */
@TableName("sys_company_group_assign")
@Data
public class CompanyGroupAssign extends DomainEnable implements Serializable {


    @TableField(value = "company_group_id")
    private Long companyGroupId;


    @TableField(value = "company_id")
    private Long companyId;


    @TableField(value = "tenant_id")
    private Long tenantId;
}
