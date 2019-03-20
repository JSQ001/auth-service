package com.hand.hcf.app.auth.security.BaseProvider;

//@RunWith(SpringRunner.class)
//@WebMvcTest(controllers = {AuthServerConfigTest.class, AuthServerConfiguration.class, ResourceServerConfiguration.class})
//@ActiveProfiles("control")
public class ArtemisAuthenticationProviderTest {

    /*@Autowired
    private MockMvc mockMvc;
    @MockBean
    private WxService wxService;
    @MockBean
    private DingTalkServiceImpl dingTalkServiceImpl;
    @MockBean
    private HaimaService haimaService;
    @MockBean
    private UserService userService;
    @MockBean(name = "ssoRestTemplate")
    private RestTemplate restTemplate1;
    @MockBean(name = "ssoDirectClientRestTemplate")
    private RestTemplate restTemplate2;

    @Test
    public void loggingIn() throws Exception {
        mockMvc.perform(post("/oauth/token")
                .header("Authorization", "Basic QXJ0ZW1pc1dlYjpuTENud2RJaGl6V2J5a0h5dVpNNlRwUURkN0t3SzlJWERLOExHc2E3U09X")
                .param("grant_type", "password")
                .param("username", "13323454321")
                .param("password", "hly123")
                .param("loginType", "wx")
                .param("wxLogin", "Y"))
                .andDo(print());

        given(dingTalkServiceImpl.loadDingTalkUserByCodeAndCorpId(Mockito.any(),Mockito.any())).willThrow(new UsernameNotFoundException("user.not.found"));
        mockMvc.perform(post("/oauth/token")
                .header("Authorization", "Basic QXJ0ZW1pc1dlYjpuTENud2RJaGl6V2J5a0h5dVpNNlRwUURkN0t3SzlJWERLOExHc2E3U09X")
                .param("grant_type", "password")
                .param("username", "13323454321")
                .param("corpId", "hly123")
                .param("dingTalkLogin", "Y"))
                .andDo(print());

        given(haimaService.loadHaimaUserByClientIdAndCode(Mockito.any(),Mockito.any())).willThrow(new UserNotActivatedException("user.not.bind"));
        mockMvc.perform(post("/oauth/token")
                .header("Authorization", "Basic QXJ0ZW1pc1dlYjpuTENud2RJaGl6V2J5a0h5dVpNNlRwUURkN0t3SzlJWERLOExHc2E3U09X")
                .param("grant_type", "password")
                .param("username", "13323454321")
                .param("tenantId", "hly123")
                .param("loginType", "haima"))
                .andDo(print());
    }

    @Test
    public void loggingSSO() throws Exception {
        mockMvc.perform(post("/oauth/token")
                .header("Authorization", "Basic QXJ0ZW1pc1dlYjpuTENud2RJaGl6V2J5a0h5dVpNNlRwUURkN0t3SzlJWERLOExHc2E3U09X")
                .param("grant_type", "password")
                .param("username", "13323454321")
                .param("password", "hly123")
                .param("loginType", "sso"))
                .andDo(print());

        mockMvc.perform(post("/oauth/token")
                .header("Authorization", "Basic QXJ0ZW1pc1dlYjpuTENud2RJaGl6V2J5a0h5dVpNNlRwUURkN0t3SzlJWERLOExHc2E3U09X")
                .param("grant_type", "password")
                .param("username", "")
                .param("password", "hly123")
                .param("loginType", "ssoDirect"))
                .andDo(print());
    }*/
}