package example.tdd.example1.controller;

import example.tdd.example1.enums.MembershipErrorResult;
import example.tdd.example1.exception.MembershipException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler  {

    @ExceptionHandler(MembershipException.class)
    public ResponseEntity<ErrorResponse> handleException(MembershipException e) {
        log.warn("MembershipException occurL ", e);
        return makeErrorResponseEntity(e.getErrorResult());
    }

    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(MembershipErrorResult errorResult) {
        return ResponseEntity.status(errorResult.getHttpStatus())
                .body(new ErrorResponse(errorResult.name(), errorResult.getMessage()));
    }


    @Getter
    @RequiredArgsConstructor
    static class ErrorResponse {
        private final String code;
        private final String message;
    }
}
