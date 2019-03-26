package com.hand.hcf.app.mdata.parameter.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainEnable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模块启用状态表
 * @Auther: chenzhipeng
 * @Date: 2018/12/26 18:52
 */
@Data
@TableName("sys_para_module_status")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParameterModuleStatus extends DomainEnable {
    /**
     * 租户id
     */
    @TableField("tenant_id")
    private Long tenantId;
    /**
     * 模块代码
     */
    @TableField("module_code")
    private String moduleCode;


}
