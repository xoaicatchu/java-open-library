package com.example.problemdetail.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail handleOrderNotFound(OrderNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setType(URI.create("https://example.com/probs/order-not-found"));
        pd.setTitle("Order Not Found");
        String detail = messageSource.getMessage("error.order.not.found", new Object[]{ex.getOrderId()}, LocaleContextHolder.getLocale());
        pd.setDetail(detail);
        pd.setProperty("orderId", ex.getOrderId());
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ProblemDetail handleInsufficientStock(InsufficientStockException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        pd.setType(URI.create("https://example.com/probs/insufficient-stock"));
        pd.setTitle("Insufficient Stock");
        String detail = messageSource.getMessage("error.stock.insufficient", new Object[]{ex.getProductId()}, LocaleContextHolder.getLocale());
        pd.setDetail(detail);
        pd.setProperty("productId", ex.getProductId());
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(OrderAlreadyCancelledException.class)
    public ProblemDetail handleOrderAlreadyCancelled(OrderAlreadyCancelledException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setType(URI.create("https://example.com/probs/order-already-cancelled"));
        pd.setTitle("Order Already Cancelled");
        String detail = messageSource.getMessage("error.order.cancelled", new Object[]{ex.getOrderId()}, LocaleContextHolder.getLocale());
        pd.setDetail(detail);
        pd.setProperty("orderId", ex.getOrderId());
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(PaymentDeclinedException.class)
    public ProblemDetail handlePaymentDeclined(PaymentDeclinedException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.PAYMENT_REQUIRED);
        pd.setType(URI.create("https://example.com/probs/payment-declined"));
        pd.setTitle("Payment Declined");
        String detail = messageSource.getMessage("error.payment.declined", new Object[]{ex.getReason()}, LocaleContextHolder.getLocale());
        pd.setDetail(detail);
        pd.setProperty("reason", ex.getReason());
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setType(URI.create("https://example.com/probs/validation-error"));
        pd.setTitle("Validation Error");
        pd.setDetail("One or more fields failed validation.");
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        pd.setProperty("invalidParams", fieldErrors);
        pd.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }
}