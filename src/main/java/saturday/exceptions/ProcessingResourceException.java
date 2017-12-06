package saturday.exceptions;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;

public class ProcessingResourceException extends ResourceNotFoundException {
    public ProcessingResourceException(String message) {
        super(message);
    }
    public ProcessingResourceException() {}
}
