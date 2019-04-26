package com.hand.hcf.app.payment.web.dto;

import lombok.Data;

/**
 * 财务信息DTO
 * Created by 刘亮 on 2017/12/20.
 */
@Data
public class FinancialDTO {

    //摘要
    private String info;

    //公司
    private String companyName;

    //成本中心
    private String costCenter;

    //科目
    private String subject;

}
