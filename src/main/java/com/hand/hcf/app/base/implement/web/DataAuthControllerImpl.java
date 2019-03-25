package com.hand.hcf.app.base.implement.web;

import com.hand.hcf.app.base.dataAuthority.adapter.DataAuthTablePropertyAdapter;
import com.hand.hcf.app.base.dataAuthority.domain.DataAuthTableProperty;
import com.hand.hcf.app.base.dataAuthority.service.DataAuthTablePropertyService;
import com.hand.hcf.app.base.dataAuthority.service.DataAuthorityService;
import com.hand.hcf.app.base.userRole.service.UserRoleService;
import com.hand.hcf.app.base.system.DataAuthTablePropertyCO;
import com.hand.hcf.core.web.dto.DataAuthValuePropertyDTO;
import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/29 18:11
 * @remark 第三方接口
 */
@AllArgsConstructor
@RestController
public class DataAuthControllerImpl {

    private final MapperFacade mapper;
    private final DataAuthTablePropertyService dataAuthTablePropertyService;
    private final DataAuthorityService dataAuthorityService;
    private final UserRoleService userRoleService;

    //@GetMapping(value = "/data/auth/table/properties/get/by/tableName")
    public List<DataAuthTablePropertyCO> getDataAuthTablePropertiesByTableName(@RequestParam("tableName") String tableName) {
        List<DataAuthTableProperty> dataAuthTablePropertiesByTableName = dataAuthTablePropertyService.getDataAuthTablePropertiesByTableName(tableName);
        return DataAuthTablePropertyAdapter.toDTO(dataAuthTablePropertiesByTableName);
    }

    //@GetMapping(value = "/data/auth/value/properties/get/by/request")
    public List<Map<String, List<DataAuthValuePropertyDTO>>> getDataAuthValuePropertiesByRequest() {
        return dataAuthorityService.getDataAuthValuePropertiesByRequest();
    }

    //@GetMapping(value = "/user/has/role")
    public Boolean userHasRole(@RequestParam("userId")Long userId) {
        return userRoleService.userHasRole(userId);
    }

}
