package serviceTests;

import dataAccess.*;
import dataAccess.Exceptions.DataAccessException;
import model.*;
import service.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class CreateGameTests {
    @Test
    void createGamePositive() throws DataAccessException {
        GameDAO gameDao = new MemoryGameDAO();
        AuthDAO authDao = new MemoryAuthDAO();
        GameService service  = new GameService(gameDao, authDao);
        AuthData authData = new AuthData("authToken", "username");
        AuthData authData2 = new AuthData("newAuth", "newUser");

        authDao.createAuth(authData);
        authDao.createAuth(authData2);
        int gameID = service.createGame("authToken", "imtiredofthis");
        int newGameID = service.createGame("newAuth", "newGame");

        Assertions.assertNotEquals(gameID, newGameID);
    }

    @Test
    void createGameNegative() throws DataAccessException {
        GameDAO gameDao = new MemoryGameDAO();
        AuthDAO authDao = new MemoryAuthDAO();
        GameService service  = new GameService(gameDao, authDao);

        Assertions.assertThrows(DataAccessException.class, () -> service.createGame("noAuthToken", "pleaseletmebedone"));
    }
}
