package com.alexbezsh.redis.ratelimiter.model;

import com.alexbezsh.redis.ratelimiter.validation.NullOrNotBlank;
import com.alexbezsh.redis.ratelimiter.validation.ValidRequestDescriptor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidRequestDescriptor
public class RequestDescriptor {

    @NullOrNotBlank
    private String clientIp;

    @NullOrNotBlank
    private String accountId;

    @NullOrNotBlank
    private String requestType;

}
