package com.hand.hcf.app.common.co;

import lombok.Data;

@Data
public class SetOfBooksInfoCO {

    private Long id;
    /**
     * 账套代码
     */
    private String setOfBooksCode;
    /**
     * 账套名称
     */
    private String setOfBooksName;
    /**
     * 本位币
     */
    private String functionalCurrencyCode;

}
