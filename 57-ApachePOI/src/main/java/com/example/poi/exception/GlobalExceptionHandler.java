package com.example.poi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IOException.class)
    public ProblemDetail handleIOException(IOException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi xử lý file Excel: " + ex.getMessage());
        problemDetail.setTitle("Excel Processing Error");
        return problemDetail;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ProblemDetail handleMaxSizeException(MaxUploadSizeExceededException exc) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.EXPECTATION_FAILED, "File quá lớn!");
        problemDetail.setTitle("File Size Limit Exceeded");
        return problemDetail;
    }
}
