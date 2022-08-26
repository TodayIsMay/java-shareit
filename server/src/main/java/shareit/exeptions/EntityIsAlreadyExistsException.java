package shareit.exeptions;

public class EntityIsAlreadyExistsException extends Exception {
    public EntityIsAlreadyExistsException(String message) {
        super(message);
    }
}
