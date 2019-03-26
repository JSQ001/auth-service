package com.hand.hcf.app.mdata.company.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.common.co.CompanyConfigurationCO;
import com.hand.hcf.app.mdata.company.domain.CompanyConfiguration;
import com.hand.hcf.app.mdata.company.domain.ConfigurationDetail;
import com.hand.hcf.app.mdata.company.persistence.CompanyConfigurationMapper;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.currency.service.CurrencyRateService;
import com.hand.hcf.app.mdata.department.domain.Department;
import com.hand.hcf.app.mdata.department.service.DepartmentUserService;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service Implementation for managing Department.
 */
@Service
@Transactional
public class CompanyConfigurationService extends BaseService<CompanyConfigurationMapper, CompanyConfiguration> {

    @Autowired
    CompanyService companyService;

    @Autowired
    HcfOrganizationInterface hcfOrganizationInterface;

    @Autowired
    private CurrencyRateService currencyRateService;

    @Autowired
    private DepartmentUserService departmentUserService;

    @Autowired
    private ContactService contactService;

    public ConfigurationDetail.Workflow getWorkflowConfiguration(UUID userOid) {
        return this.getCompanyConfigurationByUserOid(userOid).getConfiguration().getWorkflow();
    }

    public ConfigurationDetail.Bpo getBpoConfiguration(UUID userOid) {
        return this.getCompanyConfigurationByUserOid(userOid).getConfiguration().getBpo();
    }

    public ConfigurationDetail.Reimbursement getReimbursementConfiguration(UUID userOid) {
        return this.getCompanyConfigurationByUserOid(userOid).getConfiguration().getReimbursement();
    }

    public ConfigurationDetail.ApprovalRule getApprovalRuleConfiguration(UUID userOid) {
        return this.getCompanyConfigurationByUserOid(userOid).getConfiguration().getApprovalRule();
    }

    public Optional<CompanyConfiguration> findOneByCompanyOid(UUID companyOid) {
        return Optional.ofNullable(selectOne(new EntityWrapper<CompanyConfiguration>()
                .eq("company_oid", companyOid)));
    }

    public CompanyConfiguration getCompanyConfiguration(UUID companyOid) {
        Optional<CompanyConfiguration> companyConfiguration = findOneByCompanyOid(companyOid);
        if (companyConfiguration.isPresent()) {
            return companyConfiguration.get();
        } else {
            return new CompanyConfiguration();
        }

    }

    public CompanyConfiguration getCompanyConfigurationByUserOid(UUID userOid) {
        UserDTO user = contactService.getUserDTOByUserOid(userOid);
        CompanyConfiguration companyConfiguration = selectById(companyService.findOne(user.getCompanyId()).getCompanyOid());
        //根据用户帐套获取当前公司币种
        String baseCurrency = currencyRateService.getUserSetOfBooksBaseCurrency(userOid);
        if (companyConfiguration != null) {
            companyConfiguration.setCurrencyCode(baseCurrency);
            ConfigurationDetail configurationDetail = companyConfiguration.getConfiguration();
            if (companyConfiguration.getConfiguration().getUserConfigurations() != null) {
                ConfigurationDetail.CustomConfiguration departmentCustomConfiguration = this.getDepartmentCustomConfiguration(companyConfiguration.getConfiguration().getDepartmentConfigurations(), user);
                for (ConfigurationDetail.CustomConfiguration customConfiguration : companyConfiguration.getConfiguration().getUserConfigurations()) {
                    if (customConfiguration.getOids().contains(user.getUserOid())) {
                        ConfigurationDetail.Workflow workflow = customConfiguration.getWorkflow();
                        ConfigurationDetail.Travel travel = customConfiguration.getTravel();
                        ConfigurationDetail.Reimbursement reimbursement = customConfiguration.getReimbursement();
                        ConfigurationDetail.Bpo bpo = customConfiguration.getBpo();
                        ConfigurationDetail.Didi didi = customConfiguration.getDidi();
                        if (travel == null) {
                            if (departmentCustomConfiguration != null && departmentCustomConfiguration.getTravel() != null) {
                                travel = departmentCustomConfiguration.getTravel();
                            } else {
                                travel = companyConfiguration.getConfiguration().getTravel();
                            }
                        }
                        this.getConfigurationIfNull(travel, departmentCustomConfiguration == null ? null : departmentCustomConfiguration.getTravel(), companyConfiguration.getConfiguration().getTravel());

                        if (reimbursement == null) {
                            if (departmentCustomConfiguration != null && departmentCustomConfiguration.getReimbursement() != null) {
                                reimbursement = departmentCustomConfiguration.getReimbursement();
                            } else {
                                reimbursement = companyConfiguration.getConfiguration().getReimbursement();
                            }
                        }
                        this.getConfigurationIfNull(reimbursement, departmentCustomConfiguration == null ? null : departmentCustomConfiguration.getReimbursement(), companyConfiguration.getConfiguration().getReimbursement());

                        if (bpo == null) {
                            if (departmentCustomConfiguration != null && departmentCustomConfiguration.getBpo() != null) {
                                bpo = departmentCustomConfiguration.getBpo();
                            } else {
                                bpo = companyConfiguration.getConfiguration().getBpo();
                            }
                        }
                        this.getConfigurationIfNull(bpo, departmentCustomConfiguration == null ? null : departmentCustomConfiguration.getBpo(), companyConfiguration.getConfiguration().getBpo());
                        if (didi == null) {
                            if (departmentCustomConfiguration != null && departmentCustomConfiguration.getDidi() != null) {
                                didi = departmentCustomConfiguration.getDidi();
                            } else {
                                didi = companyConfiguration.getConfiguration().getDidi();
                            }
                        }
                        this.getConfigurationIfNull(didi, departmentCustomConfiguration == null ? null : departmentCustomConfiguration.getDidi(), companyConfiguration.getConfiguration().getDidi());
                        if (workflow == null) {
                            if (departmentCustomConfiguration != null && departmentCustomConfiguration.getWorkflow() != null) {
                                workflow = departmentCustomConfiguration.getWorkflow();
                            } else {
                                workflow = companyConfiguration.getConfiguration().getWorkflow();
                            }
                        }
                        this.getConfigurationIfNull(workflow, departmentCustomConfiguration == null ? null : departmentCustomConfiguration.getWorkflow(), companyConfiguration.getConfiguration().getWorkflow());
                        configurationDetail.setTravel(travel);
                        configurationDetail.setReimbursement(reimbursement);
                        configurationDetail.setBpo(bpo);
                        configurationDetail.setDidi(didi);
                        configurationDetail.setWorkflow(workflow);
                        configurationDetail.setUserConfigurations(null);
                        configurationDetail.setDepartmentConfigurations(null);
                        companyConfiguration.setConfiguration(configurationDetail);
                        return companyConfiguration;
                    }
                }
            }
            if (companyConfiguration.getConfiguration().getDepartmentConfigurations() != null) {
                Optional<Department> department =departmentUserService.getDepartmentByUserId(user.getId());
                for (ConfigurationDetail.CustomConfiguration customConfiguration : companyConfiguration.getConfiguration().getDepartmentConfigurations()) {
                    if (department.isPresent() && customConfiguration.getOids().contains(department.get().getDepartmentOid())) {
                        ConfigurationDetail.Travel travel = customConfiguration.getTravel();
                        ConfigurationDetail.Reimbursement reimbursement = customConfiguration.getReimbursement();
                        ConfigurationDetail.Bpo bpo = customConfiguration.getBpo();
                        ConfigurationDetail.Workflow workflow = customConfiguration.getWorkflow();
                        ConfigurationDetail.Didi didi = customConfiguration.getDidi();
                        if (travel == null) {
                            travel = companyConfiguration.getConfiguration().getTravel();
                        }
                        this.getConfigurationIfNull(travel, companyConfiguration.getConfiguration().getTravel(), null);
                        if (reimbursement == null) {
                            reimbursement = companyConfiguration.getConfiguration().getReimbursement();
                        }
                        this.getConfigurationIfNull(reimbursement, companyConfiguration.getConfiguration().getReimbursement(), null);

                        if (bpo == null) {
                            bpo = companyConfiguration.getConfiguration().getBpo();
                        }
                        this.getConfigurationIfNull(bpo, companyConfiguration.getConfiguration().getBpo(), null);
                        if (workflow == null) {
                            workflow = companyConfiguration.getConfiguration().getWorkflow();
                        }
                        this.getConfigurationIfNull(workflow, companyConfiguration.getConfiguration().getWorkflow(), null);
                        if (didi == null) {
                            didi = companyConfiguration.getConfiguration().getDidi();
                        }
                        this.getConfigurationIfNull(didi, companyConfiguration.getConfiguration().getDidi(), null);
                        configurationDetail.setTravel(travel);
                        configurationDetail.setReimbursement(reimbursement);
                        configurationDetail.setBpo(bpo);
                        configurationDetail.setWorkflow(workflow);
                        configurationDetail.setDidi(didi);
                        configurationDetail.setUserConfigurations(null);
                        configurationDetail.setDepartmentConfigurations(null);
                        companyConfiguration.setConfiguration(configurationDetail);
                        return companyConfiguration;
                    }
                }
            }
            configurationDetail.setUserConfigurations(null);
            configurationDetail.setDepartmentConfigurations(null);
            companyConfiguration.setConfiguration(configurationDetail);
        } else {
            return new CompanyConfiguration();
        }
        return companyConfiguration;
    }

    public ConfigurationDetail.CustomConfiguration getDepartmentCustomConfiguration(List<ConfigurationDetail.CustomConfiguration> customConfigurationList, UserDTO user) {
        Optional<Department> department =departmentUserService.getDepartmentByUserId(user.getId());
        for (ConfigurationDetail.CustomConfiguration customConfiguration : customConfigurationList) {
            if (department.isPresent() && customConfiguration.getOids().contains(department.get().getDepartmentOid())) {
                return customConfiguration;
            }
        }
        return null;
    }

    public void getConfigurationIfNull(Object configuration, Object parentConfiguration, Object grandParentConfiguration) {
        for (Field f : configuration.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (f.get(configuration) == null) {
                    //判断属性是否为空，如果是空，则获取上一层配置属性，若上一层配置为空，再取上一层配置属性。
                    if (parentConfiguration != null && f.get(parentConfiguration) != null) {
                        f.set(configuration, f.get(parentConfiguration));
                    } else if (grandParentConfiguration != null) {
                        f.set(configuration, f.get(grandParentConfiguration));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public CompanyConfiguration save(CompanyConfiguration companyConfiguration) {
        insertOrUpdate(companyConfiguration);
        return companyConfiguration;
    }

    public CompanyConfigurationCO toCO(CompanyConfiguration companyConfiguration){
        CompanyConfigurationCO companyConfigurationCO=new CompanyConfigurationCO();
        companyConfigurationCO.setApprovalMode(companyConfiguration.getConfiguration().getApprovalRule().getApprovalMode());
        companyConfigurationCO.setApprovalPathMode(companyConfiguration.getConfiguration().getApprovalRule().getApprovalPathMode());
        companyConfigurationCO.setDepartmentLevel(companyConfiguration.getConfiguration().getApprovalRule().getDepartmentLevel());
        companyConfigurationCO.setMaxApprovalChain(companyConfiguration.getConfiguration().getApprovalRule().getMaxApprovalChain());
        return companyConfigurationCO;
    }
}
