package dataAccessTests;

import dataAccess.*;
import dataAccess.Exceptions.DataAccessException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class AuthDAOTests {
    @BeforeEach
    void clearTables() throws DataAccessException {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        authDAO.clear();
    }

    @Test
    void clear() throws DataAccessException{
        SQLAuthDAO authDAO = new SQLAuthDAO();
        AuthData newAuth = new AuthData("heyyyy", "urCute");
        authDAO.createAuth(newAuth);
        authDAO.clear();
        Assertions.assertNull(authDAO.getAuth("heyyyy"));
    }

    @Test
    void createAuthPositive() throws DataAccessException{
        SQLAuthDAO authDAO = new SQLAuthDAO();
        AuthData newAuth = new AuthData("heyyyy", "urCute");
        authDAO.createAuth(newAuth);
        Assertions.assertNotNull(authDAO.getAuth("heyyyy"));
    }

    @Test
    void createAuthNegative() throws DataAccessException{
        SQLAuthDAO authDAO = new SQLAuthDAO();
        AuthData newAuth = new AuthData("heyyyy", "urCute");
        authDAO.createAuth(newAuth);
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.createAuth(newAuth));
    }

    @Test
    void getAuthPositive() throws DataAccessException{
        SQLAuthDAO authDAO = new SQLAuthDAO();
        AuthData newAuth = new AuthData("heyyyy", "urCute");
        authDAO.createAuth(newAuth);
        AuthData checkAuth = authDAO.getAuth("heyyyy");
        Assertions.assertNotNull(checkAuth);
    }

    @Test
    void getAuthNegative() throws DataAccessException{
        SQLAuthDAO authDAO = new SQLAuthDAO();
        AuthData newAuth = new AuthData("heyyyy", "urCute");
        authDAO.createAuth(newAuth);
        Assertions.assertNull(authDAO.getAuth("boooooo"));
    }

    @Test
    void deleteAuthPositive() throws DataAccessException{
        SQLAuthDAO authDAO = new SQLAuthDAO();
        AuthData newAuth = new AuthData("heyyyy", "urCute");
        authDAO.createAuth(newAuth);
        authDAO.deleteAuth("heyyyy");
        Assertions.assertNull(authDAO.getAuth("heyyyy"));
    }

    @Test
    void deleteAuthNegative() throws DataAccessException{
        SQLAuthDAO authDAO = new SQLAuthDAO();
        AuthData newAuth = new AuthData("heyyyy", "urCute");
        authDAO.createAuth(newAuth);
        authDAO.deleteAuth("sick");
        Assertions.assertNotNull(authDAO.getAuth("heyyyy"));
    }
}
