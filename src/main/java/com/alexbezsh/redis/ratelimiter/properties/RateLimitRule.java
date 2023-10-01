package com.alexbezsh.redis.ratelimiter.properties;

import com.alexbezsh.redis.ratelimiter.validation.NullOrNotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitRule {

    private String clientIp;

    private String accountId;

    @NullOrNotBlank
    private String requestType;

    @NotNull
    private Interval timeInterval;

    @NotNull
    @Positive
    private Integer allowedRequestsNumber;

}
