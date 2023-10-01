package com.alexbezsh.redis.ratelimiter.service;

import com.alexbezsh.redis.ratelimiter.exception.RateLimitException;
import com.alexbezsh.redis.ratelimiter.model.RequestDescriptor;
import com.alexbezsh.redis.ratelimiter.properties.Interval;
import com.alexbezsh.redis.ratelimiter.properties.RateLimitRule;
import com.alexbezsh.redis.ratelimiter.properties.RateLimitRules;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(RateLimitRules.class)
public class RateLimitService {

    private final RateLimitRules rules;
    private final StringRedisTemplate template;
    private final RedisScript<Boolean> rateLimitScript;

    @SuppressWarnings("CyclomaticComplexity")
    public void checkLimits(List<RequestDescriptor> descriptors) {
        String key;
        RateLimitRule rule;
        List<String> keys = new ArrayList<>(descriptors.size());
        List<String> args = new ArrayList<>(descriptors.size() * 2);
        for (RequestDescriptor descriptor : descriptors) {
            key = getKey(descriptor);
            rule = findRule(descriptor);
            if (rule != null && !keys.contains(key)) {
                keys.add(key);
                args.add(toSeconds(rule.getTimeInterval()));
                args.add(String.valueOf(rule.getAllowedRequestsNumber() - 1));
            }
        }
        if (!keys.isEmpty()) {
            Boolean isLimitExceeded = template.execute(rateLimitScript, keys, args.toArray());
            if (Boolean.TRUE.equals(isLimitExceeded)) {
                throw new RateLimitException();
            }
        }
    }

    private String getKey(RequestDescriptor descriptor) {
        return String.format("clientIp%s:accountId%s:requestType%s",
            normalize(descriptor.getClientIp()),
            normalize(descriptor.getAccountId()),
            normalize(descriptor.getRequestType()));
    }

    private String normalize(String s) {
        return Optional.ofNullable(s).map("-"::concat).orElse("");
    }

    private RateLimitRule findRule(RequestDescriptor descriptor) {
        return rules.getRules().stream()
            .filter(r -> fieldsMatch(r.getClientIp(), descriptor.getClientIp())
                && fieldsMatch(r.getAccountId(), descriptor.getAccountId())
                && fieldsMatch(r.getRequestType(), descriptor.getRequestType()))
            .findFirst()
            .orElse(null);
    }

    private boolean fieldsMatch(String ruleField, String descriptorField) {
        if (ruleField == null) {
            return descriptorField == null;
        } else {
            return ruleField.isEmpty() && descriptorField != null
                || ruleField.equals(descriptorField);
        }
    }

    private String toSeconds(Interval interval) {
        return switch (interval) {
            case SECOND -> "1";
            case MINUTE -> "60";
            case HOUR -> "3600";
            case DAY -> "86400";
        };
    }

}
