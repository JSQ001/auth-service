package com.hand.hcf.app.mdata.location.adapter;

import com.hand.hcf.app.common.dto.LocationDTO;
import com.hand.hcf.app.mdata.location.domain.Location;
import com.hand.hcf.app.mdata.location.domain.LocationDetail;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vance on 2016/12/14.
 */
public class LocationAdapter implements Adapter<LocationDTO> {

    private Location location;

    private String language;

    public LocationAdapter(Location location, String language) {
        this.location = location;
        this.language = language;
    }

    @Override
    public LocationDTO convertToDTO() {
        if (location == null) {
            return null;
        }
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setCode(location.getCode());
        try {

            List<LocationDetail> locationDetailList = location.getLocationDetailList()
                    .stream().filter(p -> p.getLanguage().equals(language)).collect(Collectors.toList());


//            if (locationDetailList.size() > 0) {
//            }

            LocationDetail locationDetail = locationDetailList.get(0);
            locationDTO.setLanguage(locationDetail.getLanguage());
            locationDTO.setCountry(locationDetail.getCountry());
            locationDTO.setState(locationDetail.getState());
            locationDTO.setCity(locationDetail.getCity());
            locationDTO.setDistrict(locationDetail.getDistrict());
            locationDTO.setDescription(locationDetail.getDescription());

            locationDTO.setType(location.getType());
            locationDTO.setCountryCode(location.getCountry_code());
            locationDTO.setStateCode(location.getState_code());
            locationDTO.setCityCode(location.getCity_code());
            locationDTO.setDistrictCode(location.getDistrict_code());

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return locationDTO;
    }
}
