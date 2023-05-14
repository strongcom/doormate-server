package com.strongcom.doormate.handler;


import com.strongcom.doormate.exception.NotFoundAlarmException;
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
}
