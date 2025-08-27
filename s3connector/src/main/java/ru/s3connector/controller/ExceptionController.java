package ru.s3connector.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import software.amazon.awssdk.core.exception.SdkException;

@ControllerAdvice
public class ExceptionController {

    @ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
    @ExceptionHandler(exception = SdkException.class)
    public ResponseEntity<String> catchS3(SdkException e) {
        e.printStackTrace();
        return ResponseEntity.status(418).body(e.getMessage());
    }
//    @ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
//    @ExceptionHandler(exception = RuntimeException.class)
//    public ResponseEntity<String> catchRuntime(RuntimeException e) {
//        return ResponseEntity.status(418).body(e.getMessage());
//    }
}
