package com.alexbezsh.redis.ratelimiter.validation;

import com.alexbezsh.redis.ratelimiter.validation.impl.ValidRequestDescriptorImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidRequestDescriptorImpl.class)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.TYPE_USE})
public @interface ValidRequestDescriptor {

    String message() default "invalid request descriptor";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
