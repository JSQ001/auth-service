package com.hand.hcf.app.core.util;

import java.util.Map;
import java.util.TreeMap;

public final class MapUtil {

    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return map;
        }
        if (map.containsKey("MPGENVAL1")){
            try {
                Map<String, String> sortMap = new TreeMap<>((k1, k2) -> {
                    Integer i1 = TypeConversionUtils.parseInt(k1.split("MPGENVAL")[1]);
                    Integer i2 = TypeConversionUtils.parseInt(k2.split("MPGENVAL")[1]);
                    return i1.compareTo(i2);
                });

                sortMap.putAll(map);
                return sortMap;
            }catch (Exception e){
                Map<String, String> sortMap = new TreeMap<>(String::compareTo);
                sortMap.putAll(map);
                return sortMap;
            }
        }else{
            Map<String, String> sortMap = new TreeMap<>(String::compareTo);
            sortMap.putAll(map);

            return sortMap;
        }

    }

}
