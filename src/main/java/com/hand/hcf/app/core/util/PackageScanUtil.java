package com.hand.hcf.app.core.util;

import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import org.apache.ibatis.io.ResolverUtil;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class PackageScanUtil {

    /**
     * 获取packageName下继承superType的Class List
     * @param packageName   包绝对路径名称,支持以"."或者"/"分割
     * @param superType     父类Class
     * @return
     */
    public static List<Class<?>> getClassFromSuperClass(String packageName, Class<?> superType) {
        List<Class<?>> list = new ArrayList<>();
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
        resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
        Set<Class<? extends Class<?>>> mapperSet = resolverUtil.getClasses();
        if (CollectionUtils.isNotEmpty(mapperSet)) {
            list.addAll(mapperSet);
        }
        return list;
    }

    /**
     * 获取packageName下被annotationType注解的Class List
     * @param packageName       包绝对路径名称,支持以"."或者"/"分割
     * @param annotationType    注解类Class
     * @return
     */
    public static List<Class<?>> getClassWithAnnotation(String packageName, Class<? extends Annotation> annotationType) {
        return AnnotationUtil.getClassWithAnnotation(packageName,annotationType);
    }

    /**
     * 获取packageName下实现interfaceType接口的Class List
     * @param packageName       包绝对路径名称,支持以"."或者"/"分割
     * @param interfaceType     接口类Class
     * @return
     */
    public static List<Class<?>> getClassImplementClass(String packageName, Class<?> interfaceType) {
        List<Class<?>> list = new ArrayList<>();
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
        resolverUtil.findImplementations(interfaceType, packageName);
        Set<Class<? extends Class<?>>> mapperSet = resolverUtil.getClasses();
        if (CollectionUtils.isNotEmpty(mapperSet)) {
            list.addAll(mapperSet);
        }
        return list;
    }

}
