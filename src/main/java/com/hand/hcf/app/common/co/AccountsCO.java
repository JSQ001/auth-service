package com.hand.hcf.app.common.co;


import lombok.Data;

import java.io.Serializable;


/**
 * Created by silence on 2017/12/18.
 */
@Data
public class AccountsCO implements Serializable {

    private Long accountSetId;
    private String accountCode;
    private String accountName;
    private String accountDesc;
    private String accountType;
    private String balanceDirection;
    private String reportType;
    private Boolean summaryFlag;

    private Long id;
}
