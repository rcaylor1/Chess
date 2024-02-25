package serviceTests;

import dataAccess.*;
import dataAccess.Exceptions.DataAccessException;
import model.*;
import service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class LoginTests {
    @Test
    void loginPositive() throws DataAccessException {
        UserDAO userDao = new MemoryUserDAO();
        AuthDAO authDao = new MemoryAuthDAO();
        UserService service  = new UserService(userDao, authDao);
        UserData newUser = new UserData("rcaylor", "12345", "email@email.com");
        userDao.createUser(newUser);
        LoginRequest newLogin = new LoginRequest("rcaylor", "12345");

        AuthData test = service.login(newLogin);

        Assertions.assertEquals("rcaylor", test.username());
    }

    @Test
    void loginNegative() throws DataAccessException{
        UserDAO userDao = new MemoryUserDAO();
        AuthDAO authDao = new MemoryAuthDAO();
        UserService service  = new UserService(userDao, authDao);
        UserData newUser = new UserData("rcaylor", "12345", "email@email.com");
        userDao.createUser(newUser);

        LoginRequest newLogin = new LoginRequest("rcaylor", "123456");

        Assertions.assertThrows(DataAccessException.class, () -> service.login(newLogin));
    }
}
