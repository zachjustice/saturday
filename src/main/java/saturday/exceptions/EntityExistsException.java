package saturday.exceptions;

public class EntityExistsException extends ResourceNotFoundException {
    public EntityExistsException(String message) {
        super(message);
    }
}
