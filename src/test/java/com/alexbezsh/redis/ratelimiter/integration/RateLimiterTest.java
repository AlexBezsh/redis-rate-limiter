package com.alexbezsh.redis.ratelimiter.integration;

import com.alexbezsh.redis.ratelimiter.model.RequestDescriptor;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultMatcher;
import static com.alexbezsh.redis.ratelimiter.TestUtils.REQUEST_DESCRIPTOR_1_KEY;
import static com.alexbezsh.redis.ratelimiter.TestUtils.REQUEST_DESCRIPTOR_2_KEY;
import static com.alexbezsh.redis.ratelimiter.TestUtils.REQUEST_DESCRIPTOR_3_KEY;
import static com.alexbezsh.redis.ratelimiter.TestUtils.REQUEST_DESCRIPTOR_4_KEY;
import static com.alexbezsh.redis.ratelimiter.TestUtils.REQUEST_DESCRIPTOR_6_KEY;
import static com.alexbezsh.redis.ratelimiter.TestUtils.REQUEST_DESCRIPTOR_7_KEY;
import static com.alexbezsh.redis.ratelimiter.TestUtils.getDescriptorKeys;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor1;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor2;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor3;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor4;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor6;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor7;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptors;
import static com.alexbezsh.redis.ratelimiter.TestUtils.toJson;
import static com.alexbezsh.redis.ratelimiter.controller.RateLimitControllerTest.BASE_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RateLimiterTest extends AbstractIntegrationTest {

    @Test
    void rateLimitCheckShouldReturn204() throws Exception {
        List<RequestDescriptor> request = requestDescriptors();
        List<String> expectedKeys = getDescriptorKeys();
        List<String> expectedValues = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8");

        executeRequest(request, status().isNoContent());

        Set<String> keys = redisKeys();
        List<String> values = template.opsForValue().multiGet(keys);

        assertThat(keys).hasSameElementsAs(expectedKeys);
        assertThat(values).hasSameElementsAs(expectedValues);
    }

    @Test
    void rateLimitCheckShouldDecrementCounters() throws Exception {
        List<RequestDescriptor> request = List.of(requestDescriptor3(),
            requestDescriptor4(), requestDescriptor6(), requestDescriptor7());
        List<String> expectedKeys = List.of(REQUEST_DESCRIPTOR_3_KEY,
            REQUEST_DESCRIPTOR_4_KEY, REQUEST_DESCRIPTOR_6_KEY, REQUEST_DESCRIPTOR_7_KEY);
        List<String> expectedValues1 = List.of("2", "3", "5", "6");
        List<String> expectedValues2 = List.of("1", "2", "4", "5");
        List<String> expectedValues3 = List.of("0", "1", "3", "4");
        List<String> expectedValues4 = List.of("0", "0", "2", "3");

        executeRequest(request, status().isNoContent());
        List<String> values1 = template.opsForValue().multiGet(expectedKeys);
        assertThat(values1).hasSameElementsAs(expectedValues1);

        executeRequest(request, status().isNoContent());
        List<String> values2 = template.opsForValue().multiGet(expectedKeys);
        assertThat(values2).hasSameElementsAs(expectedValues2);

        executeRequest(request, status().isNoContent());
        List<String> values3 = template.opsForValue().multiGet(expectedKeys);
        assertThat(values3).hasSameElementsAs(expectedValues3);

        executeRequest(request, status().isTooManyRequests());
        List<String> values4 = template.opsForValue().multiGet(expectedKeys);
        assertThat(values4).hasSameElementsAs(expectedValues4);
    }

    @Test
    void rateLimitCheckShouldReturn429() throws Exception {
        List<RequestDescriptor> request = List.of(requestDescriptor2());

        executeRequest(request, status().isNoContent());
        String value = template.opsForValue().get(REQUEST_DESCRIPTOR_2_KEY);
        assertEquals("1", value);

        executeRequest(request, status().isNoContent());
        value = template.opsForValue().get(REQUEST_DESCRIPTOR_2_KEY);
        assertEquals("0", value);

        executeRequest(request, status().isTooManyRequests());
        value = template.opsForValue().get(REQUEST_DESCRIPTOR_2_KEY);
        assertEquals("0", value);
    }

    @Test
    void rateLimitCheckShouldReturn204IfPreviousKeyExpired() throws Exception {
        List<RequestDescriptor> request = List.of(requestDescriptor1());

        executeRequest(request, status().isNoContent());
        String value = template.opsForValue().get(REQUEST_DESCRIPTOR_1_KEY);
        assertEquals("0", value);

        Thread.sleep(1001);
        executeRequest(request, status().isNoContent());
        value = template.opsForValue().get(REQUEST_DESCRIPTOR_1_KEY);
        assertEquals("0", value);
    }

    private void executeRequest(List<RequestDescriptor> request,
                                ResultMatcher matcher) throws Exception {
        mockMvc.perform(post(BASE_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON))
            .andExpect(matcher);
    }

}
