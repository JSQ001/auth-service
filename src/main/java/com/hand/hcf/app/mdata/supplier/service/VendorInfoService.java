package com.hand.hcf.app.mdata.supplier.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.OrderNumberCO;
import com.hand.hcf.app.common.dto.VendorBankAccountCO;
import com.hand.hcf.app.common.dto.VendorInfoCO;
import com.hand.hcf.app.common.enums.SourceEnum;
import com.hand.hcf.app.mdata.bank.domain.BankInfo;
import com.hand.hcf.app.mdata.bank.dto.ReceivablesDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.supplier.constants.Constants;
import com.hand.hcf.app.mdata.supplier.domain.VendorBankAccount;
import com.hand.hcf.app.mdata.supplier.domain.VendorInfo;
import com.hand.hcf.app.mdata.supplier.enums.StatusEnum;
import com.hand.hcf.app.mdata.supplier.persistence.VendorBankAccountMapper;
import com.hand.hcf.app.mdata.supplier.persistence.VendorInfoMapper;
import com.hand.hcf.app.mdata.supplier.web.adapter.VendorBankAccountAdapter;
import com.hand.hcf.app.mdata.supplier.web.adapter.VendorInfoAdapter;
import com.hand.hcf.app.mdata.supplier.web.dto.CompanyDTO;
import com.hand.hcf.app.mdata.utils.MyStringUtils;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.core.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hand.hcf.app.mdata.supplier.constants.Constants.*;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/11 17:54
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VendorInfoService extends BaseService<VendorInfoMapper, VendorInfo> {

    @Autowired
    private VendorBankAccountMapper vendorBankAccountMapper;

    @Autowired
    private VendorBankAccountService vendorBankAccountService;

    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;

    @Autowired
    private RelationVendorCompanyService relationVendorCompanyService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private CompanyService companyService;

    private static final Logger LOGGER = LoggerFactory.getLogger(VendorInfoService.class);

    private static final Pattern vendorCodePattern = Pattern.compile("^[\\\\0-9A-Za-z._-]{1,50}$");

    private static final Pattern pattern1 = Pattern.compile("^[A-Za-z\\u4e00-\\u9fa5]{1,30}$");

    private static final Pattern pattern2 = Pattern.compile("^[0-9A-Za-z]{1,30}$");

    private static final Pattern mobilePattern = Pattern.compile("^((\\d{3,4}-)?\\d{7,8}|1\\d{10})$");

    private static final Pattern emailPattern = Pattern.compile("^[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?$");

    private static final Pattern faxPattern = Pattern.compile("^([-0-9]{1,20})$");

    private static final String SUCCEED = "0000";


    public VendorInfoCO insertOrUpdateVendorInfo(VendorInfoCO vendorInfoCO, String roleType) {
        validateVendorInfoCO(vendorInfoCO);
        VendorInfo oldVendorInfo;
        if (vendorInfoCO.getId() != null) {
            // 公司权限下，是无法修改租户级的供应商信息
            if (!Constants.TENANT_LEVEL.equals(roleType) && SourceEnum.TENANT == vendorInfoCO.getSource()) {
                throw new BizException(RespCode.SUPPLIER_LACK_OF_COMPANY_AUTHORITY);
            }
            oldVendorInfo = baseMapper.selectById(vendorInfoCO.getId());
            if (oldVendorInfo == null) {
                throw new BizException(RespCode.SUPPLIER_NOT_EXISTS);
            }
        }
        String companyOid = OrgInformationUtil.getCurrentCompanyOid().toString();
        Long tenantId = Long.valueOf(OrgInformationUtil.getCurrentTenantId());
        // 校验供应商编码重复，租户下全局校验，不区分租户级或公司级供应商[自动编码规则启用，其他处已校验，故该处不用校验]
        List<VendorInfo> vendorInfos = null;
        if (vendorInfoCO.getAutoCodeMark() == null || !vendorInfoCO.getAutoCodeMark()) {
            vendorInfos = baseMapper.selectVendorInfosByVendorCodeAndTenantId(vendorInfoCO.getVenderCode(), tenantId);
        }
        if (!CollectionUtils.isEmpty(vendorInfos)) {
            boolean validateVendorCodeDuplicationFlag = vendorInfoCO.getId() == null || (vendorInfos.size() > 1 || !vendorInfos.get(0).getId().equals(vendorInfoCO.getId()));
            if (validateVendorCodeDuplicationFlag) {
                throw new BizException(RespCode.SUPPLIER_CODE_EXISTS);
            }
        }
        // 校验供应商名称重复，维护租户级供应商，仅在租户级供应商校验；维护公司级供应商，在当前公司级和租户级校验，和供应商编码校验规则不同
        List<VendorInfo> vendorInfoList = baseMapper.selectVendorInfosByCondtions(tenantId, companyOid, vendorInfoCO.getVenNickname(), null, vendorInfoCO.getSource().toString());
        if (!CollectionUtils.isEmpty(vendorInfoList)) {
            validateVendorInfoNameAndCode(vendorInfoCO, vendorInfoList, RespCode.SUPPLIER_NAME_EXISTS, RespCode.SUPPLIER_NAME_EXISTS_IN_COMPANY_OR_TENANT_LEVEL);
        }

            VendorInfo vendorInfo = VendorInfoAdapter.vendorInfoCOToVendorInfo(vendorInfoCO);
            vendorInfo.setVendorCode(vendorInfoCO.getVenderCode());
            vendorInfo.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
            if (vendorInfoCO.getId() != null) {
                vendorInfo.setLastUpdatedDate(ZonedDateTime.now());
                super.updateById(vendorInfo);
            } else {
                vendorInfo.setTenantId(tenantId);
                vendorInfo.setCompanyOid(companyOid);
                vendorInfo.setCreatedBy(vendorInfo.getLastUpdatedBy());
                baseMapper.insert(vendorInfo);
            }
        VendorInfoCO result = VendorInfoAdapter.vendorInfoToVendorInfoCO(baseMapper.selectById(vendorInfo.getId()));
        return result;
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Page<VendorInfoCO> searchVendorInfos(Long venderTypeId, String venderCode, String venNickname, String bankAccount, Integer venType, String roleType, Pageable pageable) {
        String companyOid = OrgInformationUtil.getCurrentCompanyOid().toString();
        String companyId = OrgInformationUtil.getCurrentCompanyId().toString();
        String tenantId = OrgInformationUtil.getCurrentTenantId().toString();
        venderCode = MyStringUtils.formatFuzzyQuery(venderCode);
        venNickname = MyStringUtils.formatFuzzyQuery(venNickname);
        bankAccount = MyStringUtils.formatFuzzyQuery(bankAccount);
        Page<VendorInfo> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        List<VendorInfo> vendorInfos;
        if (StringUtils.isNotBlank(roleType) && TENANT_LEVEL.equals(roleType)) {
            // 租户级别操作，获取全局的供应商信息
            vendorInfos = baseMapper.selectVendorInfosByPage(venderTypeId, venderCode, venNickname, bankAccount, venType, tenantId, page);
        } else {
            // 公司级别操作，获取已分配公司的供应商信息以及公司级供应商信息
            vendorInfos = baseMapper.selectVendorInfosByRelationCompanyForPage(venderTypeId, venderCode, venNickname, bankAccount, venType, companyId, companyOid, page);
        }
        page.setRecords(vendorInfos);
        List<VendorInfoCO> vendorInfoCOs = page.getRecords().stream().map(VendorInfoAdapter::vendorInfoToVendorInfoCO).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(vendorInfoCOs)) {
            for (VendorInfoCO vendorInfoCO : vendorInfoCOs) {
                vendorInfoCO.setVenBankAccountBeans(
                        vendorBankAccountMapper.selectVendorBankAccountsByCompanyOidAndVendorInfoId(null, vendorInfoCO.getId().toString()).stream().map(VendorBankAccountAdapter::vendorBankAccountToVendorBankAccountCO).collect(Collectors.toList())
                );
            }
        }
        Page<VendorInfoCO> result = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        result.setRecords(vendorInfoCOs);
        result.setTotal(page.getTotal());
        return result;
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public VendorInfoCO searchVendorInfoByOne(Long id) {
        VendorInfoCO vendorInfoCO ;
        VendorInfo vendorInfo = baseMapper.selectById(id);
        if (vendorInfo == null) {
            throw new BizException(RespCode.SUPPLIER_NOT_EXISTS);
        } else {
            vendorInfoCO = VendorInfoAdapter.vendorInfoToVendorInfoCO(vendorInfo);
        }
        return vendorInfoCO;
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Page<VendorInfoCO> pageVendorInfos(String companyOid, Long companyId, Long tenantId, Long startDate, Long endDate, int page,int size) {
        ZonedDateTime startDateTime = ZonedDateTime.of(LocalDateTime.ofEpochSecond(startDate / 1000, 0, ZoneOffset.UTC), ZoneId.of("UTC"));
        ZonedDateTime endDateTime = ZonedDateTime.of(LocalDateTime.ofEpochSecond(endDate / 1000, 0, ZoneOffset.UTC), ZoneId.of("UTC"));
        Page<VendorInfo> pageo = new Page<>(page, size);
        List<VendorInfo> vendorInfos;
        if (tenantId != null) {
            // 获取租户级供应商
            vendorInfos = baseMapper.selectVendorInfosByTenantIdAndlastUpdatedDate(tenantId, startDateTime, endDateTime, pageo);
        } else {
            // 获取已分配公司的供应商信息以及公司级供应商信息
            vendorInfos = baseMapper.selectVendorInfosByRelationCompany(companyOid, companyId, startDateTime, endDateTime, pageo);
        }
        pageo.setRecords(vendorInfos);
        List<VendorInfoCO> vendorInfoCOs = pageo.getRecords().stream().map(VendorInfoAdapter::vendorInfoToVendorInfoCO).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(vendorInfoCOs)) {
            for (VendorInfoCO vendorInfoCO : vendorInfoCOs) {
                vendorInfoCO.setVenBankAccountBeans(
                        vendorBankAccountMapper.selectVendorBankAccountsByCompanyOidAndVendorInfoId(null, vendorInfoCO.getId().toString()).stream().map(VendorBankAccountAdapter::vendorBankAccountToVendorBankAccountCO).collect(Collectors.toList())
                );
            }
        }

        Page<VendorInfoCO> result = new Page<>(page + 1,size);
        result.setRecords(vendorInfoCOs);
        result.setTotal(pageo.getTotal());
        return result;

    }

    public VendorInfoCO insertOrUpdateVendorInfoByArtemis(VendorInfoCO vendorInfoCO, String operate) {
        final VendorInfo vendorInfo;
        Long currentTenantId = vendorInfoCO.getTenantId();
        String currentCompanyOid = vendorInfoCO.getCompanyOid();
        SourceEnum source = vendorInfoCO.getSource();
        List<VendorBankAccountCO> vendorBankAccountCOs = vendorInfoCO.getVenBankAccountBeans();
        boolean existVendorBankAccountCOsFlag = !CollectionUtils.isEmpty(vendorBankAccountCOs);
        if (existVendorBankAccountCOsFlag && vendorBankAccountCOs.stream().map(VendorBankAccountCO::getBankAccount).collect(Collectors.toSet()).size() != vendorBankAccountCOs.size()) {
            throw new BizException(RespCode.SUPPLIER_BANK_ACCOUNT_REPEAT);
        }
        if (StringUtils.isBlank(vendorInfoCO.getVenNickname())) {
            throw new BizException(RespCode.SUPPLIER_NOT_EXIST);
        }
        if (OPERATE_INSERT.equals(operate)) {
            List<VendorInfo> vendorInfos = baseMapper.selectVendorInfosByCondtions(currentTenantId, currentCompanyOid, vendorInfoCO.getVenNickname(), null, source.toString());
            if (!CollectionUtils.isEmpty(vendorInfos)) {
                throw new BizException(RespCode.SUPPLIER_NAME_EXISTS);
            }
            if (StringUtils.isNotBlank(vendorInfoCO.getVenNickOid()) && !CollectionUtils.isEmpty(baseMapper.selectVendorInfosByVendorCodeAndTenantId(vendorInfoCO.getVenNickOid(), currentTenantId))) {
                throw new BizException(RespCode.SUPPLIER_CODE_EXISTS);
            }
            vendorInfo = VendorInfoAdapter.vendorInfoCOToVendorInfo(vendorInfoCO);
            vendorInfo.setCreatedBy(0L);
            setBaseData(vendorInfo);
            baseMapper.insert(vendorInfo);
        } else if (OPERATE_UPDATE.equals(operate)) {
            // openApi 更新供应商信息，以供应商标识为基准，无主键标识
            if (StringUtils.isBlank(vendorInfoCO.getVenNickOid())) {
                throw new BizException(RespCode.SUPPLIER_MARK_NOT_EXIST);
            }
            // 供应商标识即编码，是当前租户下唯一的，通过openApi修改 只能是租户级改租户级供应商；公司级改公司级供应商[租户权限不能改公司级供应商，反之亦然]
            List<VendorInfo> vendorInfoList = baseMapper.selectVendorInfosByVendorCodeForArtemis(currentTenantId, currentCompanyOid, vendorInfoCO.getVenNickOid(), source.toString());
            if (CollectionUtils.isEmpty(vendorInfoList)) {
                throw new BizException(RespCode.SUPPLIER_MARK_NOT_EXIST);
            }
            if (vendorInfoList.size() > 1) {
                throw new BizException(RespCode.SUPPLIER_MULTIPLE_ID_DATABASES);
            }
            List<VendorInfo> vendorInfos = baseMapper.selectVendorInfosByCondtions(currentTenantId, currentCompanyOid, vendorInfoCO.getVenNickname(), null, source.toString());
            if (!CollectionUtils.isEmpty(vendorInfos)) {
                if (vendorInfos.size() > 1) {
                    throw new BizException(RespCode.SUPPLIER_MULTIPLE_NAME_DATABASES);
                } else if (!vendorInfoCO.getVenNickOid().equals(vendorInfos.get(0).getVendorCode())) {
                    throw new BizException(RespCode.SUPPLIER_NAME_EXISTS);
                }
            }
            vendorInfo = VendorInfoAdapter.vendorInfoCOToVendorInfo(vendorInfoCO);
            vendorInfo.setId(vendorInfoList.get(0).getId());
            setBaseData(vendorInfo);
            vendorInfo.setLastUpdatedDate(ZonedDateTime.now());
            super.updateById(vendorInfo);
        } else {
            vendorInfo = null;
        }
        if (vendorInfo == null) {
            throw new BizException(RespCode.SUPPLIER_CALL_METHOD_ERROR);
        } else {
            VendorInfoCO result = VendorInfoAdapter.vendorInfoToVendorInfoCO(baseMapper.selectById(vendorInfo.getId()));
            if (existVendorBankAccountCOsFlag) {
                vendorBankAccountCOs.forEach(v -> {
                    v.setVenInfoId(vendorInfo.getId().toString());
                    v.setCompanyOid(vendorInfo.getCompanyOid());
                    v.setVenNickOid(vendorInfo.getVendorCode());
                    // 初始化银行账号信息中银行户名默认为供应商名称;银行名称为银行开户行;银行编码为银行开户行号;
                    v.setVenBankNumberName(vendorInfoCO.getVenNickname());
                    v.setBankName(v.getBankOpeningBank());
                    v.setBankCode(v.getBankOperatorNumber());
                });
                result.setVenBankAccountBeans(vendorBankAccountService.createOrUpdateVendorBankAccounts(vendorBankAccountCOs, operate));
            }
            return result;
        }
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public List<VendorInfoCO> listVendorInfosByIds(List<String> ids) {
        List<VendorInfoCO> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(ids)) {
            list = baseMapper.selectBatchIds(ids).stream().map(VendorInfoAdapter::vendorInfoToVendorInfoCO).collect(Collectors.toList());
        }
        return list;
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Page<VendorInfoCO> pageVendorInfosByConditions(VendorInfoCO vendorInfoCO) {
        String companyOid = vendorInfoCO.getCompanyOid();
        if (StringUtils.isBlank(companyOid)) {
            LOGGER.debug("method getVendorInfosByConditions companyOid is null");
            throw new BizException(RespCode.SUPPLIER_NOT_EXIST);
        }
        Long companyId;
        try {
            companyId = companyService.getByCompanyOid(companyOid).getId();
        } catch (Exception e) {
            LOGGER.error("Get companyId by companyOid failed! error : {}", e.getMessage());
            throw new BizException(RespCode.SUPPLIER_NOT_EXIST);
        }
        String venNickname = MyStringUtils.formatFuzzyQuery(vendorInfoCO.getVenNickname());
        List<VendorInfoCO> vendorInfoCOs = baseMapper.selectVendorInfosByCompanyIdAndVendorName(companyId, venNickname).stream().map(VendorInfoAdapter::vendorInfoToVendorInfoCO).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(vendorInfoCOs)) {
            vendorInfoCOs.forEach(v -> {
                v.setVenBankAccountBeans(vendorBankAccountMapper.selectVendorBankAccountsByCompanyOidAndVendorInfoId(null, v.getId().toString()).stream().map(VendorBankAccountAdapter::vendorBankAccountToVendorBankAccountCO).collect(Collectors.toList()));
            });
        }
//        Map<String, Object> resultMap = new HashMap<>();
//        resultMap.put("accountVen", vendorInfoCOs.size());
//        resultMap.put("outVenType", vendorInfoCOs.stream().filter(v -> StatusEnum.DISABLE.getId().equals(v.getVenType())).count());
//        VendorInfoPageWrapped venBankPageBody = new VendorInfoPageWrapped();
//        venBankPageBody.setVenInfoBeans(vendorInfoCOs);
//        resultMap.put("body", venBankPageBody);
//        return ViewUtil.defaultView().body(resultMap);

        Page page = new Page();
        page.setRecords(vendorInfoCOs);
        page.setSize(vendorInfoCOs.size());
        page.setTotal(vendorInfoCOs.stream().filter(v -> StatusEnum.DISABLE.getId().equals(v.getVenType())).count());
        return page;
    }

    public VendorInfoCO getVendorInfoByArtemis(String id) {
        VendorInfoCO vendorInfoCO;
        VendorInfo vendorInfo = baseMapper.selectById(id);
        if (vendorInfo == null) {
            throw new BizException(RespCode.SUPPLIER_NOT_EXIST);
        } else {
            vendorInfoCO = VendorInfoAdapter.vendorInfoToVendorInfoCO(vendorInfo);
        }
        return vendorInfoCO;
    }

    public VendorInfoCO getOneVendorInfoByArtemis(String id) {
        VendorInfoCO vendorInfoCO = new VendorInfoCO();
        VendorInfo vendorInfo = baseMapper.selectById(id);
        if (vendorInfo != null) {
//            vendorInfoCO = VendorInfoMapping.vendorInfoToVendorInfoCO(vendorInfo);
            vendorInfoCO.setVenNickname(vendorInfo.getVendorName());
        } else {
            LOGGER.error("VendorInfo not found! id : {}", id);
            throw new ObjectNotFoundException(VendorInfo.class, id);
        }
        return vendorInfoCO;
    }

    public Page<VendorInfoCO> pageVendorInfosByConditions(Long companyId, String venNickname, int page,int size) {
        Page<VendorInfo> pageo = new Page<>(page + 1, size, "last_updated_date");
        pageo.setAsc(false);
        pageo.setRecords(baseMapper.selectVendorInfosByCompanyIdAndVendorNameForPage(companyId, MyStringUtils.formatFuzzyQuery(venNickname), pageo));
        List<VendorInfoCO> vendorInfoCOs = pageo.getRecords().stream().map(VendorInfoAdapter::vendorInfoToVendorInfoCO).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(vendorInfoCOs)) {
            for (VendorInfoCO vendorInfoCO : vendorInfoCOs) {
                vendorInfoCO.setVenBankAccountBeans(
                        vendorBankAccountMapper.selectVendorBankAccountsByCompanyOidAndVendorInfoId(null, vendorInfoCO.getId().toString()).stream().map(VendorBankAccountAdapter::vendorBankAccountToVendorBankAccountCO).collect(Collectors.toList())
                );
            }
        }
        Page<VendorInfoCO> result = new Page<>(page+ 1, size);
        result.setRecords(vendorInfoCOs);
        result.setTotal(pageo.getTotal());
        return result;
    }

    public Page<VendorInfoCO> pageVendorInfosByConditions(Long companyId, String venNickname,String vendorCode, int page,int size) {
        Page<VendorInfo> pageo = new Page<>(page + 1, size, "last_updated_date");
        pageo.setAsc(false);
        pageo.setRecords(baseMapper.selectVendorInfosByCompanyIdAndVendorNameAndCodeForPage(companyId, MyStringUtils.formatFuzzyQuery(venNickname), MyStringUtils.formatFuzzyQuery(vendorCode), pageo));
        List<VendorInfoCO> vendorInfoCOs = pageo.getRecords().stream().map(VendorInfoAdapter::vendorInfoToVendorInfoCO).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(vendorInfoCOs)) {
            for (VendorInfoCO vendorInfoCO : vendorInfoCOs) {
                vendorInfoCO.setVenBankAccountBeans(
                        vendorBankAccountMapper.selectVendorBankAccountsByCompanyOidAndVendorInfoId(null, vendorInfoCO.getId().toString()).stream().map(VendorBankAccountAdapter::vendorBankAccountToVendorBankAccountCO).collect(Collectors.toList())
                );
            }
        }
        Page<VendorInfoCO> result = new Page<>(page+ 1, size);
        result.setRecords(vendorInfoCOs);
        result.setTotal(pageo.getTotal());
        return result;
    }
    public Page<VendorInfoCO> searchVendorInfosByPage(String venNickname, String userOid, Pageable pageable) {
        UserDTO userDTO = contactService.getUserDTOByUserOid(userOid);
        Page<VendorInfo> page = new Page<>(pageable.getPageNumber()+1, pageable.getPageSize());
        page.setRecords(baseMapper.selectVendorInfosByRelationCompanyForPage(null, null, MyStringUtils.formatFuzzyQuery(venNickname), null, null, userDTO.getCompanyId().toString(), userDTO.getCompanyOid().toString(), page));
        Page<VendorInfoCO> result = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        result.setRecords(page.getRecords().stream().map(VendorInfoAdapter::vendorInfoToVendorInfoCO).collect(Collectors.toList()));
        result.setTotal(page.getTotal());
        return result;
    }

    public VendorInfoCO insertVendorInfoByBill(VendorInfoCO vendorInfoCO, String userOid) {
        VendorInfo vendorInfo;
        VendorInfoCO result;
        List<VendorBankAccountCO> vendorBankAccountCOs = vendorInfoCO.getVenBankAccountBeans();
        boolean existVendorBankAccountCOsFlag = !CollectionUtils.isEmpty(vendorBankAccountCOs);
        // 表单级新增供应商信息，供应商下银行账号信息不能为空
        if (!existVendorBankAccountCOsFlag) {
            throw new BizException(RespCode.SUPPLIER_ACCOUNT_EMPTY);
        }
        if (vendorBankAccountCOs.stream().map(VendorBankAccountCO::getBankAccount).collect(Collectors.toSet()).size() != vendorBankAccountCOs.size()) {
            throw new BizException(RespCode.SUPPLIER_ACCOUNT_REPEAT);
        }
        validateVendorInfoCO(vendorInfoCO);
        // 表单级创建银行账号，银行户名默认为供应商名称
        vendorBankAccountCOs.stream().map(vendorBankAccountCO -> {
            vendorBankAccountCO.setVenBankNumberName(vendorInfoCO.getVenNickname());
            return vendorBankAccountCO;
        }).collect(Collectors.toList());
        // 校验银行账号数据
        for (VendorBankAccountCO vendorBankAccountCO : vendorBankAccountCOs) {
            vendorBankAccountService.validateVendorBankAccountCO(vendorBankAccountCO);
        }
        UserDTO userDTO = contactService.getUserDTOByUserOid(userOid);
        // 校验供应商编码重复，租户下全局校验，不区分租户级或公司级供应商[自动编码规则启用，其他处已校验，故该处不用校验]
        if (vendorInfoCO.getAutoCodeMark() == null || !vendorInfoCO.getAutoCodeMark()) {
            List<VendorInfo> vendorInfos = baseMapper.selectVendorInfosByVendorCodeAndTenantId(vendorInfoCO.getVenderCode(), userDTO.getTenantId());
            if (!CollectionUtils.isEmpty(vendorInfos)) {
                throw new BizException(RespCode.SUPPLIER_CODE_EXISTS);
            }
        }
        // 校验供应商名称重复，创建表单级[属于公司级]供应商，在当前公司级和租户级校验，和供应商编码校验规则不同
        List<VendorInfo> vendorInfoList = baseMapper.selectVendorInfosByCondtions(userDTO.getTenantId(), userDTO.getCompanyOid().toString(), vendorInfoCO.getVenNickname(), null, vendorInfoCO.getSource().toString());
        if (!CollectionUtils.isEmpty(vendorInfoList)) {
            validateVendorInfoNameAndCode(vendorInfoCO, vendorInfoList, RespCode.SUPPLIER_NAME_EXISTS, RespCode.SUPPLIER_NAME_EXISTS_IN_COMPANY_OR_TENANT_LEVEL);
        }
        vendorInfo = VendorInfoAdapter.vendorInfoCOToVendorInfo(vendorInfoCO);
        vendorInfo.setVendorCode(vendorInfoCO.getVenderCode());
        vendorInfo.setTenantId(userDTO.getTenantId());
        vendorInfo.setCompanyOid(userDTO.getCompanyOid().toString());
        vendorInfo.setCreatedBy(userDTO.getId());
        vendorInfo.setLastUpdatedBy(userDTO.getId());
        baseMapper.insert(vendorInfo);
        result = VendorInfoAdapter.vendorInfoToVendorInfoCO(baseMapper.selectById(vendorInfo.getId()));
        vendorBankAccountCOs.forEach(v -> {
            v.setVenInfoId(vendorInfo.getId().toString());
            v.setCompanyOid(vendorInfo.getCompanyOid());
            v.setVenNickOid(vendorInfo.getVendorCode());
        });
        result.setVenBankAccountBeans(vendorBankAccountService.createVendorBankAccountsByBill(vendorBankAccountCOs, userDTO));
        return result;
    }

    public List<VendorInfoCO> searchVendorInfosByTenantIdAndVendorCode(Long tenantId, String vendorCode) {
        return baseMapper.selectVendorInfosByTenantIdAndVendorNameAndVendorId(tenantId.toString(), null, vendorCode).stream().map(VendorInfoAdapter::vendorInfoToVendorInfoCO).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public VendorInfoCO searchVendorInfoAndBank(String vendorInfoId, String bankId) {
        VendorInfoCO vendorInfoCO ;
        VendorInfo vendorInfo = baseMapper.selectById(vendorInfoId);
        if (vendorInfo == null) {
            throw new BizException(RespCode.SUPPLIER_NOT_EXISTS);
        } else {
            vendorInfoCO = VendorInfoAdapter.vendorInfoToVendorInfoCO(vendorInfo);
            List<VendorBankAccountCO> vendorBankAccountCOs;
            if (StringUtils.isBlank(bankId)) {
                vendorBankAccountCOs = vendorBankAccountMapper.selectVendorBankAccountsByCompanyOidAndVendorInfoId(null, vendorInfoId).stream().map(VendorBankAccountAdapter::vendorBankAccountToVendorBankAccountCO).collect(Collectors.toList());
            } else {
                VendorBankAccount vendorBankAccount = vendorBankAccountMapper.selectById(bankId);
                if (vendorBankAccount != null) {
                    if (!vendorBankAccount.getVendorInfoId().toString().equals(vendorInfoId)) {
                        // 数据错误，供应商下不存在该银行账号
                        throw new BizException(RespCode.SUPPLIER_BANK_ACCOUNT_NOT_MATCH);
                    }
                } else {
                    // 该银行账号不存在
                    throw new BizException(RespCode.SUPPLIER_BANK_ACCOUNT_NOT_EXISTS);
                }
                vendorBankAccountCOs = Arrays.asList(VendorBankAccountAdapter.vendorBankAccountToVendorBankAccountCO(vendorBankAccount));
            }
            vendorInfoCO.setVenBankAccountBeans(vendorBankAccountCOs);
        }
        return vendorInfoCO;
    }

    public VendorInfoCO getVendorInfoByVendorCode(String vendorCode, Long tenantId) {
        VendorInfoCO vendorInfoCO = null;
        List<VendorInfo> vendorInfos = baseMapper.selectVendorInfosByVendorCodeAndTenantId(vendorCode, tenantId);
        if (!CollectionUtils.isEmpty(vendorInfos)) {
            vendorInfoCO = VendorInfoAdapter.vendorInfoToVendorInfoCO(vendorInfos.get(0));
            vendorInfoCO.setVenBankAccountBeans(vendorBankAccountMapper.selectVendorBankAccountsByCompanyOidAndVendorInfoId(null, vendorInfoCO.getId().toString()).stream().map(VendorBankAccountAdapter::vendorBankAccountToVendorBankAccountCO).collect(Collectors.toList()));
        }
        return vendorInfoCO;
    }

    private VendorInfo setBaseData(VendorInfo vendorInfo) {
        vendorInfo.setLastUpdatedByEmployeeId(OPERATOR);
        vendorInfo.setLastUpdatedByName(OPERATOR);
        if (vendorInfo.getStatus() == null) {
            vendorInfo.setStatus(StatusEnum.ENABLE.getId());
        }
        vendorInfo.setLastUpdatedBy(0L);
        return vendorInfo;
    }

    private void validateVendorInfoCO(VendorInfoCO vendorInfoCO) {
        MyStringUtils.deleteStringTypeFieldTrim(vendorInfoCO);
        if (vendorInfoCO.getVenderTypeId() == null) {
            throw new BizException(RespCode.SUPPLIER_TYPE_EMPTY);
        }
        // 仅新增供应商操作时，需要判断是否自动编码
        if (vendorInfoCO.getId() == null && vendorInfoCO.getAutoCodeMark() != null && vendorInfoCO.getAutoCodeMark()) {
            Long tenantId = Long.valueOf(OrgInformationUtil.getCurrentTenantId());
            String vendorCode;
            List<CompanyDTO> companyDTOs = relationVendorCompanyService.findCompanyListByIdList(Arrays.asList(Long.valueOf(OrgInformationUtil.getCurrentCompanyId())));
            if (CollectionUtils.isEmpty(companyDTOs)) {
                throw new BizException(RespCode.SUPPLIER_COMPANY_CODE_NOT_EXIST);
            }
            String companyCode = companyDTOs.get(0).getCompanyCode();
            List<VendorInfo> vendorInfos;
            // 获取自动编码后，并校验有无重复，若重复则重新获取供应商编码
            // [重复获取次数：最多3次，若获取3次编码还是校验不通过，则提示用户重新保存或联系管理员修改供应商编码规则]
            int count = 0;
            do {
                OrderNumberCO orderNumberCO = hcfOrganizationInterface.getVendorCode(companyCode, tenantId);
                if (!SUCCEED.equals(orderNumberCO.getCode())) {
                    String msg = "";
                    String language = OrgInformationUtil.getCurrentLanguage();
                    language = StringUtils.isBlank(language) ? Constants.DEFAULT_LANGUAGE : (Constants.EN_LANGUAGE.equalsIgnoreCase(language) ? Constants.EN_US_LANGUAGE : language);
                    for (OrderNumberCO.Message message : orderNumberCO.getMessage()) {
                        if (language.equalsIgnoreCase(message.getLanguage())) {
                            msg = message.getContent();
                            break;
                        }
                    }
                }
                vendorCode = orderNumberCO.getOrderNumber();
                vendorInfos = baseMapper.selectVendorInfosByVendorCodeAndTenantId(vendorCode, tenantId);
                count ++;
                LOGGER.info("Get vendor code to artemis, vendorCode : {}, counts : {}", vendorCode, count);
            } while (!CollectionUtils.isEmpty(vendorInfos) && count < 3);
            if (!CollectionUtils.isEmpty(vendorInfos) && count >= 3) {
                throw new BizException(RespCode.SUPPLIER_CODE_EXISTS_AND_TRY_AGAIN);
            }
            vendorInfoCO.setVenderCode(vendorCode);
        } else {
            if (StringUtils.isBlank(vendorInfoCO.getVenderCode()) || !vendorCodePattern.matcher(vendorInfoCO.getVenderCode()).matches()) {
                throw new BizException(RespCode.SUPPLIER_CHARACTER_FORMAT_ERROR);
            }
        }
        if (StringUtils.isBlank(vendorInfoCO.getVenNickname()) || vendorInfoCO.getVenNickname().length() > 50) {
            throw new BizException(RespCode.SUPPLIER_NAME_NOT_BLANK_AND_LENGTH_NO_MORE_THAN_50);
        }
//        启用日期暂时不做校验
//        if (vendorInfoCO.getEffectiveDate() == null) {
//            return ViewUtil.defaultView(RespCode.RES_2002005).body(vendorInfoCO);
//        }
        if (vendorInfoCO.getSource() == null) {
            throw new BizException(RespCode.SUPPLIER_DATA_SOURCE_IDENTIFIER_EMPTY);
        }
        if (StringUtils.isNotBlank(vendorInfoCO.getArtificialPerson()) && !pattern1.matcher(vendorInfoCO.getArtificialPerson()).matches()) {
            throw new BizException(RespCode.SUPPLIER_LEGAL_REPRESENTATIVE_INOUT_ERROR);
        }
        if (StringUtils.isNotBlank(vendorInfoCO.getTaxIdNumber()) && !pattern2.matcher(vendorInfoCO.getTaxIdNumber()).matches()) {
            throw new BizException(RespCode.SUPPLIER_TAX_NUMBER_INOUT_ERROR);
        }
        if (StringUtils.isNotBlank(vendorInfoCO.getContact()) && !pattern1.matcher(vendorInfoCO.getContact()).matches()) {
            throw new BizException(RespCode.SUPPLIER_CONTACT_INOUT_ERROR);
        }
        if (StringUtils.isNotBlank(vendorInfoCO.getContactPhone()) && !mobilePattern.matcher(vendorInfoCO.getContactPhone()).matches()) {
            throw new BizException(RespCode.SUPPLIER_CONTACT_NUMBER_INOUT_ERROR);
        }
        if (StringUtils.isNotBlank(vendorInfoCO.getContactMail()) && !emailPattern.matcher(vendorInfoCO.getContactMail()).matches()) {
            throw new BizException(RespCode.SUPPLIER_EMAIL_INOUT_ERROR);
        }
        if (StringUtils.isNotBlank(vendorInfoCO.getFax()) && !faxPattern.matcher(vendorInfoCO.getFax()).matches()) {
            throw new BizException(RespCode.SUPPLIER_FAX_INOUT_ERROR);
        }
        if (StringUtils.isNotBlank(vendorInfoCO.getAddress()) && vendorInfoCO.getAddress().length() > 200) {
            throw new BizException(RespCode.SUPPLIER_ADDRESS_INOUT_ERROR);
        }
        if (StringUtils.isNotBlank(vendorInfoCO.getNotes()) && vendorInfoCO.getNotes().length() > 200) {
            throw new BizException(RespCode.SUPPLIER_REMARK_INOUT_ERROR);
        }
    }

    private void validateVendorInfoNameAndCode(VendorInfoCO vendorInfoCO, List<VendorInfo> vendorInfos, String code1, String code2) {
        if (vendorInfoCO.getId() != null) {
            if ((vendorInfos.size() > 1 || !vendorInfos.get(0).getId().equals(vendorInfoCO.getId()))) {
                if (vendorInfoCO.getSource() == SourceEnum.TENANT) {
                    throw new BizException(RespCode.SUPPLIER_NAME_EXISTS);
                } else if (vendorInfoCO.getSource() == SourceEnum.BILL || vendorInfoCO.getSource() == SourceEnum.COMPANY) {
                    throw new BizException(RespCode.SUPPLIER_NAME_EXISTS_IN_COMPANY_OR_TENANT_LEVEL);
                }
            }
        } else {
            if (vendorInfoCO.getSource() == SourceEnum.TENANT) {
                throw new BizException(RespCode.SUPPLIER_NAME_EXISTS);
            } else if (vendorInfoCO.getSource() == SourceEnum.BILL || vendorInfoCO.getSource() == SourceEnum.COMPANY) {
                throw new BizException(RespCode.SUPPLIER_NAME_EXISTS_IN_COMPANY_OR_TENANT_LEVEL);
            }
        }
    }
    public List<ReceivablesDTO>  selectVendorInfosByCompanyIdAndVendorName(Long companyId, String venNickname){
        List<ReceivablesDTO> receivablesDTOS = new ArrayList<>();
        List<VendorInfoCO> vendorInfoList = baseMapper.selectVendorInfosByCompanyIdAndVendorName(companyId, venNickname).stream().map(VendorInfoAdapter::vendorInfoToVendorInfoCO).collect(Collectors.toList());;
        if (CollectionUtils.isEmpty(vendorInfoList)){
            return receivablesDTOS;
        }
        vendorInfoList.stream().forEach(
                vendor->{
                    ReceivablesDTO dto = new ReceivablesDTO();
                    dto.setId(vendor.getId());
                    dto.setIsEmp(false);
                    dto.setCode(vendor.getVenderCode());
                    dto.setName(vendor.getVenNickname());
                    dto.setSign(dto.getId()+"_"+dto.getIsEmp());
                    List<BankInfo> bankInfos = new ArrayList<>();
                    List<VendorBankAccountCO> venBankAccountBeans = vendor.getVenBankAccountBeans();
                    if(com.baomidou.mybatisplus.toolkit.CollectionUtils.isNotEmpty(venBankAccountBeans)){
                        venBankAccountBeans.forEach(
                                venBankAccountBean->{
                                    BankInfo bankInfo = new BankInfo();
                                    //银行名称
                                    bankInfo.setBankName(venBankAccountBean.getBankName());
                                    //开户行代码
                                    bankInfo.setBankCode(venBankAccountBean.getBankCode());
                                    bankInfo.setNumber(venBankAccountBean.getBankAccount());
                                    bankInfos.add(bankInfo);
                                }
                        );
                    }
                    dto.setBankInfos(bankInfos);
                    receivablesDTOS.add(dto);

                }
        );
        return receivablesDTOS;
    }
}
