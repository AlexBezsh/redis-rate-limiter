package com.alexbezsh.redis.ratelimiter.validation.impl;

import com.alexbezsh.redis.ratelimiter.validation.NullOrNotBlank;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import static com.alexbezsh.redis.ratelimiter.TestUtils.getMessages;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NullOrNotBlankImplTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @ParameterizedTest
    @ValueSource(strings = {"", "    ", " \n \n \n\r "})
    void nullOrNotBlankShouldReturnViolation(String value) {
        List<String> expected = List.of("value: must be null or not blank");

        List<String> actual = getMessages(validator.validate(new TestClass(value)));

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"a", "b c"})
    void nullOrNotBlankShouldNotReturnViolation(String value) {
        List<String> actual = getMessages(validator.validate(new TestClass(value)));

        assertEquals(emptyList(), actual);
    }

    @Data
    @Builder
    @AllArgsConstructor
    private static class TestClass {

        @NullOrNotBlank
        private String value;

    }

}
