package kr.yuns.saved.data.exception;

public class SavedEntityNotFoundException extends RuntimeException {
    public SavedEntityNotFoundException() {
        super();
    }
    public SavedEntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public SavedEntityNotFoundException(String message) {
        super(message);
    }
    public SavedEntityNotFoundException(Throwable cause) {
        super(cause);
    }
}