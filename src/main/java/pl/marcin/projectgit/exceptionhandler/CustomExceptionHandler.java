package pl.marcin.projectgit.exceptionhandler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<Object> handleUserNotFound(HttpClientErrorException.NotFound ex) {
        String errorMessage = "User not found";
        int statusCode = HttpStatus.NOT_FOUND.value();
        String statusMessage = HttpStatus.NOT_FOUND.getReasonPhrase();
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", errorMessage);
        errorResponse.put("status", statusCode);

        return ResponseEntity.status(statusCode).body(Collections.singletonList(errorResponse));
    }

    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
                                                                      HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = "Unsupported media type. Only 'application/json' is supported.";
        int statusCode = HttpStatus.NOT_ACCEPTABLE.value();
        String statusMessage = HttpStatus.NOT_ACCEPTABLE.getReasonPhrase();
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", statusCode);
        errorResponse.put("message", errorMessage);
        return ResponseEntity.status(statusCode).body(Collections.singletonList(errorResponse));
    }

}
