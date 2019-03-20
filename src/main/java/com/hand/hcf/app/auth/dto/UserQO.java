package com.hand.hcf.app.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class UserQO {
    private Long id;
    private UUID userOid;
    private String login;
    private String loginBind;
    private String email;
    private String mobile;
}
