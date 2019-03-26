package com.hand.hcf.app.mdata.contact.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.base.org.SysCodeValueCO;
import com.hand.hcf.app.base.user.UserCO;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.dto.CompanyDTO;
import com.hand.hcf.app.mdata.company.service.CompanySecurityService;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.contact.domain.Contact;
import com.hand.hcf.app.mdata.contact.domain.Phone;
import com.hand.hcf.app.mdata.contact.domain.UserTempDomain;
import com.hand.hcf.app.mdata.contact.dto.ContactDTO;
import com.hand.hcf.app.mdata.contact.dto.ContactQO;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.contact.dto.UserSimpleInfoDTO;
import com.hand.hcf.app.mdata.contact.enums.*;
import com.hand.hcf.app.mdata.contact.persistence.ContactMapper;
import com.hand.hcf.app.mdata.department.domain.Department;
import com.hand.hcf.app.mdata.department.domain.DepartmentUser;
import com.hand.hcf.app.mdata.department.service.DepartmentService;
import com.hand.hcf.app.mdata.department.service.DepartmentUserService;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.legalEntity.dto.LegalEntityDTO;
import com.hand.hcf.app.mdata.legalEntity.service.LegalEntityService;
import com.hand.hcf.app.mdata.setOfBooks.service.SetOfBooksService;
import com.hand.hcf.app.mdata.system.constant.CacheConstants;
import com.hand.hcf.app.mdata.system.constant.Constants;
import com.hand.hcf.app.mdata.system.constant.SyncLockPrefix;
import com.hand.hcf.app.mdata.system.domain.MobileValidate;
import com.hand.hcf.app.mdata.system.enums.AttachmentType;
import com.hand.hcf.app.mdata.system.enums.SystemCustomEnumerationTypeEnum;
import com.hand.hcf.app.mdata.system.persistence.MobileValidateMapper;
import com.hand.hcf.app.mdata.utils.FileUtil;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.mdata.utils.StringUtil;
import com.hand.hcf.app.mdata.utils.UserInfoDisplayUtil;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.core.exception.core.ValidationError;
import com.hand.hcf.core.exception.core.ValidationException;
import com.hand.hcf.core.handler.ExcelImportHandler;
import com.hand.hcf.core.redisLock.annotations.SyncLock;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.service.ExcelImportService;
import com.hand.hcf.core.service.MessageService;
import com.hand.hcf.core.util.PageUtil;
import com.hand.hcf.core.util.RedisHelper;
import com.hand.hcf.core.util.TypeConversionUtils;
import com.hand.hcf.core.web.dto.ImportResultDTO;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Contact.
 */
@Slf4j
@Service
@Transactional
public class ContactService extends BaseService<ContactMapper, Contact> {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private MapperFacade mapper;

    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;

    @Autowired
    CompanySecurityService companySecurityService;
    @Autowired
    DepartmentService departmentService;

    @Autowired
    LegalEntityService legalEntityService;
    @Autowired
    SetOfBooksService setOfBooksService;

    @Autowired
    PhoneService phoneService;

    @Autowired
    DepartmentUserService departmentUserService;

    @Autowired
    RedisHelper redisHelper;

    @Autowired
    private UserImportService userImportService;
    @Autowired
    private ContactCardImportService contactCardImportService;
    @Autowired
    private ContactBankAccountImportService contactBankAccountImportService;
    @Autowired
    private ExcelImportService excelImportService;
    @Autowired
    private MobileValidateMapper mobileValidateMapper;
    @Autowired
    private MessageService messageService;


    public Contact contactDTOToContact(ContactDTO contactDTO) {
        return mapper.map(contactDTO, Contact.class);
    }

    public ContactDTO contactToContactDTO(Contact contact) {
        return mapper.map(contact, ContactDTO.class);
    }

    /**
     * Save a contact.
     *
     * @return the persisted entity
     */
    public ContactDTO save(ContactDTO contactDTO) {

        Contact contact = contactDTOToContact(contactDTO);
        //邮箱不为空时校验邮箱格式
        if (!StringUtils.isEmpty(contact.getEmail()) && !EmailValidator.getInstance().isValid(contact.getEmail())) {
            throw new ValidationException(new ValidationError("email", "email is error!"));
        }
        super.insert(contact);
        return contactToContactDTO(contact);
    }

    public Contact getOneByEmployeeId(String employeeId){
        Contact contact = this.selectOne(new EntityWrapper<Contact>().eq("employee_id",employeeId));
        return contact;
    }

    public Contact save(Contact contact){
        super.insert(contact);
        return contact;
    }

    /**
     * get all the contacts.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Contact> findAll(Pageable pageable) {
        return super.selectPage(PageUtil.getPage(pageable));
    }

    /**
     * get one contact by id.
     *
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Contact findOne(Long id) {
        return super.selectById(id);
    }

    /**
     * delete the  contact by id.
     */
    public void delete(Long id) {
        super.deleteById(id);
    }

    public AttachmentCO updateHeadPortraitByEmployeeId(MultipartFile file) {
        UserDTO user = getUserDTOByUserOid(OrgInformationUtil.getCurrentUserOid());
        Company company = companyService.findOne(user.getCompanyId());
        if (!FileUtil.isValidImage(file)) {
            throw new ValidationException(new ValidationError("error", "invalid file format"));
        }
        AttachmentCO AttachmentCO = hcfOrganizationInterface.uploadStatic(file, AttachmentType.HEAD_PORTRAIT);
        Contact contact = getContactByUserId(user.getId());
        if (contact != null) {
            contact.setHeadPortrait(AttachmentCO.getAttachmentOid());
            updateById(contact);
        } else {
            throw new ValidationException(new ValidationError("error", "upload head portrait error"));
        }
        return AttachmentCO;
    }

    public AttachmentCO getHeadPortraitByUserOid(UUID userOid) {
        Contact contact = getContactByUserOid(userOid);
        if (contact == null) {
            throw new ObjectNotFoundException(Contact.class, userOid);
        }
        AttachmentCO AttachmentCO = hcfOrganizationInterface.getAttachmentByOid(contact.getHeadPortrait());
        if (AttachmentCO == null) {
            return null;
        }
        return AttachmentCO;
    }
    /**
     * 根据用户oid修改头像附件
     *
     * @param userOid：用户Oid
     * @param attachmentOid：附件Oid
     */
    public void updateHeadProtraitByUserOid(UUID userOid, UUID attachmentOid) {
        Contact contact = getContactByUserOid(userOid);
        if (null != contact) {
            contact.setHeadPortrait(attachmentOid);
            updateById(contact);
        } else {
            throw new ValidationException(new ValidationError("error", "update head portrait error"));
        }
    }

    public String selectEmployeeIdByUserOid(UUID userOid){
        return getContactByUserOid(userOid).getEmployeeId();
    }

    /**
     * 根据用户ID查询员工
     * @param userId
     * @return
     */
    public Contact getContactByUserId(Long userId){
        if(userId == null){
            return null;
        }
        Contact contact = new Contact();
        contact.setUserId(userId);
        return baseMapper.selectOne(contact);
    }

    /**
     * 根据用户OID查询员工
     * @param userOid
     * @return
     */
    public Contact getContactByUserOid(UUID userOid){
        if(userOid == null){
            return null;
        }
        Contact contact = new Contact();
        contact.setUserOid(userOid);
        return baseMapper.selectOne(contact);
    }

    /**
     * 根据用户ID查询员工
     * @param userId
     * @return
     */
    public UserDTO getUserDTOByUserId(Long userId){
        if(userId == null){
            return null;
        }
        Contact contact = new Contact();
        contact.setUserId(userId);
        return contactToUserDTO(baseMapper.selectOne(contact));
    }

    /**
     * 根据用户OID查询员工
     * @param userOid
     * @return
     */
    public UserDTO getUserDTOByUserOid(UUID userOid){
        if(userOid == null){
            return null;
        }
        Contact contact = new Contact();
        contact.setUserOid(userOid);
        return contactToUserDTO(baseMapper.selectOne(contact));
    }

    /**
     * 根据用户OID查询员工
     * @param userCode
     * @return
     */
    public UserDTO getUserDTOByUserCode(String userCode){
        if(userCode == null){
            return null;
        }
        Contact contact = new Contact();
        contact.setEmployeeId(userCode);
        return contactToUserDTO(baseMapper.selectOne(contact));
    }

    /**
     * 根据用户OID查询员工
     * @param userOids
     * @return
     */
    public List<UserDTO> listUserDTOByUserOid(List<UUID> userOids){
        if(com.baomidou.mybatisplus.toolkit.CollectionUtils.isEmpty(userOids)){
            return Arrays.asList();
        }
        List<Contact> contacts = baseMapper.selectList(new EntityWrapper<Contact>().in("user_oid", userOids));
        return contacts.stream().map(e -> contactToUserDTO(e)).collect(Collectors.toList());
    }

    /**
     * 根据用户ID查询员工
     * @param userIds
     * @return
     */
    public List<UserDTO> listUserDTOByUserId(List<Long> userIds){
        if(com.baomidou.mybatisplus.toolkit.CollectionUtils.isEmpty(userIds)){
            return Arrays.asList();
        }
        List<Contact> contacts = baseMapper.selectList(new EntityWrapper<Contact>().in("user_id", userIds));
        return contacts.stream().map(e -> contactToUserDTO(e)).collect(Collectors.toList());
    }

    public UserDTO getUserDTOByUserOid(String userOid){
        if(userOid == null){
            return null;
        }
        return getUserDTOByUserOid(UUID.fromString(userOid));
    }

    public List<Contact> listByQO(ContactQO contactQO){
        return baseMapper.listByQO(contactQO);
    }

    public List<Contact> listByQO(ContactQO contactQO,Page page){
        return baseMapper.listByQO(page,contactQO);
    }

    public List<ContactDTO> listDtoByQO(ContactQO contactQO){
        return baseMapper.listDtoByQO(contactQO);
    }

    public List<ContactDTO> listDtoByQO(ContactQO contactQO,Page page){
        return baseMapper.listDtoByQO(page,contactQO);
    }

    public List<ContactDTO> listUserByNameAndCode(String name,
                                           String code,
                                           Long tenantId,
                                           Page page){
        return baseMapper.listUserByNameAndCode(name,code,tenantId,page);
    }

    public Optional<Contact> getByCompanyOidAndEmployeeId(UUID companyOid, String employeeId) {
        Long tenantId = companyService.findTenantIdByCompanyOid(companyOid);
        Optional<Contact> result = this.getByEmployeeIdAndTenantId(tenantId, employeeId);
        return result;
    }

    public Optional<Contact> getByEmployeeIdAndTenantId(Long tenantId, String employeeId) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(employeeId)) {
            return Optional.empty();
        }
        List<Contact> contacts = listByQO(ContactQO.builder().tenantId(tenantId).employeeIds(new ArrayList<String>() {{
            add(employeeId.trim());
        }}).build());
        if (CollectionUtils.isNotEmpty(contacts)) {
            Contact contact = contacts.get(0);
            return Optional.ofNullable(contact);
        }
        return Optional.empty();
    }

    /**
     * 获取用户列表 分页 (员工管理)
     *
     * @param tenantId       必填，取租户下的所有用户
     * @param keyword        如果填了，根据条件取帐套下的用户
     * @param departmentOids 如果填了，则取部门下的用户
     * @param status         如果填了，则根据状态取部门下的用户
     * @param companyOids    如果填了，则根据公司id进行查询用户
     * @return 按employee_id, created_date排序
     */
    public List<ContactDTO> listByCondition(String keyword,
                                         Long tenantId,
                                         List<UUID> departmentOids,
                                         String status,
                                         List<UUID> companyOids,
                                         UUID currentUserOid,
                                         Page page) {
        Integer statusValue = null;
        if (status == null) {
            statusValue = null;
        } else if (!org.apache.commons.lang3.StringUtils.isEmpty(status) && "all".equals(status)) {
            statusValue = null;
        } else {
            statusValue = Integer.valueOf(status);
        }
        ContactQO.ContactQOBuilder contactQOBuilder = ContactQO.builder()
                .keyword(keyword)
                .tenantId(tenantId)
                .hasDepartment(true)
                .departmentOids(departmentOids)
                .status(statusValue)
                .companyOids(companyOids)
                .currentUserOid(currentUserOid)
                .orderByEmployeeId(true);
        List<ContactDTO> list = baseMapper.listDtoByQO(page,
                contactQOBuilder.build());
        return list;
    }

    /**
     * 获取用户列表 分页 (员工管理)
     *
     * @param tenantId       必填，取租户下的所有用户
     * @param keyword        如果填了，根据条件取帐套下的用户
     * @param departmentOids 如果填了，则取部门下的用户
     * @param status         如果填了，则根据状态取部门下的用户
     * @param companyOids    如果填了，则根据公司id进行查询用户
     * @return 按employee_id, created_date排序
     */
    public List<UserDTO> listUserDTOByCondition(String keyword,
                                            Long tenantId,
                                            List<UUID> departmentOids,
                                            String status,
                                            List<UUID> companyOids,
                                            UUID currentUserOid,
                                            Page page) {
        Integer statusValue = null;
        if (status == null) {
            statusValue = null;
        } else if (!org.apache.commons.lang3.StringUtils.isEmpty(status) && "all".equals(status)) {
            statusValue = null;
        } else {
            statusValue = Integer.valueOf(status);
        }
        ContactQO.ContactQOBuilder contactQOBuilder = ContactQO.builder()
                .keyword(keyword)
                .tenantId(tenantId)
                .hasDepartment(true)
                .departmentOids(departmentOids)
                .status(statusValue)
                .companyOids(companyOids)
                .currentUserOid(currentUserOid)
                .orderByEmployeeId(true);
        List<UserDTO> list = listUserDTOByQO(
                contactQOBuilder.build(),page);
        return list;
    }

    public List<Contact> listContactByCompanyOidAndStatus(UUID companyOid, Integer status) {
        return listByQO(ContactQO.builder()
                .companyOid(companyOid)
                .status(status)
                .build());
    }

    /**
     * 获取租户下员工数量统计
     *
     * @param tenantId
     * @return
     */
    public int getTenantEnableContactCount(Long tenantId, EmployeeStatusEnum employeeStatus) {
        return super.selectCount(new EntityWrapper<Contact>().eq("tenant_id", tenantId)
                .eq("status", employeeStatus.getId()));
    }

    public List<Contact> findByDepartmentOid(UUID departmentOid) {
        return listByQO(ContactQO.builder()
                .departmentOid(departmentOid)
                .exLeaved(true)
                .build());
    }

    public List<Contact> findByDepartmentOidAndCompanyId(UUID departmentOid, Long companyId, Page page) {
        return baseMapper.listByQO(page, ContactQO.builder()
                .departmentOid(departmentOid)
                .companyId(companyId)
                .exLeaved(true)
                .build());
    }


    public List<UserDTO> listUsersByKeyword(Long tenantId, String keyword, Boolean needEmployeeId) {
        List<UserDTO> results = baseMapper.listUsersByKeyword(tenantId,keyword,needEmployeeId);
        for(UserDTO dto : results){
            dto.setEmployeeId(UserInfoDisplayUtil.recoverDeleteInfo(dto.getEmployeeId()));
            dto.setMobile(UserInfoDisplayUtil.recoverDeleteInfo(dto.getMobile()));
            dto.setEmail(UserInfoDisplayUtil.recoverDeleteInfo(dto.getEmail()));
        }
        return results;
    }

    public Contact getByQO(ContactQO contactQO) {
        List<Contact> contacts = baseMapper.listByQO(contactQO);
        if (contacts.size() > 0) {
            return contacts.get(0);
        }
        return null;
    }

    public ContactDTO getDtoByQO(ContactQO contactQO) {
        List<ContactDTO> contactDTOS = baseMapper.listDtoByQO(contactQO);
        if (contactDTOS.size() > 0) {
            return contactDTOS.get(0);
        }
        return null;
    }

    /**
     * 新增或编辑员工
     *
     * @param userDTO
     * @return userDTO
     */
    @LcnTransaction
    @SyncLock(lockPrefix = SyncLockPrefix.EMPLOYEE_NEW,waiting = true,timeOut = 3000)
    public UserDTO upsertUserForControl(UserDTO userDTO, UUID currentUserOID, Long tenantId) {
        CompanyDTO newCompany = companyService.getByCompanyOid(userDTO.getCompanyOid());
        Department newDepartment = departmentService.getByDepartmentOid(userDTO.getDepartmentOid());
        LegalEntityDTO legalEntityDTO = legalEntityService.getLegalEntity(newCompany.getLegalEntityId());
        UUID corporationOID = null;
        if(legalEntityDTO != null) {
            corporationOID = legalEntityDTO.getCompanyReceiptedOid();
        }
        Contact contact = null;
        DepartmentUser departmentUser = null;
        Phone phone = null;
        String email = userDTO.getEmail();
        String title = userDTO.getTitle();
        String mobile = userDTO.getMobile();
        String countryCode = userDTO.getCountryCode();
        String fullName = userDTO.getFullName();
        String employeeId = userDTO.getEmployeeId();

        boolean isCreateUser = false;
        boolean isCreateMobile = false;//是否创建手机号
        boolean isModifyMobile = false;//是否修改手机号
        boolean isModifyDepartment = false;//是否修改部门
        boolean isModifyEmail = false;//是否修改邮箱
        boolean isModifyName = false;//是否修改姓名

        if (userDTO.getUserOid() == null) {
            UserCO user = null;
            //insert
            isCreateUser = true;
            //检查工号是否已存在
            Contact isEmployeeIdExists = getOneByEmployeeId(employeeId);
            if (isEmployeeIdExists != null) {
                throw new BizException(RespCode.EMPLOYEE_ID_EXISTS);
            }
            String login = employeeId;
            user = new UserCO();
            contact = new Contact();
            departmentUser = new DepartmentUser();
            contact.setCompanyId(newCompany.getId());
            contact.setStatus(EmployeeStatusEnum.NORMAL.getId());
            contact.setTenantId(tenantId);
            if(!StringUtil.isNullOrEmpty(email) && baseMapper.varifyEmailExsits(email) != null){
                throw new BizException(RespCode.EMPLOYEE_EMAIL_EXISTS);
            }
            contact.setEmail(email);
            if(!StringUtil.isNullOrEmpty(mobile)) {
                if (phoneService.verifyPhoneExsits(mobile) != null) {
                    throw new BizException(RespCode.EMPLOYEE_PHONE_EXISTS);
                }
                isCreateMobile = true;
            }
            //保存至User
            user.setLogin(login);
            user.setLanguage(OrgInformationUtil.getCurrentLanguage());
            user.setTenantId(tenantId);
            user.setPhoneNumber(mobile);
            user.setEmail(email);
            user.setUserName(fullName);
            user.setRemark(title);
            user = hcfOrganizationInterface.saveUser(user);
            contact.setUserId(user.getId());
            contact.setUserOid(user.getUserOid());
        } else {
            //update
            contact = getContactByUserOid(userDTO.getUserOid());
            if (contact == null) {
                throw new BizException("6040015");
            }
            if (!contact.getTenantId().equals(tenantId)) {
                throw new BizException("6040015");
            }
            //已离职人员 不能修改信息
            if (EmployeeStatusEnum.LEAVED.equals(EmployeeStatusEnum.parse(contact.getStatus()))) {
                throw new BizException("6040016");
            }
            departmentUser = departmentUserService.selectOne(new EntityWrapper<DepartmentUser>()
                    .eq("user_id",contact.getUserId()));
            phone = phoneService.getOneByContactId(contact.getId());

            //修改公司，更新token公司取值
            if (!newCompany.getId().equals(contact.getCompanyId())) {
                contact.setCompanyId(newCompany.getId());
            }
            //修改部门
            if(!newDepartment.getId().equals(departmentUser.getDepartmentId())){
                isModifyDepartment = true;
            }
            //修改姓名
            if(!StringUtil.isNullOrEmpty(fullName) && !fullName.equalsIgnoreCase(contact.getFullName())){
                isModifyName = true;
            }
            //修改邮箱
            if (!StringUtil.isNullOrEmpty(email) && !email.equalsIgnoreCase(contact.getEmail())) {
                if(baseMapper.varifyEmailExsits(email) != null){
                    throw new BizException(RespCode.EMPLOYEE_EMAIL_EXISTS);
                }
                isModifyEmail = true;
                contact.setEmail(email);
            }
            //修改手机号
            if (!StringUtil.isNullOrEmpty(mobile)) {
                if(phone == null){
                    if(phoneService.verifyPhoneExsits(mobile) != null) {
                        throw new BizException(RespCode.EMPLOYEE_PHONE_EXISTS);
                    }
                    isCreateMobile = true;
                }else if(!mobile.equalsIgnoreCase(phone.getPhoneNumber())){
                    if(phoneService.verifyPhoneExsits(mobile) != null) {
                        throw new BizException(RespCode.EMPLOYEE_PHONE_EXISTS);
                    }
                    isModifyMobile = true;
                }else if(!countryCode.equalsIgnoreCase(phone.getCountryCode())){
                    isModifyMobile = true;
                }
            }else{
                phoneService.delete(new EntityWrapper<Phone>().eq("contact_id",contact.getId()));
            }
        }

        contact.setCorporationOid(corporationOID);
        contact.setFullName(fullName);
        contact.setEmployeeId(employeeId);
        contact.setTitle(title);
        contact.setBirthday(TypeConversionUtils.getStartTimeForDayYYMMDD(userDTO.getBirthday()));
        contact.setEntryDate(TypeConversionUtils.getStartTimeForDayYYMMDD(userDTO.getEntryDate()));

        // 判断性别编码是否为空
        if (!StringUtil.isNullOrEmpty(userDTO.getGenderCode())) {
            // 根据用户租户和性别值列表类型查询值列表
            SysCodeValueCO genderDTO = hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.SEX.getId().toString(), userDTO.getGenderCode());
            if (genderDTO != null) {
                contact.setGender(Integer.parseInt(genderDTO.getValue()));
                contact.setGenderCode(userDTO.getGenderCode());
                userDTO.setGender(Integer.parseInt(genderDTO.getValue()));
            }
        }else{
            contact.setGender(null);
            contact.setGenderCode(null);
        }
        // 判断职务编码是否为空
        if(!StringUtil.isNullOrEmpty(userDTO.getDutyCode())) {
            // 根据用户租户和职务值列表类型查询值列表
            SysCodeValueCO dutyDTO = hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.DUTY.getId().toString(), userDTO.getDutyCode());
            if (dutyDTO != null) {
                contact.setDuty(dutyDTO.getName());
                contact.setDutyCode(userDTO.getDutyCode());
                userDTO.setDutyCode(dutyDTO.getValue());
            }
        }else{
            contact.setDuty(null);
            contact.setDutyCode(null);
        }
        // 判断级别编码是否为空
        if (!StringUtil.isNullOrEmpty(userDTO.getRankCode())) {
            // 根据用户租户和职级值列表类型查询值列表
            SysCodeValueCO rankDTO = hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.LEVEL.getId().toString(), userDTO.getRankCode());
            if (rankDTO != null) {
                contact.setRank(rankDTO.getName());
                contact.setRankCode(userDTO.getRankCode());
                userDTO.setRankCode(rankDTO.getValue());
            }
        }else{
            contact.setRank(null);
            contact.setRankCode(null);
        }
        // 判断员工类型是否为空
        if (!StringUtil.isNullOrEmpty(userDTO.getEmployeeTypeCode())) {
            // 根据用户租户和人员类型值列表类型查询值列表
            SysCodeValueCO employeeTypeDTO = hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.EMPLOYEETYPE.getId().toString(), userDTO.getEmployeeTypeCode());
            if (employeeTypeDTO != null) {
                contact.setEmployeeType(employeeTypeDTO.getName());
                contact.setEmployeeTypeCode(userDTO.getEmployeeTypeCode());
                userDTO.setEmployeeType(employeeTypeDTO.getName());
            }
        }else{
            contact.setEmployeeType(null);
            contact.setEmployeeTypeCode(null);
        }

        //员工上级领导非必填查询封装
        UUID directManager = userDTO.getDirectManager();
        if (!StringUtils.isEmpty(directManager)) {
            Contact directManagerOp = getByUserOid(directManager);
            if (directManagerOp != null) {
                contact.setDirectManager(directManager);
                userDTO.setDirectManager(directManager);
                userDTO.setDirectManagerId(directManagerOp.getEmployeeId());
                userDTO.setDirectManagerName(directManagerOp.getFullName());
            }
        } else {
            contact.setDirectManager(null);
        }
        userDTO.setUserOid(contact.getUserOid());
        //保存至Contact
        if(isCreateUser) {
            contact = save(contact);
        }else {
            baseMapper.updateAllColumnById(contact);
        }
        //保存至Phone
        if(isCreateMobile){
            phoneService.setMobile(contact.getId(),mobile,countryCode);
        }else if(isModifyMobile){
            phoneService.updateByContactId(contact.getId(),mobile,countryCode);
        }
        //更新姓名,手机或邮箱至用户
        if(!isCreateUser && (isCreateMobile || isModifyMobile || isModifyEmail || isModifyName)){
            UserCO userCO = new UserCO();
            userCO.setId(contact.getUserId());
            userCO.setUserName(fullName);
            userCO.setPhoneNumber(mobile);
            userCO.setEmail(email);
            hcfOrganizationInterface.saveUser(userCO);
        }

        //保存至DepartmentUser
        if (isCreateUser) {
            departmentUser.setUserId(contact.getUserId());
            departmentUser.setDepartmentId(newDepartment.getId());
            departmentUserService.insert(departmentUser);
        } else if(isModifyDepartment) {
            departmentUser.setDepartmentId(newDepartment.getId());
            departmentUserService.updateByUserId(departmentUser);
        }
        userDTO.setDepartmentOid(newDepartment.getDepartmentOid());
        userDTO.setDepartmentPath(newDepartment.getPath());
        userDTO.setDepartmentName(newDepartment.getName());
        userDTO.setCorporationOid(newCompany.getCompanyOid());
        userDTO.setSenior(contact.getSenior());
        return userDTO;
    }

    /**
     * 根据用户Oid查询未删除用户
     *
     * @param userOid：用户Oid
     * @return
     */
    public Contact getByUserOid(UUID userOid) {
        return getByQO(ContactQO.builder().userOid(userOid).build());
    }

    public UserDTO contactToUserDTO(Contact contact) {
        return contactToUserDTO(contact, true, true);
    }

    public UserDTO contactToUserDTO(Contact contact, Boolean isCompany, Boolean isDepartment) {

        if (contact == null) {
            return null;
        }

        UserDTO userDTO = new UserDTO();
        mapper.map(contact, userDTO);
        userDTO.setId(contact.getUserId());
        userDTO.setContactId(contact.getId());
        if (contact.getHeadPortrait() != null) {
            AttachmentCO attachment = hcfOrganizationInterface.getAttachmentByOid(contact.getHeadPortrait());
            if (attachment != null) {
                userDTO.setFilePath(attachment.getFileUrl());
                userDTO.setAvatar(attachment.getFileUrl());
            }
        }
        Set<Phone> phones = getPhones(contact.getId());
        userDTO.setEmployeeId(contact.getEmployeeId());
        userDTO.setFullName(contact.getFullName());
        userDTO.setEmail(contact.getEmail());
        userDTO.setTitle(contact.getTitle());
        if(contact.getEntryDate() != null) {
            userDTO.setEntryDate(contact.getEntryDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        if(contact.getBirthday() != null) {
            userDTO.setBirthday(contact.getBirthday().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        userDTO.setEmployeeId(contact.getEmployeeId());
        userDTO.setMobile(getMobile(contact.getId()));
        userDTO.setSenior(contact.getSenior());
        userDTO.setCorporationOid(contact.getCorporationOid());
        userDTO.setGender(contact.getGender() == null ? 0 : contact.getGender());
        if ("0".equals(contact.getGenderCode())) {
            userDTO.setGenderCode(messageService.getMessageDetailByCode(RespCode.SYS_GENDER_MAN));
        } else if ("1".equals(contact.getGenderCode())) {
            userDTO.setGenderCode(messageService.getMessageDetailByCode(RespCode.SYS_GENDER_WOMAN));
        } else if ("2".equals(contact.getGenderCode())) {
            userDTO.setGenderCode(messageService.getMessageDetailByCode(RespCode.SYS_GENDER_UNKNOWN));
        }
        userDTO.setDutyCode(contact.getDutyCode());
        userDTO.setDuty(contact.getDuty());
        userDTO.setRank(contact.getRank());
        userDTO.setRankCode(contact.getRankCode());
        userDTO.setEmployeeType(contact.getEmployeeType());
        userDTO.setEmployeeTypeCode(contact.getEmployeeTypeCode());
        userDTO.setCountryCode(phoneService.getCountryCode(phones));
        userDTO.setAvatarOid(contact.getHeadPortrait());
        userDTO.setDirectManager(contact.getDirectManager());
        if (contact.getDirectManager() != null) {
            userDTO.setDirectManagerName(this.selectOne(new EntityWrapper<Contact>().eq("user_oid", contact.getDirectManager())).getFullName());
        }
        userDTO.setPhones(phones);
        if (contact.getCompanyId() != null && isCompany) {
            Company company = companyService.findOne(contact.getCompanyId());
            userDTO.setCompanyOid(company.getCompanyOid());
            userDTO.setCompanyName(company.getName());
        }
        if (isDepartment) {
            Optional<Department> department = departmentUserService.getDepartmentByUserId(contact.getUserId());
            if (department.isPresent()) {
                userDTO.setDepartmentName(department.get().getName());
                userDTO.setDepartmentPath(department.get().getPath());
                userDTO.setDepartmentId(department.get().getId());
                userDTO.setDepartmentOid(department.get().getDepartmentOid());
            }
        }

        return userDTO;
    }

    public String getMobile(Long contactId) {
        return phoneService.getMobile(contactId);
    }

    public Set<Phone> getPhones(Long contactId) {
        return phoneService.getPhones(contactId);
    }

    public UserDTO getUserDTOWithAuthorityAndDepartment(UUID userOid) {
        return contactToUserDTO(getContactByUserOid(userOid));
    }

    /**
     * get users by userOids
     *
     * @param userOids
     * @return
     */
    public List<UserDTO> listUserDTOByUserOids(Set<UUID> userOids) {
        return listUserDTOByUserOids(userOids, true);
    }

    public List<UserDTO> listUserDTOByUserOids(Set<UUID> userOids, boolean withAvatrar) {
        List<Contact> userList = listByUserOidIn(new ArrayList<>(userOids));
        return userList.stream().map(u -> contactToUserDTO(u)).collect(Collectors.toList());

    }

    public List<Contact> listByUserOidIn(List<UUID> userOids) {
        return selectList(new EntityWrapper<Contact>().in("user_oid", userOids));
    }

    //    @Cacheable(key="#email")
    public Optional<Contact> getByEmail(String email) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(email)) {
            throw new BizException(RespCode.EMAIL_IS_NULL);
        }
        return Optional.ofNullable(getByQO(ContactQO.builder().email(email).exLeaved(true).build()));
    }

    public List<UserDTO> getValidUsersByCompanyId(UUID companyOid, Page page) {
        List<Contact> users = baseMapper.listByQO(page,
                ContactQO.builder().tenantId(OrgInformationUtil.getCurrentTenantId()).exLeaved(true).orderByFullName(true).build());

        return users.stream().map(u -> contactToUserDTO(u)).collect(Collectors.toList());

    }


    public List<UserDTO> listUserDTOByQO(ContactQO contactQO,Page page){
        return listByQO(contactQO,page).stream().map(e -> contactToUserDTO(e)).collect(Collectors.toList());
    }

    /**
     * 搜索所有用户信息
     *
     * @param keyword
     * @return
     */
    public List<UserDTO> listUserDTOByKeyword(String keyword, Page page) {
        return listUserDTOByQO(ContactQO.builder()
                .tenantId(OrgInformationUtil.getCurrentTenantId())
                .keyword(keyword)
                .build(),page);
    }

    /**
     * 搜索正常用户信息
     *
     * @param keyword
     * @return
     */
    public List<UserDTO> findAvaliableUserDTO(String keyword, Page page) {
        return listUserDTOByQO(ContactQO.builder()
                .tenantId(OrgInformationUtil.getCurrentTenantId())
                .keyword(keyword)
                .statusAvailable(true)
                .build(),page);
    }

    /**
     * 搜索所有用户信息
     *
     * @param keyword
     * @return
     */
    public List<UserDTO> listUserDTOByKeywordAndCompany(String keyword, Boolean isCompany, Page page) {
        if (isCompany) {
            if (org.apache.commons.lang3.StringUtils.isEmpty(keyword)) {
                return listUserDTOByQO(ContactQO.builder()
                        .tenantId(OrgInformationUtil.getCurrentCompanyId())
                        .build(),page);
            } else {
                return listUserDTOByQO(ContactQO.builder()
                        .tenantId(OrgInformationUtil.getCurrentCompanyId())
                        .keyword(keyword)
                        .build(),page);
            }
        } else {
            if (org.apache.commons.lang3.StringUtils.isEmpty(keyword)) {
                return listUserDTOByQO(ContactQO.builder()
                        .tenantId(OrgInformationUtil.getCurrentTenantId())
                        .build(),page);
            } else {
                return listUserDTOByQO(ContactQO.builder()
                        .tenantId(OrgInformationUtil.getCurrentTenantId())
                        .keyword(keyword)
                        .build(),page);
            }
        }
    }

    //取消待离职
    public void cancelLeaveOffice(UUID userOid) {
        Contact contact = this.getContactByUserOid(userOid);
        if (contact == null) {
            throw new ObjectNotFoundException(Contact.class, userOid);
        }
        if (EmployeeStatusEnum.parse(contact.getStatus()).equals(EmployeeStatusEnum.LEAVED)) {
            throw new BizException("6045001");
        }
        if (EmployeeStatusEnum.parse(contact.getStatus()).equals(EmployeeStatusEnum.LEAVING)) {
            contact.setStatus(EmployeeStatusEnum.NORMAL.getId());
            contact.setLeavingDate(null);
            updateById(contact);
        } else {
            throw new BizException("6045002");
        }
    }

    /**
     * 员工离职
     *
     * @param userOid
     */
    public void leaveOffice(UUID userOid) {
        Contact contact = getContactByUserOid(userOid);
        long currentMillis = ZonedDateTime.now().get(ChronoField.MILLI_OF_SECOND);
        String email = contact.getEmail();
        String employeeId = contact.getEmployeeId();
        String leavedEmail = email + "_" + Constants.LEAVED + "_" + currentMillis;
        String leavedEmployee = employeeId + "_" + Constants.LEAVED;
        if (EmployeeStatusEnum.LEAVED.getId().equals(contact.getStatus())) {
            return;
        }
        //由于员工离职后，还可解除离职需求，用户的电话不能被删除
        //将用户的所有手机号设置为number_LEAVED
        Set<Phone> phones = getPhones(contact.getId());
        for (Phone phone : phones) {
            if (phone.getPhoneNumber().indexOf("_") == -1) {
                redisHelper.deleteByKey(CacheConstants.PHONE_KEY_PREFIX + phone.getPhoneNumber());
                phone.setPhoneNumber(phone.getPhoneNumber() + "_" + Constants.LEAVED);

            }
        }
        contact.setStatus(EmployeeStatusEnum.LEAVED.getId());
        contact.setLeavingDate(ZonedDateTime.now());
        hcfOrganizationInterface.updateUserLeaveOffice(contact.getUserId());
        phoneService.insertOrUpdateBatch(new ArrayList<>(phones));
        contact.setEmail(leavedEmail);
        contact.setEmployeeId(leavedEmployee);
        updateById(contact);
    }

    /**
     * 根据公司Oid、输入文字、法人实体Oid、部门Oid、职务、模糊查询用户信息
     *
     * @param customEnumerationItemOid：值列表项Oid
     * @param companyOid：公司Oid
     * @param keyword：输入文字
     * @param corporationOids：法人实体Oid
     * @param departmentOids：部门Oid
     * @param title：职务
     * @param page：分页对象
     * @return
     */
    @Transactional(readOnly = true)
    public List<UserDTO> findByCompanyOidAndTerm(UUID customEnumerationItemOid, UUID companyOid, String keyword, List<UUID> corporationOids, List<UUID> departmentOids, String title, Page page) {
        return baseMapper.listByKeywordAndCond(page, ContactQO.builder()
                .keyword(keyword)
                .status(EmployeeStatusEnum.NORMAL.getId())
                .departmentOids(departmentOids)
                .legalEntityOids(corporationOids)
                .tenantId(OrgInformationUtil.getCurrentTenantId())
                .title(title)
                .build());
    }

    public Contact getByTenantIdAndUserOid(Long tenantId, UUID userOid) {
        if (userOid == null) {
            return null;
        } else {
            return getByQO(ContactQO.builder().tenantId(tenantId).userOid(userOid).build());

        }
    }

    public UserDTO getUserInfoV2ByUserOid(UUID userOid, boolean isApp, Long tenantId, boolean tenantOnly) {
        Contact contact = null;
        if (tenantOnly) {
            contact = this.getByTenantIdAndUserOid(tenantId, userOid);
        } else {
            contact = selectOne(new EntityWrapper<Contact>().eq("user_oid",userOid));
        }
        if (contact == null) {
            throw new BizException(RespCode.USER_NOT_EXIST);
        }
        UserDTO userContactDTO = contactToUserDTO(contact);
        if (contact.getStatus().equals(EmployeeStatusEnum.LEAVED.getId())) {
            userContactDTO.setMobile(recoverDeleteInfo(userContactDTO.getMobile()));
            userContactDTO.setEmail(recoverDeleteInfo(userContactDTO.getEmail()));
            userContactDTO.setEmployeeId(recoverDeleteInfo(userContactDTO.getEmployeeId()));
        }

        // 如果为app查询那么返回对应的法人oid、否则返回对应的公司
        if (isApp) {
            LegalEntityDTO legalEntityDTO = legalEntityService.getLegalEntity(companyService.selectById(contact.getCompanyId()).getLegalEntityId());
            userContactDTO.setCorporationOid(legalEntityDTO.getCompanyReceiptedOid());
            userContactDTO.setCorporationName(legalEntityDTO.getEntityName());
        } else {
            userContactDTO.setCorporationOid(userContactDTO.getCompanyOid());
            userContactDTO.setCorporationName(userContactDTO.getCompanyName());
        }

        if (!org.apache.commons.lang3.StringUtils.isEmpty(userContactDTO.getCountryCode())) {
            Map<String, Object> param = new HashedMap();
            param.put("short_name", userContactDTO.getCountryCode());
            List<MobileValidate> mobileValidateList = mobileValidateMapper.selectByMap(param);
            if(CollectionUtils.isNotEmpty(mobileValidateList)){
                userContactDTO.setMobile("+" + mobileValidateList.get(0).getCountryCode()+ " " + userContactDTO.getMobile());
            }
        }
        //员工上级领导查询封装
        if(userContactDTO.getDirectManager() != null) {
            if (!org.apache.commons.lang3.StringUtils.isEmpty(userContactDTO.getDirectManager().toString())) {
                Contact directManagerOp = this.getByUserOid(userContactDTO.getDirectManager());
                if (directManagerOp != null) {
                    userContactDTO.setDirectManagerId(directManagerOp.getEmployeeId());
                    userContactDTO.setDirectManagerName(directManagerOp.getFullName());
                }
            }
        }
        return userContactDTO;
    }

    public String recoverDeleteInfo(String info) {
        if (org.apache.commons.lang3.StringUtils.isBlank(info)
                || !info.contains("_")) {
            return info;
        }
        int index = 0;
        String[] splits = {"_LEAVED", "_DELETE"};
        for (int i = 0; i < splits.length && index <= 0; i++) {
            index = info.indexOf(splits[i]);
        }
        if (index > 0) {
            info = info.substring(0, index);
        }
        return info;
    }

    /**
     * 根据公司oid和用户oid集合移动到指定公司oid
     *
     * @param companyOidFrom：老公司oid
     * @param userOids：用户oid集合
     * @param companyOidTo：新公司oid
     * @param selectMode：选择模式
     */
    public void moveCompanyUsers(UUID companyOidFrom, List<UUID> userOids, UUID companyOidTo, String selectMode) {
        if (companyOidFrom.equals(companyOidTo)) {
            return;
        }
        // 查询新公司是否存在
        Company company = companyService.getByCompanyOidCache(companyOidTo);
        if (null == company) {
            throw new ObjectNotFoundException(Company.class, companyOidTo);
        }
        List<Contact> contacts;
        // 判断是否全选
        if (selectMode.equals(Constants.MODE_ALL_PAGE)) {
            // 根据公司id查询用户oid
            contacts = listByQO(ContactQO.builder().companyOid(companyOidFrom).build());
        } else {
            contacts = listByUserOidIn(userOids);
        }

        for (Contact contact : contacts) {
            //判断新公司下工号是否重复
            //租户下面工号唯一，不需要再检查，在用户创建和保存时做工号检查
            contact.setCompanyId(company.getId());
            super.updateById(contact);
        }
    }

    //设置离职日期
    public void setLeavingDate(Long tenantId, UUID userOid, String leavingDate) {
        Contact contact = this.getByUserOid(userOid);
        if (contact == null) {
            throw new BizException(RespCode.USER_NOT_EXIST);
        }
        if (!contact.getTenantId().equals(tenantId)) {
            throw new BizException(RespCode.USER_NOT_EXIST);
        }
        // 判断工号是否为空、如果为空则不允许离职
        if (org.apache.commons.lang3.StringUtils.isEmpty(contact.getEmployeeId())) {
            throw new BizException("6040019");
        }
        //设置的日期是今天之前，直接离职处理
        if (TypeConversionUtils.getStartTimeForDayYYMMDD(leavingDate).isBefore(ZonedDateTime.now())) {
            this.leaveOffice(userOid);
        } else {
            contact.setStatus(EmployeeStatusEnum.LEAVING.getId());
            contact.setLeavingDate(TypeConversionUtils.getStartTimeForDayYYMMDD(leavingDate));
            updateById(contact);
        }
    }

    /**
     * 离职员工恢复入职
     *
     * @param contact
     */
    @SyncLock(lockPrefix = SyncLockPrefix.EMPLOYEE_NEW,waiting = true,timeOut = 3000)
    public void recoverEntry(Contact contact) {
        String leaveMail = contact.getEmail();
        String leaveEmployee = contact.getEmployeeId();
        String normalMail = null;
        String normalEmployee = null;
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(leaveMail)) {
            normalMail = recoverDeleteInfo(leaveMail);
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(leaveEmployee)) {
            normalEmployee = leaveEmployee.split("_")[0];
        }
        if(baseMapper.varifyEmailExsits(normalMail) != null){
            throw new BizException(RespCode.EMPLOYEE_EMAIL_EXISTS);
        }
        Company company = companyService.selectById(contact.getCompanyId());
        Optional<Contact> optional = this.getByEmployeeIdAndTenantId(company.getTenantId(), normalEmployee);
        if (optional.isPresent()) {
            throw new BizException(RespCode.EMPLOYEE_EXISTS, new String[]{normalEmployee});
        }
        //由于持久化问题，不能直接通过sql修改，通过orm
        contact.setEmail(normalMail);
        contact.setEmployeeId(normalEmployee);

        //将用户的所有手机号由number_LEAVED，恢复为 number
        Set<Phone> phones = new HashSet<>();
        String primaryMobile = "";
        for (Phone phone : getPhones(contact.getId())) {
            if (phone.getPhoneNumber().split("_").length == 2 && Constants.LEAVED.equals(phone.getPhoneNumber().split("_")[1])) {
                phone.setPhoneNumber(phone.getPhoneNumber().split("_")[0]);
                phones.add(phone);

                if (phone.getPrimaryFlag()) {
                    primaryMobile = phone.getPhoneNumber();
                }
            }
        }

        if (!org.apache.commons.lang3.StringUtils.isEmpty(primaryMobile)) {
            if(phoneService.verifyPhoneExsits(primaryMobile) != null) {
                throw new BizException(RespCode.EMPLOYEE_PHONE_EXISTS);
            }
        }
        if(!phones.isEmpty()) {
            phoneService.insertOrUpdateBatch(new ArrayList<>(phones));
        }
        contact.setStatus(EmployeeStatusEnum.NORMAL.getId());
        updateById(contact);
        hcfOrganizationInterface.updateUserRecoverEntry(contact.getUserId());
    }

    public byte[] exportUserInfoImportTemplate() {
        return userImportService.exportUserInfoImportTemplate();
    }

    public byte[] exportContactBankAccountImportTemplate() {
        return contactBankAccountImportService.exportContactBankAccountImportTemplate();
    }

    public byte[] exportContactCardImportTemplate() {
        return contactCardImportService.exportContactCardImportTemplate();
    }

    public UUID importUserPublic(MultipartFile file) throws Exception {
        try {
            InputStream in = file.getInputStream();
            XSSFSheet sheet = new XSSFWorkbook(in).getSheetAt(0);
            String sheetName = sheet.getSheetName();
            if (UserImportCode.TEMPLATE_SHEET_KEYWORD.contains(sheetName)) {
                return importUserInfo(file);
            }else if (ContactCardImportCode.TEMPLATE_SHEET_KEYWORD.contains(sheetName)) {
                return contactCardImportService.importUserInfo(file);
            }
            else if (ContactBankAccountImportCode.TEMPLATE_SHEET_KEYWORD.contains(sheetName)) {
                return contactBankAccountImportService.importUserInfo(file);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public UUID importUserInfo(MultipartFile file) throws Exception{
        UUID batchNumber = UUID.randomUUID();
        InputStream in = file.getInputStream();
        ExcelImportHandler<UserTempDomain> excelImportHandler = new ExcelImportHandler<UserTempDomain>() {
            @Override
            public void clearHistoryData() {
                userImportService.deleteHistoryData();
            }

            @Override
            public Class getEntityClass() {
                return UserTempDomain.class;
            }

            @Override
            public List<UserTempDomain> persistence(List<UserTempDomain> list) {
                // 导入数据
                userImportService.insertBatch(list);
                // 数据唯一性校验
                userImportService.updateExists(batchNumber.toString());
                return list;
            }

            @Override
            public void check(List<UserTempDomain> list) {
                checkImportData(list, batchNumber.toString());
            }
        };
        excelImportService.importExcel(in, false, 2, excelImportHandler);
        return batchNumber;
    }

    public void checkImportData(List<UserTempDomain> list, String batchNumber){
        list.stream().forEach(item -> item.setErrorDetail(""));
        // 必输字段非空校验
        list.stream().filter(item -> StringUtil.isNullOrEmpty(item.getEmployeeId())
                || StringUtil.isNullOrEmpty(item.getFullName())
                || StringUtil.isNullOrEmpty(item.getCompanyCode())
                || StringUtil.isNullOrEmpty(item.getDepartmentCode())
                || StringUtil.isNullOrEmpty(item.getEmail()))
                .forEach(item -> {
                    item.setErrorFlag(true);
                    item.setErrorDetail(item.getErrorDetail() + "必输字段不能为空！");
                });
        // 数据合法性校验
        for(UserTempDomain user : list){
            //工号需唯一性校验
            //邮箱需唯一性校验
            //手机需唯一性校验
            //直属领导工号需校验是否存在

            //公司合法校验
            String companyCode = user.getCompanyCode();
            if(!StringUtil.isNullOrEmpty(companyCode)){
                CompanyCO companyCO = companyService.getByCompanyCode(companyCode);
                if(companyCO != null){
                    user.setCompanyId(companyCO.getId());
                }else {
                    user.setErrorFlag(true);
                    user.setErrorDetail(user.getErrorDetail() + "公司不存在!");
                }
            }
            //部门合法校验
            String departmentCode = user.getDepartmentCode();
            if(!StringUtil.isNullOrEmpty(departmentCode)){
                DepartmentCO departmentCO = departmentService.getDepartmentByCodeAndTenantId(departmentCode);
                if(departmentCO != null){
                    user.setDepartmentId(departmentCO.getId());
                }else {
                    user.setErrorFlag(true);
                    user.setErrorDetail(user.getErrorDetail() + "部门不存在!");
                }
            }
            //手机区号代码校验
            String mobileAreaCode = user.getMobileAreaCode();
            if(!StringUtil.isNullOrEmpty(mobileAreaCode)){
                SysCodeValueCO countryCodeDTO = hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.NATIONALITY.getId().toString(),mobileAreaCode);
                if(countryCodeDTO == null){
                    user.setErrorFlag(true);
                    user.setErrorDetail(user.getErrorDetail()+ "手机区号代码不存在!");
                }
            }
            String directManagerId =user.getDirectManagerId();
            if(!StringUtil.isNullOrEmpty(directManagerId)){
                if(directManagerId.equals(user.getEmployeeId())){
                    user.setErrorFlag(true);
                    user.setErrorDetail(user.getErrorDetail()+ "直属领导不允许为自己!");
                }else{
                    UUID directManager = userImportService.getUserOidByEmployeeIdAndTenantId(directManagerId);
                    if(directManager != null){
                        user.setDirectManager(directManager);
                    }else{
                        user.setErrorFlag(true);
                        user.setErrorDetail(user.getErrorDetail()+ "直属领导不存在!");
                    }
                }
            }
            //职务合法校验
            String dutyCode = user.getDutyCode();
            if(!StringUtil.isNullOrEmpty(dutyCode)){
                SysCodeValueCO dutyDTO = hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.DUTY.getId().toString(), dutyCode);
                if (dutyDTO != null) {
                    user.setDuty(dutyDTO.getName());
                }else {
                    user.setErrorFlag(true);
                    user.setErrorDetail(user.getErrorDetail()+"职务不存在!");
                }
            }
            //人员类型合法校验
            String employeeTypeCode = user.getEmployeeTypeCode();
            if(!StringUtil.isNullOrEmpty(employeeTypeCode)){
                SysCodeValueCO employeeTypeDTO = hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.EMPLOYEETYPE.getId().toString(), employeeTypeCode);
                if (employeeTypeDTO != null) {
                    user.setEmployeeType(employeeTypeDTO.getName());
                }else {
                    user.setErrorFlag(true);
                    user.setErrorDetail(user.getErrorDetail()+"人员类型不存在!");
                }
            }
            // 级别合法校验
            String rankCode = user.getRankCode();
            if(!StringUtil.isNullOrEmpty(user.getRankCode())){
                SysCodeValueCO rankDTO = hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.LEVEL.getId().toString(), rankCode);
                if (rankDTO != null) {
                    user.setRank(rankDTO.getName());
                }else {
                    user.setErrorFlag(true);
                    user.setErrorDetail(user.getErrorDetail()+"级别不存在!");
                }
            }
            // 性别合法校验
            String genderCode = user.getGenderCode();
            if(!StringUtil.isNullOrEmpty(user.getGenderCode())){
                SysCodeValueCO genderDTO = hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.SEX.getId().toString(),genderCode);
                if(genderDTO != null){
                    user.setGender(Integer.getInteger(genderDTO.getName()));
                }else {
                    user.setErrorFlag(true);
                    user.setErrorDetail(user.getErrorDetail()+"性别不存在!");
                }
            }
            // 生日数据校验
            String birthdayStr = user.getBirthdayStr();
            if(!StringUtil.isNullOrEmpty(birthdayStr)){
                String t = birthdayStr.replace("/","-");
                try {
                    ZonedDateTime birthday = TypeConversionUtils.getStartTimeForDayYYMMDD(t);
                    user.setBirthday(birthday);
                }catch (Exception e){
                    user.setErrorFlag(true);
                    user.setErrorDetail(user.getErrorDetail() + "生日日期格式有误!");
                }

            }
            // 入职时间校验
            String entryDateStr = user.getEntryDateStr();
            if(!StringUtil.isNullOrEmpty(entryDateStr)){
                String t = entryDateStr.replace("/","-");
                try {
                    ZonedDateTime entryDate = TypeConversionUtils.getStartTimeForDayYYMMDD(t);
                    user.setEntryDate(entryDate);
                }catch (Exception e){
                    user.setErrorFlag(true);
                    user.setErrorDetail(user.getErrorDetail() + "入职日期格式有误!");
                }
            }
            user.setBatchNumber(batchNumber);
            user.setLogin("admin"+user.getEmployeeId());
            user.setUserOid(UUID.randomUUID());
        }
    }

    public ImportResultDTO queryResultInfo(String transactionID) {
        if(userImportService.varifyBatchNumberExsits(transactionID) != null){
            return userImportService.queryResultInfo(transactionID);
        }else if(contactBankAccountImportService.varifyBatchNumberExsits(transactionID) != null){
            return contactBankAccountImportService.queryResultInfo(transactionID);
        }else if(contactCardImportService.varifyBatchNumberExists(transactionID)){
            return contactCardImportService.queryResultInfo(transactionID);
        }else{
            return null;
        }
    }

    public byte[] exportFailedData(String transactionID) {
        if(userImportService.varifyBatchNumberExsits(transactionID) != null){
            return userImportService.exportFailedData(transactionID);
        }else if(contactBankAccountImportService.varifyBatchNumberExsits(transactionID) != null){
            return contactBankAccountImportService.exportFailedData(transactionID);
        }else if(contactCardImportService.varifyBatchNumberExists(transactionID)){
            return contactCardImportService.exportFailedData(transactionID);
        }else{
            return null;
        }
    }


    public Integer deleteImportData(String transactionID) {
        if(userImportService.varifyBatchNumberExsits(transactionID) != null){
            return userImportService.deleteImportData(transactionID);
        }else if(contactBankAccountImportService.varifyBatchNumberExsits(transactionID) != null){
            return contactBankAccountImportService.deleteImportData(transactionID);
        }else if(contactCardImportService.varifyBatchNumberExists(transactionID)){
            return contactCardImportService.deleteImportData(transactionID);
        }else{
            return null;
        }
    }

    public Object confirmImport(String transactionID) {
        if(userImportService.varifyBatchNumberExsits(transactionID)!= null){
            return contactConfirmImport(transactionID,null);
        }else if(contactBankAccountImportService.varifyBatchNumberExsits(transactionID)!= null){
            return contactBankAccountImportService.confirmImport(transactionID);
        }else if(contactCardImportService.varifyBatchNumberExists(transactionID)){
            return contactCardImportService.confirmImport(transactionID);
        }else{
            return null;
        }
    }

    /**
     * 员工导入确认
     * @param transactionID
     * @param page
     * @return
     */
    public boolean contactConfirmImport(String transactionID,Page page) {
        if(page == null){
            page = PageUtil.getPage(0, 30);
        }
        List<UserTempDomain> userTempDomains = userImportService.listImportMessageByTransactionID(transactionID, page);
        List<UserCO> users = userTempDomains.stream().map(contact -> {
            UserCO user = new UserCO();
            user.setLogin(contact.getLogin());
            user.setTenantId(OrgInformationUtil.getCurrentTenantId());
            user.setLanguage(OrgInformationUtil.getCurrentLanguage());
            user.setRemark(contact.getTitle());
            user.setUserName(contact.getFullName());
            user.setEmail(contact.getEmail());
            user.setPhoneNumber(contact.getMobile());
            return user;
        }).collect(Collectors.toList());
        hcfOrganizationInterface.saveUserBatch(users);
        Map<String, UserCO> userMap = users.stream().collect(Collectors.toMap(UserCO::getLogin, e -> e));
        userTempDomains.stream().forEach(userTemp -> {
            // 保存员工
            Contact contact = new Contact();
            UserCO user = userMap.get(userTemp.getLogin());
            BeanUtils.copyProperties(userTemp,contact);
            contact.setUserId(user.getId());
            contact.setUserOid(user.getUserOid());
            contact.setSenior(false);
            contact.setStatus(EmployeeStatusEnum.NORMAL.getId());
            save(contact);
             // 保存phone
            Phone phone = new Phone();
            phone.setContactId(contact.getId());
            phone.setCountryCode(userTemp.getMobileAreaCode());
            phone.setTypeNumber(PhoneType.MOBILE_PHONE.getId());
            phone.setPrimaryFlag(true);
            phone.setPhoneNumber(userTemp.getMobile());
            phoneService.insert(phone);
            //分配到具体部门
            if(userTemp.getDepartmentId() != null){
                DepartmentUser departmentUser = new DepartmentUser();
                departmentUser.setUserId(user.getId());
                departmentUser.setDepartmentId(userTemp.getDepartmentId());
                departmentUserService.insert(departmentUser);
            }
        });
        if(page.getTotal() > PageUtil.getPageEndIndex(page)){
            page.setCurrent(page.getCurrent() + 1);
            contactConfirmImport(transactionID,page);
        }
        userImportService.deleteImportData(transactionID);
        return true;
    }

    /**
     * 获取用户列表 分页 (员工管理)
     *
     * @param tenantId       必填，取租户下的所有用户
     * @param keyword        如果填了，根据条件取帐套下的用户
     * @param departmentOids 如果填了，则取部门下的用户
     * @param status         如果填了，则根据状态取部门下的用户
     * @param companyOids    如果填了，则根据公司id进行查询用户
     * @return 按employee_id, created_date排序
     */
    public List<UserDTO> listWithRoleByCondition(String keyword,
                                                 Long tenantId,
                                                 List<UUID> departmentOids,
                                                 String status,
                                                 List<UUID> companyOids,
                                                 Boolean isInactiveSearch,
                                                 Page page) {

        List<UserDTO> list = listUserDTOByCondition(keyword, tenantId, departmentOids, status, companyOids,null, page);
        return list;
    }

    public List<UserDTO> exportUserDTO(String keyword,
                                       Long tenantId,
                                       List<UUID> departmentOids,
                                       String status,
                                       List<UUID> companyOids,
                                       Page page){
        Integer statusValue = null;
        if (status == null) {
            statusValue = null;
        } else if (!org.apache.commons.lang3.StringUtils.isEmpty(status) && "all".equals(status)) {
            statusValue = null;
        } else {
            statusValue = Integer.valueOf(status);
        }
        List<UserDTO> list = listUserDTOByQO(ContactQO.builder()
                .keyword(keyword)
                .tenantId(tenantId)
                .hasDepartment(true)
                .departmentOids(departmentOids)
                .status(statusValue)
                .companyOids(companyOids)
                .build(),page);
        return list;
    }

    public List<ContactCO> listByUserIdsConditionByKeyWord(List<Long> ids, String keyWord) {
        Wrapper<ContactCO> wrapper = new EntityWrapper<ContactCO>().in("user_id", ids)
                .orderBy("status", true)
                .orderBy("user_id", true);
        if(! org.apache.commons.lang3.StringUtils.isEmpty(keyWord)){
            wrapper.andNew()
                    .like(org.apache.commons.lang3.StringUtils.isNotEmpty(keyWord), "full_name", keyWord)
                    .or(org.apache.commons.lang3.StringUtils.isNotEmpty(keyWord), "employee_id like concat(concat('%',{0}),'%')", keyWord);
        }
        return baseMapper.listCOByCondition(wrapper);
    }

    public List<ContactCO> listCOByKeyWord(String keyWord) {
        Long currentTenantId = OrgInformationUtil.getCurrentTenantId();
        Wrapper<ContactCO> wrapper = new EntityWrapper<ContactCO>().eq("tenant_id", currentTenantId)
                .andNew()
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(keyWord), "full_name", keyWord)
                .or(org.apache.commons.lang3.StringUtils.isNotEmpty(keyWord), "employee_id like concat(concat('%',{0}),'%')", keyWord)
                .orderBy("user_id", true);
        return baseMapper.listCOByCondition(wrapper);
    }

    public Page<ContactCO> pageCOConditionNameAndIgnoreIds(String employeeId,
                                                        String fullName,
                                                        String keyWord,
                                                        List<Long> ignoreIds,
                                                        Page<ContactCO> mybatisPage) {
        Long currentTenantId = OrgInformationUtil.getCurrentTenantId();
        Wrapper<ContactCO> wrapper = new EntityWrapper<ContactCO>().eq("tenant_id", currentTenantId)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(employeeId), "employee_id", employeeId)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(fullName), "full_name", fullName)
                .notIn(CollectionUtils.isNotEmpty(ignoreIds), "u.id", ignoreIds)
                .orderBy("status", true)
                .orderBy("employee_id");
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(keyWord)){
            wrapper.andNew()
                    .like( "full_name", keyWord)
                    .or("employee_id like concat(concat('%',{0}),'%')", keyWord);
        }
        List<ContactCO> userCOS = baseMapper.listCOByCondition(wrapper, mybatisPage);
        mybatisPage.setRecords(userCOS);
        return mybatisPage;

    }

    public Page<ContactCO> pageCOConditionNameAndIds(String employeeId,
                                                           String fullName,
                                                           String keyWord,
                                                           List<Long> ids,
                                                           Page<ContactCO> mybatisPage) {
        if (CollectionUtils.isEmpty(ids)) {
            return new Page<>();
        }
        Long currentTenantId = OrgInformationUtil.getCurrentTenantId();
        Wrapper<ContactCO> wrapper = new EntityWrapper<ContactCO>().eq("tenant_id", currentTenantId)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(employeeId), "employee_id", employeeId)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(fullName), "full_name", fullName)
                .in(CollectionUtils.isNotEmpty(ids), "user_id", ids)
                .orderBy("status", true)
                .orderBy("user_id", true);
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(keyWord)){
            wrapper.andNew()
                    .like( "full_name", keyWord)
                    .or("employee_id like concat(concat('%',{0}),'%')", keyWord);
        }
        List<ContactCO> userCOS = baseMapper.listCOByCondition(wrapper, mybatisPage);
        mybatisPage.setRecords(userCOS);
        return mybatisPage;

    }

    public OrganizationUserCO getOrganizationCOByUserId(Long userId) {
        return baseMapper.getOrganizationCOByUserId(userId);
    }

    public List<ContactCO> listByEmployeeCodeConditionCompanyIdAndDepartId(Long companyId, Long departmentId, String employeeCode) {
        return baseMapper.listByEmployeeCodeConditionCompanyIdAndDepartId(companyId, departmentId, employeeCode);
    }

    public List<ContactCO> listUserByTenantId(Long tenantId) {
        return baseMapper.listUserByTenantId(tenantId);
    }

    /**
     * 根据工号集合查询员工信息
     * @param employeeCodes
     * @return
     */
    public List<ContactCO> listUsersByEmployeeCodes(List<String> employeeCodes) {
        Long currentTenantId = OrgInformationUtil.getCurrentTenantId();
        Wrapper<ContactCO> wrapper = new EntityWrapper<ContactCO>().eq("tenant_id", currentTenantId)
                .in("employee_id", employeeCodes);
        List<ContactCO> userCOS = baseMapper.listCOByCondition(wrapper);
        return userCOS;
    }

    public List<ContactCO> listUsersByDepartmentId(Long departmentId){
        List<Long> userIds = departmentUserService.selectList(
                new EntityWrapper<DepartmentUser>()
                        .eq("department_id", departmentId)
        ).stream().map(DepartmentUser::getUserId).collect(Collectors.toList());

        if (userIds.size() == 0) {
            return new ArrayList<>();
        }
        Wrapper<ContactCO> wrapper = new EntityWrapper<ContactCO>().in("user_id", userIds);
        List<ContactCO> userCOS = baseMapper.listCOByCondition(wrapper);
        return userCOS;
    }

    /**
     * @param userOid 申请人Oid
     * @param level   1:本级, 2:第二级
     * @return
     */
    public UUID recursiveSearchDirectManager(UUID userOid, Integer level) {
        //如果入参userOid为null,则直接返回null
        if (userOid == null) {
            return null;
        }
        Contact contact  = getByUserOid(userOid);
        if (level > 1) {

            if (contact != null) {
                return recursiveSearchDirectManager(contact.getDirectManager(), --level);
            }
        } else if (level == 1) {
            if (contact != null) {
                return contact.getDirectManager();
            }
        }
        return null;
    }

    public List<UserSimpleInfoDTO> listUsersByCond(String userCode,
                                                   String userName,
                                                   Long companyId,
                                                   Long unitId,
                                                   List<Long> notExcludeIds,
                                                   Page page) {
        return baseMapper.listUserByNameAndCodeAndCompanyAndUnit(userName, userCode, companyId, unitId, notExcludeIds, OrgInformationUtil.getCurrentTenantId(),page);
    }

}
