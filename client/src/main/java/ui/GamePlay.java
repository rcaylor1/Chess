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

    public void printWhiteBoard(){
        System.out.println();
        System.out.print("    ");
        for (int i=0; i<BOARD_SIZE; i++){
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.print(columns[i]);
        }
        System.out.println();
        for (int row=BOARD_SIZE; row >=1; row--){
            System.out.print(rows[row-1]+" ");
            for (int col=1; col <= BOARD_SIZE; col++){
                ChessPiece newPiece = newBoard.getPiece(new ChessPosition(row, col));
                if (newPiece != null){
                    if (newPiece.getTeamColor()== ChessGame.TeamColor.WHITE){
                        pieceColor = SET_TEXT_COLOR_PINK;
                    }
                    else {
                        pieceColor = SET_TEXT_COLOR_BLACK;
                    }
                }
                if ((row + col) % 2 == 0) {
                    setWhite(newPiece);
                } else {
                    setBlack(newPiece);
                }
            }
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE);
        }
        System.out.print("    ");
        for (int i=0; i<BOARD_SIZE; i++){
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.print(columns[i]);
        }
    }

    public void printBlackBoard(){
        System.out.print("    ");
        for (int i=0; i<BOARD_SIZE; i++){
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.print(reverseColumns[i]);
        }
        System.out.println();
        for (int row=1; row <= BOARD_SIZE; row++){
            System.out.print(rows[row-1]+" ");
            for (int col= BOARD_SIZE; col >= 1; col--){
                ChessPiece newPiece = newBoard.getPiece(new ChessPosition(row, col));
                if (newPiece != null){
                    if (newPiece.getTeamColor()== ChessGame.TeamColor.WHITE){
                        pieceColor = SET_TEXT_COLOR_PINK;
                    }
                    else {
                        pieceColor = SET_TEXT_COLOR_BLACK;
                    }
                }
                if ((row + col) % 2 == 0) {
                    setWhite(newPiece);
                } else {
                    setBlack(newPiece);
                }
            }
            System.out.print(RESET_BG_COLOR);
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.println();
        }
        System.out.print("    ");
        for (int i=0; i<BOARD_SIZE; i++){
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.print(reverseColumns[i]);
        }
        System.out.println();
    }


    public String convertPiece(ChessPiece newPiece){
        if (newPiece == null){
            return " \u2003 ";
        }
        ChessPiece.PieceType piece = newPiece.getPieceType();
        if (newPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
            switch(piece){
                case PAWN -> pieceType = EscapeSequences.WHITE_PAWN;
                case BISHOP -> pieceType = EscapeSequences.WHITE_BISHOP;
                case ROOK -> pieceType = EscapeSequences.WHITE_ROOK;
                case KING -> pieceType = EscapeSequences.WHITE_KING;
                case KNIGHT -> pieceType = EscapeSequences.WHITE_KNIGHT;
                case QUEEN -> pieceType = EscapeSequences.WHITE_QUEEN;
                default -> pieceType = " \u2003 ";
            }
        }
        else if (newPiece.getTeamColor() == ChessGame.TeamColor.BLACK){
            switch(piece){
                case PAWN -> pieceType = EscapeSequences.BLACK_PAWN;
                case BISHOP -> pieceType = EscapeSequences.BLACK_BISHOP;
                case ROOK -> pieceType = EscapeSequences.BLACK_ROOK;
                case KING -> pieceType = EscapeSequences.BLACK_KING;
                case KNIGHT -> pieceType = EscapeSequences.BLACK_KNIGHT;
                case QUEEN -> pieceType = EscapeSequences.BLACK_QUEEN;
                default -> pieceType = " \u2003 ";
            }
        }
        return pieceType;
    }

    private void setWhite(ChessPiece piece) {
        System.out.print(SET_BG_COLOR_LIGHT_PURPLE);
        System.out.print(pieceColor);
        System.out.print(convertPiece(piece));
    }

    private void setBlack(ChessPiece piece) {
        System.out.print(SET_BG_COLOR_LIGHT_BLUE);
        System.out.print(pieceColor);
        System.out.print(convertPiece(piece));
    }

    @Override
    public void updateGame(ChessGame game) {

    }
    @Override
    public void printMessage(String message){

    }
}
