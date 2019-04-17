package com.hand.hcf.app.expense.travel.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainEnable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 差旅申请单类型关联公司
 * @author shouting.cheng
 * @date 2019/3/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("exp_travel_app_type_ass_com")
public class TravelApplicationTypeAssignCompany extends DomainEnable {
    /**
     * 差旅申请单类型ID
     */
    private Long typeId;
    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 公司代码
     */
    @TableField(exist = false)
    private String companyCode;
    /**
     * 公司名称
     */
    @TableField(exist = false)
    private String companyName;
    /**
     * 公司类型
     */
    @TableField(exist = false)
    private String companyType;
}
