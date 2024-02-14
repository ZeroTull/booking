package com.pet.booking.utils;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    @Data
    private static class JsonResponse {
        String message;

        public JsonResponse() {
        }

        public JsonResponse(String message) {
            super();
            this.message = message;
        }
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<JsonResponse> handleResponseStatusExceptionException(ResponseStatusException e) {
        return new ResponseEntity<>(new JsonResponse(e.getReason()),
                HttpStatus.BAD_REQUEST);
    }
}