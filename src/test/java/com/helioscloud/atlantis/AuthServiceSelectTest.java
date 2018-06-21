package com.helioscloud.atlantis;

import org.junit.runner.RunWith;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("select")
@SpringBootTest(classes={DataSourceAutoConfiguration.class, MybatisAutoConfiguration.class,
        TestAppConfiguration.class})
public abstract class AuthServiceSelectTest {
}