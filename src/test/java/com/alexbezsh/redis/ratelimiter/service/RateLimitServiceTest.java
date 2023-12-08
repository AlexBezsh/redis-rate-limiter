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
import static com.alexbezsh.redis.ratelimiter.TestUtils.REQUEST_DESCRIPTOR_3_KEY;
import static com.alexbezsh.redis.ratelimiter.TestUtils.getDescriptorKeys;
import static com.alexbezsh.redis.ratelimiter.TestUtils.rateLimitRules;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor1;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor10;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor2;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor3;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptors;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
        List<String> descriptorKeys = getDescriptorKeys();
        String[][] args = {{"1", "0"}, {"60", "1"}, {"3600", "2"}, {"86400", "3"}, {"1", "4"},
            {"60", "5"}, {"3600", "6"}, {"86400", "7"}, {"1", "8"}};
        List<RequestDescriptor> descriptors = requestDescriptors();

        for (int i = 0; i < descriptorKeys.size(); i++) {
            doReturn(false).when(template)
                .execute(rateLimitScript, List.of(descriptorKeys.get(i)), args[i]);
        }

        assertDoesNotThrow(() -> testedInstance.checkLimits(descriptors));
    }

    @Test
    void checkLimitsShouldSkipDuplicateDescriptors() {
        List<RequestDescriptor> descriptors = List.of(requestDescriptor1(),
            requestDescriptor1(), requestDescriptor2());

        doReturn(false).when(template)
            .execute(rateLimitScript, List.of(REQUEST_DESCRIPTOR_1_KEY), "1", "0");
        doReturn(false).when(template)
            .execute(rateLimitScript, List.of(REQUEST_DESCRIPTOR_2_KEY), "60", "1");

        assertDoesNotThrow(() -> testedInstance.checkLimits(descriptors));

        verify(template, times(1))
            .execute(rateLimitScript, List.of(REQUEST_DESCRIPTOR_1_KEY), "1", "0");
    }

    @Test
    void checkLimitsShouldThrowRateLimitException() {
        List<RequestDescriptor> descriptors = List.of(requestDescriptor2(), requestDescriptor3());

        doReturn(false).when(template)
            .execute(rateLimitScript, List.of(REQUEST_DESCRIPTOR_2_KEY), "60", "1");
        doReturn(true).when(template)
            .execute(rateLimitScript, List.of(REQUEST_DESCRIPTOR_3_KEY), "3600", "2");

        assertThrows(RateLimitException.class, () -> testedInstance.checkLimits(descriptors));
    }

    @Test
    void checkLimitsShouldNotCallRedisIfNoRuleFound() {
        List<RequestDescriptor> descriptors = List.of(requestDescriptor10());

        assertDoesNotThrow(() -> testedInstance.checkLimits(descriptors));

        verify(template, never()).execute(any(), any(), anyString(), anyString());
    }

}
