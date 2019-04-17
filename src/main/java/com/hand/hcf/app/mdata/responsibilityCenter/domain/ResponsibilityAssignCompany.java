package com.hand.hcf.app.mdata.responsibilityCenter.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainEnable;
import lombok.Data;

@Data
@TableName("sys_res_center_company_assign")
public class ResponsibilityAssignCompany extends DomainEnable {

    private Long responsibilityCenterId;

    //公司ID
    private Long companyId;

    //公司代码
    private String companyCode;

    //公司名称
    @TableField(exist = false)
    private String companyName;

    //公司类型
    @TableField(exist = false)
    private String companyType;
}
