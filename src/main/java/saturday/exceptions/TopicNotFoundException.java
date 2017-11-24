package saturday.exceptions;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;

public class TopicNotFoundException extends ResourceNotFoundException {
    public TopicNotFoundException(String message) {
        super(message);
    }
}
