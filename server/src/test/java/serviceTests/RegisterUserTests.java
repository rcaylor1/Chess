package serviceTests;

import chess.ChessGame;
import dataAccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class RegisterUserTests {
    @Test
    void registerPositive() throws DataAccessException{
        UserDAO userDao = new MemoryUserDAO();
        AuthDAO authDao = new MemoryAuthDAO();
        UserService service  = new UserService(userDao, authDao);
        UserData newUser = new UserData("rcaylor", "12345", "email@email.com");

        AuthData test = service.register(newUser);
        Assertions.assertEquals(test.username(), newUser.username());
        Assertions.assertNotNull(test.authToken());
    }

    @Test
    void registerNegative() throws DataAccessException{
        UserDAO userDao = new MemoryUserDAO();
        AuthDAO authDao = new MemoryAuthDAO();
        UserService service = new UserService(userDao, authDao);
        UserData user1 = new UserData("rcaylor", "123456", "emal@email.com");
        UserData user2 = new UserData("rcaylor", "98765", "ema@email.com");

        AuthData test = service.register(user1);

        Assertions.assertThrows(DataAccessException.class, () -> service.register(user2));
    }
}
