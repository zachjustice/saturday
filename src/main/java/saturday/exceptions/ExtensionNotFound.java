package saturday.exceptions;

public class ExtensionNotFound extends RuntimeException {
    public ExtensionNotFound(String message) {
        super(message);
    }
    public ExtensionNotFound() {
        super("Extension not found.");
    }
}
