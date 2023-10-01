package com.alexbezsh.redis.ratelimiter.controller;

import com.alexbezsh.redis.ratelimiter.controller.api.RateLimitApi;
import com.alexbezsh.redis.ratelimiter.model.RequestDescriptor;
import com.alexbezsh.redis.ratelimiter.service.RateLimitService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RateLimitController implements RateLimitApi {

    private final RateLimitService service;

    @Override
    public void limit(List<RequestDescriptor> descriptors) {
        service.checkLimits(descriptors);
    }

}
