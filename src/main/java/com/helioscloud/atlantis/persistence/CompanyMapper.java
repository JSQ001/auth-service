/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.persistence;


import java.util.UUID;


/**
 * Created by markfredchen on 2017/3/16.
 */
public interface CompanyMapper {

    Long findTenantIdByCompanyOID(UUID companyOID);
}
