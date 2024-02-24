package serviceTests;

import chess.ChessGame;
import dataAccess.*;
import model.*;
import service.ClearService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class ClearServiceTest {
    @Test
    void clearTest() throws DataAccessException{

        UserDAO testUser = new MemoryUserDAO();
        GameDAO testGame = new MemoryGameDAO();
        AuthDAO testAuth = new MemoryAuthDAO();

        testUser.createUser(new UserData("rcaylor", "12345", "abcd@gmail.com"));
        testGame.createGame(new GameData(4, "whiteUser", "blackUser", "testGame", new ChessGame()));
        testAuth.createAuth(new AuthData("dvjssv", "rebecca"));

        ClearService service = new ClearService(testUser, testGame, testAuth);

        service.clear();
        Assertions.assertNull(testUser.getUser("rcaylor"));
        Assertions.assertNull(testGame.getGame(4));
        Assertions.assertNull(testAuth.getAuth("dvjssv"));
    }

}
