package com.hand.hcf.app.mdata.supplier.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainEnable;
import lombok.Data;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/14 12:01
 */
@TableName("ven_relation_vendor_company")
@Data
public class RelationVendorCompany extends DomainEnable {

    private Long vendorInfoId;

    private Long companyId;

    private Long tenantId;
}
