package com.hand.hcf.app.prepayment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by 韩雪 on 2017/12/29.
 */
@ApiModel(description = "预付款单类型关联部门")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("csh_pay_req_type_a_department")
public class CashPayRequisitionTypeAssignDepartment {
    //主键ID
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId("id")
    @ApiModelProperty(value = "主键ID")
    private Long id;

    //预付款单类型ID
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("pay_requisition_type_id")
    @ApiModelProperty(value = "预付款单类型ID")
    private Long payRequisitionTypeId;

    //部门ID
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("department_id")
    @ApiModelProperty(value = "部门ID")
    private Long departmentId;
}
