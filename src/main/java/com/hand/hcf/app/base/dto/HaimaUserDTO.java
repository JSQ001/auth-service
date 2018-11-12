package com.hand.hcf.app.base.dto;

import lombok.Data;

/**
 * Created by Ray Ma on 2018/1/4.
 */
@Data
public class HaimaUserDTO {
    private Long tenantId;
    private String userOid;
    private String userId;
    private String userName;
    private String accountNumber;
}
