package com.helioscloud.atlantis.implement.web;

import com.cloudhelios.atlantis.client.dto.auth.DataAuthTablePropertyDTO;
import com.cloudhelios.atlantis.security.AuthoritiesConstants;
import com.cloudhelios.atlantis.service.api.AuthInterface;
import com.cloudhelios.atlantis.web.dto.DataAuthValuePropertyDTO;
import com.helioscloud.atlantis.domain.DataAuthTableProperty;
import com.helioscloud.atlantis.service.DataAuthTablePropertyService;
import com.helioscloud.atlantis.service.DataAuthorityService;
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
@RequestMapping(value = "/api/implement")
@PreAuthorize("hasRole('" + AuthoritiesConstants.INTEGRATION_CLIENTS + "')")
@AllArgsConstructor
public class ImplementController implements AuthInterface{

    private final MapperFacade mapper;
    private final DataAuthTablePropertyService dataAuthTablePropertyService;
    private final DataAuthorityService dataAuthorityService;

    @Override
    @GetMapping(value = "/data/auth/table/properties/get/by/tableName")
    public List<DataAuthTablePropertyDTO> getDataAuthTablePropertiesByTableName(@RequestParam String tableName) {
        List<DataAuthTableProperty> dataAuthTablePropertiesByTableName = dataAuthTablePropertyService.getDataAuthTablePropertiesByTableName(tableName);
        return mapper.mapAsList(dataAuthTablePropertiesByTableName, DataAuthTablePropertyDTO.class);
    }

    @Override
    @GetMapping(value = "/data/auth/value/properties/get/by/request")
    public List<Map<String, List<DataAuthValuePropertyDTO>>> getDataAuthValuePropertiesByRequest() {
        return dataAuthorityService.getDataAuthValuePropertiesByRequest();
    }
}
