package service;

import dataAccess.*;
import dataAccess.Exceptions.*;
import model.*;
import chess.ChessGame;

import java.util.ArrayList;
public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    public GameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ArrayList<GameData> listGames(String authToken) throws DataAccessException {
        if (authDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        authDAO.getAuth(authToken);
        return gameDAO.listGames();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        if (authDAO.getAuth(authToken) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        int gameID = gameDAO.incID(); //get next game ID
        gameDAO.createGame(new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }

    public void joinGame(String authToken, String color, int gameID) throws DataAccessException {
        GameData joinedGame = gameDAO.getGame(gameID);
        if (authDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: bad request");
        }
        if (gameDAO.getGame(gameID) == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (color != null) {
            if (color.equals("WHITE")){
                if (joinedGame.whiteUsername() != null){
                    throw new AlreadyTakenException("Error: already taken");
                }
                gameDAO.updateGame(new GameData(joinedGame.gameID(), authDAO.getAuth(authToken).username(), joinedGame.blackUsername(), joinedGame.gameName(), joinedGame.game()));
            } else if (color.equals("BLACK")){
                if (joinedGame.blackUsername() != null){
                    throw new AlreadyTakenException("Error: already taken");
                }
                gameDAO.updateGame(new GameData(joinedGame.gameID(), joinedGame.whiteUsername(), authDAO.getAuth(authToken).username(), joinedGame.gameName(), joinedGame.game()));
            }
        }
    }
}
