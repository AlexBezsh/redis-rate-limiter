package com.alexbezsh.redis.ratelimiter.integration;

import com.alexbezsh.redis.ratelimiter.properties.RedisProperties;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Autowired
    private RedisProperties properties;

    @Bean
    @Primary
    public Config testRedisConfig() {
        Config config = new Config();
        config.useSingleServer().setAddress(properties.getAddresses().get(0));
        return config;
    }

}
