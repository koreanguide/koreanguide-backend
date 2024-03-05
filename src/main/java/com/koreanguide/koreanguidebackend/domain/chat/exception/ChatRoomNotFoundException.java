package com.koreanguide.koreanguidebackend.domain.chat.exception;

public class ChatRoomNotFoundException extends RuntimeException {
    public ChatRoomNotFoundException() {
        super();
    }
    public ChatRoomNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public ChatRoomNotFoundException(String message) {
        super(message);
    }
    public ChatRoomNotFoundException(Throwable cause) {
        super(cause);
    }
}
