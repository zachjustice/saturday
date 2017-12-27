package saturday.exceptions;

public class AccessDeniedException extends Exception {
    public AccessDeniedException(String message) {
        super(message);
    }
    public AccessDeniedException() {}
}
