package com.hand.hcf.app.ant.mdata.location.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by zihao.yang on 2019-6-11 19:52:16
 */
@Data
@TableName("sys_payee_setting_line")
@ApiModel(description = "收款方配置行表")
public class PayeeSettingLine extends Domain {

    /**
     * 头表id
     */
    @TableField(value = "header_id")
    @ApiModelProperty(value = "头表id",dataType = "Long")
    private Long headerId;

    /**
     * 字段值编码
     */
    @TableField(value = "field_code")
    @ApiModelProperty(value = "字段值编码",dataType = "String")
    private String fieldCode;

    /**
     * 是否必填
     */
    @TableField(value = "is_nacessary_flag")
    @ApiModelProperty(value = "是否必填",dataType = "Boolean")
    private Boolean isNacessaryFlag;

}
