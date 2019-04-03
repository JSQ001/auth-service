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
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.exception.core.ValidationError;
import com.hand.hcf.core.exception.core.ValidationException;
import com.hand.hcf.core.util.LoginInformationUtil;
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

    /**
     * 修改当前登录用户的密码
     *
     * @param oldPassword
     * @param newPassword
     */
    /**
     * @api {post} /api/refactor//account/change_password 修改用户当前登录密码
     * @apiGroup Account
     * @apiVersion 0.1.0
     * @apiParam {String} oldPassword 老密码
     * @apiParam {String} newPassword 新密码
     */
    @RequestMapping(value = "/refactor/account/change_password",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@RequestParam(value = "oldPassword",required = false) String oldPassword, @RequestParam(value = "newPassword") String newPassword) {
        User user = userService.getByUserOid(LoginInformationUtil.getCurrentUserOid());
        if (user == null) {
            throw new ValidationException(new ValidationError("user", "user.not.found"));
        }

        userService.changePasswordCheckNew(user.getTenantId(), user.getUserOid(), newPassword);
        userService.changePasswordNew(user, oldPassword, newPassword);
    }


    /**
     * @api {get} /api/refactor/password/rule/topic 获取用户密码规则
     * @apiGroup Account
     * @apiVersion 0.1.0
     * @apiParam {String} username 手机号码
     * @apiSuccessExample {json} Success-Result
     * {
     * "isNeedUppercase": false,
     * "isNeedNumber": true,
     * "isNeedLowercase": true,
     * "minLength": 6,
     * "isNeedSpecialChar": false,
     * "maxLength": 32
     * }
     */
    @RequestMapping(value = "/refactor/password/rule/topic",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<Map<String, Object>> getPasswordRuleTopic(@RequestParam(value = "username") String username) {

        log.info("start to get password rule topic and loginName={}", username);
        Optional<User> user = userService.getByMobile(username);
        if (!user.isPresent()) {
            user = userService.getByEmail(username);
        }

        if (!user.isPresent()) {
            throw new ValidationException(new ValidationError("user", "user.not.found"));
        }

        User userEntity = user.get();
        PasswordRuleDTO passwordRuleDTO=userService.getPasswordRule(userEntity.getTenantId());
        int lengthMin = AccountConstants.DEFAULT_PASSWORD_LENGTH_MIN;
        int lengthMax = AccountConstants.DEFAULT_PASSWORD_LENGTH_MAX;
        String passwordRule = AccountConstants.DEFAULT_PASSWORD_RULE;
        int unLockMinutes = 0;
        if (passwordRuleDTO != null) {
            lengthMin = passwordRuleDTO.getPasswordLengthMin();
            lengthMax = passwordRuleDTO.getPasswordLengthMax();
            passwordRule = passwordRuleDTO.getPasswordRule();

        }

        Map<String, Object> returnMap = new HashMap<>();
        String[] passwordRuleArray = passwordRule.split("");
        //密码长度规则
        returnMap.put("minLength", lengthMin);
        returnMap.put("maxLength", lengthMax);
        returnMap.put("unLockMinutes",unLockMinutes);

        //密码内容规则（小写 大写 数字 特殊字符）
        for (int i = 0; i < passwordRuleArray.length; i++) {
            if ("1".equals(passwordRuleArray[i])) {
                returnMap.put(PASSWORD_TOPIC_ARRAY[i], true);
            } else {
                returnMap.put(PASSWORD_TOPIC_ARRAY[i], false);
            }
        }
        TenantProtocol tenantProtocol = tenantProtocolService.findOneByTenantId(userEntity.getTenantId());
        boolean tenantprotocolEnable = tenantProtocol != null && tenantProtocol.getEnabled();
        returnMap.put("tenantProtocol",tenantprotocolEnable);
        returnMap.put("tenantOnly",tenantProtocol != null && tenantProtocol.getTenantOnly());
        if(tenantprotocolEnable){
            returnMap.put("tenantProtocolTitle",tenantProtocol.getI18n().get("title"));
            returnMap.put("tenantProtocolId",tenantProtocol.getId().toString());
        }
        return ResponseEntity.ok().body(returnMap);
    }
}
