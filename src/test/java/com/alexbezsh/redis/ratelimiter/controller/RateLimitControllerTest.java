package com.alexbezsh.redis.ratelimiter.controller;

import com.alexbezsh.redis.ratelimiter.exception.RateLimitException;
import com.alexbezsh.redis.ratelimiter.model.ErrorResponse;
import com.alexbezsh.redis.ratelimiter.model.RequestDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor1;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptors;
import static com.alexbezsh.redis.ratelimiter.TestUtils.toJson;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RateLimitControllerTest extends AbstractControllerTest {

    public static final String BASE_URL = "/api/v1/limit";

    @Test
    void rateLimitCheckShouldReturn204() throws Exception {
        List<RequestDescriptor> request = requestDescriptors();

        mockMvc.perform(post(BASE_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isNoContent());

        verify(rateLimitService).checkLimits(request);
    }

    @ParameterizedTest
    @MethodSource("invalidDescriptors")
    void rateLimitCheckShouldReturn400(List<RequestDescriptor> request,
                                       String message) throws Exception {
        ErrorResponse expected = new ErrorResponse(BAD_REQUEST, message);

        mockMvc.perform(post(BASE_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(toJson(expected)));

        verify(rateLimitService, never()).checkLimits(any());
    }

    static Stream<Arguments> invalidDescriptors() {
        return Stream.of(
            Arguments.of(List.of(RequestDescriptor.builder().build()),
                "limit.descriptors[0]: at least one of the following fields must be present: "
                    + "clientIp, accountId, requestType"),
            Arguments.of(List.of(RequestDescriptor.builder().clientIp("").build()),
                "limit.descriptors[0].clientIp: must be null or not blank"),
            Arguments.of(Arrays.asList(null, requestDescriptor1()),
                "limit.descriptors[0].<list element>: must not be null"),
            Arguments.of(emptyList(), "limit.descriptors: must not be empty")
        );
    }

    @Test
    void rateLimitCheckShouldReturn429() throws Exception {
        List<RequestDescriptor> request = requestDescriptors();
        ErrorResponse expected = new ErrorResponse(TOO_MANY_REQUESTS, "Rate limit exceeded");

        doThrow(new RateLimitException()).when(rateLimitService).checkLimits(request);

        mockMvc.perform(post(BASE_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isTooManyRequests())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void rateLimitCheckShouldReturn500() throws Exception {
        List<RequestDescriptor> request = requestDescriptors();
        ErrorResponse expected = new ErrorResponse(
            INTERNAL_SERVER_ERROR, "Unexpected error. Reason: null");

        doThrow(RuntimeException.class).when(rateLimitService).checkLimits(request);

        mockMvc.perform(post(BASE_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andExpect(content().json(toJson(expected)));
    }

}
