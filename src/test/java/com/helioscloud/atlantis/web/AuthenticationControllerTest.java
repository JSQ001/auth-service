package com.helioscloud.atlantis.web;

import com.helioscloud.atlantis.AuthServerConfig;
import com.helioscloud.atlantis.service.AuthenticationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {AuthenticationController.class, AuthServerConfig.class, AccountResource.class})
@ActiveProfiles("control")
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationService authenticationService;

    @Test
    public void getAuthentication() throws Exception {
        mockMvc.perform(get("/api/qr/authorization"))
                .andExpect(status().is(200))
                .andDo(print());

        mockMvc.perform(get("/api/qr/authorization/abc"))
                .andExpect(status().is(404))
                .andDo(print());
    }

    @Test
    @WithMockUser
    public void postAuthenticationLogin() throws Exception {
        mockMvc.perform(post("/api/qr/authorization/prior/abc").with(csrf()))
                .andExpect(status().is(404))
                .andDo(print());

        mockMvc.perform(post("/api/qr/authorization/abc").with(csrf()))
                .andExpect(status().is(404))
                .andDo(print());
    }

    @Test
    @WithMockUser
    public void UnauthenticatedException() throws Exception {
        mockMvc.perform(get("/api/check_token"))
                .andExpect(status().is(500));
    }
}