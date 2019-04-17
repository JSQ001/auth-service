package com.hand.hcf.app.mdata.utils;

import com.hand.hcf.app.core.exception.BizException;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Created by silence on 2017/9/26.
 */
public class DataFilteringUtil {

    public static String getDataFilterCode(String data){
        //  检查参数是否为空
        if (StringUtil.isNullOrEmpty(data)) {
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

    public static String getDataFilterName(String data){
        if (StringUtil.isNullOrEmpty(data)) {
            throw new BizException(RespCode.DataFilteringUtil_29004);
        }
        //  Name过滤单引号
        String esc = StringEscapeUtils.escapeSql(data);
        esc = esc.replace(" ","");
        //  Name过滤特殊字符
        String spe = StringUtil.filterSpecialCharacters(esc);
        //  检验Name长度是否超标
        if ( !spe.matches("^[\\u4e00-\\u9fa5_a-zA-Z0-9_]{1,100}$") ) {
            throw new BizException(RespCode.DataFilteringUtil_29002);
        }
        return spe;
    }
}
