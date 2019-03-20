package com.hand.hcf.app.auth.dto;

import lombok.Data;

@Data
public class PasswordPolicyDTO {

    private Integer passwordExpireDays;
    private Integer passwordAttemptTimes;
    private Integer autoUnlockDuration;
}
