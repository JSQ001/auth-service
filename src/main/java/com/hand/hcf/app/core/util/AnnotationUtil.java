package com.hand.hcf.app.core.util;

import org.reflections.Reflections;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class AnnotationUtil {

    private AnnotationUtil() {

    }

    /**
     * 获取packageName下被annotationType注解的Class List
     *
     * @param packageName    包绝对路径名称,支持以"."或者"/"分割
     * @param annotationType 注解类Class
     * @return
     */
    public static List<Class<?>> getClassWithAnnotation(String packageName, Class<? extends Annotation> annotationType) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> typesAnnotated = reflections.getTypesAnnotatedWith(annotationType);
        return new ArrayList<>(typesAnnotated);
    }

    /**
     * 获取类的注释信息
     *
     * @param clazz 主类
     * @param <T>
     * @return
     * @annotationClass 注解类
     */
    public static <T extends Annotation> T getClassAnnotation(Class clazz, Class<T> annotationClass) {
        return AnnotationUtils.findAnnotation(clazz,annotationClass);
    }

    /**
     * 获取主类中 使用注释的fieldName
     *
     * @param clazz           主类
     * @param annotationClass 注释类
     * @return
     */
    public static <T extends Annotation> Map<String, T> getFieldAnnotation(Class clazz, Class<T> annotationClass) {
        Field[] declaredFields = clazz.getDeclaredFields();
        Map<String, T> map = new HashMap<String, T>();
        for (Field field : declaredFields) {
            T annotation = AnnotationUtils.findAnnotation(field, annotationClass);
            if (annotation != null) {
                map.put(field.getName(), annotation);
            }
        }
        return map;
    }

    /**
     * 获取主类中 使用注释的methodName
     *
     * @param clazz           主类
     * @param annotationClass 注释类
     * @return
     */
    public static <T extends Annotation> Map<String, T> getMethodAnnotation(Class clazz, Class<T> annotationClass) {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        Map<String, T> map = new HashMap<String, T>();
        for (Method method : declaredMethods) {
            T annotation = AnnotationUtils.getAnnotation(method, annotationClass);
            if (annotation != null ) {
                map.put(method.getName(), annotation);
            }
        }
        return map;
    }

    /**
     * 获取主类中，使用注解的方法，及注释的属性值(不考虑多重注解问题)
     * @param clazz
     * @param annotationClass
     * @param <T>
     * @return
     */
    public static <T extends Annotation> Map<Method, Map<Object,Object>> getMethodClassAnnotation(Class clazz, Class<T> annotationClass) {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        Map<Method, Map<Object,Object>> map = new ConcurrentHashMap<>();
        for (Method method : declaredMethods) {
            // 判断该方法是否使用该注解
            if (method.isAnnotationPresent(annotationClass)) {
                map.put(method, getAnnotationValues(method.getAnnotation(annotationClass)));
            // 若该方法没有使用该注解，则在其使用的注解中，查找是否使用该注解
            } else {
                // 获取方法使用的注解
                Annotation[] annotations = method.getAnnotations();
                for (Annotation annotation : annotations) {
                    Class<? extends Annotation> aClass = annotation.annotationType();
                    if (aClass.isAnnotationPresent(annotationClass)) {
                        // 获取方法注解的注解
                        T methodAnnotationAnnotation = aClass.getAnnotation(annotationClass);
                        // 获取方法注解的注解 的属性信息
                        Map<Object,Object> annotationValues2 = getAnnotationValues(methodAnnotationAnnotation);
                        // 获取方法注解的属性信息
                        Map<Object,Object> annotationValues = getAnnotationValues(annotation);
                        // 为防止修改原注解的属性信息，将其属性信息拷贝到另外的容器
                        Map<Object,Object> annotationValuesCopy = new LinkedHashMap<>(annotationValues2);
                        // 若有相同的属性，将方法注解的属性值，赋值给注解的注解
                        annotationValues.entrySet().forEach(entry -> {
                            if(annotationValuesCopy.containsKey(entry.getKey())){
                                annotationValuesCopy.put(entry.getKey(),entry.getValue());
                            }
                        });
                        map.put(method, annotationValuesCopy);
                    }
                }
            }
        }
        return map;
    }


    /**
     * 获取该注解对象的属性值
     * @param annotation
     * @param property
     * @return
     */
    public static Object getAnnotationValue(Annotation annotation, String property) {
        Object result = null;
        Map map = getAnnotationValues(annotation);
        if (map != null) {
            result = map.get(property);
        }
        return result;
    }

    /**
     * 获取注解对象所有的属性值
     * @param annotation
     * @return
     */
    public static Map<Object,Object> getAnnotationValues(Annotation annotation) {
        if (annotation != null) {
            InvocationHandler invo = Proxy.getInvocationHandler(annotation); //获取被代理的对象
            Map<Object,Object> map = (Map<Object,Object>) getFieldValue(invo, "memberValues");
            return map;
        }
        return null;
    }

    private static <T> Object getFieldValue(T object, String property) {
        if (object != null && property != null) {
            Class<T> currClass = (Class<T>) object.getClass();
            try {
                Field field = currClass.getDeclaredField(property);
                field.setAccessible(true);
                return field.get(object);
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException(currClass + " has no property: " + property);
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
