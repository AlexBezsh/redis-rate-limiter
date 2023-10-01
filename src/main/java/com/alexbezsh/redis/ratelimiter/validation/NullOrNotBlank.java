package com.alexbezsh.redis.ratelimiter.validation;

import com.alexbezsh.redis.ratelimiter.validation.impl.NullOrNotBlankImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NullOrNotBlankImpl.class)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.TYPE_USE})
public @interface NullOrNotBlank {

    String message() default "must be null or not blank";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
