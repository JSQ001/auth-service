package com.hand.hcf.app.base.util;

import com.hand.hcf.app.core.exception.BizException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by silence on 2017/9/26.
 */
public class DataFilteringUtil {

    public static String getDataFilterCode(String data){
        //  检查参数是否为空
        if (StringUtils.isEmpty(data)) {
            throw new BizException(RespCode.DataFilteringUtil_29003);
        }
        //  Code过滤单引号
        String esc = StringEscapeUtils.escapeSql(data);
        //  Code过滤特殊字符
        String spe = StringUtil.filterSpecialCharacters(esc);
        //  检验Code长度是否超标,是否包含中文
        if ( !spe.matches("^[0-9a-zA-Z_]{1,35}$" )) {
            throw new BizException(RespCode.DataFilteringUtil_29001);
        }
        return spe;
    }


}
