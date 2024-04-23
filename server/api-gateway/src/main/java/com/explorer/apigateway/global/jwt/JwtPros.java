package com.explorer.apigateway.global.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "jwt")
public class JwtPros {

    private String accessKey;

}
