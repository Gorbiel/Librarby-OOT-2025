package agh.oot.librarby.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    private final String existingResourceId;

    public ResourceAlreadyExistsException(String message, Object existingResourceId) {
        super(message);
        this.existingResourceId = String.valueOf(existingResourceId);
    }

    public String getExistingResourceId() {
        return existingResourceId;
    }
}