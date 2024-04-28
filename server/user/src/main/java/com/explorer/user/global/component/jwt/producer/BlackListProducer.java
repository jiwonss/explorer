package com.explorer.user.global.component.jwt.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlackListProducer {

    private final RabbitTemplate rabbitTemplate;

    private static final String exchange = "direct.user";
    private static final String routingKey = "token";

    public void sendAccessToken(String accessToken) {
        rabbitTemplate.convertAndSend(exchange, routingKey, accessToken);
    }

}
