package com.alexbezsh.redis.ratelimiter.config;

import com.alexbezsh.redis.ratelimiter.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
@EnableConfigurationProperties(RedisProperties.class)
public class AppConfig {

    private final RedisProperties properties;

    @Bean
    public Config redisConfig() {
        Config config = new Config();
        config.useSingleServer().setAddress(properties.getAddress());
        return config;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(Config config) {
        return new RedissonConnectionFactory(config);
    }

    @Bean
    public StringRedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    public RedisScript<Boolean> rateLimitScript() {
        return RedisScript.of(new ClassPathResource("rateLimitScript.lua"), Boolean.class);
    }

}
