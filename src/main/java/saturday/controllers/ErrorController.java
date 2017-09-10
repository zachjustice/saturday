package storyboard.controllers;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zachjustice on 8/15/17.
 */
@RestControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public Map<String, String> handleResourceNotFoundException(ResourceNotFoundException e) {
        Map<String, String> response = new HashMap<>();
        response.put("Status", "failure");
        response.put("message", e.getMessage());

        return response;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public Map<String, String> handleException(Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("Status", "failure");
        response.put("message", e.getMessage());

        return response;
    }
}
