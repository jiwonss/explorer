package com.explorer.apigateway.global.filter;

import com.explorer.apigateway.global.common.dto.TokenInfo;
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
                    throw new AuthenticationException("사용할 수 없는 토큰입니다.");
                }

                TokenInfo token = jwtUtils.parseToken(accessToken);
                addAuthorizationHeaders(exchange.getRequest(), token);
            } catch (ExpiredJwtException ex) {
                throw new AuthenticationException("토큰이 만료되었습니다.");
            } catch (MalformedJwtException | SignatureException | IllegalArgumentException |
                     NullPointerException ex) {
                throw new AuthenticationException("인증에 실패하였습니다.");
            }

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
