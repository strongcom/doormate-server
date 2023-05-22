package com.strongcom.doormate.exception;

public class NotFoundAuthorizationException extends RuntimeException {
    public NotFoundAuthorizationException() {
        super();
    }

    public NotFoundAuthorizationException(String message) {
        super(message);
    }

    public NotFoundAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundAuthorizationException(Throwable cause) {
        super(cause);
    }
}
