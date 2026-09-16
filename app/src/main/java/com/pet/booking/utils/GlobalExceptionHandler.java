package com.pet.booking.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
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

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<JsonResponse> handleResponseStatusException(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(new JsonResponse(e.getReason()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonResponse> handleUnexpectedException(Exception e) {
        // The client only ever gets the generic message below (don't leak internals), but
        // without logging the real exception server-side this class of failure is
        // undiagnosable -- confirmed the hard way: this handler caught a real bug with zero
        // trace of what it was.
        log.error("Unhandled exception", e);
        return new ResponseEntity<>(new JsonResponse("An unexpected error occurred."),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}