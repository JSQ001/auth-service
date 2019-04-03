package com.hand.hcf.app.mdata.contact.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.contact.domain.Contact;
import com.hand.hcf.app.mdata.contact.domain.ContactCard;
import com.hand.hcf.app.mdata.contact.dto.ContactCardDTO;
import com.hand.hcf.app.mdata.contact.dto.ContactCardImportDTO;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.contact.enums.CardType;
import com.hand.hcf.app.mdata.contact.persistence.ContactCardMapper;
import com.hand.hcf.app.mdata.contact.utils.UserInfoEncryptUtil;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.system.domain.BatchTransactionLog;
import com.hand.hcf.app.mdata.system.enums.SystemCustomEnumerationTypeEnum;
import com.hand.hcf.app.mdata.utils.ImportValidateUtil;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.core.exception.core.ValidationError;
import com.hand.hcf.core.exception.core.ValidationException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.service.MessageService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/*import com.hand.hcf.app.client.org.SysCodeValueCO;*/

@Service
public class ContactCardService extends BaseService<ContactCardMapper,ContactCard> {

    @Autowired
    private MessageService messageService;
    @Autowired
    private ContactService contactService;

    @Autowired
    HcfOrganizationInterface hcfOrganizationInterface;




    public List<ContactCard> getUserContactCards(UUID userOid, Boolean enabled,Boolean primary) {
       return  selectList(new EntityWrapper<ContactCard>()
        .eq("user_oid",userOid)
        .eq(enabled!=null,"enabled",enabled)
        .eq(primary!=null,"primary",primary)
        .orderBy("primary",false)
        .orderBy("enabled",false)
        .orderBy("last_updated_date",false));

    }


    public ContactCard getUserContactCard(UUID userOid, Boolean enabled,Boolean primary) {
        return  selectOne(new EntityWrapper<ContactCard>()
                .eq("user_oid",userOid)
                .eq(enabled!=null,"enabled",enabled)
                .eq(primary!=null,"primary",primary)
                );

    }

    public List<ContactCard> listContactCardsByUserOid(UUID userOid) {
        return selectList(new EntityWrapper<ContactCard>().eq("user_oid",userOid));
    }

    public List<ContactCard> getUserEnableContactCards(UUID userOid) {
        return getUserContactCards(userOid,true,null);
    }

    public List<ContactCard> findByEnableTrueAndUserOidIn(List<UUID> userOids){
        return  findByUserOidIn(userOids,true,null);
    }

    public List<ContactCard> findByUserOidIn(List<UUID> userOids, Boolean enabled, Boolean primary){
        return  selectList(new EntityWrapper<ContactCard>()
                .in("user_oid",userOids)
                .eq(primary!=null,"primary",primary)
                .eq(enabled!=null,"enabled",enabled));
    }

    public Optional<ContactCard> findByUserOidAndCardType(UUID userOid,Integer cardType)
    {
        return Optional.ofNullable(selectOne(new EntityWrapper<ContactCard>()
                .eq("user_oid",userOid)
                .eq("card_type",cardType)
        ));
    }

    public Optional<ContactCard> findOneByOid(UUID contactCardOid)
    {
        return Optional.ofNullable(selectOne(new EntityWrapper<ContactCard>()
                .eq("contact_card_oid",contactCardOid)
        ));
    }

    public Optional<ContactCard> findByUserOidAndCardNoAndCardType(UUID userOid,String cardNo,Integer cardType)
    {
        return Optional.ofNullable(selectOne(new EntityWrapper<ContactCard>()
                .eq("user_oid",userOid)
                .eq("card_no",cardNo)
                .eq("card_type",cardType)
        ));
    }
    

    public List<ContactCard> getUserDisableContactCards(UUID userOid) {
        return getUserContactCards(userOid,false,null);
    }

    public Boolean checkCardNoExist(String cardNo,Integer cardType){
        if(baseMapper.checkCardNoExist(OrgInformationUtil.getCurrentTenantId(),cardNo,cardType) > 0){
            return false;
        }else {
            return true;
        }
    }

    @Transactional
    public ContactCard upsertContactCard(ContactCardDTO contactCardDTO, Long currentUserId, boolean isExcelImport) {
        ContactCard contactCard;
        ContactCard oldContactCard = null;
//        User user = hcfOrganizationInterface.getByUserOid(contactCardDTO.getUserOid());

        if(StringUtils.isEmpty(contactCardDTO.getCardType())){
            throw new BizException(RespCode.CARD_TYPE_NOT_NULL);
        }
        if(StringUtils.isEmpty(contactCardDTO.getLastName())){
            throw new BizException(RespCode.CARD_LAST_NAME_NOT_NULL);
        }
        if(!isChineseName(contactCardDTO.getLastName()) && StringUtils.isEmpty(contactCardDTO.getFirstName())){
            throw new BizException(RespCode.CARD_FIRST_NAME_NOT_NULL);
        }
        if (!contactCardDTO.getEnabled() && contactCardDTO.getPrimary()) {
            throw new BizException(RespCode.CARD_DEFAULT_NOT_DISABLE);
        }
        Optional<ContactCard> existContactCard = Optional.empty();
        if (contactCardDTO.getContactCardOid() == null) {
            //create
            contactCard = new ContactCard();
            contactCard.setContactCardOid(UUID.randomUUID());
            existContactCard = findByUserOidAndCardType(contactCardDTO.getUserOid(), contactCardDTO.getCardType());
            if (existContactCard.isPresent()) {
                throw new BizException(RespCode.CARD_EXIST);
            }
            //判断要添加的证件类型 证件号是否已重复
            if(!StringUtils.isEmpty(contactCardDTO.getCardNo())){
                Integer count=baseMapper.checkCardNoExist(OrgInformationUtil.getCurrentTenantId(),contactCardDTO.getCardNo(),contactCardDTO.getCardType());
                if (count>0){
                    throw new BizException(RespCode.CARD_NUM_NOT_REPEAT);
                }

            }
            // 判断是否默认
            if (contactCardDTO.getPrimary()) {
                Optional<ContactCard> defaultContactCard = Optional.ofNullable(getUserContactCard(contactCardDTO.getUserOid(),true,true));
                if (defaultContactCard.isPresent()) {
                    defaultContactCard.get().setPrimary(false);
                    insertOrUpdate(defaultContactCard.get());
                }
            } else {
                // 查询是否有启用的证件信息
                List<ContactCard> contactCardList = getUserContactCards(contactCardDTO.getUserOid(),true,null);
                if(CollectionUtils.isEmpty(contactCardList)){
                    throw new BizException(RespCode.CARD_MUST_HAVE_ENABLED_DEFAULT);
                }
            }
        } else {
            Optional<ContactCard> contactCardOptional =findOneByOid(contactCardDTO.getContactCardOid());
            if (!contactCardOptional.isPresent()) {
                throw new ObjectNotFoundException(ContactCard.class, contactCardDTO.getContactCardOid());
            }
            contactCard = contactCardOptional.get();
            oldContactCard = new ContactCard();
            BeanUtils.copyProperties(contactCard,oldContactCard);
            // 如果修改了证件类型则需要重新校验是否存在同样证件类型
            if(!contactCard.getCardType().equals(contactCardDTO.getCardType())){
                existContactCard = findByUserOidAndCardType(contactCardDTO.getUserOid(), contactCardDTO.getCardType());
                if (existContactCard.isPresent()) {
                    throw new BizException(RespCode.CARD_EXIST);
                }
                //判断要修改的证件类型 证件号是否已重复
                if(!StringUtils.isEmpty(contactCardDTO.getCardNo())){
                    Integer count=baseMapper.checkCardNoExist(OrgInformationUtil.getCurrentTenantId(),contactCardDTO.getCardNo(),contactCardDTO.getCardType());
                    if (count>0){
                        throw new BizException(RespCode.CARD_NUM_NOT_REPEAT);
                    }
                }
            }
            // 导入
            if(isExcelImport){
                if(contactCard.getPrimary()){
                    contactCardDTO.setPrimary(true);
                }
            }else{
                // 页面修改
                if(!contactCardDTO.getPrimary()){
                    // 判断此用户是否存在默认的证件信息
                    Optional<ContactCard> defaultCard =Optional.ofNullable( getUserContactCard(contactCardDTO.getUserOid(),true,true));
                    if(!defaultCard.isPresent() || defaultCard.get().getContactCardOid().equals(contactCard.getContactCardOid())){
                        throw new BizException(RespCode.CARD_MUST_HAVE_DEFAULT);
                    }
                }
            }
            if(contactCardDTO.getPrimary()){
                // 根据用户Oid查询默认的证件
                Optional<ContactCard> result = Optional.ofNullable( getUserContactCard(contactCardDTO.getUserOid(),true,true));
                // 默认证件不是当前修改的证件则修改为不默认
                if(result.isPresent() && !result.get().getContactCardOid().equals(contactCard.getContactCardOid())){
                    ContactCard oldDefault = result.get();
                    oldDefault.setPrimary(false);
                    insertOrUpdate(oldDefault);
                }
            }
        }

        contactCard.setPrimary(contactCardDTO.getPrimary());
        contactCard.setUserOid(contactCardDTO.getUserOid());
        contactCard.setEnabled(contactCardDTO.getEnabled());
        contactCard.setCardType(contactCardDTO.getCardType());
        contactCard.setCardNo(contactCardDTO.getCardNo());
        contactCard.setFirstName(contactCardDTO.getFirstName());
        contactCard.setLastName(contactCardDTO.getLastName());
        contactCard.setNationalityCode(contactCardDTO.getNationalityCode());
//        contactCard.setNationality(contactCardDTO.getNationality());
        // 判断国籍是否为空
        if(!StringUtils.isEmpty(contactCardDTO.getNationalityCode())){
            // 根据用户租户和国籍值列表类型查询值列表
            SysCodeValueCO sysCodeValue = hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.NATIONALITY.getId().toString(), contactCardDTO.getNationalityCode());
            // 根据值列表id和value查询值列表项
            if(sysCodeValue == null){
                throw new BizException(RespCode.CARD_NATIONAL_NOT_EXIST);
            }

        }
        contactCard.setCardExpiredTime(contactCardDTO.getCardExpiredTime());
        insertOrUpdate(contactCard);
//        String message = new StringBuffer(messageTranslationService.getMessageDetailByCode(OrgInformationUtil.getCurrentLanguage(), DataOperationMessageKey.ADD_USER_CONTACT_CARD, (StringUtils.isEmpty(contactCard.getCardNo()) ? "" : UserInfoEncryptUtil.displayCardNo(contactCard.getCardNo(), true)))).toString();
//        if(isInsert){
//            dataOperationService.save(currentUserId,contactCard,message, OperationEntityTypeEnum.USER.getKey(), OperationTypeEnum.ADD.getKey(),user.getTenantId());
//        }else{
//            oldContactCard.setCardNo(UserInfoEncryptUtil.displayCardNo(oldContactCard.getCardNo(),true));
//            ContactCard contactCardNew = new ContactCard();
//            BeanUtils.copyProperties(contactCard,contactCardNew);
//            contactCardNew.setCardNo(UserInfoEncryptUtil.displayCardNo(contactCardNew.getCardNo(),true));
//            dataOperationService.save(currentUserId,oldContactCard,contactCardNew,OperationEntityTypeEnum.USER.getKey(),OperationTypeEnum.UPDATE.getKey(),user.getTenantId(),contactCardNew.getCardNo());
//        }
        return contactCard;
    }

    @Transactional
    public List<ContactCard> upsertContactCards(List<ContactCardDTO> contactCardDTOs, Long currentUserId) {
        List<ContactCard> successContactCards = new LinkedList<>();
        for (ContactCardDTO contactCardDTO : contactCardDTOs) {
            successContactCards.add(upsertContactCard(contactCardDTO, currentUserId,false));
        }
        return successContactCards;
    }

    @Transactional
    public ContactCard upsertContactCardByImport(ContactCardImportDTO contactCardImportDTO, Company currentCompany, Long currentUserId) {
        if (contactCardImportDTO.getUserOid() == null) {
            if (StringUtils.isEmpty(contactCardImportDTO.getEmployeeId())) {
                throw new ValidationException(new ValidationError("contactCard", "employeeId can't be null"));
            }
            Optional<Contact> userOptional = contactService.getByCompanyOidAndEmployeeId(currentCompany.getCompanyOid(), contactCardImportDTO.getEmployeeId());
            if (userOptional.isPresent()) {
                contactCardImportDTO.setUserOid(userOptional.get().getUserOid());
            } else {
                throw new ValidationException(new ValidationError("user", "can't get user with employeeId"));
            }
        }
        ContactCardDTO contactCardDTO = contactCardImportDTOToDTO(contactCardImportDTO);
        Optional<ContactCard> contactCardOptional = findByUserOidAndCardType(contactCardDTO.getUserOid(), contactCardDTO.getCardType());
        if (contactCardOptional.isPresent()) {
            contactCardDTO.setContactCardOid(contactCardOptional.get().getContactCardOid());
        }
        return upsertContactCard(contactCardDTO, currentUserId,true);
    }

    @Transactional
    public List<ContactCard> paseImportData(Company currentCompany, List<ContactCardImportDTO> contactCtripImportDTOs, BatchTransactionLog transactionLog) {
        JSONObject error = transactionLog.getErrors();
        List<ContactCard> successContactCards = new ArrayList<>();
        for (ContactCardImportDTO contactCardImportDTO : contactCtripImportDTOs) {

            try {

                if(org.apache.commons.lang.StringUtils.isEmpty(contactCardImportDTO.getCardNo()) || !StringUtils.isEmpty(contactCardImportDTO.getCardNo())&&!contactCardImportDTO.getCardNo().contains("*")){
                    successContactCards.add(upsertContactCardByImport(contactCardImportDTO, currentCompany, transactionLog.getCreatedBy()));
                }
                transactionLog.setSuccessEntities(transactionLog.getSuccessEntities() + 1);
            } catch (ValidationException e) {
                transactionLog.setFailureEntities(transactionLog.getFailureEntities() + 1);
                if (e.getValidationErrors().size() == 0) {
                    ImportValidateUtil.addErrorToJSON(error, messageService.getMessageDetailByCode(RespCode.UNKNOWN_ERROR), contactCardImportDTO.getRowNum());
                    contactCardImportDTO.setErrorDetail(messageService.getMessageDetailByCode(RespCode.UNKNOWN_ERROR));
                } else if (e.getValidationErrors().get(0).getMessage().equals("cardType error")) {
                    ImportValidateUtil.addErrorToJSON(error, messageService.getMessageDetailByCode(RespCode.CARD_TYPE_INVALID), contactCardImportDTO.getRowNum());
                    contactCardImportDTO.setErrorDetail(messageService.getMessageDetailByCode(RespCode.CARD_TYPE_INVALID));
                } else if (e.getValidationErrors().get(0).getMessage().equals("card expired time pattern error")) {
                    ImportValidateUtil.addErrorToJSON(error, messageService.getMessageDetailByCode("6047019"), contactCardImportDTO.getRowNum());
                    contactCardImportDTO.setErrorDetail(messageService.getMessageDetailByCode("6047019"));
                } else if (e.getValidationErrors().get(0).getMessage().equals("can't get user with employeeId")) {
                    ImportValidateUtil.addErrorToJSON(error, messageService.getMessageDetailByCode("6047034"), contactCardImportDTO.getRowNum());
                    contactCardImportDTO.setErrorDetail(messageService.getMessageDetailByCode("6047034"));
                } else if (e.getValidationErrors().get(0).getMessage().equals("contactCard has exist")) {
                    ImportValidateUtil.addErrorToJSON(error, messageService.getMessageDetailByCode("6047027"), contactCardImportDTO.getRowNum());
                    contactCardImportDTO.setErrorDetail(messageService.getMessageDetailByCode("6047027"));
                } else {
                    ImportValidateUtil.addErrorToJSON(error, messageService.getMessageDetailByCode(RespCode.UNKNOWN_ERROR), contactCardImportDTO.getRowNum());
                    contactCardImportDTO.setErrorDetail(messageService.getMessageDetailByCode(RespCode.UNKNOWN_ERROR));
                }
                UserImportService.failedContactCards.get(transactionLog.getTransactionOid()).add(contactCardImportDTO);
            } catch (Exception e) {
                transactionLog.setFailureEntities(transactionLog.getFailureEntities() + 1);
                ImportValidateUtil.addErrorToJSON(error, messageService.getMessageDetailByCode(RespCode.UNKNOWN_ERROR), contactCardImportDTO.getRowNum());
                contactCardImportDTO.setErrorDetail(messageService.getMessageDetailByCode(RespCode.UNKNOWN_ERROR));
                UserImportService.failedContactCards.get(transactionLog.getTransactionOid()).add(contactCardImportDTO);
            }
        }

        return successContactCards;
    }

    /**
     * 批量获取用户默认证件信息
     *
     * @param userOids
     * @return
     */
    public Map<UUID, ContactCard> getUsersDefaultCardsMap(List<UUID> userOids) {
        if(CollectionUtils.isEmpty(userOids)) {
            return null;
        }
        List<ContactCard> contactCards = findByUserOidIn(userOids,null,true);
        return contactCards.stream().collect(Collectors.toMap(ContactCard::getUserOid, c -> c));
    }

    /**
     * 获取用户默认证件信息
     *
     * @param userOid
     * @return
     */
    public ContactCardDTO getUserDefaultCard(UUID userOid) {
        ContactCardDTO contactCardDTO = null;
        if(userOid != null){
            Optional<ContactCard> contactCard = Optional.ofNullable(getUserContactCard(userOid,true,true));
            if(contactCard.isPresent()){
                contactCardDTO = contactCardToDTO(contactCard.get(),OrgInformationUtil.getCurrentLanguage());
            }
        }
        return contactCardDTO;
    }

    public void saveDefautNameCard(UserDTO user){
        Optional<ContactCard> optional = findByUserOidAndCardType(user.getUserOid(),CardType.ID_CARD.getId());
        if(optional.isPresent()) {
            return;
        }
        Contact contact=contactService.getContactByUserOid(user.getUserOid());
        if(contact == null) {
            return;
        }

        String fullName = contact.getFullName();
        if(org.apache.commons.lang3.StringUtils.isNotBlank(fullName)
            &&isChineseName(fullName)){
            ContactCard contactCard = new ContactCard();
            contactCard.setContactCardOid(UUID.randomUUID());
            contactCard.setUserOid(user.getUserOid());
            contactCard.setCardType(CardType.ID_CARD.getId());
            contactCard.setEnabled(true);
            contactCard.setFirstName(fullName);
            contactCard.setCreatedDate(ZonedDateTime.now());
            contactCard.setCreatedBy(user.getId());
            insertOrUpdate(contactCard);
        }
    }

    public boolean isChineseName(String fullName) {
        //判断是否是中文
        if(fullName.matches("^[\\u4e00-\\u9fa5]+$")){
            return true;
        }
        return false;
    }

    public List<ContactCard> findByEnableTrueAndDefaultTrueAndUserOidIn(List<UUID> userOids) {
        return findByUserOidIn(userOids,true,true);
    }
    public ContactCardDTO contactCardToDTO(ContactCard contactCard, String language) {
        ContactCardDTO contactCardDTO = new ContactCardDTO();
        contactCardDTO.setContactCardOid(contactCard.getContactCardOid());
        contactCardDTO.setUserOid(contactCard.getUserOid());
        contactCardDTO.setCardType(contactCard.getCardType());
        CardType cardType = CardType.parse(contactCard.getCardType());
        contactCardDTO.setCardTypeName(cardType == null ? null : CardType.getCardNameByType(cardType,language));
        contactCardDTO.setOriginalCardNo(UserInfoEncryptUtil.detrypt(contactCard.getCardNo()));
        contactCardDTO.setCardNo(UserInfoEncryptUtil.displayCardNo(contactCard.getCardNo(),true));
        contactCardDTO.setCardNoStr(UserInfoEncryptUtil.detrypt(contactCard.getCardNo()));
        contactCardDTO.setFirstName(contactCard.getFirstName());
        contactCardDTO.setLastName(contactCard.getLastName());
        contactCardDTO.setCardExpiredTime(contactCard.getCardExpiredTime());
        contactCardDTO.setCardExpiredTimeStr(contactCard.getCardExpiredTime() == null ? "" : contactCard.getCardExpiredTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
        contactCardDTO.setPrimary(contactCard.getPrimary());
        contactCardDTO.setPrimaryStr(contactCard.getPrimary() ? messageService.getMessageDetailByCode(RespCode.SYS_YES) : messageService.getMessageDetailByCode(RespCode.SYS_NO));
        contactCardDTO.setEnabled(contactCard.getEnabled());
        contactCardDTO.setEnabledStr(contactCard.getEnabled() ? messageService.getMessageDetailByCode(RespCode.SYS_ENABLED) : messageService.getMessageDetailByCode(RespCode.SYS_DISABLED));
        contactCardDTO.setNationality(contactCard.getNationality());
        contactCardDTO.setNationalityCode(contactCard.getNationalityCode());
        contactCardDTO.setEmployeeId(contactService.selectEmployeeIdByUserOid(contactCard.getUserOid()));
        if(!StringUtils.isEmpty(contactCard.getNationalityCode())){
            // 根据用户租户和性别值列表类型查询值列表
            SysCodeValueCO sysCodeValue = hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.NATIONALITY.getId().toString(), contactCard.getNationalityCode());
            // 根据值列表id和value查询值列表项
            if(sysCodeValue != null){
                contactCardDTO.setNationality(sysCodeValue.getName());
            }

        }else{
            contactCardDTO.setNationality(contactCard.getNationality());
        }
        return contactCardDTO;
    }

    public ContactCardDTO contactCardImportDTOToDTO(ContactCardImportDTO contactCardImportDTO) {
        ContactCardDTO contactCardDTO = new ContactCardDTO();
        contactCardDTO.setUserOid(contactCardImportDTO.getUserOid());
        contactCardDTO.setEmployeeId(contactCardImportDTO.getEmployeeId());
        contactCardDTO.setCardType(Integer.parseInt(contactCardImportDTO.getCardType()));
        contactCardDTO.setCardNo(UserInfoEncryptUtil.encrypt(contactCardImportDTO.getCardNo()));
        contactCardDTO.setFirstName(contactCardImportDTO.getFirstName());
        contactCardDTO.setLastName(contactCardImportDTO.getLastName());
        contactCardDTO.setNationality(contactCardImportDTO.getNationality());
        contactCardDTO.setNationalityCode(contactCardImportDTO.getNationality());
        if (!StringUtils.isEmpty(contactCardImportDTO.getCardExpiredTime())) {
            try {
                contactCardDTO.setCardExpiredTime(ZonedDateTime.parse(contactCardImportDTO.getCardExpiredTime(), DateTimeFormatter.ofPattern("yyyy/MM/dd").withZone(ZoneId.systemDefault())));
            } catch (Exception e) {
                throw new ValidationException(new ValidationError("ContactCard", "card expired time pattern error"));
            }
        }
        contactCardDTO.setPrimary(true);
        contactCardDTO.setEnabled(true);
        return contactCardDTO;
    }

    public List<ContactCardDTO> exportContactCardDTO(Page page,List<UUID> userOids){
        List<ContactCard> contactCards = selectList(new EntityWrapper<ContactCard>().in("user_oid",userOids));
        List<ContactCardDTO> result = contactCards.stream().map(item -> contactCardToDTO(item,OrgInformationUtil.getCurrentLanguage())).collect(Collectors.toList());
        return result;
    }

}
