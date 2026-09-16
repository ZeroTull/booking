package com.pet.booking.utils;

import io.unified.verify.hard.Verify;
import io.unified.verify.soft.Verifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.testng.annotations.Test;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    public void responseStatusExceptionKeepsItsOwnStatusCode() {
        // Previously hardcoded to BAD_REQUEST regardless of the exception's actual status.
        ResponseStatusException e = new ResponseStatusException(HttpStatus.NOT_FOUND, "Widget not found.");

        ResponseEntity<?> response = handler.handleResponseStatusException(e);

        Verify.Object.equals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void genericExceptionReturns500WithoutLeakingItsMessage() {
        // Previously this class type couldn't be handled at all: the @ExceptionHandler(Exception.class)
        // annotation matched, but the method parameter only accepted ResponseStatusException, so Spring
        // could not invoke it for any other exception type.
        RuntimeException e = new RuntimeException("some internal detail that should not reach the client");

        ResponseEntity<?> response = handler.handleUnexpectedException(e);

        Verifier verifier = new Verifier();
        verifier.Object.equals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        verifier.Bool.isFalse(String.valueOf(response.getBody()).contains("some internal detail"));
        verifier.verify();
    }
}
