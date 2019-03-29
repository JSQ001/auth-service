package com.hand.hcf.app.mdata.contact.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.OrganizationUserCO;
import com.hand.hcf.app.mdata.contact.domain.Contact;
import com.hand.hcf.app.mdata.contact.dto.ContactDTO;
import com.hand.hcf.app.mdata.contact.dto.ContactQO;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.contact.dto.UserSimpleInfoDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface ContactMapper extends BaseMapper<Contact> {

    List<Contact> listByQO(ContactQO contactQO);

    List<Contact> listByQO(Page page, ContactQO contactQO);

    List<ContactDTO> listDtoByQO(ContactQO contactQO);

    List<ContactDTO> listDtoByQO(Page page, ContactQO contactQO);

    List<ContactDTO> listUserByNameAndCode(@Param("name") String name,
                                           @Param("code") String code,
                                           @Param("tenantId") Long tenantId,
                                           Page page);

    /**
     * 根据租户级keyWord获取用户信息
     * @param tenantId
     * @param keyword
     * @param needEmployeeId
     * @return
     */
    List<UserDTO> listUsersByKeyword(@Param("tenantId") Long tenantId,
                                     @Param("keyword") String keyword,
                                     @Param("needEmployeeId") boolean needEmployeeId);

    /**
     * 验证是否与在职员工邮箱冲突,
     *
     * @param email 用户邮箱
     * @return null为不冲突
     */
    String varifyEmailExsits(@Param("email") String email);

    List<UserDTO> listByKeywordAndCond(Page page, ContactQO contactQO);

    List<ContactCO> listCOByCondition(@Param("ew") Wrapper<ContactCO> wrapper);
    List<ContactCO> listCOByCondition(@Param("ew") Wrapper<ContactCO> wrapper, RowBounds rowBounds);

    OrganizationUserCO getOrganizationCOByUserId(@Param("userId") Long userId);

    List<ContactCO> listByEmployeeCodeConditionCompanyIdAndDepartId(@Param("companyId") Long companyId,
                                                                    @Param("departmentId") Long departmentId,
                                                                    @Param("employeeCode") String employeeCode);

    /**
     * 根据租户id获取当前租户下所有用户相关信息，给hmap同步通讯录使用
     * @param tenantId 账套id
     * */
    List<ContactCO> listUserByTenantId(Long tenantId);

    /**
     * 获取工作组员工信息
     *
     * @param name 员工名称
     * @param code 员工代码
     * @param companyId 机构id
     * @param unitId 部门id
     * @param ids 不包含的用户id
     * @param tenantId 租户id
     * @param page 分页
     * @return 员工信息
     */
    List<UserSimpleInfoDTO> listUserByNameAndCodeAndCompanyAndUnit(@Param("name") String name,
                                                                   @Param("code") String code,
                                                                   @Param("companyId") Long companyId,
                                                                   @Param("unitId") Long unitId,
                                                                   @Param("ids") List<Long> ids,
                                                                   @Param("tenantId") Long tenantId,
                                                                   Page page);
}
