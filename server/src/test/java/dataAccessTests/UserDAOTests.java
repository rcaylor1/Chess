package dataAccessTests;

import dataAccess.*;
import dataAccess.Exceptions.DataAccessException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class UserDAOTests {
    @BeforeEach
    void clearTables() throws DataAccessException {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    @Test
    void clear() throws DataAccessException{
        SQLUserDAO userDAO = new SQLUserDAO();
        UserData user1 = new UserData("rcaylor", "password", "ihatethis@email.com");
        UserData user2 = new UserData("ncaylor", "12345", "ithinkImabouttopuke@email");
        userDAO.clear();
        Assertions.assertNull(userDAO.getUser("rcaylor"));
        Assertions.assertNull(userDAO.getUser("ncaylor"));
    }

    @Test
    void createUserPositive() throws DataAccessException{
        SQLUserDAO userDAO = new SQLUserDAO();
        UserData user1 = new UserData("rcaylor", "12345", "howmuchlonger@email");
        userDAO.createUser(user1);
        UserData checkUser = userDAO.getUser(user1.username());
        Assertions.assertEquals(user1, checkUser);
    }

    @Test
    void createUserNegative() throws DataAccessException{
        SQLUserDAO userDAO = new SQLUserDAO();
        UserData user1 = new UserData("rcaylor", "12345", "howmuchlonger@email");
        userDAO.createUser(user1);
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(user1));
    }

    @Test
    void getUserPositive() throws DataAccessException{
        SQLUserDAO userDAO = new SQLUserDAO();
        UserData user1 = new UserData("rcaylor", "12345", "howmuchlonger@email");
        userDAO.createUser(user1);
        UserData checkUser = userDAO.getUser(user1.username());
        Assertions.assertNotNull(checkUser);
    }

    @Test
    void getUserNegative() throws DataAccessException{
        SQLUserDAO userDAO = new SQLUserDAO();
        UserData user1 = new UserData("rcaylor", "12345", "howmuchlonger@email");
        userDAO.createUser(user1);
        Assertions.assertNull(userDAO.getUser("emma"));
    }
}
