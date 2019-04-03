package com.hand.hcf.app.mdata.authorize.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.co.DepartmentCO;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.mdata.authorize.domain.FormCentralizedAuth;
import com.hand.hcf.app.mdata.authorize.dto.FormCentralizedAuthDTO;
import com.hand.hcf.app.mdata.authorize.persistence.FormCentralizedAuthMapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.department.service.DepartmentService;
import com.hand.hcf.app.mdata.externalApi.HcfFormTypeInterface;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.service.SetOfBooksService;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 单据集中授权服务类
 * @author shouting.cheng
 * @date 2019/1/22
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FormCentralizedAuthService extends BaseService<FormCentralizedAuthMapper, FormCentralizedAuth> {

    @Autowired
    private MapperFacade mapperFacade;
    @Autowired
    private HcfOrganizationInterface organizationInterface;
    @Autowired
    private HcfFormTypeInterface formTypeInterface;
    @Autowired
    private ContactService contactService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private SetOfBooksService setOfBooksService;

    /**
     * 新建
     * @param auth
     * @return
     */
    public FormCentralizedAuth createFormCentralizedAuth(FormCentralizedAuth auth) {
        if (auth.getId() != null) {
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        if (auth.getTenantId() == null) {
            auth.setTenantId(OrgInformationUtil.getCurrentTenantId());
        }
        auth.setTenantId(OrgInformationUtil.getCurrentTenantId());
        baseMapper.insert(auth);
        return auth;
    }

    /**
     * 更新
     * @param auth
     * @return
     */
    public FormCentralizedAuth updateFormCentralizedAuth(FormCentralizedAuth auth) {
        if (auth.getId() == null) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        baseMapper.updateAllColumnById(auth);
        return auth;
    }

    /**
     * 删除
     * @param id
     */
    public void deleteFormCentralizedAuthById(Long id) {
        if (baseMapper.selectById(id) == null) {
            throw new BizException(RespCode.VEN_AUTHORIZE_NOT_EXIST);
        }
        baseMapper.deleteById(id);
    }

    /**
     * 查询
     * @param id
     * @return
     */
    public FormCentralizedAuthDTO getFormCentralizedAuthById(Long id) {
        FormCentralizedAuth auth = baseMapper.selectById(id);
        return toDTO(auth);
    }

    /**
     * 条件查询
     * @param setOfBooksId
     * @param documentCategory
     * @param formId
     * @param companyId
     * @param unitId
     * @param mandatorId
     * @param baileeId
     * @param startDate
     * @param endDate
     * @param mybatisPage
     * @return
     */
    public List<FormCentralizedAuthDTO> pageFormCentralizedAuthByCondition(Long setOfBooksId, String documentCategory, Long formId, Long companyId, Long unitId, Long mandatorId, Long baileeId, ZonedDateTime startDate, ZonedDateTime endDate, Page mybatisPage) {
        List<FormCentralizedAuth> authList = baseMapper.selectPage(mybatisPage,
                new EntityWrapper<FormCentralizedAuth>()
                        .eq(setOfBooksId != null, "set_of_books_id", setOfBooksId)
                        .eq(companyId != null, "company_id", companyId)
                        .eq(unitId != null, "unit_id", unitId)
                        .eq(mandatorId != null, "mandator_id", mandatorId)
                        .eq(baileeId != null, "bailee_id", baileeId)
                        .eq(documentCategory != null, "document_category", documentCategory)
                        .eq(formId != null, "form_id", formId)
                        .ge(startDate != null, "start_date", startDate)
                        .le(endDate != null, "end_date", endDate)
        );
        return toDTOs(authList);
    }

    /**
     * domainList转dtoList
     * @param domainList
     * @return
     */
    private List<FormCentralizedAuthDTO> toDTOs(List<FormCentralizedAuth> domainList) {
        List<FormCentralizedAuthDTO> dtoList = new ArrayList<>();

        List<UserDTO> userCO1List = contactService.listUserDTOByUserId(domainList.stream().map(FormCentralizedAuth::getBaileeId).collect(Collectors.toList()));
        Map<Long, UserDTO> baileeMap = userCO1List.stream().collect(Collectors.toMap(UserDTO::getId, e -> e, (k1, k2) -> k2));

        List<UserDTO> userCO2List = contactService.listUserDTOByUserId(domainList.stream().map(FormCentralizedAuth::getMandatorId).collect(Collectors.toList()));
        Map<Long, UserDTO> mandatorMap = userCO2List.stream().collect(Collectors.toMap(UserDTO::getId, e -> e, (k1, k2) -> k2));

        List<CompanyCO> companyCOList = companyService.listByIds(domainList.stream().map(FormCentralizedAuth::getCompanyId).collect(Collectors.toList()));
        Map<Long, String> companyMap = companyCOList.stream().collect(Collectors.toMap(CompanyCO::getId, CompanyCO::getName, (k1, k2) -> k2));

        List<DepartmentCO> departmentCOList = departmentService.listDepartmentsByIds(domainList.stream().map(FormCentralizedAuth::getUnitId).collect(Collectors.toList()),null);
        Map<Long, String> unitMap = departmentCOList.stream().filter(e -> e.getId() != null).collect(Collectors.toMap(DepartmentCO::getId, DepartmentCO::getName, (k1, k2) -> k2));

        List<SysCodeValueCO> sysCodeValueCOList = organizationInterface.listAllSysCodeValueByCode("SYS_APPROVAL_FORM_TYPE");
        Map<String, String> sysCodeValueMap = sysCodeValueCOList.stream().collect(Collectors.toMap(SysCodeValueCO::getValue, SysCodeValueCO::getName, (k1, k2) -> k2));

        List<SetOfBooks> setOfBooksInfoCOList = setOfBooksService.getSetOfBooksListByIds(domainList.stream().map(FormCentralizedAuth::getSetOfBooksId).collect(Collectors.toList()));
        Map<Long, SetOfBooks> setOfBooksMap = setOfBooksInfoCOList.stream().collect(Collectors.toMap(SetOfBooks::getId, e -> e, (k1, k2) -> k2));

        for (FormCentralizedAuth domain : domainList){
            //转化相同属性
            FormCentralizedAuthDTO dto = mapperFacade.map(domain, FormCentralizedAuthDTO.class);

            //转化其他属性
            UserDTO baileeCO = baileeMap.get(domain.getBaileeId());
            if (baileeCO != null) {
                dto.setBaileeName(baileeCO.getFullName());
                dto.setBaileeCode(baileeCO.getEmployeeId());
            }
            UserDTO mandatorCO = mandatorMap.get(domain.getMandatorId());
            if (mandatorCO != null) {
                dto.setMandatorName(mandatorCO.getFullName());
                dto.setMandatorCode(mandatorCO.getEmployeeId());
            }
            dto.setCompanyName(companyMap.get(domain.getCompanyId()));
            dto.setUnitName(unitMap.get(domain.getUnitId()));
            if (dto.getFormId() != null) {
                dto.setFormName(formTypeInterface.getFormTypeNameByDocumentCategoryAndFormTypeId(dto.getDocumentCategory(), dto.getFormId()));
            }
            dto.setDocumentCategoryDesc(sysCodeValueMap.get(domain.getDocumentCategory()));

            SetOfBooks setOfBooksInfoCO = setOfBooksMap.get(domain.getSetOfBooksId());
            if (setOfBooksInfoCO != null) {
                dto.setSetOfBooksName(setOfBooksInfoCO.getSetOfBooksName());
                dto.setSetOfBooksCode(setOfBooksInfoCO.getSetOfBooksCode());
            }

            dtoList.add(dto);
        }

        return dtoList;
    }

    /**
     * domain转dto
     * @param domain
     * @return
     */
    private FormCentralizedAuthDTO toDTO(FormCentralizedAuth domain) {
        FormCentralizedAuthDTO dto = mapperFacade.map(domain, FormCentralizedAuthDTO.class);

        UserDTO bailee = contactService.getUserDTOByUserId(dto.getBaileeId());
        if (bailee != null) {
            dto.setBaileeName(bailee.getFullName());
            dto.setBaileeCode(bailee.getEmployeeId());
        }

        UserDTO mandator = contactService.getUserDTOByUserId(dto.getMandatorId());
        if (mandator != null) {
            dto.setMandatorName(mandator.getFullName());
            dto.setMandatorCode(mandator.getEmployeeId());
        }

        CompanyCO company = companyService.getById(dto.getCompanyId());
        if (company != null) {
            dto.setCompanyName(company.getName());
        }

        DepartmentCO department =departmentService.getDepartmentById(dto.getUnitId());
        if (department != null) {
            dto.setUnitName(department.getName());
        }

        if (dto.getFormId() != null) {
            dto.setFormName(formTypeInterface.getFormTypeNameByDocumentCategoryAndFormTypeId(dto.getDocumentCategory(), dto.getFormId()));
        }

        SysCodeValueCO sysCodeValue = organizationInterface.getValueBySysCodeAndValue("SYS_APPROVAL_FORM_TYPE", dto.getDocumentCategory());
        if (sysCodeValue != null) {
            dto.setDocumentCategoryDesc(sysCodeValue.getName());
        }

        SetOfBooks setOfBooksInfoCO = setOfBooksService.getSetOfBooksById(dto.getSetOfBooksId());
        if (setOfBooksInfoCO != null){
            dto.setSetOfBooksName(setOfBooksInfoCO.getSetOfBooksName());
            dto.setSetOfBooksCode(setOfBooksInfoCO.getSetOfBooksCode());
        }
        return dto;
    }
}
