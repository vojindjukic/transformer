package org.example.transformerapp.execution.controller;

import org.example.transformerapp.execution.service.TransformationExecutionException;
import org.example.transformerapp.transformer.RegexOperationInterruptedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.concurrent.TimeoutException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(TransformationExecutionException.class)
    public ResponseEntity<String> handleTransformationExecutionException(TransformationExecutionException ex) {

        var cause = ex.getCause();
        if (cause instanceof RegexOperationInterruptedException || cause instanceof TimeoutException) {
            return ResponseEntity
                    .status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Transformation exceeded the maximum allowed time.");
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Transformation aborted due to an internal error: " + ex.getMessage());
    }

}
