package dataAccess.Exceptions;

public class BadRequest extends DataAccessException {
    public BadRequest(String message) {
        super(message);
    }
}