package com.strongcom.doormate.exception;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException() {
        super();
    }

    public DuplicateUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateUserException(Throwable cause) {
        super(cause);
    }

    public DuplicateUserException(String message) {
        super(message);
    }
}
