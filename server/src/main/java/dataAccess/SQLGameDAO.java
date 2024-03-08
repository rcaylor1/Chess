package dataAccess;

import dataAccess.Exceptions.*;
import model.GameData;
import chess.ChessGame;
import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO{
    public SQLGameDAO() throws DataAccessException{
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException{
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    @Override
    public void createGame(GameData newGame) throws DataAccessException{
        String statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        String gameJson = new Gson().toJson(newGame.game());
        executeUpdate(statement, newGame.gameID(), null, null, newGame.gameName(), new Gson().toJson(newGame.game()));
//        executeUpdate(statement, newGame.gameID(), newGame);
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException{
        var games = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game";
            var ps = conn.prepareStatement(statement);
            var rs = ps.executeQuery();
            while (rs.next()) {
                games.add(readGame(rs));
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return games;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException{
        var statement = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
        executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), new Gson().toJson(game.game()), game.gameID());
    }

    public int newID = 0;
    @Override
    public int incID(){
        return newID++;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(100),
              `blackUsername` varchar(100),
              `gameName` varchar(100) NOT NULL,
              `game` text NOT NULL,
              PRIMARY KEY (`gameID`),
              INDEX (gameName)
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof GameData p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var jsonGame = rs.getString("game");
        var game = new Gson().fromJson(jsonGame, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }
}
