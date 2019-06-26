package com.hand.hcf.app.ant.excel.domain.temp;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/6/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("excel_import_temp")
public class ExcelTemplateTempDomain extends Domain {

    //临时表字段
    @TableField(value = "enabled_str")
    private String enabledStr;

    @TableField(value = "row_number")
    private String rowNumber ;

    @TableField(value = "batch_number")
    private String batchNumber ;

    @TableField(value = "error_detail")
    private String errorDetail;

    @TableField(value = "error_flag")
    private Boolean errorFlag;

    /**
     * 业务小类
     */
    @TableField(value = "expense_type_id")
    private Long expenseTypeId;
    /**
     * OU
     */
    @TableField(value = "report_line_OU")
    private Long companyId;
    /**
     * 受益期开始
     */
    @TableField(value = "expense_date_start")
    private ZonedDateTime expenseDateStart;
    /**
     * 受益期结束
     */
    @TableField(value = "expense_date_end")
    private ZonedDateTime expenseDateEnd;
    /**
     * 币种
     */
    @TableField(value = "currency_code")
    private String currencyCode;

    /**
     * 报账金额
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    @TableField(exist = false)
    private String attribute1;
    @TableField(exist = false)
    private String attribute2;
    @TableField(exist = false)
    private String attribute3;
    @TableField(exist = false)
    private String attribute4;
    @TableField(exist = false)
    private String attribute5;
    @TableField(exist = false)
    private String attribute6;
    @TableField(exist = false)
    private String attribute7;
    @TableField(exist = false)
    private String attribute8;
    @TableField(exist = false)
    private String attribute9;
    @TableField(exist = false)
    private String attribute10;
    @TableField(exist = false)
    private String attribute11;
    @TableField(exist = false)
    private String attribute12;
    @TableField(exist = false)
    private String attribute13;
    @TableField(exist = false)
    private String attribute14;
    @TableField(exist = false)
    private String attribute15;
    @TableField(exist = false)
    private String attribute16;
    @TableField(exist = false)
    private String attribute17;
    @TableField(exist = false)
    private String attribute18;
    @TableField(exist = false)
    private String attribute19;
    @TableField(exist = false)
    private String attribute20;



    /*@TableField("attribute1_id")
    private Long attribute1Id;
    @TableField("attribute2_id")
    private Long attribute2Id;
    @TableField("attribute3_id")
    private Long attribute3Id;
    @TableField("attribute4_id")
    private Long attribute4Id;
    @TableField("attribute5_id")
    private Long attribute5Id;
    @TableField("attribute6_id")
    private Long attribute6Id;
    @TableField("attribute7_id")
    private Long attribute7Id;
    @TableField("attribute8_id")
    private Long attribute8Id;
    @TableField("attribute9_id")
    private Long attribute9Id;
    @TableField("attribute10_id")
    private Long attribute10Id;
    @TableField("attribute11_id")
    private Long attribute11Id;
    @TableField("attribute12_id")
    private Long attribute12Id;
    @TableField("attribute13_id")
    private Long attribute13Id;
    @TableField("attribute14_id")
    private Long attribute14Id;
    @TableField("attribute15_id")
    private Long attribute15Id;
    @TableField("attribute16_id")
    private Long attribute16Id;
    @TableField("attribute17_id")
    private Long attribute17Id;
    @TableField("attribute18_id")
    private Long attribute18Id;
    @TableField("attribute19_id")
    private Long attribute19Id;
    @TableField("attribute20_id")
    private Long attribute20Id;

    @TableField("attribute_value1_id")
    private Long attributeValue1Id;
    @TableField("attribute_value2_id")
    private Long attributeValue2Id;
    @TableField("attribute_value3_id")
    private Long attributeValue3Id;
    @TableField("attribute_value4_id")
    private Long attributeValue4Id;
    @TableField("attribute_value5_id")
    private Long attributeValue5Id;
    @TableField("attribute_value6_id")
    private Long attributeValue6Id;
    @TableField("attribute_value7_id")
    private Long attributeValue7Id;
    @TableField("attribute_value8_id")
    private Long attributeValue8Id;
    @TableField("attribute_value9_id")
    private Long attributeValue9Id;
    @TableField("attribute_value10_id")
    private Long attributeValue10Id;
    @TableField("attribute_value11_id")
    private Long attributeValue11Id;
    @TableField("attribute_value12_id")
    private Long attributeValue12Id;
    @TableField("attribute_value13_id")
    private Long attributeValue13Id;
    @TableField("attribute_value14_id")
    private Long attributeValue14Id;
    @TableField("attribute_value15_id")
    private Long attributeValue15Id;
    @TableField("attribute_value16_id")
    private Long attributeValue16Id;
    @TableField("attribute_value17_id")
    private Long attributeValue17Id;
    @TableField("attribute_value18_id")
    private Long attributeValue18Id;
    @TableField("attribute_value19_id")
    private Long attributeValue19Id;
    @TableField("attribute_value20_id")
    private Long attributeValue20Id;*/

    @TableField(value = "segment1")
    private String segment1;
    @TableField(value = "segment2")
    private String segment2;
    @TableField(value = "segment3")
    private String segment3;
    @TableField(value = "segment4")
    private String segment4;
    /*@TableField(value = "segment5")
    private String segment5;
    @TableField(value = "segment6")
    private String segment6;
    @TableField(value = "segment7")
    private String segment7;
    @TableField(value = "segment8")
    private String segment8;
    @TableField(value = "segment9")
    private String segment9;
    @TableField(value = "segment10")
    private String segment10;
    @TableField(value = "segment11")
    private String segment11;
    @TableField(value = "segment12")
    private String segment12;
    @TableField(value = "segment13")
    private String segment13;
    @TableField(value = "segment14")
    private String segment14;
    @TableField(value = "segment15")
    private String segment15;
    @TableField(value = "segment16")
    private String segment16;
    @TableField(value = "segment17")
    private String segment17;
    @TableField(value = "segment18")
    private String segment18;
    @TableField(value = "segment19")
    private String segment19;
    @TableField(value = "segment20")
    private String segment20;*/

}
