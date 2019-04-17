package com.hand.hcf.app.mdata.location.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import com.hand.hcf.app.mdata.location.adapter.SolrLocationAdapter;
import com.hand.hcf.app.mdata.location.domain.Location;
import com.hand.hcf.app.mdata.location.domain.LocationDetail;
import com.hand.hcf.app.mdata.location.domain.VendorAlias;
import com.hand.hcf.app.mdata.location.dto.SolrLocationDTO;
import com.hand.hcf.app.mdata.location.persistence.LocationDetailMapper;
import com.hand.hcf.app.mdata.location.persistence.LocationMapper;
import com.hand.hcf.app.mdata.location.persistence.VendorAliasMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ray Ma on 2018/3/15.
 */
@Service
public class LocationDBService {

    @Autowired
    private LocationService locationService;
    @Autowired
    private VendorAliasService vendorAliasService;


    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private VendorAliasMapper vendorAliasMapper;


    @Autowired
    private LocationDetailMapper locationDetailMapper;
    private final String Location = "sys_location";
    private static final Logger log = LoggerFactory.getLogger(LocationDBService.class);


    public List<SolrLocationDTO> addOrUpdate(Location location, Boolean updateSolrIndex) {
        log.debug("Request to create or update location vendorInfo with code:{}", location.getCode());
        //TODO 新加code ，添加主数据 ，主数据详情 ， 供应商编码
//        List<Location> locations = locationRepository.findByCode(location.getCode());
//        if(CollectionUtils.isEmpty(locations)){
//            Location newLocation = new Location();
//            locationRepository.save(newLocation);
//
//            LocationDetail locationDetail = new LocationDetail();
//            locationDetailRepository.save(locationDetail);
//        }
        Location existLocation = locationService.findOneByCode(location.getCode());
        if (existLocation == null) {
            throw new ValidationException(new ValidationError("sys_location", "Location with code" + location.getCode() + "not exist"));
        }
        //清空LocationDetailList防止出现集合公用情况
        existLocation.setVendorAliasList(null);
        existLocation.getLocationDetailList().stream().map(u -> {
            u.setVendorAliasList(null);
            return u;
        }).collect(Collectors.toList());

        List<VendorAlias> vendorAliases = location.getVendorAliasList();
        List<SolrLocationDTO> solrLocationDTOs = new ArrayList<>();
        vendorAliases.stream().forEach(u -> {
            List<VendorAlias> newVendorAliasList = new ArrayList<>();
            List<VendorAlias> vList = vendorAliasMapper.selectList(
                    new EntityWrapper<VendorAlias>()
                            .eq("code",u.getCode())
                            .eq("vendor_type",u.getVendorType())
                            .eq("language",u.getLanguage()));
            VendorAlias existVendor = null;
            if (vList != null && vList.size() > 0){
                existVendor = vList.get(0);
            }
            VendorAlias result;
            //更新数据库数据
            if (existVendor != null) {
                existVendor.setLocation(null);
                existVendor.setVendorType(u.getVendorType());
                existVendor.setAlias(u.getAlias());
                existVendor.setLanguage(u.getLanguage());
                existVendor.setVendorCountryCode(u.getVendorCountryCode());
                existVendor.setVendorCode(u.getVendorCode());
                result = vendorAliasService.save(existVendor);
            } else {
                u.setId(null);
                result = vendorAliasService.save(u);
            }
            newVendorAliasList.add(result);
            existLocation.setVendorAliasList(newVendorAliasList);
            SolrLocationAdapter solrLocationAdapter = new SolrLocationAdapter(existLocation, u.getLanguage(), u.getVendorType());
            solrLocationDTOs.add(solrLocationAdapter.convertToDTO());
        });
        return solrLocationDTOs;
    }

    /**
     * 单量更新供应商信息和索引信息
     *
     * @param code
     * @param vendorType
     * @param language
     * @param deleteSolrIndex
     */
    public void deleteVendorAliasSingle(String code, String vendorType, String language, Boolean deleteSolrIndex) {
        log.debug("Request to delete One vendorInfo with Param Code:{},vendorType:{},language:{} and deleteSolrIndex:{}", code, vendorType, language, deleteSolrIndex);
        VendorAlias vendorAlias = null;
        List<VendorAlias> vList = vendorAliasMapper.selectList(
                new EntityWrapper<VendorAlias>()
                        .eq("code",code)
                        .eq("vendor_type",vendorType)
                        .eq("language",language));

        if (vList != null && vList.size() > 0){
            vendorAlias = vList.get(0);
        }

        if (vendorAlias == null) {
            throw new ValidationException(new ValidationError("VendorAlias", code + ":" + vendorType + ":" + language + " not exist"));
        }

        vendorAliasMapper.deleteById(vendorAlias.getId());
        if (deleteSolrIndex) {

        }
    }

    public Location createLocation(Location location) {
        Location exist = locationService.findOneByCode(location.getCode());
        if(exist != null){
            throw new ValidationException(new ValidationError("location with code :"+exist.getCode(),"exist"));
        }
        List<LocationDetail> lList = location.getLocationDetailList();
        List<VendorAlias> vList = location.getVendorAliasList();
        locationMapper.insert(location);
        lList.stream().forEach(u->{
            locationDetailMapper.insert(u);
        });
        vList.stream().forEach(u->{
            vendorAliasMapper.insert(u);
        });
        return location;
    }
    public Location updateLocation(Location location) {
        Location exist = locationService.findOneByCode(location.getCode());
        if(exist == null){
            throw new ValidationException(new ValidationError("location with code :"+location.getCode(),"not exist"));
        }
        location.setId(exist.getId());
        List<LocationDetail> lList = location.getLocationDetailList();
        List<VendorAlias> vList = location.getVendorAliasList();
        locationMapper.updateById(location);

        lList.stream().forEach(u->{
            if (u.getId() == null){
                locationDetailMapper.insert(u);
            }else{
                locationDetailMapper.updateById(u);
            }
        });
        vList.stream().forEach(u->{
            if (u.getId() == null){
                vendorAliasMapper.insert(u);
            }else{
                vendorAliasMapper.updateById(u);
            }
        });
        return location;
    }
}
