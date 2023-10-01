package com.alexbezsh.redis.ratelimiter;

import com.alexbezsh.redis.ratelimiter.model.RequestDescriptor;
import com.alexbezsh.redis.ratelimiter.properties.Interval;
import com.alexbezsh.redis.ratelimiter.properties.RateLimitRule;
import com.alexbezsh.redis.ratelimiter.properties.RateLimitRules;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {

    public static final String CLIENT_IP_1 = "1.1.1.1";
    public static final String ACCOUNT_ID_1 = "1";
    public static final String REQUEST_TYPE_1 = "login";

    public static final String CLIENT_IP_2 = "2.2.2.2";
    public static final String ACCOUNT_ID_2 = "2";
    public static final String REQUEST_TYPE_2 = "getCart";

    public static final String REQUEST_DESCRIPTOR_1_KEY =
        "clientIp-1.1.1.1:accountId-1:requestType-login";
    public static final String REQUEST_DESCRIPTOR_2_KEY =
        "clientIp-1.1.1.1:accountId-1:requestType";
    public static final String REQUEST_DESCRIPTOR_3_KEY =
        "clientIp-1.1.1.1:accountId:requestType-login";
    public static final String REQUEST_DESCRIPTOR_4_KEY = "clientIp:accountId-1:requestType-login";
    public static final String REQUEST_DESCRIPTOR_5_KEY = "clientIp-1.1.1.1:accountId:requestType";
    public static final String REQUEST_DESCRIPTOR_6_KEY = "clientIp:accountId-1:requestType";
    public static final String REQUEST_DESCRIPTOR_7_KEY = "clientIp:accountId:requestType-login";
    public static final String REQUEST_DESCRIPTOR_8_KEY = "clientIp-2.2.2.2:accountId:requestType";
    public static final String REQUEST_DESCRIPTOR_9_KEY = "clientIp:accountId-2:requestType";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(
                "Unable to convert object to JSON. Reason: " + e.getMessage());
        }
    }

    public static List<String> getMessages(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .collect(Collectors.toList());
    }

    public static List<String> getDescriptorKeys() {
        return List.of(REQUEST_DESCRIPTOR_1_KEY, REQUEST_DESCRIPTOR_2_KEY, REQUEST_DESCRIPTOR_3_KEY,
            REQUEST_DESCRIPTOR_4_KEY, REQUEST_DESCRIPTOR_5_KEY, REQUEST_DESCRIPTOR_6_KEY,
            REQUEST_DESCRIPTOR_7_KEY, REQUEST_DESCRIPTOR_8_KEY, REQUEST_DESCRIPTOR_9_KEY);
    }

    public static RateLimitRules rateLimitRules() {
        return RateLimitRules.builder()
            .rules(Arrays.asList(rateLimitRule1(), rateLimitRule2(), rateLimitRule3(),
                rateLimitRule4(), rateLimitRule5(), rateLimitRule6(), rateLimitRule7(),
                rateLimitRule8(), rateLimitRule9()))
            .build();
    }

    public static List<RequestDescriptor> requestDescriptors() {
        return Arrays.asList(requestDescriptor1(), requestDescriptor2(), requestDescriptor3(),
            requestDescriptor4(), requestDescriptor5(), requestDescriptor6(), requestDescriptor7(),
            requestDescriptor8(), requestDescriptor9(), requestDescriptor10());
    }

    public static RateLimitRule rateLimitRule1() {
        return RateLimitRule.builder()
            .clientIp(CLIENT_IP_1)
            .accountId(ACCOUNT_ID_1)
            .requestType(REQUEST_TYPE_1)
            .timeInterval(Interval.SECOND)
            .allowedRequestsNumber(1)
            .build();
    }

    public static RequestDescriptor requestDescriptor1() {
        return RequestDescriptor.builder()
            .clientIp(CLIENT_IP_1)
            .accountId(ACCOUNT_ID_1)
            .requestType(REQUEST_TYPE_1)
            .build();
    }

    public static RateLimitRule rateLimitRule2() {
        return RateLimitRule.builder()
            .clientIp(CLIENT_IP_1)
            .accountId(ACCOUNT_ID_1)
            .timeInterval(Interval.MINUTE)
            .allowedRequestsNumber(2)
            .build();
    }

    public static RequestDescriptor requestDescriptor2() {
        return RequestDescriptor.builder()
            .clientIp(CLIENT_IP_1)
            .accountId(ACCOUNT_ID_1)
            .build();
    }

    public static RateLimitRule rateLimitRule3() {
        return RateLimitRule.builder()
            .clientIp(CLIENT_IP_1)
            .requestType(REQUEST_TYPE_1)
            .timeInterval(Interval.HOUR)
            .allowedRequestsNumber(3)
            .build();
    }

    public static RequestDescriptor requestDescriptor3() {
        return RequestDescriptor.builder()
            .clientIp(CLIENT_IP_1)
            .requestType(REQUEST_TYPE_1)
            .build();
    }

    public static RateLimitRule rateLimitRule4() {
        return RateLimitRule.builder()
            .accountId(ACCOUNT_ID_1)
            .requestType(REQUEST_TYPE_1)
            .timeInterval(Interval.DAY)
            .allowedRequestsNumber(4)
            .build();
    }

    public static RequestDescriptor requestDescriptor4() {
        return RequestDescriptor.builder()
            .accountId(ACCOUNT_ID_1)
            .requestType(REQUEST_TYPE_1)
            .build();
    }

    public static RateLimitRule rateLimitRule5() {
        return RateLimitRule.builder()
            .clientIp(CLIENT_IP_1)
            .timeInterval(Interval.SECOND)
            .allowedRequestsNumber(5)
            .build();
    }

    public static RequestDescriptor requestDescriptor5() {
        return RequestDescriptor.builder()
            .clientIp(CLIENT_IP_1)
            .build();
    }

    public static RateLimitRule rateLimitRule6() {
        return RateLimitRule.builder()
            .accountId(ACCOUNT_ID_1)
            .timeInterval(Interval.MINUTE)
            .allowedRequestsNumber(6)
            .build();
    }

    public static RequestDescriptor requestDescriptor6() {
        return RequestDescriptor.builder()
            .accountId(ACCOUNT_ID_1)
            .build();
    }

    public static RateLimitRule rateLimitRule7() {
        return RateLimitRule.builder()
            .requestType(REQUEST_TYPE_1)
            .timeInterval(Interval.HOUR)
            .allowedRequestsNumber(7)
            .build();
    }

    public static RequestDescriptor requestDescriptor7() {
        return RequestDescriptor.builder()
            .requestType(REQUEST_TYPE_1)
            .build();
    }

    public static RateLimitRule rateLimitRule8() {
        return RateLimitRule.builder()
            .clientIp("")
            .timeInterval(Interval.DAY)
            .allowedRequestsNumber(8)
            .build();
    }

    public static RequestDescriptor requestDescriptor8() {
        return RequestDescriptor.builder()
            .clientIp(CLIENT_IP_2)
            .build();
    }

    public static RateLimitRule rateLimitRule9() {
        return RateLimitRule.builder()
            .accountId("")
            .timeInterval(Interval.SECOND)
            .allowedRequestsNumber(9)
            .build();
    }

    public static RequestDescriptor requestDescriptor9() {
        return RequestDescriptor.builder()
            .accountId(ACCOUNT_ID_2)
            .build();
    }

    // won't match any rule
    public static RequestDescriptor requestDescriptor10() {
        return RequestDescriptor.builder()
            .requestType(REQUEST_TYPE_2)
            .build();
    }

}
