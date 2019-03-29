package com.hand.hcf.app.mdata.setOfBooks.dto;

import lombok.Data;

/**
 * Created by fanfuqiang 2018/11/21
 */
@Data
public class SetOfBooksPeriodDTO {


    private Long setOfBooksId; //  账套ID
    private String setOfBooksCode;  //  账套代码
    private String setOfBooksName;  //  账套名称

    private Long periodSetId;  //  会计期ID
    private String periodSetCode;  //  会计期代码
    private String periodSetName;  //  会计期名称
    private Integer totalPeriodNum;  //  会计期总期间数
}
