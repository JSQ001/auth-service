package com.hand.hcf.app.mdata.implement.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.company.dto.CompanyLovDTO;
import com.hand.hcf.app.mdata.company.dto.CompanyLovQueryParams;
import com.hand.hcf.app.mdata.company.service.*;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.department.dto.DepartmentLovDTO;
import com.hand.hcf.app.mdata.department.dto.DepartmentLovQueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/18
 */

@RestController
public class CompanyControllerImpl {

    @Autowired
    private CompanyService companyService;
    @Autowired
    private CompanyGroupService companyGroupService;

    @Autowired
    private CompanyConfigurationService companyConfigurationService;
    @Autowired
    private ContactService contactService;
    @Autowired
    private CompanyLevelService companyLevelService;
    @Autowired
    private CompanyAssociateUnitService companyAssociateUnitService;


    /**
     * 查询当前账套下的所有机构(根据enabled指定是否是启用的)
     * @param setOfBooksId 账套Id
     * @param enabled 是否启用，为空时则查询所有
     */
    public List<CompanyCO> listBySetOfBooksIdConditionByEnabled(@RequestParam("setOfBooksId") Long setOfBooksId,
                                                                @RequestParam(value = "enabled",required = false) Boolean enabled){
        return companyService.listBySetOfBooksIdConditionByEnabled(setOfBooksId, enabled);
    }


    /**
     * 根据ID集合查询公司信息
     * @param companyIds 公司Id集合
     */
    public List<CompanyCO> listByIds(@RequestBody List<Long> companyIds){
        return companyService.listByIds(companyIds);
    }

    /**
     * 根据公司id查询公司信息
     * @param id 公司Id
     */
    public CompanyCO getById(@RequestParam("id") Long id){
        return companyService.getById(id);
    }

    /**
     * 根据公司Oid集合查询公司信息
     * @param companyOids
     */
    public List<CompanyCO> listByCompanyOidList(@RequestBody List<String> companyOids){
        return companyService.listByCompanyOidList(companyOids);
    }

    /**
     * 根据公司oid 查询公司信息
     * @param oid 公司Oid
     */
    public CompanyCO getByCompanyOid(@RequestParam("oid") String oid){
        return companyService.getByCompanyOid(oid);
    }

    /**
     * 如果existsCompanyIds集合为空则分页查询当前账套下的公司，反之查询指定的公司
     * @param setOfBooksId  账套ID
     * @param companyCode 公司代码 条件查询
     * @param companyCodeFrom 公司代码从 条件查询
     * @param companyCodeTo 公司代码至 条件查询
     * @param companyName 公司名称 条件查询
     * @param page 每页多少条
     * @param size 每页大小
     * @param existsCompanyIds 存在的公司Id集合 条件查询
     */
    public Page<CompanyCO> pageBySetOfBooksIdConditionByIds(@RequestParam("setOfBooksId") Long setOfBooksId,
                                                            @RequestParam(value = "companyCode",required = false) String companyCode,
                                                            @RequestParam(value = "companyCodeFrom",required = false) String companyCodeFrom,
                                                            @RequestParam(value = "companyCodeTo",required = false) String companyCodeTo,
                                                            @RequestParam(value = "companyName",required = false) String companyName,
                                                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                                            @RequestBody(required = false) List<Long> existsCompanyIds){
        Page<CompanyCO> mybatisPage = new Page<>(page + 1, size);
        return companyService.pageBySetOfBooksIdConditionByIds(setOfBooksId,
                companyCode,
                companyCodeFrom,
                companyCodeTo,
                companyName,
                existsCompanyIds,
                mybatisPage);
    }


    /**
     * 分页条件查询当前账套下的公司，排除指定的机构
     * @param setOfBooksId 账套Id
     * @param companyCode 公司代码 条件查询
     * @param companyCodeFrom 公司代码从 条件查询
     * @param companyCodeTo 公司代码至 条件查询
     * @param companyName 公司名称
     * @param page 第几页
     * @param size 每页大小
     * @param ignoreCompanyIds 需要排除的公司Id集合 条件查询
     */
    public Page<CompanyCO> pageBySetOfBooksIdConditionByIgnoreIds(@RequestParam("setOfBooksId") Long setOfBooksId,
                                                                  @RequestParam(value = "companyCode",required = false) String companyCode,
                                                                  @RequestParam(value = "companyCodeFrom",required = false) String companyCodeFrom,
                                                                  @RequestParam(value = "companyCodeTo",required = false) String companyCodeTo,
                                                                  @RequestParam(value = "companyName",required = false) String companyName,
                                                                  @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                  @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                  @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                                                  @RequestBody(required = false) List<Long> ignoreCompanyIds){
        Page<CompanyCO> mybatisPage = new Page<>(page + 1, size);
        return companyService.pageBySetOfBooksIdConditionByIgnoreIds(setOfBooksId,
                companyCode,
                companyCodeFrom,
                companyCodeTo,
                companyName,
                enabled,
                ignoreCompanyIds,
                mybatisPage);
    }

    /**
     * 分页条件查询当前租户下的公司，排除指定的公司
     * @param tenantId  租户id
     * @param setOfBooksId  账套id
     * @param companyCode  公司代码
     * @param companyName  公司名称
     * @param companyCodeFrom  公司代码从
     * @param companyCodeTo  公司代码至
     * @param page  第几页
     * @param size  每页大小
     * @param ignoreCompanyIds  需要排除的公司Id集合 条件查询
     * @return
     */
    public Page<CompanyCO> pageByTenantIdConditionByIgnoreIds(@RequestParam("tenantId") Long tenantId,
                                                              @RequestParam(value = "setOfBooksId",required = false) Long setOfBooksId,
                                                              @RequestParam(value = "companyCode",required = false) String companyCode,
                                                              @RequestParam(value = "companyName",required = false) String companyName,
                                                              @RequestParam(value = "companyCodeFrom",required = false) String companyCodeFrom,
                                                              @RequestParam(value = "companyCodeTo",required = false) String companyCodeTo,
                                                              @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                              @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                                              @RequestBody(required = false) List<Long> ignoreCompanyIds){
        Page<CompanyCO> mybatisPage = new Page<>(page + 1, size);
        return companyService.pageByTenantIdConditionByIgnoreIds(
                tenantId,
                setOfBooksId,
                companyCode,
                companyName,
                companyCodeFrom,
                companyCodeTo,
                ignoreCompanyIds,
                mybatisPage);
    }


    /**
     * 根据账套ID和公司代码查询公司信息
     * @param setOfBooksId 账套Id
     * @param companyCodes 公司代码集合
     */
    public List<CompanyCO> listBySetOfBooksIdAndCompanyCodes(@RequestParam("setOfBooksId") Long setOfBooksId,
                                                             @RequestBody List<String> companyCodes){
        return companyService.listBySetOfBooksIdAndCompanyCodes(setOfBooksId, companyCodes);
    }


    /**
     * 根据公司信息获取该公司所在的公司组信息
     * @param companyId 公司Id
     */
    public List<CompanyGroupCO> listCompanyGroupByCompanyId(@RequestParam("companyId") Long companyId){
        return companyGroupService.listCompanyGroupByCompanyId(companyId);
    }

    /**
     * 查询指定公司组下的所有公司
     * @param companyGroupId 公司组Id
     */
    public List<CompanyCO> listByCompanyGroupIdConditionByEnabled(@RequestParam("companyGroupId") Long companyGroupId,
                                                                  @RequestParam(value = "enabled",required = false) Boolean enabled){
        return companyService.listByCompanyGroupIdConditionByEnabled(companyGroupId, enabled);
    }

    /**
     * 根据公司组id查询公司组信息及其分配的公司id集合
     * @param companyGroupId  公司组id
     */
    public CompanyGroupCO getCompanyGroupAndCompanyIdsByCompanyGroupId(@RequestParam("companyGroupId") Long companyGroupId,
                                                                       @RequestParam(value = "enabled",required = false) Boolean enabled){
        return companyGroupService.getCompanyGroupAndCompanyIdsByCompanyGroupId(companyGroupId, enabled);
    }

    public List<CompanyGroupCO> listCompanyGroupAndCompanyIdsByCompanyGroupIds(@RequestBody List<Long> companyGroupIds,
                                                                               @RequestParam(value = "enabled",required = false) Boolean enabled) {
        return companyGroupService.listCompanyGroupAndCompanyIdsByCompanyGroupIds(companyGroupIds, enabled);
    }

    /**
     * 根据账套Id查询当前账套下的所有公司组信息以及公司组包含的公司Id
     * @param setOfBooksId 账套Id
     */
    public List<CompanyGroupCO> listCompanyGroupAndCompanyIdsBySetOfBooksId(@RequestParam("setOfBooksId") Long setOfBooksId,
                                                                            @RequestParam(value = "enabled",required = false) Boolean enabled){
        return  companyGroupService.listCompanyGroupAndCompanyIdsBySetOfBooksId(setOfBooksId, enabled);
    }

    /**
     * 根据公司代码查询指定的公司
     * @param companyCode 公司代码
     */
    public CompanyCO getByCompanyCode(@RequestParam("companyCode") String companyCode){
        return companyService.getByCompanyCode(companyCode);
    }


    /**
     *  根据公司Id分页获取指定公司的子公司
     * @param companyId 公司Id
     * @param ignoreOwn 是否包含本公司 必输
     * @param companyCode 公司代码  条件查询
     * @param companyName 公司名称 条件查询
     * @param companyCodeFrom 公司代码从 条件查询
     * @param companyCodeTo 公司代码至 条件查询
     * @param keyWord 关键字 条件查询
     * @param page 第几页
     * @param size 每页大小
     */
    public Page<CompanyCO> pageChildrenCompaniesByCondition(@RequestParam("companyId") Long companyId,
                                                            @RequestParam("ignoreOwn") Boolean ignoreOwn,
                                                            @RequestParam(required = false,value = "companyCode") String companyCode,
                                                            @RequestParam(required = false,value = "companyCodeFrom") String companyCodeFrom,
                                                            @RequestParam(required = false,value = "companyCodeTo") String companyCodeTo,
                                                            @RequestParam(required = false,value = "companyName") String companyName,
                                                            @RequestParam(value = "keyWord",required = false) String keyWord,
                                                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                            @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        Page<CompanyCO> mybatisPage = new Page<>(page + 1, size);
        return companyService.pageChildrenCompaniesByCondition(companyId,
                ignoreOwn,
                companyCode,
                companyCodeFrom,
                companyCodeTo,
                companyName,
                keyWord,
                mybatisPage);
    }

    /**
     *  根据公司Id获取指定公司的所有子公司
     * @param companyId 公司Id
     * @param ignoreOwn 是否包含本公司 必输
     * @param companyCode 公司代码 条件查询
     * @param companyName 公司名称 条件查询
     * @param companyCodeFrom 公司代码从 条件查询
     * @param companyCodeTo 公司代码至 条件查询
     * @param keyWord 关键字
     */
    public List<CompanyCO> listChildrenCompaniesByCondition(@RequestParam("companyId") Long companyId,
                                                            @RequestParam("ignoreOwn") Boolean ignoreOwn,
                                                            @RequestParam(required = false,value = "companyCode") String companyCode,
                                                            @RequestParam(required = false,value = "companyCodeFrom") String companyCodeFrom,
                                                            @RequestParam(required = false,value = "companyCodeTo") String companyCodeTo,
                                                            @RequestParam(required = false,value = "companyName") String companyName,
                                                            @RequestParam(value = "keyWord",required = false) String keyWord){
        return companyService.listChildrenCompaniesByCondition(companyId,
                ignoreOwn,
                companyCode,
                companyCodeFrom,
                companyCodeTo,
                companyName,
                keyWord);
    }

    /**
     * 获取指定公司的子公司Id集合
     * @param companyId 公司Id
     */
    public Set<Long> listChildrenCompanyIdsByCompanyId(@RequestParam("companyId") Long companyId){
        return companyService.listChildrenCompanyIdsByCompanyId(companyId);
    }


    /**
     * 根据关键字以及是否启用分页查询公司信息
     * @param keyWord 关键字 条件查询
     * @param enabled 是否启用 条件查询
     * @param page 第几页
     * @param size 每页大小
     */
    public Page<CompanyCO> pageConditionKeyWordAndEnabled(@RequestParam(value = "keyWord",required = false) String keyWord,
                                                          @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                          @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                          @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        Page<CompanyCO> mybatisPage = new Page<>(page + 1, size);

        return companyService.pageConditionKeyWordAndEnabled(keyWord, enabled, mybatisPage);
    }

    /**
     *  根据 公司Id集合以及关键字分页查询公司信息
     * @param companyIds 公司Id集合
     * @param keyWord 关键字 条件查询
     * @param page 第几页
     * @param size 每页大小
     */
    public Page<CompanyCO> pageByCompanyIdsConditionByKeyWord(@RequestBody List<Long> companyIds,
                                                              @RequestParam(value = "keyWord",required = false) String keyWord,
                                                              @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                              @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        Page<CompanyCO> mybatisPage = new Page<>(page + 1, size);

        return companyService.pageByCompanyIdsConditionByKeyWord(companyIds, keyWord, mybatisPage);
    }

    /**
     * 根据用户oid查询用户所属公司
     *
     * @param userOid 用户oid
     */
    public CompanyCO getByUserOid(@RequestParam("userOid") UUID userOid) {
        return companyService.getById(contactService.getContactByUserOid(userOid).getCompanyId());
    }

    /**
     * 根据公司oid查询公司配置
     *
     * @param companyOid 公司oid
     */
    public CompanyConfigurationCO getCompanyConfigByCompanyOid(@RequestParam("companyOid") UUID companyOid) {
        return companyConfigurationService.toCO(companyConfigurationService.getCompanyConfiguration(companyOid));
    }

    /**
     * 根据公司代码、公司名称不分页查询账套下的公司
     * @param setOfBooksId
     * @param companyCode
     * @param companyName
     * @return
     */
    public List<CompanyCO> listCompanyBySetOfBooksIdAndCodeAndName(@RequestParam(value = "setOfBooksId",required = false)Long setOfBooksId,
                                                                   @RequestParam(value = "companyCode",required = false)String companyCode,
                                                                   @RequestParam(value = "companyName",required = false)String companyName){
        return companyService.listCompanyBySetOfBooksIdAndCodeAndName(setOfBooksId, companyCode, companyName);
    }

    /**
     * 根据租户id查询机构信息
     * @param tenantId
     * @param enabled
     * @return
     */
    public List<CompanyCO> listCompaniesByTenantId(@RequestParam(value = "tenantId")Long tenantId,
                                                   @RequestParam(value = "enabled", required = false)Boolean enabled) {
        return companyService.listCompanyByTenantId(tenantId, enabled);
    }

    public List<CompanyLevelCO> listCompanyLevel(Long companyLevelId, String companyLevelCode) {
        return companyLevelService.selectByCondition(companyLevelId,companyLevelCode);
    }

    /**
     * 根据条件查询公司信息
     * @param companyCode
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param companyName
     * @param keyWord
     * @param enabled
     * @param ids
     * @return
     */
    public List<CompanyCO> listCompanyByCond(@RequestParam(value = "companyCode", required = false) String companyCode,
                                             @RequestParam(value = "companyCodeFrom", required = false) String companyCodeFrom,
                                             @RequestParam(value = "companyCodeTo", required = false) String companyCodeTo,
                                             @RequestParam(value = "companyName", required = false) String companyName,
                                             @RequestParam(value = "keyWord", required = false) String keyWord,
                                             @RequestParam(value = "enabled",required = false) Boolean enabled,
                                             @RequestBody(required = false) List<Long> ids) {
        return companyService.listCompanyByCond(companyCode, companyCodeFrom, companyCodeTo, companyName, keyWord, enabled, ids);
    }

    /**
     * 查询公司lov
     *
     * @param id              公司id
     * @param companyCode     公司代码
     * @param companyCodeFrom 公司代码从
     * @param companyCodeTo   公司代码至
     * @param companyName     公司名称
     * @param departmentId    部门id
     * @param enabled         公司状态
     * @param setOfBooksId    账套Id
     * @param codeName        公司代码/名称
     * @param page
     * @param size
     * @return
     */
    public Page<CompanyCO> pageAssociateCompanyByCond(@RequestParam(required = false) Long id,
                                                      @RequestParam(required = false) String companyCode,
                                                      @RequestParam(required = false) String companyCodeFrom,
                                                      @RequestParam(required = false) String companyCodeTo,
                                                      @RequestParam(required = false) String companyName,
                                                      @RequestParam(required = false) Long departmentId,
                                                      @RequestParam(required = false) Boolean enabled,
                                                      @RequestParam(required = false) Long setOfBooksId,
                                                      @RequestParam(required = false) String codeName,
                                                      @RequestParam(required = false, defaultValue = "0") int page,
                                                      @RequestParam(required = false, defaultValue = "10") int size) {
        Page mybatisPage = PageUtil.getPage(page, size);
        CompanyLovQueryParams queryParams = CompanyLovQueryParams.builder()
                .id(id)
                .codeName(codeName)
                .companyCode(companyCode)
                .companyCodeFrom(companyCodeFrom)
                .companyCodeTo(companyCodeTo)
                .departmentId(departmentId)
                .tenantId(LoginInformationUtil.getCurrentTenantId())
                .enabled(enabled)
                .setOfBooksId(setOfBooksId)
                .companyName(companyName).build();
        List<CompanyLovDTO> companyLovDTOList = companyAssociateUnitService.queryCompanyLov(mybatisPage, queryParams);
        List<CompanyCO>  result = new ArrayList<>();
        for (CompanyLovDTO lov: companyLovDTOList) {
            result.add(CompanyCO.builder()
                    .id(lov.getId())
                    .companyOid(UUID.fromString(lov.getCompanyOid()))
                    .name(lov.getCompanyName())
                    .companyCode(lov.getCompanyCode())
                    .build());
        }
        Page<CompanyCO> resultPage = new Page();
        resultPage.setRecords(result);
        resultPage.setTotal(mybatisPage.getTotal());
        return resultPage;
    }

    /**
     * 查询部门lov
     *
     * @param companyId          公司id
     * @param departmentCode     部门代码
     * @param departmentCodeFrom 部门代码从
     * @param departmentCodeTo   部门代码至
     * @param departmentName     部门名称
     * @param ids                部门id集合
     * @param setOfBooksId       账套id
     * @param status             部门状态
     * @param codeName           部门代码/名称
     * @param page
     * @param size
     * @return
     */
    public Page<DepartmentCO> pageAssociateDepartmentByCond(@RequestParam(required = false) Long companyId,
                                                            @RequestParam(required = false) String departmentCode,
                                                            @RequestParam(required = false) String departmentCodeFrom,
                                                            @RequestParam(required = false) String departmentCodeTo,
                                                            @RequestParam(required = false) String departmentName,
                                                            @RequestBody(required = false) List<Long> ids,
                                                            @RequestParam(required = false) Long setOfBooksId,
                                                            @RequestParam(required = false) Integer status,
                                                            @RequestParam(required = false) String codeName,
                                                            @RequestParam(required = false, defaultValue = "0") int page,
                                                            @RequestParam(required = false, defaultValue = "10") int size) {
        Page mybatisPage = PageUtil.getPage(page, size);
        DepartmentLovQueryParams queryParams = DepartmentLovQueryParams.builder()
                .companyId(companyId)
                .codeName(codeName)
                .departmentCode(departmentCode)
                .departmentCodeFrom(departmentCodeFrom)
                .departmentCodeTo(departmentCodeTo)
                .ids(ids)
                .tenantId(LoginInformationUtil.getCurrentTenantId())
                .status(status)
                .setOfBooksId(setOfBooksId)
                .departmentName(departmentName).build();
        List<DepartmentLovDTO> departmentLovDTOList = companyAssociateUnitService.queryDepartmentLov(mybatisPage, queryParams);
        List<DepartmentCO> result = new ArrayList<>();
        for (DepartmentLovDTO lov: departmentLovDTOList) {
            result.add(DepartmentCO.builder()
                    .id(lov.getId())
                    .name(lov.getDepartmentName())
                    .departmentCode(lov.getDepartmentCode())
                    .path(lov.getDepartmentPath())
                    .departmentOid(UUID.fromString(lov.getDepartmentOid()))
                    .build());
        }
        Page<DepartmentCO> resultPage = new Page();
        resultPage.setRecords(result);
        resultPage.setTotal(mybatisPage.getTotal());
        return resultPage;
    }
}
