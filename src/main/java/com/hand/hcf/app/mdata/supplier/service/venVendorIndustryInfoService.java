package com.hand.hcf.app.mdata.supplier.service;

import com.hand.hcf.app.mdata.supplier.domain.VendorIndustryInfo;
import com.hand.hcf.app.mdata.supplier.persistence.VenVendorIndustryInfoMapper;
import com.hand.hcf.app.core.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:
 * @version: 1.0
 * @author: jaixing.che
 * @date: 2019/3/20
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class venVendorIndustryInfoService extends BaseService<VenVendorIndustryInfoMapper, VendorIndustryInfo> {

}
