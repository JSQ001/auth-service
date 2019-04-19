package com.hand.hcf.app.core.config;

import com.baomidou.mybatisplus.MybatisConfiguration;
import com.baomidou.mybatisplus.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.mapper.LogicSqlInjector;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
import com.baomidou.mybatisplus.spring.boot.starter.GlobalConfig;
import com.baomidou.mybatisplus.spring.boot.starter.MybatisPlusProperties;
import com.hand.hcf.app.core.annotation.EnableBaseI18nService;

import com.hand.hcf.app.core.persistence.DomainObjectMetaObjectHandler;
import com.hand.hcf.app.core.plugin.*;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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
/**
解决 oracle，mysql对数据库类型不兼容的处理 Boolean,数据库number被统一处理成了BigDecimal
@EnableBaseI18nService
*/
@EnableBaseI18nService
@EnableConfigurationProperties({MybatisPlusProperties.class})
public class DatabaseConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfiguration.class);
    private final MybatisPlusProperties properties;
    private final ResourceLoader resourceLoader;

    @Autowired
    private Environment env;

    @Value("${spring.dbType}")// oracle 或 mysql
    private String dbType;

    @Value("${mybatis-plus.enable-performance-interceptor:false}")
    private Boolean enablePerf;


    public DatabaseConfiguration(MybatisPlusProperties properties, ResourceLoader resourceLoader) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
    }

    /*@Bean
    @ConfigurationProperties(prefix="spring.datasource" )
    public DataSource dataSource(){
        logger.info("database connection pool is creating......");
        return DataSourceBuilder.create().build();
    }*/

    @Bean
    public MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean(DataSource dataSource) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        mybatisSqlSessionFactoryBean.setDataSource(dataSource);
        mybatisSqlSessionFactoryBean.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            mybatisSqlSessionFactoryBean.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        mybatisSqlSessionFactoryBean.setPlugins(getInterceptors());

        GlobalConfiguration globalConfig;
        if (!ObjectUtils.isEmpty(this.properties.getGlobalConfig())) {
            globalConfig = this.properties.getGlobalConfig().convertGlobalConfiguration();
        } else {
            globalConfig = new GlobalConfiguration();
        }
        //主键类型  0:"数据库ID自增", 1:"用户输入ID",2:"全局唯一ID (数字类型唯一ID)", 3:"全局唯一ID UUID";
        globalConfig.setIdType(2);
        globalConfig.setSqlInjector(new LogicSqlInjector());
        globalConfig.setDbColumnUnderline(true);
        globalConfig.setDbType(dbType);
        //全局自动填充配置
        globalConfig.setMetaObjectHandler(new DomainObjectMetaObjectHandler());
        //逻辑删除配置
        globalConfig.setLogicDeleteValue("1");
        globalConfig.setLogicNotDeleteValue("0");
        mybatisSqlSessionFactoryBean.setGlobalConfig(globalConfig);

        MybatisConfiguration configuration = this.properties.getConfiguration();
        if (configuration == null) {
            configuration = new MybatisConfiguration();
        }
        configuration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        //开启驼峰
        configuration.setMapUnderscoreToCamelCase(true);
        //配置JdbcTypeForNull, oracle数据库必须配置
        configuration.setJdbcTypeForNull(JdbcType.VARCHAR);

        mybatisSqlSessionFactoryBean.setConfiguration(configuration);
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            mybatisSqlSessionFactoryBean.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
            mybatisSqlSessionFactoryBean.setMapperLocations(this.properties.resolveMapperLocations());
        }
        if (!StringUtils.isEmpty(this.properties.getTypeHandlersPackage())) {
            mybatisSqlSessionFactoryBean.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        // 提供databaseId 需要使用特定的数据库函数时，可以根据_databaseId进行判断
        mybatisSqlSessionFactoryBean.setDatabaseIdProvider(getDatabaseIdProvider());
        if (!StringUtils.isEmpty(this.properties.getTypeEnumsPackage())){
            mybatisSqlSessionFactoryBean.setTypeEnumsPackage(this.properties.getTypeEnumsPackage());
        }
        return mybatisSqlSessionFactoryBean;
    }

    /**
     * mybtais 插件
     * @return
     */
    @Bean
    public Interceptor[] getInterceptors() {
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        // 考虑到公用字段填充与部分方法冲突，现使用拦截器创建时公用字段enabled、deleted、versionNumber
        DomainMetaInterceptor domainMetaInterceptor = new DomainMetaInterceptor();
        interceptors.add(domainMetaInterceptor);
        logger.info("performanceInterceptor enabled:"+enablePerf);
        if (enablePerf) {
            Interceptor performanceInterceptor = new HcfPerformanceInterceptor();
            Properties performanceInterceptorProps = new Properties();
            performanceInterceptorProps.setProperty("maxTime", "10000");
            performanceInterceptorProps.setProperty("format", "true");
            performanceInterceptor.setProperties(performanceInterceptorProps);
            interceptors.add(performanceInterceptor);
        }
        HcfPaginationInterceptor pagination = new HcfPaginationInterceptor();
        pagination.setDialectType(dbType);
        if ("oracle".equals(dbType)){
            System.getProperties().setProperty("oracle.jdbc.J2EE13Compliant", "true");
        }
        pagination.setLocalPage(true);
        I18nSqlProcessInterceptor i18nInterceptor = new I18nSqlProcessInterceptor();
        i18nInterceptor.setDialectType(dbType);
        interceptors.add(pagination);
        interceptors.add(i18nInterceptor);

        // 乐观锁重写，使用 updateById 或者 updateAllColumnById方法后会把更新后的versionNumber的值赋值给原对象
        interceptors.add(new HcfOptimisticLockerInterceptor());

        // 数据权限拦截器(拦截器需要最后放在最后，最先执行该拦截器，处理掉sql中特殊标识)
        DataAuthProcessInterceptor dataAuthProcessInterceptor = new DataAuthProcessInterceptor();
        interceptors.add(dataAuthProcessInterceptor);
        return interceptors.toArray(new Interceptor[]{});
    }


    @Bean
    public DatabaseIdProvider getDatabaseIdProvider(){
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("Oracle","oracle");
        properties.setProperty("MySQL","mysql");
        properties.setProperty("DB2","db2");
        properties.setProperty("Derby","derby");
        properties.setProperty("H2","h2");
        properties.setProperty("HSQL","hsql");
        properties.setProperty("Informix","informix");
        properties.setProperty("MS-SQL","ms-sql");
        properties.setProperty("PostgreSQL","postgresql");
        properties.setProperty("Sybase","sybase");
        properties.setProperty("Hana","hana");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }
}