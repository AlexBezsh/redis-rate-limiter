package com.alexbezsh.redis.ratelimiter.validation.impl;

import com.alexbezsh.redis.ratelimiter.model.RequestDescriptor;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static com.alexbezsh.redis.ratelimiter.TestUtils.getMessages;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor1;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor10;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor2;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor3;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor4;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor5;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor6;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor7;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor8;
import static com.alexbezsh.redis.ratelimiter.TestUtils.requestDescriptor9;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidRequestDescriptorImplTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void nullOrNotBlankShouldReturnViolation() {
        RequestDescriptor descriptor = RequestDescriptor.builder().build();
        List<String> expected = List.of("descriptor: at least one of the following fields must "
            + "be present: clientIp, accountId, requestType");

        List<String> actual = getMessages(validator.validate(new TestClass(descriptor)));

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("validRequestDescriptors")
    void nullOrNotBlankShouldNotReturnViolation(RequestDescriptor descriptor) {

        List<String> actual = getMessages(validator.validate(descriptor));

        assertEquals(emptyList(), actual);
    }

    static Stream<Arguments> validRequestDescriptors() {
        return Stream.of(
            Arguments.of(requestDescriptor1()),
            Arguments.of(requestDescriptor2()),
            Arguments.of(requestDescriptor3()),
            Arguments.of(requestDescriptor4()),
            Arguments.of(requestDescriptor5()),
            Arguments.of(requestDescriptor6()),
            Arguments.of(requestDescriptor7()),
            Arguments.of(requestDescriptor8()),
            Arguments.of(requestDescriptor9()),
            Arguments.of(requestDescriptor10())
        );
    }

    @Data
    @Builder
    @AllArgsConstructor
    private static class TestClass {

        @Valid
        private RequestDescriptor descriptor;

    }

}
