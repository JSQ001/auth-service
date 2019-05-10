package com.hand.hcf.app.expense.type.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.expense.type.domain.enums.FieldType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/7
 */
@ApiModel(description = "ExpenseFieldDTO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseFieldDTO implements Serializable{
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "字段类型")
    private FieldType fieldType;
    /**
     * 字段数据类型
     */
    @ApiModelProperty(value = "字段数据类型")
    private String fieldDataType;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "值")
    private String value;
    @ApiModelProperty(value = "编码名称")
    private String codeName;
    @ApiModelProperty(value = "信息key")
    private String messageKey;
    @ApiModelProperty(value = "序列")
    private Integer sequence;
    /**
     * 值列表oid
     */
    @ApiModelProperty(value = "值列表oid")
    private UUID customEnumerationOid;
    /**
     * 映射列
     */
    @ApiModelProperty(value = "映射列")
    private Integer mappedColumnId;
    /**
     * 打印隐藏
     */
    @ApiModelProperty(value = "打印隐藏")
    private Boolean printHide;
    /**
     * 是否必填
     */
    @ApiModelProperty(value = "是否必填")
    private Boolean required;
    /**
     * 列表展示
     */
    @ApiModelProperty(value = "列表展示")
    private Boolean showOnList;
    /**
     * 是否包含国家
     */
    private Boolean containCountry;
    /**
     * 是否包含省/州
     */
    private Boolean containProvince;
    /**
     * 是否包含直辖市
     */
    private Boolean containMunicipality;
    /**
     * 是否包含市/区
     */
    private Boolean containCity;
    /**
     * 是否包含县
     */
    private Boolean containRegion;
    /**
     * 费用字段oid
     */
    @ApiModelProperty(value = "费用字段oid")
    private UUID fieldOid;
    /**
     * 费用字段是否可编辑，true可编辑，false不可编辑
     */
    @ApiModelProperty(value = "费用字段是否可编辑，true可编辑，false不可编辑")
    private Boolean editable;

    /**
     * 默认值模式CURRENT CUSTOM API
     */
    @ApiModelProperty(value = "默认值模式CURRENT CUSTOM API")
    private String defaultValueMode;
    /**
     * 默认值存储
     */
    @ApiModelProperty(value = "默认值存储")
    private String defaultValueKey;
    /**
     * 默认值显示
     */
    @ApiModelProperty(value = "默认值显示")
    private String showValue;
    /**
     * 管理员是否可配置默认值
     */
    @ApiModelProperty(value = "管理员是否可配置默认值")
    private Boolean defaultValueConfigurable;

    /**
     * 公共字段标记
     */
    @ApiModelProperty(value = "公共字段标记")
    private Boolean commonField;
    /**
     * 报表key
     */
    @ApiModelProperty(value = "报表key")
    private String reportKey;

    /**
     * 费用字段名称多语言字段
     */
    @ApiModelProperty(value = "费用字段名称多语言字段")
    private Map<String, List<Map<String, String>>> i18n;

    /**
     * 值列表值信息
     */
    @ApiModelProperty(value = "值列表值信息")
    private List<OptionDTO> options;

}
