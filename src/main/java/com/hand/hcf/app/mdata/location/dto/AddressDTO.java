package com.hand.hcf.app.mdata.location.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by silence on 2017/12/4.
 *
 * 州/省/直辖市Map
 * Map<String,Map<String,List<String>>> stateMap;
 * 城市Map
 * Map<String,List<String>> cityMap;
 * 地区List
 * List<String> districtList;
 *
 */
@Data
public class AddressDTO implements Serializable{
    String value;
    String label;
    List<AddressDTO> children;
}
