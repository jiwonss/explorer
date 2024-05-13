package com.explorer.logic.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    private LettuceConnectionFactory createConnectionFactory(String host, int port, String password) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setPassword(password);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        return createConnectionFactory(host, port, password);
    }

    @Bean("reactiveRedisTemplate")
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        return new ReactiveRedisTemplate<>(
                factory,
                RedisSerializationContext.<String, Object>newSerializationContext()
                        .key(new StringRedisSerializer())
                        .value(new GenericJackson2JsonRedisSerializer())
                        .hashKey(new StringRedisSerializer())
                        .hashValue(new GenericJackson2JsonRedisSerializer())
                        .build());
    }

    @Bean("stringReactiveRedisTemplate")
    public ReactiveRedisTemplate<String, Object> stringReactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext<String, Object> serializationContext =
                RedisSerializationContext.<String, Object>newSerializationContext(new StringRedisSerializer())
                        .hashKey(new StringRedisSerializer())
                        .hashValue(new StringRedisSerializer()) // 여기서 Object 대신 다른 클래스 지정 가능
                        .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return createConnectionFactory(host, port, password);
    }

}
