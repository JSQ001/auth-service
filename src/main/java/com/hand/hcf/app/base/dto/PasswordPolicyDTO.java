package com.hand.hcf.app.base.dto;

import lombok.Data;

@Data
public class PasswordPolicyDTO {

    private Integer passwordExpireDays;
    private Integer passwordAttemptTimes;
    private Integer autoUnlockDuration;
}
