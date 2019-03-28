package com.hand.hcf.app.mdata.supplier.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.dto.VendorTypeCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.supplier.service.VendorTypeService;
import com.hand.hcf.core.util.PaginationUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/11 11:15
 */
@RestController
@RequestMapping("/api/ven/type")
public class VendorTypeController {

    private VendorTypeService vendorTypeService;

    public VendorTypeController(VendorTypeService vendorTypeService) {
        this.vendorTypeService = vendorTypeService;
    }

    /**
     * 分页获取供应商类型信息
     *
     * @param code
     * @param name
     * @param isEnabled
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/query")
    public ResponseEntity<List<VendorTypeCO>> searchVendorTypes(@RequestParam(value = "code", required = false) String code,
                                                                @RequestParam(value = "name", required = false) String name,
                                                                @RequestParam(value = "enabled", required = false) Boolean isEnabled,
                                                                Pageable pageable) throws URISyntaxException {
        Page<VendorTypeCO> page = vendorTypeService.searchVendorTypes(code, name, isEnabled, Long.valueOf(OrgInformationUtil.getCurrentTenantId()), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ven/type/query");
        return new ResponseEntity<>(page.getRecords(), headers, HttpStatus.OK);
    }

    @PostMapping
//    @PreAuthorize("hasRole('" + AuthoritiesConstants.ROLE_TENANT_ADMIN + "')")
    public ResponseEntity<VendorTypeCO> createVendorType(@RequestBody VendorTypeCO vendorTypeCO,
                                                         @RequestParam(value = "roleType", required = false) String roleType) throws URISyntaxException {
        return ResponseEntity.ok(vendorTypeService.insertOrUpDateVendorType(vendorTypeCO, roleType));
    }

    @PutMapping
//    @PreAuthorize("hasRole('" + AuthoritiesConstants.ROLE_TENANT_ADMIN + "')")
    public ResponseEntity<VendorTypeCO> updateVendorType(@RequestBody VendorTypeCO vendorTypeCO,
                                                         @RequestParam(value = "roleType", required = false) String roleType) throws URISyntaxException {
        return ResponseEntity.ok(vendorTypeService.insertOrUpDateVendorType(vendorTypeCO, roleType));
    }
}
