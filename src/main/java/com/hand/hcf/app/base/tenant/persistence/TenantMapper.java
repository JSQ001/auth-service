package com.hand.hcf.app.base.tenant.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.tenant.domain.Tenant;
import com.hand.hcf.app.base.tenant.dto.TenantDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface TenantMapper extends BaseMapper<Tenant> {

    List<TenantDTO> listTenantDTOsByCondition(@Param("tenantName") String tenantName,
                                              @Param("tenantCode") String tenantCode,
                                              @Param("employeeId") String employeeId,
                                              @Param("fullName") String fullName,
                                              @Param("mobile") String mobile,
                                              @Param("email") String email,
                                              @Param("login") String login,
                                              RowBounds rowBounds);

}
