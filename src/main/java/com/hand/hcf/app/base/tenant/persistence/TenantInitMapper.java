package com.hand.hcf.app.base.tenant.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.tenant.domain.Tenant;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TenantInitMapper extends BaseMapper<Tenant> {


    void updateClientTenantId(@Param("clientId") String clientId,
                              @Param("additionalInformation") String additionalInformation);

    void deleteAccessToken(@Param("clientId") String clientId);

    int updateDepartmentBySql(@Param("companyId") Long companyId,
                              @Param("tenantId") Long tenantId);


    int initCompanySecurity(@Param("companyOid") UUID companyOid,
                            @Param("tenantId") Long tenantId);

    int initCompanyStandardCurrency(@Param("companyOid") UUID companyOid,
                                    @Param("tenantId") Long tenantId);

    int initCarousel(@Param("companyOid") UUID companyOid,
                     @Param("tenantId") Long tenantId);

    int initUser(@Param("companyId") Long companyId,
                 @Param("tenantId") Long tenantId);

    int getUserCount();


    int initLevel(@Param("companyOid") UUID companyOid,
                  @Param("tenantId") Long tenantId);

    /**
     * 系统内当前定时任务总数统计
     * @return
     */
    int getTaskConfigCount();

    /**
     * 初始化定时任务
     * @return
     */
    int updateTaskConfig();

    /**
     * 系统内当前报表副本数目
     * @return
     */
    int getReportLineCount();

    /**
     * 报表副本更新
     * @return
     */
    int updateReportLine();


    List<Long> getTenantMutiSetOfBooks();

    List<Map<String,String>> selectEffectiveUser(@Param("tenantId") Long tenantId);

    Long findHandCompanyTenantId();

    UUID findHandCompanyOid();

    Integer updateHandUserCompany(@Param("companyReceiptedOid") UUID companyReceiptedOid,
                                  @Param("companyId") Long companyId);
}
