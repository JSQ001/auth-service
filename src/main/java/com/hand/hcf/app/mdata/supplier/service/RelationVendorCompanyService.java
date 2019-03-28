package com.hand.hcf.app.mdata.supplier.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.dto.RelationVendorCompanyCO;
import com.hand.hcf.app.common.dto.VendorInfoCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.service.SetOfBooksService;
import com.hand.hcf.app.mdata.supplier.constants.Constants;
import com.hand.hcf.app.mdata.supplier.domain.RelationVendorCompany;
import com.hand.hcf.app.mdata.supplier.domain.VendorInfo;
import com.hand.hcf.app.mdata.supplier.persistence.RelationVendorCompanyMapper;
import com.hand.hcf.app.mdata.supplier.persistence.VendorInfoMapper;
import com.hand.hcf.app.mdata.supplier.web.adapter.RelationVendorCompanyAdapter;
import com.hand.hcf.app.mdata.supplier.web.dto.CompanyDTO;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/14 16:52
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RelationVendorCompanyService extends BaseService<RelationVendorCompanyMapper, RelationVendorCompany> {

    @Autowired
    private VendorInfoMapper vendorInfoMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private SetOfBooksService setOfBooksService;

    @Autowired
    private MapperFacade mapper;

    public RelationVendorCompanyCO batchCreateRelationVendorCompanys(RelationVendorCompanyCO relationVendorCompanyCO, String roleType) {
        if (!(StringUtils.isNotBlank(roleType) && Constants.TENANT_LEVEL.equals(roleType))) {
            throw new BizException(RespCode.SUPPLIER_NON_TENANT_LEVEL_TO_DISTRIBUTE);
        }

        Long tenantId = Long.valueOf(OrgInformationUtil.getCurrentTenantId());
        Long userId = OrgInformationUtil.getCurrentUserId();
        Set<String> vendorInfoIds = relationVendorCompanyCO.getInfoIDs();
        Set<String> companyIds = relationVendorCompanyCO.getCompanyIDs();
        if (CollectionUtils.isEmpty(vendorInfoIds) || CollectionUtils.isEmpty(companyIds)) {
            throw new BizException(RespCode.SUPPLIER_NON_SUPPLIER_OR_COMPANY_TO_DISTRIBUTE);
        } else {
            vendorInfoIds.forEach(vendorInfoId -> {
                companyIds.forEach(companyId -> {
                    List<RelationVendorCompany> relationVendorCompanies = baseMapper.selectRelationVendorCompanysByVendorInfoIdAndCompanyId(Long.valueOf(vendorInfoId), Long.valueOf(companyId));
                    if (CollectionUtils.isEmpty(relationVendorCompanies)) {
                        RelationVendorCompany relationVendorCompany = new RelationVendorCompany();
                        relationVendorCompany.setVendorInfoId(Long.valueOf(vendorInfoId));
                        relationVendorCompany.setCompanyId(Long.valueOf(companyId));
                        relationVendorCompany.setTenantId(tenantId);
                        relationVendorCompany.setCreatedBy(userId);
                        relationVendorCompany.setLastUpdatedBy(userId);
                        baseMapper.insert(relationVendorCompany);
                    }
                });
            });

        }
        return relationVendorCompanyCO;
    }

    public RelationVendorCompanyCO updateRelationVendorCompany(RelationVendorCompanyCO relationVendorCompanyCO, String roleType) {
        if (!(StringUtils.isNotBlank(roleType) && Constants.TENANT_LEVEL.equals(roleType))) {
            throw new BizException(RespCode.SUPPLIER_NON_TENANT_LEVEL_TO_DISTRIBUTE);
        }
        Long userId = OrgInformationUtil.getCurrentTenantId();
        RelationVendorCompany relationVendorCompany = baseMapper.selectById(relationVendorCompanyCO.getId());
        if (relationVendorCompany == null) {
            throw new BizException(RespCode.SUPPLIER_COMPANY_ASSOCIATION_NOT_EXISTS);
        }
        RelationVendorCompany relationVendorCompanyNew = RelationVendorCompanyAdapter.relationVendorCompanyCOToRelationVendorCompany(relationVendorCompanyCO);
        relationVendorCompanyNew.setLastUpdatedBy(userId);
        relationVendorCompanyNew.setLastUpdatedDate(ZonedDateTime.now());
        super.updateById(relationVendorCompanyNew);
        return RelationVendorCompanyAdapter.relationVendorCompanyToRelationVendorCompanyCO(baseMapper.selectById(relationVendorCompanyNew.getId()));
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Page<CompanyDTO> selectRelationVendorCompanys(Long infoId, Page page) {
        List<CompanyDTO> result = new ArrayList<>();

        List<RelationVendorCompany> relationVendorCompanies = baseMapper.selectRelationVendorCompanyByPage(infoId, page);
        if (relationVendorCompanies != null){
            result = findCompanyListByIdList(relationVendorCompanies.stream().map(RelationVendorCompany::getCompanyId).collect(Collectors.toList()));
            result.forEach(companyDTO -> {
                for (RelationVendorCompany relationVendorCompany : relationVendorCompanies) {
                    if (companyDTO.getId().equals(relationVendorCompany.getCompanyId())) {
                        companyDTO.setInfoAssignCompanyId(relationVendorCompany.getId());
                        companyDTO.setEnabled(relationVendorCompany.getEnabled());
                        break;
                    }
                }
            });
            result.sort(Comparator.comparing(CompanyDTO::getCompanyCode));
        }
        page.setRecords(result);
        return page;
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public List<RelationVendorCompanyCO> selectRelationVendorCompanysByVendorInfoId(Long infoId) {
        return baseMapper.selectRelationVendorCompanysByVendorInfoId(infoId).stream().map(RelationVendorCompanyAdapter::relationVendorCompanyToRelationVendorCompanyCO).collect(Collectors.toList());
    }

    public VendorInfoCO saveConnectVendorAndCompany(VendorInfoCO vendorInfoCO) {
        Long tenantId = vendorInfoCO.getTenantId();
        Long companyId = vendorInfoCO.getCompanyId();
        // 校验供应商在所属租户下[租户级]是否存在
        List<VendorInfo> vendorInfos = vendorInfoMapper.selectVendorInfosByTenantIdAndVendorNameAndVendorId(tenantId.toString(),null,vendorInfoCO.getVenNickOid());
        if (CollectionUtils.isEmpty(vendorInfos)) {
            throw new BizException(RespCode.SUPPLIER_NOT_EXIST_UNDER_CURRENT_TENANT);
        }
        if (vendorInfos.size() > 1) {
            throw new BizException(RespCode.SUPPLIER_MULTIPLE_EXIST_UNDER_CURRENT_TENANT);
        }
        Long vendorInfoId = vendorInfos.get(0).getId();
        // 校验供应商对应公司是否已经分配
        List<RelationVendorCompany> relationVendorCompanies = baseMapper.selectRelationVendorCompanysByVendorInfoIdAndCompanyId(vendorInfoId, companyId);
        if (!CollectionUtils.isEmpty(relationVendorCompanies)) {
            throw new BizException(RespCode.SUPPLIER_HAS_ASSIGNED_A_COMPANY);
        }
        RelationVendorCompany relationVendorCompany = new RelationVendorCompany();
        relationVendorCompany.setCompanyId(companyId);
        relationVendorCompany.setVendorInfoId(vendorInfoId);
        relationVendorCompany.setTenantId(tenantId);
        relationVendorCompany.setCreatedBy(0L);
        relationVendorCompany.setLastUpdatedBy(relationVendorCompany.getCreatedBy());
        baseMapper.insert(relationVendorCompany);
        return vendorInfoCO;
    }

    /**
     * 查询某租户下某供应商尚未分配的公司
     * @param tenantId
     * @param vendorInfoId
     * @param setOfBooksId
     * @param companyCode
     * @param companyName
     * @param page
     * @return
     */
    public Page<CompanyCO> selectVendorUnassignedCompany(Long tenantId,Long vendorInfoId,Long setOfBooksId,String companyCode,String companyName,Page page){
        List<Long> existCompanyIds = new ArrayList<>();
        if (vendorInfoId != null) {
            existCompanyIds = this.selectList(
                    new EntityWrapper<RelationVendorCompany>()
                            .eq(tenantId != null, "tenant_id", tenantId)
                            .eq(vendorInfoId != null, "vendor_info_id", vendorInfoId)
            ).stream().map(RelationVendorCompany::getCompanyId).collect(Collectors.toList());
        }
        Page<CompanyCO> companyCOPage = companyService.pageByTenantIdConditionByIgnoreIds(
                tenantId, setOfBooksId, companyCode, companyName, null, null, existCompanyIds,page);
        return companyCOPage;
    }

    /**
     * 查询某租户下 某些 供应商尚未分配的公司
     * @param tenantId
     * @param setOfBooksId
     * @param companyCode
     * @param companyName
     * @param vendorInfoIdList
     * @param page
     * @return
     */
    public Page<CompanyCO> selectBatchVendorUnassignedCompany(Long tenantId,Long setOfBooksId,String companyCode,String companyName,List<Long> vendorInfoIdList,Page page){
        List<Long> existCompanyIds = this.selectList(
                new EntityWrapper<RelationVendorCompany>()
                        .eq(tenantId != null,"tenant_id",tenantId)
                        .in("vendor_info_id",vendorInfoIdList)
        ).stream().map(RelationVendorCompany::getCompanyId).collect(Collectors.toList());
        Page<CompanyCO> companyCOPage = companyService.pageByTenantIdConditionByIgnoreIds(
                tenantId, setOfBooksId, companyCode, companyName, null, null, existCompanyIds,page);
        return companyCOPage;
    }

    public List<CompanyDTO> findCompanyListByIdList(List<Long> companyIDS) {
        List<CompanyCO> coms = companyService.listByIds(companyIDS);
        Map<Long, String> setBooksMap = new HashMap<>(16);
        List<CompanyDTO> list = mapper.mapAsList(coms, CompanyDTO.class);
        list.forEach(companyDTO -> {
            if (companyDTO.getSetOfBooksId() != null && StringUtils.isEmpty(companyDTO.getSetOfBooksName())) {
                if (setBooksMap.containsKey(companyDTO.getSetOfBooksId())) {
                    companyDTO.setSetOfBooksName(setBooksMap.get(companyDTO.getSetOfBooksId()));
                } else {
                    SetOfBooks setOfBooksInfoCO = setOfBooksService.getSetOfBooksById(companyDTO.getSetOfBooksId());
                    if (setOfBooksInfoCO != null) {
                        setBooksMap.put(companyDTO.getSetOfBooksId(), setOfBooksInfoCO.getSetOfBooksName());
                        companyDTO.setSetOfBooksName(setBooksMap.get(companyDTO.getSetOfBooksId()));
                    }
                }
            }
        });
        return list;
    }
}
