package com.hand.hcf.app.mdata.location.web;

import com.hand.hcf.app.mdata.location.domain.Location;
import com.hand.hcf.app.mdata.location.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 18:31 2018/5/17
 * @Modified by
 */
@RestController
@RequestMapping("/api")
public class TestControllor {
    @Autowired
    LocationService locationService;

    @GetMapping("/code")
    public ResponseEntity<Location> getCode(@RequestParam String code){
        return ResponseEntity.ok(locationService.findOneByCode(code));
    }

}
