package com.hand.hcf.app.mdata.company.service;

import com.baomidou.mybatisplus.mapper.SqlHelper;
import com.baomidou.mybatisplus.plugins.Page;

import com.hand.hcf.app.base.implement.web.CommonControllerImpl;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.mdata.company.domain.CompanyAssociateUnit;
import com.hand.hcf.app.mdata.company.dto.CompanyAssociateUnitDTO;
import com.hand.hcf.app.mdata.company.dto.CompanyLovDTO;
import com.hand.hcf.app.mdata.company.dto.CompanyLovQueryParams;
import com.hand.hcf.app.mdata.company.persistence.CompanyAssociateUnitMapper;
import com.hand.hcf.app.mdata.contact.dto.ContactDTO;
import com.hand.hcf.app.mdata.department.dto.DepartmentLovDTO;
import com.hand.hcf.app.mdata.department.dto.DepartmentLovQueryParams;
import com.hand.hcf.app.mdata.parameter.service.ParameterSettingService;
import com.hand.hcf.app.mdata.responsibilityCenter.dto.ResponsibilityDefaultDTO;
import com.hand.hcf.app.mdata.responsibilityCenter.dto.ResponsibilityLovDTO;
import com.hand.hcf.app.mdata.responsibilityCenter.service.ResponsibilityCenterService;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.utils.ParameterCodeConstants;
import com.hand.hcf.app.mdata.utils.RespCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/4/15
 */
@Service
public class CompanyAssociateUnitService extends BaseService<CompanyAssociateUnitMapper, CompanyAssociateUnit> {
    @Autowired
    private ResponsibilityCenterService centerService;
    @Autowired
    private ParameterSettingService parameterSettingService;
    @Autowired
    private CommonControllerImpl organizationClient;

    public List<CompanyAssociateUnitDTO> queryByCompanyId(Page page, Long companyId) {
        // 先查询关联的部门信息
        List<CompanyAssociateUnitDTO> result = baseMapper.queryByCompanyId(page, companyId);
        // 部门公司默认的责任中心查询不方便子关联查询，因此从新查询然后赋值

        List<Long> departmentIds = result
                .stream()
                .map(CompanyAssociateUnitDTO::getDepartmentId).collect(Collectors.toList());

        Map<Long, ResponsibilityDefaultDTO> centers = centerService.getDefaultCenterByCompanyAndDepartment(
                companyId, departmentIds);
        result.forEach(e ->
            e.setResponsibilityName(centers.get(e.getDepartmentId()) != null
                    ? centers.get(e.getDepartmentId()).getCodeName() : null));
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean associate(Long companyId, List<Long> departmentIds) {
        if (CollectionUtils.isEmpty(departmentIds)){
            return false;
        }
        List<CompanyAssociateUnit> associateUnits = departmentIds
                .stream()
                .filter(Objects::nonNull)
                .map(e -> {
                    CompanyAssociateUnit unit = new CompanyAssociateUnit();
                    unit.setCompanyId(companyId);
                    unit.setDepartmentId(e);
                    unit.setEnabled(Boolean.TRUE);
                    return unit;
                }).collect(Collectors.toList());
        return this.insertBatch(associateUnits);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateAssociate(CompanyAssociateUnit domain) {
        // 如果是禁用校验公司部门下是否存在员工，有就不能更新
        if (domain.getEnabled() == null || !domain.getEnabled()){
            if (SqlHelper.retBool(baseMapper.checkEmployee(domain))){
                throw new BizException(RespCode.MDATA_COMPANY_UNIT_ASSOICATE_NOT_ALLOW_DISABLED);
            }
        }
        return this.updateById(domain);
    }

    public List<CompanyAssociateUnitDTO> queryCanAssociate(Page page,
                                                           Long companyId,
                                                           String codeName,
                                                           String codeFrom,
                                                           String codeTo) {
        Long currentTenantId = LoginInformationUtil.getCurrentTenantId();
        return baseMapper.queryCanAssociate(page, companyId, currentTenantId, codeName, codeFrom, codeTo);
    }

    public List<ContactDTO> queryContact(Page page,
                                         Long companyId,
                                         Long departmentId,
                                         String codeName,
                                         String dutyCode,
                                         Integer status) {

        return baseMapper.queryContact(page, companyId, departmentId, codeName, dutyCode, status);
    }

    public List<ResponsibilityLovDTO> queryResponsibility(Page page,
                                                          Long companyId,
                                                          Long departmentId,
                                                          String codeName) {
        return centerService.pageByCompanyAndDepartment(page, companyId, departmentId, null,
                null, null, codeName, null);
    }

    public List<DepartmentLovDTO> queryDepartmentLov(Page page, DepartmentLovQueryParams queryVO) {
        // 如果传了部门id就是查询单个部门，因此不需要查询总数
        if (queryVO.getId() != null){
            page.setSearchCount(Boolean.FALSE);
        }
        if (queryVO.getCompanyId() != null || queryVO.getSetOfBooksId() != null) {
            // 查询当前租户是否开启公司部门关联参数
            queryVO.setAssociateCompanyFlag(isOpenCompanyAssociate(queryVO.getTenantId()));
        } else {
            queryVO.setAssociateCompanyFlag(Boolean.FALSE);
        }
        List<DepartmentLovDTO> result = baseMapper.queryDepartmentLov(page, queryVO);
        if (!CollectionUtils.isEmpty(result) && queryVO.getId() != null){
            page.setTotal(1L);
        }
        return result;
    }

    public List<CompanyLovDTO> queryCompanyLov(Page page, CompanyLovQueryParams queryParams) {
        // 如果传了公司id就是查询单个记录，无需查询总记录数
        if (queryParams.getId() != null){
            page.setSearchCount(Boolean.FALSE);
        }
        if (queryParams.getDepartmentId() != null) {
            // 查询当前租户是否开启公司部门关联参数

            queryParams.setAssociateDepartmentFlag(isOpenCompanyAssociate(queryParams.getTenantId()));
        } else {
            queryParams.setAssociateDepartmentFlag(Boolean.FALSE);
        }
        List<CompanyLovDTO> result = baseMapper.queryCompanyLov(page, queryParams);
        // 设置公司类型名称
        if (!CollectionUtils.isEmpty(result)){
            Map<String, String> sysCodeMap = organizationClient.mapAllSysCodeValueByCode("1011");
            result.forEach(e -> e.setCompanyTypeName(sysCodeMap.get(e.getCompanyTypeCode())));
            if (queryParams.getId() != null){
                page.setTotal(1L);
            }
        }
        return result;
    }

    /**
     * 查询租户是否启用公司部门关联关系
     * @param tenantId
     * @return
     */
    public boolean isOpenCompanyAssociate(Long tenantId){
        String value = parameterSettingService.getTenantParameterValueByCode(tenantId,
                ParameterCodeConstants.COMPANY_UNIT_RELATION);
        String flag = "Y";
        return flag.equals(value);
    }

    public boolean isAssociate(Long tenantId, Long companyId, Long departmentId){
        if (isOpenCompanyAssociate(tenantId)){
            return SqlHelper.retBool(this.selectCount(
                    this.getWrapper().eq("company_id", companyId)
                            .eq("department_id" ,departmentId)
                            .eq("enabled", Boolean.TRUE)));
        } else {
            return true;
        }
    }

    /**
     * 根据部门id 查询启用的账套
     * @param departmentId 部门id
     * @return List<SetOfBooks>
     */
    public List<SetOfBooks> listSetOfBooksByDepartmentId(Long departmentId) {
        return baseMapper.listSetOfBooksByDepartmentId(departmentId);
    }
}
