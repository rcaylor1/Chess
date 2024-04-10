package websocket;

import javax.websocket.*;

import chess.ChessGame;
import chess.ChessMove;
import ui.ResponseException;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint{
    private GameHandler gameHandler;
    private Session session;

    public WebSocketFacade(String url, GameHandler gameHandler) throws ResponseException {
        try{
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.gameHandler = gameHandler;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>(){
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()){
                        case ERROR: {
                            Error errorMessage = new Gson().fromJson(message, Error.class);
                            gameHandler.printMessage(errorMessage.getMessage());
                        }
                        case NOTIFICATION: {
                            Notification notificationMessage = new Gson().fromJson(message, Notification.class);
                            gameHandler.printMessage(notificationMessage.getMessage());
                        }
                        case LOAD_GAME: {
                            LoadGame loadGameMessage = new Gson().fromJson(message, LoadGame.class);
                            gameHandler.updateGame(loadGameMessage.getGame());
                        }
                        default:
                            break;
                    }
                }
            });
        }
        catch (URISyntaxException | DeploymentException | IOException e){
            throw new ResponseException(e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    private void sendMessage(UserGameCommand command) throws IOException{
        session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void joinPlayer(String authToken, int gameID, ChessGame.TeamColor team) throws IOException {
        this.sendMessage(new JoinPlayer(authToken, gameID, team));
    }

    public void joinObserver(String authToken, int gameID) throws IOException {
        this.sendMessage(new JoinObserver(authToken, gameID));
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws IOException {
        this.sendMessage(new MakeMove(authToken, gameID, move));
    }

    public void leaveGame(String authToken, int gameID) throws IOException {
        this.sendMessage(new Leave(authToken, gameID));
    }

    public void resignGame(String authToken, int gameID) throws IOException {
        this.sendMessage(new Resign(authToken, gameID));
    }
}
