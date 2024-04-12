package server;

import chess.ChessGame;
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
            case MAKE_MOVE -> System.out.println("make move not implemented");
            case LEAVE -> System.out.println("leave not implemented");
            case RESIGN -> System.out.println("resign not implemented");
        }
    }

    public void joinPlayer(Session session, String message) throws IOException {
        JoinPlayer command = new Gson().fromJson(message, JoinPlayer.class);
        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            String authToken = command.getAuthString();
            AuthData authData = authDAO.getAuth(authToken);

            if (gameData == null) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR, "Error: bad game ID");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }
            if (authData == null){
                Error error = new Error(ServerMessage.ServerMessageType.ERROR, "Error: authToken");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }

            if (command.getPlayerColor() == ChessGame.TeamColor.WHITE) {
                if (gameData.whiteUsername() == null || !gameData.whiteUsername().equals(authData.username())) {
                    Error error = new Error(ServerMessage.ServerMessageType.ERROR,"Error: Spot not available");
                    session.getRemote().sendString(new Gson().toJson(error));
                    return;
                }
            } else if (command.getPlayerColor() == ChessGame.TeamColor.BLACK) {
                if (gameData.blackUsername() == null || !gameData.blackUsername().equals(authData.username())) {
                    Error error = new Error(ServerMessage.ServerMessageType.ERROR,"Error: Spot not available");
                    session.getRemote().sendString(new Gson().toJson(error));
                    return;
                }
            }

            sessions.addSessionToGame(command.getGameID(), authToken, session);
            LoadGame loadGameMessage = new LoadGame(gameData.game());
            this.sendMessage(command.getGameID(), loadGameMessage, authToken);
            Notification newNotification = new Notification(String.format(authData.username() + " joined as " + command.getPlayerColor()));
            this.broadcastMessage(command.getGameID(), newNotification, authToken);

        } catch (DataAccessException | IOException e) {
            session.getRemote().sendString(new Gson().toJson(new Error(ServerMessage.ServerMessageType.ERROR,"Error: " + e.getMessage())));
        }
    }

    private void joinObserver(Session session, String message) throws IOException {
        JoinObserver command = new Gson().fromJson(message, JoinObserver.class);
        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            String authToken = command.getAuthString();
            AuthData authData = authDAO.getAuth(authToken);
            if (gameData == null) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR,"Error: No game with this ID");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }
            if (authData == null){
                Error error = new Error(ServerMessage.ServerMessageType.ERROR, "Error: authToken");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }

            this.sessions.addSessionToGame(command.getGameID(), authToken, session);
            LoadGame rootMessage = new LoadGame(gameData.game());
            sendMessage(command.getGameID(), rootMessage, authToken);
            Notification newNotification = new Notification(String.format(authData.username() + " joined as an observer"));
            this.broadcastMessage(command.getGameID(), newNotification, authToken);
        }
        catch (DataAccessException e){
            session.getRemote().sendString(new Gson().toJson(new Error(ServerMessage.ServerMessageType.ERROR,"Error: " + e.getMessage())));
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

    @OnWebSocketError
    public void onError(Throwable cause) {
        return;
    }
}
