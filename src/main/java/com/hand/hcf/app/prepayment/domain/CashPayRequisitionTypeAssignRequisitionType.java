package com.hand.hcf.app.prepayment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by 韩雪 on 2017/12/5.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("csh_pay_req_type_a_req_type")
public class CashPayRequisitionTypeAssignRequisitionType{
    //主键ID
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id")
    private Long id;

    //预付款单类型ID
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "pay_requisition_type_id")
    private Long payRequisitionTypeId;

    //申请单类型ID
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "requisition_type_id")
    private Long requisitionTypeId;

}
