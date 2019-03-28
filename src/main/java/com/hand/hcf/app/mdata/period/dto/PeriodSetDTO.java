package com.hand.hcf.app.mdata.period.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 *会计期间实体视图对象类
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PeriodSetDTO {

    private Long id;
    private String periodSetCode;
    private Integer totalPeriodNum;
    private String periodAdditionalFlag;//P:附加前缀,S:附加后缀
    private String periodAdditionalFlagDes;//P:附加前缀,S:附加后缀

    private Long tenantId;//租户id
    private Boolean enabled;
    private Boolean deleted;
    //会计期名称
    private String periodSetName;
    protected ZonedDateTime createdDate;
    protected ZonedDateTime lastUpdatedDate;
    protected Long createdBy;
    protected Long lastUpdatedBy;
    @TableField(exist = false)
    private Map<String, List<Map<String, String>>> i18n;
}
