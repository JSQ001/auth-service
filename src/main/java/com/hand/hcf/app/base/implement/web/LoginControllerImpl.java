package com.hand.hcf.app.base.implement.web;

import com.hand.hcf.app.base.system.UserRequestCO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LoginControllerImpl {

    public void loginFailed(@RequestBody UserRequestCO requestCO) {
    }

}

