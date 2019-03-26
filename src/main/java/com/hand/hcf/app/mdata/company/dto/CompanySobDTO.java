package com.hand.hcf.app.mdata.company.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * @Author: 魏胜
 * @Description:
 * @Date: 2018/5/30 12:22
 */
@Data
public class CompanySobDTO implements Serializable {


    private Long id;

    @JsonIgnore
    private UUID companyOid;

    private String name;

    private Long setOfBooksId;

    private String setOfBooksName;

    private String companyCode;
}
