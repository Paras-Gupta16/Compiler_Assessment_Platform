package com.example.compiler.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("compiler")
    public Executor codeCompiler(){
        ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
        poolTaskExecutor.setCorePoolSize(10);
        poolTaskExecutor.setMaxPoolSize(20);
        poolTaskExecutor.setQueueCapacity(50);
        poolTaskExecutor.setThreadNamePrefix("Compiler-");
        poolTaskExecutor.initialize();
        return poolTaskExecutor;
    }
}
