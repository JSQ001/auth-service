package com.hand.hcf.app.core.plugin;

import com.baomidou.mybatisplus.entity.TableFieldInfo;
import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.PluginUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;
import com.hand.hcf.app.core.component.ApplicationContextUtils;
import com.hand.hcf.app.core.enums.DataAuthFilterMethodEnum;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.handler.DataAuthorityMetaHandler;
import com.hand.hcf.app.core.util.DataAuthorityUtil;
import com.hand.hcf.app.core.util.ReflectHelper;
import com.hand.hcf.app.core.util.RespCode;
import com.hand.hcf.app.core.util.StringUtil;
import com.hand.hcf.app.core.web.dto.DataAuthValuePropertyDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/12 16:01
 * @remark 数据权限
 */
@Slf4j
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class DataAuthProcessInterceptor implements Interceptor {

    private static final Pattern compile = Pattern.compile("[\\s\\t\\n]{1,}", Pattern.CASE_INSENSITIVE);

    private static final String DELIMITER_DOT = ".";

    private DataAuthorityMetaHandler dataAuthorityMetaHandler;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        initMetaHandler();
        StatementHandler statementHandler = (StatementHandler) PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
        MappedStatement ms = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
        if (!SqlCommandType.SELECT.equals(ms.getSqlCommandType())) {
            return invocation.proceed();
        }
        BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
        String sql = boundSql.getSql();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        MapperMethod.ParamMap<?> parameterMap;
        if(MapperMethod.ParamMap.class.isAssignableFrom(boundSql.getParameterObject().getClass())){
            parameterMap = (MapperMethod.ParamMap<?>) boundSql.getParameterObject();
        }else{
            parameterMap = new MapperMethod.ParamMap<>();
        }

        List<String> parameters = null;
        String methodName = ms.getId().substring(ms.getId().lastIndexOf(DELIMITER_DOT) + 1);
        if(CollectionUtils.isEmpty(parameterMappings)){
            Class<?> aClass = Class.forName(ms.getId().substring(0, ms.getId().lastIndexOf(DELIMITER_DOT)));
            Method[] methods = aClass.getMethods();
            for(Method method : methods){
                if(method.getName().equals(methodName)){
                    ParamNameResolver paramNameResolver = new ParamNameResolver(ms.getConfiguration(), method);
                    String[] names = paramNameResolver.getNames();
                    if(Arrays.asList(names).stream().allMatch(e -> parameterMap.containsKey(e))){
                        parameters = Arrays.asList(names);
                        break;
                    }
                }
            }
        }
        // 判断sql是否包含权限标志
        // sql中不包含数据权限标志，则在条件中查找，并将其替换至sql中
        if(! sql.contains(DataAuthorityUtil.DATA_AUTH_LABEL)){
            sql = replaceDataAuthLabelInParameterToSql(sql,parameterMappings,parameters,parameterMap);
        }
        //mybatis封装的基础查询方法，默认使用数据权限标记
        TableInfo tableInfo = null;
        //拦截mybatis封装的查询方法
//        if(! DataAuthorityUtil.checkMapperIsIgnore(ms.getId())){
//            if(DataAuthorityUtil.selectOperationList.contains(methodName)){
//                if(!sql.contains(DataAuthorityUtil.DATA_AUTH_LABEL)){
//                    Class<?> type = ms.getResultMaps().get(0).getType();
//                    tableInfo = TableInfoHelper.getTableInfo(type);
//                    if(tableInfo != null){
//                        String tableName = tableInfo.getTableName();
//                        String regex = tableName.toLowerCase() + "+[\\s\\t\\n]{1,}+where+[\\s\\t\\n]{1,}";
//                        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
//                        Matcher m = p.matcher(sql);
//                        if(m.find()){
//                            String group = m.group(0);
//                            sql = sql.replace(group,group + " " + DataAuthorityUtil.getDataAuthBasicLabel(tableName,"") + " and ");
//                        }
//                    }
//                }
//            }
//        }
        if(sql.contains(DataAuthorityUtil.DATA_AUTH_LABEL)){
            sql = handleDataAuthLabel(sql,tableInfo);
            ReflectHelper.setFieldValue(boundSql, "sql", sql);
            ReflectHelper.setFieldValue(boundSql, "parameterMappings", parameterMappings);
            ReflectHelper.setFieldValue(boundSql, "parameterObject", parameterMap);
        }
        return invocation.proceed();
    }

    /**
     * 处理sql
     * @param sql
     * @return
     */
    private String handleDataAuthLabel(String sql, TableInfo tableInfo){
        String dataAuthLabel = getDataAuthLabel(sql);
        Map<String, String> dataAuthMap = DataAuthorityUtil.analysisDataAuthLabel(dataAuthLabel);
        checkDataAuthLabel(dataAuthMap);
        if(tableInfo == null){
            tableInfo = getTableInfoByTableName(DataAuthorityUtil.getMapValue(dataAuthMap,DataAuthorityUtil.TABLE_NAME));
        }
        sql = updateBoundSql(sql, dataAuthLabel, dataAuthMap, tableInfo);
        if(sql.contains(DataAuthorityUtil.DATA_AUTH_LABEL)){
            sql = handleDataAuthLabel(sql,tableInfo);
        }
        return sql;
    }

    /**
     * 初始化处理器
     */
    private void initMetaHandler(){
        ApplicationContext applicationContext = ApplicationContextUtils.getApplicationContext();
        try {
            this.dataAuthorityMetaHandler = applicationContext.getBean(DataAuthorityMetaHandler.class);
        }catch (Exception e){
            // 获取不到bean，则直接赋值默认数据
            this.dataAuthorityMetaHandler = new DataAuthorityMetaHandler() {
                @Override
                public boolean checkEnabledDataAuthority() {
                    return false;
                }

                @Override
                public Map<String, String> updateDataAuthMap(Map<String, String> dataAuthMap) {
                    return dataAuthMap;
                }

                @Override
                public List<Map<String, List<DataAuthValuePropertyDTO>>> getDataAuthValueProperties() {
                    return null;
                }
            };
        }
    }

    /**
     * 将参数中数据权限标识替换到sql相应位置上
     * @param sql   待执行的sql
     * @param parameterMappings
     * @param parameterMap
     * @return
     */
    private String replaceDataAuthLabelInParameterToSql(String sql, List<ParameterMapping> parameterMappings, List<String> parameters, MapperMethod.ParamMap<?> parameterMap){
        Set<? extends Map.Entry<String, ?>> entries = parameterMap.entrySet();
        Iterator<? extends Map.Entry<String, ?>> iterator = entries.iterator();
        while(iterator.hasNext()){
            Map.Entry<String, ?> entry = iterator.next();
            Object value = entry.getValue();
            if(value != null){
                boolean contains = value.toString().contains(DataAuthorityUtil.DATA_AUTH_LABEL);
                if(contains){
                    Integer i = null;
                    ParameterMapping parameterMapping = null;
                    if(CollectionUtils.isNotEmpty(parameterMappings)){
                        List<ParameterMapping> collect = parameterMappings.stream().filter(parameter -> {
                            return parameter.getProperty().equals(entry.getKey());
                        }).collect(Collectors.toList());
                        if(CollectionUtils.isNotEmpty(collect)) {
                            parameterMapping = collect.get(0);
                            i = parameterMappings.indexOf(parameterMapping);
                        }
                    }else if(CollectionUtils.isNotEmpty(parameters)){
                        List<String> collect = parameters.stream().filter(parameter -> {
                            return parameter.equals(entry.getKey());
                        }).collect(Collectors.toList());
                        if(CollectionUtils.isNotEmpty(collect)) {
                            i = parameters.indexOf(collect.get(0));
                        }
                    }
                    if(i != null){
                        int index = 0;
                        for(int m = 0; m <= i; m ++){
                            index = sql.indexOf("?",index + 1);
                        }
                        StringBuffer stringBuffer = new StringBuffer(sql);
                        sql = stringBuffer.replace(index, index + 1, value.toString()).toString();
                        if(parameterMapping != null){
                            parameterMappings.remove(parameterMapping);
                        }
                    }
                    iterator.remove();
                }
            }
        }
        return sql;
    }

    /**
     * 若是sql中包含数据权限标识，直接替换sql中的标识
     * @param sql
     * @param dataAuthMap
     * @return
     */
    private String updateBoundSql(String sql,String dataAuthLabel, Map<String, String> dataAuthMap,TableInfo tableInfo){
        StringBuffer updatedSql = new StringBuffer();
        String sqlBackup = sql;
        // 判断是否启用数据权限，要是不启用，直接替换为恒等式
        if(dataAuthorityMetaHandler.checkEnabledDataAuthority()){
            List<Map<String, List<DataAuthValuePropertyDTO>>> dataAuthValuePropertyList = dataAuthorityMetaHandler.getDataAuthValueProperties();
            boolean hasEmpty = CollectionUtils.isEmpty(dataAuthValuePropertyList) || dataAuthValuePropertyList.stream().anyMatch(dataAuthValueProperties -> {
                return dataAuthValueProperties.size() == 0 || dataAuthValueProperties.values().stream().anyMatch(dataAuthValuePropertyDTOS -> dataAuthValuePropertyDTOS.size() == 0);
            });
            // 如果某个规则下明细返回空集合，则表示该规则所有数据范围均为全部，则直接返回恒等式
            if(hasEmpty){
                updatedSql.append(" 1 = 1 ");
            }else {
                updateDataAuthMap(dataAuthMap);
                // 全部转换为小写，容易比对
                sql = sql.toLowerCase();
                // 获取表别名
                String tableName = DataAuthorityUtil.getMapValue(dataAuthMap,DataAuthorityUtil.TABLE_NAME);
                // 获取表别名
                String tableAlias = DataAuthorityUtil.getMapValue(dataAuthMap,DataAuthorityUtil.TABLE_ALIAS);
                // 若表别名为空，则需要从sql中解析数据
                if (StringUtils.isEmpty(tableAlias)) {
                    int labelIndex = sql.indexOf(DataAuthorityUtil.DATA_AUTH_LABEL.toLowerCase());
                    sql = sql.substring(0, labelIndex);
                    String tableRegex = "[\\s\\t\\n\\,]{1,}+" + tableName.toLowerCase() + "+[\\s\\t\\n\\,]{1,}";
                    Pattern tablePattern = Pattern.compile(tableRegex, Pattern.CASE_INSENSITIVE);
                    Matcher tableMatcher = tablePattern.matcher(sql);
                    String tableSplit = null;
                    if(tableMatcher.find()){
                        tableSplit = tableMatcher.group(0);
                    }else{
                        throw new BizException(RespCode.SYS_DATA_AUTHORITY_TABLE_NOT_FOUND, new String[]{tableName});
                    }
                    String[] split = sql.split(tableSplit);
                    // 数组长度为1，表示未匹配到表
                    if (split.length == 1) {
                        throw new BizException(RespCode.SYS_DATA_AUTHORITY_TABLE_NOT_FOUND, new String[]{tableName});
                        // 数据长度为2，表示只有表名只出现了一次，直接获取别名
                    } else if (split.length == 2) {
                        String regex = tableName.toLowerCase() + "+[\\s\\t\\n]{1,}+where+[\\s\\t\\n]{1,}";
                        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                        Matcher m = p.matcher(sql);
                        // 如果表没有别名
                        if (m.find()) {
                            // 是否有关联表
                            boolean relatedTable = checkBaseTableRelatedOthers(dataAuthValuePropertyList,dataAuthMap);
                            // 如果有关联表，则需要设置表别名
                            if (relatedTable) {
                                String group = m.group(0);
                                int index = sql.indexOf(group);
                                String substring = sqlBackup.substring(index, index + group.length());
                                Matcher matcher = compile.matcher(substring);
                                if (matcher.find()) {
                                    String group1 = matcher.group(0);
                                    // 默认设置为base + 6位随机字符串
//                                    String alias = DataAuthorityUtil.DEFAULT_TABLE_ALIAS + "_" + RandomStringUtils.random(6, 97, 122, false, false);
                                    String alias = DataAuthorityUtil.DEFAULT_TABLE_ALIAS;
                                    String replace = matcher.replaceFirst(" " + alias + group1);
                                    tableAlias = alias;
                                    DataAuthorityUtil.setMapEntry(dataAuthMap,DataAuthorityUtil.TABLE_ALIAS, tableAlias);
                                    sqlBackup = sqlBackup.replace(group, replace);
                                }
                            }
                            // 如果表已经有别名，则表示需要解析sql，获取表别名
                        } else {
                            String substring = split[1].trim();
                            int startIndex = sql.indexOf(tableName.toLowerCase()) + tableName.length();
                            int aliasLength = substring.indexOf("where") + 1;
                            // 防止表名到where关键字中还有其他表，并且没有用空格间隔，直接取第一个逗号前的数据
                            // 使用 外连接方法肯定会有空格，所以不用考虑
                            String alias = sqlBackup.substring(startIndex, startIndex + aliasLength).trim().split(",")[0];
                            Matcher matcher = compile.matcher(alias);
                            if (matcher.find()) {
                                String splitChar = matcher.group(0);
                                alias = alias.substring(0, alias.indexOf(splitChar));
                            }
                            tableAlias = alias;
                            DataAuthorityUtil.setMapEntry(dataAuthMap,DataAuthorityUtil.TABLE_ALIAS, tableAlias);
                        }
                        // 如果长度超过2，则表示需要解析sql，获取表别名
                    } else {
                        // sql有效的表别名所在Index
                        Integer effectiveTableAliasIndex = getEffectiveTableAlias(sql, tableName);
                        String substring = sqlBackup.substring(effectiveTableAliasIndex).trim();
                        Matcher matcher = compile.matcher(substring);
                        if (matcher.find()) {
                            String splitChar = matcher.group(0);
                            String alias = substring.substring(0, substring.indexOf(splitChar));
                            tableAlias = alias;
                            DataAuthorityUtil.setMapEntry(dataAuthMap,DataAuthorityUtil.TABLE_ALIAS, tableAlias);
                        }
                    }
                }
                dataAuthValuePropertyList.stream().forEach(dataAuthValueProperties -> {
                    String dataAuthSql = assembleSqlByDataAuthProperties(dataAuthMap, dataAuthValueProperties,tableInfo);
                    if(StringUtils.isEmpty(updatedSql)){
                        updatedSql.append("(").append(dataAuthSql);
                    }else{
                        updatedSql.append(" or ").append(dataAuthSql);
                    }
                });
                updatedSql.append(")");
            }
        }else{
            updatedSql.append(" 1 = 1 ");
        }
        // 考虑到标识符相同，想对应的筛选的语句肯定也一样，所以直接全部替换
        String replace = sqlBackup.replace(DataAuthorityUtil.DATA_AUTH_LABEL  + dataAuthLabel, updatedSql);
        log.debug("数据权限重置sql: {}" ,replace);
        return replace;
    }

    /**
     * 判断基础表是否关联了其他表
     * @param dataAuthValuePropertyList
     * @return
     */
    private Boolean checkBaseTableRelatedOthers(List<Map<String, List<DataAuthValuePropertyDTO>>> dataAuthValuePropertyList,
                                                Map<String, String> dataAuthMap){
        return dataAuthValuePropertyList.stream().anyMatch(dataAuthValueProperties -> {
            return dataAuthValueProperties.values().stream().anyMatch(dataAuthValuePropertyDTOS -> {
                return dataAuthValuePropertyDTOS.stream().anyMatch(dataAuthValuePropertyDTO -> {
                    String dataLabelValue = DataAuthorityUtil.getMapValue(dataAuthMap,dataAuthValuePropertyDTO.getDataType());
                    if(StringUtils.isNotEmpty(dataLabelValue)){
                        String[] split = dataLabelValue.split(DataAuthorityUtil.DATA_AUTH_TYPE_LABEL_SEPARATOR_REGEX);
                        if(split.length > 1){
                            return split[1].equals(DataAuthFilterMethodEnum.CUSTOM_SQL.name());
                        }
                    }
                    //以下通过模糊匹配不是很准确
//                    String regex = ";+[\\s\\t\\n]{0,}" + DataAuthFilterMethodEnum.CUSTOM_SQL.name() + "+[\\s\\t\\n]{0,}+;";
//                    Pattern p = Pattern.compile(regex);
//                    Matcher matcher = p.matcher(dataLabelValue == null ? "" : dataLabelValue);
//                    if (matcher.find()) {
//                        return true;
//                    }
                    return false;
                });
            });
        });
    }

    /**
     * 根据配置的属性，生成相应的sql
     * @param dataAuthMap
     * @param dataAuthValueProperties
     * @return
     */
    private String assembleSqlByDataAuthProperties(Map<String, String> dataAuthMap,
                                                   Map<String,List<DataAuthValuePropertyDTO>> dataAuthValueProperties,
                                                   TableInfo tableInfoByTableName){
        String tableAlias = DataAuthorityUtil.getMapValue(dataAuthMap,DataAuthorityUtil.TABLE_ALIAS);
        // 支持后期项目开发视图，当通过表名查不到对应domain类时，默认为表中包含该列
        boolean foundTable = tableInfoByTableName == null ? false : true;
        StringBuffer stringBuffer = new StringBuffer();
        Map<String, String> defaultColumnProperties = DataAuthorityUtil.defaultColumnProperties;
        stringBuffer.append("(");
        dataAuthValueProperties.entrySet().stream().forEach(entry -> {
            log.debug("数据权限规则 {} 开始拼接条件！",entry.getKey());
            stringBuffer.append("(");
            StringBuffer first = new StringBuffer("");
            entry.getValue().stream().forEach(dataAuthValuePropertyDTO -> {
                // 数据范围为全选，不作为筛选条件
                if(dataAuthValuePropertyDTO.getAllFlag()){
                    return;
                }
                // 数据类型
                String dataType = dataAuthValuePropertyDTO.getDataType();
                List<String> valueKeyList = dataAuthValuePropertyDTO.getValueKeyList();
                if(CollectionUtils.isEmpty(valueKeyList)){
                    throw new BizException(RespCode.SYS_DATA_AUTHORITY_COLUMN_VALUES_EMPTY,new String[]{dataType});
                }
                String columnProperty = DataAuthorityUtil.getMapValue(dataAuthMap,dataType);
                // 当配置信息为空时，获取默认信息
                if(StringUtils.isEmpty(columnProperty)){
                    columnProperty = DataAuthorityUtil.getMapValue(defaultColumnProperties,dataType);
                }
                if(StringUtils.isEmpty(columnProperty)){
                    log.debug("数据类型" + dataType + "未匹配到列信息配置！");
                    throw new BizException(RespCode.SYS_DATA_AUTHORITY_COLUMN_PROPERTIES_NONE,new String[]{dataType});
                }
                String[] split = columnProperty.split(DataAuthorityUtil.DATA_AUTH_TYPE_LABEL_SEPARATOR_REGEX);
                int count = StringUtil.substringCount(columnProperty, DataAuthorityUtil.DATA_AUTH_TYPE_LABEL_SEPARATOR);
                if(split.length != 1 && count != 2){
                    log.debug("数据类型" + dataType + "对应列配置信息错误！");
                    throw new BizException(RespCode.SYS_DATA_AUTHORITY_COLUMN_PROPERTIES_ERROR,new String[]{dataType});
                }
                // 列名
                String columnNames = split[0];
                // 条件添加方式
                String filterMethod = "";
                // 自定义sql
                String customSQL = "";
                if(count == 2){
                    filterMethod = split[1];
                    if(split.length == 3){
                        customSQL = split[2];
                    }
                }
                String[] columnNameSplit = columnNames.split(",");
                for(String columnName : columnNameSplit){
                    // 通过列筛选
                    if(StringUtils.isEmpty(filterMethod) || DataAuthFilterMethodEnum.TABLE_COLUMN.name().equals(filterMethod)){
                        String metaFieldName = null;
                        Class metaPropertyType = null;
                        if(foundTable){
                            List<TableFieldInfo> fieldList = tableInfoByTableName.getFieldList();
                            List<TableFieldInfo> collect = fieldList.stream().filter(field -> {
                                return columnName.equalsIgnoreCase(field.getColumn());
                            }).collect(Collectors.toList());
                            // 没有找到对应列，试着匹配ID
                            if(CollectionUtils.isEmpty(collect)){
                                if(columnName.equalsIgnoreCase(tableInfoByTableName.getKeyColumn())){
                                    metaFieldName = tableInfoByTableName.getKeyProperty();
                                    metaPropertyType = Long.class;
                                }
                            }else{
                                metaFieldName = collect.get(0).getProperty();
                                metaPropertyType = collect.get(0).getPropertyType();
                            }
                            if(StringUtils.isEmpty(metaFieldName)){
                                log.debug("数据类型" + dataType + "未匹配到列");
                                return;
                            }
                        }
                        if(StringUtils.isNotEmpty(first.toString())){
                            stringBuffer.append("and ");
                        }else{
                            first.append("not first");
                        }

                        if("EXCLUDE".equals(dataAuthValuePropertyDTO.getFiltrateMethod())){
                            stringBuffer.append("not ");
                        }
                        stringBuffer.append("exists (select 1 from dual where ");
                        if(StringUtils.isNotEmpty(tableAlias)){
                            stringBuffer.append(tableAlias).append(".");
                        }
                        stringBuffer.append(columnName).append(" in (");
                        String values = "";
                        if(metaFieldName == null || Number.class.isAssignableFrom(metaPropertyType)){
                            values = valueKeyList.stream().collect(Collectors.joining(","));
                        }else{
                            values = valueKeyList.stream().map(valueKey ->{
                                return "'" + valueKey + "'";
                            }).collect(Collectors.joining(","));
                        }
                        stringBuffer.append(values).append(")) ");
                        // 自定义sql
                    }else if(DataAuthFilterMethodEnum.CUSTOM_SQL.name().equals(filterMethod)){
                        if(StringUtils.isNotEmpty(first.toString())){
                            stringBuffer.append("and ");
                        }else{
                            first.append("not first");
                        }
                        if("EXCLUDE".equals(dataAuthValuePropertyDTO.getFiltrateMethod())){
                            stringBuffer.append("not ");
                        }
                        stringBuffer.append("exists (");
                        // 关联表表别名
                        String relatedTableAlias = extractMessageFirst(customSQL);
                        if(StringUtils.isEmpty(relatedTableAlias)){
                            log.error("关联表未指定表别名，可在自定义sql中通过 {alias} 指定表别名，同时也标注筛选条件替换位置！");
                            throw new BizException(RespCode.SYS_DATA_AUTHORITY_RELATED_TABLE_ALIAS_EMPTY);
                        }
                        String relatedTableCondition = " " + relatedTableAlias + "." + columnName + " in(";
                        // 分配表默认全部为ID
                        String values = valueKeyList.stream().collect(Collectors.joining(","));
                        relatedTableCondition = relatedTableCondition + values + ")";
                        String replace = customSQL.replace("{" + relatedTableAlias + "}", relatedTableCondition);
                        stringBuffer.append(replace).append(")");
                    }else{
                        log.debug("数据权限只支持列筛选及自定义sql两种方式！");
                        throw new BizException(RespCode.SYS_DATA_AUTHORITY_DATA_TYPE_ERROR);
                    }
                }
            });
            stringBuffer.append(") or ");
        });
        // 将最后一个or替换为右括号
        stringBuffer.replace(stringBuffer.length() - 4,stringBuffer.length(),")");
        String result = stringBuffer.toString();
        log.debug("数据权限筛选条件sql: {}",result);
        return result;
    }

    /**
     * 更新数据权限表字段信息
     * @param dataAuthMap
     * @return
     */
    private Map<String,String> updateDataAuthMap(Map<String, String> dataAuthMap){
        return dataAuthorityMetaHandler.updateDataAuthMap(dataAuthMap);
    }

    /**
     * 获取一段sql里有效的表别名
     * 原理： 表名与标识之间每个右括号肯定能找到对应的左括号，反之则不在一个作用区间
     * @param str
     * @return 别名所在的Index
     */
    private Integer getEffectiveTableAlias(String str,String tableName){
        int lastRightBracketIndex = str.lastIndexOf(")");
        int lastTableIndex = str.lastIndexOf(tableName.toLowerCase());
        // 两个之间没有括号
        if(lastRightBracketIndex == -1 || lastRightBracketIndex < lastTableIndex){
            return lastTableIndex + tableName.length();
        }
        int lastLeftBracketIndex = str.lastIndexOf("(");
        // 右括号后面的左括号全部替换成其他特殊字符
        StringBuffer stringBuffer = new StringBuffer(str);
        if(lastRightBracketIndex < lastLeftBracketIndex){
            stringBuffer.replace(lastLeftBracketIndex, lastLeftBracketIndex + 1, "-");
            return getEffectiveTableAlias(stringBuffer.toString(),tableName);
        }
        // 右括号与表之间没有左括号，则将表名替换为相同长度的字符串
        if(lastLeftBracketIndex < lastTableIndex){
            int length = tableName.length();
            String tablePlaceholder = RandomStringUtils.randomNumeric(length);
            stringBuffer.replace(lastTableIndex, lastTableIndex + length, tablePlaceholder);
            return getEffectiveTableAlias(stringBuffer.toString(),tableName);
        }
        // 将一对括号同时替换为其他字符，递归匹配
        stringBuffer.replace(lastLeftBracketIndex, lastLeftBracketIndex + 1, "-");
        stringBuffer.replace(lastRightBracketIndex,lastRightBracketIndex+1,"-");
        return getEffectiveTableAlias(stringBuffer.toString(),tableName);
    }

    /**
     * 校验数据权限解析数据
     * @param dataAuthMap
     * @return
     */
    private void checkDataAuthLabel(Map<String, String> dataAuthMap){
        if(dataAuthMap == null){
            throw new BizException(RespCode.SYS_DATA_AUTHORITY_TABLE_EMPTY);
        }
        if(DataAuthorityUtil.getMapContainsKey(dataAuthMap,DataAuthorityUtil.TABLE_NAME)){
            if(StringUtils.isNotEmpty(DataAuthorityUtil.getMapValue(dataAuthMap,DataAuthorityUtil.TABLE_NAME))){
                return;
            }
        }
        throw new BizException(RespCode.SYS_DATA_AUTHORITY_TABLE_EMPTY);
    }

    /**
     * 根据表名获取TableInfo
     * @param tableName
     * @return
     */
    private TableInfo getTableInfoByTableName(String tableName){
        List<TableInfo> tableInfos = TableInfoHelper.getTableInfos();
        for(TableInfo tableInfo : tableInfos){
            if(tableInfo.getTableName().equalsIgnoreCase(tableName)){
                return tableInfo;
            }
        }
        return null;
    }

    /**
     * 从字符串中解析出数据权限标语句
     * @param analysisString
     * @return
     */
    private String getDataAuthLabel(String analysisString){
        return "{" + extractMessageFirst(analysisString.substring(analysisString.indexOf(DataAuthorityUtil.DATA_AUTH_LABEL))) + "}";
    }

    /**
     * 获取{}中的内容
     * @param msg
     * @return
     */
    private String extractMessageFirst(String msg){
        int start = 0;
        int startFlag = 0;
        int endFlag = 0;
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) == '{') {
                startFlag++;
                if (startFlag == endFlag + 1) {
                    start = i;
                }
            } else if (msg.charAt(i) == '}') {
                endFlag++;
                if (endFlag == startFlag) {
                    // {}这种空括号不需要返回
                    if(! (start == i - 1)){
                        return msg.substring(start + 1, i);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
