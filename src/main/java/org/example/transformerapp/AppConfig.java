package org.example.transformerapp;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Transformer application API")
                        .version("1.0")
                        .description("API for transforming strings using various transformers"));
    }
}
