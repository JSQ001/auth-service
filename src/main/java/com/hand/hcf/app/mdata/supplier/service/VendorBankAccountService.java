package com.hand.hcf.app.mdata.supplier.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.VendorBankAccountCO;
import com.hand.hcf.app.common.enums.SourceEnum;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.bank.dto.BankAccountDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.supplier.constants.Constants;
import com.hand.hcf.app.mdata.supplier.domain.VendorBankAccount;
import com.hand.hcf.app.mdata.supplier.domain.VendorInfo;
import com.hand.hcf.app.mdata.supplier.enums.StatusEnum;
import com.hand.hcf.app.mdata.supplier.enums.VendorStatusEnum;
import com.hand.hcf.app.mdata.supplier.persistence.VendorBankAccountMapper;
import com.hand.hcf.app.mdata.supplier.persistence.VendorInfoMapper;
import com.hand.hcf.app.mdata.supplier.web.adapter.VendorBankAccountAdapter;
import com.hand.hcf.app.mdata.supplier.web.dto.VendorAccountDTO;
import com.hand.hcf.app.mdata.utils.MyStringUtils;
import com.hand.hcf.app.mdata.utils.RespCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/4 17:15
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VendorBankAccountService extends BaseService<VendorBankAccountMapper, VendorBankAccount> {
    @Autowired
    private VendorInfoMapper vendorInfoMapper;

    @Autowired
    private VendorBankAccountMapper vendorBankAccountMapper;

    private static final String GO = "go";

    private static final String FALSE = "false";

    //数字或者英文横杠
    private static final Pattern bankAccountPattern = Pattern.compile("^[0-9-]{1,50}$");

    public VendorBankAccountCO createOrUpdateVendorBankAccount(VendorBankAccountCO vendorBankAccountCO, String roleType) {
        VendorBankAccountCO result;
        VendorInfo vendorInfo = vendorInfoMapper.selectById(vendorBankAccountCO.getVenInfoId());
        if (vendorInfo == null) {
            throw new BizException(RespCode.SUPPLIER_NOT_EXISTS);
        } else {
            validateVendorBankAccountCO(vendorBankAccountCO);
            // 公司权限无法新增或修改租户级供应商下的银行账号信息
            if (!Constants.TENANT_LEVEL.equals(roleType) && SourceEnum.TENANT == vendorInfo.getSource()) {
                throw new BizException(RespCode.SUPPLIER_LACK_OF_COMPANY_AUTHORITY_TO_UPDATE_ACCOUNT);
            }
            Long currentUserId = OrgInformationUtil.getCurrentUserId();
            vendorBankAccountCO.setVenInfoName(vendorInfo.getVendorName());
            // 供应商编码替代了供应商标识
            vendorBankAccountCO.setVenNickOid(vendorInfo.getVendorCode());
            if (StatusEnum.DISABLE == StatusEnum.parse(vendorInfo.getStatus())) {
                throw new BizException(RespCode.SUPPLIER_DISABLED);
            } else {
                validateBankAccountDuplication(vendorBankAccountCO);
                List<VendorBankAccount> vendorBankAccountList = baseMapper.selectVendorBankAccountsByConditions(vendorBankAccountCO.getVenInfoId(), Boolean.TRUE, StatusEnum.ENABLE.getId());
                if (GO.equals(vendorBankAccountCO.getConstraintFlag())) {
                    if (!CollectionUtils.isEmpty(vendorBankAccountList)) {
                        vendorBankAccountList.forEach(vendorBankAccount -> {
                            if (vendorBankAccountCO.getId() == null || !vendorBankAccount.getId().equals(vendorBankAccountCO.getId())) {
                                vendorBankAccount.setPrimaryFlag(Boolean.FALSE);
                                vendorBankAccount.setVendorBankStatus(VendorStatusEnum.EDITOR.getVendorStatus());
                                super.updateById(vendorBankAccount);
                            }
                        });
                    }
                } else if (vendorBankAccountCO.getPrimaryFlag() && StatusEnum.ENABLE == StatusEnum.parse(vendorInfo.getStatus())) {
                    boolean check = !CollectionUtils.isEmpty(vendorBankAccountList) && (vendorBankAccountList.size() > 1 || vendorBankAccountCO.getId() == null || !vendorBankAccountList.get(0).getId().equals(vendorBankAccountCO.getId()));
                    if (check) {
                        vendorBankAccountCO.setConstraintFlag(FALSE);
                        vendorBankAccountCO.setMesBankName(vendorBankAccountList.get(0).getBankName());
                        vendorBankAccountCO.setMesVenBankNumberName(vendorBankAccountList.get(0).getVenBankNumberName());
                        throw new BizException(RespCode.SUPPLIER_ENABLED_MASTER_ACCOUNT_EXISTS);
                    }
                }
                VendorBankAccount vendorBankAccount = VendorBankAccountAdapter.vendorBankAccountCOToVendorBankAccount(vendorBankAccountCO);
                // 中控上面维护供应商银行账号，地址即为开户行城市[和产品 客户确认过，这是以前老供应商逻辑]
                // 表单上单独维护供应商银行账号，地址即为开户行城市
//                        vendorBankAccount.setBankAddress(vendorBankAccountCO.getBankOpeningCity());
                if (vendorBankAccountCO.getId() == null) {
                    vendorBankAccount.setVendorBankStatus(VendorStatusEnum.EDITOR.getVendorStatus());
                    baseMapper.insert(vendorBankAccount);
                } else {
                    vendorBankAccount.setVendorBankStatus(VendorStatusEnum.EDITOR.getVendorStatus());
                    // isDeleted isEnable 没有用到，也没删除
                    super.updateById(vendorBankAccount);
                }
                result = VendorBankAccountAdapter.vendorBankAccountToVendorBankAccountCO(baseMapper.selectById(vendorBankAccount.getId()));

            }

            if (result != null) {
                // 维护供应商银行账号信息成功后，需要同步供应商的最后修改人信息
                // vendorInfo.setLastUpdatedByEmployeeId(vendorBankAccountCO.getVenOperatorNumber());
                // vendorInfo.setLastUpdatedByName(vendorBankAccountCO.getVenOperatorName());
                vendorInfo.setLastUpdatedBy(currentUserId);
                vendorInfo.setLastUpdatedDate(ZonedDateTime.now());
                vendorInfoMapper.updateById(vendorInfo);
            }
        }
        return result;
    }

    public Map<String,String> createOrUpdateVendorBankAccountBatch(List<VendorBankAccountCO> vendorBankAccountCOList) {
            Map<String,String> map = new HashMap<>(16);
        for (int i = 0; i < vendorBankAccountCOList.size(); i++) {
            VendorBankAccountCO t = vendorBankAccountCOList.get(i);
            if (t.getVenBankNumberName()==null||t.getBankAccount()==null||t.getBankCode()==null){
                map.put("第"+(i+1)+"行数据","银行");
            }
        }
        vendorBankAccountCOList.forEach(t->{

            VendorBankAccountCO result;
            VendorInfo vendorInfo = vendorInfoMapper.selectById(t.getVenInfoId());
            if (vendorInfo == null) {
                throw new BizException(RespCode.SUPPLIER_NOT_EXISTS);
            } else {
                validateVendorBankAccountCO(t);
                Long currentUserId = OrgInformationUtil.getCurrentUserId();
                t.setVenInfoName(vendorInfo.getVendorName());
                // 供应商编码替代了供应商标识
                t.setVenNickOid(vendorInfo.getVendorCode());
                if (StatusEnum.DISABLE == StatusEnum.parse(vendorInfo.getStatus())) {
                    throw new BizException(RespCode.SUPPLIER_DISABLED);
                } else {
                    validateBankAccountDuplication(t);
                    List<VendorBankAccount> vendorBankAccountList = baseMapper.selectVendorBankAccountsByConditions(t.getVenInfoId(), Boolean.TRUE, StatusEnum.ENABLE.getId());
                    if (GO.equals(t.getConstraintFlag())) {
                        if (!CollectionUtils.isEmpty(vendorBankAccountList)) {
                            vendorBankAccountList.forEach(vendorBankAccount -> {
                                if (t.getId() == null || !vendorBankAccount.getId().equals(t.getId())) {
                                    vendorBankAccount.setPrimaryFlag(Boolean.FALSE);
                                    vendorBankAccount.setVendorBankStatus(VendorStatusEnum.EDITOR.getVendorStatus());
                                    super.updateById(vendorBankAccount);
                                }
                            });
                        }
                    } else if (t.getPrimaryFlag() && StatusEnum.ENABLE == StatusEnum.parse(vendorInfo.getStatus())) {
                        boolean check = !CollectionUtils.isEmpty(vendorBankAccountList) && (vendorBankAccountList.size() > 1 || t.getId() == null || !vendorBankAccountList.get(0).getId().equals(t.getId()));
                        if (check) {
                            t.setConstraintFlag(FALSE);
                            t.setMesBankName(vendorBankAccountList.get(0).getBankName());
                            t.setMesVenBankNumberName(vendorBankAccountList.get(0).getVenBankNumberName());
                            throw new BizException(RespCode.SUPPLIER_ENABLED_MASTER_ACCOUNT_EXISTS);
                        }
                    }
                    VendorBankAccount vendorBankAccount = VendorBankAccountAdapter.vendorBankAccountCOToVendorBankAccount(t);
                    // 中控上面维护供应商银行账号，地址即为开户行城市[和产品 客户确认过，这是以前老供应商逻辑]
                    // 表单上单独维护供应商银行账号，地址即为开户行城市
                    if (t.getId() == null) {
                        vendorBankAccount.setVendorBankStatus(VendorStatusEnum.EDITOR.getVendorStatus());
                        baseMapper.insert(vendorBankAccount);
                    } else {
                        vendorBankAccount.setVendorBankStatus(VendorStatusEnum.EDITOR.getVendorStatus());
                        // isDeleted isEnable 没有用到，也没删除
                        super.updateById(vendorBankAccount);
                    }
                    result = VendorBankAccountAdapter.vendorBankAccountToVendorBankAccountCO(baseMapper.selectById(vendorBankAccount.getId()));

                }

                if (result != null) {
                    // 维护供应商银行账号信息成功后，需要同步供应商的最后修改人信息
                    // vendorInfo.setLastUpdatedByEmployeeId(vendorBankAccountCO.getVenOperatorNumber());
                    // vendorInfo.setLastUpdatedByName(vendorBankAccountCO.getVenOperatorName());
                    vendorInfo.setLastUpdatedBy(currentUserId);
                    vendorInfo.setLastUpdatedDate(ZonedDateTime.now());
                    vendorInfoMapper.updateById(vendorInfo);
                }
            }
        });
        return map;
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Page<VendorBankAccountCO> searchVendorBankAccounts(String vendorInfoId, Integer status, Pageable pageable) {
        Page<VendorBankAccount> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize(), "bank_code");
        page.setAsc(true);
        page.setRecords(baseMapper.selectVendorBankAccountsByPages(vendorInfoId, status, page));
        List<VendorBankAccountCO> vendorBankAccountCOs = page.getRecords().stream().map(VendorBankAccountAdapter::vendorBankAccountToVendorBankAccountCO).collect(Collectors.toList());
        Page<VendorBankAccountCO> result = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        result.setRecords(vendorBankAccountCOs);
        result.setTotal(page.getTotal());
        return result;
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public List<VendorBankAccountCO> searchVendorBankAccounts(String vendorInfoId) {
        List<VendorBankAccountCO> result = new ArrayList<>();
        VendorInfo vendorInfo = vendorInfoMapper.selectById(vendorInfoId);
        if (vendorInfo == null) {
            throw new BizException(RespCode.SUPPLIER_NOT_EXIST);
        } else {
            List<VendorBankAccount> vendorBankAccounts = baseMapper.selectVendorBankAccountsByVendorInfoIdAndStatusAndVendorBankStatus(vendorInfoId);
            result = vendorBankAccounts.stream().map(vendorBankAccount -> {
                VendorBankAccountCO vendorBankAccountCO = VendorBankAccountAdapter.vendorBankAccountToVendorBankAccountCO(vendorBankAccount);
                vendorBankAccountCO.setVenInfoName(vendorInfo.getVendorName());
                // 供应商编码替换供应商标识，供应商标识废弃
                vendorBankAccountCO.setVenNickOid(vendorInfo.getVendorCode());
                // 兼容artemis中使用的supplier中废弃字段
                vendorBankAccountCO.setBankOpeningBank(vendorBankAccount.getBankName());
                vendorBankAccountCO.setBankOperatorNumber(vendorBankAccount.getBankCode());
                return vendorBankAccountCO;
            }).collect(Collectors.toList());
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public List<VendorBankAccountCO> listVendorBankAccounts(List<String> vendorInfoIds) {
//        return vendorInfoMapper.selectBatchIds(vendorInfoIds).stream().map(vendorInfo ->
//                        baseMapper.selectVendorBankAccountsByCompanyOidAndVendorInfoId(null, vendorInfo.getId().toString()).stream().map(vendorBankAccount -> {
//                            VendorBankAccountCO vendorBankAccountCO = VendorBankAccountAdapter.vendorBankAccountToVendorBankAccountCO(vendorBankAccount);
//                            vendorBankAccountCO.setVenInfoName(vendorInfo.getVendorName());
//                            // 供应商编码替换供应商标识，供应商标识废弃
//                            vendorBankAccountCO.setVenNickOid(vendorInfo.getVendorCode());
//                            // 兼容artemis中使用的supplier中废弃字段
//                            vendorBankAccountCO.setBankOpeningBank(vendorBankAccount.getBankName());
//                            vendorBankAccountCO.setBankOperatorNumber(vendorBankAccount.getBankCode());
//                            return vendorBankAccountCO;
//                        }).collect(Collectors.toList())
//                ).collect(Collectors.toList());

        List<VendorBankAccountCO> list = new ArrayList<>();
        List<VendorInfo> vendorInfos = vendorInfoMapper.selectBatchIds(vendorInfoIds);
        if (vendorInfos != null) {
            vendorInfos.stream().forEach(vendorInfo -> {
                baseMapper.selectVendorBankAccountsByCompanyOidAndVendorInfoId(null, vendorInfo.getId().toString()).stream().forEach(vendorBankAccount -> {
                    VendorBankAccountCO vendorBankAccountCO = VendorBankAccountAdapter.vendorBankAccountToVendorBankAccountCO(vendorBankAccount);
                    vendorBankAccountCO.setVenInfoName(vendorInfo.getVendorName());
                    // 供应商编码替换供应商标识，供应商标识废弃
                    vendorBankAccountCO.setVenNickOid(vendorInfo.getVendorCode());
                    // 兼容artemis中使用的supplier中废弃字段
                    vendorBankAccountCO.setBankOpeningBank(vendorBankAccount.getBankName());
                    vendorBankAccountCO.setBankOperatorNumber(vendorBankAccount.getBankCode());
                    list.add(vendorBankAccountCO);
                });
            });
        }
        return list;
    }

    public void validateBankAccountsDuplication(List<VendorBankAccountCO> vendorBankAccountCOs) {
        StringBuilder msg = new StringBuilder();
        if (!CollectionUtils.isEmpty(vendorBankAccountCOs)) {
            for (VendorBankAccountCO vendorBankAccountCO : vendorBankAccountCOs) {
                validateBankAccountDuplication(vendorBankAccountCO);
            }
        }
        if (StringUtils.isNotBlank(msg.toString())) {
            throw new BizException(RespCode.SUPPLIER_BANK_ACCOUNT_WAS_OCCUPIED);
        }
    }

    public List<VendorBankAccountCO> createOrUpdateVendorBankAccounts(List<VendorBankAccountCO> vendorBankAccountCOs, String operate) {
        List<VendorBankAccountCO> vendorBankAccountCOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(vendorBankAccountCOs)) {
            for (VendorBankAccountCO vendorBankAccountCO : vendorBankAccountCOs) {
                VendorBankAccount vendorBankAccount = VendorBankAccountAdapter.vendorBankAccountCOToVendorBankAccount(vendorBankAccountCO);
                if (Constants.OPERATE_INSERT.equals(operate)) {
                    vendorBankAccount.setCreatedBy(0L);
                    setBaseData(vendorBankAccount);
                    baseMapper.insert(vendorBankAccount);
                } else if (Constants.OPERATE_UPDATE.equals(operate)) {
                    List<VendorBankAccount> vendorBankAccounts = baseMapper.selectVendorBankAccountsByBankAccountAndVendorInfoId(vendorBankAccountCO.getBankAccount(), vendorBankAccountCO.getVenInfoId());
                    if (CollectionUtils.isEmpty(vendorBankAccounts)) {
                        vendorBankAccount.setCreatedBy(0L);
                        setBaseData(vendorBankAccount);
                        baseMapper.insert(vendorBankAccount);
                    } else {
                        vendorBankAccount.setId(vendorBankAccounts.get(0).getId());
                        vendorBankAccount.setLastUpdatedDate(ZonedDateTime.now());
                        setBaseData(vendorBankAccount);
                        // isDeleted isEnable 没有用到，也没删除
                        super.updateById(vendorBankAccount);
                    }
                }
                vendorBankAccountCOList.add(VendorBankAccountAdapter.vendorBankAccountToVendorBankAccountCO(baseMapper.selectById(vendorBankAccount.getId())));
            }
        }
        return vendorBankAccountCOList;
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public List<VendorBankAccountCO> listVendorBankAccountsByBankAccount(String bankAccount) {
        return baseMapper.selectList(new EntityWrapper<VendorBankAccount>().eq("bank_account", bankAccount)).stream().map(VendorBankAccountAdapter::vendorBankAccountToVendorBankAccountCO).collect(Collectors.toList());
    }

    public List<VendorBankAccountCO> createVendorBankAccountsByBill(List<VendorBankAccountCO> vendorBankAccountCOs, UserDTO currentUser) {
        List<VendorBankAccountCO> vendorBankAccountCOList = new ArrayList<>();
        for (VendorBankAccountCO vendorBankAccountCO : vendorBankAccountCOs) {
            VendorBankAccount vendorBankAccount = VendorBankAccountAdapter.vendorBankAccountCOToVendorBankAccount(vendorBankAccountCO);
            // 表单上维护银行账号信息，开户行所在地和银行开户行城市一致
            vendorBankAccount.setBankAddress(vendorBankAccountCO.getBankOpeningCity());
            vendorBankAccount.setCreatedBy(currentUser.getId());
            vendorBankAccount.setLastUpdatedBy(currentUser.getId());
            vendorBankAccount.setCompanyOid(currentUser.getCompanyOid().toString());
            if (vendorBankAccount.getStatus() == null) {
                vendorBankAccount.setStatus(StatusEnum.ENABLE.getId());
            }
            baseMapper.insert(vendorBankAccount);
            vendorBankAccountCOList.add(VendorBankAccountAdapter.vendorBankAccountToVendorBankAccountCO(baseMapper.selectById(vendorBankAccount.getId())));
        }
        return vendorBankAccountCOList;
    }

    /**
     * 租户级别，同一个供应商下，银行账号不能重复
     *
     * @param vendorBankAccountCO
     * @return
     */
    private void validateBankAccountDuplication(VendorBankAccountCO vendorBankAccountCO) {
        List<VendorBankAccount> vendorBankAccounts = baseMapper.selectVendorBankAccountsByBankAccountAndVendorInfoId(vendorBankAccountCO.getBankAccount(), vendorBankAccountCO.getVenInfoId());
        boolean check = !CollectionUtils.isEmpty(vendorBankAccounts) && (vendorBankAccounts.size() > 1 || vendorBankAccountCO.getId() == null || !vendorBankAccounts.get(0).getId().equals(vendorBankAccountCO.getId()));
        if (check) {
            throw new BizException(RespCode.SUPPLIER_ACCOUNT_IN_USED);
        }
    }

    private void setBaseData(VendorBankAccount vendorBankAccount) {
        vendorBankAccount.setLastUpdatedBy(0L);
        if (vendorBankAccount.getStatus() == null) {
            vendorBankAccount.setStatus(StatusEnum.ENABLE.getId());
        }
    }

    public void validateVendorBankAccountCO(VendorBankAccountCO vendorBankAccountCO) {
        MyStringUtils.deleteStringTypeFieldTrim(vendorBankAccountCO);
        if (StringUtils.isBlank(vendorBankAccountCO.getBankName())) {
            throw new BizException(RespCode.SUPPLIER_BANK_NAME_EMPTY);
        }
        if (StringUtils.isBlank(vendorBankAccountCO.getVenBankNumberName())) {
            throw new BizException(RespCode.SUPPLIER_BANK_ACCOUNT_EMPTY);
        }
        if (StringUtils.isBlank(vendorBankAccountCO.getBankAccount()) || !bankAccountPattern.matcher(vendorBankAccountCO.getBankAccount()).matches()) {
            throw new BizException(RespCode.SUPPLIER_ACCOUNT__INOUT_ERROR);
        }
    }

    /**
     * 根据租户id和供应商名称,代码【模糊】分页查询 获取供应商银行信息
     * @param name
     * @param code
     * @param queryPage
     * @return
     */
    public List<VendorAccountDTO> getReceivablesByNameAndCode(String name, String code, Page queryPage) {
        List<VendorInfo> vendorInfos = vendorInfoMapper
                .selectVendorInfosByTenantIdAndVendorNameAndCodeForPage(OrgInformationUtil.getCurrentTenantId(), name, code, queryPage);
        List<VendorAccountDTO> result = new ArrayList<>(32);
        vendorInfos.stream().forEach(vendorInfo -> {
            VendorAccountDTO vendorAccountDTO = new VendorAccountDTO();
            vendorAccountDTO.setId(vendorInfo.getId());
            vendorAccountDTO.setCode(vendorInfo.getVendorCode());
            vendorAccountDTO.setName(vendorInfo.getVendorName());
            vendorAccountDTO.setIsEmp(false);
            vendorAccountDTO.setSign(vendorAccountDTO.getId() + "_" + vendorAccountDTO.getIsEmp());
            List<VendorBankAccount> vendorBankAccounts = vendorBankAccountMapper.selectVendorBankAccountsByCompanyOidAndVendorInfoId(null, vendorInfo.getId().toString());
            List<BankAccountDTO> bankInfos = vendorBankAccounts.stream().map(vendorBankAccount -> {
                BankAccountDTO bankAccount = new BankAccountDTO();
                bankAccount.setAccount(vendorBankAccount.getBankAccount());
                bankAccount.setBankAccountName(vendorBankAccount.getVenBankNumberName());
                bankAccount.setBankCode(vendorBankAccount.getBankCode());
                bankAccount.setBankName(vendorBankAccount.getBankName());
                bankAccount.setPrimary(vendorBankAccount.getPrimaryFlag());
                return bankAccount;
            }).collect(Collectors.toList());
            vendorAccountDTO.setBankInfos(bankInfos);
            result.add(vendorAccountDTO);
        });

        return result;
    }

    /**
     * 查询某公司下的、启用状态为启用的、审核状态为审核通过的租户下的供应商
     * @param companyId
     * @param name
     * @param code
     * @param queryPage
     * @return
     */
    public List<VendorAccountDTO> getVendorByCompanyIdAndNameAndCode(
            Long companyId, Boolean enabled, String name, String code, Page queryPage) {
        List<VendorInfo> vendorInfos = vendorInfoMapper
                .selectVendorInfosByTenantIdCompanyIdAndVendorNameAndCodeForPage(
                        OrgInformationUtil.getCurrentTenantId(),companyId, enabled, name, code, queryPage);
        List<VendorAccountDTO> result = new ArrayList<>(32);
        vendorInfos.stream().forEach(vendorInfo -> {
            VendorAccountDTO vendorAccountDTO = new VendorAccountDTO();
            vendorAccountDTO.setId(vendorInfo.getId());
            vendorAccountDTO.setCode(vendorInfo.getVendorCode());
            vendorAccountDTO.setName(vendorInfo.getVendorName());
            vendorAccountDTO.setIsEmp(false);
            vendorAccountDTO.setSign(vendorAccountDTO.getId() + "_" + vendorAccountDTO.getIsEmp());
            List<VendorBankAccount> vendorBankAccounts = vendorBankAccountMapper.selectVendorBankAccountsByVendorInfoIdAndStatusAndVendorBankStatus(vendorInfo.getId().toString());
            List<BankAccountDTO> bankInfos = vendorBankAccounts.stream().map(vendorBankAccount -> {
                BankAccountDTO bankAccount = new BankAccountDTO();
                bankAccount.setAccount(vendorBankAccount.getBankAccount());
                bankAccount.setBankAccountName(vendorBankAccount.getVenBankNumberName());
                bankAccount.setBankCode(vendorBankAccount.getBankCode());
                bankAccount.setBankName(vendorBankAccount.getBankName());
                bankAccount.setPrimary(vendorBankAccount.getPrimaryFlag());
                return bankAccount;
            }).collect(Collectors.toList());
                vendorAccountDTO.setBankInfos(bankInfos);
                result.add(vendorAccountDTO);
        });

        return result;
    }



    public VendorBankAccountCO operationVendor(VendorBankAccountCO vendorBankAccountCO, String roleType, String action) {
        VendorBankAccount vendorBankAccount = VendorBankAccountAdapter.vendorBankAccountCOToVendorBankAccount(vendorBankAccountCO);
        //提交校验
        if (action.equals(VendorStatusEnum.PENGDING.getVendorStatus())&&(vendorBankAccount.equals(VendorStatusEnum.PENGDING.getVendorStatus())||vendorBankAccount.equals(VendorStatusEnum.PENGDING.getVendorStatus()))){
            throw new BizException(RespCode.SUPPLIER_VENDOR_STATUS);
        }
        //APPROVED(审批通过)REFUSE(审批驳回)
        if ((action.equals(VendorStatusEnum.APPROVED.getVendorStatus())||(action.equals(VendorStatusEnum.APPROVED.getVendorStatus()))&&(!vendorBankAccount.equals(VendorStatusEnum.APPROVED.getVendorStatus())))){
            throw new BizException(RespCode.SUPPLIER_VENDOR_STATUS);
        }
        vendorBankAccount.setVendorBankStatus(action);
        vendorBankAccount.setLastUpdatedBy(vendorBankAccount.getLastUpdatedBy());
        vendorBankAccount.setLastUpdatedDate(ZonedDateTime.now());
        baseMapper.updateById(vendorBankAccount);
        return vendorBankAccountCO;
    }
}
