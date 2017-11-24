package saturday.exceptions;

public class TopicMemberNotFoundException extends Exception {
    public TopicMemberNotFoundException(String message) {
        super(message);
    }
    public TopicMemberNotFoundException() {}
}
