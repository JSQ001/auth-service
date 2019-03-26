package com.hand.hcf.app.mdata.location.web;


import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.location.domain.Location;
import com.hand.hcf.app.mdata.location.dto.SolrLocationDTO;
import com.hand.hcf.app.mdata.location.service.LocationDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Ray Ma on 2018/3/26.
 */
@RestController
@RequestMapping("/db")
public class LocationDBController {

    @Autowired
    private LocationDBService locationDBService;

    /**
     * 单个添加供应商别名
     *
     * @return
     */
    @PostMapping(value = "/update/vendor/single")
    public ResponseEntity<List<SolrLocationDTO>> updateDateBaseAndSolr(@RequestBody Location location,
                                                                       @RequestParam(value = "updateSolrIndex",required = false,defaultValue = "true") Boolean updateSolrIndex) {
        List<SolrLocationDTO> solrLocationDTOS = locationDBService.addOrUpdate(location,updateSolrIndex);
        return ResponseEntity.ok(solrLocationDTOS);
    }

    @DeleteMapping(value = "/delete/vendor/single")
    public ResponseEntity<Boolean> deleteVendorSingle(@RequestParam("code") String code,
                                                      @RequestParam("vendorType") String vendorType,
                                                      @RequestParam(value = "deleteSolrIndex",required = false,defaultValue = "true") Boolean deleteSolrIndex) {
        String language= OrgInformationUtil.getCurrentLanguage();
        locationDBService.deleteVendorAliasSingle(code, vendorType, language,deleteSolrIndex);
        return ResponseEntity.ok(Boolean.TRUE);
    }

    @PostMapping(value = "/new/location")
    public ResponseEntity<Location> createLocation(@RequestBody Location location){
        Location result = locationDBService.createLocation(location);
        return ResponseEntity.ok(result);
    }
    @PutMapping(value = "/new/location")
    public ResponseEntity<Location> UpdateLocation(@RequestBody Location location){
        Location result = locationDBService.updateLocation(location);
        return ResponseEntity.ok(result);
    }
}
