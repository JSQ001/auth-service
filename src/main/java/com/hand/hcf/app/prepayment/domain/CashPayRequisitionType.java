package com.hand.hcf.app.prepayment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.common.enums.CashPayRequisitionTypeBasisEnum;
import com.hand.hcf.app.common.enums.CashPayRequisitionTypeEmployeeEnum;
import com.hand.hcf.app.common.enums.CashPayRequisitionTypeTypeEnum;
import com.hand.hcf.core.domain.DomainLogicEnable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by 韩雪 on 2017/10/24.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("csh_sob_pay_req_type")
public class CashPayRequisitionType extends DomainLogicEnable {
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "set_of_book_id")
    private Long setOfBookId;//账套ID

    @TableField(value = "type_code")
    private String typeCode;//预付款单类型代码

    @TableField(value = "type_name")
    private String typeName;//预付款单类型名称

    @TableField(value = "payment_method_category")
    private String paymentMethodCategory;//付款方式类型(线上、线下、落地文件)

    //关联表单类型oid
    @TableField(value = "form_oid", strategy = FieldStrategy.IGNORED)
    private String formOid;

    //关联表单名称
    @TableField(value = "form_name", strategy = FieldStrategy.IGNORED)
    private String formName;

    //关联表单类型
    @TableField(value = "form_type", strategy = FieldStrategy.IGNORED)
    private Long formType;

    //是否全部申请单类型
    @TableField(value = "all_type")
    private CashPayRequisitionTypeTypeEnum allType;
    //是否全部现金事务分类
    @TableField(value = "all_class", strategy = FieldStrategy.IGNORED)
    private Boolean allClass;

    @TableField(exist = false)
    private String setOfBookCode;//账套code

    @TableField(exist = false)
    private String setOfBookName;//账套name

    @TableField(exist = false)
    private String paymentMethodCategoryName;//付款方式类型name



    //是否需要申请
    @TableField(value = "need_apply")
    private Boolean needApply;

    //关联申请单依据
    @TableField(value = "application_form_basis", strategy = FieldStrategy.IGNORED)
    private CashPayRequisitionTypeBasisEnum applicationFormBasis;


    //适用人员
    @TableField(value = "apply_employee")
    private CashPayRequisitionTypeEmployeeEnum applyEmployee;
}
