package com.hand.hcf.app.mdata.supplier.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

/**
 * @description:
 * @version: 1.0
 * @author: jaixing.che
 * @date: 2019/3/20
 */
@Data
@TableName(value = "ven_vendor_industry_info")
public class VendorIndustryInfo extends Domain {

    private Long  vendorId;
    private Long industryId;
}
