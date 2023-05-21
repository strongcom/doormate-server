package com.strongcom.doormate.handler;


import com.strongcom.doormate.exception.DuplicateException;
import com.strongcom.doormate.exception.DuplicateUserException;
import com.strongcom.doormate.exception.NotFoundAlarmException;
import com.strongcom.doormate.exception.NotFoundUserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 오늘 울려야할 알림이 없을 경우 응답
    @ExceptionHandler(NotFoundAlarmException.class)
    public ResponseEntity<String> handlerNotAlarmException(NotFoundAlarmException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(e.getMessage());
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<String> handlerLoginSuccess(DuplicateUserException e) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(e.getMessage());
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<String> handlerUsernameException(DuplicateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }

    @ExceptionHandler(NotFoundUserException.class)
    public ResponseEntity<String> handlerUserNotExist(NotFoundUserException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
