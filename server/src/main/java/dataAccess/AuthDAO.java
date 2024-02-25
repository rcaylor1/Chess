package dataAccess;

import model.AuthData;

public interface AuthDAO {
    public void clear() throws DataAccessException;
    public AuthData createAuth(AuthData auth) throws DataAccessException;
    public AuthData getAuth(String auth) throws DataAccessException;
    public void deleteAuth(String authToken) throws DataAccessException;
}
