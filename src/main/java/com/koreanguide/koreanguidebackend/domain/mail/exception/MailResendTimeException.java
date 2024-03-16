package com.koreanguide.koreanguidebackend.domain.mail.exception;

public class MailResendTimeException extends RuntimeException {
    public MailResendTimeException() {
        super();
    }
    public MailResendTimeException(String message, Throwable cause) {
        super(message, cause);
    }
    public MailResendTimeException(String message) {
        super(message);
    }
    public MailResendTimeException(Throwable cause) {
        super(cause);
    }
}
