package com.helioscloud.atlantis.web;

import com.helioscloud.atlantis.domain.UserDevice;
import com.helioscloud.atlantis.service.UserDeviceService;
import com.helioscloud.atlantis.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author create by wang quanxi.
 */

@RestController
@RequestMapping("/api/userdevice")
public class UserDeviceResource {

    private final Logger log = LoggerFactory.getLogger(UserDeviceResource.class);

    private UserDeviceService userDeviceService;

    @Autowired
    public UserDeviceResource(UserDeviceService userDeviceService) {
        this.userDeviceService = userDeviceService;
    }

    @RequestMapping(value = "/my",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDevice>> findByUserOID() {
        return ResponseEntity.ok(userDeviceService.findByUserOID(SecurityUtils.getCurrentUserOID()));
    }

    @RequestMapping(
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDevice> updateRemark(@RequestBody UserDevice userDevice) {
        log.debug("REST request to update UserDevice : {}", userDevice);
        userDevice = userDeviceService.updateRemark(userDevice);
        return ResponseEntity.ok().body(userDevice);
    }
    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDevice> deleteDevice(@PathVariable("id") Long id) {
        log.debug("REST request to delete UserDevice : {}", id);
        userDeviceService.delete(id);
        return ResponseEntity.ok().build();
    }
}

