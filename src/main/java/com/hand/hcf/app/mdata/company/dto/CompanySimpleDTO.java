package com.hand.hcf.app.mdata.company.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 公司简单视图对象
 * Created by Strive on 18/4/2.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanySimpleDTO {
    private UUID companyOid;
    private String companyName;
}

