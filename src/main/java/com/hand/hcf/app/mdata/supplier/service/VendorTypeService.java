package com.hand.hcf.app.mdata.supplier.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.dto.VendorTypeCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.supplier.constants.Constants;
import com.hand.hcf.app.mdata.supplier.domain.VendorType;
import com.hand.hcf.app.mdata.supplier.persistence.VendorTypeMapper;
import com.hand.hcf.app.mdata.supplier.web.adapter.VendorTypeAdapter;
import com.hand.hcf.app.mdata.utils.MyStringUtils;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/11 11:13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VendorTypeService extends BaseService<VendorTypeMapper, VendorType> {

    private static final Pattern vendorTypeCodePattern = Pattern.compile("^[0-9A-Za-z]{1,30}$");

    private static final Pattern vendorTypeNamePattern = Pattern.compile("^[0-9A-Za-z\\u4e00-\\u9fa5]{1,30}$");


    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Page<VendorTypeCO> searchVendorTypes(String code, String name, Boolean isEnabled, Long tenantId, Pageable pageable) {
        String codeN = MyStringUtils.formatFuzzyQuery(code);
        String nameN =  MyStringUtils.formatFuzzyQuery(name);
        Page<VendorType> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize(), "code");
        page.setAsc(false);
        page.setRecords(baseMapper.selectVendorTypesByPages(codeN, nameN, null, tenantId, isEnabled, page));
        List<VendorTypeCO> vendorTypeCOs = page.getRecords().stream().map(VendorTypeAdapter::vendorTypeToVendorTypeCO).collect(Collectors.toList());
        Page<VendorTypeCO> result = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        result.setRecords(vendorTypeCOs);
        result.setTotal(page.getTotal());
        return result;
    }

    public VendorTypeCO insertOrUpDateVendorType(VendorTypeCO vendorTypeCO, String roleType) {
        MyStringUtils.deleteStringTypeFieldTrim(vendorTypeCO);
        if (StringUtils.isBlank(vendorTypeCO.getVendorTypeCode()) || !vendorTypeCodePattern.matcher(vendorTypeCO.getVendorTypeCode()).matches()) {
            throw new BizException(RespCode.SUPPLIER_TYPE_CODE_INPUT_ERROR);
        }
        if (StringUtils.isBlank(vendorTypeCO.getName()) || !vendorTypeNamePattern.matcher(vendorTypeCO.getName()).matches()) {
            throw new BizException(RespCode.SUPPLIER_TYPE_NAME_INPUT_ERROR);
        }
        if (!Constants.TENANT_LEVEL.equals(roleType)) {
            throw new BizException(RespCode.SUPPLIER_NON_GROUP_AUTHORITY);
        }
        VendorType vendorType;
        VendorTypeCO result;
        Long companyId = Long.valueOf(OrgInformationUtil.getCurrentCompanyId());
        Long tenantId = Long.valueOf(OrgInformationUtil.getCurrentTenantId());
        Long userId = OrgInformationUtil.getCurrentUserId();
        if (vendorTypeCO.getId() == null) {
            List<VendorType> checkCodeAndNameDuplication = baseMapper.selectVendorTypeByCode(vendorTypeCO.getVendorTypeCode(), vendorTypeCO.getName(), tenantId);
            if (!CollectionUtils.isEmpty(checkCodeAndNameDuplication)) {
                throw new BizException(RespCode.SUPPLIER_TYPE_NAME_AND_CODE_BOTH_EXIST);
            }
            List<VendorType> checkCodeDuplication = baseMapper.selectVendorTypeByCode(vendorTypeCO.getVendorTypeCode(), null, tenantId);
            if (!CollectionUtils.isEmpty(checkCodeDuplication)) {
                throw new BizException(RespCode.SUPPLIER_TYPE_CODE_EXISTS);
            }
            List<VendorType> checkNameDuplication = baseMapper.selectVendorTypeByCode(null, vendorTypeCO.getName(), tenantId);
            if (!CollectionUtils.isEmpty(checkNameDuplication)) {
                throw new BizException(RespCode.SUPPLIER_TYPE_NAME_EXISTS);
            }
            vendorType = VendorTypeAdapter.vendorTypeCOToVendorType(vendorTypeCO);
            vendorType.setCompanyId(companyId);
            vendorType.setTenantId(tenantId);
            vendorType.setCreatedBy(userId);
            vendorType.setLastUpdatedBy(userId);
            baseMapper.insert(vendorType);
            result = VendorTypeAdapter.vendorTypeToVendorTypeCO(baseMapper.selectById(vendorType.getId()));
        } else {
            // 业务逻辑：供应商类型code不支持修改，无需校验code
            vendorType = baseMapper.selectById(vendorTypeCO.getId());
            if (vendorType == null) {
                throw new BizException(RespCode.SUPPLIER_TYPE_NOT_EXISTS);
            } else {
                List<VendorType> vendorTypes = baseMapper.selectVendorTypeByCode(null, vendorTypeCO.getName(), tenantId);
                boolean checkFlag = !CollectionUtils.isEmpty(vendorTypes) && (vendorTypes.size() > 1 || !vendorTypes.get(0).getId().equals(vendorTypeCO.getId()));
                if (checkFlag) {
                    throw new BizException(RespCode.SUPPLIER_TYPE_NAME_EXISTS);
                } else {
                    BeanUtils.copyProperties(vendorTypeCO, vendorType);
                    vendorType.setCompanyId(companyId);
                    vendorType.setTenantId(tenantId);
                    vendorType.setLastUpdatedBy(userId);
                    vendorType.setLastUpdatedDate(ZonedDateTime.now());
                    super.updateById(vendorType);
                    result = VendorTypeAdapter.vendorTypeToVendorTypeCO(baseMapper.selectById(vendorType.getId()));
                }
            }

        }
        return result;
    }
}
