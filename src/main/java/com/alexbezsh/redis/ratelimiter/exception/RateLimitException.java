package com.alexbezsh.redis.ratelimiter.exception;

public class RateLimitException extends RuntimeException {

    public RateLimitException() {
        super("Rate limit exceeded");
    }

}
