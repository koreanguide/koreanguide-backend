package com.koreanguide.koreanguidebackend.domain.review.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException() {
        super();
    }
    public ReviewNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public ReviewNotFoundException(String message) {
        super(message);
    }
    public ReviewNotFoundException(Throwable cause) {
        super(cause);
    }
}
