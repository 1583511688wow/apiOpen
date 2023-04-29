package com.ljh.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CorsConfig {





    @Bean
    @LoadBalanced     // 如果不添加，无法通过服务名进行调用，只能通过ip调用
    public WebClient.Builder webBuilder(){
        return WebClient.builder();
    }
}
