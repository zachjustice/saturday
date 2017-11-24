package saturday.exceptions;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;

public class TopicMemberNotFoundException extends ResourceNotFoundException {
    public TopicMemberNotFoundException(String message) {
        super(message);
    }
    public TopicMemberNotFoundException() {}
}
