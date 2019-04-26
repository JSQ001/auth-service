package com.hand.hcf.app.payment.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 15:36 2018/4/28
 * @Modified by
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartnerSelectDTO {
    private Long partnerId; // ID
    private String partnerCategory; //类型 VENDER-供应商 EMPLOYEE-员工
    private String partnerName;//姓名
}
