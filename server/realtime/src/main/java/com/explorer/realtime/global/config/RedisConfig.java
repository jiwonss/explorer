package com.explorer.realtime.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Value("${spring.data.redis.channel.host}")
    private String channelHost;

    @Value("${spring.data.redis.channel.port}")
    private int channelPort;

    @Value("${spring.data.redis.channel.password}")
    private String channelPassword;

    @Value("${spring.data.redis.game.host}")
    private String gameHost;

    @Value("${spring.data.redis.game.port}")
    private int gamePort;

    @Value("${spring.data.redis.game.password}")
    private String gamePassword;

    @Value("${spring.data.redis.staticgame.host}")
    private String staticgameHost;

    @Value("${spring.data.redis.staticgame.port}")
    private int staticgamePort;

    @Value("${spring.data.redis.staticgame.password}")
    private String staticgamePassword;

    private LettuceConnectionFactory createConnectionFactory(String host, int port, String password) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setPassword(password);
        return new LettuceConnectionFactory(config);
    }

    private ReactiveRedisConnectionFactory createReactiveConnectionFactory(String host, int port, String password) {
        return createConnectionFactory(host, port, password);
    }

    @Primary
    @Bean(name = "channelReactiveRedisConnectionFactory")
    public ReactiveRedisConnectionFactory channelReactiveRedisConnectionFactory() {
        return createReactiveConnectionFactory(channelHost, channelPort, channelPassword);
    }

    @Bean(name = "channelReactiveRedisTemplate")
    public ReactiveRedisTemplate<String, Object> channelReactiveRedisTemplate() {
        return new ReactiveRedisTemplate<>(
                channelReactiveRedisConnectionFactory(),
                RedisSerializationContext.<String, Object>newSerializationContext(new StringRedisSerializer())
                        .value(new GenericJackson2JsonRedisSerializer())
                        .build());
    }

    @Bean(name = "gameReactiveRedisConnectionFactory")
    public ReactiveRedisConnectionFactory gameReactiveRedisConnectionFactory() {
        return createReactiveConnectionFactory(gameHost, gamePort, gamePassword);
    }

    @Bean(name = "gameReactiveRedisTemplate")
    public ReactiveRedisTemplate<String, Object> gameReactiveRedisTemplate() {
        return new ReactiveRedisTemplate<>(
                gameReactiveRedisConnectionFactory(),
                RedisSerializationContext.<String, Object>newSerializationContext(new StringRedisSerializer())
                        .value(new GenericJackson2JsonRedisSerializer())
                        .build());
    }

    @Bean(name = "staticgameReactiveRedisConnectionFactory")
    public ReactiveRedisConnectionFactory staticgameReactiveRedisConnectionFactory() {
        return createReactiveConnectionFactory(gameHost, gamePort, gamePassword);
    }

    @Bean(name = "staticgameReactiveRedisTemplate")
    public ReactiveRedisTemplate<String, String> staticgameReactiveRedisTemplate() {
        return new ReactiveRedisTemplate<>(
                gameReactiveRedisConnectionFactory(),
                RedisSerializationContext.<String, String>newSerializationContext(new StringRedisSerializer())
                        .value(new StringRedisSerializer())
                        .build());
    }
}
