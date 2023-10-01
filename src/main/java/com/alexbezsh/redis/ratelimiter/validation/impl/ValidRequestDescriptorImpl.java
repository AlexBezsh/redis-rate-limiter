package com.alexbezsh.redis.ratelimiter.validation.impl;

import com.alexbezsh.redis.ratelimiter.model.RequestDescriptor;
import com.alexbezsh.redis.ratelimiter.validation.ValidRequestDescriptor;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ValidRequestDescriptorImpl
    implements ConstraintValidator<ValidRequestDescriptor, RequestDescriptor> {

    public static final String REQUIRED_FIELDS_MISSING = "at least one of the following fields "
        + "must be present: clientIp, accountId, requestType";

    @Override
    public boolean isValid(RequestDescriptor descriptor, ConstraintValidatorContext context) {
        if (descriptor == null) {
            return true;
        }
        boolean isValid = isValid(descriptor);
        if (!isValid) {
            context.buildConstraintViolationWithTemplate(REQUIRED_FIELDS_MISSING)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        }
        return isValid;
    }

    private boolean isValid(RequestDescriptor descriptor) {
        return descriptor.getClientIp() != null
            || descriptor.getAccountId() != null
            || descriptor.getRequestType() != null;
    }

}
