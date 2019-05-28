package com.hand.hcf.app.core.plugin;

import com.hand.hcf.app.core.domain.Domain;
import com.hand.hcf.app.core.domain.DomainI18n;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.ReflectionException;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.invoker.Invoker;

import java.util.Properties;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/9/4 21:05
 * @remark 公用字段默认值设置
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class DomainMetaInterceptor implements Interceptor{
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
//        Object[] parameters = invocation.getArgs();
//        if(parameters.length > 1){
//            Object parameter = invocation.getArgs()[1];
//            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
//            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
//            // 如果为插入或者更新，需要更新公用字段
//            if (SqlCommandType.INSERT.equals(sqlCommandType) || SqlCommandType.UPDATE.equals(sqlCommandType)) {
//                if(SqlCommandType.UPDATE.equals(sqlCommandType)){
//                    parameter = ((Map)parameter).get("et");
//                }
//                if(parameter != null){
//                    Class classParameter = parameter.getClass();
//                    if(classParameter != null){
//                        if (DomainI18n.class.isAssignableFrom(classParameter) || parameter instanceof DomainI18n || Domain.class.isAssignableFrom(classParameter) || parameter instanceof Domain) {
//                            Reflector reflector = new Reflector(classParameter);
//                            Invoker lastUpdatedBy = reflector.getSetInvoker("lastUpdatedBy");
//                            Invoker lastUpdatedDate = reflector.getSetInvoker("lastUpdatedDate");
//                            lastUpdatedBy.invoke(parameter,new Object[]{getCurrentUserId()});
//                            lastUpdatedDate.invoke(parameter,new Object[]{ZonedDateTime.now()});
//                            // 插入需要更新其他字段
//                            if(SqlCommandType.INSERT.equals(sqlCommandType)){
//                                Invoker createdDate = reflector.getSetInvoker("createdDate");
//                                createdDate.invoke(parameter,new Object[]{ZonedDateTime.now()});
//                                Invoker createdBy = reflector.getSetInvoker("createdBy");
//                                createdBy.invoke(parameter,new Object[]{getCurrentUserId()});
//                                Invoker versionNumber = reflector.getSetInvoker("versionNumber");
//                                versionNumber.invoke(parameter,new Object[]{1});
//                                try{
//                                    Invoker enabledGet = reflector.getGetInvoker("enabled");
//                                    Object invoke = enabledGet.invoke(parameter, new Object[]{});
//                                    if(invoke == null){
//                                        reflector.getSetInvoker("enabled").invoke(parameter,new Object[]{Boolean.TRUE});
//                                    }
//                                }catch(ReflectionException e){
//
//                                }
//                                try{
//                                    Invoker deletedGet = reflector.getGetInvoker("deleted");
//                                    Object invoke = deletedGet.invoke(parameter, new Object[]{});
//                                    if(invoke == null){
//                                        reflector.getSetInvoker("deleted").invoke(parameter,new Object[]{Boolean.FALSE});
//                                    }
//                                }catch(ReflectionException e){
//
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object[] parameters = invocation.getArgs();
        Object parameter = invocation.getArgs()[1];
        if(SqlCommandType.INSERT.equals(sqlCommandType)){
            if (parameter != null) {
                Class classParameter = parameter.getClass();
                if (classParameter != null) {
                    if (checkParameterMeta(classParameter,parameter)) {
                        // 如果为插入或者更新，需要更新公用字段
                        Reflector reflector = new Reflector(classParameter);
                        Invoker versionNumber = reflector.getSetInvoker("versionNumber");
                        versionNumber.invoke(parameter,new Object[]{1});
                        try {
                            Invoker enabledGet = reflector.getGetInvoker("enabled");
                            Object invoke = enabledGet.invoke(parameter, new Object[]{});
                            if (invoke == null) {
                                reflector.getSetInvoker("enabled").invoke(parameter, new Object[]{Boolean.TRUE});
                            }
                        } catch (ReflectionException e) {

                        }
                        try {
                            Invoker deletedGet = reflector.getGetInvoker("deleted");
                            Object invoke = deletedGet.invoke(parameter, new Object[]{});
                            if (invoke == null) {
                                reflector.getSetInvoker("deleted").invoke(parameter, new Object[]{Boolean.FALSE});
                            }
                        } catch (ReflectionException e) {

                        }
                    }
                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * 判断参数属性
     * @param classParameter
     * @param parameter
     * @return
     */
    private Boolean checkParameterMeta(Class classParameter, Object parameter){
        return DomainI18n.class.isAssignableFrom(classParameter) || parameter instanceof DomainI18n || Domain.class.isAssignableFrom(classParameter) || parameter instanceof Domain;
    }

    private Long getCurrentUserId() {
        Long currentUserID = null;
        try {
            currentUserID = LoginInformationUtil.getCurrentUserId();
        }catch (Exception e){
            e.printStackTrace();
        }
        return currentUserID == null ? 0L : currentUserID;
    }
}
