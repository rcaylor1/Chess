package serviceTests;

import chess.ChessGame;
import dataAccess.*;
import dataAccess.Exceptions.DataAccessException;
import model.*;
import service.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.util.ArrayList;

public class ListGamesTests {
    @Test
    void listGamesPositive() throws DataAccessException {
        GameDAO gameDao = new MemoryGameDAO();
        AuthDAO authDao = new MemoryAuthDAO();
        GameService service  = new GameService(gameDao, authDao);

        GameData game1 = new GameData(7, "whiteUser", "blackUser", "losers", new ChessGame());
        GameData game2 = new GameData(14, "winner", "loser", "tiebreaker", new ChessGame());
        authDao.createAuth(new AuthData("authToken", "rcaylor"));
        gameDao.createGame(game1);
        gameDao.createGame(game2);
        ArrayList <GameData> expected = new ArrayList<GameData>();
        expected.add(game1);
        expected.add(game2);

        Assertions.assertEquals(expected, service.listGames("authToken"));
    }

    @Test
    void listGamesNegative() throws DataAccessException{
        GameDAO gameDao = new MemoryGameDAO();
        AuthDAO authDao = new MemoryAuthDAO();
        GameService service  = new GameService(gameDao, authDao);

        GameData game1 = new GameData(7, "whiteUser", "blackUser", "losers", new ChessGame());
        GameData game2 = new GameData(14, "winner", "loser", "tiebreaker", new ChessGame());
        authDao.createAuth(new AuthData("authToken", "rcaylor"));

        gameDao.createGame(game1);
        gameDao.createGame(game2);
        ArrayList <GameData> expected = new ArrayList<GameData>();
        expected.add(game1);

        Assertions.assertNotEquals(expected, service.listGames("authToken"));
    }
}
