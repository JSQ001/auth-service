package com.helioscloud.atlantis.security.ArtemisProvider;

import com.helioscloud.atlantis.AuthServerConfigTest;
import com.helioscloud.atlantis.config.AuthServerConfiguration;
import com.helioscloud.atlantis.config.ResourceServerConfiguration;
import com.helioscloud.atlantis.exception.UserNotActivatedException;
import com.helioscloud.atlantis.service.DingTalkServiceImpl;
import com.helioscloud.atlantis.service.HaimaService;
import com.helioscloud.atlantis.service.UserService;
import com.helioscloud.atlantis.service.WxService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {AuthServerConfigTest.class, AuthServerConfiguration.class, ResourceServerConfiguration.class})
@ActiveProfiles("control")
public class ArtemisAuthenticationProviderTest {

    @Autowired
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
    }
}