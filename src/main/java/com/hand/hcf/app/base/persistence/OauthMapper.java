package com.hand.hcf.app.base.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.dto.ClientDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OauthMapper extends BaseMapper<ClientDTO> {

    List<BaseClientDetails> getClientDetailWithAddition();

    void updateClientTenantId(@Param("clientId") String clientId,
                              @Param("additionalInformation") String additionalInformation);

    void deleteAccessToken(@Param("clientId") String clientId);

    void updateOauthAccessTokenById(Map<String, Object> map);

    List<String> findWebAuthenticationIdByLogin(String login);

    void deleteWebOauthTokenByLogin(String login);

    List<String> findAuthenticationIdByLogin(String login);


    List<String> findRefreshByLogin(String login);


    void deleteOauthTokenByLogin(String login);


    void deleteRefreshByToken(String tokenId);

    List<ClientDTO> getCompanyClient(@Param("companyOid") UUID companyOid);

    List<ClientDTO> getTenantClient(@Param("tenantId") Long tenantId);
}
