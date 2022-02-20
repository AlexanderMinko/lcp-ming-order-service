package com.lenovo.orderservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Data
public class AppConfig {

    @Value("${product-service.url}")
    private String productServiceUrl;

}
