package com.hand.hcf.app.auth;

import com.hand.hcf.app.auth.service.AuthenticationService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@TestConfiguration
@MapperScan(basePackages = "com.hand.hcf.app.base.persistence")
@ComponentScan(basePackages = "com.hand.hcf.app.base.service",
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = AuthenticationService.class)})
public class TestAppConfiguration {
}
