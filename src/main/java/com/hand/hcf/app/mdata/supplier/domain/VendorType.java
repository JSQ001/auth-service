package com.hand.hcf.app.mdata.supplier.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainLogicEnable;
import lombok.Data;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/11 10:56
 */
@TableName("ven_vendor_type")
@Data
public class VendorType extends DomainLogicEnable {

    private String code;

    private String name;

    private Long companyId;

    private Long tenantId;
}
