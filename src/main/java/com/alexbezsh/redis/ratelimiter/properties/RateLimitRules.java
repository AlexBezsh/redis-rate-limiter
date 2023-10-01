package com.alexbezsh.redis.ratelimiter.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
@ConfigurationProperties("rate-limit")
public class RateLimitRules {

    @Valid
    @NotEmpty
    private List<@NotNull RateLimitRule> rules;

}
