package com.hand.hcf.app.mdata.authorize.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.authorize.domain.FormPersonalAuth;
import com.hand.hcf.app.mdata.authorize.dto.FormPersonalAuthDTO;
import com.hand.hcf.app.mdata.authorize.persistence.FormPersonalAuthMapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.externalApi.HcfFormTypeInterface;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.utils.RespCode;
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
 * 单据个人授权服务类
 * @author shouting.cheng
 * @date 2019/1/22
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FormPersonalAuthService extends BaseService<FormPersonalAuthMapper, FormPersonalAuth> {

    @Autowired
    private MapperFacade mapperFacade;
    @Autowired
    private HcfOrganizationInterface organizationInterface;
    @Autowired
    private HcfFormTypeInterface formTypeInterface;
    @Autowired
    private ContactService contactService;

    /**
     * 新建
     * @param auth
     * @return
     */
    public FormPersonalAuth createFormPersonalAuth(FormPersonalAuth auth) {
        if (auth.getId() != null) {
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        if (auth.getTenantId() == null) {
            auth.setTenantId(OrgInformationUtil.getCurrentTenantId());
        }
        baseMapper.insert(auth);
        return auth;
    }

    /**
     * 更新
     * @param auth
     * @return
     */
    public FormPersonalAuth updateFormPersonalAuth(FormPersonalAuth auth) {
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
    public void deleteFormPersonalAuthById(Long id) {
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
    public FormPersonalAuthDTO getFormPersonalAuthById(Long id) {
        FormPersonalAuth auth = baseMapper.selectById(id);
        return toDTO(auth);
    }

    /**
     * 条件查询
     * @param documentCategory
     * @param formId
     * @param baileeId
     * @param startDate
     * @param endDate
     * @param mybatisPage
     * @return
     */
    public List<FormPersonalAuthDTO> pageFormPersonalAuthByCondition(String documentCategory, Long formId, Long baileeId, ZonedDateTime startDate, ZonedDateTime endDate, Page mybatisPage) {
        List<FormPersonalAuth> authList = baseMapper.selectPage(mybatisPage,
                new EntityWrapper<FormPersonalAuth>()
                        .eq("mandator_id", OrgInformationUtil.getCurrentUserId())
                        .eq(documentCategory != null, "document_category", documentCategory)
                        .eq(formId != null, "form_id", formId)
                        .eq(baileeId!= null, "bailee_id", baileeId)
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
    private List<FormPersonalAuthDTO> toDTOs(List<FormPersonalAuth> domainList) {
        List<FormPersonalAuthDTO> dtoList = new ArrayList<>();

        List<UserDTO> userCO1List = contactService.listUserDTOByUserId(domainList.stream().map(FormPersonalAuth::getBaileeId).collect(Collectors.toList()));
        Map<Long, UserDTO> baileeMap = userCO1List.stream().collect(Collectors.toMap(UserDTO::getId, e -> e, (k1, k2) -> k2));

        List<UserDTO> userCO2List = contactService.listUserDTOByUserId(domainList.stream().map(FormPersonalAuth::getMandatorId).collect(Collectors.toList()));
        Map<Long, UserDTO> mandatorMap = userCO2List.stream().collect(Collectors.toMap(UserDTO::getId, e -> e, (k1, k2) -> k2));

        List<SysCodeValueCO> sysCodeValueCOList = organizationInterface.listAllSysCodeValueByCode("SYS_APPROVAL_FORM_TYPE");
        Map<String, String> sysCodeValueMap = sysCodeValueCOList.stream().collect(Collectors.toMap(SysCodeValueCO::getValue, SysCodeValueCO::getName, (k1, k2) -> k2));

        for (FormPersonalAuth domain : domainList){
            //转化相同属性
            FormPersonalAuthDTO dto = mapperFacade.map(domain, FormPersonalAuthDTO.class);

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
            if (dto.getFormId() != null) {
                dto.setFormName(formTypeInterface.getFormTypeNameByDocumentCategoryAndFormTypeId(dto.getDocumentCategory(), dto.getFormId()));
            }
            dto.setDocumentCategoryDesc(sysCodeValueMap.get(domain.getDocumentCategory()));

            dtoList.add(dto);
        }
        return dtoList;
    }

    /**
     * domain转dto
     * @param domain
     * @return
     */
    private FormPersonalAuthDTO toDTO(FormPersonalAuth domain) {
        FormPersonalAuthDTO dto = mapperFacade.map(domain, FormPersonalAuthDTO.class);

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

        if (dto.getFormId() != null) {
            dto.setFormName(formTypeInterface.getFormTypeNameByDocumentCategoryAndFormTypeId(dto.getDocumentCategory(), dto.getFormId()));
        }
        SysCodeValueCO sysCodeValue = organizationInterface.getValueBySysCodeAndValue("SYS_APPROVAL_FORM_TYPE", dto.getDocumentCategory());
        if (sysCodeValue != null) {
            dto.setDocumentCategoryDesc(sysCodeValue.getName());
        }
        return dto;
    }
}
