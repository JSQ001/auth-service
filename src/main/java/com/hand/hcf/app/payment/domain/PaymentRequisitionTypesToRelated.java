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
 * @Description: 付款申请单关联报账单类型实体类
 * @Date: Created in 15:06 2018/1/22
 * @Modified by
 */

@ApiModel(description = "付款申请单关联报账单类型实体类")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("csh_req_types_to_related")
public class PaymentRequisitionTypesToRelated extends Domain {

    @ApiModelProperty(value = "主键ID")
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;//ID

    @ApiModelProperty(value = "借款申请单ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "acp_req_types_id")
    private Long acpReqTypesId;//借款申请单ID

    @ApiModelProperty(value = "报账单ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "type_id")
    private Long typeId;//报账单ID

    @ApiModelProperty(value = "关联类型")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "related_type")
    private String relatedType; ///关联类型

}
