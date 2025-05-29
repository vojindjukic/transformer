package org.example.transformerapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {

    @Bean
    public ExecutorService regexThreadPool() {
        int numCores = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(numCores);
    }
}
