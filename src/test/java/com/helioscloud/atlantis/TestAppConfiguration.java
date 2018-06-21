package com.helioscloud.atlantis;

import com.helioscloud.atlantis.service.AuthenticationService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@TestConfiguration
@MapperScan(basePackages = "com.helioscloud.atlantis.persistence")
@ComponentScan(basePackages = "com.helioscloud.atlantis.service",
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = AuthenticationService.class)})
public class TestAppConfiguration {
}
