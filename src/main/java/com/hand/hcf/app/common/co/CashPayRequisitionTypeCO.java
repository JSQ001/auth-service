package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.common.enums.CashPayRequisitionTypeBasisEnum;
import com.hand.hcf.app.common.enums.CashPayRequisitionTypeEmployeeEnum;
import com.hand.hcf.app.common.enums.CashPayRequisitionTypeTypeEnum;
import com.hand.hcf.core.web.dto.DomainObjectDTO;
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
public class CashPayRequisitionTypeCO extends DomainObjectDTO {
    /**
     * //账套ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBookId;

    private String typeCode;//预付款单类型代码

    private String typeName;//预付款单类型名称

    private String paymentMethodCategory;//付款方式类型(线上、线下、落地文件)

    //关联表单类型oid
    private String formOid;

    //关联表单名称
    private String formName;

    //关联表单类型
    private Long formType;

    //是否全部申请单类型
    private CashPayRequisitionTypeTypeEnum allType;
    //是否全部现金事务分类
    private Boolean allClass;

    private String setOfBookCode;//账套code

    private String setOfBookName;//账套name

    private String paymentMethodCategoryName;//付款方式类型name


    //是否需要申请
    private Boolean needApply;

    //关联申请单依据
    private CashPayRequisitionTypeBasisEnum applicationFormBasis;


    //适用人员
    private CashPayRequisitionTypeEmployeeEnum applyEmployee;
}
