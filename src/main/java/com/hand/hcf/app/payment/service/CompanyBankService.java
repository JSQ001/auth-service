package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.DataAuthorityUtil;
import com.hand.hcf.app.mdata.bank.dto.BankInfoDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.implement.web.BankInfoControllerImpl;
import com.hand.hcf.app.payment.domain.CompanyBank;
import com.hand.hcf.app.payment.domain.CompanyBankAuth;
import com.hand.hcf.app.payment.domain.CompanyBankPayment;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.persistence.CompanyBankAuthMapper;
import com.hand.hcf.app.payment.persistence.CompanyBankMapper;
import com.hand.hcf.app.payment.persistence.CompanyBankPaymentMapper;
import com.hand.hcf.app.payment.utils.PatternMatcherUtil;
import com.hand.hcf.app.payment.utils.RespCode;
import com.hand.hcf.app.payment.utils.StringUtil;
import com.hand.hcf.app.payment.web.dto.CompanyBankDTO;
import com.hand.hcf.app.payment.web.dto.CompanyBankImportDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 刘亮 on 2017/9/8.
 */
@Service
@Transactional
public class CompanyBankService extends ServiceImpl<CompanyBankMapper, CompanyBank> {

    @Autowired
    private CompanyBankMapper companyBankMapper;
    @Autowired
    private PaymentOrganizationService organizationService;
    @Autowired
    private CompanyBankPaymentMapper companyBankPaymentMapper;
    @Autowired
    private CompanyBankAuthMapper companyBankAuthMapper;
    @Autowired
    public final static String BANK_NUMBER_VALIDATION_REGEX = "[A-Za-z0-9]{1,36}";   // 银行code验证表达式 只能限制输入数字
    @Autowired
    private BankInfoControllerImpl bankInterface;


    //新增或修改公司银行账户
    public CompanyBank insertOrUpdateCompanyBank(CompanyBank companyBank, UUID userId) {
//        boolean validateBankName = true;
        boolean validateBankNumber = true;
        boolean isInsert = true;
        companyBank.setTenantId(OrgInformationUtil.getCurrentTenantId());
        //  name过滤单引号
//        String name = StringEscapeUtils.escapeSql(companyBank.getBankAccountName());
        //  name过滤特殊字符
//        String nameResult = StringUtil.filterSpecialCharacters(name);
        //  过滤后重新set
//        companyBank.setBankAccountName(nameResult);
        //  number过滤单引号
        String number = StringEscapeUtils.escapeSql(companyBank.getBankAccountNumber());
        //  number过滤特殊字符
        String numberResult = StringUtil.filterSpecialCharacters(number);
        //  过滤后重新set
        companyBank.setBankAccountNumber(numberResult);
        // 验证备注是否超过100位文字
        companyBank.setRemark(checkRemark(companyBank.getRemark()));
        if (companyBank.getId() == null) {
            companyBank.setCreatedDate(ZonedDateTime.now());
            CompanyCO companyCO = organizationService.getById(companyBank.getCompanyId());
            SetOfBooksInfoCO setOfBooks = organizationService.getSetOfBooksById(companyCO.getSetOfBooksId());
            companyBank.setCompanyCode(companyCO.getCompanyCode());
            companyBank.setSetOfBooksId(companyCO.getSetOfBooksId());
            companyBank.setSetOfBooksCode(setOfBooks.getSetOfBooksCode());
        } else {
            isInsert = false;
            CompanyBank oldCompanyBank = companyBankMapper.selectById(companyBank.getId());
//            if(oldCompanyBank.getBankName().equals(companyBank.getBankName())){
//                validateBankName = false;
//            }
            if (oldCompanyBank.getBankAccountNumber().equals(companyBank.getBankAccountNumber())) {
                validateBankNumber = false;
            }
        }
        //修改
        companyBank.setLastUpdatedDate(ZonedDateTime.now());
        companyBank.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        // 是否验证银行名称
//        if(validateBankName){
//            checkCompanyBankName(companyBank.getBankAccountName());
//        }
        // 是否验证银行账号
        if (validateBankNumber) {
            checkCompanyBankNumber(companyBank.getBankAccountNumber());
        }
        if (isInsert) {
            companyBankMapper.insert(companyBank);
        } else {
            companyBankMapper.updateById(companyBank);
        }
        //  return companyBank;
        CompanyCO companyDTO = organizationService.getById(companyBank.getCompanyId());
        companyBank.setCompanyCode(companyDTO.getCompanyCode() == null ? "" : companyDTO.getCompanyCode());
        companyBank.setSetOfBooksId(companyDTO.getSetOfBooksId());
        SetOfBooksInfoCO setOfBooks = organizationService.getSetOfBooksById(companyDTO.getSetOfBooksId());
        companyBank.setSetOfBooksCode(setOfBooks.getSetOfBooksCode() == null ? "" : setOfBooks.getSetOfBooksCode());
        companyBankMapper.updateById(companyBank);
        return companyBankMapper.selectById(companyBank.getId());
    }


    //逻辑删除公司银行账户
    public boolean deleteCompanyBankById(Long id) {
        CompanyBank companyBank = companyBankMapper.selectById(id);
        if (companyBank == null) {
            throw new BizException(RespCode.COMPANY_BANK_NOT_FOUND);
        }
        companyBank.setDeleted(true);
        companyBank.setBankAccountName(companyBank.getBankAccountName() + "_DELETE_" + RandomStringUtils.randomNumeric(6));
        companyBank.setBankAccountNumber(companyBank.getBankAccountNumber() + "_DELETE_" + RandomStringUtils.randomNumeric(6));
        int i = companyBankMapper.updateById(companyBank);
        return i != 0 ? true : false;
    }


//    public void checkCompanyBankName(String companyBankName){
//        if(companyBankName.length()>35){
//            throw new BizException(RespCode.COMPANY_BANK_CODE_OR_NUM_LENGTH_MORE_THEN_LIMIT_22003);
//        }
//        EntityWrapper<CompanyBank> wrapper = new EntityWrapper<>();
//        wrapper.eq("bank_account_name",companyBankName);
//        wrapper.eq("tenant_id",SecurityUtils.getCurrentTenantID());
//        wrapper.eq("deleted",false);
//        if(companyBankMapper.selectList(wrapper).size() != 0){
//            throw new BizException(RespCode.COMPANY_BANK_CODE_OR_NUM_22002);
//        }
//    }

    public void checkCompanyBankNumber(String companyBankNumber) {
        if (companyBankNumber.length() > 35) {
            throw new BizException(RespCode.COMPANY_BANK_CODE_OR_NUM_LENGTH_MORE_THEN_LIMIT);
        }
        // 只允许输入字母和数字
        if (!PatternMatcherUtil.validationPatterMatcherRegex(companyBankNumber, BANK_NUMBER_VALIDATION_REGEX)) {
            throw new BizException(RespCode.ACCOUNT_NUMBER_IS_ALLOWED_TO_ENTER_LETTERS_OR_NUMBERS);
        }
        EntityWrapper<CompanyBank> wrapper = new EntityWrapper<>();
        wrapper.eq("bank_account_number", companyBankNumber);
        wrapper.eq("tenant_id", OrgInformationUtil.getCurrentTenantId());
        wrapper.eq("deleted", false);
        if (companyBankMapper.selectList(wrapper).size() != 0) {
            throw new BizException(RespCode.COMPANY_BANK_CODE_OR_NUM);
        }
    }

    //根据公司id分页查询所有银行账户
    public Page<CompanyBankDTO> selectCompanyBankByCompanyId(Long companyId, String companyCode, String companyName, String companyBankCode, String companyBankName, Long setOfBooksId, String currency, Page<CompanyBankDTO> page, boolean dataAuthFlag) {
        EntityWrapper<CompanyBank> wrapper = new EntityWrapper<>();
        List<Long> companyIds = new ArrayList<>();

        String dataAuthLabel = null;
        if(dataAuthFlag){
            Map<String,String> map = new HashMap<>();
            map.put(DataAuthorityUtil.TABLE_NAME, "csh_company_bank");
            map.put(DataAuthorityUtil.SOB_COLUMN, "set_of_books_id");
            dataAuthLabel = DataAuthorityUtil.getDataAuthLabel(map);
        }

        if (companyId == null) {
//            companyIds = companyBankMapper.selectCompanyIdsByCodeAndName(companyCode, companyName, OrgInformationUtil.getCurrentTenantId(), setOfBooksId);
//            if (CollectionUtils.isEmpty(companyIds)) {
//                return page;
//            }
//            wrapper.in(CollectionUtils.isNotEmpty(companyIds), "company_id", companyIds);
            //@wyz,companyId为空不默认取当前公司
            //companyId = OrgInformationUtil.getCurrentCompanyId();
        }
        wrapper.eq(companyId != null, "company_id", companyId);
        wrapper.like(StringUtils.isNotEmpty(companyCode), "company_code", companyCode);
        if(StringUtils.isNotEmpty(companyName)){
            List<CompanyCO> companyCOS = organizationService.listCompanyBySetOfBooksIdAndCodeAndName(null, null, companyName);
            if (CollectionUtils.isNotEmpty(companyCOS)) {
                List<Long> ids = companyCOS.stream().map(CompanyCO::getId).collect(Collectors.toList());
                wrapper.in("company_id",ids);
            } else {
                wrapper.where("1=2");
            }
        }
        wrapper.eq(setOfBooksId != null,"set_of_books_id", setOfBooksId);
        wrapper.like(StringUtils.isNotEmpty(companyBankCode), "bank_account_number", companyBankCode);
        wrapper.like(StringUtils.isNotEmpty(companyBankName), "bank_account_name", companyBankName);
        wrapper.eq("deleted", false);
        wrapper.eq("tenant_id", OrgInformationUtil.getCurrentTenantId());
        wrapper.eq(StringUtils.isNotEmpty(currency), "currency_code", currency);
        //jiu.zhao TODO
        //wrapper.and(dataAuthLabel);
        wrapper.orderBy("set_of_books_code");
        wrapper.orderBy("company_code");
        wrapper.orderBy("bank_account_number");
        List<CompanyBank> list = companyBankMapper.selectPage(page, wrapper);
        List<CompanyBankDTO> companyBankDTOS = new ArrayList<>();
        list.forEach(
                companyBank -> {
                    CompanyBankDTO companyBankDTO = new CompanyBankDTO();
                    BeanUtils.copyProperties(companyBank, companyBankDTO);
//                companyBankDTO.setCurrencyName(huilianyiStandardCurrencyRepository.findOneByBaseAndOtherCurrency("CNY",companyBankDTO.getCurrencyCode()).getOtherCurrencyName());
                    CompanyCO companyDTO = organizationService.getById(companyBank.getCompanyId());
                    companyBankDTO.setSetOfBooksId(companyDTO.getSetOfBooksId());
                    SetOfBooksInfoCO setOfBooksInfoCO =  organizationService.getSetOfBooksById(companyDTO.getSetOfBooksId());
                    companyBankDTO.setSetOfBooksCode(setOfBooksInfoCO.getSetOfBooksCode());
                    companyBankDTO.setSetOfBooksName(setOfBooksInfoCO.getSetOfBooksName());
                    companyBankDTO.setCompanyCode(companyDTO.getCompanyCode() == null ? "" : companyDTO.getCompanyCode());
                    companyBankDTO.setCompanyName(companyDTO.getName());
                    companyBankDTO.setCurrencyName(organizationService.getForeignCurrencyByCode("CNY", companyBankDTO.getCurrencyCode(), OrgInformationUtil.getCurrentSetOfBookId()).getCurrencyName());
                    companyBankDTOS.add(companyBankDTO);
                }
        );
        if (CollectionUtils.isNotEmpty(companyBankDTOS)) {
            page.setRecords(companyBankDTOS);
        }
        return page;
    }

    public List<CompanyBank> getByCompanyIdAndPaymentMethodCode(Long companyId, String paymentMethodCode, String currency) {

//        //查询员工所在的公司和部门
//        UUID empOID = SecurityUtils.getCurrentUserOID();
//        ManagedUserDTO domain = new ManagedUserDTO(userService.getUserWithAuthorities(empOID));
//        Long departmentId = departmentService.findOne(domain.getDepartmentOID()).getId();
//        Long companyId = domain.getCompanyId();
//        List<CompanyBank> list = companyBankAuthMapper.getCompanyBankByAuthNoPage(empOID.toString(), departmentId, companyId);
//
//        return list;

        List<Long> bankAccountIds = companyBankPaymentMapper.selectList(
                new EntityWrapper<CompanyBankPayment>()
                        .eq("enabled", true)
                        .eq("deleted", false)
                        .eq("payment_method_category", paymentMethodCode)
                        .groupBy("bank_account_id")
                        .setSqlSelect("bank_account_id")
        ).stream().map(CompanyBankPayment::getBankAccountId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(bankAccountIds)) {
            return new ArrayList<>();
        }

        //授权到该员工的银行账户id
        List<Long> accountIds = getCompanyBankByUserOID(OrgInformationUtil.getCurrentUserOid());

        if (CollectionUtils.isEmpty(accountIds)) {
            return new ArrayList<>();
        }
        bankAccountIds.retainAll(accountIds);
        bankAccountIds.stream().distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(bankAccountIds)) {

            List<CompanyBank> list = companyBankMapper.selectList(
                    new EntityWrapper<CompanyBank>()
                            .eq("company_id", companyId)
                            .eq("enabled", true)
                            .eq("deleted", false)
                            .eq(StringUtils.isNotEmpty(currency), "currency_code", currency)
                            .in(CollectionUtils.isNotEmpty(bankAccountIds), "id", bankAccountIds)
            );
            return list;
        } else {
            return new ArrayList<>();
        }
    }


    //查询银行账户授权到的银行账户
    public List<Long> getCompanyBankByUserOID(UUID userOID) {

       ContactCO userCO = organizationService.getByUserOid(userOID.toString());

        Long departmentId = organizationService.getDepartmentByEmpOid(userCO.getUserOid()).getId();
        //从授权到的公司里面找
        List<Long> companyAccountIds = companyBankAuthMapper.selectList(
                new EntityWrapper<CompanyBankAuth>()
                        .eq("authorize_company_id", userCO.getCompanyId())
                        .eq("auth_flag", 1001)
                        .eq("enabled", true)
                        .eq("deleted", false)

        ).stream().map(CompanyBankAuth::getBankAccountId).collect(Collectors.toList());

        //从授权到部门里面找
        List<Long> departmentAccountIds = companyBankAuthMapper.selectList(
                new EntityWrapper<CompanyBankAuth>()
                        .eq("authorize_department_id", departmentId)
                        .eq("authorize_company_id", userCO.getCompanyId())
                        .eq("auth_flag", 1002)
                        .eq("enabled", true)
                        .eq("deleted", false)

        ).stream().map(CompanyBankAuth::getBankAccountId).collect(Collectors.toList());

        //从授权到员工里面找
        List<Long> userAccountIds = companyBankAuthMapper.selectList(
                new EntityWrapper<CompanyBankAuth>()
                        .eq("authorize_department_id", departmentId)
                        .eq("authorize_company_id", userCO.getCompanyId())
                        .eq("authorize_employee_id", userCO.getUserOid())
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

    //根据公司银行id查看公司银行明细
    public CompanyBank selectById(Long companyBankId) {
        CompanyBank companyBank = companyBankMapper.selectById(companyBankId);
        return companyBank;
    }


    //根据公司银行账户账号bankAccountNumber查询公司银行账户信息
    public CompanyBank selectCompanyBankByBankAccountNumber(String bankAccountNumber) {
        List<CompanyBank> list = companyBankMapper.selectList(
                new EntityWrapper<CompanyBank>()
                        .eq("bank_account_number", bankAccountNumber)
                        .eq("deleted", false)
        );
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }


    public Page<CompanyBank> getBySetOfBooksId(Long setOfBooksId, Page<CompanyBank> page) {

        List<CompanyBank> companyBanks = companyBankMapper.selectPage(page,
                new EntityWrapper<CompanyBank>()
                        .eq("set_of_books_id", setOfBooksId)
                        .eq("deleted", false)
                        .orderBy("id")
        );


        if (CollectionUtils.isNotEmpty(companyBanks)) {
            page.setRecords(companyBanks);
        }
        return page;
    }

    /**
     * 检查备注
     *
     * @param remark：备注
     * @return
     */
    public String checkRemark(String remark) {
        if (!StringUtils.isEmpty(remark)) {
            if (remark.trim().length() > 100) {
                throw new BizException("6022007");
            }
            remark = remark.trim();
        }
        return remark;
    }


    public Page<BasicCO> pageCompanyBankByInfoResultBasic(String selectId, String code, String name, String securityType, Long filterId, Page page) {
        //此处的selectId是number
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(selectId)){
            CompanyBank companyBank = this.selectCompanyBankByBankAccountNumber(selectId);
            if(companyBank == null || companyBank.getDeleted()){
                return page;
            }
            BasicCO basicCO =BasicCO
                    .builder()
                    .id(companyBank.getBankAccountNumber())
                    .name(companyBank.getBankAccountNumber())
                    .code(companyBank.getBankAccountName())
                    .build();
            page.setRecords(Arrays.asList(basicCO));
        }else {
            List<Long> companyIds = organizationService.listAllBySetOfBooksId(filterId)
                    .stream()
                    .map(CompanyCO::getId)
                    .collect(Collectors.toList());
            //根据银行账户code  name 条件查询账套下所有公司的所有银行账户
            List<BasicCO> list = baseMapper.pageCompanyBankByInfoResultBasic(filterId,name,code,companyIds,page);
            if(CollectionUtils.isNotEmpty(list)){
                page.setRecords(list);
            }
        }
        return page;
    }

    public Map<String,String> importCompanyBank(List<CompanyBankImportDTO> list) {
        Map<String,String> map = new HashMap<>(2);
        StringBuilder message = new StringBuilder();
        List<CompanyBank> companyBankList = new ArrayList<>();
        list.forEach(item -> {
            String errorMessage = "";
            Long tenantId = OrgInformationUtil.getCurrentTenantId();
            Long setOfBooksId = null;
            if(StringUtils.isEmpty(item.getSetOfBooksCode())){
                errorMessage += "第"+item.getRowNumber()+"行，账套代码为空\r\n";
            }else{
                List<SetOfBooksInfoCO> setOfBooksInfo =
                        organizationService.getSetOfBooksBySetOfBooksCode(item.getSetOfBooksCode());
                if(CollectionUtils.isEmpty(setOfBooksInfo)){
                    errorMessage += "第"+item.getRowNumber()+"行，当前租户下该账套不存在\r\n";
                }else{
                    setOfBooksId = setOfBooksInfo.get(0).getId();
                }
            }
            if(StringUtils.isEmpty(item.getCompanyCode())){
                errorMessage += "第"+item.getRowNumber()+"行，公司代码为空\r\n";
            }else{
                CompanyCO companyCO = organizationService.getByCompanyCode(item.getCompanyCode());
                if(!companyCO.getTenantId().equals(tenantId)||
                        !companyCO.getSetOfBooksId().equals(setOfBooksId)){
                    errorMessage += "第"+item.getRowNumber()+"行，当前租户账套下不存在该公司\r\n";
                }
            }
            if(StringUtils.isEmpty(item.getBankCode())){
                errorMessage += "第"+item.getRowNumber()+"行，银行代码为空\r\n";
            }else{
                BankInfoDTO bankInfoDTO = bankInterface.getBankDataByCode(item.getBankCode());
                if(ObjectUtils.isEmpty(bankInfoDTO)){
                    errorMessage += "第"+item.getRowNumber()+"行，该银行不存在\r\n";
                }else{
                    if(StringUtils.isNotEmpty(item.getBankBranchName()) &&
                            !bankInfoDTO.getBankBranchName().equals(item.getBankBranchName())){
                        errorMessage += "第"+item.getRowNumber()+"行，该支行名称与系统中不一致\r\n";
                    }
                }
            }
            if(StringUtils.isEmpty(item.getBankAccountName())){
                errorMessage += "第"+item.getRowNumber()+"行，银行账户名称为空\r\n";
            }
            if(StringUtils.isEmpty(item.getBankAccountNumber())){
                errorMessage += "第"+item.getRowNumber()+"行，银行账户账号为空\r\n";
            }else{
                CompanyBank bank = selectCompanyBankByBankAccountNumber(item.getBankAccountNumber());
                if(!ObjectUtils.isEmpty(bank)){
                    errorMessage += "第"+item.getRowNumber()+"行，该银行账户账号已存在\r\n";
                }
            }
            if(StringUtils.isEmpty(item.getAccountCode())){
                errorMessage += "第"+item.getRowNumber()+"行，银行账户编码为空\r\n";
            }else{
                List<CompanyBank> bankInfo = baseMapper.selectList(new EntityWrapper<CompanyBank>()
                        .eq("account_code", item.getAccountCode())
                        .eq("tenant_id", tenantId)
                        .eq("deleted", false)
                );
                if(!CollectionUtils.isNotEmpty(bankInfo)){
                    errorMessage += "第"+item.getRowNumber()+"行，当前租户下该银行账户编码已存在\r\n";
                }
            }
            if(StringUtils.isEmpty(item.getCurrencyCode())){
                errorMessage += "第"+item.getRowNumber()+"行，币种为空\r\n";
            }else{
                List<CurrencyRateCO> currency = organizationService.listCurrencysByCode(item.getCurrencyCode(),true,setOfBooksId);
                if(CollectionUtils.isEmpty(currency)){
                    errorMessage += "第"+item.getRowNumber()+"行，当前账套下该币种未启用或不存在\r\n";
                }
            }
            if(StringUtils.isEmpty(item.getEnabled())){
                errorMessage += "第"+item.getRowNumber()+"行，启用标志为空\r\n";
            }
            if(StringUtils.isEmpty(item.getDeleted())){
                errorMessage += "第"+item.getRowNumber()+"行，删除标志为空\r\n";
            }
            if("".equals(errorMessage)){
                CompanyBank companyBank = new CompanyBank();
                companyBank.setTenantId(tenantId);
                companyBank.setSetOfBooksId(setOfBooksId);
                companyBank.setSetOfBooksCode(item.getSetOfBooksCode());
                companyBank.setCompanyCode(item.getCompanyCode());
                companyBank.setBankCode(item.getBankCode());
                companyBank.setBankBranchName(item.getBankBranchName());
                companyBank.setBankAccountName(item.getBankAccountName());
                companyBank.setBankAccountNumber(item.getBankAccountNumber());
                companyBank.setAccountCode(item.getAccountCode());
                companyBank.setSwiftCode(item.getSwiftCode());
                companyBank.setCurrencyCode(item.getCurrencyCode());
                companyBank.setRemark(item.getRemark());
                if(item.getEnabled().equals("1")){
                    companyBank.setEnabled(true);
                }else{
                    companyBank.setEnabled(false);
                }
                if(item.getDeleted().equals("0")){
                    companyBank.setDeleted(false);
                }else{
                    companyBank.setDeleted(true);
                }
                companyBankList.add(companyBank);
            }else{
                message.append(errorMessage);
            }
        });
        if ("".equals(message.toString())) {
            for(CompanyBank companyBank : companyBankList){
                baseMapper.insert(companyBank);
            }
            map.put("导入成功","success");
            return map;
        } else {
            map.put("导入失败","fail");
            map.put("message",message.toString());
            return map;
        }
    }
}
