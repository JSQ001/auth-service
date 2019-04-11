package com.hand.hcf.app.base.tenant.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.attachment.AttachmentService;
import com.hand.hcf.app.base.attachment.domain.Attachment;
import com.hand.hcf.app.base.attachment.enums.AttachmentType;
import com.hand.hcf.app.base.code.service.SysCodeService;
import com.hand.hcf.app.base.system.constant.CacheConstants;
import com.hand.hcf.app.base.tenant.domain.Tenant;
import com.hand.hcf.app.base.tenant.dto.TenantDTO;
import com.hand.hcf.app.base.tenant.dto.TenantRegisterDTO;
import com.hand.hcf.app.base.tenant.persistence.TenantMapper;
import com.hand.hcf.app.base.user.domain.User;
import com.hand.hcf.app.base.user.enums.CreatedTypeEnum;
import com.hand.hcf.app.base.user.service.UserService;
import com.hand.hcf.app.base.userRole.domain.Role;
import com.hand.hcf.app.base.userRole.domain.UserRole;
import com.hand.hcf.app.base.userRole.service.RoleFunctionService;
import com.hand.hcf.app.base.userRole.service.RoleService;
import com.hand.hcf.app.base.userRole.service.UserRoleService;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.core.domain.enumeration.LanguageEnum;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.util.PageUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = {CacheConstants.TENANT})
public class TenantService extends BaseService<TenantMapper, Tenant> {



    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private MapperFacade mapper;

    @Autowired
    private UserService userService;


    @Autowired
    private SysCodeService sysCodeService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleFunctionService roleFunctionService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * save tenant
     *
     * @param tenant
     */
    public void saveTenant(Tenant tenant) {
        if (StringUtils.isEmpty(tenant.getTenantName())) {
            throw new BizException(RespCode.TENANT_NAME_NULL);
        }
        insert(tenant);
    }

    /**
     * save tenant
     *
     * @param tenant
     */
    public void updateTenant(Tenant tenant) {
        if (StringUtils.isEmpty(tenant.getTenantName())) {
            throw new BizException(RespCode.TENANT_NAME_NULL);
        }
        updateById(tenant);
    }

    /**
     * listDTOByQO one by tenantId
     *
     * @param tenantId
     * @return
     */
    public Tenant findTenantById(Long tenantId) {
        Tenant tenant = selectById(tenantId);
        if (tenant.getLogoId() != null) {
            AttachmentCO AttachmentCO = attachmentService.getAttachmentById(tenant.getLogoId());
            if (AttachmentCO != null) {
                tenant.setLogoURL(AttachmentCO.getThumbnailUrl());
            }
        }
        return tenant;
    }


    /**
     * 查询所有租户信息
     *
     * @return
     */
    public List<Tenant> findAll() {
        return selectByMap(new HashMap<>());
    }

    public Tenant updateTenantLogo(TenantDTO tenantDTO) {
        Tenant tenant = this.findTenantById(tenantDTO.getId());
        if (tenant == null) {
            throw new BizException(RespCode.TENANT_NOT_EXIST);
        }
        tenant.setShowCustomLogo(tenantDTO.getShowCustomLogo());
        tenant.setEnableNewControl(tenantDTO.getEnableNewControl());
        updateById(tenant);
        return tenant;
    }


    /**
     * 批量获取租户信息
     *
     * @param tenantIds
     * @return
     */
    public List<Tenant> getTenantsByIds(List<Long> tenantIds) {
        return selectBatchIds(tenantIds);
    }

    /**
     * 获取所有有效且付费的租户
     *
     * @return
     */
    public List<Tenant> getAllValidTenants() {
        return selectList(new EntityWrapper<Tenant>()
                .eq("licensed", true)
                .eq("enalbed", true));
    }


    /**
     * 获取指定租户信息
     *
     * @param tenantId
     * @return
     */
    public Tenant getTenantById(Long tenantId) {
        return selectById(tenantId);
    }

    public TenantDTO getTenantDTOById(Long id) {
        return tenantToTenantDTO(selectById(id));
    }


    /**
     * 根据付费标记和租户名称[模糊]分页查询
     *
     * @param licensed 付费标记
     * @param keyword  租户名称
     * @param pageable 分页
     * @return 租户信息
     */
    public Page<TenantDTO> searchAllValidTenants(Boolean licensed, String keyword, Pageable pageable) {
        Page<Tenant> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize(), "last_updated_date");
        page.setAsc(false);
        Page<Tenant> tenants = selectPage(page, new EntityWrapper<Tenant>()
                .eq(licensed != null, "licensed", licensed)
                .like(keyword != null, "tenant_name", keyword)
                .eq("enabled", true));
        page.setRecords(tenants.getRecords());
        Page<TenantDTO> result = PageUtil.getPage(pageable);
        result.setRecords(page.getRecords().stream().map(domain -> tenantToTenantDTO(domain)).collect(Collectors.toList()));
        result.setTotal(page.getTotal());
        return result;
    }


    public Tenant selectByOne(Long id) {
        return selectOne(new EntityWrapper<Tenant>()
                .eq("id", id)
                .eq("enabled", true));
    }

    /**
     * 修改租户付费标记
     *
     * @param tenantDTO 租户信息
     * @return 最新租户
     */
    public TenantDTO modifyTenant(TenantDTO tenantDTO, Boolean updateCascade) {
        Tenant tenant = selectByOne(tenantDTO.getId());
        if (tenant == null) {
            // 当前租户不存在(enabled =1 , deleted = 0)
            throw new BizException(RespCode.TENANT_NOT_EXIST);
        }
        tenant.setLicensed(tenantDTO.getLicensed());
        if (updateCascade != null && updateCascade) {
            tenant.setTenantName(tenantDTO.getTenantName());
            tenant.setTenantCode(tenantDTO.getTenantCode());
        }
        tenant.setLastUpdatedBy(tenantDTO.getLastUpdatedBy());
        tenant.setLastUpdatedDate(ZonedDateTime.now());
        updateById(tenant);
        return tenantToTenantDTO(tenant);
    }


    public Tenant uploadTenantLogo(UUID companyOid, Long tenantId, MultipartFile file) {
        Tenant tenant = findTenantById(tenantId);
        if (tenant == null) {
            throw new BizException(RespCode.TENANT_NOT_EXIST);
        }
        Attachment attachment;
        if (tenant.getLogoId() != null) {
            AttachmentCO AttachmentCO = attachmentService.getAttachmentById(tenant.getLogoId());
            attachmentService.removeFile(true, AttachmentCO.getFileUrl());
        }
        attachment = attachmentService.uploadStatic(file, AttachmentType.COMPANY_LOGO);
        tenant.setLogoId(attachment.getId());
        updateById(tenant);
        tenant.setLogoURL(attachment.getThumbnailPath());
        return tenant;
    }



    public Tenant tenantDTOToTenant(TenantDTO tenantDTO) {
        if (tenantDTO == null) {
            return null;
        }
        Tenant tenant = new Tenant();
        mapper.map(tenantDTO, tenant);
        return tenant;
    }

    public TenantDTO tenantToTenantDTO(Tenant tenant) {
        if (tenant == null) {
            return null;
        }
        TenantDTO tenantDTO = new TenantDTO();
        mapper.map(tenant, tenantDTO);
        return tenantDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean registerTenant(TenantRegisterDTO register){
        Tenant tenant = mapper.map(register, Tenant.class);
        tenant.setEnabled(Boolean.TRUE);
        tenant.setDeleted(Boolean.FALSE);
        tenant.setStatus("1001");
        tenant.setId(null);
        this.insert(tenant);
        register.setId(tenant.getId());

        // 初始化用户
        User user = initUser(register);
        // 初始化角色
        Role role = roleService.initRoleByTenant(tenant);
        // 初始化角色菜单
        roleFunctionService.initRoleFunctionByTenant(role);
        // 初始化用户角色
        initUserRole(role, user);
        // 初始化值列表
        sysCodeService.init();
        return true;
    }

    private void initUserRole(Role role, User user) {
        UserRole userRole = new UserRole();
        userRole.setRoleId(role.getId());
        userRole.setUserId(user.getId());
        userRole.setValidDateFrom(ZonedDateTime.now());
        userRole.setEnabled(Boolean.TRUE);
        userRoleService.insert(userRole);
    }



    private User initUser( TenantRegisterDTO register) {
        if (register.getPassword() == null || !register.getPassword().equals(register.getPasswordConfirm())){
            throw new BizException(RespCode.TENANT_ADMIN_PASSWORD_IS_ERROR);
        }
        //login唯一性校验
        Integer exists = userService.selectCount(new EntityWrapper<User>().eq("login",register.getLogin()));
        if (exists != null && exists > 0){
            throw new BizException(RespCode.USER_LOGIN_EXISTS);
        }
        String passwordHash = passwordEncoder.encode(register.getPassword());
        User user = new User();
        user.setCreatedType(CreatedTypeEnum.INIT_TENANT);
        user.setLogin(register.getLogin());
        user.setTenantId(register.getId());
        user.setUserOid(UUID.randomUUID());
        user.setActivated(Boolean.TRUE);
        user.setUserName(register.getFullName());
        user.setEmail(register.getEmail());
        user.setMobile(register.getMobile());
        user.setAvatarOid(UUID.randomUUID());
        user.setPasswordHash(passwordHash);
        user.setLanguage(LanguageEnum.ZH_CN.getKey());
        user.setPasswordAttempt(0);
        user.setLockStatus(2001);
        userService.insert(user);
        return user;
    }

    public List<TenantDTO> listTenantDTOsByCondition(String tenantName, String tenantCode, String userName, String mobile, String email, String login, String remark,Page page){
        List<TenantDTO> result = baseMapper.listTenantDTOsByCondition(tenantName,tenantCode,userName,mobile,email,login,remark,page);
        return result;
    }
}
