package saturday.exceptions;

public class ProcessingResourceException extends RuntimeException {
    public ProcessingResourceException(String message) {
        super(message);
    }
    public ProcessingResourceException() {}
}
