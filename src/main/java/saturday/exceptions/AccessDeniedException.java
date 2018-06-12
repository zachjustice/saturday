package saturday.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
    public AccessDeniedException() {
        super("Authenticated entity does not have sufficient permissions.");
    }
}
