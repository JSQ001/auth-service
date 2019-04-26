package com.hand.hcf.app.mdata.legalEntity.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
/*import com.hand.hcf.app.client.attachment.AttachmentCO;*/
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.legalEntity.conver.LegalEntityConver;
import com.hand.hcf.app.mdata.legalEntity.domain.LegalEntity;
import com.hand.hcf.app.mdata.legalEntity.dto.LegalEntityDTO;
import com.hand.hcf.app.mdata.legalEntity.persistence.LegalEntityMapper;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.persistence.SetOfBooksMapper;
import com.hand.hcf.app.mdata.setOfBooks.service.SetOfBooksService;
import com.hand.hcf.app.mdata.system.constant.CacheConstants;
import com.hand.hcf.app.mdata.utils.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 法人实体业务逻辑层
 * Created by Strive on 17/9/4.
 */
@Service
@Transactional
@CacheConfig(cacheNames = {CacheConstants.LEGAL_ENTITY})
public class LegalEntityService extends BaseService<LegalEntityMapper,LegalEntity> {

    private final Logger log = LoggerFactory.getLogger(LegalEntityService.class);

    @Autowired
    private LegalEntityMapper legalEntityMapper;

    @Autowired
    @Lazy
    private SetOfBooksService setOfBooksService;

    @Autowired
    private BaseI18nService baseI18nService;

    @Autowired
    @Lazy
    private CompanyService companyService;

    @Autowired
    private LegalEntityCacheService legalEntityCacheService;

    @Autowired
    private HcfOrganizationInterface organizationInterface;

    @Autowired
    private SetOfBooksMapper setOfBooksMapper;

    /**
     * 新增或修改法人实体
     *
     * @param legalEntityDTO：法人实体视图对象
     * @return
     */
    public LegalEntityDTO addOrUpdateLegalEntity(LegalEntityDTO legalEntityDTO, Long tenantId, boolean isValidation) {
        // 验证父级法人实体是否存在
        if (null != legalEntityDTO.getParentLegalEntityId()) {
            LegalEntity parent = legalEntityMapper.selectById(legalEntityDTO.getParentLegalEntityId());
            if (null == parent) {
                throw new BizException(RespCode.PARENT_LEGAL_ENTITY_NULL);
            }
        }

        LegalEntity legalEntity = null;
        boolean isNameValidation = true;
        boolean isTaxpayerNumberValidation = true;
        boolean isInsert = true;
        if (null != legalEntityDTO.getId()) { // 修改
            isInsert = false;
            legalEntity = legalEntityMapper.selectById(legalEntityDTO.getId());
            if (null == legalEntity) {
                throw new BizException(RespCode.LEGAL_ENTITY_NULL);
            }
            if (legalEntity.getEntityName().equals(legalEntityDTO.getEntityName())) {
                isNameValidation = false;
            }
            // 验证纳税人识别号是否存在
            if (!legalEntity.getTaxpayerNumber().equals(legalEntityDTO.getTaxpayerNumber())
                    && isValidationEntityTaxpayerNumber(legalEntityDTO.getTaxpayerNumber())) {
                throw new BizException(RespCode.TAX_PAYER_NUMBER_NULL);
            }
            //我们收集了一版法人实体的数据，客户这边存在多个法人实体用同一个银行账号的情况，所以去掉唯一性校验
            // 验证纳税人银行卡号是否存在
//            if(!legalEntity.getAccountNumber().equals(legalEntityDTO.getAccountNumber())
//                    && isValidationEntityAccountNumber(legalEntityDTO.getAccountNumber())){
//                throw new BizException(RespCode.LEGAL_ENTITY_ACCOUNT_NUMBER_EXIST);
//            }
            isTaxpayerNumberValidation = false;
            if(!legalEntityDTO.getEnabled() && !legalEntity.getEnabled().equals(legalEntityDTO.getEnabled())){
                //已被使用的法人实体不可被禁用
                int count = companyService.selectCount(new EntityWrapper<Company>().eq("legal_entity_id",legalEntity.getId()));
                if(count > 0){
                    throw new BizException(RespCode.LEGAL_ENTITY_HAS_BEEN_USED);
                }
            }
        }

        // 验证法人实体名称是否存在
        if (isNameValidation) {
            if (isValidationEntityName(tenantId, legalEntityDTO.getEntityName())) {
                throw new BizException(RespCode.LEGAL_ENTITY_NAME_NULL);
            }
        }

        // 是否验证纳税人识别号
        if (isValidation) {
            // 验证纳税人识别号是否存在
            if (isTaxpayerNumberValidation) {
                if (isValidationEntityTaxpayerNumber(legalEntityDTO.getTaxpayerNumber())) {
                    throw new BizException(RespCode.TAX_PAYER_NUMBER_NULL);
                }
                if(isValidationEntityAccountNumber(legalEntityDTO.getAccountNumber())){
                    throw new BizException(RespCode.LEGAL_ENTITY_ACCOUNT_NUMBER_EXIST);
                }
            }
        }

        legalEntity = LegalEntityConver.legalEntityDTOTolegalEntity(legalEntityDTO);

        if (isInsert) {
            legalEntity.setLegalEntityOid(UUID.randomUUID());
            legalEntity.setCreatedDate(ZonedDateTime.now());
        }
        legalEntity.setTenantId(tenantId);
        this.saveLegalEntity(legalEntity);
        legalEntityDTO = LegalEntityConver.legalEntityTolegalEntityDTO(legalEntity);
        legalEntityCacheService.evictTenantLegalEntity(tenantId);

        LegalEntityDTO result = quoteAttributeAssignment(legalEntityDTO);
        return result;
    }

    /**
     * 根据输入文本查询法人实体信息
     *
     * @param page：分页对象
     * @param keyword：文本
     * @return
     */
    public Page<LegalEntityDTO> findLegalEntityByKeyWord(Page page, UUID userOid, String keyword) {
        CompanyCO companyCO = companyService.getCompanyByUserOid(userOid);
        EntityWrapper<LegalEntity> ew = new EntityWrapper();
        ew.where(" tenant_id = {0} ", companyCO.getTenantId()).and(" deleted = 0 ").and(" entity_name LIKE {0}", "%" + keyword + "%").or(" address LIKE {0}", "%" + keyword + "%")
                .or(" taxpayer_number LIKE {0}", "%" + keyword + "%").or(" account_bank LIKE {0}", "%" + keyword + "%").or(" telephone LIKE {0}", "%" + keyword + "%")
                .or(" account_number LIKE {0}", "%" + keyword + "%");
        Page<LegalEntity> legalEntityPage = page;
        legalEntityPage.setRecords(baseI18nService.convertListByLocale(legalEntityMapper.selectPage(page, ew)));

        if (CollectionUtils.isNotEmpty(legalEntityPage.getRecords())) {
            List<LegalEntityDTO> legalEntityDTOs = new ArrayList<>();
            LegalEntityDTO legalEntityDTO = null;
            for (LegalEntity legalEntity : legalEntityPage.getRecords()) {
                legalEntityDTO = LegalEntityConver.legalEntityTolegalEntityDTO(legalEntity);
                legalEntityDTOs.add(quoteAttributeAssignment(legalEntityDTO));
            }
            page.setRecords(legalEntityDTOs);
        }
        return page;
    }

    /**
     * 根据法人实体ID查询法人实体信息
     *
     * @param legalEntityId：法人实体ID
     * @return
     */
    public LegalEntityDTO getLegalEntity(Long legalEntityId) {
        if (null == legalEntityId) {
            return null;
        }
        LegalEntity result = selectOneTranslatedTableInfoWithI18n(legalEntityId);
        LegalEntityDTO legalEntityDTO = null;
        if (null != result) {
            legalEntityDTO = LegalEntityConver.legalEntityTolegalEntityDTO(result);
            return quoteAttributeAssignment(legalEntityDTO);
        }
        return null;
    }

    /**
     * 根据法人实体ID删除法人实体信息
     *
     * @param legalEntityId：法人实体ID
     */
    public void deleteLegalEntity(Long legalEntityId) {
        LegalEntity result = this.selectOneTranslatedTableInfoWithI18n(legalEntityId);
        if (null != result) {
            result.setDeleted(true);
            this.saveLegalEntity(result);
        }
    }

    /**
     * 验证法人实体名称是否存在
     *
     * @param entityName：法人实体名称
     * @return：是否存在
     */
    public boolean isValidationEntityName(Long tenantId, String entityName) {
        LegalEntity legalEntity = new LegalEntity();
        legalEntity.setTenantId(tenantId);
        legalEntity.setEntityName(entityName);
        legalEntity.setDeleted(false);
        return null != legalEntityMapper.selectOne(legalEntity);
    }

    /**
     * 验证法人实体纳税人识别号是否存在
     *
     * @param taxpayerNumber：纳税人识别号
     * @return:是否存在
     */
    public boolean isValidationEntityTaxpayerNumber(String taxpayerNumber) {
        LegalEntity legalEntity = new LegalEntity();
        legalEntity.setTaxpayerNumber(taxpayerNumber);
        legalEntity.setDeleted(false);
        return null != legalEntityMapper.selectOne(legalEntity);
    }

    public boolean isValidationEntityAccountNumber(String accountNumber){
        LegalEntity legalEntity = new LegalEntity();
        legalEntity.setAccountNumber(accountNumber);
        legalEntity.setDeleted(false);
        return null != legalEntityMapper.selectOne(legalEntity);
    }

    /**
     * 根据公司id和公司oid或法人实体oid查询法人实体信息
     * //     * @param companyId：公司id
     *
     * @param companyReceiptedOid：公司oid或法人实体oid
     * @return Long companyId,
     */
    public LegalEntityDTO findLegalEntityByOid(UUID companyReceiptedOid) {
        if (null == companyReceiptedOid) {
            return null;
        }
        LegalEntityDTO legalEntityDTO = null;
        LegalEntity legalEntity = this.selectByLegalEntityOid(companyReceiptedOid);
        if (null != legalEntity) {
            legalEntity = selectOneTranslatedTableInfoWithI18n(legalEntity.getId());
        }
        legalEntityDTO = LegalEntityConver.legalEntityTolegalEntityDTO(legalEntity);
        legalEntityDTO = quoteAttributeAssignment(legalEntityDTO);
        return legalEntityDTO;
    }


    /**
     * 根据公司id和公司名称查询法人实体信息
     *
     * @param tenantId：租户id      //     * @param companyId：公司id
     * @param companyName：法人实体名称
     * @return Long companyId,
     */
    public LegalEntityDTO findOneByCompanyIdAndCompanyNameAndEnableTrue(Long tenantId, String companyName) {
        LegalEntityDTO legalEntityDTO = null;
        LegalEntity legalEntityParam = new LegalEntity();
        legalEntityParam.setTenantId(tenantId);
        legalEntityParam.setEntityName(companyName);
        legalEntityParam.setEnabled(true);
        LegalEntity legalEntity = legalEntityMapper.selectOne(legalEntityParam);
        legalEntityDTO = LegalEntityConver.legalEntityTolegalEntityDTO(legalEntity);
        legalEntityDTO = quoteAttributeAssignment(legalEntityDTO);
        return legalEntityDTO;
    }

    /**
     * 根据公司id和法人实体名称查询法人实体
     * //     * @param companyId：公司id
     *
     * @param companyName：法人实体名称
     * @return Long companyId,
     */
    public LegalEntityDTO findByCompanyName(Long tenantId, String companyName) {
        LegalEntity legalEntityParam = new LegalEntity();
        legalEntityParam.setTenantId(tenantId);
        legalEntityParam.setEntityName(companyName);
        LegalEntity legalEntity = legalEntityMapper.selectOne(legalEntityParam);
        if (null != legalEntity) {
            legalEntity = selectOneTranslatedTableInfoWithI18n(legalEntity.getId());
        }
        LegalEntityDTO legalEntityDTO = LegalEntityConver.legalEntityTolegalEntityDTO(legalEntity);
        legalEntityDTO = quoteAttributeAssignment(legalEntityDTO);
        return legalEntityDTO;
    }

    /**
     * 根据公司id和法人实体oid集合查询法人实体信息
     * //     * @param companyId：公司id
     *
     * @param companyReceiptedOids：法人实体oid集合
     * @return Long companyId,
     */
    public List<LegalEntityDTO> findByCompanyReceiptedOidIn(List<UUID> companyReceiptedOids) {
        List<LegalEntityDTO> legalEntityDTOs = new ArrayList<>();
        EntityWrapper ew = new EntityWrapper();
        ew.in("legal_entity_oid", companyReceiptedOids);
        List<LegalEntity> legalEntitys = legalEntityMapper.selectList(ew);
        legalEntitys = baseI18nService.convertListByLocale(legalEntitys);
        LegalEntityDTO legalEntityDTO = null;
        for (LegalEntity legalEntity : legalEntitys) {
            legalEntityDTO = LegalEntityConver.legalEntityTolegalEntityDTO(legalEntity);
            legalEntityDTOs.add(quoteAttributeAssignment(legalEntityDTO));
        }
        return legalEntityDTOs;
    }

    /**
     * 根据法人实体Oid集合查询启用的法人实体信息
     * //     * @param companyId：公司id
     *
     * @param companyReceiptedOids：法人实体oid集合
     * @return Long companyId,
     */
    public List<LegalEntityDTO> findByEnableTrueAndCompanyReceiptedOidIn(List<UUID> companyReceiptedOids) {
        List<LegalEntityDTO> legalEntityDTOs = new ArrayList<>();
        EntityWrapper ew = new EntityWrapper();
        ew.in("legal_entity_oid", companyReceiptedOids).and(" enabled = 1");
        List<LegalEntity> legalEntitys = legalEntityMapper.selectList(ew);
        legalEntitys = baseI18nService.convertListByLocale(legalEntitys);
        LegalEntityDTO legalEntityDTO = null;
        for (LegalEntity legalEntity : legalEntitys) {
            legalEntityDTO = LegalEntityConver.legalEntityTolegalEntityDTO(legalEntity);
            legalEntityDTOs.add(quoteAttributeAssignment(legalEntityDTO));
        }
        return legalEntityDTOs;
    }

    /**
     * 根据公司id查询法人实体信息
     *
     * @param tenantId：租户id //     * @param companyId：公司id
     * @return
     */
    //jiu.zhao redis
    //@Cacheable(key = "#tenantId.toString()")
    public List<LegalEntityDTO> findByCompanyId(Long tenantId) {
        List<LegalEntityDTO> legalEntityDTOs = new ArrayList<>();
        EntityWrapper ew = new EntityWrapper();
        ew.where(" tenant_id = {0}", tenantId).and(" enabled = 1 ");
        List<LegalEntity> legalEntitys = legalEntityMapper.selectList(ew);
        legalEntitys = baseI18nService.convertListByLocale(legalEntitys);
        LegalEntityDTO legalEntityDTO = null;
        for (LegalEntity legalEntity : legalEntitys) {
            legalEntityDTO = LegalEntityConver.legalEntityTolegalEntityDTO(legalEntity);
            legalEntityDTOs.add(quoteAttributeAssignment(legalEntityDTO));
        }
        return legalEntityDTOs;
    }


    /**
     * 根据公司id查询法人实体信息
     *
     * @param tenantId：租户id //     * @param companyId：公司id
     * @return
     */
    public List<LegalEntityDTO> findLegalEntityWithoutCompany(Long tenantId, UUID companyOid) {
        Long baseLegalEntityId = null;
        if (companyOid != null) {
            Company company = companyService.getByCompanyOidCache(companyOid);
            if (company == null) {
                throw new BizException(RespCode.LEGAL_ENTITY_NAME_NULL);
            }
            baseLegalEntityId = company.getLegalEntityId();
        }
        List<LegalEntityDTO> legalEntityDTOs = new ArrayList<>();
        EntityWrapper ew = new EntityWrapper();
        ew.where(" tenant_id = {0}", tenantId).and(" enabled = 1 ");
        List<LegalEntity> legalEntitys = legalEntityMapper.selectList(ew);
        Long finalBaseLegalEntityId = baseLegalEntityId;
        legalEntitys = legalEntitys.stream().filter(u ->
                companyService.countIsEnabledTrueCompanyByLegalEntityId(u.getId()) == 0 || (finalBaseLegalEntityId != null && finalBaseLegalEntityId.equals(u.getId()))).collect(Collectors.toList());
        legalEntitys = baseI18nService.convertListByLocale(legalEntitys);
        LegalEntityDTO legalEntityDTO = null;
        for (LegalEntity legalEntity : legalEntitys) {
            legalEntityDTO = LegalEntityConver.legalEntityTolegalEntityDTO(legalEntity);
            legalEntityDTOs.add(quoteAttributeAssignment(legalEntityDTO));
        }
        return legalEntityDTOs;
    }

    /**
     * 根据公司id查询法人实体信息
     *
     * @param tenantId：租户id //     * @param companyId：公司id
     * @return
     */
    public List<LegalEntityDTO> findByCompanyOrTenant(Long companyId, Long tenantId, Boolean useCompany) {
        EntityWrapper ew = new EntityWrapper();
        ew.where(" tenant_id = {0}", tenantId);
        List<LegalEntityDTO> legalEntityDTOs = new ArrayList<>();
        List<LegalEntity> legalEntitys = legalEntityMapper.selectList(ew);
        legalEntitys = baseI18nService.convertListByLocale(legalEntitys);
        LegalEntityDTO legalEntityDTO = null;
        for (LegalEntity legalEntity : legalEntitys) {
            legalEntityDTO = LegalEntityConver.legalEntityTolegalEntityDTO(legalEntity);
            legalEntityDTOs.add(quoteAttributeAssignment(legalEntityDTO));
        }
        return legalEntityDTOs;
    }

    /**
     * 根据公司id查询法人实体信息
     *
     * @param tenantId             ：租户id
     * @param legalEntityId：法人实体id //     * @param companyId：公司id
     * @return Long companyId,
     */
    public List<LegalEntityDTO> findParentLegalEntityByCompanyId(Long tenantId, Long legalEntityId, Long setOfBookID) {
        List<LegalEntityDTO> legalEntityDTOs = new ArrayList<>();
        EntityWrapper ew = new EntityWrapper();
        ew.where(" tenant_id = {0}", tenantId).and(" enabled = 1 ");
        ew.eq(setOfBookID != null, "set_of_books_id", setOfBookID);
        if (null != legalEntityId) {
            ew.ne("id", legalEntityId);
        }
        List<LegalEntity> legalEntitys = legalEntityMapper.selectList(ew);
        legalEntitys = baseI18nService.convertListByLocale(legalEntitys);
        LegalEntityDTO legalEntityDTO = null;
        for (LegalEntity legalEntity : legalEntitys) {
            legalEntityDTO = LegalEntityConver.legalEntityTolegalEntityDTO(legalEntity);
            legalEntityDTOs.add(quoteAttributeAssignment(legalEntityDTO));
        }
        return legalEntityDTOs;
    }

    /**
     * 根据公司id和是否启用状态降序查询法人实体信息
     *
     * @param tenantId：租户id //     * @param companyId：公司id
     * @return Long companyId
     */
    public List<LegalEntityDTO> findByCompanyIdOrderByEnableDesc(Long tenantId) {
        List<LegalEntityDTO> legalEntityDTOs = new ArrayList<>();
        EntityWrapper ew = new EntityWrapper();
        ew.where(" tenant_id = {0} ", tenantId).orderBy("enabled", false);
        List<LegalEntity> legalEntitys = legalEntityMapper.selectList(ew);
        legalEntitys = baseI18nService.convertListByLocale(legalEntitys);
        LegalEntityDTO legalEntityDTO = null;
        for (LegalEntity legalEntity : legalEntitys) {
            legalEntityDTO = LegalEntityConver.legalEntityTolegalEntityDTO(legalEntity);
            legalEntityDTOs.add(quoteAttributeAssignment(legalEntityDTO));
        }
        return legalEntityDTOs;
    }

    /**
     * 根据租户id和主语言查询法人实体列表信息(此方法用于校验开票信息抬头)
     *
     * @param tenantId：租户id
     * @return
     */
    public List<LegalEntityDTO> findByTenantIdAndMainLanguage(Long tenantId) {
        List<LegalEntityDTO> legalEntityDTOs = new ArrayList<>();
        EntityWrapper ew = new EntityWrapper();
        ew.where(" tenant_id = {0} ", tenantId).orderBy("enabled", false);
        List<LegalEntity> legalEntitys = legalEntityMapper.selectList(ew);
        if (CollectionUtils.isNotEmpty(legalEntitys)) {
            legalEntitys = baseI18nService.selectListTranslatedTableInfoWithI18n(legalEntitys.stream().map(LegalEntity::getId).collect(Collectors.toList()), LegalEntity.class);
            LegalEntityDTO legalEntityDTO = null;
            for (LegalEntity legalEntity : legalEntitys) {
                legalEntityDTO = LegalEntityConver.legalEntityTolegalEntityDTO(legalEntity);
                boolean flag = true;
                if (legalEntityDTO.getI18n() != null) {
                    for (Map.Entry<String, List<Map<String, String>>> entry : legalEntityDTO.getI18n().entrySet()) {
                        if ("entityName".equals(entry.getKey())) {
                            List<Map<String, String>> list = entry.getValue();
                            for (int i = 0; i < list.size(); i++) {
                                if (legalEntityDTO.getMainLanguage().equals(list.get(i).get("language"))) {
                                    legalEntityDTO.setEntityName(list.get(i).get("value"));
                                    flag = false;
                                    break;
                                }
                            }
                            if (!flag) {
                                break;
                            }
                        }
                    }
                }
                legalEntityDTOs.add(quoteAttributeAssignment(legalEntityDTO));
            }
        }
        return legalEntityDTOs;
    }


    /**
     * 根据公司id和法人实体oid和启用状态降序查询法人实体信息
     * //     * @param companyID：公司id
     *
     * @param companyReceiptedOids：法人实体oid
     * @param pageable：分页对象
     * @return
     */
    public org.springframework.data.domain.Page<LegalEntity> findByCompanyReceiptedOidInOrderByEnableDesc(List<UUID> companyReceiptedOids, Pageable pageable) {
        Page mybatisPage = PageUtil.getPage(pageable);
        List<LegalEntity> legalEntityList = legalEntityMapper.findLegalEntityByLegalEntityOidInOrderByEnableDesc(companyReceiptedOids, mybatisPage);
        return new PageImpl<LegalEntity>(legalEntityList, pageable, mybatisPage.getTotal());
    }


    /**
     * 根据公司id和是否启用状态统计条数信息
     *
     * @param company：公司
     * @param enable：是否启用
     * @return：条数
     */
    public Long countByCompanyCompanyOidAndStatus(Company company, boolean enable) {
        return legalEntityMapper.countByTenantIdAndEnable(company.getTenantId(), enable);
    }

    /**
     * 根据法人实体id统计子法人实体条数
     *
     * @param legalEntityId：法人实体id
     * @return
     */
    public Long countSubLegalEntityByLegalEntityId(Long legalEntityId) {
        return legalEntityMapper.countSubLegalEntityByLegalEntityId(legalEntityId);
    }

    public List<LegalEntityDTO> findByTenantId(Long tenantId) {
        Map<String, Object> map = new HashMap<>();
        map.put("tenant_id", tenantId);
        List<LegalEntity> legalEntities = legalEntityMapper.selectByMap(map);
        legalEntities = baseI18nService.convertListByLocale(legalEntities);
        List<LegalEntityDTO> legalEntityDTOs = new ArrayList<>();
        LegalEntityDTO legalEntityDTO = null;
        for (LegalEntity legalEntity : legalEntities) {
            legalEntityDTO = LegalEntityConver.legalEntityTolegalEntityDTO(legalEntity);
            legalEntityDTOs.add(quoteAttributeAssignment(legalEntityDTO));
        }
        return legalEntityDTOs;
    }

    /**
     * 根据法人实体id和上级法人实体获取 上级法人路径或子级法人路径
     *
     * @param legalEntity：法人实体对象
     * @param hasParent：是否有上级法人
     * @return
     */
    private List<String> getSiblingLegalEntityPathList(LegalEntity legalEntity, boolean hasParent) {
        List<String> siblingLegalEntityPathList = null;
        if (!hasParent) {
            siblingLegalEntityPathList = legalEntityMapper.findRootSiblingLegalEntityPathList(legalEntity.getId());
        } else {
            siblingLegalEntityPathList = legalEntityMapper.findSiblingCompanyPathList(legalEntity.getId(), legalEntity.getParentLegalEntityId());
        }
        return siblingLegalEntityPathList;
    }

    /**
     * 获取法人实体状态
     *
     * @param legalEntityId：法人实体id
     * @return
     */
    public boolean getLegalEntityState(Long legalEntityId) {
        return legalEntityMapper.getLegalEntityState(legalEntityId);
    }

    /**
     * 根据法人实体id查询多语言信息
     *
     * @param id：法人实体id
     * @return
     */
    public LegalEntity selectOneTranslatedTableInfoWithI18n(Long id) {
        return baseI18nService.selectOneTranslatedTableInfoWithI18n(id, LegalEntity.class);
    }

    /**
     * 根据角色id查询法人实体信息
     *
     * @param roleId：角色id
     * @return
     */
    public List<LegalEntity> findLegalEntityByRoleId(Long roleId) {
        List<LegalEntity> legalEntities = legalEntityMapper.findLegalEntityByRoleId(roleId);
        legalEntities = baseI18nService.convertListByLocale(legalEntities);
        return legalEntities;
    }

    /**
     * 根据账套id分页查询法人实体信息
     *
     * @param setOfBooksId：账套id
     * @param pageable：分页对象
     * @return
     */
    public Page<LegalEntityDTO> findLegalEntityBySetOfBooksId(Long setOfBooksId, Pageable pageable) {
        Page mybatisPage = PageUtil.getPage(pageable);
        List<LegalEntity> legalEntities = legalEntityMapper.findLegalEntityBySetOfBooksId(setOfBooksId, true, mybatisPage);
        legalEntities = baseI18nService.convertListByLocale(legalEntities);
        List<LegalEntityDTO> legalEntityDTOs = new ArrayList<>();
        LegalEntityDTO legalEntityDTO = null;
        for (LegalEntity legalEntity : legalEntities) {
            legalEntityDTO = LegalEntityConver.legalEntityTolegalEntityDTO(legalEntity);
            legalEntityDTOs.add(quoteAttributeAssignment(legalEntityDTO));
        }
        mybatisPage.setRecords(legalEntityDTOs);
        return mybatisPage;
    }

    /**
     * 引用属性赋值
     *
     * @param legalEntityDTO：法人实体视图对象
     * @return
     */
    private LegalEntityDTO quoteAttributeAssignment(LegalEntityDTO legalEntityDTO) {
        if (null == legalEntityDTO) {
            return null;
        }
        SetOfBooks setOfBooks = null;
        LegalEntity parentLegalEntity = null;
        AttachmentCO AttachmentCO = null;
        // 查询账套名称
        if (null != legalEntityDTO.getSetOfBooksId()) {
            setOfBooks = setOfBooksService.findTransSetOfBooksById(legalEntityDTO.getSetOfBooksId());
            if (null != setOfBooks) {
                legalEntityDTO.setSetOfBooksName(setOfBooks.getSetOfBooksName());
            }
        }
        // 查询上级法人实体名称
        if (null != legalEntityDTO.getParentLegalEntityId()) {
            parentLegalEntity = selectOneTranslatedTableInfoWithI18n(legalEntityDTO.getParentLegalEntityId());
            if (null != parentLegalEntity) {
                legalEntityDTO.setParentLegalEntityName(parentLegalEntity.getEntityName());
            }
        }
        // 查询附件信息
        if (null != legalEntityDTO.getAttachmentId()) {
            AttachmentCO = organizationInterface.getAttachmentById(legalEntityDTO.getAttachmentId());
            if (null != AttachmentCO) {
                legalEntityDTO.setFileURL(AttachmentCO.getFileUrl());
                legalEntityDTO.setThumbnailUrl(AttachmentCO.getThumbnailUrl());
                legalEntityDTO.setIconUrl(AttachmentCO.getIconUrl());
            }
        }
        return legalEntityDTO;
    }

    /**
     * 新增或修改法人实体对象
     *
     * @param legalEntity：法人实体对象
     * @return
     */
    public LegalEntity saveLegalEntity(LegalEntity legalEntity) {
        if (null != legalEntity.getId()) {
            legalEntityMapper.updateAllColumnById(legalEntity);
        } else {
            legalEntityMapper.insert(legalEntity);
        }
        this.reloadLegalEntityCache(legalEntity);
        return legalEntity;
    }

    /**
     * 重新加载法人实体缓存
     *
     * @param legalEntity：法人实体对象
     */
    public void reloadLegalEntityCache(LegalEntity legalEntity) {
        legalEntityCacheService.reloadCacheLegalEntityById(legalEntity);
        legalEntityCacheService.reloadCacheLegalEntityByOid(legalEntity);
    }

    /**
     * 根据Oid查询法人实体
     *
     * @param legalEntityOid
     * @return
     */
    public LegalEntity selectByLegalEntityOid(UUID legalEntityOid) {
        return legalEntityCacheService.getLegalEntityByOid(legalEntityOid);
    }

    /**
     * 根据租户id和关键词查询法人实体
     *
     * @param tenantId：租户id
     * @param keyword：关键字
     * @return
     */
    public List<LegalEntityDTO> findByTenantAndKeyword(Long tenantId, String keyword, Page page) {
        List<LegalEntityDTO> legalEntityDTOs = new ArrayList<>();
        List<LegalEntity> legalEntitys = legalEntityMapper.findByTenantIdAndNameAndEnabledTrue(tenantId, keyword, page);
        legalEntitys = baseI18nService.convertListByLocale(legalEntitys);
        LegalEntityDTO legalEntityDTO = null;
        for (LegalEntity legalEntity : legalEntitys) {
            legalEntityDTO = LegalEntityConver.legalEntityTolegalEntityDTO(legalEntity);
            legalEntityDTOs.add(quoteAttributeAssignment(legalEntityDTO));
        }
        return legalEntityDTOs;
    }

    public List<LegalEntity> selectByLegalEntityOids(List<UUID> legalEntityOids) {
        return legalEntityMapper.selectList(new EntityWrapper<LegalEntity>().in("legal_entity_oid", legalEntityOids));
    }

    public Page<BasicCO> pageLegalEntityByInfoResultBasic(String code, String name, Long selectId, String securityType, Long filterId, Page myBatisPage) {
        if(selectId != null){
            LegalEntity legalEntity = legalEntityMapper.selectById(selectId);
            if(legalEntity == null || legalEntity.getDeleted()){
                return myBatisPage;
            }
            BasicCO basicCO = BasicCO
                    .builder()
                    .id(legalEntity.getId())
                    .name(legalEntity.getTaxpayerNumber())
                    .code(legalEntity.getEntityName())
                    .build();
            myBatisPage.setRecords(Arrays.asList(basicCO));
        }else {
            List<BasicCO> list = baseMapper.pageLegalEntityByInfoResultBasic(filterId,code,name,myBatisPage);
            if(CollectionUtils.isNotEmpty(list)){
                myBatisPage.setRecords(list);
            }
        }
        return myBatisPage;
    }


    /**
     * 法人实体批量导入
     *
     * @param list
     * @return
     */
    public String importLegalEntityBatch(List<LegalEntityDTO> list) {

        StringBuffer stringBuffer = new StringBuffer();
        int i = 0;
        for (LegalEntityDTO legalEntityDTO : list) {
            i++;
            //法人实体名称
            if (legalEntityDTO.getEntityName() == null) {
                stringBuffer.append("序号" + i + "：法人实体名称不能为空;");
            }
            //开户行
            if (legalEntityDTO.getAccountBank() == null) {
                stringBuffer.append("序号" + i + "：开户行不能为空;");
            }
            //地址
            if (legalEntityDTO.getAddress() == null) {
                stringBuffer.append("序号" + i + "：地址不能为空;");
            }
            //电话
            if (legalEntityDTO.getTelePhone() == null) {
                stringBuffer.append("序号" + i + "：电话不能为空;");
            }
            //纳税人识别号
            if (legalEntityDTO.getTaxpayerNumber() == null) {
                stringBuffer.append("序号" + i + "：纳税人识别号不能为空;");
            }
            //银行卡号
            if (legalEntityDTO.getAccountNumber() == null) {
                stringBuffer.append("序号" + i + "：银行卡号不能为空;");
            }
            //账套
            if (legalEntityDTO.getSetOfBooksCode() == null) {
                stringBuffer.append("序号" + i + "：帐套不能为空;");
            }

            LegalEntity legalEntity;


            //纳税人识别号唯一性校验
            if (isValidationEntityTaxpayerNumber(legalEntityDTO.getTaxpayerNumber())) {
                stringBuffer.append("序号" + i + "：纳税人识别号已存在;");
            }

            if (isValidationEntityName(OrgInformationUtil.getCurrentTenantId(), legalEntityDTO.getEntityName())) {
                stringBuffer.append("序号" + i + "：法人实体已存在;");
            }

            if (isValidationEntityAccountNumber(legalEntityDTO.getAccountNumber())) {
                stringBuffer.append("序号" + i + "：法人银行卡号已存在;");
            }
            //校验账套为租户下账套
            SetOfBooks target = new SetOfBooks();
            target.setSetOfBooksCode(legalEntityDTO.getSetOfBooksCode());
            target.setTenantId(OrgInformationUtil.getCurrentTenantId());
            SetOfBooks setOfBooks = setOfBooksMapper.selectOne(target);

            if (setOfBooks == null) {
                stringBuffer.append("序号" + i + "：帐套不是当前租户下帐套;");
            }else{
                legalEntityDTO.setSetOfBooksId(setOfBooks.getId());
            }



            //取上级法人id
            if (StringUtils.isNotEmpty(legalEntityDTO.getParentLegalEntityName())) {
                legalEntity = new LegalEntity();
                legalEntity.setEntityName(legalEntityDTO.getParentLegalEntityName());
                legalEntity.setSetOfBooksId(setOfBooks.getId());
                legalEntity.setTenantId(OrgInformationUtil.getCurrentTenantId());
                legalEntity = legalEntityMapper.selectOne(legalEntity);
                if (legalEntity == null) {
                    stringBuffer.append("序号" + i + "：上级法人在当前帐套下不存在;");
                } else {
                    legalEntityDTO.setParentLegalEntityId(legalEntity.getParentLegalEntityId());
                }
            }

            if (stringBuffer.toString().length() > 0) {
                throw new RuntimeException(stringBuffer.toString());
            }
            addOrUpdateLegalEntity(legalEntityDTO, OrgInformationUtil.getCurrentTenantId(), true);

        }
        return "导入成功,共导入" + list.size() + "条";
    }
}
