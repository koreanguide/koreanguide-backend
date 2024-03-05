package com.koreanguide.koreanguidebackend.domain.track.exception;

public class TrackNotFoundException extends RuntimeException {
    public TrackNotFoundException() {
        super();
    }
    public TrackNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public TrackNotFoundException(String message) {
        super(message);
    }
    public TrackNotFoundException(Throwable cause) {
        super(cause);
    }
}
