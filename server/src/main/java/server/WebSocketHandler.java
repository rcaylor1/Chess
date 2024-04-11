package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.*;
import dataAccess.Exceptions.DataAccessException;
import model.AuthData;
import model.ErrorMessage;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(session, message);
            case JOIN_OBSERVER -> System.out.println("join observer not implemented");
            case MAKE_MOVE -> System.out.println("make move not implemented");
            case LEAVE -> System.out.println("leave not implemented");
            case RESIGN -> System.out.println("resign not implemented");
        }
    }

    public void joinPlayer(Session session, String message) throws IOException {
        JoinPlayer command = new Gson().fromJson(message, JoinPlayer.class);
        try {
            GameData gameData = gameDAO.getGame(command.getGameID());

            if (gameData == null) {
                ErrorMessage error = new ErrorMessage("Error: Game does not exist");
                session.getRemote().sendString(new Gson().toJson(error));
            }

            String authToken = command.getAuthString();
            AuthData authData = authDAO.getAuth(authToken);
            String username = authData.username();


            if (command.getPlayerColor() == ChessGame.TeamColor.WHITE) {
                if (!gameData.whiteUsername().equals(username)) {
                    ErrorMessage error = new ErrorMessage("Error: Spot not available");
                    session.getRemote().sendString(new Gson().toJson(error));
                }
            } else if (command.getPlayerColor() == ChessGame.TeamColor.BLACK) {
                if (!Objects.equals(gameData.blackUsername(), username)) {
                    ErrorMessage error = new ErrorMessage("Error: Spot not available");
                    session.getRemote().sendString(new Gson().toJson(error));
                }
            }

            sessions.addSessionToGame(command.getGameID(), authToken, session);
            LoadGame loadGameMessage = new LoadGame(gameData.game());
            this.sendMessage(command.getGameID(), loadGameMessage, authToken);
            Notification newNotification = new Notification(String.format(username + " joined as " + command.getPlayerColor()));
            this.broadcastMessage(command.getGameID(), newNotification, authToken);

        } catch (DataAccessException e) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
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
        HashMap<String, Session> game = sessions.getSessionsForGame(gameID);
        Session session = game.get(authToken);
        session.getRemote().sendString(new Gson().toJson(serverMessage));
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {}

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
    }

    @OnWebSocketError
    public void onError(Throwable cause) {
        return;
    }
}
