package com.hand.hcf.app.ant.sso.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

@Data
@NoArgsConstructor
public class TokenDTO {
    String userName;
    OAuth2AccessToken token;
}
