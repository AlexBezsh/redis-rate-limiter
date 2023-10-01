package com.alexbezsh.redis.ratelimiter.validation.impl;

import com.alexbezsh.redis.ratelimiter.validation.NullOrNotBlank;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullOrNotBlankImpl implements ConstraintValidator<NullOrNotBlank, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        return s == null || s.trim().length() > 0;
    }

}
