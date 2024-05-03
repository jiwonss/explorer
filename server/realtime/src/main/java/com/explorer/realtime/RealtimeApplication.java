package com.explorer.realtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@ConfigurationPropertiesScan
@SpringBootApplication
@EnableFeignClients(basePackages = "com.explorer.realtime")
public class RealtimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealtimeApplication.class, args);
    }

}
