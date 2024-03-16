package com.koreanguide.koreanguidebackend.domain.mail.exception;

public class KeyIncorrectException extends RuntimeException {
    public KeyIncorrectException() {
        super();
    }
    public KeyIncorrectException(String message, Throwable cause) {
        super(message, cause);
    }
    public KeyIncorrectException(String message) {
        super(message);
    }
    public KeyIncorrectException(Throwable cause) {
        super(cause);
    }
}
