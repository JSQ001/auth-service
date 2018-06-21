package com.helioscloud.atlantis.security;

import com.helioscloud.atlantis.AuthServiceH2Test;
import com.helioscloud.atlantis.OAuthHelperH2;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class ArtemisAuthorizationCodeServicesTest extends AuthServiceH2Test {

   /* @Autowired
    private ArtemisAuthorizationCodeServices artemisAuthorizationCodeServices;

    @Autowired
    private OAuthHelperH2 helper;

    @Test
    public void createAuthorizationCode() {
        String code = artemisAuthorizationCodeServices.createAuthorizationCode(helper.oAuth2Authentication("ArtemisWeb", "13323454321"));
        assertThat(code.isEmpty()).isFalse();
        artemisAuthorizationCodeServices.readAuthentication(code);
        artemisAuthorizationCodeServices.consumeAuthorizationCode(code);
    }*/
}