package com.hand.hcf.app.ant.attachment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description:  单据类型附件权限设置domain类
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @date 2019/5/16 14:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("exp_report_type_attchment")
public class ExpReportTypeAttchment extends Domain implements Serializable {

    @TableField("id")
    private Long id;

    /**
     * 租户ID
     */
    @NotNull
    @TableField("tenant_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    /**
     * 账套ID
     */
    @NotNull
    @TableField("set_of_books_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;

    /**
     * 报账单类型代码
     */
    @NotNull
    @TableField("report_type_code")
    private String reportTypeCode;

    /**
     * 报账单类型名称
     */
    @NotNull
    @TableField("report_type_name")
    private String reportTypeName;

    /**
     * 状态;是否启用
     */
    @TableField("enabled")
    private Boolean enabled;


}
