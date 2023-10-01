package com.alexbezsh.redis.ratelimiter.controller;

import com.alexbezsh.redis.ratelimiter.service.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AbstractControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected RateLimitService rateLimitService;

}
