package serviceTests;

import dataAccess.*;
import dataAccess.Exceptions.DataAccessException;
import model.*;
import service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
public class JoinGameTests {
    @Test
    void joinGamePositive() throws DataAccessException {
        AuthDAO testAuthDAO = new MemoryAuthDAO();
        GameDAO testGameDAO = new MemoryGameDAO();
        GameService service = new GameService(testGameDAO, testAuthDAO);

        testAuthDAO.createAuth(new AuthData("authToken", "rcaylor"));
        int gameID = service.createGame("authToken", "gamename");

        Assertions.assertDoesNotThrow(() -> service.joinGame("authToken", "WHITE", gameID));
    }

    @Test
    void joinGameNegative() throws DataAccessException {
        AuthDAO testAuthDAO = new MemoryAuthDAO();
        GameDAO testGameDAO = new MemoryGameDAO();
        GameService service = new GameService(testGameDAO, testAuthDAO);

        testAuthDAO.createAuth(new AuthData("authToken", "rcaylor"));
        testAuthDAO.createAuth(new AuthData("newAuth", "hopefullyalmostdone"));

        int gameID = service.createGame("authToken", "gamename");
        service.joinGame("authToken", "WHITE", gameID);

        Assertions.assertThrows(DataAccessException.class, () -> service.joinGame("newAuth", "WHITE", gameID));
    }
}
