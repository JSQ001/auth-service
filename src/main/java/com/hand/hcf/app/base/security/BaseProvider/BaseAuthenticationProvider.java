package com.hand.hcf.app.base.security.BaseProvider;

import com.alibaba.fastjson.JSONObject;
import com.hand.hcf.app.base.service.*;
import com.hand.hcf.app.base.util.DecideAuthenticationServiceExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Created by Yuvia on 2017/2/27.
 */
public class BaseAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
   // private SaltSource saltSource;

    @Autowired
    private WxService wxService;

    //    private IDingTalkDetailService dingTalkDetailService;
    @Autowired
    private DingTalkServiceImpl dingTalkService;

    @Autowired
    private HaimaService haimaService;

    @Autowired
    private UELoginUserDetailService ueLoginUserDetailService;

    @Autowired
    private AppDetailService appDetailService;

    // ~ Methods
    // ========================================================================================================
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

        if (authentication.getCredentials() == null && authentication.getPrincipal() == null) {
            logger.debug("Authentication failed: no credentials provided");
            throw new BadCredentialsException("Bad credentials:" + userDetails);
        }
    }

    @Override
    protected void doAfterPropertiesSet() throws Exception {
        Assert.notNull(this.wxService,
                "A wxService must be set");
        Assert.notNull(this.dingTalkService,
                "A DingTalkServiceImpl must be set");
        Assert.notNull(this.haimaService,
                "A haimaService must be set");
    }

    @Override
    protected final UserDetails retrieveUser(String username,
                                             UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        UserDetails loadedUser;
        String suiteId = null;
        String corpId = null;
        String companyOID = null;
        String tenantId = null;
        try {
            String password = (String) authentication.getCredentials();
            Map details = (Map) authentication.getDetails();
            logger.info("details:{}" + JSONObject.toJSONString(details));
            if ((details.get("wxLogin") != null && details.get("wxLogin").toString().equals("Y")) || (details.get("loginType") != null && details.get("loginType").toString().equals("wx"))) {//微信端调用接口
                if (details.get("suiteId") == null) {
                    suiteId = "";
                } else {
                    suiteId = details.get("suiteId").toString();
                }
                if (details.get("corpId") == null) {
                    corpId = "";
                } else {
                    corpId = details.get("corpId").toString();
                }
                if (details.get("companyStr") == null) {
                    companyOID = "";
                } else {
                    companyOID = details.get("companyStr").toString();
                }

                if (companyOID.equals("") && password != null && !password.equals("null")) {
                    companyOID = password;
                }
                loadedUser = wxService.loadUserByUsername(username, suiteId, corpId, companyOID);// 区别在这里


            } else if (details.get("dingTalkLogin") != null && details.get("dingTalkLogin").toString().equals("Y")) {//钉钉端调用接口
                if (!StringUtils.isEmpty(details.get("corpId") + "")) {
                    corpId = details.get("corpId").toString();
                }
                loadedUser = dingTalkService.loadDingTalkUserByCodeAndCorpId(username, corpId);// 区别在这里
            } else if (details.get("loginType") != null && details.get("loginType").toString().equals("haima")) {
                if (!StringUtils.isEmpty(details.get("tenantId").toString())) {
                    tenantId = details.get("tenantId").toString();
                }
                loadedUser = haimaService.loadHaimaUserByClientIdAndCode(username, tenantId);
            } else if (details.get("loginType") != null && details.get("loginType").toString().equals("wxfw")) {
                loadedUser = ueLoginUserDetailService.loadUserByUsername(username);
            } else if (details.get("loginType") != null && details.get("loginType").toString().equals("app")) {
                loadedUser = appDetailService.loadUserByUsername(username);
            } else {//非微信端，抛异常轮询APP登录方法。
                throw new UsernameNotFoundException("user.not.found");
            }

        } catch (UsernameNotFoundException notFound) {
            throw notFound;
        } catch (Exception repositoryProblem) {
            return DecideAuthenticationServiceExceptionUtil.decideAuthenticationServiceException(repositoryProblem);
        }

        if (loadedUser == null) {
            throw new AuthenticationServiceException(
                    "UserDetailsService returned null, which is an interface contract violation");
        }
        return loadedUser;
    }


    protected PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    /**
     * Sets the PasswordEncoder instance to be used to encode and validate
     * passwords. If not set, the password will be compared as plain text.
     * <p>
     * For systems which are already using salted password which are encoded
     * with a previous release, the encoder should be of type
     * {@code org.springframework.security.authentication.encoding.PasswordEncoder}
     * . Otherwise, the recommended approach is to use
     * {@code org.springframework.security.crypto.password.PasswordEncoder}.
     *
     * @param passwordEncoder must be an instance of one of the {@code PasswordEncoder}
     *                        types.
     */
    public void setPasswordEncoder(Object passwordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");

        if (passwordEncoder instanceof PasswordEncoder) {
            this.passwordEncoder = (PasswordEncoder) passwordEncoder;
            return;
        }

        if (passwordEncoder instanceof org.springframework.security.crypto.password.PasswordEncoder) {
            final org.springframework.security.crypto.password.PasswordEncoder delegate = (org.springframework.security.crypto.password.PasswordEncoder) passwordEncoder;
            this.passwordEncoder =delegate;/* new PasswordEncoder() {
                private void checkSalt(Object salt) {
                    Assert.isNull(salt,
                            "Salt value must be null when used with crypto module PasswordEncoder");
                }

                public String encodePassword(String rawPass, Object salt) {
                    checkSalt(salt);
                    return delegate.encode(rawPass);
                }

                public boolean isPasswordValid(String encPass, String rawPass,
                                               Object salt) {
                    checkSalt(salt);
                    return delegate.matches(rawPass, encPass);
                }
            };
*/
            return;
        }

        throw new IllegalArgumentException(
                "passwordEncoder must be a PasswordEncoder instance");
    }

//    protected SaltSource getSaltSource() {
 //       return saltSource;
  //  }

    /**
     * The source of salts to use when decoding passwords. <code>null</code> is
     * a valid value, meaning the <code>DaoAuthenticationProvider</code> will
     * present <code>null</code> to the relevant <code>PasswordEncoder</code>.
     * <p>
     * Instead, it is recommended that you use an encoder which uses a random
     * salt and combines it with the password field. This is the default
     * approach taken in the
     * {@code org.springframework.security.crypto.password} package.
     *
     * @param saltSource to use when attempting to decode passwords via the
     *                   <code>PasswordEncoder</code>
     */
   // public void setSaltSource(SaltSource saltSource) {
  //      this.saltSource = saltSource;
   // }
}
