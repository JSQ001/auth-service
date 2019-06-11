package com.hand.hcf.app.core.util;

import com.alibaba.fastjson.JSON;
import com.hand.hcf.app.core.exception.BizException;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.invoker.Invoker;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kai.zhang on 2017-12-27.
 * 反射工具类
 */
public final class ReflectUtil {

    private ReflectUtil() {
    }

    /**
     * 获取属性Type的泛型Class
     *
     * @param clazz     主类
     * @param fieldName 属性名
     * @return
     */
    public static Class<?> getGenericType(Class<?> clazz, String fieldName, Integer genericIndex) {
        try {
            Field declaredField = null;
            declaredField = clazz.getDeclaredField(fieldName);
            Type genericType = declaredField.getGenericType();
            if (genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                return (Class<?>) parameterizedType.getActualTypeArguments()[genericIndex];
            }
            return null;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取指定类的属性的get&set方法,组装成方便使用的Reflector对象
     *
     * @param map   不能为null
     * @param clazz
     * @return
     */
    public static void setReflectorsFromClass(Map<Class<?>, Reflector> map, Class clazz) {
        map.put(clazz, new Reflector(clazz));
    }

    /**
     * 通过反射执行set方法，value可自动转换类型(值为空时，不设置值)
     *
     * @param instance  实例
     * @param fieldName 设置的属性名
     * @param value     设置的值
     * @param isNotNull 值是否不能为空
     * @param respCode  多语言抛错相关
     * @param args      多语言抛错相关
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public static void executeFieldSetter(Object instance, String fieldName, Object value, boolean isNotNull, String respCode, Object[] args) {
        executeFieldSetter(instance, fieldName, value, isNotNull, true, respCode, args);
    }

    /**
     * 通过反射执行set方法，value可自动转换类型
     *
     * @param instance   实例
     * @param fieldName  设置的属性名
     * @param value      设置的值
     * @param isNotNull  值是否不能为空
     * @param ignoreNull 忽略空值
     * @param respCode   多语言抛错相关
     * @param args       多语言抛错相关
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public static void executeFieldSetter(Object instance, String fieldName, Object value, boolean isNotNull, boolean ignoreNull, String respCode, Object[] args) {
        if (isNotNull && TypeConversionUtils.isEmpty(value)) {
            throw new BizException(respCode, args);
        }
        // 值为空，且忽略空值时，不设置值
        if (TypeConversionUtils.isEmpty(value) && ignoreNull) {
            return;
        }
        Class<?> clazz = instance.getClass();
        Reflector reflector = new Reflector(clazz);
        Invoker setInvoker = reflector.getSetInvoker(fieldName);
        Class<?> getterType = reflector.getGetterType(fieldName);
        try {
            Object value1 = typeConversionHelper(getterType, value);
            setInvoker.invoke(instance, new Object[]{value1});
        } catch (IllegalAccessException e) {
            throw new BizException(RespCode.SYS_REFLECT_EXECUTE_GET_SET_FAIL);
        } catch (InvocationTargetException e) {
            throw new BizException(RespCode.SYS_REFLECT_EXECUTE_GET_SET_FAIL);
        }
    }

    /**
     * 执行get方法
     *
     * @param instance
     * @param fieldName
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object executeFieldGetter(Object instance, String fieldName) {
        Class<?> clazz = instance.getClass();
        Reflector reflector = new Reflector(clazz);
        Invoker getInvoker = reflector.getGetInvoker(fieldName);
        try {
            return getInvoker.invoke(instance, new Object[]{});
        } catch (IllegalAccessException e) {
            throw new BizException(RespCode.SYS_REFLECT_EXECUTE_GET_SET_FAIL);
        } catch (InvocationTargetException e) {
            throw new BizException(RespCode.SYS_REFLECT_EXECUTE_GET_SET_FAIL);
        }
    }

    /**
     * 执行实例的方法
     *
     * @param instance       类实例
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @param param          参数
     * @return 若捕获到异常，返回空值
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object executeMethod(Object instance, String methodName, Class<?>[] parameterTypes, Object[] param) {
        Class<?> instanceClass = instance.getClass();
        try {
            Method declaredMethod = instanceClass.getDeclaredMethod(methodName, parameterTypes);
            //为防止方法为私有方法，必须修改访问权限
            declaredMethod.setAccessible(true);
            parameterTypeConversionHelper(declaredMethod,param);
            Object result = declaredMethod.invoke(instance, param);
            return result;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("execute method failed");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("execute method failed");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("execute method failed");
        }
    }

    /**
     * 执行实例的方法
     * @param instance       类实例
     * @param method     方法
     * @param param          参数
     * @return 若捕获到异常，返回空值
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <T extends Object> T executeMethod(Class<T> resultType, Object instance, Method method, Object ... param) {
        try {
            parameterTypeConversionHelper(method,param);
            Object result = method.invoke(instance, param);
            if(resultType == null){
                return null;
            }
            return (T) result;
        // 只处理反射相关的错误，其他错误正常抛出
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("execute method failed!");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("execute method failed!");
        }
    }

    /**
     * 执行实例的方法
     * @param instance       类实例
     * @param method     方法
     * @param parameterMap  参数名称 - 参数值映射关系
     * @return 若捕获到异常，返回空值
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <T extends Object> T executeMethodForMap(Class<T> resultType, Object instance, Method method, Map<String,Object> parameterMap) {
        List<Object> list = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        if(parameters != null && parameters.length > 0){
            for(int i = 0; i < parameters.length; i++){
                String parameterName = parameters[i].getName();
                Object parameterValue = parameterMap.get(parameterName);
                list.add(parameterValue);
            }
        }
        Object[] objects = list.toArray();
        return executeMethod(resultType,instance,method,objects);
    }

    private static void parameterTypeConversionHelper(Method method,Object ... param){
        Parameter[] parameters = method.getParameters();
        if(parameters != null && parameters.length > 0){
            for(int i = 0; i < parameters.length; i++){
                Object parameterValue = param[i];
                Class<?> type = parameters[i].getType();
                Object value = typeConversionHelper(type, parameterValue);
                param[i] = value;
            }
        }
    }

    /**
     * 类型转换
     * @param resultType
     * @param value
     * @param <T>
     * @return
     */
    private static <T extends Object> T typeConversionHelper(Class<T> resultType,Object value){
        T result = null;
        if(value != null){
            result = JSON.parseObject(JSON.toJSONString(value),resultType);
        }
        return result;
    }
}
