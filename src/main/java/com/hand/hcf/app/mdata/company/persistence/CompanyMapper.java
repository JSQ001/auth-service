

package com.hand.hcf.app.mdata.company.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.dto.CompanyQO;
import com.hand.hcf.app.mdata.company.dto.CompanySobDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CompanyMapper extends BaseMapper<Company> {

    List<Company> getByQO(CompanyQO companyQO);

    List<Company> getByQO(CompanyQO companyQO, Pagination page);

    List<Company> findAllBySetOfBooksId(@Param("setOfBooksId") Long setOfBooksId,
                                        @Param("name") String name,
                                        @Param("companyCode") String companyCode,
                                        @Param("companyCodeFrom") String companyCodeFrom,
                                        @Param("companyCodeTo") String companyCodeTo,
                                        @Param("restrictionCompanyIds") List<Long> restrictionCompanyIds,
                                        @Param("legalEntityId") Long legalEntityId,
                                        Pagination page);

    List<Company> getCompanyWithoutEnumeration(@Param("tenantId") Long tenantId,
                                               @Param("name") String name,
                                               @Param("source") Long source,
                                               @Param("companyCode") String companyCode,
                                               @Param("companyLevelId") Long companyLevelId,
                                               @Param("legalEntityId") Long legalEntityId,
                                               @Param("companyCodeFrom") String companyCodeFrom,
                                               @Param("companyCodeTo") String companyCodeTo,
                                               Pagination page);

    List<Company> getCompanyWithCarousel(@Param("tenantId") Long tenantId,
                                         @Param("name") String name,
                                         @Param("source") Long source,
                                         @Param("companyCode") String companyCode,
                                         @Param("companyLevelId") Long companyLevelId,
                                         @Param("legalEntityId") Long legalEntityId,
                                         @Param("companyCodeFrom") String companyCodeFrom,
                                         @Param("companyCodeTo") String companyCodeTo,
                                         Pagination page);

    List<Company> getCompanyWithoutLevels(@Param("tenantId") Long tenantId,
                                          @Param("name") String name,
                                          @Param("source") Long source,
                                          @Param("companyCode") String companyCode,
                                          @Param("companyLevelId") Long companyLevelId,
                                          @Param("legalEntityId") Long legalEntityId,
                                          @Param("companyCodeFrom") String companyCodeFrom,
                                          @Param("companyCodeTo") String companyCodeTo,
                                          Pagination page);

    List<Company> selectCompanyByTenantIdAndEnabled(@Param("tenantId") Long tenantId,
                                                    @Param("companyCode") String companyCode,
                                                    @Param("name") String name,
                                                    @Param("setOfBooksId") Long setOfBooksId,
                                                    @Param("enabled") Boolean enabled,
                                                    @Param("filter") List<Long> filter,
                                                    Pagination page);

    List<CompanySobDTO> getCompaniesByTenantIdAndCondition(@Param("tenantId") Long tenantId,
                                                           @Param("setOfBooksId") Long setOfBooksId,
                                                           @Param("enable") Boolean enable,
                                                           @Param("keyWord") String keyWord,
                                                           Pagination page);

    List<Company> findBySetOfBooksIdAndIsEnabledTrue(@Param("setOfBooksId") Long setOfBooksId,
                                                     @Param("filterCompanyOids") String filterCompanyOids);

    List<Company> findByCompanyOidIn(@Param("companyOids") List<UUID> companyOids);

    List<Company> findOneByuserOid(@Param("userOid") String userOid);

    List<Company> findByIdIn(@Param("companyIds") List<Long> companyIds, Pagination page);

    List<Company> findByIdIn(@Param("companyIds") List<Long> companyIds);


    List<String> selectRootSiblingCompanyPathList(@Param("companyId") Long companyId);

    List<String> selectSiblingCompanyPathList(@Param("companyId") Long companyId,
                                              @Param("parentCompanyId") Long parentCompanyId);

    Long countIsEnabledTrueCompanyByLegalEntityId(@Param("legalEntityId") Long legalEntityId);

    Long findTenantIdByCompanyOid(@Param("companyOid") UUID companyOid);

    Long countCompanyByTenantId(@Param("tenantId") Long tenantId);

    /**
     * 根据公司ID获取下属公司ID
     * @param companyIds
     * @return
     */
    Set<Long> getCompanyChildrenIdByCompanyIds(@Param(value = "companyIds") Set<Long> companyIds);

    List<Company> getCompanyByCond(@Param("companyIds") List<Long> companyIds,
                                   @Param("companyCode") String companyCode,
                                   @Param("companyName") String companyName,
                                   @Param("companyCodeFrom") String companyCodeFrom,
                                   @Param("companyCodeTo") String companyCodeTo,
                                   @Param("keyWord") String keyWord,
                                   Pagination pagination
    );

    List<Company> getCompanyByCond(@Param("companyIds") List<Long> companyIds,
                                   @Param("companyCode") String companyCode,
                                   @Param("companyName") String companyName,
                                   @Param("companyCodeFrom") String companyCodeFrom,
                                   @Param("companyCodeTo") String companyCodeTo,
                                   String keyWord
    );

    List<CompanyCO> listCompanyCO(@Param("ew") Wrapper<CompanyCO> wrapper);
    List<CompanyCO> listCompanyCO(@Param("ew") Wrapper<CompanyCO> wrapper, RowBounds rowBounds);

    List<BasicCO> pageCompanyByCond(@Param("tenantId") Long tenantId,
                                    @Param("setOfBooksId") Long setOfBooksId,
                                    @Param("companyId") Long companyId,
                                    @Param("code") String code,
                                    @Param("name") String name,
                                    Page page);

    List<BasicCO> listCompanyByCodeAndSecurityType(@Param("setOfBooksId") Long setOfBooksId,
                                                   @Param("companyCode") String code,
                                                   @Param("companyName") String name,
                                                   @Param("tenantId") Long tenantId,
                                                   @Param("companyId") Long companyId,
                                                   Pagination page);

    /**
     * 根据账套Id获取所有启用公司
     * @param setOfBooksId 账套id
     * @param keyword   公司代码或者名称
     * @param codeFrom 公司代码从
     * @param codeTo 公司代码至
     * @param Page 分页
     * @return
     */
    List<Company> pageCompanyBySetOfBooksIdAndCond(@Param("setOfBooksId") Long setOfBooksId,
                                                   @Param("keyword") String keyword,
                                                   @Param("codeFrom") String codeFrom,
                                                   @Param("codeTo") String codeTo,
                                                   Pagination Page);
    /**
     * 根据租户id获取当前租户下所有机构，给hmap同步通讯录使用
     * @param tenantId 账套id
     * @param enabled 是否启用
    * */
    List<CompanyCO> listCompanyByTenantId(@Param("tenantId") Long tenantId,
                                          @Param("enabled") Boolean enabled);
}
