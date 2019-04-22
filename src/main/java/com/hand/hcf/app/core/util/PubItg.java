package com.hand.hcf.app.core.util;

import com.hand.hcf.app.core.exception.BizException;

/**
 * Created by zhiyu.liu on 2018/4/24.
 */
public class PubItg {


    public static String getValue(String valueName, Object value, String defaultValue) {
        if(value!=null){
            return value.toString();
        }else if(defaultValue!=null){
            return defaultValue;
        }else {
            throw new BizException(valueName+" is null");
        }
    }


}
