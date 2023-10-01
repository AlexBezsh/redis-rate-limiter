package com.alexbezsh.redis.ratelimiter.service;

import com.alexbezsh.redis.ratelimiter.exception.RateLimitException;
import com.alexbezsh.redis.ratelimiter.model.RequestDescriptor;
import com.alexbezsh.redis.ratelimiter.properties.RateLimitRules;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import static com.alexbezsh.redis.ratelimiter.TestUtils.REQUEST_DESCRIPTOR_1_KEY;
import static com.alexbezsh.redis.ratelimiter.TestUtils.REQUEST_DESCRIPTOR_2_KEY;
import static com.alexbezsh.redis.ratelimiter.TestUtils.getDescriptorKeys;
import static com.alexbezsh.redis.ratelimiter.TestUtils.rateLimitRules;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor1;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor10;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor2;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptors;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceTest {

    @InjectMocks
    private RateLimitService testedInstance;

    @Spy
    private RateLimitRules rules = rateLimitRules();

    @Mock
    private StringRedisTemplate template;

    @Mock
    private RedisScript<Boolean> rateLimitScript;

    @Test
    void checkLimitsSuccess() {
        List<RequestDescriptor> descriptors = requestDescriptors();
        List<String> expectedKeys = getDescriptorKeys();
        String[] expectedArgs = {
            "1", "0", "60", "1", "3600", "2",
            "86400", "3", "1", "4", "60", "5",
            "3600", "6", "86400", "7", "1", "8"};

        doReturn(false).when(template).execute(rateLimitScript, expectedKeys, expectedArgs);

        assertDoesNotThrow(() -> testedInstance.checkLimits(descriptors));
    }

    @Test
    void checkLimitsShouldSkipTheSameDescriptors() {
        List<RequestDescriptor> descriptors = List.of(requestDescriptor1(), requestDescriptor1());
        List<String> expectedKeys = List.of(REQUEST_DESCRIPTOR_1_KEY);
        String[] expectedArgs = {"1", "0"};

        doReturn(false).when(template).execute(rateLimitScript, expectedKeys, expectedArgs);

        assertDoesNotThrow(() -> testedInstance.checkLimits(descriptors));
    }

    @Test
    void checkLimitsShouldThrowRateLimitException() {
        List<RequestDescriptor> descriptors = List.of(requestDescriptor2());
        List<String> expectedKeys = List.of(REQUEST_DESCRIPTOR_2_KEY);
        String[] expectedArgs = {"60", "1"};

        doReturn(true).when(template).execute(rateLimitScript, expectedKeys, expectedArgs);

        assertThrows(RateLimitException.class, () -> testedInstance.checkLimits(descriptors));
    }

    @Test
    void checkLimitsShouldNotCallRedisIfNoRuleFound() {
        List<RequestDescriptor> descriptors = List.of(requestDescriptor10());

        assertDoesNotThrow(() -> testedInstance.checkLimits(descriptors));

        verify(template, never()).execute(any(), any(), anyString(), anyString());
    }

}
