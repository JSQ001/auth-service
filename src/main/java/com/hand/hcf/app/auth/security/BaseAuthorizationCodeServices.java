

package com.hand.hcf.app.auth.security;

import com.hand.hcf.app.auth.domain.BaseAuthorizationCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Date;

public class BaseAuthorizationCodeServices implements AuthorizationCodeServices {
    private final Logger LOG = LoggerFactory.getLogger(BaseAuthorizationCodeServices.class);

    private static final String DEFAULT_SELECT_STATEMENT = "select authorization_code, authentication from oauth_code where code = ?";
    private static final String DEFAULT_INSERT_STATEMENT = "insert into oauth_code (authentication_id, code, authentication, authorization_code) values (?, ?, ?, ?)";
    private static final String DEFAULT_DELETE_STATEMENT = "delete from oauth_code where code = ?";
    private static final String DEFAULT_CODE_FROM_AUTHENTICATION_SELECT_STATEMENT = "select authorization_code from oauth_code where authentication_id = ?";
    private static final String DEFAULT_Code_AUTHENTICATION_SELECT_STATEMENT = "select authentication from oauth_code where code = ?";

    private String selectAuthenticationSql = DEFAULT_SELECT_STATEMENT;
    private String insertAuthenticationSql = DEFAULT_INSERT_STATEMENT;
    private String deleteAuthenticationSql = DEFAULT_DELETE_STATEMENT;
    private String selectCodeFromAuthenticationSql = DEFAULT_CODE_FROM_AUTHENTICATION_SELECT_STATEMENT;
    private String selectCodeAuthenticationSql = DEFAULT_Code_AUTHENTICATION_SELECT_STATEMENT;

    private int codeValiditySeconds = 10 * 60; // default 10 m.

    private final JdbcTemplate jdbcTemplate;

    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
    private RandomValueStringGenerator generator = new RandomValueStringGenerator();

    public BaseAuthorizationCodeServices(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource required");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public String createAuthorizationCode(OAuth2Authentication authentication) {
        BaseAuthorizationCode authCode = getAuthCode(authentication);
        if (authCode != null) {
            if (authCode.isExpired()) {
                deleteCode(authCode.getValue());
            } else {
                authCode.setExpiration(new Date(System.currentTimeMillis() + (codeValiditySeconds * 1000L)));
                store(authCode, authentication);
                return authCode.getValue();
            }
        }

        String code = generator.generate();
        authCode = new BaseAuthorizationCode(code, new Date(System.currentTimeMillis() + (codeValiditySeconds * 1000L)));
        store(authCode, authentication);
        return code;
    }


    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code)
        throws InvalidGrantException {
        OAuth2Authentication auth = remove(code);
        if (auth == null) {
            throw new InvalidGrantException("Invalid authorization code: " + code);
        }
        return auth;
    }

    private void store(BaseAuthorizationCode code, OAuth2Authentication authentication) {
        if (readAuthentication(code.getValue()) != null) {
            deleteCode(code.getValue());
        }
        jdbcTemplate.update(insertAuthenticationSql,
            new Object[]{authenticationKeyGenerator.extractKey(authentication), code.getValue(), new SqlLobValue(SerializationUtils.serialize(authentication)), new SqlLobValue(SerializationUtils.serialize(code))}, new int[]{
                Types.VARCHAR, Types.VARCHAR, Types.BLOB, Types.BLOB});
    }

    private OAuth2Authentication remove(String code) {
        BaseAuthorizationCode authCode;

        try {
            authCode = jdbcTemplate.queryForObject(selectAuthenticationSql,
                    (rs, rowNum) -> SerializationUtils.deserialize(rs.getBytes("authorization_code")), code);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        if (authCode.isExpired()) {
            deleteCode(authCode.getValue());
            return null;
        }
        return deleteCode(code);
    }

    public OAuth2Authentication deleteCode(String code) {
        OAuth2Authentication authentication;

        try {
            authentication = jdbcTemplate.queryForObject(selectAuthenticationSql,
                    (rs, rowNum) -> SerializationUtils.deserialize(rs.getBytes("authentication")), code);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        if (authentication != null) {
            jdbcTemplate.update(deleteAuthenticationSql, code);
        }

        return authentication;
    }

    public BaseAuthorizationCode getAuthCode(OAuth2Authentication authentication) {
        BaseAuthorizationCode code = null;

        String key = authenticationKeyGenerator.extractKey(authentication);
        try {
            code = jdbcTemplate.queryForObject(selectCodeFromAuthenticationSql,
                    (rs, rowNum) -> deserializeAuthCode(rs.getBytes(1)), key);
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failed to find Code for authentication " + authentication);
            }
        } catch (IllegalArgumentException e) {
            LOG.error("Could not extract Code for authentication " + authentication, e);
        }

        if (code != null
            && !key.equals(authenticationKeyGenerator.extractKey(readAuthentication(code.getValue())))) {
            deleteCode(code.getValue());
            // Keep the store consistent (maybe the same user is represented by this authentication but the details have
            // changed)
            store(code, authentication);
        }
        return code;
    }

    public OAuth2Authentication readAuthentication(String code) {
        OAuth2Authentication authentication = null;

        try {
            authentication = jdbcTemplate.queryForObject(selectCodeAuthenticationSql,
                    (rs, rowNum) -> deserializeAuthentication(rs.getBytes(1)), code);
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Failed to find access token for token " + code);
            }
        } catch (IllegalArgumentException e) {
            LOG.warn("Failed to deserialize authentication for " + code, e);
            deleteCode(code);
        }

        return authentication;
    }

    protected BaseAuthorizationCode deserializeAuthCode(byte[] code) {
        return SerializationUtils.deserialize(code);
    }

    protected OAuth2Authentication deserializeAuthentication(byte[] authentication) {
        return SerializationUtils.deserialize(authentication);
    }

}
