package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.DepartmentCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.payment.domain.CompanyBank;
import com.hand.hcf.app.payment.domain.CompanyBankAuth;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.persistence.CompanyBankAuthMapper;
import com.hand.hcf.app.payment.utils.RespCode;
import com.hand.hcf.app.payment.web.adapter.CompanyBankAuthAdapter;
import com.hand.hcf.app.payment.web.dto.CompanyBankAuthDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by 刘亮 on 2017/9/28.
 */
@Service
@Transactional
public class CompanyBankAuthService extends ServiceImpl<CompanyBankAuthMapper, CompanyBankAuth> {


    @Autowired
    private CompanyBankAuthMapper companyBankAuthMapper;
    @Autowired
    private CompanyBankService companyBankService;
    @Autowired
    private PaymentOrganizationService organizationService;
    @Autowired
    private CompanyBankAuthAdapter companyBankAuthAdapter;


    public CompanyBankAuth insertOrUpdateCompanyBankAuth(CompanyBankAuth companyBankAuth) {
        CompanyCO company = organizationService.getById(companyBankAuth.getAuthorizeCompanyId());
        companyBankAuth.setCompanyCode(company.getCompanyCode() == null ? "" : company.getCompanyCode());
        if (companyBankAuth.getAuthorizeDepartmentId() != null) {
            DepartmentCO department = organizationService.getDepartmentById(companyBankAuth.getAuthorizeDepartmentId());
            companyBankAuth.setDepartmentCode(department.getDepartmentCode() == null ? "" : department.getDepartmentCode());
        }
        if (companyBankAuth.getAuthorizeEmployeeId() != null) {
            ContactCO userCO = organizationService.getByUserCode(companyBankAuth.getEmployeeCode());//new ManagedUserDTO(userService.getUserWithAuthorities(companyBankAuth.getAuthorizeEmployeeId()));
            companyBankAuth.setEmployeeCode(userCO.getEmployeeCode() == null ? "" : userCO.getEmployeeCode());
        }
        if (companyBankAuth.getId() == null) {//新增
            companyBankAuth.setCreatedDate(ZonedDateTime.now());
            companyBankAuth.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
            companyBankAuth.setLastUpdatedDate(ZonedDateTime.now());
            companyBankAuth.setCreatedBy(OrgInformationUtil.getCurrentUserId());
            checkCompanyAuth(companyBankAuth);
            companyBankAuthMapper.insert(companyBankAuth);
            return companyBankAuthMapper.selectById(companyBankAuth.getId());
        }else{
           CompanyBankAuth oldCompanyBankAuth = companyBankAuthMapper.selectById(companyBankAuth.getId());
           if(oldCompanyBankAuth != null){
               companyBankAuth.setVersionNumber(oldCompanyBankAuth.getVersionNumber());
           }
        }
        //修改
        companyBankAuth.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        companyBankAuth.setLastUpdatedDate(ZonedDateTime.now());
        companyBankAuth.setDeleted(false);
        companyBankAuthMapper.updateAllColumnById(companyBankAuth);
        return companyBankAuthMapper.selectById(companyBankAuth.getId());
    }

    private void checkCompanyAuth(CompanyBankAuth companyBankAuth) {
        List<CompanyBankAuth> list = companyBankAuthMapper.selectList(new EntityWrapper<CompanyBankAuth>()
                .eq("bank_account_id", companyBankAuth.getBankAccountId())
                .eq("authorize_company_id", companyBankAuth.getAuthorizeCompanyId())
                .eq("authorize_department_id", companyBankAuth.getAuthorizeDepartmentId())
                .eq("authorize_employee_id", companyBankAuth.getAuthorizeEmployeeId())
                .eq("enabled", true)
                .eq("deleted", false)
        );
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        throw new BizException(RespCode.COMPANY_BANK_AUTH_EXIT);
    }


    //逻辑删除公司下授权的银行账户
    public Boolean deleteById(Long id) {
        CompanyBankAuth companyBankAuth = companyBankAuthMapper.selectById(id);
        if (companyBankAuth == null) {
            return true;
        }
        companyBankAuth.setDeleted(true);
        int i = companyBankAuthMapper.updateById(companyBankAuth);
        return i != 0 ? true : false;
    }


    //根据银行账户id，分页查询所有银行账户授权
    public Page<CompanyBankAuthDTO> selectConpanyBankAuths(Long id, Page page) {

        EntityWrapper<CompanyBankAuth> wrapper = new EntityWrapper<>();
        wrapper.eq("bank_account_id", id);
        wrapper.eq("deleted", false);
        wrapper.orderBy("company_code");
        wrapper.orderBy("department_code");
        wrapper.orderBy("employee_code");
        List<CompanyBankAuth> list = companyBankAuthMapper.selectPage(page, wrapper);
        List<CompanyBankAuthDTO> dtoList = new ArrayList<>();
        for (CompanyBankAuth companyBankAuth : list) {
            dtoList.add(companyBankAuthAdapter.toDTO(companyBankAuth));
        }
        if (CollectionUtils.isNotEmpty(dtoList)) {
            page.setRecords(dtoList);
        }
        return page;
    }

    //根据用户id查看被授权到本用户的银行账户列表
    public List<CompanyBank> selectByEmpAuth(UUID empId) {

        List<Long> companyBankIds = getCompanyBankByUserOID(empId);
        List<CompanyBank> companyBankList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(companyBankIds)) {
            companyBankList = companyBankService.selectBatchIds(companyBankIds);
        }

//        //查询员工所在的公司和部门
//        ManagedUserDTO domain = new ManagedUserDTO(userService.getUserWithAuthorities(empId));
//        Long departmentId = departmentService.findOne(domain.getDepartmentOID()).getId();
//        Long companyId = domain.getCompanyId();
//        List<CompanyBank> list = companyBankAuthMapper.getCompanyBankByAuthNoPage(empId.toString(), departmentId, companyId);

//        List<CompanyBank> listCompanyBank = new ArrayList<>();
//        EntityWrapper<CompanyBankAuth> wrapper = new EntityWrapper<>();
//        wrapper.eq("authorize_employee_id",empId);
//        wrapper.eq("authorize_company_id",SecurityUtils.getCurrentCompanyId());
//        wrapper.eq("is_deleted",false);
//        wrapper.orderBy("company_code");
//        wrapper.orderBy("department_code");
//        wrapper.orderBy("employee_code");
//        List<CompanyBankAuth> list = companyBankAuthMapper.selectList(wrapper);
//        for(CompanyBankAuth companyBankAuth:list){
//            CompanyBank companyBank = companyBankService.selectById(companyBankAuth.getBankAccountId());
//            listCompanyBank.add(companyBank);
//        }
        return companyBankList;
    }

    //根据用户id查询用户所能看到的所有授权银行账户信息
    public Page<CompanyBankAuthDTO> getCompanyBankAuthDTOByEmpOid(String employeeOID, String companyCode, String companyName, Page page) {

        List<Long> bankAccountIds = companyBankAuthMapper.getBankAccountIds(companyCode, companyName);
        if (CollectionUtils.isEmpty(bankAccountIds)) {
            return page;
        }
        //查询员工所在的公司和部门
        // ManagedUserDTO dto = new ManagedUserDTO(userService.getUserWithAuthorities(UUID.fromString(employeeOID)));
        ContactCO dto = organizationService.getByUserOid(employeeOID);
        Long departmentId = organizationService.getDepartmentByEmpOid(employeeOID).getId();
        Long companyId = dto.getCompanyId();

        List<CompanyBankAuthDTO> bankAuthDTOS =
                companyBankAuthMapper.getCompanyBankAuthByAuth(employeeOID, departmentId, companyId, bankAccountIds, page)
                        .stream().map(a -> companyBankAuthAdapter.toDTO(a)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(bankAuthDTOS)) {
            page.setRecords(bankAuthDTOS);
        }
        return page;
    }


    //查询银行账户授权到的银行账户
    public List<Long> getCompanyBankByUserOID(UUID userOID) {

        ContactCO userDTO = organizationService.getByUserOid(userOID.toString());
        Long departmentId = organizationService.getDepartmentByEmpOid(userOID.toString()).getId();

        //从授权到的公司里面找
        List<Long> companyAccountIds = companyBankAuthMapper.selectList(
                new EntityWrapper<CompanyBankAuth>()
                        .eq("authorize_company_id", userDTO.getCompanyId())
                        .eq("auth_flag", 1001)
                        .eq("enabled", true)
                        .eq("deleted", false)

        ).stream().map(CompanyBankAuth::getBankAccountId).collect(Collectors.toList());

        //从授权到部门里面找
        List<Long> departmentAccountIds = companyBankAuthMapper.selectList(
                new EntityWrapper<CompanyBankAuth>()
                        .eq("authorize_department_id", departmentId)
                        .eq("authorize_company_id", userDTO.getCompanyId())
                        .eq("auth_flag", 1002)
                        .eq("enabled", true)
                        .eq("deleted", false)

        ).stream().map(CompanyBankAuth::getBankAccountId).collect(Collectors.toList());

        //从授权到员工里面找
        List<Long> userAccountIds = companyBankAuthMapper.selectList(
                new EntityWrapper<CompanyBankAuth>()
                        .eq("authorize_department_id", departmentId)
                        .eq("authorize_company_id", userDTO.getCompanyId())
                        .eq("authorize_employee_id", userDTO.getUserOid())
                        .eq("auth_flag", 1003)
                        .eq("enabled", true)
                        .eq("deleted", false)
        ).stream().map(CompanyBankAuth::getBankAccountId).collect(Collectors.toList());

        //取并集然后去重
        userAccountIds.addAll(departmentAccountIds);
        userAccountIds.addAll(companyAccountIds);
        userAccountIds.stream().distinct().collect(Collectors.toList());


        return userAccountIds;
    }


    /**
     * 根据用户OID查询该用户被授权的付款公司信息
     *
     * @param employeeOID
     * @param companyCode
     * @param companyName
     * @param page
     * @return
     */
    public Page<CompanyBankAuthDTO> getCompanyInfoByEmpOid(String employeeOID, String companyCode, String companyName, Page page) {
        //此domain中没有companyName字段，无法在此处做模糊查询，需要三方接口中处理。
        List<Long> bankAccountIds = companyBankAuthMapper.getBankAccountIds(companyCode, companyName);
        if (CollectionUtils.isEmpty(bankAccountIds)) {
            return page;
        }

        ContactCO dto = organizationService.getByUserOid(employeeOID);
        Long departmentId = organizationService.getDepartmentByEmpOid(employeeOID).getId();
        Long companyId = dto.getCompanyId();
        ZonedDateTime currentDate = ZonedDateTime.now();
        List<Long> companyIds = companyBankAuthMapper.getPaymentCompanyInfo(employeeOID, departmentId, companyId, bankAccountIds, currentDate, page);
        List<CompanyCO> companyCOS = organizationService.listCompanyByCond(null,null,null,companyName,null,null,companyIds);
        List<CompanyBankAuthDTO> bankAuthDTOS = new ArrayList<>();
        for (CompanyCO companyCO : companyCOS) {
            CompanyBankAuthDTO companyBankAuthDTO = new CompanyBankAuthDTO();
            companyBankAuthDTO.setBankAccountCompanyId(companyCO.getId());
            companyBankAuthDTO.setBankAccountCompanyCode(companyCO.getCompanyCode());
            companyBankAuthDTO.setBankAccountCompanyName(companyCO.getName());
            bankAuthDTOS.add(companyBankAuthDTO);
        }

        if (CollectionUtils.isNotEmpty(bankAuthDTOS)) {
            page.setRecords(bankAuthDTOS);
        }
        return page;
    }


}
