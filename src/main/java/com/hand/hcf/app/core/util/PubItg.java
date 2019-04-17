package com.hand.hcf.app.core.util;

import com.hand.hcf.app.core.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhiyu.liu on 2018/4/24.
 */
@Service
public class PubItg {

    private static final Logger log = LoggerFactory.getLogger(PubItg.class);

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
