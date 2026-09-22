package com.example.resilience.exception;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CallNotPermittedException.class)
    public ProblemDetail handleCallNotPermitted(CallNotPermittedException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, "Circuit breaker is open");
        pd.setType(URI.create("urn:problem:circuit-breaker-open"));
        return pd;
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ProblemDetail handleRequestNotPermitted(RequestNotPermitted ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
        pd.setType(URI.create("urn:problem:rate-limit-exceeded"));
        return pd;
    }
}
