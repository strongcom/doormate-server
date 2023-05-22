package com.strongcom.doormate.handler;


import com.strongcom.doormate.domain.Message;
import com.strongcom.doormate.exception.*;
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
    public ResponseEntity<Message> handlerNotAlarmException(NotFoundAlarmException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Message(e.getMessage()));
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<Message> handlerLoginSuccess(DuplicateUserException e) {
        log.info(e.getMessage());
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new Message(e.getMessage()));
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Message> handlerUsernameException(DuplicateException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new Message(e.getMessage()));
    }

    @ExceptionHandler(NotFoundUserException.class)
    public ResponseEntity<Message> handlerUserNotExist(NotFoundUserException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Message(e.getMessage()));
    }

    @ExceptionHandler(NotFoundAuthorizationException.class)
    public ResponseEntity<Message> handlerNotAuthorization(NotFoundAuthorizationException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Message(e.getMessage()));
    }
    @ExceptionHandler(NotFoundReminderException.class)
    public ResponseEntity<Message> handlerNotFoundReminder(NotFoundReminderException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Message(e.getMessage()));
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<Message> handlerNotFoundToken(TokenNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Message(e.getMessage()));
    }
}
