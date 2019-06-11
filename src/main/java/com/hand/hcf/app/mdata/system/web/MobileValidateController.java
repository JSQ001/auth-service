package com.hand.hcf.app.mdata.system.web;

import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.mdata.system.domain.MobileValidate;
import com.hand.hcf.app.mdata.system.service.MobileValidateService;
import com.hand.hcf.app.mdata.utils.HeaderUtil;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * REST controller for managing MobileValidate.
 */
@Slf4j
@RestController
@RequestMapping("/api/mobilevalidate")
public class MobileValidateController {


    @Autowired
    private MobileValidateService mobileValidateService;

    @RequestMapping(method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MobileValidate> createMobileValidate(@RequestBody MobileValidate mobileValidate) throws URISyntaxException {
        log.debug("REST request to save mobileValidate : {}", mobileValidate);
        if (mobileValidate.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("mobilevalidate", "idexists", "A new contact cannot already have an ID")).body(null);
        }
        MobileValidate result = mobileValidateService.save(mobileValidate);
        return ResponseEntity.created(new URI("/api/mobilevalidate/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("mobilevalidate", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /contacts -> Updates an existing MobileValidate.
     */
    @RequestMapping(method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MobileValidate> updateMobileValidate(@RequestBody MobileValidate mobileValidate) throws URISyntaxException {
        log.debug("REST request to update mobileValidate : {}", mobileValidate);
        if (mobileValidate.getId() == null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("mobilevalidate", "id.not.exists", "Id or mobileValidateOID is required")).body(null);
        }
        MobileValidate result = mobileValidateService.update(mobileValidate);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("mobilevalidate", result.getId().toString()))
            .body(result);
    }

    /**
     * GET  /contacts/:id -> get the "id" MobileValidate.
     */
    @RequestMapping(value = "/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MobileValidate> getMobileValidate(@PathVariable Long id) {
        log.debug("REST request to get Contact : {}", id);
        MobileValidate result = mobileValidateService.findOne(id);
        if(result != null){
            return  new ResponseEntity<>(result,HttpStatus.OK);
        }else{
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * DELETE  /contacts/:id -> delete the "id" MobileValidate.
     */
    @RequestMapping(value = "/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteMobileValidate(@PathVariable Long id) {
        log.debug("REST request to enable MobileValidate : {}", id);
        mobileValidateService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("mobilevalidate", id.toString())).build();
    }

    @RequestMapping(value = "/list",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<MobileValidate>> getMobileValidateList(@RequestParam(name = "isEnabled",required = false) Boolean isEnabled) {
        String language = LoginInformationUtil.getCurrentLanguage();
        List<MobileValidate> result = mobileValidateService.findAll(isEnabled,language);
        return  new ResponseEntity<>(result,HttpStatus.OK);
    }
}
