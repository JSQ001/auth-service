package com.hand.hcf.app.mdata.company.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by 刘亮 on 2017/10/24.
 * 用于封装传递参数
 */
@Data
public class CompanyParamDTO {
    private Long id;
    private Long setOfBooks;
    private String compamyCode;
    private String companyName;
    private String companyCodeFrom;
    private String companyCodeTo;
    private List<Long> companyIds;
}
