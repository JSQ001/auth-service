

package com.hand.hcf.app.mdata.company.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.auth.implement.web.OauthControllerImpl;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.RandomUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.domain.CompanyGroupAssign;
import com.hand.hcf.app.mdata.company.domain.CompanyLevel;
import com.hand.hcf.app.mdata.company.domain.CompanySecurity;
import com.hand.hcf.app.mdata.company.dto.*;
import com.hand.hcf.app.mdata.company.persistence.CompanyMapper;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.contact.enums.EmployeeStatusEnum;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.currency.service.CurrencyI18nService;
import com.hand.hcf.app.mdata.currency.service.CurrencyRateService;
import com.hand.hcf.app.mdata.department.domain.Department;
import com.hand.hcf.app.mdata.department.domain.enums.DepartmentTypeEnum;
import com.hand.hcf.app.mdata.department.service.DepartmentRoleService;
import com.hand.hcf.app.mdata.department.service.DepartmentService;
import com.hand.hcf.app.mdata.department.service.DepartmentUserService;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.legalEntity.dto.LegalEntityDTO;
import com.hand.hcf.app.mdata.legalEntity.service.LegalEntityService;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.service.SetOfBooksService;
import com.hand.hcf.app.mdata.system.constant.CacheConstants;
import com.hand.hcf.app.mdata.system.constant.Constants;
import com.hand.hcf.app.mdata.system.enums.AttachmentType;
import com.hand.hcf.app.mdata.system.enums.SystemCustomEnumerationTypeEnum;
import com.hand.hcf.app.mdata.utils.PathUtil;
import com.hand.hcf.app.mdata.utils.PatternMatcherUtil;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.mdata.utils.VerifyCodeUtils;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@CacheConfig(cacheNames = {CacheConstants.COMPANY})
public class CompanyService extends BaseService<CompanyMapper, Company> {

    private final String REDIS_KEY = CacheConstants.COMPANY_REGISTER_VERIFY_CODE_PREFIX;

    @Autowired
    private MapperFacade mapper;


    @Autowired
    private CompanyCacheService companyCacheService;

    @Autowired
    private BaseI18nService baseI18nService;

    @Autowired
    private CompanySecurityService companySecurityService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CompanyLevelService companyLevelService;

    @Autowired
    private SetOfBooksService setOfBooksService;

    @Autowired
    private LegalEntityService legalEntityService;

    @Autowired
    private CurrencyRateService currencyRateService;

    @Autowired
    private CurrencyI18nService currencyI18nService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private CompanyGroupAssignService companyGroupAssignService;

    @Autowired
    private OauthControllerImpl oauthClient;

    @Autowired
    private DepartmentUserService departmentUserService;

    @Autowired
    private DepartmentRoleService departmentRoleService;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;

    @Autowired
    private ContactService contactService;

    public List<Company> findAll() {
        return selectList(null);
    }

    public CompanyDTO createCompany(CompanyDTO companyDTO) {
        Company company = companyDTOToCompany(companyDTO);
        company.setGroupCompanyOid(companyDTO.getGroupCompanyOid());
        company.setCompanyOid(UUID.randomUUID());
        company.setCreatedDate(ZonedDateTime.now());
        insert(company);
        baseI18nService.insertOrUpdateI18n(company.getI18n(), company.getClass(), company.getId());
        companySecurityService.createDefaultComapnySecutiry(company.getCompanyOid());

        return companyToCompanyDTO(company);
    }

    public void checkCompanyName(Long setOfbooksId, String companyName, Long companyId) {
        if (StringUtils.isEmpty(companyName)) {
            throw new BizException("6050001");
        }
        // 判断是否超过100字符
        if (companyName.trim().length() > 100) {
            throw new BizException("6050002");
        }
        List<Company> companys = baseMapper.selectList(new EntityWrapper<Company>()
                .eq("set_of_books_id", setOfbooksId)
                .eq("name", companyName));
        Company company = null;
        if (companys.size() > 0) {
            company = companys.get(0);
        }

        //新建
        if (companyId == null) {
            if (company != null) {
                throw new BizException("6050003");
            }
        } else {
            //修改
            if (company != null && company.getEnabled() && !companyId.equals(company.getId())) {
                throw new BizException("6050003");
            }
        }
    }

    private List<String> getSiblingCompanyPathList(Company company, boolean hasParent) {
        List<String> siblingCompanyPathList = null;
        if (!hasParent) {
            siblingCompanyPathList = baseMapper.selectRootSiblingCompanyPathList(company.getId());
        } else {
            siblingCompanyPathList = baseMapper.selectSiblingCompanyPathList(company.getId(), company.getParentCompanyId());
        }
        return siblingCompanyPathList;
    }

    @Transactional
    public CompanyDTO createCompanyNew(CompanyDTO companyDTO) {
        log.debug("Request to save Company with Admin User: {}", companyDTO);
        Company company = null;
        Long setOfBooksId = legalEntityService.getLegalEntity(companyDTO.getLegalEntityId()).getSetOfBooksId();
        SetOfBooks setOfBooks = setOfBooksService.findSetOfBooksById(setOfBooksId);
        // 验证公司名称
        this.checkCompanyName(setOfBooks.getId(), companyDTO.getName(), null);
        PatternMatcherUtil.commonCodeCheckReg(companyDTO.getCompanyCode());

        List<Company> companys = baseMapper.selectList(new EntityWrapper<Company>()
                .eq("tenant_id", companyDTO.getTenantId())
                .eq("company_code", companyDTO.getCompanyCode()));
        if (companys.size() > 0) {
            company = companys.get(0);
        }
        if (company != null) {
            throw new BizException(RespCode.E_121302);
        }
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
//        Tenant tenant = userService.findCurrentTenantByUSerOid(currentUserOid);
//        if (tenant == null) {
//            throw new BizException(RespCode.TENANT_NOT_EXIST);
//        }
        // 验证当前法人实体是否为启用状态
        boolean legalEntityState = legalEntityService.getLegalEntityState(companyDTO.getLegalEntityId());
        if (!legalEntityState) {  // 禁用状态
            throw new BizException(RespCode.E_120307);
        }
        company = new Company();
        company.setCompanyOid(UUID.randomUUID());
        company.setTenantId(tenantId);
        company.setName(companyDTO.getName());
        company.setCompanyCode(companyDTO.getCompanyCode());
        //拿取租户的公司类型

//        SysCode sysCode = sysCodeValueService.getByCode(SystemCustomEnumerationTypeEnum.COMPANY_TYPE.getId().toString());
//
//        if (sysCode != null) {
//            List<SysCodeValue> items = sysCodeValueService.listValueBySysCodeIdConditionEnabled(sysCode.getId(), true);
//            Long id = items.stream().filter(u -> "业务实体".equals(u.getName())).findFirst().get().getId();
//            company.setCompanyTypeId(id);
//        }
        company.setCompanyTypeCode("1");
        company.setCompanyLevelId(companyDTO.getCompanyLevelId());
        company.setParentCompanyId(companyDTO.getParentCompanyId());
        // 根据法人实体获取账套id
        company.setSetOfBooksId(setOfBooksId);
        company.setStartDateActive(companyDTO.getStartDateActive());
        company.setEndDateActive(companyDTO.getEndDateActive());
        company.setAddress(companyDTO.getAddress());
        company.setLegalEntityId(companyDTO.getLegalEntityId());
        company.setGroupCompanyOid(companyDTO.getGroupCompanyOid());
        company.setTaxId(companyDTO.getTaxId());
        company.setCreatedBy(OrgInformationUtil.getCurrentUserId());
        company.setCreatedDate(ZonedDateTime.now());
        company.setEnabled(companyDTO.getEnabled());
        // Calculate Depth
        Company parentCompany = null;
        if (company.getParentCompanyId() != null) {
            parentCompany = this.findOne(company.getParentCompanyId());
            Optional.ofNullable(parentCompany).orElseThrow(() -> new ValidationException(new ValidationError("parentCompany", "not.exists")));
            company.setDepth(parentCompany.getDepth() == null ? 1 : parentCompany.getDepth() + 1);
        } else {
            company.setDepth(1);
        }
        // Calculate Path
        List<String> siblingCompanyPathList = getSiblingCompanyPathList(company, parentCompany != null);
        company.setPath(PathUtil.calculatePath(3, siblingCompanyPathList, (parentCompany != null) ? parentCompany.getPath() : null, "Company"));

        insert(company);
        companyDTO.setId(company.getId());
        companyDTO.setCompanyOid(company.getCompanyOid());
        companyDTO.setPath(company.getPath());
        companyDTO.setDepth(company.getDepth());
        //创建公司默认币种汇率
        //exchangeRateService.createDefaultExchangeRate(companyId.getCompanyOid(), setOfBooks.getFunctionalCurrencyCode(), companyDTO.getBaseCurrencyName(), tenant.getId());

//        registerGuideService.init(currentUserOid, companyId.getCompanyOid());//记录注册配置
//        //创建默认表单
//        customFormService.initTenantCompanyDefaultForms(companyId.getCompanyOid(), tenant.getId(), currentUserOid);
//        registerGuideService.openDefaultService(companyId.getCompanyOid());
//
//        //关闭注册公司引导项,导入提示项
//        registerGuideService.finished(companyId.getCompanyOid());
//        registerGuideService.imported(companyId.getCompanyOid());
//        //初始化公司差旅行程标准
//        travelItineraryStandService.initCompanyDataByCompanyOid(Arrays.asList(companyId.getCompanyOid()));
//        //创建公司添加日志
//        dataOperationService.save(OrgInformationUtil.getCurrentUserOid(), companyToCompanyDTO(companyId),
//                messageTranslationService.getMessageDetailByCode(OrgInformationUtil.getCurrentLanguage(), DataOperationMessageKey.ADD_COMPANY_INFO,
//                        companyId.getName(), companyId.getId()), OperationEntityTypeEnum.COMPANY.getKey(), OperationTypeEnum.ADD.getKey(), tenant.getId());
        companyCacheService.evictTenantCompany(company.getTenantId());//失效租户列表缓存
        return companyDTO;
    }

    public CompanyDTO updateCompany(CompanyDTO companyDTO, UUID userOid) {
        companyCacheService.evictTenantCompany(companyDTO.getTenantId());
        companyCacheService.evictCompanyByCompanyOid(companyDTO.getCompanyOid());
        log.debug("Request to update Company : {}", companyDTO);

        Company company = baseMapper.getByQO(CompanyQO.builder().companyOid(companyDTO.getCompanyOid()).build()).get(0);
        if (companyDTO.getEnabled() != null && !companyDTO.getEnabled()) {    // 禁用
            // 判断此公司下是否有员工如果存在员工则不能禁用
            int count = contactService.listContactByCompanyOidAndStatus(companyDTO.getCompanyOid(), EmployeeStatusEnum.NORMAL.getId()).size();
            if (count > 0) {
                throw new BizException(RespCode.COMPANY_6030002);
            }
        }
        // 判断公司编码是否已存在
        if (!company.getCompanyCode().equals(companyDTO.getCompanyCode())) {
            if (baseMapper.getByQO(CompanyQO.builder().tenantId(companyDTO.getTenantId()).companyCode(companyDTO.getCompanyCode()).build()).size() > 0) {
                throw new BizException("6030009");
            }
        }

        checkCompanyName(companyDTO.getSetOfBooksId(), companyDTO.getName(), companyDTO.getId());

        updateCompanySecurity(companyDTO);

        company.setName(!StringUtils.isEmpty(companyDTO.getName()) ? companyDTO.getName() : company.getName());
        company.setTaxId(!StringUtils.isEmpty(companyDTO.getTaxId()) ? companyDTO.getTaxId() : company.getTaxId());
        company.setCompanyCode(!StringUtils.isEmpty(companyDTO.getCompanyCode()) ? companyDTO.getCompanyCode() : company.getCompanyCode());
        company.setCompanyTypeCode(null != companyDTO.getCompanyTypeCode() ? companyDTO.getCompanyTypeCode() : company.getCompanyTypeCode());
        company.setCompanyLevelId(companyDTO.getCompanyLevelId());
        company.setLegalEntityId(null != companyDTO.getLegalEntityId() ? companyDTO.getLegalEntityId() : company.getLegalEntityId());

        if (null != companyDTO.getLegalEntityId()) {
            // 判断法人实体状态是否启用
            boolean legalEntityState = legalEntityService.getLegalEntityState(companyDTO.getLegalEntityId());
            if (!legalEntityState) {
                throw new BizException(RespCode.E_120307);
            }

            // 根据当前公司获取法人实体
            company.setSetOfBooksId(legalEntityService.getLegalEntity(null != companyDTO.getLegalEntityId() ? companyDTO.getLegalEntityId() : company.getLegalEntityId()).getSetOfBooksId());
        }

        if (companyDTO.getParentCompanyId() != null) {
            if (company.getParentCompanyId() != null && !company.getParentCompanyId().equals(companyDTO.getParentCompanyId())) {
                //如果该公司下面还有子公司则不允许修改其父公司
                if (baseMapper.getByQO(CompanyQO.builder().parentCompanyId(company.getParentCompanyId()).build()).size() > 0) {
                    throw new BizException(RespCode.COMPANY_6030001);
                }
            }
            Company parentCompany = this.selectById(companyDTO.getParentCompanyId());
            //修改公司的path
            List<String> siblingCompanyPathList = getSiblingCompanyPathList(company, parentCompany != null);
            company.setPath(PathUtil.calculatePath(3, siblingCompanyPathList, (parentCompany != null) ? parentCompany.getPath() : null, "Company"));
            company.setParentCompanyId(companyDTO.getParentCompanyId());
            company.setDepth(parentCompany.getDepth() == null ? 1 : parentCompany.getDepth() + 1);

        } else {
            List<String> siblingCompanyPathList = getSiblingCompanyPathList(company, null != null);
            company.setPath(PathUtil.calculatePath(3, siblingCompanyPathList, null, "Company"));
            company.setParentCompanyId(null);
            company.setDepth(1);
        }

        //因为法人是不允许更新的，所以这一段基本可以不需要
        company.setStartDateActive(null != companyDTO.getStartDateActive() ? companyDTO.getStartDateActive() : company.getStartDateActive());
        company.setEndDateActive(companyDTO.getEndDateActive());
        company.setAddress(companyDTO.getAddress());
        company.setEnabled(null != companyDTO.getEnabled() ? companyDTO.getEnabled() : company.getEnabled());
        company.setI18n(null != companyDTO.getI18n() ? companyDTO.getI18n() : company.getI18n());
        updateById(company);
        baseI18nService.insertOrUpdateI18n(company.getI18n(), company.getClass(), company.getId());
        CompanyDTO dto = companyToCompanyDTO(company);
//        if (esCompanyIndexSerivce.isElasticSearchEnable()) {
//            esCompanyIndexSerivce.saveUserIndex(companyDTOtoCompanyInfo(dto));
//        }
        return dto;


    }

    private CompanySecurity updateCompanySecurity(CompanyDTO companyDTO) {
        CompanySecurity oldCompanySecurity = new CompanySecurity();
        CompanySecurity newCompanySecurity = new CompanySecurity();
        CompanySecurity companySecurity = companySecurityService.getTenantCompanySecurity(companyDTO.getTenantId());
        BeanUtils.copyProperties(companySecurity, oldCompanySecurity);
        if (companyDTO.getCreateDataType() != 0) {
            companySecurity.setCreateDataType(companyDTO.getCreateDataType());
            if (companyDTO.getNoticeType() != 0) {
                companySecurity.setNoticeType(companyDTO.getNoticeType());
            }
            companySecurity.setDimissionDelayDays(companyDTO.getDimissionDelayDays());
            companySecurity.setPasswordExpireDays(companyDTO.getPasswordExpireDays());
            companySecurity.setPasswordLengthMax(companyDTO.getPasswordLengthMax());
            companySecurity.setPasswordLengthMin(companyDTO.getPasswordLengthMin());
            companySecurity.setPasswordRepeatTimes(companyDTO.getPasswordRepeatTimes());
            companySecurity.setPasswordAttemptTimes(companyDTO.getPasswordAttemptTimes());
            companySecurity.setAutoUnlockDuration(companyDTO.getAutoUnlockDuration());
            companySecurity.setEnableEmailModify(companyDTO.getEnableEmailModify());
            companySecurity.setEnableMobileModify(companyDTO.getEnableMobileModify());
            companySecurity.setEnableEmailModify(companyDTO.getEnablePasswordModify());
            if (!StringUtils.isEmpty(companyDTO.getPasswordRule())) {
                companySecurity.setPasswordRule(companyDTO.getPasswordRule());
            }
            BeanUtils.copyProperties(companySecurity, newCompanySecurity);
            companySecurityService.updateById(companySecurity);
//            dataOperationService.save(OrgInformationUtil.getCurrentUserOid(),oldCompanySecurity,newCompanySecurity, OperationEntityTypeEnum.COMPANY_SECURITY.getKey(), OperationTypeEnum.UPDATE.getKey(),companyDTO.getTenantId(),newCompanySecurity.getTenantId().toString());
        }
        return companySecurity;
    }


    @Transactional
    public Company uploadCompanyLogo(UUID companyOid, MultipartFile file) {
        Company company = baseMapper.getByQO(CompanyQO.builder().companyOid(companyOid).build()).get(0);
        if (company == null) {
            throw new BizException(RespCode.COMPANY_NOT_EXIST);
        }
        AttachmentCO attachment;
        if (company.getLogoId() != null) {
            AttachmentCO AttachmentCO = hcfOrganizationInterface.getAttachmentById(company.getLogoId());
            hcfOrganizationInterface.removeFile(true, AttachmentCO.getFileUrl());
        }
        attachment = hcfOrganizationInterface.uploadStatic(file, AttachmentType.COMPANY_LOGO);
        company.setLogoId(attachment.getId());
        updateById(company);
        return company;
    }

    public VerifyCodeDTO generateVerifyCode() {
        String verifyCode = RandomUtil.generateNumeric(4);
        byte[] image = VerifyCodeUtils.outputImage(verifyCode);
        if (image == null) {
            throw new BizException(RespCode.COMPANY_6030007);
        }
        UUID uuid = UUID.randomUUID();
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key = (REDIS_KEY + uuid).getBytes(Charset.forName("utf8"));
                connection.set(key, verifyCode.getBytes(Charset.forName("utf8")));
                connection.expire(key, CacheConstants.cacheExpireMap.get(REDIS_KEY));
                return null;
            }
        });
        try {
            return VerifyCodeDTO.builder().attachmentOid(uuid).image(new String(Base64.getEncoder().encode(image), "UTF-8")).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Company findOne(Long id) {
        return selectById(id);
    }

    public Company getByCompanyOidCache(UUID companyOid) throws BizException {
        if (companyOid == null) {
            throw new BizException(RespCode.COMPANY_NOT_EXIST);
        }
        return companyCacheService.getByCompanyOid(companyOid);
    }

    public CompanyDTO getByCompanyOid(UUID companyOid) {
        log.debug("Request to get Company: {}", companyOid);
        Company company = baseMapper.getByQO(CompanyQO.builder().companyOid(companyOid).build()).get(0);
        CompanyDTO companyDTO = companyToCompanyDTO(company);
        return quoteAttributeAssignment(companyDTO);
    }

    /**
     * 递归创建部门
     *
     * @param departmentPath：部门路径
     * @param company：公司
     * @param managerMobile：手机号
     * @param userMap：用户map
     * @return
     */
    public Department saveDepartment(String departmentPath, Company company, String
            managerMobile, Map<String, UserDTO> userMap, String dataSourceType, UUID currentUserOid) {
        Department department = null;

        UserDTO manager = null;
        String departmentName = departmentPath.substring(departmentPath.lastIndexOf(Constants.DEPARTMENT_SPLIT) + 1);
        if (!StringUtils.isEmpty(departmentName)) {
            department = departmentService.findByPathAndCompanyCompanyOid(departmentPath, company.getCompanyOid(), DepartmentTypeEnum.FIND_ENABLN_DISABLE.getId());
            if (department == null) {
                department = new Department();
                Department parentDepartment = null;
                if (departmentPath.contains(Constants.DEPARTMENT_SPLIT)) {
                    String parentPath = departmentPath.substring(0, departmentPath.lastIndexOf(Constants.DEPARTMENT_SPLIT));
                    parentDepartment = saveDepartment(parentPath, company, "", null, dataSourceType, currentUserOid);
                }
                department.setParent(parentDepartment);
                //部门不属于公司
                department.setCompany(null);
                department.setName(departmentName);
                department.setPath(departmentPath);
                department.setStatus(DepartmentTypeEnum.ENABLE.getId());
                department.setDepartmentOid(UUID.randomUUID());
                department.setTenantId(company.getTenantId());
                department.setDataSource(dataSourceType);
                department.setDepartmentCode(null);
                if (!StringUtils.isEmpty(managerMobile)) {
                    manager = userMap.get(managerMobile);
                    department.setManager(manager);
                }
                departmentService.insert(department);
                if (userMap != null && userMap.size() > 0) {
                    for (UserDTO user : userMap.values()) {
                        departmentUserService.addOrUpdateUserDepartment(user.getId(), department.getId());
                    }
                }
                //初始化默认角色部门权限
//                financeRoleService.initDefaultFinanceRoleDepartment(companyId.getTenantId(), department.getDepartmentOid());
            } else {
                if (DepartmentTypeEnum.DISABLE.getId().equals(department.getStatus())) {
                    throw new ValidationException(new ValidationError("department.disable", "department disable"));
                }
                if (userMap != null) {
                    if (!StringUtils.isEmpty(managerMobile)) {
                        manager = userMap.get(managerMobile);
                        department.setManager(manager);
                    } else {
                        manager = department.getManager();
                    }

                    // 根据部门id查询关联用户信息
                    Set<UserDTO> users = departmentUserService.findUsersByDepartmentId(department.getId());
                    Map<UUID, UserDTO> existUsers = new HashMap<UUID, UserDTO>();
                    for (UserDTO u : users) {
                        existUsers.put(u.getUserOid(), u);
                    }
                    for (UserDTO u : userMap.values()) {
                        if (!existUsers.containsKey(u.getUserOid())) {
                            users.add(u);
                        }
                    }
                    // 判断用户是否为空、不为空则先清除关联关系
                    if (!CollectionUtils.isEmpty(users)) {
                        // 根据部门id删除关联关系
                        departmentUserService.removeUserDepartment(users.stream().map(UserDTO::getId).collect(Collectors.toList()), department.getId());
                    }
                    // 关联用户部门信息
                    for (UserDTO user : users) {
                        departmentUserService.addOrUpdateUserDepartment(user.getId(), department.getId());
                    }
                    department.setLastUpdatedDate(ZonedDateTime.now());
                    try {
                        departmentService.insert(department);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            departmentRoleService.upsertDepartmentManager(department.getId(), manager == null ? null : manager.getUserOid());
        } else {
            log.debug("departmentName is null");
            throw new ValidationException(new ValidationError("operation", "departmentName error"));
        }

        return department;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Department> saveDepartmentAndGetAll(String departmentPath, Company company, String
            managerMobile, Map<String, UserDTO> userMap) {
        List<Department> departmentDTOList = new ArrayList<>();
        Department department = null;
        UserDTO manager = null;
        String departmentName = departmentPath.substring(departmentPath.lastIndexOf(Constants.DEPARTMENT_SPLIT) + 1);
        department = departmentService.findByPathAndCompanyCompanyOid(departmentPath, company.getCompanyOid(), DepartmentTypeEnum.FIND_ENABLN_DISABLE.getId());
        if (department == null) {
            department = new Department();
            List<Department> parentDepartments = null;
            Department parentDepartment = null;
            if (departmentPath.contains(Constants.DEPARTMENT_SPLIT)) {
                String parentPath = departmentPath.substring(0, departmentPath.lastIndexOf(Constants.DEPARTMENT_SPLIT));
                parentDepartments = saveDepartmentAndGetAll(parentPath, company, "", null);
                departmentDTOList.addAll(parentDepartments);
                parentDepartment = departmentService.findByPathAndCompanyCompanyOid(parentPath, company.getCompanyOid(), DepartmentTypeEnum.FIND_ENABLN_DISABLE.getId());
            }
            department.setParent(parentDepartment);
            department.setCompany(company);
            department.setName(departmentName);
            department.setPath(departmentPath);
            department.setStatus(DepartmentTypeEnum.ENABLE.getId());
            department.setDepartmentOid(UUID.randomUUID());
            if (!org.apache.commons.lang3.StringUtils.isEmpty(managerMobile)) {
                manager = userMap.get(managerMobile);
                department.setManager(manager);
            }
            if (userMap != null && userMap.size() > 0) {
                department.setUsers(new HashSet<UserDTO>(userMap.values()));
            }
            department.setTenantId(company.getTenantId());
            departmentService.insert(department);
            departmentDTOList.add(department);
        } else {
            if (userMap != null) {
                if (!org.apache.commons.lang3.StringUtils.isEmpty(managerMobile)) {
                    manager = userMap.get(managerMobile);
                    department.setManager(manager);
                }
                Set<UserDTO> users = department.getUsers();
                //update for modify mobile,userdomain equals bug
                Map<UUID, UserDTO> existUsers = new HashMap<UUID, UserDTO>();
                for (UserDTO u : users) {
                    existUsers.put(u.getUserOid(), u);
                }
                for (UserDTO u : userMap.values()) {
                    if (!existUsers.containsKey(u.getUserOid())) {
                        users.add(u);
                    }
                }
                department.setUsers(users);
                department.setLastUpdatedDate(ZonedDateTime.now());
                departmentService.insert(department);
            }
        }
        departmentRoleService.upsertDepartmentManager(department.getId(), manager == null ? null : manager.getUserOid());
        return departmentDTOList;
    }

    /**
     * 获取公司统计信息
     *
     * @param company
     * @return
     */
    public Map<String, Long> getCompanyCountInfo(Company company) {
        Long tenantId = company.getTenantId();
        Map<String, Long> companyCountInfo = new HashMap<>();
        companyCountInfo.put("validUser", Long.valueOf(contactService.getTenantEnableContactCount(company.getTenantId(), EmployeeStatusEnum.NORMAL)));
        companyCountInfo.put("validDepartment", departmentService.countByTenantId(tenantId));
        companyCountInfo.put("validCorporation", legalEntityService.countByCompanyCompanyOidAndStatus(company, true));
        companyCountInfo.put("validCompany", countCompanyByTenantId(company.getTenantId()));
        return companyCountInfo;
    }

    /**
     * 根据租户id统计启用公司数量
     *
     * @param tenantId：租户id
     * @return
     */
    public Long countCompanyByTenantId(Long tenantId) {
        return baseMapper.countCompanyByTenantId(tenantId);
    }

    public List<ClientCO> getMyCompanyClient(UUID companyOid) {

        Company company = baseMapper.getByQO(CompanyQO.builder().companyOid(companyOid).build()).get(0);
        if (company == null) {
            throw new BizException(RespCode.COMPANY_NOT_EXIST);
        }
        //jiu.zhao  三方接口修改 20190326
        return oauthClient.listCompanyClientByCompanyOid(companyOid.toString());
    }

    public List<ClientCO> getTenantClient(Long tenantId) {
        List<ClientCO> tenantClient = oauthClient.listTenantClientByTenantId(tenantId);
        return tenantClient;
    }

    /**
     * 根据法人实体id查询公司信息
     *
     * @param legalEntityId：法人实体信息
     * @return
     */
    public Page<CompanyDTO> findCompanyByLegalEntityId(Long legalEntityId, String keyword, Pageable pageable) {
        Page mybatisPage = PageUtil.getPage(pageable);

        List<Company> companies = baseMapper.getByQO(CompanyQO.builder().legalEntityId(legalEntityId).
                name(keyword).build(), mybatisPage);
        List<CompanyDTO> companyDTOs = new ArrayList<>();
        CompanyDTO companyDTO = null;
        for (Company company : companies) {
            companyDTO = companyToCompanyDTO(company);
            companyDTOs.add(quoteAttributeAssignment(companyDTO));
        }
        mybatisPage.setRecords(companyDTOs);
        return mybatisPage;
    }

    public Page<CompanyDTO> getCompanyBySetOfBooksIdAndCondition(Long setOfBooksId, String companyCode, String name, String companyCodeFrom, String companyCodeTo, Long companyGroupId, Pageable pageable) {
        Page mybatisPage = PageUtil.getPage(pageable);

        List<Long> restrictionCompanyIds = new ArrayList<>();
        if (companyGroupId != null) {
            List<CompanyGroupAssign> groupAssigns = companyGroupAssignService.findCompanyGroupByCompanyGroupId(companyGroupId);
            restrictionCompanyIds = groupAssigns.stream().map(CompanyGroupAssign::getCompanyId).collect(Collectors.toList());
        }
        List<Company> companies = baseMapper.findAllBySetOfBooksId(setOfBooksId, name, companyCode, companyCodeFrom, companyCodeTo, restrictionCompanyIds, null, mybatisPage);
        List<CompanyDTO> companyDTOs = new ArrayList<>();
        CompanyDTO companyDTO = null;
        for (Company company : companies) {
            companyDTO = companyToCompanyDTO(company);
            companyDTOs.add(quoteAttributeAssignment(companyDTO));
        }

        mybatisPage.setRecords(companyDTOs);
        return mybatisPage;
    }


    /**
     * 根据条件分页查询公司信息
     *
     * @param tenantId：租户id
     * @param companyCode：公司code
     * @param name：公司名称
     * @param setOfBooksId：账套id
     * @param legalEntityId：法人实体id
     * @param pageable：分页对象
     * @return
     */
    public Page<CompanyDTO> findCompanyByTerm(Long tenantId, String companyCode, String name, Long setOfBooksId, Long legalEntityId, Boolean enabled, Pageable pageable) {
        Page mybatisPage = PageUtil.getPage(pageable);

        List<Company> companies = baseMapper.getByQO(CompanyQO.builder().tenantId(tenantId).
                setOfBooksId(setOfBooksId).
                legalEntityId(legalEntityId).
                companyCode(companyCode).
                name(name).
                enabled(enabled).build(), mybatisPage);
        List<CompanyDTO> companyDTOs = new ArrayList<>();
        CompanyDTO companyDTO = null;
        for (Company company : companies) {
            companyDTO = companyToCompanyDTO(company);
            companyDTOs.add(quoteAttributeAssignment(companyDTO));
        }
        mybatisPage.setRecords(companyDTOs);
        return mybatisPage;
    }

    /**
     * 集团租户为公司分配值列表
     *
     * @param tenantId
     * @param name
     * @param source          当只选择了一个值列表时不为空，多个时为空，以此判断是否需要对查询的公司进行已分配过滤
     * @param companyCode
     * @param companyLevelId
     * @param legalEntityId
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param pageable
     * @return
     */
    public Page<CompanyDTO> findCompanyForEnumerationDeploy(Long tenantId, String name, Long source, String companyCode, Long companyLevelId, Long legalEntityId, String companyCodeFrom, String companyCodeTo, Pageable pageable) {
        Page mybatisPage = PageUtil.getPage(pageable);

        List<Company> companies =
                baseMapper.getCompanyWithoutEnumeration(tenantId, name, source, companyCode, companyLevelId, legalEntityId, companyCodeFrom, companyCodeTo, mybatisPage);
        List<CompanyDTO> companyDTOs = new ArrayList<>();
        CompanyDTO companyDTO = null;
        for (Company company : companies) {
            companyDTO = companyToCompanyDTO(company);
            companyDTOs.add(quoteAttributeAssignment(companyDTO));
        }
        mybatisPage.setRecords(companyDTOs);
        return mybatisPage;

    }

    /**
     * 集团租户为公司分配公告模板
     *
     * @param tenantId
     * @param name
     * @param source          当只选择了一个值列表时不为空，多个时为空，以此判断是否需要对查询的公司进行已分配过滤
     * @param companyCode
     * @param companyLevelId
     * @param legalEntityId
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param pageable
     * @return
     */
    public Page<CompanyDTO> findCompanyForCarouselDeploy(Long tenantId, String name, Long source, String companyCode, Long companyLevelId, Long legalEntityId, String companyCodeFrom, String companyCodeTo, Pageable pageable) {
        Page mybatisPage = PageUtil.getPage(pageable);

        List<Company> companies =
                baseMapper.getCompanyWithCarousel(tenantId, name, source, companyCode, companyLevelId, legalEntityId, companyCodeFrom, companyCodeTo, mybatisPage);
        List<CompanyDTO> companyDTOs = new ArrayList<>();
        CompanyDTO companyDTO = null;
        for (Company company : companies) {
            companyDTO = companyToCompanyDTO(company);
            companyDTOs.add(quoteAttributeAssignment(companyDTO));
        }
        mybatisPage.setRecords(companyDTOs);
        return mybatisPage;
    }

    /**
     * 集团租户为公司分配公司级别
     *
     * @param tenantId
     * @param name
     * @param source          当只选择了一个公司级别时不为空，多个时为空，以此判断是否需要对查询的公司进行已分配过滤
     * @param companyCode
     * @param companyLevelId
     * @param legalEntityId
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param pageable
     * @return
     */
    public Page<CompanyDTO> findCompanyForLevelsDeploy(Long tenantId, String name, Long source, String companyCode, Long companyLevelId, Long legalEntityId, String companyCodeFrom, String companyCodeTo, Pageable pageable) {
        Page mybatisPage = PageUtil.getPage(pageable);

        List<Company> companies =
                baseMapper.getCompanyWithoutLevels(tenantId, name, source, companyCode, companyLevelId, legalEntityId, companyCodeFrom, companyCodeTo, mybatisPage);
        List<CompanyDTO> companyDTOs = new ArrayList<>();
        CompanyDTO companyDTO = null;
        for (Company company : companies) {
            companyDTO = companyToCompanyDTO(company);
            companyDTOs.add(quoteAttributeAssignment(companyDTO));
        }
        mybatisPage.setRecords(companyDTOs);
        return mybatisPage;
    }

    /**
     * 根据租户id查询公司信息
     *
     * @param currentTenantID：当前租户id
     * @return
     */
    public List<CompanyDTO> findTenantAllCompany(Long currentTenantID) {
        List<Company> companies = baseMapper.getByQO(CompanyQO.builder().tenantId(currentTenantID).build());
        List<CompanyDTO> companyDTOs = new ArrayList<>();
        CompanyDTO companyDTO = null;
        for (Company company : companies) {
            companyDTO = companyToCompanyDTO(company);
            companyDTOs.add(quoteAttributeAssignment(companyDTO));
        }
        return companyDTOs;
    }

    /**
     * 根据租户id查询公司信息 排序 先启用 后禁用
     */
    public List<CompanyDTO> findTenantAllCompanySorted(Long currentTenantID) {
        List<CompanyDTO> list = findTenantAllCompany(currentTenantID).stream().sorted((r1, r2) -> {
            if (r1.getEnabled() && !r2.getEnabled()) {
                return -1;
            }
            if (!r1.getEnabled() && r2.getEnabled()) {
                return 1;
            }
            return 0;
        }).collect(Collectors.toList());
        return list;
    }

    /**
     * 根据租户id查询公司名称和公司oid()
     * 业务逻辑
     * 1、老公司情况下查询法人实体名称和oid
     * 2、新公司情况下查询公司名称和oid
     *
     * @param tenantId 租户id
     * @return (公司名称和oid或法人实体名称和oid)
     */
    public Page<CompanySimpleDTO> getTenantAllCompanyNameAndOid(Long tenantId, String keyword, Boolean enabled, Pageable pageable) {
        Page mybatisPage = PageUtil.getPage(pageable);

        List<CompanySimpleDTO> companySimpleDTOs = new ArrayList<>();
        // 判断是否为老公司
//        if (tenantCompanyHisService.getTenantCompanyHisByTenantId(tenantId) != null) {    // 老公司
//            // 根据租户id查询法人实体信息
//            List<LegalEntityDTO> legalEntityDTOs = legalEntityService.findByTenantAndKeyword(tenantId, keyword, mybatisPage);
//            for (LegalEntityDTO legalEntityDTO : legalEntityDTOs) {
//                companySimpleDTOs.add(new CompanySimpleDTO(legalEntityDTO.getCompanyReceiptedOid(), legalEntityDTO.getEntityName()));
//            }
//            mybatisPage.setRecords(companySimpleDTOs);
//            return mybatisPage;
//        } else {  // 新公司
        // 根据租户id查询公司信息
        CompanyQO companyQO = CompanyQO.builder().tenantId(tenantId).name(keyword).build();
        if(enabled != null){
            companyQO.setEnabled(enabled);
        }
        List<Company> companies = baseMapper.getByQO(companyQO, mybatisPage);
        for (Company company : companies) {
            companySimpleDTOs.add(new CompanySimpleDTO(company.getCompanyOid(), company.getName()));
        }
        mybatisPage.setRecords(companySimpleDTOs);
        return mybatisPage;
        //}
    }

    public List<CompanyDTO> findAllCompanyByTenantIdAndCompanyType(Long currentTenantID, Long legalEntityId, String companyType, List<UUID> filterCompanyOids) {
        List<CompanyDTO> companyDTOS = new ArrayList<>();
        if (legalEntityId != null) {
            LegalEntityDTO legalEntity = legalEntityService.getLegalEntity(legalEntityId);
            if (legalEntity != null) {
                companyDTOS = this.findBySetOfBooksIdAndIsEnabledTrue2(legalEntity.getSetOfBooksId(), filterCompanyOids);
            }
        } else {
            companyDTOS = this.findTenantAllCompany(currentTenantID);
        }
        if (!StringUtils.isEmpty(companyType)) {
            companyDTOS = companyDTOS.stream().filter(u -> u.getCompanyTypeName().equals(companyType)).collect(Collectors.toList());
        }
        return companyDTOS;
    }

    public List<CompanyDTO> findBySetOfBooksIdAndIsEnabledTrue(Long setOfBookId) {
        List<Company> companies = baseMapper.getByQO(CompanyQO.builder().setOfBooksId(setOfBookId).build());
        List<CompanyDTO> companyDTOs = new ArrayList<>();
        CompanyDTO companyDTO = null;
        for (Company company : companies) {
            companyDTO = companyToCompanyDTO(company);
            companyDTOs.add(quoteAttributeAssignment(companyDTO));
        }
        return companyDTOs;
    }


    public List<CompanyDTO> findBySetOfBooksIdAndIsEnabledTrue2(Long setOfBookId, List<UUID> filterCompanyOids) {
        List<Company> companies = null;
        if (filterCompanyOids != null && filterCompanyOids.size() >= 1) {
            //正常，有当前公司时要筛选掉自己以及自己下属
            companies = baseMapper.findBySetOfBooksIdAndIsEnabledTrue(setOfBookId, filterCompanyOids.get(0).toString());
        } else {
            //新增时没有当前公司，只需要筛选当前账套（即选择法人之后）
            companies = baseMapper.getByQO(CompanyQO.builder().setOfBooksId(setOfBookId).build());
        }
        List<CompanyDTO> companyDTOs = new ArrayList<>();
        CompanyDTO companyDTO = null;
        for (Company company : companies) {
            companyDTO = companyToCompanyDTO(company);
            companyDTOs.add(quoteAttributeAssignment(companyDTO));
        }
        return companyDTOs;
    }

    /**
     * 查询公司或法人实体信息
     *
     * @return
     */
    public List<CompanyDTO> findCompanyOrLegalEntityInfo(Long companyId, Long tenantId) {
        // 查询公司信息
        return findTenantAllCompany(tenantId);
    }

    /**
     * 根据租户id查询公司信息
     *
     * @param currentTenantID：当前租户id
     * @return
     */
    public Page<Company> findByTenantIdAndNameLike(Long currentTenantID, String keyword, Boolean enabled, Pageable pageable) {
        Page mybatisPage = PageUtil.getPage(pageable);
        List<Company> companies;
        companies = baseMapper.getByQO(CompanyQO.builder().tenantId(currentTenantID).
                name(keyword).build(), mybatisPage);
        mybatisPage.setRecords(companies);
        return mybatisPage;
    }

    /**
     * @Author mh.z
     * @Date 2019/01/23
     * @Description 根据账套ID和公司名称/代码查找公司（公司名称和代码是模糊查找）
     *
     * @param setOfBooksId 账套ID
     * @param keyword 关键字（公司名称/代码）
     * @param enabled
     * @param pageable
     * @return
     */
    public Page<Company> findBySetOfBookAndNameLike(Long setOfBooksId, String keyword, Boolean enabled, Long ignoreCompanyId, Pageable pageable) {
        Wrapper<Company> wrapper = new EntityWrapper<Company>();

        // 账套ID
        wrapper.eq("set_of_books_id", setOfBooksId);
        // 公司名称和代码模糊匹配
        if (!StringUtils.isEmpty(keyword)) {
            wrapper.andNew().like("name", keyword)
                    .or().like("company_code", keyword);
        }
        //不包括ignoreCompanyId
        wrapper.ne(!StringUtils.isEmpty(ignoreCompanyId), "id", ignoreCompanyId);
        // 根据公司代码升序排
        wrapper.orderBy("company_code", true);
        // 根据生成的条件查找公司列表
        List<Company> companies = baseMapper.selectList(wrapper);

        Page mybatisPage = PageUtil.getPage(pageable);
        mybatisPage.setRecords(companies);
        return mybatisPage;
    }

    /**
     * 根据租户id、公司名称查询公司信息
     *
     * @param tenantId：租户id
     * @param name：公司名称
     * @return
     */
    public Company findByTenantIdAndName(Long tenantId, String name) {
        return baseMapper.getByQO(CompanyQO.builder().name(name).build()).get(0);
    }

    /**
     * 根据法人实体id统计公司条数
     *
     * @param legalEntityId：法人实体id
     * @return：条数
     */
    public Long countIsEnabledTrueCompanyByLegalEntityId(Long legalEntityId) {
        return baseMapper.countIsEnabledTrueCompanyByLegalEntityId(legalEntityId);
    }

    /**
     * 引用属性赋值方法 (公共方法)
     *
     * @param companyDTO
     * @return
     */
    public CompanyDTO quoteAttributeAssignmentPublic(CompanyDTO companyDTO) {
        return quoteAttributeAssignment(companyDTO);
    }


    public List<Company> findByCompanyOidIn(List<UUID> companyOids) {
        return baseMapper.findByCompanyOidIn(companyOids);
    }

    public Company findCompanyByUserOid(UUID userOid) {
        return baseMapper.findOneByuserOid(String.valueOf(userOid)).get(0);
    }

    public Page<Company> findByIdsIn(List<Long> companyIds, Pageable pageable) {
        Page mybatisPage = PageUtil.getPage(pageable);
        List<Company> companies = baseMapper.findByIdIn(companyIds, mybatisPage);
        mybatisPage.setRecords(companies);
        return mybatisPage;
    }

    public List<Company> findByIdsIn(List<Long> companyIds) {
        return baseMapper.findByIdIn(companyIds);
    }

    public String getCompanyRegistrationTokenForTest(UUID attachmentOid) {
        final String[] verifyCode = {""};
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key = (REDIS_KEY + attachmentOid).getBytes(Charset.forName("utf8"));
                byte[] val = connection.get(key);
                if (val == null) {
                    throw new ValidationException(new ValidationError("token", "verifyCode not exist"));
                }
                verifyCode[0] = new String(val, Charset.forName("utf8"));
                return null;
            }
        });
        return verifyCode[0];

    }

    public Company findCompanyByCompanyCodeAndTenantId(String companyCode, Long tenantId) {
        return baseMapper.getByQO(CompanyQO.builder().tenantId(tenantId).companyCode(companyCode).build()).get(0);
    }

    public List<CompanyDTO> getCompaniesByTenantId(Long tenantId, Page page) {
        List<Company> companies = baseMapper.getByQO(CompanyQO.builder().tenantId(tenantId).build(), page);
        List<CompanyDTO> companyDTOs = new ArrayList<>();
        CompanyDTO companyDTO = null;
        for (Company company : companies) {
            companyDTO = companyToCompanyDTO(company);
            companyDTOs.add(quoteAttributeAssignment(companyDTO));
        }
        return companyDTOs;
    }

    public List<CompanySobDTO> getCompaniesByTenantIdNotPage(Long tenantId) {
        List<Company> companies = baseMapper.getByQO(CompanyQO.builder().tenantId(tenantId).build());
        List<CompanySobDTO> companySobDTOS = new ArrayList<>();
        CompanySobDTO companySobDTO = null;
        for (Company company : companies) {
            companySobDTO = companyToCompanySobDTO(company);
            companySobDTOS.add(companySobDTO);
        }
        return companySobDTOS;
    }


    /**
     * 获取本级及下属公司 - 分页
     *
     * @param companyId
     * @param companyCode
     * @param companyName
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param page
     * @return
     */
    public Page<CompanyDTO> getCompanyChildrenAndOwnByCondition(Long companyId,
                                                                String companyCode,
                                                                String companyName,
                                                                String companyCodeFrom,
                                                                String companyCodeTo,
                                                                String keyWord,
                                                                Page page) {
        List<CompanyDTO> companyByCond = getCompanyByCond(companyId, false, companyCode, companyName, companyCodeFrom, companyCodeTo, keyWord, page);
        page.setRecords(companyByCond);
        return page;
    }

    /**
     * 获取下属公司信息
     *
     * @param companyId
     * @param ignoreOwn       忽略本公司
     * @param companyCode
     * @param companyName
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param page
     * @return
     */
    private List<CompanyDTO> getCompanyByCond(Long companyId,
                                              boolean ignoreOwn,
                                              String companyCode,
                                              String companyName,
                                              String companyCodeFrom,
                                              String companyCodeTo,
                                              String keyWord,
                                              Page page) {
        Set<Long> companyChildrenIdByCompanyIds = getCompanyChildrenIdByCompanyId(companyId);
        if (!ignoreOwn) {
            companyChildrenIdByCompanyIds.add(companyId);
        }
        if (CollectionUtils.isEmpty(companyChildrenIdByCompanyIds)) {
            return new ArrayList<>();
        }

        List<CompanyDTO> collect;
        if (page == null) {
            List<Company> companies =
                    baseMapper.getCompanyByCond(new ArrayList<Long>(companyChildrenIdByCompanyIds), companyCode, companyName, companyCodeFrom, companyCodeTo, keyWord);
            collect = companies.stream().map(company ->
                    companyToCompanyDTO(company)).collect(Collectors.toList());
        } else {
            List<Company> companies =
                    baseMapper.getCompanyByCond(new ArrayList<Long>(companyChildrenIdByCompanyIds), companyCode, companyName, companyCodeFrom, companyCodeTo, keyWord, page);
            collect = companies.stream().map(company ->
                    companyToCompanyDTO(company)).collect(Collectors.toList());
        }
        return collect;
    }

    /**
     * 根据公司ID获取下属公司ID (不包括本公司)
     *
     * @param companyId
     * @return
     */
    public Set<Long> getCompanyChildrenIdByCompanyId(Long companyId) {
        Set<Long> companyIds = new HashSet<>();
        companyIds.add(companyId);
        return getCompanyChildrenIdByCompanyIds(companyIds, null);
    }

    /**
     * 根据公司ID获取下属公司ID (不包括本公司)
     *
     * @param companyIds 公司ID
     * @param summaryIds
     * @return
     */
    private Set<Long> getCompanyChildrenIdByCompanyIds(Set<Long> companyIds, Set<Long> summaryIds) {
        if (summaryIds == null) {
            summaryIds = new HashSet<>();
        }
        if (CollectionUtils.isEmpty(companyIds)) {
            return summaryIds;
        }
        // 获取子公司
        Set<Long> companyChildrenIdByCompanyIds = baseMapper.getCompanyChildrenIdByCompanyIds(companyIds);
        // 当子公司集合不为空
        if (CollectionUtils.isNotEmpty(companyChildrenIdByCompanyIds)) {
            // 添加本次查询的
            boolean b = summaryIds.addAll(companyChildrenIdByCompanyIds);
            if (b) {
                getCompanyChildrenIdByCompanyIds(companyChildrenIdByCompanyIds, summaryIds);
            }
        }
        return summaryIds;
    }

    /**
     * 获取指定公司
     *
     * @param companyId
     * @return
     */
    public Company getCompanyById(Long companyId) {
        return selectById(companyId);
    }

    public CompanyDTO findCompanyById(Long id) {
        return companyToCompanyDTO(selectById(id));
    }

    public Company getCompanyByCompanyOid(UUID companyOid) {
        return baseMapper.getByQO(CompanyQO.builder().companyOid(companyOid).build()).get(0);
    }

    public Page<CompanyDTO> findCompanyDTOByTenantId(Long tenantId, Long infoId, String companyCode, String name, Long setOfBooksId, Boolean isEnabled, Pageable pageable) {
        Page mybatisPage = PageUtil.getPage(pageable);

        //  已分配的ID集合
        List<Long> filter = new ArrayList<>();
        if (infoId != null) {
            List<Company> companies = baseMapper.getByQO(CompanyQO.builder().setOfBooksId(setOfBooksId).build());
//            List<RelationVendorCompanyDTO> list = managementClient.getVendorInfoAssignCompanyByInfoId(infoId);
            //  判断是否分配过公司
            for (Company company : companies) {
//                Long companyId = Long.valueOf(relationVendorCompanyDTO.getCompanyId());
                filter.add(company.getId());
            }
        }
        //  分页查询公司
        List<Company> companies = baseMapper.selectCompanyByTenantIdAndEnabled(tenantId, companyCode, name, setOfBooksId, isEnabled, filter, mybatisPage);

        List<CompanyDTO> companyDTOs = new ArrayList<>();
        CompanyDTO companyDTO = null;
        for (Company company : companies) {
            companyDTO = companyToCompanyDTO(company);
            companyDTOs.add(quoteAttributeAssignment(companyDTO));
        }
        mybatisPage.setRecords(companyDTOs);
        return mybatisPage;
    }


    public List<CompanySobDTO> getCompanyByCondition(String keyword, Long tenantId, Boolean enabled, Long setOfBooksId, Page page) {
        return baseMapper.getCompaniesByTenantIdAndCondition(tenantId, setOfBooksId, enabled, keyword, page);
    }

    public List<CompanySecurity> getTenantCompanySecurity(Long tenantId) {
        return companySecurityService.listCompanySecuritysByTenant(tenantId);
    }

    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public Long findTenantIdByCompanyOid(UUID companyOid) {
        return baseMapper.findTenantIdByCompanyOid(companyOid);
    }


    /**
     * 引用属性赋值方法
     *
     * @param companyDTO：公司视图对象
     * @return
     */
    private CompanyDTO quoteAttributeAssignment(CompanyDTO companyDTO) {
        if (null == companyDTO) {
            return null;
        }
        CompanyLevel companyLevel = null;
        SetOfBooks setOfBooks = null;
        LegalEntityDTO legalEntityDTO = null;
        Company parentCompany = null;
        // 查询公司级别名称
        if (null != companyDTO.getCompanyLevelId()) {
            companyLevel = companyLevelService.getCompanyLevelById(companyDTO.getCompanyLevelId());
            if (null != companyLevel) {
                companyDTO.setCompanyLevelName(companyLevel.getDescription());
            }
        }
        // 查询公司类型名称
        if (null != companyDTO.getCompanyTypeCode()) {
            SysCodeValueCO sysCodeValue =
                    hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.COMPANY_TYPE.getId().toString(),companyDTO.getCompanyTypeCode());
            if (null != sysCodeValue) {
                companyDTO.setCompanyTypeName(sysCodeValue.getName());
            }
        }
        // 查询账套名称
        if (null != companyDTO.getSetOfBooksId()) {
            setOfBooks = setOfBooksService.getSetOfBooksById(companyDTO.getSetOfBooksId());
            if (null != setOfBooks) {
                companyDTO.setSetOfBooksName(setOfBooks.getSetOfBooksName());
            }
        }
        // 查询法人实体名称
        if (null != companyDTO.getLegalEntityId()) {
            legalEntityDTO = legalEntityService.getLegalEntity(companyDTO.getLegalEntityId());
            if (null != legalEntityDTO) {
                companyDTO.setLegalEntityName(legalEntityDTO.getEntityName());
            }
        }
        // 查询上级机构名称
        if (null != companyDTO.getParentCompanyId()) {
            parentCompany = selectById(companyDTO.getParentCompanyId());
            if (null != parentCompany) {
                companyDTO.setParentCompanyName(parentCompany.getName());
            }
        }

        //封装公司本位币
        String companyBaseCurrency = currencyRateService.getCompanySetOfBooksBaseCurrency(companyDTO.getCompanyOid());
        String currencyName = currencyI18nService.i18nTranstateByCurrencyCodes(Arrays.asList(companyBaseCurrency), null).get(companyBaseCurrency);
        companyDTO.setBaseCurrency(companyBaseCurrency);
        companyDTO.setBaseCurrencyName(currencyName);

        return companyDTO;
    }

    public Page<CompanyDTO> findUserSetOfBooksCompanys(UUID userOid, Long setOfBooksId, String name, Boolean enabled, Pageable pageable) {
        if (setOfBooksId == null) {
            setOfBooksId = setOfBooksService.getSetOfBooksIdByUserOid(userOid);
        }
        Page myBatisPage = PageUtil.getPage(pageable);
        List<Company> companies = baseMapper.getByQO(CompanyQO.builder().setOfBooksId(setOfBooksId).
                name(name).build(), myBatisPage);
        List<CompanyDTO> companyDTOs = new ArrayList<>();

        CompanyDTO companyDTO = null;
        for (Company company : companies) {
            companyDTO = companyToCompanyDTO(company);
            companyDTOs.add(quoteAttributeAssignment(companyDTO));
        }
        myBatisPage.setRecords(companyDTOs);
        return myBatisPage;
    }

    public CompanyDTO companyToCompanyDTO(Company company) {
        if (company == null) {
            return null;
        }
        CompanyDTO dto = new CompanyDTO();
        mapper.map(company, dto);
        return dto;
    }

    public CompanyInfo companyToCompanyInfo(Company company) {
        CompanyInfo dto = new CompanyInfo();
        mapper.map(company, dto);
        return dto;
    }

    public Company companyDTOToCompany(CompanyDTO dto) {
        Company company = new Company();
        mapper.map(dto, company);
        return company;
    }

    public CompanySobDTO companyToCompanySobDTO(Company company) {
        if (company == null) {
            return null;
        }
        CompanySobDTO companyDTO = new CompanySobDTO();
        mapper.map(company, companyDTO);
        return companyDTO;
    }


    public CompanyInfo companyDTOtoCompanyInfo(CompanyDTO companyDTO) {
        CompanyInfo companyInfo = new CompanyInfo();
        companyInfo.setId(companyDTO.getId());
        companyInfo.setCompanyCode(companyDTO.getCompanyCode());
        companyInfo.setName(companyDTO.getName());
        companyInfo.setTenantId(companyDTO.getTenantId());
        companyInfo.setEnabled(companyDTO.getEnabled());
        companyInfo.setCompanyOid(companyDTO.getCompanyOid().toString());
        companyInfo.setCompanyLevelId(companyDTO.getCompanyLevelId());
        companyInfo.setCompanyTypeCode(companyDTO.getCompanyTypeCode());
        companyInfo.setCompanyTypeName(companyDTO.getCompanyTypeName());
        companyInfo.setDoneRegisterLead(companyDTO.getDoneRegisterLead());
        companyInfo.setTaxId(companyDTO.getTaxId());
        companyInfo.setSetOfBooksId(companyDTO.getSetOfBooksId());
        companyInfo.setLegalEntityId(companyDTO.getLegalEntityId());
        return companyInfo;
    }

    /******************************* 以下为对外接口 **********************************/
    public List<CompanyCO> listBySetOfBooksIdConditionByEnabled(Long setOfBooksId, Boolean enabled) {
        List<CompanyCO> result = listCompanyCO(new EntityWrapper<CompanyCO>()
                .eq("set_of_books_id", setOfBooksId)
                .eq(enabled != null, "enabled", enabled));
        setSetOfBooksNameById(result, setOfBooksId);
        return result;
    }
    
    public List<CompanyCO> listCompanyCO(Wrapper<CompanyCO> wrapper){
        List<CompanyCO> companyCOS = baseMapper.listCompanyCO(wrapper);
        Map<String, String> collect = hcfOrganizationInterface.listAllSysCodeValueByCode(SystemCustomEnumerationTypeEnum.COMPANY_TYPE.getId().toString())
                .stream().collect(Collectors.toMap(SysCodeValueCO::getValue, SysCodeValueCO::getName));
        companyCOS.stream().forEach(companyCO -> {
            if(companyCO.getCompanyTypeCode() != null){
                companyCO.setCompanyTypeName(collect.get(companyCO.getCompanyTypeCode()));
            }
        });
        return companyCOS;
    }

    public List<CompanyCO> listCompanyCO(Wrapper<CompanyCO> wrapper,Page page){
        List<CompanyCO> companyCOS = baseMapper.listCompanyCO(wrapper,page);
        Map<String, String> collect = hcfOrganizationInterface.listAllSysCodeValueByCode(SystemCustomEnumerationTypeEnum.COMPANY_TYPE.getId().toString())
                .stream().collect(Collectors.toMap(SysCodeValueCO::getValue, SysCodeValueCO::getName));
        companyCOS.stream().forEach(companyCO -> {
            if(companyCO.getCompanyTypeCode() != null){
                companyCO.setCompanyTypeName(collect.get(companyCO.getCompanyTypeCode()));
            }
        });
        return companyCOS;
    }

    private void setSetOfBooksName(List<CompanyCO> coList) {
        if (CollectionUtils.isNotEmpty(coList)) {
            List<Long> setOfBooksIds = coList.stream().map(CompanyCO::getSetOfBooksId).collect(Collectors.toList());
            List<SetOfBooks> setOfBooks = setOfBooksService.selectBatchIds(setOfBooksIds);
            Map<Long, SetOfBooks> setOfBooksMap = setOfBooks.stream().collect(Collectors.toMap(SetOfBooks::getId, e -> e, (k1, k2) -> k1));
            coList.forEach(e -> {
                if (setOfBooksMap.containsKey(e.getSetOfBooksId())) {
                    e.setSetOfBooksName(setOfBooksMap.get(e.getSetOfBooksId()).getSetOfBooksName());
                }
            });
        }
    }

    private void setSetOfBooksNameById(List<CompanyCO> result, Long setOfBooksId) {
        if (CollectionUtils.isNotEmpty(result)) {
            SetOfBooks setOfBooks = setOfBooksService.selectById(setOfBooksId);
            if (setOfBooks != null) {
                result.forEach(e -> e.setSetOfBooksName(setOfBooks.getSetOfBooksName()));
            }
        }
    }

    public List<CompanyCO> listByIds(List<Long> companyIds) {
        if (CollectionUtils.isEmpty(companyIds)) {
            return new ArrayList<>();
        }
        List<CompanyCO> result = listCompanyCO(new EntityWrapper<CompanyCO>()
                .in("t.id", companyIds));
        setSetOfBooksName(result);
        return result;
    }

    public CompanyCO getById(Long id) {
        List<CompanyCO> result = listCompanyCO(new EntityWrapper<CompanyCO>()
                .eq("t.id", id));
        if (CollectionUtils.isNotEmpty(result)) {
            CompanyCO companyCO = result.get(0);
            return companyCO;
        }
        return null;
    }

    public List<CompanyCO> listByCompanyOidList(List<String> companyOids) {
        List<CompanyCO> result = listCompanyCO(new EntityWrapper<CompanyCO>()
                .eq("t.company_oid", companyOids));
        setSetOfBooksName(result);
        return result;
    }

    public CompanyCO getByCompanyOid(String oid) {
        List<CompanyCO> result = listCompanyCO(new EntityWrapper<CompanyCO>()
                .eq("t.company_oid", oid));
        if (CollectionUtils.isNotEmpty(result)) {
            CompanyCO companyCO = result.get(0);
            return companyCO;
        }
        return null;
    }

    public Page<CompanyCO> pageBySetOfBooksIdConditionByIds(Long setOfBooksId,
                                                            String companyCode,
                                                            String companyCodeFrom,
                                                            String companyCodeTo,
                                                            String companyName,
                                                            List<Long> existsCompanyIds,
                                                            Page<CompanyCO> mybatisPage) {
        Wrapper<CompanyCO> wrapper = new EntityWrapper<CompanyCO>()
                .eq("t.set_of_books_id", setOfBooksId)
                .like(StringUtils.hasText(companyCode), "t.company_code", companyCode)
                .ge(StringUtils.hasText(companyCodeFrom), "t.company_code", companyCodeFrom)
                .le(StringUtils.hasText(companyCodeTo), "t.company_code", companyCodeTo)
                .like(StringUtils.hasText(companyName), "t.name", companyName)
                .in(CollectionUtils.isNotEmpty(existsCompanyIds), "t.id", existsCompanyIds);

        List<CompanyCO> result = listCompanyCO(wrapper, mybatisPage);
        setSetOfBooksNameById(result, setOfBooksId);
        mybatisPage.setRecords(result);
        return mybatisPage;
    }

    public Page<CompanyCO> pageBySetOfBooksIdConditionByIgnoreIds(Long setOfBooksId,
                                                                  String companyCode,
                                                                  String companyCodeFrom,
                                                                  String companyCodeTo,
                                                                  String companyName,
                                                                  Boolean enabled,
                                                                  List<Long> ignoreCompanyIds,
                                                                  Page<CompanyCO> mybatisPage) {
        Wrapper<CompanyCO> wrapper = new EntityWrapper<CompanyCO>()
                .eq("t.set_of_books_id", setOfBooksId)
                .like(StringUtils.hasText(companyCode), "t.company_code", companyCode)
                .ge(StringUtils.hasText(companyCodeFrom), "t.company_code", companyCodeFrom)
                .le(StringUtils.hasText(companyCodeTo), "t.company_code", companyCodeTo)
                .like(StringUtils.hasText(companyName), "t.name", companyName)
                .eq(enabled != null,"enabled",enabled)
                .notIn(CollectionUtils.isNotEmpty(ignoreCompanyIds), "t.id", ignoreCompanyIds);

        List<CompanyCO> result = listCompanyCO(wrapper, mybatisPage);
        setSetOfBooksNameById(result, setOfBooksId);
        mybatisPage.setRecords(result);
        return mybatisPage;
    }


    public Page<CompanyCO> pageByTenantIdConditionByIgnoreIds(Long tenantId,
                                                              Long setOfBooksId,
                                                              String companyCode,
                                                              String companyName,
                                                              String companyCodeFrom,
                                                              String companyCodeTo,
                                                              List<Long> ignoreCompanyIds,
                                                              Page<CompanyCO> mybatisPage) {
        Wrapper<CompanyCO> wrapper = new EntityWrapper<CompanyCO>()
                .eq(tenantId != null, "t.tenant_id", tenantId)
                .eq(setOfBooksId != null, "t.set_of_books_id", setOfBooksId)
                .like(StringUtils.hasText(companyCode), "t.company_code", companyCode)
                .like(StringUtils.hasText(companyName), "t.name", companyName)
                .ge(StringUtils.hasText(companyCodeFrom), "t.company_code", companyCodeFrom)
                .le(StringUtils.hasText(companyCodeTo), "t.company_code", companyCodeTo)
                .notIn(CollectionUtils.isNotEmpty(ignoreCompanyIds), "t.id", ignoreCompanyIds);
        List<CompanyCO> result = listCompanyCO(wrapper, mybatisPage);
        setSetOfBooksNameById(result, setOfBooksId);
        mybatisPage.setRecords(result);
        return mybatisPage;
    }


    public List<CompanyCO> listBySetOfBooksIdAndCompanyCodes(Long setOfBooksId, List<String> companyCodes) {
        Wrapper<CompanyCO> wrapper = new EntityWrapper<CompanyCO>()
                .eq("t.set_of_books_id", setOfBooksId)
                .in(CollectionUtils.isNotEmpty(companyCodes), "t.company_code", companyCodes);
        List<CompanyCO> result = listCompanyCO(wrapper);
        setSetOfBooksNameById(result, setOfBooksId);
        return result;
    }


    public List<CompanyCO> listByCompanyGroupIdConditionByEnabled(Long companyGroupId, Boolean enabled) {
        List<CompanyGroupAssign> companyGroupAssigns = companyGroupAssignService.selectList(new EntityWrapper<CompanyGroupAssign>()
                .eq("company_group_id", companyGroupId)
                .eq(enabled != null, "enabled", enabled));
        if (CollectionUtils.isNotEmpty(companyGroupAssigns)) {
            List<Long> companyIds = companyGroupAssigns.stream().map(CompanyGroupAssign::getCompanyId).collect(Collectors.toList());
            Wrapper<CompanyCO> wrapper = new EntityWrapper<CompanyCO>()
                    .in("t.id", companyIds)
                    .eq(enabled != null, "t.enabled", enabled);
            return listCompanyCO(wrapper);
        }
        return new ArrayList<>();
    }

    public CompanyCO getByCompanyCode(String companyCode) {
        Long currentTenantId = OrgInformationUtil.getCurrentTenantId();
        Wrapper<CompanyCO> wrapper = new EntityWrapper<CompanyCO>()
                .eq("t.company_code", companyCode)
                .eq("t.tenant_id", currentTenantId);
        List<CompanyCO> companyCOS = listCompanyCO(wrapper);
        if (CollectionUtils.isNotEmpty(companyCOS)) {
            return companyCOS.get(0);
        }
        return null;
    }

    public Page<CompanyCO> pageChildrenCompaniesByCondition(Long companyId,
                                                            Boolean ignoreOwn,
                                                            String companyCode,
                                                            String companyCodeFrom,
                                                            String companyCodeTo,
                                                            String companyName,
                                                            String keyWord,
                                                            Page<CompanyCO> mybatisPage) {
        List<CompanyDTO> dtos = getCompanyByCond(companyId, ignoreOwn, companyCode, companyCodeFrom, companyCodeTo, companyName, keyWord, mybatisPage);
        List<CompanyCO> companyCOS = dtos.stream().map(e -> {
            CompanyCO companyCO = CompanyCO.builder()
                    .companyCode(e.getCompanyCode())
                    .companyLevelId(e.getCompanyLevelId())
                    .address(e.getAddress())
                    .id(e.getId())
                    .companyOid(e.getCompanyOid())
                    .companyTypeName(e.getCompanyTypeName())
                    .legalEntityId(e.getLegalEntityId())
                    .name(e.getName())
                    .parentCompanyId(e.getParentCompanyId())
                    .setOfBooksId(e.getSetOfBooksId())
                    .tenantId(e.getTenantId())
                    .setOfBooksName(e.getSetOfBooksName())
                    .build();
            return companyCO;
        }).collect(Collectors.toList());
        mybatisPage.setRecords(companyCOS);
        return mybatisPage;
    }

    public List<CompanyCO> listChildrenCompaniesByCondition(Long companyId,
                                                            Boolean ignoreOwn,
                                                            String companyCode,
                                                            String companyCodeFrom,
                                                            String companyCodeTo,
                                                            String companyName,
                                                            String keyWord) {
        List<CompanyDTO> dtos = getCompanyByCond(companyId, ignoreOwn, companyCode, companyCodeFrom, companyCodeTo, companyName, keyWord, null);
        List<CompanyCO> companyCOS = dtos.stream().map(e -> {
            CompanyCO companyCO = CompanyCO.builder()
                    .companyCode(e.getCompanyCode())
                    .companyLevelId(e.getCompanyLevelId())
                    .address(e.getAddress())
                    .id(e.getId())
                    .companyOid(e.getCompanyOid())
                    .companyTypeName(e.getCompanyTypeName())
                    .legalEntityId(e.getLegalEntityId())
                    .name(e.getName())
                    .parentCompanyId(e.getParentCompanyId())
                    .setOfBooksId(e.getSetOfBooksId())
                    .tenantId(e.getTenantId())
                    .setOfBooksName(e.getSetOfBooksName())
                    .build();
            return companyCO;
        }).collect(Collectors.toList());
        return companyCOS;
    }

    public Set<Long> listChildrenCompanyIdsByCompanyId(Long companyId) {
        return getCompanyChildrenIdByCompanyId(companyId);
    }

    public Page<CompanyCO> pageConditionKeyWordAndEnabled(String keyWord, Boolean enabled, Page<CompanyCO> mybatisPage) {
        Long currentTenantId = OrgInformationUtil.getCurrentTenantId();
        Wrapper<CompanyCO> wrapper = new EntityWrapper<CompanyCO>().eq("t.tenant_id", currentTenantId)
                .eq(enabled != null, "t.enabled", enabled)
                .andNew()
                .like(StringUtils.hasText(keyWord), "t.name", keyWord)
                .or(StringUtils.hasText(keyWord), "t.company_code like concat(concat('%',{0}),'%')", keyWord);

        List<CompanyCO> companyCOS = listCompanyCO(wrapper, mybatisPage);
        setSetOfBooksName(companyCOS);

        mybatisPage.setRecords(companyCOS);
        return mybatisPage;
    }

    public Page<CompanyCO> pageByCompanyIdsConditionByKeyWord(List<Long> companyIds, String keyWord, Page<CompanyCO> mybatisPage) {
        Wrapper<CompanyCO> wrapper = new EntityWrapper<CompanyCO>().in("t.id", companyIds)
                .andNew()
                .like(StringUtils.hasText(keyWord), "t.name", keyWord)
                .or(StringUtils.hasText(keyWord), "t.company_code like concat(concat('%',{0}),'%')", keyWord);
        List<CompanyCO> companyCOS = listCompanyCO(wrapper, mybatisPage);
        setSetOfBooksName(companyCOS);
        mybatisPage.setRecords(companyCOS);
        return mybatisPage;
    }

    public List<BasicCO> pageCompanyByCond(Long tenantId, Long setOfBooksId, Long companyId, String code, String name, Page page) {

        return baseMapper.pageCompanyByCond(tenantId, setOfBooksId, companyId, code, name, page);
    }

    public Page<BasicCO> pageCompaniesByInfoResultBasic(Long selectId, String code, String name, String securityType, Long filterId, Page page) {
        List<BasicCO> BasicCOS = null;
        if (selectId != null) {
            Company company = this.getCompanyById(selectId);
            if (company == null) {
                return page;
            }
            BasicCO basicCO = new BasicCO();
            basicCO.setId(company.getId());
            basicCO.setCode(company.getCompanyCode());
            basicCO.setName(company.getName());
            page.setRecords(Arrays.asList(basicCO));
        } else {
            if (securityType.equals("TENANT")) {
                BasicCOS = baseMapper.listCompanyByCodeAndSecurityType(null, code, name, filterId, null, page);
            }
            if (securityType.equals("SET_OF_BOOKS")) {
                BasicCOS = baseMapper.listCompanyByCodeAndSecurityType(filterId, code, name, null, null, page);
            }
            if (securityType.equals("COMPANY")) {
                BasicCOS = baseMapper.listCompanyByCodeAndSecurityType(null, code, name, null, filterId, page);
            }
            if (securityType.equals("SYSTEM")) {
                BasicCOS = baseMapper.listCompanyByCodeAndSecurityType(null, code, name, null, null, page);
            }
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(BasicCOS)) {
                page.setRecords(BasicCOS);
            }
        }
        return page;
    }

    public List<BasicCO> pageByLevelAndCompanyIdOrCond(Long selectId,
                                                       String code,
                                                       String name,
                                                       String securityType,
                                                       Long filterId,
                                                       Page page) {
        List<BasicCO> result = new ArrayList<>();
        if (selectId != null) {
            Company company = baseMapper.selectById(selectId);
            BasicCO basicCO = BasicCO.builder()
                    .id(company.getId())
                    .code(company.getCompanyCode())
                    .name(company.getName())
                    .build();
            result.add(basicCO);
        } else {
            Long tenantId = null;
            Long setOfBooksId = null;
            Long companyId = null;
            if (securityType.equals("TENANT")) {
                tenantId = filterId;
            } else if (securityType.equals("SOB")) {
                setOfBooksId = filterId;
            } else if (securityType.equals("COMPANY")) {
                companyId = filterId;
            } else {
                return null;
            }
            result = this.pageCompanyByCond(tenantId, setOfBooksId, companyId, code, name, page);

        }

        return result;
    }

    /**
     * 根据账套Id获取所有启用公司
     *
     * @param setOfBooksId 账套id
     * @param keyword      公司代码或者名称
     * @param codeFrom     公司代码从
     * @param codeTo       公司代码至
     * @param mybaitsPage  分页
     * @return
     */
    public List<Company> pageCompanyBySetOfBooksIdAndCond(Long setOfBooksId, String keyword, String codeFrom, String codeTo, Page mybaitsPage) {
        return baseMapper.pageCompanyBySetOfBooksIdAndCond(setOfBooksId, keyword, codeFrom, codeTo, mybaitsPage);
    }

    public List<CompanyCO> listCompanyBySetOfBooksIdAndCodeAndName(Long setOfBooksId, String companyCode, String companyName) {
        List<CompanyCO> result = new ArrayList<>();
        List<Company> companies = this.selectList(
                new EntityWrapper<Company>()
                        .eq(setOfBooksId != null, "set_of_books_id", setOfBooksId)
                        .like(companyCode != null, "company_code", companyCode)
                        .like(companyName != null, "name", companyName)
        );
        if (companies.size() > 0){
            companies.stream().forEach(company -> {
                CompanyCO companyCO = new CompanyCO();
                mapper.map(company,companyCO);
                result.add(companyCO);
            });
        }
        return result;
    }
    public List<CompanyCO> listCompanyByTenantId(Long tenantId, Boolean enabled) {
        return companyMapper.listCompanyByTenantId(tenantId, enabled);
    }

    public CompanyCO getCompanyByUserOid(UUID userOid){
        return getById(contactService.getContactByUserOid(userOid).getCompanyId());
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
    public List<CompanyCO> listCompanyByCond(String companyCode,
                                                   String companyCodeFrom,
                                                   String companyCodeTo,
                                                   String companyName,
                                                   String keyWord,
                                                   Boolean enabled,
                                                   List<Long> ids) {
        Long tenantId = LoginInformationUtil.getCurrentTenantId();
        Wrapper<CompanyCO> wrapper = new EntityWrapper<CompanyCO>()
                .eq("tenant_id",tenantId)
                .eq(!StringUtils.isEmpty(enabled), "enabled", enabled)
                .in(ids != null && ids.size() > 0, "t.id", ids)
                .ge(StringUtils.hasText(companyCodeFrom), "company_code", companyCodeFrom)
                .le(StringUtils.hasText(companyCodeTo), "company_code", companyCodeTo)
                .like(StringUtils.hasText(companyCode), "company_code", companyCode)
                .like(StringUtils.hasText(companyName), "name", companyName);
        if(StringUtils.hasText(keyWord)) {
            wrapper .andNew()
                    .like(StringUtils.hasText(keyWord), "name", keyWord)
                    .or()
                    .like(StringUtils.hasText(keyWord), "company_code", keyWord);
        }
        List<CompanyCO> result =  listCompanyCO(wrapper);
        setSetOfBooksName(result);
        return result;
    }
}
