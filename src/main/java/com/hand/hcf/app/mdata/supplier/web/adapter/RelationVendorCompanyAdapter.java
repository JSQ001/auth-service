package com.hand.hcf.app.mdata.supplier.web.adapter;

import com.hand.hcf.app.common.dto.RelationVendorCompanyCO;
import com.hand.hcf.app.mdata.supplier.domain.RelationVendorCompany;
import org.springframework.beans.BeanUtils;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/14 16:44
 */
public class RelationVendorCompanyAdapter {

    public static RelationVendorCompany relationVendorCompanyCOToRelationVendorCompany(RelationVendorCompanyCO relationVendorCompanyCO) {
        if (relationVendorCompanyCO == null) {
            return null;
        }
        RelationVendorCompany relationVendorCompany = new RelationVendorCompany();
        BeanUtils.copyProperties(relationVendorCompanyCO, relationVendorCompany);
        return relationVendorCompany;
    }

    public static RelationVendorCompanyCO relationVendorCompanyToRelationVendorCompanyCO(RelationVendorCompany relationVendorCompany) {
        if (relationVendorCompany == null) {
            return null;
        }
        RelationVendorCompanyCO relationVendorCompanyCO = new RelationVendorCompanyCO();
        BeanUtils.copyProperties(relationVendorCompany, relationVendorCompanyCO);
        return relationVendorCompanyCO;
    }
}
