package com.hand.hcf.app.mdata.base.util;

import com.hand.hcf.app.common.co.OrganizationUserCO;
import com.hand.hcf.app.mdata.implement.web.ContactControllerImpl;
import com.hand.hcf.core.component.ApplicationContextProvider;
import com.hand.hcf.core.security.domain.PrincipalLite;
import com.hand.hcf.core.util.LoginInformationUtil;
import org.springframework.context.ApplicationContext;

import java.util.UUID;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/2/27 14:46
 * @remark
 */
public class OrgInformationUtil extends LoginInformationUtil {


    //获取账套
    public static Long getCurrentSetOfBookId() {
        PrincipalLite user = getUser();
        return getOrganizationUserCO(user.getId()).getSetOfBookId();
    }

    //获取公司id
    public static Long getCurrentCompanyId() {
        PrincipalLite user = getUser();
        return getOrganizationUserCO(user.getId()).getCompanyId();
    }

    //获取公司Oid
    public static UUID getCurrentCompanyOid() {
        PrincipalLite user = getUser();
        return getOrganizationUserCO(user.getId()).getCompanyOid();
    }

    //获取部门id
    public static Long getCurrentDepartmentId() {
        PrincipalLite user = getUser();
        return getOrganizationUserCO(user.getId()).getDepartmentId();
    }

    //获取部门oid
    public static UUID getCurrentDepartmentOid() {
        PrincipalLite user = getUser();
        return getOrganizationUserCO(user.getId()).getDepartmentOid();
    }

    private static OrganizationUserCO getOrganizationUserCO(Long userId){
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        ContactControllerImpl bean = applicationContext.getBean(ContactControllerImpl.class);
        OrganizationUserCO organizationUserCO = bean.getOrganizationCOByUserId(userId);
        if (organizationUserCO == null){
            // admin 无组织架构信息
            OrganizationUserCO co = new OrganizationUserCO();
            co.setUserId(userId);
            return co;
        }
        return organizationUserCO;
    }

}
