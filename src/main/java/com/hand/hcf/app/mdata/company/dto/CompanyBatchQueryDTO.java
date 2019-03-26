package com.hand.hcf.app.mdata.company.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by silence on 2018/1/19.
 */
@Data
public class CompanyBatchQueryDTO {
    Long setOfBooksId;
    List<String> codes;
}
