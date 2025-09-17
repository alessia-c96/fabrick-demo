package com.example.fabrick_demo.exception;

import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeign(FeignException ex) {
        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) status = HttpStatus.BAD_GATEWAY;

        String body = ex.contentUTF8();
        if (body == null || body.isBlank()) body = "{\"status\":\"KO\",\"errors\":[{\"description\":\"Upstream error\"}],\"payload\":{}}";

        MediaType ct = MediaType.APPLICATION_JSON;
        try {
            var hdrs = ex.responseHeaders();
            if (hdrs != null) {
                var ctVals = hdrs.getOrDefault("content-type", Collections.emptyList());
                if (!ctVals.isEmpty()) {
                    ct = MediaType.parseMediaType(ctVals.iterator().next());
                }
            }
        } catch (Exception ignore) {}

        log.error("Feign error: status={} body={}", status.value(), body);
        return ResponseEntity.status(status)
                .contentType(ct)
                .body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleRse(ResponseStatusException ex) {
        String msg = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        log.error("RSE: status={} reason={}", ex.getStatusCode().value(), msg);
        return ResponseEntity.status(ex.getStatusCode())
                .contentType(MediaType.TEXT_PLAIN)
                .body(msg);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidBody(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
                .toList();

        var body = new LinkedHashMap<String, Object>();
        body.put("status", "KO");
        body.put("errors", errors);
        body.put("payload", Collections.emptyMap());

        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidParams(ConstraintViolationException ex) {
        var errors = ex.getConstraintViolations().stream()
                .map(v -> Map.of("param", v.getPropertyPath().toString(), "message", v.getMessage()))
                .toList();

        var body = new LinkedHashMap<String, Object>();
        body.put("status", "KO");
        body.put("errors", errors);
        body.put("payload", Collections.emptyMap());

        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        var body = Map.of(
                "status", "KO",
                "errors", List.of(Map.of("description", "Internal error")),
                "payload", Collections.emptyMap());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

}
