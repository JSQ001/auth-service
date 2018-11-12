

package com.hand.hcf.app.base.persistence;


import java.util.UUID;


/**
 * Created by markfredchen on 2017/3/16.
 */
public interface CompanyMapper {

    Long findTenantIdByCompanyOID(UUID companyOID);
}
