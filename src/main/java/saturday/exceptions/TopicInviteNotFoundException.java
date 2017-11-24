package saturday.exceptions;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;

public class TopicInviteNotFoundException extends ResourceNotFoundException {
    public TopicInviteNotFoundException(String message) {
        super(message);
    }
    public TopicInviteNotFoundException() {}
}
