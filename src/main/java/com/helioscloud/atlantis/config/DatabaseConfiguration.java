package com.helioscloud.atlantis.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.MybatisConfiguration;
import com.baomidou.mybatisplus.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
import com.cloudhelios.atlantis.annotation.EnableBaseI18nService;
import com.cloudhelios.atlantis.annotation.I18nDomainScan;
import com.cloudhelios.atlantis.plugin.HecPaginationInterceptor;
import com.cloudhelios.atlantis.plugin.HecPerformanceInterceptor;
import com.cloudhelios.atlantis.plugin.I18nSqlProcessInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @description:
 * @version: 1.0
 * @author: wenzhou.tang@hand-china.com
 * @date: 2017/7/21 10:59
 */
@Configuration
@EnableConfigurationProperties({DataSourceProperties.class, MybatisProperties.class})
@EnableBaseI18nService
@I18nDomainScan(basePackages = {"com.cloudhelios.atlantis.accounting.domain"})
public class DatabaseConfiguration {
    private final DataSourceProperties dataSourceProperties;
    private final MybatisProperties properties;
    private final ResourceLoader resourceLoader;
    @Value("${spring.dbType}")// oracle 或 mysql
    private String dbType;

    public DatabaseConfiguration(DataSourceProperties dataSourceProperties, MybatisProperties properties, ResourceLoader resourceLoader) {
        this.dataSourceProperties = dataSourceProperties;
        this.properties = properties;
        this.resourceLoader = resourceLoader;
    }
    @Bean
    public DataSource dataSource(){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(dataSourceProperties.getUrl());
        dataSource.setUsername(dataSourceProperties.getUsername());
//        dataSource.setPassword(new String(Base64.getDecoder().decode(dataSourceProperties.getPassword())));
        dataSource.setPassword(dataSourceProperties.getPassword());
        dataSource.setDriverClassName(dataSourceProperties.getDriverClassName());
        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(true);
        dataSource.setValidationQuery("SELECT 1 FROM DUAL");
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeout(300);
        dataSource.setMaxActive(100);
        return dataSource;
    }
    @Bean
    public MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean() {
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        mybatisSqlSessionFactoryBean.setDataSource(dataSource());
      //  mybatisSqlSessionFactoryBean.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            mybatisSqlSessionFactoryBean.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        mybatisSqlSessionFactoryBean.setConfiguration(properties.getConfiguration());
        mybatisSqlSessionFactoryBean.setPlugins(getInterceptors());
        GlobalConfiguration globalConfig = new GlobalConfiguration();
//        globalConfig.setDbType(cloudheliosProperties.getDatasource().getDbType());
        globalConfig.setIdType(2);
        globalConfig.setDbColumnUnderline(true);
        mybatisSqlSessionFactoryBean.setGlobalConfig(globalConfig);
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        mybatisConfiguration.setMapUnderscoreToCamelCase(true);
        mybatisSqlSessionFactoryBean.setConfiguration(mybatisConfiguration);

        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            mybatisSqlSessionFactoryBean.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
            mybatisSqlSessionFactoryBean.setMapperLocations(this.properties.resolveMapperLocations());
        }
        if (!ObjectUtils.isEmpty(this.properties.getTypeHandlersPackage())) {
            mybatisSqlSessionFactoryBean.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        return mybatisSqlSessionFactoryBean;
    }
    @Bean
    public Interceptor[] getInterceptors() {
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        Interceptor performanceInterceptor = new HecPerformanceInterceptor();     //处理oracle不兼容Boolean类型
        OptimisticLockerInterceptor optimisticLockerInterceptor = new OptimisticLockerInterceptor();
        Properties performanceInterceptorProps = new Properties();
        performanceInterceptorProps.setProperty("maxTime", "10000");
        performanceInterceptorProps.setProperty("format", "true");
        performanceInterceptorProps.setProperty("oracle.jdbc.J2EE13Compliant","true");
        performanceInterceptor.setProperties(performanceInterceptorProps);
        HecPaginationInterceptor pagination = new HecPaginationInterceptor();      //处理oracle不兼容Boolean类型
        pagination.setDialectType(dbType);  //数据库类型从配置文件获取
        I18nSqlProcessInterceptor i18nInterceptor = new I18nSqlProcessInterceptor();
        i18nInterceptor.setDialectType(dbType);
        pagination.setLocalPage(true);
        interceptors.add(pagination);
        interceptors.add(performanceInterceptor);
        interceptors.add(optimisticLockerInterceptor);
        interceptors.add(i18nInterceptor);
        return interceptors.toArray(new Interceptor[]{});
    }
}