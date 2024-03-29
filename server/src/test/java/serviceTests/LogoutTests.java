package serviceTests;

import dataAccess.*;
import dataAccess.Exceptions.DataAccessException;
import model.*;
import service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class LogoutTests {
    @Test
    void logoutPositive() throws DataAccessException {
        UserDAO userDao = new MemoryUserDAO();
        AuthDAO authDao = new MemoryAuthDAO();
        UserService service  = new UserService(userDao, authDao);
        UserData newUser = new UserData("rcaylor", "123456", "emal@email.com");
        AuthData test = service.register(newUser);
        Assertions.assertDoesNotThrow(() -> service.logout(test.authToken()));
    }

    @Test
    void logoutNegative() {
        UserDAO userDao = new MemoryUserDAO();
        AuthDAO authDao = new MemoryAuthDAO();
        UserService service  = new UserService(userDao, authDao);
        UserData newUser = new UserData("rcaylor", "123456", "emal@email.com");
        Assertions.assertThrows(DataAccessException.class, () -> service.logout("bad") );
    }
}
