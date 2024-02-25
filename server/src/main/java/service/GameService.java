package service;

import dataAccess.*;
import model.*;
import chess.ChessGame;

import java.util.ArrayList;
public class GameService {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    public GameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ArrayList<GameData> listGames(String authToken) throws DataAccessException {
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
}
