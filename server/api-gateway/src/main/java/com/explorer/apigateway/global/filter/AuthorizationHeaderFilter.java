package com.explorer.apigateway.global.filter;

import com.explorer.apigateway.global.common.dto.TokenInfo;
import com.explorer.apigateway.global.error.exception.AuthenticationErrorCode;
import com.explorer.apigateway.global.error.exception.AuthenticationException;
import com.explorer.apigateway.global.jwt.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
@RefreshScope
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    @Autowired
    private JwtUtils jwtUtils;

    public AuthorizationHeaderFilter() {
        super(Config.class);
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            try {
                String accessToken = exchange.getRequest().getHeaders().get(AUTHORIZATION).get(0).substring(7);

                if (jwtUtils.isBlackListed(accessToken)) {
                    throw new AuthenticationException(AuthenticationErrorCode.INVALID_TOKEN);
                }

                TokenInfo token = jwtUtils.parseToken(accessToken);
                addAuthorizationHeaders(exchange.getRequest(), token);
            } catch (ExpiredJwtException ex) {
                throw new AuthenticationException(AuthenticationErrorCode.EXPIRED_TOKEN);
            } catch (Exception ex) {
                throw new AuthenticationException(AuthenticationErrorCode.FAILED_AUTHENTICATION);
            }

            log.info("accessToken : {}", exchange.getRequest().getHeaders().get(AUTHORIZATION).get(0).substring(7));
            return chain.filter(exchange);
        };
    }

    private void addAuthorizationHeaders(ServerHttpRequest request, TokenInfo token) {
        request.mutate()
                .header("X-Authorization-Id", token.getUserId())
                .build();
    }


    static class Config {

    }

}
