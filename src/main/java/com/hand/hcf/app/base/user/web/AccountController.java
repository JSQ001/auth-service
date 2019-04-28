package com.hand.hcf.app.base.user.web;

import com.hand.hcf.app.base.tenant.domain.TenantProtocol;
import com.hand.hcf.app.base.tenant.service.TenantProtocolService;
import com.hand.hcf.app.base.user.constant.AccountConstants;
import com.hand.hcf.app.base.user.domain.User;
import com.hand.hcf.app.base.user.dto.PasswordRuleDTO;
import com.hand.hcf.app.base.user.dto.UserDTO;
import com.hand.hcf.app.base.user.service.LoginAttemptService;
import com.hand.hcf.app.base.user.service.UserService;
import com.hand.hcf.app.base.userRole.service.UserRoleService;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing the current user's account.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private UserService userService;
    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    TenantProtocolService tenantProtocolService;

    private static final String LOWER_CASE = "isNeedLowercase";

    private static final String UPPER_CASE = "isNeedUppercase";

    private static final String NUMBER = "isNeedNumber";

    private static final String SPECIAL_CHAR = "isNeedSpecialChar";

    private static final String[] PASSWORD_TOPIC_ARRAY = new String[4];

    static {
        PASSWORD_TOPIC_ARRAY[0] = LOWER_CASE;
        PASSWORD_TOPIC_ARRAY[1] = UPPER_CASE;
        PASSWORD_TOPIC_ARRAY[2] = NUMBER;
        PASSWORD_TOPIC_ARRAY[3] = SPECIAL_CHAR;
    }

    /**
     * GET  /account -> get the current user.
     */
    @RequestMapping(value = "/account",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserDTO> getAccount(@RequestHeader(required = false,name="x-helios-client") String client,
                                              @RequestHeader(required = false,name="x-helios-clientVersion") String clientVersion,
                                              @RequestHeader(required = false,name="x-helios-appVersion") String appVersion,
                                              @RequestHeader(required = false,name = "x-native-app", defaultValue = "false") boolean nativeApp) {
        Long userId= LoginInformationUtil.getCurrentUserId();
        if(userId.equals(-1L)){
            return null;
        }
        // 先判断是否存在启用的角色再进行后续处理 20181114
        Boolean aBoolean = false;
        try {
            aBoolean = userRoleService.userHasRole(userId);
        }catch (Exception e){
            throw new BizException("dispatch auth error", e.getMessage());
        }
        if (null == aBoolean || !aBoolean) {
            throw new BizException("user_not_assign_role", "当前用户没有对应角色，请联系系统管理员！");
        }

        //清除当前用户的登录缓存
        loginAttemptService.loginSucceeded(userId);
        UserDTO userDTO=userService.getAccountInfo(appVersion,client,clientVersion, nativeApp);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

}
