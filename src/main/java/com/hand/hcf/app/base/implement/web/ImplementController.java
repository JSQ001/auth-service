package com.hand.hcf.app.base.implement.web;

import com.hand.hcf.app.base.domain.DataAuthTableProperty;
import com.hand.hcf.app.base.service.DataAuthTablePropertyService;
import com.hand.hcf.app.base.service.DataAuthorityService;
import com.hand.hcf.app.base.service.UserRoleService;
import com.hand.hcf.app.client.auth.AuthInterface;
import com.hand.hcf.app.client.auth.DataAuthTablePropertyDTO;
import com.hand.hcf.core.security.AuthoritiesConstants;
import com.hand.hcf.core.web.dto.DataAuthValuePropertyDTO;
import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/29 18:11
 * @remark 第三方接口
 */
@RestController
//@RequestMapping(value = "/api/implement")
//@PreAuthorize("hasRole('" + AuthoritiesConstants.INTEGRATION_CLIENTS + "')")
@AllArgsConstructor
public class ImplementController implements AuthInterface {

    private final MapperFacade mapper;
    private final DataAuthTablePropertyService dataAuthTablePropertyService;
    private final DataAuthorityService dataAuthorityService;
    private final UserRoleService userRoleService;

    @Override
    //@GetMapping(value = "/data/auth/table/properties/get/by/tableName")
    public List<DataAuthTablePropertyDTO> getDataAuthTablePropertiesByTableName(@RequestParam("tableName") String tableName) {
        List<DataAuthTableProperty> dataAuthTablePropertiesByTableName = dataAuthTablePropertyService.getDataAuthTablePropertiesByTableName(tableName);
        return mapper.mapAsList(dataAuthTablePropertiesByTableName, DataAuthTablePropertyDTO.class);
    }

    @Override
    //@GetMapping(value = "/data/auth/value/properties/get/by/request")
    public List<Map<String, List<DataAuthValuePropertyDTO>>> getDataAuthValuePropertiesByRequest() {
        return dataAuthorityService.getDataAuthValuePropertiesByRequest();
    }

    @Override
    //@GetMapping(value = "/user/has/role")
    public Boolean userHasRole(@RequestParam("userId")Long userId) {
        return userRoleService.userHasRole(userId);
    }
}
