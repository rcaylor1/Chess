package dataAccess;

import model.AuthData;

public interface AuthDAO {
    public void clear() throws DataAccessException;
    public AuthData createAuth(String username) throws DataAccessException;
    public AuthData getAuth(AuthData authToken) throws DataAccessException;
    public void deleteAuth(AuthData authToken) throws DataAccessException;
}
