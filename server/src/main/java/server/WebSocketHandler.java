package server;

import chess.*;
import com.google.gson.Gson;
import dataAccess.*;
import dataAccess.Exceptions.DataAccessException;
import model.AuthData;

import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;
import java.io.IOException;
import java.util.*;

@WebSocket
public class WebSocketHandler {
    private WebSocketSessions sessions;
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    public WebSocketHandler() {
        try {
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
            userDAO = new SQLUserDAO();
            sessions = new WebSocketSessions();
        }
        catch (DataAccessException e){
            throw new RuntimeException(e);
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(session, message);
            case JOIN_OBSERVER -> joinObserver(session, message);
            case MAKE_MOVE -> makeMove(session, message);
            case LEAVE -> leave(session, message);
            case RESIGN -> resign(session, message);
        }
    }

    public void joinPlayer(Session session, String message) throws IOException {
        JoinPlayer command = new Gson().fromJson(message, JoinPlayer.class);
        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            String authToken = command.getAuthString();
            AuthData authData = authDAO.getAuth(authToken);

            if (gameData == null) {
                Error error = new Error("Error: bad game ID");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }
            if (authData == null){
                Error error = new Error("Error: authToken");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }

            if (command.getPlayerColor() == ChessGame.TeamColor.WHITE) {
                if (gameData.whiteUsername() == null || !gameData.whiteUsername().equals(authData.username())) {
                    Error error = new Error("Error: Spot not available");
                    session.getRemote().sendString(new Gson().toJson(error));
                    return;
                }
            } else if (command.getPlayerColor() == ChessGame.TeamColor.BLACK) {
                if (gameData.blackUsername() == null || !gameData.blackUsername().equals(authData.username())) {
                    Error error = new Error("Error: Spot not available");
                    session.getRemote().sendString(new Gson().toJson(error));
                    return;
                }
            }

            sessions.addSessionToGame(command.getGameID(), authToken, session);
            LoadGame loadGameMessage = new LoadGame(gameData.game());
            sendMessage(command.getGameID(), loadGameMessage, authToken);
            Notification newNotification = new Notification(String.format(authData.username() + " joined as " + command.getPlayerColor()));
            broadcastMessage(command.getGameID(), newNotification, authToken);

        } catch (DataAccessException | IOException e) {
            Error error = new Error("Error: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private void joinObserver(Session session, String message) throws IOException {
        JoinObserver command = new Gson().fromJson(message, JoinObserver.class);
        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            String authToken = command.getAuthString();
            AuthData authData = authDAO.getAuth(authToken);
            if (gameData == null) {
                Error error = new Error("Error: No game with this ID");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }
            if (authData == null){
                Error error = new Error("Error: authToken");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }

            this.sessions.addSessionToGame(command.getGameID(), authToken, session);
            LoadGame rootMessage = new LoadGame(gameData.game());
            sendMessage(command.getGameID(), rootMessage, authToken);
            Notification newNotification = new Notification(String.format(authData.username() + " joined as an observer"));
            broadcastMessage(command.getGameID(), newNotification, authToken);
        }
        catch (DataAccessException e){
            Error error = new Error("Error: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    public void leave(Session session, String message) throws IOException {
        Leave command = new Gson().fromJson(message, Leave.class);
        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            String authToken = command.getAuthString();
            AuthData authData = authDAO.getAuth(authToken);
            if (gameData == null) {
                Error error = new Error("Error: No game with this ID");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }
            if (authData == null){
                Error error = new Error("Error: invalid authToken");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }
            ChessGame game = gameData.game();
            if (game.getTeamTurn() == ChessGame.TeamColor.WHITE){
                gameDAO.updateGame(new GameData(command.getGameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game()));
            } else if (game.getTeamTurn() == ChessGame.TeamColor.BLACK){
                gameDAO.updateGame(new GameData(command.getGameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game()));
            }

            Notification newNotification = new Notification(String.format(authData.username() + " left the game "));
            broadcastMessage(command.getGameID(), newNotification, authToken);
            sessions.removeSessionFromGame(command.getGameID(), authToken);
        }
        catch (DataAccessException e){
            Error error = new Error("Error: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    public void resign(Session session, String message) throws IOException {
        Resign command = new Gson().fromJson(message, Resign.class);
        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            ChessGame game = gameData.game();

            String authToken = command.getAuthString();
            AuthData authData = authDAO.getAuth(authToken);


            if ((gameData.whiteUsername() == null || !gameData.whiteUsername().equals(authData.username()))){
                if ((gameData.blackUsername() == null || !gameData.blackUsername().equals(authData.username()))) {
                    Error error = new Error("Error: Observers cannot resign");
                    session.getRemote().sendString(new Gson().toJson(error));
                    return;
                }
            }

            if (game.getTeamTurn() == ChessGame.TeamColor.GAME_DONE){
                Error error = new Error("Error: cannot resign");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }

            game.setTeamTurn(ChessGame.TeamColor.GAME_DONE);
            gameDAO.updateGame(new GameData(command.getGameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
            Notification newNotification = new Notification(String.format(authData.username() + " resigned"));
            sendMessage(command.getGameID(), newNotification, authToken);
            broadcastMessage(command.getGameID(), newNotification, authToken);

        }
        catch (DataAccessException e){
            Error error = new Error("Error: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    public void makeMove(Session session, String message) throws IOException {
        MakeMove command = new Gson().fromJson(message, MakeMove.class);
        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            String authToken = command.getAuthString();
            AuthData authData = authDAO.getAuth(authToken);
            ChessMove move = command.getMove();
            ChessGame.TeamColor teamColor = null;

            if (gameData == null) {
                Error error = new Error("Error: No game with this ID");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }
            if (authData == null){
                Error error = new Error("Error: invalid authToken");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }

            ChessGame game = gameData.game();
            ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());
            if (gameData.whiteUsername().equals(authData.username())) {
                teamColor = ChessGame.TeamColor.WHITE;
            } else if (gameData.blackUsername().equals(authData.username())) {
                teamColor = ChessGame.TeamColor.BLACK;
            }
            if (game.getTeamTurn() == ChessGame.TeamColor.GAME_DONE){
                Error error = new Error("Error: Game is over. No more moves can be made");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }
            if (piece.getTeamColor() != teamColor){
                Error error = new Error("Error: Not your team's piece");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }

            game.makeMove(move);
            gameDAO.updateGame(new GameData(command.getGameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));

            LoadGame loadGameMessage = new LoadGame(game);
            Notification notification = new Notification(authData.username() + " moved to " + command.getMove().getEndPosition());
            sendMessage(command.getGameID(), loadGameMessage, authToken);
            broadcastMessage(command.getGameID(), loadGameMessage, authToken);
            broadcastMessage(command.getGameID(), notification, authToken);
        }
        catch (DataAccessException | InvalidMoveException e){
            Error error = new Error("Error: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    public void broadcastMessage(int gameID, ServerMessage message, String exceptThisAuthToken) throws IOException{
        Map<String, Session> sessionsList = sessions.getSessionsForGame(gameID);
        if (sessionsList != null) {
            for (HashMap.Entry<String, Session> entry : sessionsList.entrySet()) {
                String authToken = entry.getKey();
                if (!authToken.equals(exceptThisAuthToken)) {
                    Session newSession = entry.getValue();
                    if (newSession.isOpen()) {
                        newSession.getRemote().sendString(new Gson().toJson(message));
                    }
                }
            }
        }
    }


    private void sendMessage(int gameID, ServerMessage serverMessage, String authToken) throws IOException {
        Map<String, Session> game = sessions.getSessionsForGame(gameID);
        Session session = game.get(authToken);
        session.getRemote().sendString(new Gson().toJson(serverMessage));
    }

}

