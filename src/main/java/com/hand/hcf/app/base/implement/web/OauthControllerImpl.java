package com.hand.hcf.app.base.implement.web;

import com.hand.hcf.app.base.service.OauthService;
import com.hand.hcf.app.client.oauth.OauthInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
public class OauthControllerImpl implements OauthInterface {

    @Autowired
    private OauthService oauthService;

    @Override
    public void updateOauthAccessTokenCompanyByLogin(String login, Long companyId, UUID companyOid) {
        oauthService.updateOauthAccessTokenCompanyByLogin(login, companyId, companyOid);
    }
}
