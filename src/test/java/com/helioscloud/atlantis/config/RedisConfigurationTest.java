package com.helioscloud.atlantis.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ActiveProfiles("select")
@SpringBootTest(classes={RedisConfiguration.class})
public class RedisConfigurationTest {

    @Test
    public void config () {

    }
}