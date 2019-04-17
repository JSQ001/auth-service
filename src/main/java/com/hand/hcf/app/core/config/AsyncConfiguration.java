package com.hand.hcf.app.core.config;

import com.hand.hcf.app.core.async.HcfAsyncTaskExecutor;
import com.hand.hcf.app.core.async.HcfAsyncUncaughtExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
@ConditionalOnProperty(name = "async.enable", havingValue = "true")
@Slf4j
public class AsyncConfiguration implements AsyncConfigurer {

    @Value("${async.max-pool-size:0}")
    private int maxPoolSize;

    @Value("${async.queue-capacity:0}")
    private int queueCapacity;

    @Value("${async.core-pool-size:0}")
    private int corePoolSize;

    @Value("${async.keep-alive-seconds:30}")
    private int keepAliveSeconds;


    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix("HCF-Executor-");

        return new HcfAsyncTaskExecutor(executor);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new HcfAsyncUncaughtExceptionHandler();
    }
}
