package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 15:26 2018/1/22
 * @Modified by
 */
@ApiModel(description = "借款申请单分配用户")
@Data
@TableName("csh_req_types_to_users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequisitionTypesToUsers extends Domain {
    @ApiModelProperty(value = "主键ID")
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;//ID

    @ApiModelProperty(value = "借款申请单ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "acp_req_types_id")
    private Long acpReqTypesId;//借款申请单ID

    @ApiModelProperty(value = "员工组或者部门ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "user_group_id")
    private Long userGroupId;//员工组或者部门ID

    @ApiModelProperty(value = "关联类型")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "user_type")
    private String userType; //关联类型

    @ApiModelProperty(value = "名称")
    @TableField(value = "path_or_name")
    private String pathOrName;//名称

}
