

package com.hand.hcf.app.base.security;

import com.hand.hcf.core.util.LoginInformationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by markfredchen on 16/4/2.
 */
public class BaseTokenStore extends JdbcTokenStore {
    private static final Logger LOG = LoggerFactory.getLogger(BaseTokenStore.class);
    private static final String DEFAULT_ACCESS_TOKEN_FROM_AUTHENTICATION_SELECT_STATEMENT = "select token_id, token from oauth_access_token where authentication_id = ?";
    private static final String DEFAULT_ACCESS_TOKEN_SELECT_STATEMENT = "/*FORCE_MASTER*/select token_id, token from oauth_access_token where token_id = ?";//强制使用读写库
    private static final String DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_STATEMENT = "/*FORCE_MASTER*/select token_id, authentication from oauth_access_token where token_id = ?";//强制使用读写库
    private String selectAccessTokenSql = DEFAULT_ACCESS_TOKEN_SELECT_STATEMENT;
    private String selectAccessTokenAuthenticationSql = DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_STATEMENT;
    private final JdbcTemplate jdbcTemplate;
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    public BaseTokenStore(DataSource dataSource) {
        super(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
        this.authenticationKeyGenerator = authenticationKeyGenerator;
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        OAuth2AccessToken accessToken = null;

        String key = authenticationKeyGenerator.extractKey(authentication);
        try {
            accessToken = jdbcTemplate.queryForObject(DEFAULT_ACCESS_TOKEN_FROM_AUTHENTICATION_SELECT_STATEMENT,
                    (rs, rowNum) -> deserializeAccessToken(rs.getBytes(2)), key);
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failed to find access token for authentication " + authentication);
            }
        } catch (IllegalArgumentException e) {
            LOG.error("Could not extract access token for authentication " + authentication, e);
        }

        if (accessToken != null
                && (readAuthentication(accessToken.getValue()) == null || !key.equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue()))))) {
            removeAccessToken(accessToken.getValue());
            // Keep the store consistent (maybe the same user is represented by this authentication but the details have
            // changed)
            storeAccessToken(accessToken, authentication);
        }
        return accessToken;
    }

    @Override
    public void removeAccessToken(String token) {
        super.removeAccessToken(token);
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        OAuth2AccessToken accessToken = null;

        try {
            accessToken = jdbcTemplate.queryForObject(selectAccessTokenSql,
                    (rs, rowNum) -> deserializeAccessToken(rs.getBytes(2)), extractTokenKey(tokenValue));
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Failed to find access token for token " + tokenValue);
            }
        } catch (IllegalArgumentException e) {
            LOG.warn("Failed to deserialize access token for " + tokenValue, e);
            removeAccessToken(tokenValue);
        }

        return accessToken;
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        OAuth2Authentication authentication = null;

        try {
            authentication = jdbcTemplate.queryForObject(selectAccessTokenAuthenticationSql,
                    (rs, rowNum) -> deserializeAuthentication(rs.getBytes(2)), extractTokenKey(token));
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Failed to find access token for token " + token);
            }
        } catch (IllegalArgumentException e) {
            LOG.warn("Failed to deserialize authentication for " + token, e);
            removeAccessToken(token);
        }

        return authentication;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        Map<String, String> param = authentication.getOAuth2Request().getRequestParameters();
        boolean isValidate = false;
        String deviceId = param.get("deviceId");
        if (!StringUtils.hasText(deviceId)) {
            isValidate = true;
        } else {
            token.getAdditionalInformation().put("email", LoginInformationUtil.getUser().getEmail());
            token.getAdditionalInformation().put("mobile", LoginInformationUtil.getUser().getMobile());
        }
       // isValidate = authClient.logLogin(param);
        token.getAdditionalInformation().put("isDeviceValidate", isValidate);
        super.storeAccessToken(token, authentication);
        //保存登录记录

    }

    boolean getIsValidatedDevice(String accessTokenValue) {
        OAuth2AccessToken accessToken = readAccessToken(accessTokenValue);
        if (accessToken != null && accessToken.getAdditionalInformation().containsKey("isDeviceValidate")) {
            return (boolean) accessToken.getAdditionalInformation().get("isDeviceValidate");
        }
        return true;
    }
}
