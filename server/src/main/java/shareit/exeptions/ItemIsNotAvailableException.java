package shareit.exeptions;

public class ItemIsNotAvailableException extends Exception {
    public ItemIsNotAvailableException(String message) {
        super(message);
    }
}
