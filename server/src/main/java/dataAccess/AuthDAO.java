package dataAccess;

import model.AuthData;

public interface AuthDAO {
    public void clear() throws DataAccessException;
    public AuthData createAuth(AuthData authToken) throws DataAccessException;
    public AuthData getAuth(String auth) throws DataAccessException;
    public void deleteAuth(AuthData authToken) throws DataAccessException;
}
