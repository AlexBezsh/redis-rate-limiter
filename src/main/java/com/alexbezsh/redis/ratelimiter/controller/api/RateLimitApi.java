package com.alexbezsh.redis.ratelimiter.controller.api;

import com.alexbezsh.redis.ratelimiter.model.RequestDescriptor;
import com.alexbezsh.redis.ratelimiter.swagger.Default400Response;
import com.alexbezsh.redis.ratelimiter.swagger.Default429Response;
import com.alexbezsh.redis.ratelimiter.swagger.Default500Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Rate Limit")
@Validated
@RequestMapping("/api/v1")
public interface RateLimitApi {

    @Default400Response
    @Default429Response
    @Default500Response
    @Operation(summary = "Check and Decrement Limit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/limit", consumes = APPLICATION_JSON_VALUE)
    void limit(@RequestBody @Valid @NotEmpty List<@NotNull RequestDescriptor> descriptors);

}
