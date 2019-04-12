package com.hand.hcf.app.common.co;

import lombok.Data;

@Data
public class UserRequestCO {
    private Long userId;
    private String userName;
    private String ip;
    private String userAgent;
    private Integer autoUnlockDuration;
}
