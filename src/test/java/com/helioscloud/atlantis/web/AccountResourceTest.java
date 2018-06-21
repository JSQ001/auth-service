package com.helioscloud.atlantis.web;

import com.helioscloud.atlantis.AuthServerConfig;
import com.helioscloud.atlantis.OAuthHelper;
import com.helioscloud.atlantis.config.AuthServerConfiguration;
import com.helioscloud.atlantis.config.ResourceServerConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {AccountResource.class, OAuthHelper.class, AuthServerConfiguration.class, AuthServerConfig.class, ResourceServerConfiguration.class})
@ActiveProfiles("control")
public class AccountResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OAuthHelper helper;

    @Test
    public void checkToken() throws Exception {
        RequestPostProcessor bearerToken = helper.bearerToken("ArtemisWeb", "13323454321");
        mockMvc.perform(get("/api/check_token").with(bearerToken))
                .andExpect(status().isOk());
    }
}