package clientTests;

import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ResponseException;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(String.format("http://localhost:%d", port));
    }

    @BeforeEach
    void clear() throws Exception {
        UserDAO userDAO = new SQLUserDAO();
        GameDAO gameDAO = new SQLGameDAO();
        AuthDAO authDAO = new SQLAuthDAO();
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void registerPositive() throws ResponseException {
        UserData newUser = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(newUser);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerNegative() throws ResponseException {
        UserData user1 = new UserData("player1", "password", "p1@email.com");
        UserData user2 = new UserData("player1", "notevenclose", "tobeingdone");
        var authData = facade.register(user1);
        Assertions.assertThrows(ResponseException.class, ()-> facade.register(user2));
    }

    @Test
    void loginPositive() throws ResponseException {
        UserData newUser = new UserData("player1", "password", "p1@email.com");
        facade.register(newUser);
        var authData = facade.login(newUser);
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void loginNegative() throws ResponseException {
        UserData user1 = new UserData("player1", "password", "p1@email.com");
        Assertions.assertThrows(ResponseException.class, ()-> facade.login(user1));
    }

    @Test
    void logoutPositive() throws ResponseException {
        UserData newUser = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(newUser);
        facade.login(newUser);
        facade.logout(authData.authToken());
        Assertions.assertThrows(ResponseException.class, () -> facade.logout(authData.authToken()));
    }

    @Test
    void logoutNegative() throws ResponseException {
        UserData newUser = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(newUser);
        facade.login(newUser);
        Assertions.assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    void createGamePositive() throws ResponseException {
        UserData newUser = new UserData("player1", "password", "p1@email.com");
        GameData newGame = new GameData(0, "white", "white", "NewGame", new ChessGame());
        AuthData newAuth = facade.register(newUser);
        GameData gameData = facade.createGame(newGame, newAuth.authToken());
        Assertions.assertNotEquals(gameData.gameID(), 0);
    }

    @Test
    void createGameNegative() throws ResponseException {
        UserData newUser = new UserData("player1", "password", "p1@email.com");
        GameData newGame = new GameData(0, "white", "white", "NewGame", new ChessGame());
        var authData = facade.register(newUser);
        Assertions.assertDoesNotThrow(() -> facade.createGame(newGame, authData.authToken()));
    }

    @Test
    void listGamePositive() throws ResponseException {
        UserData newUser = new UserData("player1", "password", "p1@email.com");
        var newAuth = facade.register(newUser);
        GameData game1 = new GameData(1, "white", "black", "Game1", new ChessGame());
        GameData game2 = new GameData(2, "WHITE", "BLACK", "Game2", new ChessGame());
        GameData newGame1 = facade.createGame(game1, newAuth.authToken());
        GameData newGame2 = facade.createGame(game2, newAuth.authToken());
        Assertions.assertNotNull(facade.listGames(newAuth.authToken()));
    }

    @Test
    void listGameNegative() throws ResponseException {
        GameData game1 = new GameData(1, "white", "black", "Game1", new ChessGame());
        Assertions.assertThrows(ResponseException.class,() -> facade.listGames("tired"));
    }

    @Test
    void joinGamePositive() throws ResponseException {
        UserData newUser = new UserData("player1", "password", "p1@email.com");
        var newAuth = facade.register(newUser);
        GameData game1 = new GameData(1, "white", "black", "Game1", new ChessGame());
        GameData newGame1 = facade.createGame(game1, newAuth.authToken());
        Assertions.assertDoesNotThrow(() -> facade.joinGame(new JoinGameRequest("WHITE", newGame1.gameID()), newAuth.authToken()));
    }

    @Test
    void joinGameNegative() throws ResponseException {
        UserData newUser = new UserData("player1", "password", "p1@email.com");
        var newAuth = facade.register(newUser);
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(new JoinGameRequest("WHITE", 1), newAuth.authToken()));
    }
}
