/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.hand.hcf.app.core.plugin;

import com.baomidou.mybatisplus.enums.DBType;
import com.baomidou.mybatisplus.plugins.pagination.DialectFactory;
import com.baomidou.mybatisplus.plugins.pagination.PageHelper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.baomidou.mybatisplus.plugins.parser.ISqlParser;
import com.baomidou.mybatisplus.plugins.parser.SqlInfo;
import com.baomidou.mybatisplus.toolkit.JdbcUtils;
import com.baomidou.mybatisplus.toolkit.PluginUtils;
import com.baomidou.mybatisplus.toolkit.SqlUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.hand.hcf.app.core.handler.HcfMybatisDefaultParameterHandler;
import com.hand.hcf.app.core.util.ReflectHelper;
import com.hand.hcf.app.core.util.ReflectionUtil;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;


/**
 * 分页拦截器 源自 PaginationInterceptor类
 * 解决 oracle，mysql对数据库类型不兼容的处理 Boolean,数据库number被统一处理成了BigDecimal
 * @author houyin.zhang
 * @Date 2018-04-18
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class HcfPaginationInterceptor implements Interceptor {

    // 日志
    private static final Log logger = LogFactory.getLog(HcfPaginationInterceptor.class);
    // COUNT SQL 解析
    private ISqlParser sqlParser;
    /* 溢出总页数，设置第一页 */
    private boolean overflowCurrent = false;
    /* 方言类型 */
    private String dialectType;
    /* 方言实现类 */
    private String dialectClazz;
    /* 是否开启 PageHelper localPage 模式 */
    private boolean localPage = false;

    /**
     * Physical Pagination Interceptor for all the queries with parameter {@link RowBounds}
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);

        // 先判断是不是SELECT操作
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");

        //20180419 如果是oracle类型则将boolean的  false转为0,true转为1
        BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
        String tempSql = boundSql.getSql();
        tempSql = ReflectionUtil.changeBooleanPresentation(tempSql);
        ReflectHelper.setFieldValue(boundSql, "sql", tempSql);
        //boundSql = new BoundSql(mappedStatement.getConfiguration(),tempSql,boundSql.getParameterMappings(),boundSql.getParameterObject());
        String originalSql = boundSql.getSql();

        if (!SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType())) {
            return invocation.proceed();
        }

        RowBounds rowBounds = (RowBounds) metaStatementHandler.getValue("delegate.rowBounds");
        /* 不需要分页的场合 */
        if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
            // 本地线程分页
            if (localPage) {
                // 采用ThreadLocal变量处理的分页
                rowBounds = PageHelper.getPagination();
                if (rowBounds == null) {
                    return invocation.proceed();
                }
            } else {
                // 无需分页
                //return invocation.proceed();
            }
        }

        // 针对定义了rowBounds，做为mapper接口方法的参数
        Connection connection = (Connection) invocation.getArgs()[0];
        DBType dbType = StringUtils.isNotEmpty(dialectType) ? DBType.getDBType(dialectType) : JdbcUtils.getDbType(connection.getMetaData().getURL());
        if (rowBounds instanceof Pagination) {
            Pagination page = (Pagination) rowBounds;
            boolean orderBy = true;
            if (page.isSearchCount()) {
                SqlInfo sqlInfo = SqlUtils.getOptimizeCountSql(true,sqlParser, originalSql);
                orderBy = sqlInfo.isOrderBy();
                this.queryTotal(overflowCurrent, sqlInfo.getSql(), mappedStatement, boundSql, page, connection);
                if (page.getTotal() <= 0) {
                    return invocation.proceed();
                }
            }
            String buildSql = SqlUtils.concatOrderBy(originalSql, page, orderBy);
            originalSql = DialectFactory.buildPaginationSql(page, buildSql, dbType, dialectClazz);
        } else {
            // support physical Pagination for RowBounds
            originalSql = DialectFactory.buildPaginationSql(rowBounds, originalSql, dbType, dialectClazz);
        }

		/*
         * <p> 禁用内存分页 </p>
         * <p> 内存分页会查询所有结果出来处理（这个很吓人的），如果结果变化频繁这个数据还会不准。</p>
		 */
        metaStatementHandler.setValue("delegate.boundSql.sql", originalSql);
        metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
        metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
        return invocation.proceed();
    }

    /**
     * 查询总记录条数
     *
     * @param sql
     * @param mappedStatement
     * @param boundSql
     * @param page
     */
    protected void queryTotal(boolean overflowCurrent, String sql, MappedStatement mappedStatement, BoundSql boundSql, Pagination page, Connection connection) {
        //20180419 如果是oracle类型则将boolean的  false转为0,true转为1
        sql = ReflectionUtil.changeBooleanPresentation(sql);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            HcfMybatisDefaultParameterHandler parameterHandler = new HcfMybatisDefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            int total = 0;
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    total = resultSet.getInt(1);
                }
            }
            page.setTotal(total);
            /*
             * 溢出总页数，设置第一页
			 */
            int pages = Math.toIntExact(page.getPages());
            if (overflowCurrent && (page.getCurrent() > pages)) {
                page = new Pagination(1, page.getSize());
                page.setTotal(total);
            }
        } catch (Exception e) {
            logger.error("Error: Method queryTotal execution error !", e);
        }
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties prop) {
        String dialectType = prop.getProperty("dialectType");
        String dialectClazz = prop.getProperty("dialectClazz");

        if (StringUtils.isNotEmpty(dialectType)) {
            this.dialectType = dialectType;
        }
        if (StringUtils.isNotEmpty(dialectClazz)) {
            this.dialectClazz = dialectClazz;
        }
    }

    public void setDialectType(String dialectType) {
        this.dialectType = dialectType;
    }

    public void setDialectClazz(String dialectClazz) {
        this.dialectClazz = dialectClazz;
    }

    public void setOverflowCurrent(boolean overflowCurrent) {
        this.overflowCurrent = overflowCurrent;
    }

    public void setSqlParser(ISqlParser sqlParser) {
        this.sqlParser = sqlParser;
    }

    public void setLocalPage(boolean localPage) {
        this.localPage = localPage;
    }
}
