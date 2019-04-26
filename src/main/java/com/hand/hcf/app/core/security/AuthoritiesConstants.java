package com.hand.hcf.app.core.security;

/**
 * PaymentConstants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ROLE_TENANT_ADMIN = "ROLE_TENANT_ADMIN"; // 租户管理员权限

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String COMPANY_ADMIN = "ROLE_COMPANY_ADMIN";

    public static final String COMPANY_FINANCE_ADMIN = "ROLE_COMPANY_FINANCE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String INTEGRATION_CLIENTS = "ROLE_INTEGRATION_CLIENTS";

    public static final String COMPANY_API = "ROLE_COMPANY_API";

    public static final String COMPANY_BILLING = "ROLE_COMPANY_BILLING";//开票权限

    public static final String COMPANY_BOOK = "ROLE_COMPANY_BOOK";//订票专员权限

    public static final String COMPANY_BOOK_ADMIN = "ROLE_COMPANY_BOOK_ADMIN";//订票专员主管权限
    public static final String DATA_ADMIN="ROLE_DATA_ADMIN";

    public static final String COMPANY_FINANCE_RECEIVED = "ROLE_COMPANY_FINANCE_RECEIVED"; //财务收报销单权限
    public static final String OPEN_API = "ROLE_OPEN_API";//开放平台权限
    public static final String SUPER_AUD = "Super Audit Role";//财务超级查看权限，系统默认，财务管理的查看无限制
    public static final String SUPER_FIN = "Super Finance Role";//财务超级财务权限，系统默认 ，财务管理的操作无限制
    public static final String FINANCE_API = "ROLE_FINANCE_API";//财务中间件权限
    public static final String OPERATION_API = "ROLE_OPERATION";// 操作银行权限
    private AuthoritiesConstants() {
    }
}
