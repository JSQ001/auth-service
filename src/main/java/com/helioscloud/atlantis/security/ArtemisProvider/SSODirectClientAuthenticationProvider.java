package com.helioscloud.atlantis.security.ArtemisProvider;

import com.helioscloud.atlantis.exception.ThirdpartyConnectionError;
import com.helioscloud.atlantis.exception.ThirdpartyValidationError;
import com.helioscloud.atlantis.util.DecideAuthenticationServiceExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class SSODirectClientAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private RestTemplate restTemplate;
    private UserDetailsService userDetailsService;
    private String ssourl;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getPrincipal() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                "AbstractUserDetailsAuthenticationProvider.badCredentials",
                "Bad credentials"));
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        UserDetails loadedUser;
        Object clientName = null;
        try {
            Map details = (Map) authentication.getDetails();
            if (!StringUtils.isEmpty(details.get("loginType")) && details.get("loginType").toString().equals("ssoDirect")) {
                String url = ssourl + "/sso/getuserinfoByDirectClient?client_name={client_name}&username={username}&password={password}";
                clientName = details.get("client_name");
                username = restTemplate.getForObject(url, String.class, clientName, username, authentication.getCredentials());
                if (StringUtils.isEmpty(username)) {
                    throw new ThirdpartyValidationError("corp.user.invalid");
                }
                loadedUser = this.getUserDetailsService().loadUserByUsername(username);
            } else {
                throw new UsernameNotFoundException("user.not.found");
            }
        } catch (OAuth2Exception e) {
            throw e;
        } catch (HttpServerErrorException e) {
            throw new ThirdpartyConnectionError("corp.connection.fail");
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
