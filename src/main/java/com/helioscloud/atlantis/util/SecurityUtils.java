package com.helioscloud.atlantis.util;

import com.cloudhelios.atlantis.security.domain.PrincipalLite;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static  final String APPROVER_TYPE_ROBOT_OID="00000000-0000-0000-0000-000000000000";

    public static UUID getCurrentUserOID() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
             if (authentication.getPrincipal() instanceof PrincipalLite) {
                return ((PrincipalLite) authentication.getPrincipal()).getUserOID();

            } else if (authentication.getName() != null) {
                return UUID.nameUUIDFromBytes(authentication.getName().getBytes(StandardCharsets.UTF_8));
            }
        }
        return null;
    }


    /**
     * 获取当前用户oid 如果找不到则默认为机器人
     * @return
     */
    public static UUID getCurrentUserOIDDefaultRobot() {
        try {
            if (SecurityUtils.getCurrentUserOID() == null) {
                return UUID.fromString(APPROVER_TYPE_ROBOT_OID);
            }
            return SecurityUtils.getCurrentUserOID();
        } catch (IllegalStateException e) {
            return UUID.fromString(APPROVER_TYPE_ROBOT_OID);
        }


    }
}
