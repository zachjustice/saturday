package saturday.controllers;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
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
        return getErrorResponse(e);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public Map<String, String> handleAccessDeniedException(Exception e) {
        return getErrorResponse(e);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class) // TODO Exceptions should be 500, and explicitly catch 400 errors
    public Map<String, String> handleException(Exception e) {
        return getErrorResponse(e);
    }

    private Map<String, String> getErrorResponse(Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("Status", "failure");
        response.put("message", e.getMessage());

        return response;
    }
}
