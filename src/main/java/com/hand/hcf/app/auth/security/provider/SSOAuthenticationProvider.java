package com.hand.hcf.app.auth.security.provider;

import com.hand.hcf.app.auth.util.DecideAuthenticationServiceExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class SSOAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private RestTemplate restTemplate;
    private UserDetailsService userDetailsService;
    private String ssourl;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)  {
        if (authentication.getPrincipal() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                "AbstractUserDetailsAuthenticationProvider.badCredentials",
                "Bad credentials"));
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)  {
        UserDetails loadedUser;

        try {
            Map details = (Map) authentication.getDetails();
            if (!StringUtils.isEmpty(details.get("loginType")) && details.get("loginType").toString().equals("sso")) {//sso端调用接口
                String url = ssourl + "/sso/getuserinfo?code={code}";
                username = restTemplate.getForObject(url, String.class, username);
                if (StringUtils.isEmpty(username)) {
                    throw new BadCredentialsException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.badCredentials",
                        "Bad credentials"));
                }
                loadedUser = this.getUserDetailsService().loadUserByUsername(username);
            } else {
                throw new UsernameNotFoundException("user.not.found");
            }
        } catch (UsernameNotFoundException notFound) {
            throw notFound;
        } catch (Exception repositoryProblem) {
            return DecideAuthenticationServiceExceptionUtil.decideAuthenticationServiceException(repositoryProblem);
        }

        if (loadedUser == null) {
            throw new AuthenticationServiceException(
                "UserDetailsService returned null, which is an interface contract violation");
        }
        return loadedUser;
    }

    @Autowired
    @Qualifier("ssoDetailService")
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    @Qualifier("ssoOAuth2RestTemplate")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setSsourl(String ssourl) {
        this.ssourl = ssourl;
    }
}
