package com.hand.hcf.app.core.util;

import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.google.gson.Gson;
import com.hand.hcf.app.core.enums.DataAuthFilterMethodEnum;
import com.hand.hcf.app.core.exception.BizException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/18 14:02
 * @remark
 */
public final class DataAuthorityUtil {

    /**
     * 数据权限特定标志
     */
    public static final String DATA_AUTH_LABEL = "$HCFDATA";

    /**
     * 表名
     */
    public static final String TABLE_NAME = "TableName";

    /**
     * 表别名
     */
    public static final String TABLE_ALIAS = "TableAlias";

    /**
     * 账套ID列名标识
     */
    public static final String SOB_COLUMN = "SOB";

    /**
     * 公司ID列名标识
     */
    public static final String COMPANY_COLUMN = "COMPANY";

    /**
     * 部门ID列名标识
     */
    public static final String UNIT_COLUMN = "UNIT";

    /**
     * 员工ID列名标识
     */
    public static final String EMPLOYEE_COLUMN = "EMPLOYEE";

    /**
     * 默认账套ID对应的列名
     */
    public static final String DEFAULT_SOB_COLUMN = "set_of_books_id";

    /**
     * 默认公司ID对应的列名
     */
    public static final String DEFAULT_COMPANY_COLUMN = "company_id";

    /**
     * 默认部门ID对应的列名
     */
    public static final String DEFAULT_UNIT_COLUMN = "unit_id";

    /**
     * 默认员工ID对应的列名
     */
    public static final String DEFAULT_EMPLOYEE_COLUMN = "employee_id";

    /**
     * 列标识对应默认列配置
     */
    public static final Map<String,String> defaultColumnProperties = new HashMap<>();

    /**
     * 筛选条件添加方式 DataAuthFilterMethodEnum值
     */
    public static final String FILTER_METHOD = "FilterMethod";

    /**
     * 关联表，可关联多个，需要定义别名
     * table1 t1,table2 t2
     */
//    public static final String RELATED_TABLE_NAMES = "RELATED_TABLE_NAMES";

    /**
     * 筛选条件添加方式为关联表，需定义表关联关系
     * 用{alias}标注表别名 (alias根据实际情况而定),并且该标识也标注了条件筛选位置
     */
    public static final String CUSTOM_SQL = "customSQL";

    /**
     * 默认表别名
     */
    public static final String defaultTableAlias = "base";

    static{
        defaultColumnProperties.put(SOB_COLUMN,getDataAuthTypeLabelValue(DEFAULT_SOB_COLUMN,null,null));
        defaultColumnProperties.put(COMPANY_COLUMN,getDataAuthTypeLabelValue(DEFAULT_COMPANY_COLUMN,null,null));
        defaultColumnProperties.put(UNIT_COLUMN,getDataAuthTypeLabelValue(DEFAULT_UNIT_COLUMN,null,null));
        defaultColumnProperties.put(EMPLOYEE_COLUMN,getDataAuthTypeLabelValue(DEFAULT_EMPLOYEE_COLUMN,null,null));
    }


    /**
     * 组装列配置信息
     * @param columnName     列名
     * @param filterMethod   条件列席
     * @param customSQL      自定义sql，需要在不影响sql正确含义的地方，用{alias}标注表别名 (alias根据实际情况而定)
     * @return value
     */
    public static String getDataAuthTypeLabelValue(String columnName,
                                              DataAuthFilterMethodEnum filterMethod,
                                              String customSQL){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getString(columnName)).append(";")
                .append(getString(filterMethod == null ? null : filterMethod.name())).append(";");
        if(DataAuthFilterMethodEnum.CUSTOM_SQL.equals(filterMethod)){
            stringBuffer.append(getString(customSQL));
        }
        return stringBuffer.toString();
    }

    /**
     * 组装列配置信息
     * @param dataAuthType   数据类型
     * @param columnName     列名
     * @param filterMethod   条件列席
     * @param customSQL      自定义sql，需要在不影响sql正确含义的地方，用{alias}标注表别名 (alias根据实际情况而定)
     * @return dataAuthType : value
     */
    public static String getDataAuthTypeLabel(String dataAuthType,
                                              String columnName,
                                              DataAuthFilterMethodEnum filterMethod,
                                              String customSQL){
        if(StringUtils.isEmpty(dataAuthType)){
            return "";
        }
        return dataAuthType + ":" + getDataAuthTypeLabelValue(columnName,filterMethod,customSQL);
    }

    private static String getString(String str){
        if(StringUtils.isEmpty(str)){
            return "";
        }
        return str;
    }

    /**
     * 生成数据权限标识
     * @return
     */
    public static String getDataAuthLabel(Map<String,?> map){
        StringBuffer dataAuthLabelBuffer = new StringBuffer(DATA_AUTH_LABEL);
        dataAuthLabelBuffer.append("{");
        map.entrySet().forEach(entry -> {
            dataAuthLabelBuffer.append(entry.getKey()).append(":").append("\"").append(entry.getValue().toString()).append("\",");
        });
        String dataAuthLabel = dataAuthLabelBuffer.toString();
        boolean b = StringUtils.endsWith(dataAuthLabel, ",");
        if(b){
            dataAuthLabel = dataAuthLabel.substring(0,dataAuthLabel.length() - 1);
        }
        return dataAuthLabel + "}";
    }

    /**
     * 解析数据权限标识
     * @param dataAuthLabel  数据权限标识{...}
     * @return
     */
    public static Map<String,String> analysisDataAuthLabel(String dataAuthLabel){
        String analysisString = new String(dataAuthLabel);
        if(dataAuthLabel.contains(DATA_AUTH_LABEL)){
            analysisString = dataAuthLabel.replace(DATA_AUTH_LABEL,"");
        }
        Gson gson = new Gson();
        Map<String,String> map = new HashMap<>();
        try {
            map = gson.fromJson(analysisString, map.getClass());
            return map;
        }catch(Exception e){
            e.printStackTrace();
            throw new BizException(RespCode.SYS_DATA_AUTHORITY_ANALYSIS_ERROR);
        }

    }
}
