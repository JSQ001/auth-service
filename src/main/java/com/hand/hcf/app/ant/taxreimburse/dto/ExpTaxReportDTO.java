package com.hand.hcf.app.ant.taxreimburse.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 税金申报信息类
 * @date 2019/5/29 10:18
 */
@Data
public class ExpTaxReportDTO extends Domain {
    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 币种代码
     */
    private String currencyCode;

    /**
     * 申报总金额
     */
    private BigDecimal requestAmountSum;

}
