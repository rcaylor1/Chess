package ui;

import chess.*;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.LoadGame;
import websocket.GameHandler;
import websocket.WebSocketFacade;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GamePlay implements GameHandler {
    private static final int BOARD_SIZE = 8;
    private final String[] columns = {" a  ", " b  ", " c  ", "d  ", " e  ", " f ", "  g ", " h "};
    private final String[] reverseColumns = {" h  ", " g  ", " f  ", "e  ", " d  ", " c ", "  b ", " a "};
    private final String[] rows = {" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 ", " 9 "};
    private String pieceColor = "";
    private String pieceType = "";
    private final ChessBoard newBoard = new ChessBoard();
    ChessGame.TeamColor teamColor;
    int gameID;
    static String authToken;
    WebSocketFacade socket = new WebSocketFacade("http://localhost:8080", GamePlay.this);

    public GamePlay(ChessGame.TeamColor teamColor, String authToken, int gameID) throws ResponseException {
        this.teamColor = teamColor;
        GamePlay.authToken = authToken;
        this.gameID = gameID;
    }


    public void printBoard(){
        try{
//            newBoard.resetBoard();
            if (teamColor != null) {
                socket.joinPlayer(authToken, gameID, teamColor);
            } else {
                socket.joinObserver(authToken, gameID);
            }
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }






    @Override
    public void updateGame(ChessGame game) {

    }
    @Override
    public void printMessage(String message){

    }
}
