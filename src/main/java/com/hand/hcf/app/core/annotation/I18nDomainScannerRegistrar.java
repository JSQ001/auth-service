package com.hand.hcf.app.core.annotation;

import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.core.annotation.I18nDomainScan;
import com.hand.hcf.app.core.domain.BaseI18nDomain;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.util.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.VFS;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class I18nDomainScannerRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(I18nDomainScan.class.getName()));
        List<String> basePackages = new ArrayList<String>();
        for (String pkg : annotationAttrs.getStringArray("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : annotationAttrs.getStringArray("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : annotationAttrs.getClassArray("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        if (CollectionUtils.isNotEmpty(basePackages)) {
            //必须设置SpringBootVFS
            VFS.addImplClass(SpringBootVFS.class);
            //package缓存
            BaseI18nService.i18nDomainMethodCache = ReflectionUtil.getReflectorsFromPackage(basePackages, BaseI18nDomain.class);

        } else {
            log.info("I18nDomainScan is not configured,if not use i18n interceptor,that's ok!");
        }
    }
}
