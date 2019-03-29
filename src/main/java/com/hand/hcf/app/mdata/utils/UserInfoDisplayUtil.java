package com.hand.hcf.app.mdata.utils;

import org.springframework.util.StringUtils;

public class UserInfoDisplayUtil {
    public static String recoverDeleteInfo(String info) {
        if (StringUtils.isEmpty(info)
            || !info.contains("_")) {
            return info;
        }
        int index = 0;
        String[] splits = {"_LEAVED", "_DELETE"};
        for (int i = 0; i < splits.length && index <= 0; i++) {
            index = info.indexOf(splits[i]);
        }
        if (index > 0) {
            info = info.substring(0, index);
        }
        return info;
    }
}
