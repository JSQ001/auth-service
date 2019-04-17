package com.hand.hcf.app.mdata.company.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.RandomUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.domain.CompanySecurity;
import com.hand.hcf.app.mdata.company.persistence.CompanySecurityMapper;
import com.hand.hcf.app.mdata.system.constant.AccountConstants;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * comment
 * Created by fanfuqiang 2018/11/20
 */
@Service
public class CompanySecurityService extends BaseService<CompanySecurityMapper, CompanySecurity> {

    private final Logger log = LoggerFactory.getLogger(CompanySecurityService.class);


    public void createDefaultComapnySecutiry(UUID companyOid) {
        //创建公司默认配置信息
        CompanySecurity companySecurity = new CompanySecurity();
        companySecurity.setAccountCode(RandomUtil.generateCompanyAcountCode());
        companySecurity.setCompanyOid(companyOid);
        companySecurity.setDimissionDelayDays(AccountConstants.DEFAULT_DIMISSION_DELAY_DAYS);
        companySecurity.setNoticeType(AccountConstants.DEFAULT_NOTICETYPE);
        companySecurity.setPasswordLengthMin(AccountConstants.DEFAULT_PASSWORD_LENGTH_MIN);
        companySecurity.setPasswordLengthMax(AccountConstants.DEFAULT_PASSWORD_LENGTH_MAX);
        companySecurity.setPasswordRule(AccountConstants.DEFAULT_PASSWORD_RULE);
        companySecurity.setPasswordRepeatTimes(AccountConstants.DEFAULT_PASSWORD_REPEAT_TIME);
        companySecurity.setPasswordExpireDays(AccountConstants.DEFAULT_PASSWORD_EXPIRE_DAYS);
        companySecurity.setCreateDataType(AccountConstants.DEFAULT_CRATE_DATA_TYPE);
        companySecurity.setTenantId(OrgInformationUtil.getCurrentTenantId());
        insert(companySecurity);
//        dataOperationService.save(OrgInformationUtil.getCurrentUserOid(), companySecurity, messageTranslationService.getMessageDetailByCode(OrgInformationUtil.getCurrentUserLanguage(), DataOperationMessageKey.SECURITY_CONFIGURATION_ID, companySecurity.getId()),
//                OperationEntityTypeEnum.COMPANY_SECURITY.getKey(), OperationTypeEnum.ADD.getKey(), companySecurity.getTenantId());
    }

    public void createTenantCompanySecurity(Long tenantId) {
        //创建租户默认配置信息
        CompanySecurity companySecurity = new CompanySecurity();
        companySecurity.setAccountCode(RandomUtil.generateCompanyAcountCode());
        companySecurity.setDimissionDelayDays(AccountConstants.DEFAULT_DIMISSION_DELAY_DAYS);
        companySecurity.setNoticeType(AccountConstants.DEFAULT_NOTICETYPE);
        companySecurity.setPasswordLengthMin(AccountConstants.DEFAULT_PASSWORD_LENGTH_MIN);
        companySecurity.setPasswordLengthMax(AccountConstants.DEFAULT_PASSWORD_LENGTH_MAX);
        companySecurity.setPasswordRule(AccountConstants.DEFAULT_PASSWORD_RULE);
        companySecurity.setPasswordRepeatTimes(AccountConstants.DEFAULT_PASSWORD_REPEAT_TIME);
        companySecurity.setPasswordExpireDays(AccountConstants.DEFAULT_PASSWORD_EXPIRE_DAYS);
        companySecurity.setCreateDataType(AccountConstants.DEFAULT_CRATE_DATA_TYPE);
        companySecurity.setTenantId(tenantId);
        insert(companySecurity);
//        dataOperationService.save(OrgInformationUtil.getCurrentUserOid(), companySecurity, messageTranslationService.getMessageDetailByCode(OrgInformationUtil.getCurrentUserLanguage(), DataOperationMessageKey.SECURITY_CONFIGURATION_ID, companySecurity.getId()),
//                OperationEntityTypeEnum.COMPANY_SECURITY.getKey(), OperationTypeEnum.ADD.getKey(), tenantId);
    }

    //创建公司时赋值租户的公司安全策略
    public void copyTenantCompanySecurity(Long tenantId, UUID companyOid) {
        CompanySecurity tenantCompanySecurity = this.getTenantCompanySecurity(tenantId);
        if (tenantCompanySecurity != null) {
            //创建公司默认配置信息
            CompanySecurity companySecurity = new CompanySecurity();
            companySecurity.setAccountCode(RandomUtil.generateCompanyAcountCode());
            companySecurity.setCompanyOid(companyOid);
            companySecurity.setDimissionDelayDays(tenantCompanySecurity.getDimissionDelayDays());
            companySecurity.setNoticeType(tenantCompanySecurity.getNoticeType());
            companySecurity.setPasswordLengthMin(tenantCompanySecurity.getPasswordLengthMin());
            companySecurity.setPasswordLengthMax(tenantCompanySecurity.getPasswordLengthMax());
            companySecurity.setPasswordRule(tenantCompanySecurity.getPasswordRule());
            companySecurity.setPasswordRepeatTimes(tenantCompanySecurity.getPasswordRepeatTimes());
            companySecurity.setPasswordExpireDays(tenantCompanySecurity.getPasswordExpireDays());
            companySecurity.setCreateDataType(tenantCompanySecurity.getCreateDataType());
            companySecurity.setTenantId(tenantId);
            insert(companySecurity);
        }
    }

    public CompanySecurity getTenantCompanySecurity(Long tenantId) {
        CompanySecurity companySecurity = baseMapper.findTenantCompanySecurity(tenantId);
        return companySecurity;
    }


    public List<CompanySecurity> listCompanySecuritysByTenant(Long tenantId) {
      return  selectList(new EntityWrapper<CompanySecurity>()
       .eq("tenant_id",tenantId));
    }


    private CompanySecurity getCompanySecurity(UUID companyOId) {
        log.info("query companySecurity instance by param companyId={}", companyOId);
        Map<String, Object> param = new HashedMap();
        param.put("company_oid", companyOId);
        List<CompanySecurity> companySecurityList = baseMapper.selectByMap(param);
        if (companySecurityList == null || companySecurityList.size() <= 0) {
            return null;
        }
        return companySecurityList.get(0);
    }



    public CompanySecurity findOneByCompanyOid(UUID companyOid) {
        Map<String, Object> param = new HashMap<>();
        param.put("company_oid", companyOid);
        List<CompanySecurity> companySecurityList = baseMapper.selectByMap(param);
        if (companySecurityList.size() == 0) {
            return null;
        }
        return companySecurityList.get(0);
    }

    public List<CompanySecurity> findTenantHisCompanySecurity() {
        return baseMapper.findTenantHisCompanySecurity();
    }

    public CompanySecurity getCompanySecurityByCompanyOid(UUID companyOid) {

        List<CompanySecurity> companySecurities = selectList(new EntityWrapper<CompanySecurity>()
                .eq("company_oid", companyOid));
        if (companySecurities.size() == 0) {
            return companySecurities.get(0);
        }
        return null;
    }

    /**
     * 查询企业账号
     *
     * @param tenantId   租户ID
     * @param companyOid 公司Oid
     * @return 企业账号
     */
    public String selectCompanyAccountCode(Long tenantId, UUID companyOid) {
        CompanySecurity companySecurity=selectOne(new EntityWrapper<CompanySecurity>().setSqlSelect("account_code")
                .eq("company_oid", companyOid)
                .eq("tenant_id", tenantId));
        if (companySecurity!=null) {
           return companySecurity.getAccountCode();
        }
        return null;
    }

    public String getAccountPrefix(Company company) {
        String accountPrefix = "";
        CompanySecurity companySecurity = getTenantCompanySecurity(company.getTenantId());

        if(companySecurity == null){
            throw new ObjectNotFoundException(CompanySecurity.class,"companyOid["+company.getCompanyOid()+"]");
        }

        if(companySecurity != null){
            accountPrefix += companySecurity.getAccountCode();
        }
        return accountPrefix;
    }
}
