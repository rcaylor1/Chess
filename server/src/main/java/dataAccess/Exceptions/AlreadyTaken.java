package dataAccess.Exceptions;

public class AlreadyTaken extends DataAccessException {
    public AlreadyTaken(String message) {
        super(message);
    }
}