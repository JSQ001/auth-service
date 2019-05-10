package com.hand.hcf.app.expense.adjust.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustLine;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by silence on 2018/3/21.
 */
@ApiModel(description = "费用调整单行")
@Data
public class ExpenseAdjustLinesBean {
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "id")
    private Long id;

    // 租户ID
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "租户id")
    private Long tenantId;

    // 账套ID
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "账套ID")
    private Long setOfBooksId;

    // 费用调整单头ID
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "费用调整单头ID")
    private Long expAdjustHeaderId;

    // 单据行类型
    @ApiModelProperty(value = "单据行类型")
    private String adjustLineCategory;

    // 公司id
    @ApiModelProperty(value = "公司id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;

    // 部门id
    @ApiModelProperty(value = "部门id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long unitId;

    // 申请人id
    @ApiModelProperty(value = "申请人id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;

    // 申请人日期
    @ApiModelProperty(value = "申请人日期")
    private ZonedDateTime adjustDate;

    // 说明
    @ApiModelProperty(value = "说明")
    private String description;

    // 币种
    @ApiModelProperty(value = "币种")
    private String currencyCode;

    // 汇率
    @ApiModelProperty(value = "汇率")
    private BigDecimal exchangeRate;

    // 费用类型id
    @ApiModelProperty(value = "账套ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long expenseTypeId;

    // 金额
    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    // 本币金额
    @ApiModelProperty(value = "本币金额")
    private BigDecimal functionalAmount;

    // 维度1
    @ApiModelProperty(value = "维度1")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension1Id;

    // 维度2
    @ApiModelProperty(value = "维度2")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension2Id;

    // 维度3
    @ApiModelProperty(value = "维度3")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension3Id;

    // 维度4
    @ApiModelProperty(value = "维度4")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension4Id;

    // 维度5
    @ApiModelProperty(value = "维度5")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension5Id;

    // 维度6
    @ApiModelProperty(value = "维度6")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension6Id;

    // 维度7
    @ApiModelProperty(value = "维度7")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension7Id;

    // 维度8
    @ApiModelProperty(value = "维度8")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension8Id;

    // 维度9
    @ApiModelProperty(value = "维度9")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension9Id;

    // 维度10
    @ApiModelProperty(value = "维度10")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension10Id;

    // 维度11
    @ApiModelProperty(value = "维度11")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension11Id;

    // 维度12
    @ApiModelProperty(value = "维度12")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension12Id;

    // 维度13
    @ApiModelProperty(value = "维度13")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension13Id;

    // 维度14
    @ApiModelProperty(value = "维度14")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension14Id;

    // 维度15
    @ApiModelProperty(value = "维度15")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension15Id;

    // 维度16
    @ApiModelProperty(value = "维度16")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension16Id;

    // 维度17
    @ApiModelProperty(value = "维度17")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension17Id;

    // 维度18
    @ApiModelProperty(value = "维度18")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension18Id;

    // 维度19
    @ApiModelProperty(value = "维度19")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension19Id;

    // 维度20
    @ApiModelProperty(value = "维度20")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension20Id;

    // 审核状态
    @ApiModelProperty(value = "审核状态")
    private String auditFlag;

    // 审核日期
    @ApiModelProperty(value = "审核日期")
    private String auditDate;

    // 源单据行id
    @ApiModelProperty(value = "源单据行id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sourceAdjustLineId;

    // 创建凭证标志
    @ApiModelProperty(value = "创建凭证标志")
    private String jeCreationStatus;

    // 创建凭证日期
    @ApiModelProperty(value = "创建凭证日期")
    private ZonedDateTime jeCreationDate;

    // 费用分摊第二层
    @ApiModelProperty(value = "费用分摊第二层")
    private List<ExpenseAdjustLine> linesList;

    //删除行 id
    @ApiModelProperty(value = "删除行 id")
    private  List<Long> deleteIds;

    @ApiModelProperty(value = "附件OID 多个 ,隔开")
    private String attachmentOid; // 附件OID 多个 ,隔开


}
