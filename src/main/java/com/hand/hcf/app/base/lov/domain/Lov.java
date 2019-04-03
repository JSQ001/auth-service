package com.hand.hcf.app.base.lov.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.hand.hcf.app.base.lov.web.dto.LovColumnInfoDTO;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.annotation.UniqueField;
import com.hand.hcf.core.domain.DomainI18n;
import lombok.Data;

import java.util.List;

/**
 * Created by weishan on 2019/3/5.
 * lov定义
 */
@Data
@TableName("sys_lov")
public class Lov extends DomainI18n {

    @TableField("lov_code")
    @UniqueField
    private String lovCode; //应用代码

    @I18nField
    @TableField("lov_name")
    private String lovName; // 应用名称

    private String dataType;

    private Long appId;

    @TableField(exist = false)
    private String appName;

    private Long apiId;
    @TableField(exist = false)
    private String apiName;

    private String sqlText;
    private String title;
    private String prompt;
    private String requestColumn;
    private String responseColumn;

    @TableField(exist = false)
    private List<LovColumnInfoDTO> requestColumnInfo;
    @TableField(exist = false)
    private List<LovColumnInfoDTO> responseColumnInfo;
    @TableField(value = "remarks", strategy = FieldStrategy.IGNORED)
    private String remarks;
}
