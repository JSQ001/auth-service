package com.hand.hcf.app.mdata.setOfBooks.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by fanfuqiang 2018/11/20
 */
@Data
public class SetOfBooksDTO {

    private Long setOfBooksId;     //  账套ID
    private String setOfBooksCode;  //  账套代码
    private String setOfBooksName;  //  账套名称

    private Long periodSetId;    //  会计期ID
    private String periodSetCode;  //  会计期代码

    private Long accountSetId;      //  科目表ID
    private String accountSetCode;  //  科目表代码
    private String functionalCurrencyCode;  //  本位币
    private Boolean enabled;  //  启用标志
    private Map<String, List<Map<String, String>>> i18n;  //  多语言字段
    private Integer versionNumber;

}
