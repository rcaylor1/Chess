package ui;

import chess.*;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GamePlay {
    private final int BOARD_SIZE = 8;
    private final String[] columns = {null, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", null};
    private final String[] reverseColumns = {null, " h ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", null};
    private final String[] rows = {null, " 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6", " 7 ", " 8", " 9 ", null};
    private final String[] reverseRows = {null, " 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 ", null};

  private final ChessBoard newBoard = new ChessBoard();

    public void printBoard(){
        newBoard.resetBoard();
        printWhiteBoard();
        System.out.println();
        printBlackBoard();
    }

    public void printWhiteBoard(){
        System.out.println(Arrays.toString(columns));
        for (int row=1; row <= BOARD_SIZE; row++){
            System.out.println(rows[row+1]);
            for (int col=1; col <= BOARD_SIZE; col++){
                ChessPiece newPiece = newBoard.getPiece(new ChessPosition(row, col));
                if ((row+col)%2==0){
                    setWhite();
                    System.out.print(convertPiece(newPiece));
                } else {
                    setBlack();
                    System.out.print(convertPiece(newPiece));
                }
                if (newPiece != null){
                    if (newPiece.getTeamColor()== ChessGame.TeamColor.WHITE){
                        System.out.print(SET_TEXT_COLOR_GREEN);
                    }
                    else {
                        System.out.print(SET_TEXT_COLOR_RED);
                    }
                } else {
                    System.out.print(" \u2003 ");
                }
            }
        }
        System.out.println(Arrays.toString(columns));
    }

    public void printBlackBoard(){
        System.out.println(Arrays.toString(reverseColumns));
        for (int row=1; row <= BOARD_SIZE; row++){
            System.out.println(reverseRows[row+1]);
            for (int col=1; col <= BOARD_SIZE; col++){
                ChessPiece newPiece = newBoard.getPiece(new ChessPosition(row, col));
                if ((row+col)%2==0){
                    setWhite();
                    System.out.print(convertPiece(newPiece));
                } else {
                    setBlack();
                    System.out.print(convertPiece(newPiece));
                }
                if (newPiece != null){
                    if (newPiece.getTeamColor()== ChessGame.TeamColor.WHITE){
                        System.out.print(SET_TEXT_COLOR_GREEN);
                    }
                    else {
                        System.out.print(SET_TEXT_COLOR_RED);
                    }
                } else {
                    System.out.print(" \u2003 ");
                }
            }
        }
        System.out.println(Arrays.toString(reverseColumns));
    }

    public String convertPiece(ChessPiece newPiece){
        if (newPiece == null){
            return " \u2003 ";
        }
        ChessPiece.PieceType piece = newPiece.getPieceType();
        String emptyString = " ";
        if (newPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
            switch(piece){
                case PAWN -> emptyString = EscapeSequences.WHITE_PAWN;
                case BISHOP -> emptyString = EscapeSequences.WHITE_BISHOP;
                case ROOK -> emptyString = EscapeSequences.WHITE_ROOK;
                case KING -> emptyString = EscapeSequences.WHITE_KING;
                case KNIGHT -> emptyString = EscapeSequences.WHITE_KNIGHT;
                case QUEEN -> emptyString = EscapeSequences.WHITE_QUEEN;
                default -> emptyString = " \u2003 ";
            }
        }
        else if (newPiece.getTeamColor() == ChessGame.TeamColor.BLACK){
            switch(piece){
                case PAWN -> emptyString = EscapeSequences.BLACK_PAWN;
                case BISHOP -> emptyString = EscapeSequences.BLACK_BISHOP;
                case ROOK -> emptyString = EscapeSequences.BLACK_ROOK;
                case KING -> emptyString = EscapeSequences.BLACK_KING;
                case KNIGHT -> emptyString = EscapeSequences.BLACK_KNIGHT;
                case QUEEN -> emptyString = EscapeSequences.BLACK_QUEEN;
                default -> emptyString = " \u2003 ";
            }
        }
        return emptyString;
    }

    private void setWhite() {
        System.out.print(SET_BG_COLOR_WHITE);
        System.out.print(SET_TEXT_COLOR_MAGENTA);
    }

    private void setBlack() {
        System.out.print(SET_BG_COLOR_BLACK);
        System.out.print(SET_BG_COLOR_YELLOW);
    }
}
