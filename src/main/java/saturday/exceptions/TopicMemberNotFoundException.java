package saturday.exceptions;

public class TopicMemberNotFoundException extends ResourceNotFoundException {
    public TopicMemberNotFoundException(String message) {
        super(message);
    }
    public TopicMemberNotFoundException() {}
}
