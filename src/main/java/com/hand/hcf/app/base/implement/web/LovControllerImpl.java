package com.hand.hcf.app.base.implement.web;

import com.hand.hcf.app.base.lov.service.LovService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/4/10
 */
@RestController
public class LovControllerImpl {
    @Autowired
    private LovService service;


    public Object getObjectByLovCode(@RequestParam("code") String code,
                                     @RequestParam("id") String id) {
        //return service.getObjectByLovCode(code,id);
        //jiu.zhao TODO
        return null;
    }
}
