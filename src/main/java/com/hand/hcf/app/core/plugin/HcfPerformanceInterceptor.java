package com.hand.hcf.app.core.plugin;

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.toolkit.PluginUtils;
import com.baomidou.mybatisplus.toolkit.SqlUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.baomidou.mybatisplus.toolkit.SystemClock;
import com.hand.hcf.app.core.util.ReflectionUtil;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.Properties;

/**
 * 源自 PerformanceInterceptor 类
 * 性能分析拦截器，用于输出每条 SQL 语句及其执行时间
 * 解决 oracle，mysql对数据库类型不兼容的处理 Boolean,数据库number被统一处理成了BigDecimal
 * @author houyin.zhang
 * @Date 2018-04-18
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})})
public class HcfPerformanceInterceptor implements Interceptor {
    private static final Log logger = LogFactory.getLog(HcfPerformanceInterceptor.class);
    /**
     * SQL 执行最大时长，超过自动停止运行，有助于发现问题。
     */
    private long maxTime = 0;

    /**
     * SQL 是否格式化
     */
    private boolean format = false;

    /**
     * 是否写入日志文件<br>
     * true 写入日志文件，不阻断程序执行！<br>
     * 超过设定的最大执行时长异常提示！
     */
    private boolean writeInLog = false;

    private Method oracleGetOriginalSqlMethod;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Statement statement;
        Object firstArg = invocation.getArgs()[0];
        if (Proxy.isProxyClass(firstArg.getClass())) {
            statement = (Statement) SystemMetaObject.forObject(firstArg).getValue("h.statement");
        } else {
            statement = (Statement) firstArg;
        }
        try {
            statement.getClass().getDeclaredField("stmt");
            statement = (Statement) SystemMetaObject.forObject(statement).getValue("stmt.statement");
        } catch (Exception e) {
            // do nothing
        }

        String originalSql = null;
        String stmtClassName = statement.getClass().getName();
        if ("oracle.jdbc.driver.T4CPreparedStatement".equals(stmtClassName)) {
            try {
                if (oracleGetOriginalSqlMethod != null) {
                    Object stmtSql = oracleGetOriginalSqlMethod.invoke(statement);
                    if (stmtSql != null && stmtSql instanceof String) {
                        originalSql = (String) stmtSql;
                    }
                } else {
                   Class<?> clazz = Class.forName("oracle.jdbc.driver.OracleStatement");
                   oracleGetOriginalSqlMethod = clazz.getDeclaredMethod("getOriginalSql", (Class<?>) null);
                    if (oracleGetOriginalSqlMethod != null) {
                        Object stmtSql = oracleGetOriginalSqlMethod.invoke(statement);
                        if (stmtSql != null && stmtSql instanceof String) {
                            originalSql = (String) stmtSql;
                        }
                    }
                }
            } catch (Exception e) {//ignore
            }
        }
        if (originalSql == null) {
            originalSql = statement.toString();
        }
        int index = originalSql.indexOf(':');
        if (index > 0) {
            originalSql = originalSql.substring(index + 1, originalSql.length());
        }
        //20180419如果是oracle类型则将boolean的  false转为0,true转为1
        originalSql = ReflectionUtil.changeBooleanPresentation(originalSql);

        // 计算执行 SQL 耗时
        long start = SystemClock.now();
        Object result = invocation.proceed();
        long timing = SystemClock.now() - start;

        // 格式化 SQL 打印执行结果
        Object target = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(target);
        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        StringBuilder formatSql = new StringBuilder();
        formatSql.append(" Time：").append(timing);
        formatSql.append(" ms - ID：").append(ms.getId());
        formatSql.append("\n Execute SQL：").append(SqlUtils.sqlFormat(originalSql, format)).append("\n");
        if (this.isWriteInLog()) {
            if (this.getMaxTime() >= 1 && timing > this.getMaxTime()) {
                logger.error(formatSql.toString());
            } else {
                logger.debug(formatSql.toString());
            }
        } else {
            System.err.println(formatSql.toString());
            if (this.getMaxTime() >= 1 && timing > this.getMaxTime()) {
                throw new MybatisPlusException(" The SQL execution time is too large, please optimize ! ");
            }
        }
        return result;
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
        String maxTime = prop.getProperty("maxTime");
        String format = prop.getProperty("format");
        if (StringUtils.isNotEmpty(maxTime)) {
            this.maxTime = Long.parseLong(maxTime);
        }
        if (StringUtils.isNotEmpty(format)) {
            this.format = Boolean.valueOf(format);
        }
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public boolean isFormat() {
        return format;
    }

    public void setFormat(boolean format) {
        this.format = format;
    }

    public boolean isWriteInLog() {
        return writeInLog;
    }

    public void setWriteInLog(boolean writeInLog) {
        this.writeInLog = writeInLog;
    }
}
