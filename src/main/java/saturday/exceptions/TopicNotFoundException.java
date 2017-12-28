package saturday.exceptions;

public class TopicNotFoundException extends ResourceNotFoundException {
    public TopicNotFoundException(String message) {
        super(message);
    }
}
