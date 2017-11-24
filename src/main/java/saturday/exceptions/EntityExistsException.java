package saturday.exceptions;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;

public class EntityExistsException extends ResourceNotFoundException {
    public EntityExistsException(String message) {
        super(message);
    }
}
