package dataAccessTests;

import chess.ChessGame;
import dataAccess.*;
import dataAccess.Exceptions.DataAccessException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;

public class GameDAOTests {
    @BeforeEach
    void clearTables() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.clear();
    }

    @Test
    void clear() throws DataAccessException{
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.createGame(new GameData(3, "white", "black", "game3", new ChessGame()));
        gameDAO.clear();
        Assertions.assertNull(gameDAO.getGame(3));
    }

    @Test
    void createGamePositive() throws DataAccessException{
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.createGame(new GameData(3, "white", "black", "game3", new ChessGame()));
        Assertions.assertNotNull(gameDAO.getGame(3));
    }

    @Test
    void createGameNegative() throws DataAccessException{
        SQLGameDAO gameDAO = new SQLGameDAO();
        GameData newGame = new GameData(3, "white", "black", "game3", new ChessGame());
        gameDAO.createGame(newGame);
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(newGame));
    }

    @Test
    void getGamePositive() throws DataAccessException{
        SQLGameDAO gameDAO = new SQLGameDAO();
        GameData newGame = new GameData(3, "white", "black", "game3", new ChessGame());
        gameDAO.createGame(newGame);
        Assertions.assertNotNull(gameDAO.getGame(3));
    }

    @Test
    void getGameNegative() throws DataAccessException{
        SQLGameDAO gameDAO = new SQLGameDAO();
        GameData newGame = new GameData(3, "white", "black", "game3", new ChessGame());
        gameDAO.createGame(newGame);
        Assertions.assertNull(gameDAO.getGame(4));
    }

    @Test
    void listGamesPositive() throws DataAccessException{
        SQLGameDAO gameDAO = new SQLGameDAO();
        ArrayList<GameData> games = new ArrayList<GameData>();
        GameData newGame = new GameData(3, "white", "black", "game3", new ChessGame());
        gameDAO.createGame(newGame);
        games.add(newGame);
        ArrayList<GameData> listedGames = gameDAO.listGames();
        Assertions.assertNotNull(listedGames);
    }

    @Test
    void listGamesNegative() throws DataAccessException{
        SQLGameDAO gameDAO = new SQLGameDAO();
        ArrayList<GameData> games = new ArrayList<GameData>();
        GameData newGame = new GameData(3, "white", "black", "game3", new ChessGame());
        gameDAO.createGame(newGame);
        games.add(newGame);
        ArrayList<GameData> listedGames = gameDAO.listGames();
        Assertions.assertNotEquals(3, listedGames.size());
    }

    @Test
    void updateGamePositive() throws DataAccessException{
        SQLGameDAO gameDAO = new SQLGameDAO();
        GameData newGame = new GameData(3, "white", "black", "game3", new ChessGame());
        gameDAO.createGame(newGame);
        gameDAO.updateGame(newGame);
        Assertions.assertEquals(newGame.whiteUsername(), "white");
    }

    @Test
    void updateGameNegative() throws DataAccessException{
        SQLGameDAO gameDAO = new SQLGameDAO();
        GameData newGame = new GameData(3, "white", "black", "game3", new ChessGame());
        gameDAO.createGame(newGame);
        gameDAO.updateGame(new GameData(3, "whiteUser", "black", "game3", new ChessGame()));
        Assertions.assertNotEquals(newGame.whiteUsername(), "whiteUser");
    }

    @Test
    void incIDPositive() throws DataAccessException{
        SQLGameDAO gameDAO = new SQLGameDAO();
        int newID = 2;
        int newInc = gameDAO.incID();
        Assertions.assertEquals(2, newID);
    }

    @Test
    void incIDNegative() throws DataAccessException{
        SQLGameDAO gameDAO = new SQLGameDAO();
        int newID = 3;
        int newInc = gameDAO.incID();
        Assertions.assertNotEquals(2, newID);
    }
}
