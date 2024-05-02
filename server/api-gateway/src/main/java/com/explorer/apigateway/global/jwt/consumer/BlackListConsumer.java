package com.explorer.apigateway.global.jwt.consumer;

import com.explorer.apigateway.global.jwt.JwtUtils;
import com.explorer.apigateway.global.jwt.repository.BlackListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlackListConsumer {

    private final JwtUtils jwtUtils;
    private final BlackListRepository blackListRepository;

    @RabbitListener(queues = "blacklist")
    public void receiveAccessToken(String accessToken) {
        blackListRepository.save(accessToken, jwtUtils.getExpirationDate(accessToken));
    }

}