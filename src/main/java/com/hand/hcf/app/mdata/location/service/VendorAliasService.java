package com.hand.hcf.app.mdata.location.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.app.mdata.implement.web.LocationControllerImpl;
import com.hand.hcf.app.mdata.location.domain.VendorAlias;
import com.hand.hcf.app.mdata.location.domain.VendorAliasDetail;
import com.hand.hcf.app.mdata.location.dto.GetAliasByVendorCodeResponseDTO;
import com.hand.hcf.app.mdata.location.persistence.VendorAliasDetailMapper;
import com.hand.hcf.app.mdata.location.persistence.VendorAliasMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vance on 2016/12/20.
 */
@Service
@Slf4j
public class VendorAliasService {
    @Autowired
    private VendorAliasMapper vendorAliasMapper;
    @Autowired
    private VendorAliasDetailMapper vendorAliasDetailMapper;

    @Transactional
    public void createVendorAlias(String code, String vendorType, String alias) {
        List<VendorAlias> list = vendorAliasMapper.selectList(new EntityWrapper<VendorAlias>()
                .eq("code",code)
                .eq("vendor_type",vendorType)
                .eq("alias",alias));

        if (list == null || list.size() == 0){
            VendorAlias vendorAlias = new VendorAlias();
            vendorAlias.setAlias(alias);
            vendorAlias.setCode(code);
            vendorAlias.setVendorType(vendorType);
            vendorAliasMapper.insert(vendorAlias);
        }
    }



    public VendorAlias getSingleVendorAliasBy(String code, String vendorType, String language) {
        List<VendorAlias> vendorAliasList = vendorAliasMapper.selectList(
                new EntityWrapper<VendorAlias>()
                        .eq("code",code)
                        .eq("vendor_type",vendorType)
                        .eq("language",language));
        if (vendorAliasList == null || vendorAliasList.size() == 0) {
            throw new ObjectNotFoundException(VendorAlias.class, "Code = " + code + " VendorType = " + vendorType + " Language = " + language);
        }
        return vendorAliasList.get(0);
    }

    public List<VendorAlias> getVendorAliasByAliasAndVendorType(String language, String from, String to, String city) {
        List<VendorAlias> resultVendor = new ArrayList<>();
        List<VendorAlias> fromVendor = vendorAliasMapper.selectList(
                new EntityWrapper<VendorAlias>()
                .eq("alias",city)
                .eq("vendor_type",from)
                .eq("language",language));
        if (CollectionUtils.isEmpty(fromVendor)) {
            log.error("供应商:{}没有找到城市:{}", from, city);
        } else {
            List<String> codes = fromVendor.stream().map(VendorAlias::getCode).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(codes)) {
                codes.stream().forEach(code -> {
                    List<VendorAlias> vendorAliases = vendorAliasMapper.selectList(
                            new EntityWrapper<VendorAlias>()
                                    .eq("code",code)
                                    .eq("vendor_type",to)
                                    .eq("language",language));
                    if (CollectionUtils.isNotEmpty(vendorAliases)) {
                        resultVendor.addAll(vendorAliases);
                    }
                });
            }
        }
        return resultVendor;
    }

    public GetAliasByVendorCodeResponseDTO findVendorAliasDetailsByCode(String code, String vendorType, String language) {
        List<VendorAlias> vendorAliases = vendorAliasMapper.selectList(
                new EntityWrapper<VendorAlias>()
                        .eq("code",code)
                        .eq("vendor_type",vendorType)
                        .eq("language",language));
        if (vendorAliases == null || vendorAliases.size() == 0) {
            String message = new String("VendorType  = " + vendorType + " and code = " + code + " and Language = " + language + " not found");
            throw new ObjectNotFoundException(LocationControllerImpl.class, message);
        }
        VendorAlias vendorAlias = vendorAliases.get(0);
        GetAliasByVendorCodeResponseDTO getAliasByVendorCodeResponseDTO = new GetAliasByVendorCodeResponseDTO();
        getAliasByVendorCodeResponseDTO.setAlias(vendorAlias.getAlias());

        List<VendorAliasDetail> list = vendorAliasDetailMapper.selectList(
                new EntityWrapper<VendorAliasDetail>()
                        .eq("code",code)
                        .eq("vendor_type",vendorType));
        if (list != null && list.size() > 0) {

            getAliasByVendorCodeResponseDTO.setCityAlias(list.get(0).getCityAliasCode());
            getAliasByVendorCodeResponseDTO.setCountryAlias(list.get(0).getCountryAliasCode());
        }
        return getAliasByVendorCodeResponseDTO;
    }

    public List<GetAliasByVendorCodeResponseDTO> vendorAliasDetailsByCodes(List<String> codes, String vendorType, String language) {
        return codes.stream().map(u ->this.findVendorAliasDetailsByCode(u,vendorType,language)).collect(Collectors.toList());
    }





    public VendorAlias save(VendorAlias vendorAlias) {
        if (vendorAlias.getId() == null) {
            vendorAliasMapper.insert(vendorAlias);
        }else {
            vendorAliasMapper.updateById(vendorAlias);
        }
        return vendorAlias;
    }

    public void deleteOneById(Long id) {
        vendorAliasMapper.deleteById(id);
    }

    public List<VendorAlias> getForCityName(String code, String vendorType, String language){
        List<VendorAlias> list = vendorAliasMapper.selectList(
                new EntityWrapper<VendorAlias>()
                .eq("code",code)
                .eq("vendor_type",vendorType)
                .eq("language",language));
        return list;
    }

    public List<VendorAlias> getForCode(String vendorCode,String vendorType,String language){
        List<VendorAlias> list = vendorAliasMapper.selectList(
                new EntityWrapper<VendorAlias>()
                        .eq("alias",vendorCode)
                        .eq("vendor_type",vendorType)
                        .eq("language",language));
        return list;
    }


}
