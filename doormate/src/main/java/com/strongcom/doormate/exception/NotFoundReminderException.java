package com.strongcom.doormate.exception;

public class NotFoundReminderException extends RuntimeException {
    public NotFoundReminderException() {
        super();
    }

    public NotFoundReminderException(String message) {
        super(message);
    }

    public NotFoundReminderException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundReminderException(Throwable cause) {
        super(cause);
    }
}
