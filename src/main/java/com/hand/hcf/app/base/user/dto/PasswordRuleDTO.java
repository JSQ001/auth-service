package com.hand.hcf.app.base.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordRuleDTO {
    protected int passwordExpireDays;

    protected String passwordRule;

    protected int passwordLengthMin;

    protected int passwordLengthMax;

    protected int passwordRepeatTimes;

    private int autoUnlockDuration;

    private int passwordAttemptTimes;

    private Boolean allowReset=false;
}
