package com.hand.hcf.app.expense.adjust.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by silence on 2018/3/20.
 */
@Data
public class ExpenseAdjustLineWebDTO {
    // 主键ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    // 公司id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
    // 公司名称
    private String companyName;
    // 部门id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long unitId;
    // 部门名称
    private String unitName;
    // 费用类型id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long expenseTypeId;
    // 费用类型Name
    private String expenseTypeName;
    // 单据行类型
    private String adjustLineCategory;
    /**
     * 版本号
     */
    private Integer versionNumber;

    /**
     * 维度信息
     */
    private List<ExpenseDimension> dimensions;

    // 维度1
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension1Id;
    private String dimension1Name;

    // 维度2
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension2Id;
    private String dimension2Name;

    // 维度3
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension3Id;
    private String dimension3Name;

    // 维度4
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension4Id;
    private String dimension4Name;

    // 维度5
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension5Id;
    private String dimension5Name;

    // 维度6
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension6Id;
    private String dimension6Name;

    // 维度7
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension7Id;
    private String dimension7Name;

    // 维度8
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension8Id;
    private String dimension8Name;

    // 维度9
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension9Id;
    private String dimension9Name;

    // 维度10
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension10Id;
    private String dimension10Name;

    // 维度11
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension11Id;
    private String dimension11Name;

    // 维度12
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension12Id;
    private String dimension12Name;

    // 维度13
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension13Id;
    private String dimension13Name;

    // 维度14
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension14Id;
    private String dimension14Name;

    // 维度15
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension15Id;
    private String dimension15Name;

    // 维度16
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension16Id;
    private String dimension16Name;

    // 维度17
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension17Id;
    private String dimension17Name;

    // 维度18
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension18Id;
    private String dimension18Name;

    // 维度19
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension19Id;
    private String dimension19Name;

    // 维度20
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension20Id;
    private String dimension20Name;

    // 金额
    private BigDecimal amount;
    private BigDecimal functionalAmount; // 本位币金额
    // 说明
    private String description;
    // 第二页DTO数据
    private List<ExpenseAdjustLineWebDTO> linesDTOList;

    /**
     * 增加附件
     */
    private List<String> attachmentOids;
    private String attachmentOid;
    private List<AttachmentCO> attachments;


}
