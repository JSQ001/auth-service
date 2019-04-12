package com.hand.hcf.app.mdata.contact.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.UserBankAccountCO;
import com.hand.hcf.app.mdata.bank.domain.BankInfo;
import com.hand.hcf.app.mdata.bank.dto.BankAccountDTO;
import com.hand.hcf.app.mdata.bank.service.BankInfoService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.contact.domain.ContactBankAccount;
import com.hand.hcf.app.mdata.contact.dto.ContactAccountDTO;
import com.hand.hcf.app.mdata.contact.dto.ContactBankAccountDTO;
import com.hand.hcf.app.mdata.contact.dto.ContactDTO;
import com.hand.hcf.app.mdata.contact.persistence.ContactBankAccountMapper;
import com.hand.hcf.app.mdata.contact.utils.UserInfoEncryptUtil;
import com.hand.hcf.app.mdata.department.service.DepartmentService;
import com.hand.hcf.app.mdata.department.service.DepartmentUserService;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.legalEntity.service.LegalEntityService;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.service.MessageService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing ContactBankAccount.
 */
@Service
public class ContactBankAccountService extends BaseService<ContactBankAccountMapper, ContactBankAccount> {

    private static NumberFormat formatter = new DecimalFormat("#0");
    private final Logger log = LoggerFactory.getLogger(ContactBankAccountService.class);

    @Autowired
    ApplicationEventPublisher userEventPublisher;
    @Autowired
    BankInfoService bankInfoService;
    @Autowired
    CompanyService companyService;
    @Autowired
    LegalEntityService legalEntityService;
    @Autowired
    private PhoneService phoneService;
    @Autowired
    private ContactService contactService;


    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentUserService departmentUserService;

    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;

    @Autowired
    private MessageService messageService;


    @Transactional
    public ContactBankAccount createContactBankAccount(ContactBankAccountDTO contactBankAccountDTO, Long currentUserId) {
        Long tenantId = -1L;
        if (null == contactBankAccountDTO.getEnabled()) {
            contactBankAccountDTO.setEnabled(Boolean.FALSE);
        }
        if (contactBankAccountDTO.getUserOid() == null) {
            throw new BizException(RespCode.USER_OID_NOT_NULL);
        }
        if (StringUtils.isEmpty(contactBankAccountDTO.getBankAccountNo())) {
            throw new BizException(RespCode.BANK_ACC_NO_NOT_NULL);
        }
        if (StringUtils.isEmpty(contactBankAccountDTO.getBankAccountName())) {
            throw new BizException(RespCode.BANK_ACC_NAME_NOT_NULL);
        }
        if (StringUtils.isEmpty(contactBankAccountDTO.getBranchName())) {
            throw new BizException(RespCode.BANK_NAME_NOT_NULL);
        }
        //根据租户id获取所有useroid
//        List<User> users = userService.listByQO(UserQO.builder().tenantId(OrgInformationUtil.getCurrentTenantId()).build());
//        users.stream().forEach(userOid -> {
//            //在遍历useroid
//            Optional<ContactBankAccount> contactBankAccount = findByUserOidAndBankAccountNo(
//                    userOid.getUserOid(), contactBankAccountDTO.getBankAccountNo());
//            if (contactBankAccount.isPresent()) {
//                throw new BizException(RespCode.SYS_BANK_ACCOUNT_IS_USED);
//            }
//        });
        if(!checkBankCardNoExists(contactBankAccountDTO.getBankAccountNo())){
            throw new BizException(RespCode.SYS_BANK_ACCOUNT_IS_USED);
        }

        if (null == contactBankAccountDTO.getEnabled()) {
            contactBankAccountDTO.setEnabled(false);
        }
        if (contactBankAccountDTO.getPrimary() == null) {
            contactBankAccountDTO.setPrimary(Boolean.FALSE);
        }
        // 判断是否禁用默认
        if (!contactBankAccountDTO.getEnabled() && contactBankAccountDTO.getPrimary()) {
            throw new BizException(RespCode.BANK_ACC_NOT_DISABLE_DEFAULT);
        }
        ContactBankAccount contactBankAccount = ContactBankAccountDTOToCContactBankAccount(contactBankAccountDTO);
        if (contactBankAccountDTO.getTenantId() == null) {
            tenantId = contactService.getUserDTOByUserOid(contactBankAccountDTO.getUserOid()).getTenantId();
        } else {
            tenantId = contactBankAccountDTO.getTenantId();
        }
        BankInfo bankInfo = null;
        if (!StringUtils.isEmpty(contactBankAccount.getBankCode())) {
            // 判断系统银行是否存在此银行编码、如果存在则用系统银行、否则查询自定义银行是否存在此银行编码
            bankInfo = bankInfoService.findOneByTenantIdAndBankCode(0L, contactBankAccount.getBankCode());
            if (bankInfo == null) {
                bankInfo = bankInfoService.findOneByTenantIdAndBankCode(tenantId, contactBankAccount.getBankCode());
            }
        } else if (!StringUtils.isEmpty(contactBankAccount.getBranchName())) {
            // 判断系统银行是否存在此分行名称、如果存在则用系统银行、否则查询自定义银行是否存在此分行名称
            bankInfo = bankInfoService.findOneByTenantIdAndBankBranchName(0L, contactBankAccount.getBranchName());
            if (bankInfo == null) {
                bankInfo = bankInfoService.findOneByTenantIdAndBankBranchName(tenantId, contactBankAccount.getBranchName());
            }
        }
        if (bankInfo == null) {
            throw new BizException(RespCode.BANK_NOT_EXIST);
        } else {
            contactBankAccount.setBankName(bankInfo.getBankName());
            contactBankAccount.setBankCode(bankInfo.getBankCode());
            contactBankAccount.setBranchName(bankInfo.getBankBranchName());
        }
        contactBankAccount.setContactBankAccountOid(UUID.randomUUID());

        if (contactBankAccountDTO.getPrimary()) {
            ContactBankAccount result = findOneByUserOidAndIsPrimary(contactBankAccount.getUserOid(), true);
            if (result != null) {
                result.setPrimary(false);
                insertOrUpdate(result);
            }
        } else {
            // 查询是否有启用的银行信息
            List<ContactBankAccount> contactBankAccounts = findByUserOid(contactBankAccount.getUserOid());
            if (CollectionUtils.isEmpty(contactBankAccounts)) {
                contactBankAccount.setPrimary(true);
            }
        }
//        User user = userService.getByUserOid(contactBankAccountDTO.getUserOid());
//
//        userService.saveUser(user);
        insertOrUpdate(contactBankAccount);
//        String message = new StringBuffer().append(messageTranslationService.getMessageDetailByCode(OrgInformationUtil.getCurrentLanguage(), DataOperationMessageKey.ADD_USER_BANK_ACCOUNT, contactBankAccount.getBankAccountName(), UserInfoEncryptUtil.displayBankNo(contactBankAccount.getBankAccountNo(), true))).toString();
//
//        dataOperationService.save(OrgInformationUtil.getCurrentUserId(), contactBankAccount, message, OperationEntityTypeEnum.USER.getKey(), OperationTypeEnum.ADD.getKey(), tenantId);
        return contactBankAccount;
    }

    public Optional<ContactBankAccount> findByUserOidAndBankAccountNo(UUID userOid, String bankAccountNo) {
        return Optional.ofNullable(selectOne(new EntityWrapper<ContactBankAccount>()
                .eq("user_oid", userOid)
                .eq("bank_account_no", bankAccountNo)));
    }

    public Integer countByBankAccountNo(String bankAccountNo) {
        return selectCount(new EntityWrapper<ContactBankAccount>()
                .eq("bank_account_no", bankAccountNo));
    }

    public ContactBankAccount findOneByBankAccountOid(UUID bankAccountOid) {
        return selectOne(new EntityWrapper<ContactBankAccount>()
                .eq("contact_bank_account_oid", bankAccountOid));
    }

    public ContactBankAccount findOneByUserOidAndIsPrimary(UUID userOid, Boolean isPrimary) {
        return selectOne(new EntityWrapper<ContactBankAccount>()
                .eq("user_oid", userOid)
                .eq("primary", isPrimary));
    }

    public List<ContactBankAccount> findByUserOid(UUID userOid) {
        return selectList(new EntityWrapper<ContactBankAccount>()
                .eq("user_oid", userOid)
                .orderBy("enabled,primary,last_updated_date", false));
    }

    public List<ContactBankAccount> findByUserOids(List<UUID> userOids, Boolean isPrimary, Boolean enabled) {
        return selectList(new EntityWrapper<ContactBankAccount>()
                .in("user_oid", userOids)
                .eq(isPrimary!=null,"primary", isPrimary)
                .eq(enabled!=null,"enabled", enabled));
    }

    public Page<ContactBankAccount> findByUserOid(UUID userOid, Page page) {
        return selectPage(page, new EntityWrapper<ContactBankAccount>()
                .eq("user_oid", userOid)
                .orderBy("primary", false)
                .orderBy("enabled",false)
                .orderBy("last_updated_date",false));

    }

    public Page<ContactBankAccount> findByUserOidAndEnable(UUID userOid, Boolean enabled, Page page) {
        return selectPage(page, new EntityWrapper<ContactBankAccount>()
                .eq("user_oid", userOid)
                .eq("enabled", enabled)
                .orderBy("primary", false)
                .orderBy("last_updated_date",false));
    }

    @Transactional
    public ContactBankAccount updateContactBankAccount(ContactBankAccountDTO contactBankAccountDTO, Long currentUserId, boolean isExcelImport) {
        ContactBankAccount contactBankAccount = findOneByBankAccountOid(contactBankAccountDTO.getContactBankAccountOid());
        if (contactBankAccount == null) {
            throw new ObjectNotFoundException(ContactBankAccount.class, contactBankAccountDTO.getContactBankAccountOid());
        }
        if (null == contactBankAccountDTO.getEnabled()) {
            contactBankAccountDTO.setEnabled(false);
        }
        if (contactBankAccountDTO.getPrimary() == null) {
            contactBankAccountDTO.setPrimary(Boolean.FALSE);
        }
        ContactBankAccount oldContactBankAccount = new ContactBankAccount();
        BeanUtils.copyProperties(contactBankAccount, oldContactBankAccount);
        // 判断是否已存在此银行卡号信息
        if (!contactBankAccountDTO.getBankAccountNo().equals(contactBankAccount.getBankAccountNo())) {
            Optional<ContactBankAccount> old = findByUserOidAndBankAccountNo(contactBankAccountDTO.getUserOid(), contactBankAccountDTO.getBankAccountNo());
            if (old.isPresent()) {
                throw new BizException(RespCode.SYS_BANK_ACCOUNT_IS_USED);
            }
        }
//        User user = userService.getByUserOid(contactBankAccountDTO.getUserOid());
        BankInfo bankInfo = null;
        if (!StringUtils.isEmpty(contactBankAccountDTO.getBankCode())) {
            // 判断系统银行是否存在此银行编码、如果存在则用系统银行、否则查询自定义银行是否存在此银行编码
            bankInfo = bankInfoService.findOneByTenantIdAndBankCode(0L, contactBankAccountDTO.getBankCode());
            if (bankInfo == null) {
                bankInfo = bankInfoService.findOneByTenantIdAndBankCode(OrgInformationUtil.getCurrentTenantId(), contactBankAccountDTO.getBankCode());
            }
        } else if (!StringUtils.isEmpty(contactBankAccountDTO.getBranchName())) {
            // 判断系统银行是否存在此分行名称、如果存在则用系统银行、否则查询自定义银行是否存在此分行名称
            bankInfo = bankInfoService.findOneByTenantIdAndBankBranchName(0L, contactBankAccountDTO.getBranchName());
            if (bankInfo == null) {
                bankInfo = bankInfoService.findOneByTenantIdAndBankBranchName(OrgInformationUtil.getCurrentTenantId(), contactBankAccountDTO.getBranchName());
            }
        }
        if (bankInfo == null) {
            throw new BizException(RespCode.BANK_NOT_EXIST);
        } else {
            contactBankAccount.setBankName(bankInfo.getBankName());
            contactBankAccount.setBankCode(bankInfo.getBankCode());
            contactBankAccount.setBranchName(bankInfo.getBankBranchName());
        }
        // 判断是否禁用默认
        if (!contactBankAccountDTO.getEnabled() && contactBankAccountDTO.getPrimary()) {
            throw new BizException(RespCode.BANK_ACC_NOT_DISABLE_DEFAULT);
        }
        // 导入
        if (isExcelImport) {
            // 由于导入的时候没有标识是否默认，所以默认为dto里的isprimary默认为false，所以这里需要判断如果修改的银行之前为true则还设置为true
            if (contactBankAccount.getPrimary()) {
                contactBankAccountDTO.setPrimary(true);
            }
        } else {
            // 页面修改
            // 判断是否默认，如果非默认则判断此用户是否有默认的银行卡信息，如果没有则提示必须要有一张默认银行卡
            if (!contactBankAccountDTO.getPrimary()) {
                // 查询是否有默认银行卡
                ContactBankAccount defaultBankAccount = findOneByUserOidAndIsPrimary(contactBankAccountDTO.getUserOid(), true);
                // 如果默认银行是当前修改银行
                if (defaultBankAccount == null || defaultBankAccount.getContactBankAccountOid().equals(contactBankAccountDTO.getContactBankAccountOid())) {
                    throw new BizException(RespCode.BANK_ACC_MUST_DEFAULT_CARD);
                }
            }
        }

//        userService.saveUser(user);
        contactBankAccount.setUserOid(contactBankAccountDTO.getUserOid());
        contactBankAccount.setBankAccountNo(contactBankAccountDTO.getBankAccountNo());
        contactBankAccount.setBankAccountName(contactBankAccountDTO.getBankAccountName());
        contactBankAccount.setAccountLocation(contactBankAccountDTO.getAccountLocation());
        // 当设为默认的时，则把之前设置默认的取消掉
        if (contactBankAccountDTO.getPrimary()) {
            ContactBankAccount result = findOneByUserOidAndIsPrimary(contactBankAccount.getUserOid(), true);
            // 判断查询出来的是否为当前修改的默认银行
            if (result != null && !result.getContactBankAccountOid().equals(contactBankAccountDTO.getContactBankAccountOid())) {
                result.setPrimary(false);
                insertOrUpdate(result);
            }
        }
        contactBankAccount.setPrimary(contactBankAccountDTO.getPrimary());
        contactBankAccount.setEnabled(contactBankAccountDTO.getEnabled());
        insertOrUpdate(contactBankAccount);
        oldContactBankAccount.setBankAccountNo(UserInfoEncryptUtil.displayCardNo(oldContactBankAccount.getBankAccountNo(), true));
        ContactBankAccount contactBankAccountNew = new ContactBankAccount();
        BeanUtils.copyProperties(contactBankAccount, contactBankAccountNew);
        contactBankAccountNew.setBankAccountNo(UserInfoEncryptUtil.displayCardNo(contactBankAccountNew.getBankAccountNo(), true));
//        dataOperationService.save(OrgInformationUtil.getCurrentUserId(), oldContactBankAccount, contactBankAccountNew, OperationEntityTypeEnum.USER.getKey(), OperationTypeEnum.UPDATE.getKey(), user.getTenantId(), contactBankAccount.getBankAccountName());
        return contactBankAccount;
    }

    public List<ContactBankAccountDTO> getALlContactBankAccount(Page page) {
        Page<ContactBankAccount> contactBankAccountList = selectPage(page);
        return contactBankAccountList.getRecords().stream()
                .map(c -> ContactBankAccountToContactBankAccountDTO(c)).collect(Collectors.toList());
    }

    public List<ContactBankAccountDTO> getContactBankAccountByUserOid(UUID userOid, Boolean enable, Page page) {
        Page<ContactBankAccount> contactBankAccountList;
        if (enable == null) {
            contactBankAccountList = findByUserOid(userOid, page);
        } else {
            contactBankAccountList = findByUserOidAndEnable(userOid, enable, page);
        }
        return contactBankAccountList.getRecords().stream()
                .map(c -> ContactBankAccountToContactBankAccountDTO(c)).collect(Collectors.toList());
    }

    public List<ContactBankAccountDTO> getContactBankAccountByUserOidNoPage(UUID userOid) {
        List<ContactBankAccount> contactBankAccountList = findByUserOid(userOid);
        return (contactBankAccountList.stream()
                .map(c -> ContactBankAccountToContactBankAccountDTO(c)).collect(Collectors.toList()));
    }

    public List<ContactBankAccountDTO> getEnableContactBankAccountByUserOid(UUID userOid, Page page) {
        Page<ContactBankAccount> contactBankAccountList = findByUserOidAndEnable(userOid, true, page);
        return contactBankAccountList.getRecords().stream()
                .map(c -> ContactBankAccountToContactBankAccountDTO(c)).collect(Collectors.toList());
    }

    public List<ContactBankAccountDTO> getDisableContactBankAccountByUserOid(UUID userOid, Page page) {
        Page<ContactBankAccount> contactBankAccountList = findByUserOidAndEnable(userOid, false, page);
        return contactBankAccountList.getRecords().stream()
                .map(c -> ContactBankAccountToContactBankAccountDTO(c)).collect(Collectors.toList());
    }

    public ContactBankAccountDTO findOneByContactBankAccountOid(UUID contactBankAccountOid) {
        log.debug("Request to get ContactBankAccount: {}", contactBankAccountOid);
        ContactBankAccount contactBankAccount = findByContactBankAccountOid(contactBankAccountOid);
        if (contactBankAccount == null) {
            throw new ObjectNotFoundException(ContactBankAccount.class, contactBankAccountOid);
        }
        return ContactBankAccountToContactBankAccountDTO(contactBankAccount);
    }

    public ContactBankAccount findByContactBankAccountOid(UUID contactBankAccountOid) {
        log.debug("Request to get ContactBankAccount: {}", contactBankAccountOid);
        ContactBankAccount contactBankAccount = findByContactBankAccountOid(contactBankAccountOid);
        if (contactBankAccount == null) {
            throw new ObjectNotFoundException(ContactBankAccount.class, contactBankAccountOid);
        }
        return contactBankAccount;
    }

    /**
     * delete the  ContactBankAccount by id.
     */
    public void delete(Long id) {
        log.debug("Request to delete ContactBankAccount : {}", id);
        deleteById(id);
    }

    /**
     * delete the  ContactBankAccount by userOid.
     */
    @Transactional
    public void deleteByUserOid(UUID userOid) {
        delete(new EntityWrapper<ContactBankAccount>()
                .eq("user_oid", userOid));
    }

    @Transactional
    public void deleteByBankAccountOid(UUID bankAccountOid) {
        delete(new EntityWrapper<ContactBankAccount>()
                .eq("contact_bank_account_oid", bankAccountOid));
    }


    public boolean compareString(String s1, String s2) {
        if (StringUtils.isEmpty(s1) && StringUtils.isEmpty(s2)) {
            return true;
        } else if (!StringUtils.isEmpty(s1) && !StringUtils.isEmpty(s2)) {
            return s1.equals(s2);
        } else {
            return false;
        }
    }

    @Transactional
    public List<ContactBankAccount> upsertContactBankAccounts(List<ContactBankAccountDTO> contactBankAccountDTOs, Long currentUserId) {
        List<ContactBankAccount> successContactBankAccounts = new LinkedList<>();
        for (ContactBankAccountDTO contactBankAccountDTO : contactBankAccountDTOs) {
            successContactBankAccounts.add(upsertContactBankAccount(contactBankAccountDTO, currentUserId, false));
        }
        return successContactBankAccounts;
    }

    @Transactional
    public ContactBankAccount upsertContactBankAccount(ContactBankAccountDTO contactBankAccountDTO, Long currentUserId, boolean isExcelImport) {
        Optional<ContactBankAccount> contactBankAccountOptional = findByUserOidAndBankAccountNo(contactBankAccountDTO.getUserOid(), contactBankAccountDTO.getBankAccountNo());
        if (contactBankAccountOptional.isPresent()) {
            contactBankAccountDTO.setContactBankAccountOid(contactBankAccountOptional.get().getContactBankAccountOid());
            return updateContactBankAccount(contactBankAccountDTO, currentUserId, isExcelImport);
        } else {
            return createContactBankAccount(contactBankAccountDTO, currentUserId);
        }
    }


    /**
     * 批量获取用户的默认银行帐号信息
     *
     * @param userOids
     * @return
     */
    public Map<UUID, ContactBankAccount> getUsersDefaultBankAccountMaps(List<UUID> userOids) {
        List<ContactBankAccount> contactBankAccounts = findByUserOids(userOids,true,true);
        return contactBankAccounts.stream().collect(Collectors.toMap(ContactBankAccount::getUserOid, c -> c));
    }



    /**
     * 获取用户默认银行信息
     *
     * @param userOid
     * @return
     */
    public ContactBankAccountDTO getUserDefaultBank(UUID userOid) {
        ContactBankAccount contactBankAccount = findOneByUserOidAndIsPrimary(userOid, true);
        if (null == contactBankAccount) {
            return null;
        }
        return ContactBankAccountToContactBankAccountDTO(contactBankAccount);
    }

    public ContactBankAccountDTO ContactBankAccountToContactBankAccountDTO(ContactBankAccount contactBankAccount) {
        ContactBankAccountDTO result = new ContactBankAccountDTO();
        result.setContactBankAccountOid(contactBankAccount.getContactBankAccountOid());
        result.setUserOid(contactBankAccount.getUserOid());
        result.setBankAccountNo(UserInfoEncryptUtil.detrypt(contactBankAccount.getBankAccountNo()));
        result.setOriginalBankAccountNo(UserInfoEncryptUtil.displayBankNo(contactBankAccount.getBankAccountNo(), true));
        result.setBankAccountName(contactBankAccount.getBankAccountName());
        result.setBankName(contactBankAccount.getBankName());
        result.setBranchName(contactBankAccount.getBranchName());
        result.setPrimary(contactBankAccount.getPrimary());
        result.setPrimaryStr(contactBankAccount.getPrimary() ? messageService.getMessageDetailByCode(RespCode.SYS_YES) : messageService.getMessageDetailByCode(RespCode.SYS_NO));
        result.setEnabled(contactBankAccount.getEnabled());
        result.setEnabledStr(contactBankAccount.getEnabled() ? messageService.getMessageDetailByCode(RespCode.SYS_ENABLED) : messageService.getMessageDetailByCode(RespCode.SYS_DISABLED));
        result.setAccountLocation(contactBankAccount.getAccountLocation());
        result.setBankCode(contactBankAccount.getBankCode());
        result.setEmployeeId(contactService.selectEmployeeIdByUserOid(contactBankAccount.getUserOid()));
        return result;
    }

    public ContactBankAccount ContactBankAccountDTOToCContactBankAccount(ContactBankAccountDTO dto) {
        ContactBankAccount contactBankAccount = new ContactBankAccount();
        contactBankAccount.setAccountLocation(dto.getAccountLocation());
        contactBankAccount.setContactBankAccountOid(dto.getContactBankAccountOid());
        contactBankAccount.setUserOid(dto.getUserOid());
        contactBankAccount.setBankAccountNo(dto.getBankAccountNo());
        contactBankAccount.setBankName(dto.getBankName());
        contactBankAccount.setBankAccountName(dto.getBankAccountName());
        contactBankAccount.setBranchName(dto.getBranchName());
        contactBankAccount.setPrimary(dto.getPrimary() == null ? false : dto.getPrimary());
        contactBankAccount.setEnabled(dto.getEnabled());
        contactBankAccount.setBankCode(dto.getBankCode());
        return contactBankAccount;
    }

    public UserBankAccountCO getUserBankAccountByUserIdAndAccountNumber(Long userId, String number) {
        String encrypt = UserInfoEncryptUtil.encrypt(number);
        UUID userOid = contactService.getUserDTOByUserId(userId).getUserOid();
        List<ContactBankAccount> contactBankAccounts = this.selectList(new EntityWrapper<ContactBankAccount>().eq("user_oid", userOid).eq("bank_account_no", encrypt));
        if (CollectionUtils.isNotEmpty(contactBankAccounts)){
            ContactBankAccount contactBankAccount = contactBankAccounts.get(0);
            UserBankAccountCO result = toUserBankAccountCO(userId,contactBankAccount);
            return result;
        }
        return null;
    }

    public UserBankAccountCO toUserBankAccountCO(Long userId, ContactBankAccount contactBankAccount){
        UserBankAccountCO result = new UserBankAccountCO();
        result.setAccountLocation(contactBankAccount.getAccountLocation());
        result.setBankAccountName(contactBankAccount.getBankAccountName());
        result.setBankAccountNo(UserInfoEncryptUtil.detrypt(contactBankAccount.getBankAccountNo()));
        result.setBankCode(contactBankAccount.getBankCode());
        result.setBankName(contactBankAccount.getBankName());
        result.setBankAccountName(contactBankAccount.getBankAccountName());
        result.setUserId(userId);
        result.setBranchName(contactBankAccount.getBranchName());
        result.setContactBankAccountOid(contactBankAccount.getContactBankAccountOid());
        result.setUserOid(contactBankAccount.getUserOid());
        return result;
    }

    public List<ContactBankAccountDTO> exportContactBankAccountDTO(Page page, List<UUID> userOids){
        List<ContactBankAccount> contactBankAccounts = selectList(new EntityWrapper<ContactBankAccount>().in("user_oid",userOids));
        List<ContactBankAccountDTO> result = contactBankAccounts.stream().map(item -> ContactBankAccountToContactBankAccountDTO(item)).collect(Collectors.toList());
        return result;
    }

    public Boolean checkBankCardNoExists(String bankAccountNo){
        if(baseMapper.checkBankCardNoExists(OrgInformationUtil.getCurrentTenantId(),bankAccountNo) > 0){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 获取员工银行信息
     * @param name
     * @param code
     * @param queryPage
     * @return
     */
    public List<ContactAccountDTO> getReceivablesByNameAndCode(String name, String code, Page queryPage) {
        List<ContactAccountDTO> list = new ArrayList<>();
        List<ContactDTO> contactDTOS = contactService.listUserByNameAndCode(name,code,OrgInformationUtil.getCurrentTenantId(),queryPage);
        contactDTOS.stream().forEach(contactDTO -> {
            ContactAccountDTO contactAccountDTO = new ContactAccountDTO();
            contactAccountDTO.setId(contactDTO.getUserId());
            contactAccountDTO.setCode(contactDTO.getEmployeeId());
            contactAccountDTO.setName(contactDTO.getFullName());
            contactAccountDTO.setDepartment(contactDTO.getDepartmentName());
            contactAccountDTO.setJob(contactDTO.getTitle());
            contactAccountDTO.setIsEmp(true);
            contactAccountDTO.setSign(contactDTO.getUserId()+"_"+contactAccountDTO.getIsEmp());
            List<ContactBankAccountDTO> contactBankAccountDTOs = this.getContactBankAccountByUserOidNoPage(contactDTO.getUserOid());
            List<BankAccountDTO> bankInfos = contactBankAccountDTOs.stream().map(contactBankAccountDTO -> {
                BankAccountDTO bankAccount = new BankAccountDTO();
                bankAccount.setAccount(contactBankAccountDTO.getBankAccountNo());
                bankAccount.setBankAccountName(contactBankAccountDTO.getBankAccountName());
                bankAccount.setBankCode(contactBankAccountDTO.getBankCode());
                bankAccount.setBankName(contactBankAccountDTO.getBankName());
                bankAccount.setPrimary(contactBankAccountDTO.getPrimary());
                return bankAccount;
            }).collect(Collectors.toList());
            contactAccountDTO.setBankInfos(bankInfos);
            list.add(contactAccountDTO);
        });
        return list;
    }
}
