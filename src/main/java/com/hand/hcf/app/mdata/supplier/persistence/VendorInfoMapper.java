package com.hand.hcf.app.mdata.supplier.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.mdata.supplier.domain.VendorInfo;
import org.apache.ibatis.annotations.Param;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/8 10:25
 */
public interface VendorInfoMapper extends BaseMapper<VendorInfo> {

    /**
     * 获取供应商信息
     *
     * @param tenantId
     * @param companyOid
     * @param vendorName
     * @param vendorCode
     * @param source
     * @return
     */
    List<VendorInfo> selectVendorInfosByCondtions(@Param("tenantId") Long tenantId,
                                                  @Param("companyOid") String companyOid,
                                                  @Param("vendorName") String vendorName,
                                                  @Param("vendorCode") String vendorCode,
                                                  @Param("source") String source);

    /**
     * 分页获取供应商信息
     *
     * @param venderTypeId
     * @param venderCode
     * @param venNickname
     * @param bankAccount
     * @param venType
     * @param tenantId
     * @param page
     * @return
     */
    List<VendorInfo> selectVendorInfosByPage(@Param("venderTypeId") Long venderTypeId,
                                             @Param("venderCode") String venderCode,
                                             @Param("venNickname") String venNickname,
                                             @Param("bankAccount") String bankAccount,
                                             @Param("venType") Integer venType,
                                             @Param("tenantId") String tenantId,
                                             Pagination page);

    /**
     * artemis调用，分页获取供应商信息
     *
     * @param companyOID
     * @param startDateTime
     * @param endDateTime
     * @param page
     * @return
     */
    List<VendorInfo> selectVendorInfosByCompanyOidAndlastUpdatedDate(@Param("companyOID") String companyOID,
                                                                     @Param("startDateTime") ZonedDateTime startDateTime,
                                                                     @Param("endDateTime") ZonedDateTime endDateTime,
                                                                     Pagination page);

    /**
     * artemis调用，根据租户id分页获取租户级供应商信息
     *
     * @param tenantId
     * @param startDateTime
     * @param endDateTime
     * @param page
     * @return
     */
    List<VendorInfo> selectVendorInfosByTenantIdAndlastUpdatedDate(@Param("tenantId") Long tenantId,
                                                                   @Param("startDateTime") ZonedDateTime startDateTime,
                                                                   @Param("endDateTime") ZonedDateTime endDateTime,
                                                                   Pagination page);

    /**
     * 租户级别，供应商名称查询
     *
     * @param tenantId
     * @param vendorName
     * @return
     */
    List<VendorInfo> selectVendorInfosByTenantIdAndVendorName(@Param("tenantId") String tenantId,
                                                              @Param("vendorName") String vendorName);

    /**
     * 租户级别，供应商名称或标识查询
     *
     * @param tenantId
     * @param vendorName
     * @param vendorId   供应商编码[等同于供应商标识，供应商标识弃用]
     * @return
     */
    List<VendorInfo> selectVendorInfosByTenantIdAndVendorNameAndVendorId(@Param("tenantId") String tenantId,
                                                                         @Param("vendorName") String vendorName,
                                                                         @Param("vendorId") String vendorId);

    /**
     * 公司级别 分页获取供应商信息
     *
     * @param venderTypeId
     * @param venderCode
     * @param venNickname
     * @param bankAccount
     * @param venType
     * @param companyId
     * @param companyOid
     * @param page
     * @return
     */
    List<VendorInfo> selectVendorInfosByRelationCompanyForPage(@Param("venderTypeId") Long venderTypeId,
                                                               @Param("venderCode") String venderCode,
                                                               @Param("venNickname") String venNickname,
                                                               @Param("bankAccount") String bankAccount,
                                                               @Param("venType") Integer venType,
                                                               @Param("companyId") String companyId,
                                                               @Param("companyOid") String companyOid,
                                                               Page<VendorInfo> page);

    /**
     * 根据公司id和供应商名称【模糊】查询
     *
     * @param companyId
     * @param venNickname
     * @return
     */
    List<VendorInfo> selectVendorInfosByCompanyIdAndVendorName(@Param("companyId") Long companyId,
                                                               @Param("venNickname") String venNickname);

    /**
     * 根据公司id和供应商名称【模糊】分页查询
     *
     * @param companyId
     * @param venNickname
     * @param page
     * @return
     */
    List<VendorInfo> selectVendorInfosByCompanyIdAndVendorNameForPage(@Param("companyId") Long companyId,
                                                                      @Param("venNickname") String venNickname,
                                                                      Pagination page);

    /**
     * 根据公司id和供应商名称,代码【模糊】分页查询
     * 按代码升序
     * @param companyId
     * @param venNickname
     * @param page
     * @return
     */
    List<VendorInfo> selectVendorInfosByCompanyIdAndVendorNameAndCodeForPage(@Param("companyId") Long companyId,
                                                                             @Param("venNickname") String venNickname,
                                                                             @Param("vendorCode") String vendorCode,
                                                                             Pagination page);

    /**
     * 根据租户id、公司id和供应商名称,代码【模糊】分页查询
     * 按代码升序
     * @param tenantId
     * @param companyId
     * @param venNickname
     * @param vendorCode
     * @param page
     * @return
     */
    List<VendorInfo> selectVendorInfosByTenantIdCompanyIdAndVendorNameAndCodeForPage(
            @Param("tenantId") Long tenantId,
            @Param("companyId") Long companyId,
            @Param("venNickname") String venNickname,
            @Param("vendorCode") String vendorCode,
            Pagination page);

    /**
     * 根据租户id和供应商名称,代码【模糊】分页查询
     * 按代码升序
     * @param tenantId
     * @param venNickname
     * @param page
     * @return
     */
    List<VendorInfo> selectVendorInfosByTenantIdAndVendorNameAndCodeForPage(@Param("tenantId") Long tenantId,
                                                                            @Param("venNickname") String venNickname,
                                                                            @Param("vendorCode") String vendorCode,
                                                                            Pagination page);


    /**
     * 同一个租户下，查询重复的供应商名称
     *
     * @return
     */
    List<VendorInfo> searchDuplicationVendorInfosByTenant();

    /**
     * 根据供应商名称和租户id，查询具体重复的供应商信息，并根据type id正序排列
     *
     * @param vendorName
     * @param tenantId
     * @return
     */
    List<VendorInfo> selectVendorInfosByVendorNameAndTenantId(@Param("vendorName") String vendorName,
                                                              @Param("tenantId") Long tenantId);

    /**
     * 根据供应商名称 租户id 供应商来源级别查询
     *
     * @param vendorName
     * @param tenantId
     * @param source
     * @return
     */
    List<VendorInfo> selectVendorInfosByVendorNameAndTenantIdAndSource(@Param("vendorName") String vendorName,
                                                                       @Param("tenantId") Long tenantId,
                                                                       @Param("source") String source);

    /**
     * artemis调用，分页获取分配到指定公司的租户级供应商信息和公司级供应商信息
     *
     * @param companyOid
     * @param companyId
     * @param startDateTime
     * @param endDateTime
     * @param page
     * @return
     */
    List<VendorInfo> selectVendorInfosByRelationCompany(@Param("companyOid") String companyOid,
                                                        @Param("companyId") Long companyId,
                                                        @Param("startDateTime") ZonedDateTime startDateTime,
                                                        @Param("endDateTime") ZonedDateTime endDateTime,
                                                        Page<VendorInfo> page);

    /**
     * 同一个租户下，查询重复的供应商编码(供应商标识)
     *
     * @return
     */
    List<VendorInfo> searchDuplicationVendorCodesByTenant();

    /**
     * 查询所有供应商所属的租户
     *
     * @return
     */
    List<VendorInfo> selectVendorInfosGroupByTenantId();

    /**
     * 获取指定租户下所有供应商信息
     *
     * @param tenantId
     * @return
     */
    List<VendorInfo> selectVendorInfosByTenantId(@Param("tenantId") Long tenantId);

    /**
     * 指定租户下查找指定供应商编码，用于供应商编码校验：同一个租户下，唯一且不区分供应商状态和级别
     *
     * @param vendorCode
     * @param tenantId
     * @return
     */
    List<VendorInfo> selectVendorInfosByVendorCodeAndTenantId(@Param("vendorCode") String vendorCode,
                                                              @Param("tenantId") Long tenantId);


    /**
     * 用于artemis openApi update操作，根据供应商编码 在租户或公司级别下查询
     *
     * @param tenantId
     * @param companyOid
     * @param vendorCode
     * @param source
     * @return
     */
    List<VendorInfo> selectVendorInfosByVendorCodeForArtemis(@Param("tenantId") Long tenantId,
                                                             @Param("companyOid") String companyOid,
                                                             @Param("vendorCode") String vendorCode,
                                                             @Param("source") String source);
    /**
     * 用于供应商审批留查询
     *
     * @param venderTypeId
     * @param venderCode
     * @param venNickname
     * @param bankAccount
     * @param venType
     * @param tenantId
     * @return
     */
    List<VendorInfo> selectVendorInfoforApprovalByPage(@Param("venderTypeId") Long venderTypeId,
                                                 @Param("venderCode") String venderCode,
                                                 @Param("venNickname") String venNickname,
                                                 @Param("bankAccount") String bankAccount,
                                                 @Param("venType") Integer venType,
                                                 @Param("tenantId") String tenantId,
                                                 Pagination page);

}
