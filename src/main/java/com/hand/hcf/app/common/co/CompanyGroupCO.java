package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fanfuqiang 2018/11/21
 */
@Data
public class CompanyGroupCO implements Serializable {

    private Long id;
    private String companyGroupCode;

    private String companyGroupName;

    private String description;


    private Long setOfBooksId;


    private Long tenantId;

    private List<Long> companyIds;
}
