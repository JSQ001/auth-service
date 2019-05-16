package com.hand.hcf.app.base.tenant.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.tenant.domain.Tenant;
import com.hand.hcf.app.base.tenant.dto.TenantDTO;
import com.hand.hcf.app.common.co.ApplicationCO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface TenantMapper extends BaseMapper<Tenant> {

    List<TenantDTO> listTenantDTOsByCondition(@Param("tenantName") String tenantName,
                                              @Param("tenantCode") String tenantCode,
                                              @Param("userName")  String userName,
                                              @Param("mobile")  String mobile,
                                              @Param("email")  String email,
                                              @Param("login") String login,
                                              @Param("remark") String remark,
                                              RowBounds rowBounds);

    /**
     * 查询当前租户分配的应用
     * @param tenantId 租户id
     * @return List<ApplicationCO>
     */
    List<ApplicationCO> listApplications(@Param("tenantId") Long tenantId);
}
