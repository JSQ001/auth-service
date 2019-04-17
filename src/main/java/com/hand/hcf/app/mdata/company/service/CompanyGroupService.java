package com.hand.hcf.app.mdata.company.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.common.co.CompanyGroupCO;
import com.hand.hcf.app.core.domain.enumeration.LanguageEnum;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.conver.CompanyGroupCover;
import com.hand.hcf.app.mdata.company.domain.CompanyGroup;
import com.hand.hcf.app.mdata.company.dto.CompanyGroupDTO;
import com.hand.hcf.app.mdata.company.persistence.CompanyGroupMapper;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.service.SetOfBooksService;
import com.hand.hcf.app.mdata.utils.PatternMatcherUtil;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.mdata.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by silence on 2017/9/18.
 */
@Service
public class CompanyGroupService extends ServiceImpl<CompanyGroupMapper, CompanyGroup> {

    private final Logger log = LoggerFactory.getLogger(CompanyGroupService.class);

    @Autowired
    private CompanyGroupMapper companyGroupMapper;
    @Autowired
    private BaseI18nService baseI18nService;
    @Autowired
    private SetOfBooksService setOfBooksService;


    /**
     * 新建公司组
     *
     * @param companyGroup
     * @return CompanyGroup
     */
    public CompanyGroup addCompanyGroup(CompanyGroup companyGroup) {
        //  参数是否为空校验
        if (companyGroup.getId() != null) {
            throw new BizException(RespCode.COMPANY_GROUP_27001);
        }
        if (StringUtil.isNullOrEmpty(companyGroup.getCompanyGroupCode())) {
            throw new BizException(RespCode.COMPANY_GROUP_27002);
        }
        if (StringUtil.isNullOrEmpty(companyGroup.getCompanyGroupName())) {
            throw new BizException(RespCode.COMPANY_GROUP_27003);
        }


        PatternMatcherUtil.commonCodeCheck(companyGroup.getCompanyGroupCode());

        if (companyGroup.getSetOfBooksId() == null) {
            throw new BizException(RespCode.COMPANY_GROUP_27005);
        }
        //  Set租户ID
        companyGroup.setTenantId(OrgInformationUtil.getCurrentTenantId());
        //  校验是否获取到租户ID
        if (companyGroup.getTenantId() == null) {
            throw new BizException(RespCode.COMPANY_GROUP_27007);
        }
        //  查询判断是否重复
        CompanyGroup target = new CompanyGroup();
        target.setCompanyGroupCode(companyGroup.getCompanyGroupCode());
        target.setSetOfBooksId(companyGroup.getSetOfBooksId());
        CompanyGroup selectResult = companyGroupMapper.selectOne(target);
        //  查询结果不为空则有重复
        if (selectResult != null) {
            throw new BizException(RespCode.COMPANY_GROUP_27008);
        }
        //  插入检验后数据
        companyGroupMapper.insert(companyGroup);
        return companyGroup;
    }


    /**
     * 更新公司组
     *
     * @param companyGroup
     * @return CompanyGroup
     */
    @Transactional
    public CompanyGroup updateCompanyGroup(CompanyGroup companyGroup) {
        if (companyGroup.getId() == null) {
            throw new BizException(RespCode.COMPANY_GROUP_27009);
        }
        //  查询判断是否存在
        CompanyGroup result = companyGroupMapper.selectById(companyGroup);
        if (result == null || result.getDeleted() == true) {
            throw new BizException(RespCode.COMPANY_GROUP_27010);
        }
        //  检查是否输入Code
        if (!StringUtil.isNullOrEmpty(companyGroup.getCompanyGroupCode())) {
            PatternMatcherUtil.commonCodeCheckReg(companyGroup.getCompanyGroupCode());

            companyGroup.setCompanyGroupCode(companyGroup.getCompanyGroupCode());
        }
        try {
            companyGroupMapper.updateById(companyGroup);
        } catch (DuplicateKeyException exce) {
            throw new BizException(RespCode.COMPANY_GROUP_27011);
        }
        return companyGroup;
    }


    /**
     * 删除公司组
     *
     * @param id 主键ID
     * @return
     */
    public Boolean deleteCompanyGroup(Long id) {
        CompanyGroup companyGroup = new CompanyGroup();
        companyGroup.setId(id);
        CompanyGroup result = companyGroupMapper.selectOne(companyGroup);
        if (null != result && result.getDeleted() != true) { // 删除成功
            result.setDeleted(true);
            result.setCompanyGroupCode(result.getCompanyGroupCode() + "_DELETE_" + RandomStringUtils.randomNumeric(6));
            companyGroupMapper.updateById(result);
        } else { // 删除失败
            throw new BizException(RespCode.COMPANY_GROUP_27010);
        }
        // 返回成功标志
        return true;
    }


    /**
     * 公司组ID查询
     *
     * @param id 公司组ID
     * @return AccountSet
     */
    public CompanyGroupDTO findCompanyGroupById(Long id) {
        CompanyGroup companyGroup = baseI18nService.selectOneBaseTableInfoWithI18n(id, CompanyGroup.class);
        CompanyGroupDTO dto = CompanyGroupCover.toDTO(companyGroup);
        dto.setI18n(companyGroup.getI18n());
        //  set账套
        SetOfBooks setOfBooks = setOfBooksService.selectById(companyGroup.getSetOfBooksId());
        if (setOfBooks != null) {
            dto.setSetOfBooksCode(setOfBooks.getSetOfBooksCode());
            dto.setSetOfBooksName(setOfBooks.getSetOfBooksName());
        }
        quoteAttributeAssignment(dto);
        return dto;
    }

    /**
     * 公司组ID查询(多语言翻译后)
     *
     * @param id 公司组ID
     * @return AccountSet
     */
    public CompanyGroupDTO findTransCompanyGroupById(Long id) {
        CompanyGroup companyGroup = baseI18nService.selectOneTranslatedTableInfoWithI18n(id, CompanyGroup.class);
        CompanyGroupDTO dto = CompanyGroupCover.toDTO(companyGroup);
        dto.setI18n(companyGroup.getI18n());
        //  set账套
        SetOfBooks setOfBooks = setOfBooksService.selectById(companyGroup.getSetOfBooksId());
        if (setOfBooks != null) {
            dto.setSetOfBooksCode(setOfBooks.getSetOfBooksCode());
            dto.setSetOfBooksName(setOfBooks.getSetOfBooksName());
        }
        quoteAttributeAssignment(dto);
        return dto;
    }


    /**
     * 分页查询 公司组表条件查询
     *
     * @param setOfBooksId     账套ID
     * @param companyGroupCode 公司组代码
     * @param companyGroupName 公司组名称
     * @param page             分页对象
     * @return
     */
    public Page<CompanyGroup> findCompanyGroupByCode(Long setOfBooksId, String companyGroupCode, String companyGroupName, Long tenantId, Page<CompanyGroup> page) {
        List<CompanyGroup> list = companyGroupMapper.selectPage(page, new EntityWrapper<CompanyGroup>()
                .where("deleted = false")
                .eq(setOfBooksId != null, "set_of_books_id", setOfBooksId)
                .like("company_group_code", companyGroupCode)
                .like("company_group_name", companyGroupName)
                .eq("tenant_id", tenantId)
                .orderBy("company_group_code")
        );
        //  判断是否查询到数据
        if (CollectionUtils.isNotEmpty(list)) {
            /*List<CompanyGroup> i18ns = baseI18nService.convertListByLocale(list);*/
            List<CompanyGroup> i18ns = new ArrayList<>();
            list.stream().forEach((CompanyGroup companyGroup) -> {
                i18ns.add(baseI18nService.selectOneTranslatedTableInfoWithI18n(companyGroup.getId(), CompanyGroup.class));
            });
            page.setRecords(i18ns);
        }
        return page;
    }

    /**
     * 根据条件分页查询 公司组DTO条件查询
     *
     * @param setOfBooksId     账套ID
     * @param companyGroupCode 公司组代码
     * @param companyGroupName 公司组名称
     * @param enabled          是否启用
     * @param page             分页对象
     * @return
     */
    public Page<CompanyGroupDTO> findCompanyGroupByConditions(Long setOfBooksId, String companyGroupCode, String companyGroupName, Boolean enabled, Long tenantId, Page<CompanyGroupDTO> page) {
        List<CompanyGroup> list = companyGroupMapper.findCompanyGroupByConditions(setOfBooksId, companyGroupCode, companyGroupName, enabled, tenantId, page);
        //  判断是否查询到数据
        if (CollectionUtils.isNotEmpty(list)) {
            List<CompanyGroupDTO> companyGroupDTOs = new ArrayList<>();
            CompanyGroupDTO dto = null;
            for (CompanyGroup companyGroup : list) {
                companyGroup = baseI18nService.selectOneTranslatedTableInfoWithI18n(companyGroup.getId(), CompanyGroup.class);
                dto = new CompanyGroupDTO();
                BeanUtils.copyProperties(companyGroup, dto);
                companyGroupDTOs.add(quoteAttributeAssignment(dto));
            }
            page.setRecords(companyGroupDTOs);
        }
        return page;
    }

    private CompanyGroupDTO quoteAttributeAssignment(CompanyGroupDTO dto) {
        SetOfBooks setOfBooks = setOfBooksService.getSetOfBooksById(dto.getSetOfBooksId());
        if(setOfBooks != null) {
            dto.setSetOfBooksCode(setOfBooks.getSetOfBooksCode());
            dto.setSetOfBooksName(setOfBooks.getSetOfBooksName());
        }
        return dto;
    }

    public List<CompanyGroupCO> listCompanyGroupByCompanyId(Long companyId) {
        String language = OrgInformationUtil.getCurrentLanguage();
        if (!StringUtils.hasText(language)){
            language = LanguageEnum.ZH_CN.getKey();
        }
        List<CompanyGroupCO> result = baseMapper.listCompanyGroup(companyId,language);
        return result;
    }

    public CompanyGroupCO getCompanyGroupAndCompanyIdsByCompanyGroupId(Long companyGroupId, Boolean enabled) {
        String language = OrgInformationUtil.getCurrentLanguage();
        if (!StringUtils.hasText(language)){
            language = LanguageEnum.ZH_CN.getKey();
        }
        return baseMapper.getCompanyGroupAndCompanyIdsByCompanyGroupId(companyGroupId, language, enabled);
    }

    public List<CompanyGroupCO> listCompanyGroupAndCompanyIdsBySetOfBooksId(Long setOfBooksId, Boolean enabled) {

        List<CompanyGroup> companyGroups = this.selectList(new EntityWrapper<CompanyGroup>()
                .eq("setOfBooksId", setOfBooksId)
                .eq(enabled != null,"enabled", enabled));

        if (CollectionUtils.isNotEmpty(companyGroups)){
            List<CompanyGroupCO> collect = companyGroups.stream().map(e -> {
                CompanyGroupCO groupCO = new CompanyGroupCO();
                groupCO.setCompanyGroupCode(e.getCompanyGroupCode());
                groupCO.setCompanyGroupName(e.getCompanyGroupName());
                groupCO.setDescription(e.getDescription());
                groupCO.setId(e.getId());
                groupCO.setTenantId(e.getTenantId());
                groupCO.setSetOfBooksId(e.getSetOfBooksId());
                List<Long> list = baseMapper.listAssignCompanyIds(e.getId(),enabled);
                groupCO.setCompanyIds(list);
                return groupCO;
            }).collect(Collectors.toList());
            return collect;
        }
        return new ArrayList<>();

    }

    public List<CompanyGroupCO> listCompanyGroupAndCompanyIdsByCompanyGroupIds(List<Long> companyGroupIds, Boolean enabled) {
        List<CompanyGroupCO> result = new ArrayList<>();
        companyGroupIds.stream().forEach(e -> {
            CompanyGroupCO companyGroupCO= getCompanyGroupAndCompanyIdsByCompanyGroupId(e, enabled);
            if (companyGroupCO != null) {
                result.add(companyGroupCO);
            }
        });
        return result;
    }
}
