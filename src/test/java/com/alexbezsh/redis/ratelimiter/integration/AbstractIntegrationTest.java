package com.alexbezsh.redis.ratelimiter.integration;

import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    protected static GenericContainer<?> redis = new GenericContainer<>("redis:7.2.3")
        .withExposedPorts(6379);

    @Autowired
    protected MockMvc mockMvc;

    @SpyBean
    protected StringRedisTemplate template;

    @DynamicPropertySource
    protected static void properties(DynamicPropertyRegistry registry) {
        redis.start();
        registry.add("redis.address", () -> String.format("redis://%s:%s",
            redis.getHost(), redis.getFirstMappedPort()));
    }

    @AfterEach
    protected void cleanUp() {
        Set<String> keys = redisKeys();
        if (keys != null && !keys.isEmpty()) {
            template.delete(keys);
        }
    }

    protected Set<String> redisKeys() {
        return template.keys("*");
    }

}
