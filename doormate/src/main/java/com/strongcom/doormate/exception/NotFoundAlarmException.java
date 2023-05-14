package com.strongcom.doormate.exception;

public class NotFoundAlarmException extends NullPointerException {
    public NotFoundAlarmException() {
        super();
    }

    public NotFoundAlarmException(String s) {
        super(s);
    }
}
