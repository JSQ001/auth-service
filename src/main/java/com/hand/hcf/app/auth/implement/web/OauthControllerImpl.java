package com.hand.hcf.app.auth.implement.web;

import com.hand.hcf.app.auth.dto.ClientDTO;
import com.hand.hcf.app.auth.service.OauthService;
import com.hand.hcf.app.client.oauth.ClientCO;
import com.hand.hcf.app.client.oauth.OauthInterface;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
public class OauthControllerImpl implements OauthInterface {

    @Autowired
    private OauthService oauthService;

    @Autowired
    private MapperFacade mapper;

    @Override
    public void updateOauthAccessTokenCompanyByLogin(@RequestParam(value = "login") String login,
                                                     @RequestParam(value = "companyId") Long companyId,
                                                     @RequestParam(value = "companyOid") String companyOid) {
        oauthService.updateOauthAccessTokenCompanyByLogin(login, companyId, UUID.fromString(companyOid));
    }

    @Override
    public void deleteOauthAccessTokenByLogin(String login) {
        oauthService.deleteOauthAccessToken(login);
    }

    @Override
    public List<ClientCO> listCompanyClientByCompanyOid(String companyOid) {
        List<ClientDTO> clientDTOList = oauthService.listCompanyClient(UUID.fromString(companyOid));
        List<ClientCO> clientCOList = new ArrayList<>();
        clientDTOList.stream().forEach(e -> {
            ClientCO clientCO = mapper.map(e, ClientCO.class);
            clientCOList.add(clientCO);
        });
        return clientCOList;
    }

    @Override
    public List<ClientCO> listTenantClientByTenantId(Long tenantId) {
        List<ClientDTO> clientDTOList = oauthService.listTenantClient(tenantId);
        List<ClientCO> clientCOList = new ArrayList<>();
        clientDTOList.stream().forEach(e -> {
            ClientCO clientCO = mapper.map(e, ClientCO.class);
            clientCOList.add(clientCO);
        });
        return clientCOList;
    }
}
