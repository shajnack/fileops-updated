package com.fileops;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GitAppConfig {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
