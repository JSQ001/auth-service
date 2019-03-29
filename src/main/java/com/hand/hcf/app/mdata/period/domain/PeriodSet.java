package com.hand.hcf.app.mdata.period.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18nEnable;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Project Name:会计期间类
 */
@Data
@TableName(value = "sys_period_set")
public class PeriodSet extends DomainI18nEnable implements Serializable {
    @NotNull
    private String periodSetCode;//会计期代码
    @I18nField
    private String periodSetName;//会计期名称
    @NotNull
    private Integer totalPeriodNum;//会计期总数
    @NotNull
    private String periodAdditionalFlag;//P:附加前缀,S:附加后缀

    private Long tenantId;//租户id
}
