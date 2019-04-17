package com.hand.hcf.app.mdata.supplier.web.adapter;

import com.hand.hcf.app.common.co.VendorInfoCO;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.supplier.domain.VendorInfo;
import com.hand.hcf.app.mdata.supplier.domain.VendorType;
import com.hand.hcf.app.mdata.supplier.persistence.VendorTypeMapper;
import com.hand.hcf.app.mdata.supplier.service.dto.vendorInfoforStatusDTO;
import com.hand.hcf.app.core.exception.core.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/11 20:04
 */
@Component
public class VendorInfoAdapter {

    private VendorTypeMapper vendorTypeMapper;

    private static VendorInfoAdapter vendorInfoAdapter;
    @Autowired
    private  HcfOrganizationInterface hcfOrganizationInterface;
    @Autowired
    private ContactService contactService;

    private static final Logger LOGGER = LoggerFactory.getLogger(VendorInfoAdapter.class);

    public VendorInfoAdapter(VendorTypeMapper vendorTypeMapper,HcfOrganizationInterface hcfOrganizationInterface){
        this.vendorTypeMapper = vendorTypeMapper;
        this.hcfOrganizationInterface = hcfOrganizationInterface;
    }

    @PostConstruct
    private void initialize() {
        vendorInfoAdapter = this;
        vendorInfoAdapter.vendorTypeMapper = this.vendorTypeMapper;
        vendorInfoAdapter.hcfOrganizationInterface = this.hcfOrganizationInterface;
    }

    public static VendorInfo vendorInfoCOToVendorInfo(VendorInfoCO vendorInfoCO) {
        if (vendorInfoCO == null) {
            return null;
        }
        VendorInfo vendorInfo = new VendorInfo();
        BeanUtils.copyProperties(vendorInfoCO, vendorInfo);
        vendorInfo.setVendorName(vendorInfoCO.getVenNickname());
        vendorInfo.setVendorCode(vendorInfoCO.getVenNickOid());
        vendorInfo.setStatus(vendorInfoCO.getVenType());
        vendorInfo.setVendorTypeId(vendorInfoCO.getVenderTypeId());
        vendorInfo.setLastUpdatedByEmployeeId(vendorInfoCO.getVenOperatorNumber());
        vendorInfo.setLastUpdatedByName(vendorInfoCO.getVenOperatorName());
        vendorInfo.setVendorLevelId(vendorInfoCO.getVenderLevelId());
        vendorInfo.setLegalRepresentative(vendorInfoCO.getArtificialPerson());
        vendorInfo.setTaxId(vendorInfoCO.getTaxIdNumber());
        vendorInfo.setRemark(vendorInfoCO.getNotes());
        return vendorInfo;
    }

    public static vendorInfoforStatusDTO vendorInfoToVendorInfoCO(VendorInfo vendorInfo) {
        if (vendorInfo == null) {
            return null;
        }
        Long vendorTypeId = vendorInfo.getVendorTypeId();
        vendorInfoforStatusDTO vendorInfoCO = new vendorInfoforStatusDTO();
        BeanUtils.copyProperties(vendorInfo, vendorInfoCO);
        vendorInfoCO.setVenNickOid(vendorInfo.getVendorCode());
        vendorInfoCO.setVenNickname(vendorInfo.getVendorName());
        vendorInfoCO.setVenderCode(vendorInfo.getVendorCode());
        vendorInfoCO.setVenType(vendorInfo.getStatus());
        vendorInfoCO.setVenderTypeId(vendorTypeId);
        UserDTO ContactCO = vendorInfoAdapter.contactService.getUserDTOByUserId(vendorInfo.getLastUpdatedBy());
        if(ContactCO != null){
            //vendorInfoCO.setVenOperatorNumber(ContactCO.getUserOid().toString());
            //vendorInfoCO.setVenOperatorName(ContactCO.getFullName());
            //@wxd供应商更新日志用户应该显示为工号-姓名
            vendorInfoCO.setVenOperatorNumber(ContactCO.getEmployeeId());
            vendorInfoCO.setVenOperatorName(ContactCO.getFullName());
        }

        vendorInfoCO.setVenderLevelId(vendorInfo.getVendorLevelId());
        vendorInfoCO.setArtificialPerson(vendorInfo.getLegalRepresentative());
        vendorInfoCO.setTaxIdNumber(vendorInfo.getTaxId());
        vendorInfoCO.setNotes(vendorInfo.getRemark());
        vendorInfoCO.setWebUpdateDate(vendorInfo.getLastUpdatedDate());
        vendorInfoCO.setCreateTime(vendorInfo.getCreatedDate());
        vendorInfoCO.setUpdateTime(vendorInfo.getLastUpdatedDate());
        if (vendorTypeId != null) {
            VendorType vendorType = vendorInfoAdapter.vendorTypeMapper.selectById(vendorTypeId);
            if (vendorType != null) {
                vendorInfoCO.setVenderTypeName(vendorType.getName());
                vendorInfoCO.setVenTypes(Arrays.asList(VendorTypeAdapter.vendorTypeToVendorTypeCO(vendorType)));
            } else {
                LOGGER.error("Vendor type not found, id : {}", vendorTypeId);
                throw new ObjectNotFoundException(VendorType.class, vendorTypeId);
            }
        }
        return vendorInfoCO;
    }
}
