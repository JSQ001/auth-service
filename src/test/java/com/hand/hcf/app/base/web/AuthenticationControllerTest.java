package com.hand.hcf.app.base.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/*
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
}*/
