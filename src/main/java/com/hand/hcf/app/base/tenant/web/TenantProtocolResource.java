package com.hand.hcf.app.base.tenant.web;


import com.hand.hcf.app.base.tenant.domain.TenantProtocol;
import com.hand.hcf.app.base.tenant.service.TenantProtocolService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/tenant/protocol")
@RestController
public class TenantProtocolResource {

    @Autowired
    private TenantProtocolService tenantProtocolService;

    @RequestMapping(value = "/input", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TenantProtocol> upsertTenantProcotol(@RequestBody TenantProtocol tenantProtocol) {
        tenantProtocol.setTenantId(LoginInformationUtil.getCurrentTenantId());
        TenantProtocol result = tenantProtocolService.inputTenantProtocol(tenantProtocol, LoginInformationUtil.getCurrentUserId());
        return ResponseEntity.ok(result);

    }

    @RequestMapping(value = "/tenant", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TenantProtocol> findOneByTenantId(@RequestParam(value = "tenantId",required = false) Long tenantId) {
       if(tenantId == null){
           tenantId = LoginInformationUtil.getCurrentTenantId();
       }
        TenantProtocol result = tenantProtocolService.findOneByTenantId(tenantId);
        return ResponseEntity.ok(result);

    }

    @RequestMapping(value = "/id", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TenantProtocol> findOneById(@RequestParam(value = "id") Long id) {
        TenantProtocol result = tenantProtocolService.findOneById(id);
        return ResponseEntity.ok(result);
    }






}
