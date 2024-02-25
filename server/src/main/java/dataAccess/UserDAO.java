package dataAccess;

import dataAccess.Exceptions.DataAccessException;
import model.UserData;

public interface UserDAO {
    public void clear() throws DataAccessException;

    public void createUser(UserData username) throws DataAccessException;
    public UserData getUser(String username) throws DataAccessException;
}
