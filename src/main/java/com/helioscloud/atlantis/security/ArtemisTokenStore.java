/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cloudhelios.atlantis.security.domain.PrincipalLite;
import com.helioscloud.atlantis.constant.UserLoginLogConstant;
import com.helioscloud.atlantis.domain.UserDevice;
import com.helioscloud.atlantis.domain.UserLoginLog;
import com.helioscloud.atlantis.domain.enumeration.DeviceStatusEnum;
import com.helioscloud.atlantis.domain.enumeration.DeviceVerificationStatus;
import com.helioscloud.atlantis.dto.UserDTO;
import com.helioscloud.atlantis.persistence.UserDeviceMapper;
import com.helioscloud.atlantis.service.UserService;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.UUID;

import static com.helioscloud.atlantis.constant.ElasticSearchConstants.DEFAULT_INDEX_TYPE;
import static com.helioscloud.atlantis.constant.ElasticSearchConstants.USER_LOGIN_LOG_INDEX;

/**
 * Created by markfredchen on 16/4/2.
 */
public class ArtemisTokenStore extends JdbcTokenStore {
    private static final Logger LOG = LoggerFactory.getLogger(ArtemisTokenStore.class);
    private static final String DEFAULT_ACCESS_TOKEN_FROM_AUTHENTICATION_SELECT_STATEMENT = "select token_id, token from oauth_access_token where authentication_id = ?";
    private static final String DEFAULT_ACCESS_TOKEN_SELECT_STATEMENT = "/*FORCE_MASTER*/select token_id, token from oauth_access_token where token_id = ?";//强制使用读写库
    private static final String DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_STATEMENT = "/*FORCE_MASTER*/select token_id, authentication from oauth_access_token where token_id = ?";//强制使用读写库
    private String selectAccessTokenSql = DEFAULT_ACCESS_TOKEN_SELECT_STATEMENT;
    private String selectAccessTokenAuthenticationSql = DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_STATEMENT;
    private final JdbcTemplate jdbcTemplate;
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
    @Autowired
    private UserService userService;
    @Autowired
    private UserDeviceMapper userDeviceMapper;
    @Autowired
    private TransportClient client;

    public ArtemisTokenStore(DataSource dataSource) {
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
        UserDevice paramUserDevice = new UserDevice();
        if (!StringUtils.hasText(deviceId)) {
            isValidate = true;
        } else {
            paramUserDevice = parseUserDevice(authentication);
            UserDevice userDevice = new UserDevice();
            userDevice.setUserOID(paramUserDevice.getUserOID());
            userDevice.setDeviceID(paramUserDevice.getDeviceID());
            userDevice = userDeviceMapper.selectOne(userDevice);
            if (userDevice != null) {
                isValidate = DeviceStatusEnum.NOMAL.getID().equals(userDevice.getStatus());
                try {
                    userDevice.setOsVersion(paramUserDevice.getOsVersion());
                    userDevice.setAppVersion(paramUserDevice.getAppVersion());
                    userDeviceMapper.updateById(userDevice);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                userDeviceMapper.insert(paramUserDevice);
            }
            UserDTO user = userService.findOneByUserOID(getUserOID(authentication));
            // 新版 APP 默认开启，只有设置为close时才不需要设备验证
            if (DeviceVerificationStatus.CLOSED.name().equals(user.getDeviceVerificationStatus())) {
                isValidate = true;
            }
            token.getAdditionalInformation().put("email", user.getEmail());
            token.getAdditionalInformation().put("mobile", user.getMobile());
        }
        token.getAdditionalInformation().put("isDeviceValidate", isValidate);
        super.storeAccessToken(token, authentication);
        //保存登录记录
        String login = param.get("username");
        if (StringUtils.hasText(login)) {
            UserLoginLog userLoginLog = new UserLoginLog();
            BeanUtils.copyProperties(paramUserDevice, userLoginLog, "id", "status");
            userLoginLog.setDeviceId(deviceId);
            userLoginLog.setLogin(login);
            userLoginLog.setLoginType(param.get("loginType"));
            userLoginLog.setCreatedDate(DateTime.now());
            if (isValidate) {
                userLoginLog.setStatus(UserLoginLogConstant.USER_LOGIN_STATUS_SUCCESS);
            } else {
                userLoginLog.setStatus(UserLoginLogConstant.USER_LOGIN_STATUS_FAIL);
            }
            try {
                IndexResponse response = client.prepareIndex(USER_LOGIN_LOG_INDEX, DEFAULT_INDEX_TYPE)
                        .setSource(new ObjectMapper().writeValueAsString(userLoginLog), XContentType.JSON)
                        .get();
                LOG.debug("Elasticsearch index [{}], status {}", userLoginLog, response.status());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    private UserDevice parseUserDevice(OAuth2Authentication authentication) {
        Map<String, String> param = authentication.getOAuth2Request().getRequestParameters();
        UserDevice userDevice = new UserDevice();
        userDevice.setDeviceID(param.get("deviceId"));
        userDevice.setUserOID(getUserOID(authentication));
        if (StringUtils.hasText(param.get("vendorTypeID"))) {
            userDevice.setVendorTypeID(Integer.parseInt(param.get("vendorTypeID")));
        }
        if (StringUtils.hasText(param.get("platformID"))) {
            userDevice.setPlatformID(Integer.parseInt(param.get("platformID")));
        }
        userDevice.setOsVersion(param.get("osVersion"));
        userDevice.setAppVersion(param.get("appVersion"));
        userDevice.setPixelRatio(param.get("pixelRatio"));
        userDevice.setDeviceBrand(param.get("deviceBrand"));
        userDevice.setDeviceModel(param.get("deviceModel"));
        userDevice.setDeviceName(param.get("deviceName"));
        userDevice.setStatus(DeviceStatusEnum.UNVALIDATED.getID());
        return userDevice;
    }

    private UUID getUserOID(OAuth2Authentication authentication) {
        return ((PrincipalLite) authentication.getUserAuthentication().getPrincipal()).getUserOID();
    }

    boolean getIsValidatedDevice(String accessTokenValue) {
        OAuth2AccessToken accessToken = readAccessToken(accessTokenValue);
        if (accessToken != null && accessToken.getAdditionalInformation().containsKey("isDeviceValidate")) {
            return (boolean) accessToken.getAdditionalInformation().get("isDeviceValidate");
        }
        return true;
    }
}
