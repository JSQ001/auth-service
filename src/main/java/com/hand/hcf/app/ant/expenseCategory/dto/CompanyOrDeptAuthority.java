package com.hand.hcf.app.ant.expenseCategory.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.expense.common.utils.RespCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;


@ApiModel(description = "费用小类权限范围")
@Data
@TableName("ant_exp_expense_category_authority")
@AllArgsConstructor
@NoArgsConstructor
@Component
public class CompanyOrDeptAuthority {
    @TableId
    @JsonSerialize(
            using = ToStringSerializer.class   //用字符串类型序列化id
    )
    private Long id;

    /**
     * 适用类型：type
     */
    @ApiModelProperty(value = "费用小类ID")
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    @TableField(value = "category_id")
    private Long categoryId;


    /**
     * 适用类型：type
     */
    @ApiModelProperty(value = "适用类型")
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    @TableField(value = "type")
    private String type;

    /**
     * 公司或部门ID
     */
    @ApiModelProperty(value = "公司或部门ID")
    @TableField(value = "com_or_dept_id")
    private Long comOrDeptId;

    /**
     * value
     */
    @TableField(exist = false)
    private List<Long> values;
}
