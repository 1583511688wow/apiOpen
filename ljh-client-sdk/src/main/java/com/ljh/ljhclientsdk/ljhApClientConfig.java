package com.ljh.ljhclientsdk;


import com.ljh.ljhclientsdk.client.LjhClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@ComponentScan
@ConfigurationProperties("ljh.client")
@Configuration
public class ljhApClientConfig {


    private String accessKey;

    private  String secretKey;


    @Bean
    public LjhClient ljhClient( ){


        return new LjhClient(accessKey, secretKey);
    }



}
