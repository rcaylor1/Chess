package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Iterator;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor currentTeam;
    private ChessBoard board = new ChessBoard();
    public ChessGame() {
        board.resetBoard(); //start with an empty board
        currentTeam = TeamColor.WHITE; //first team to start is always white
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = this.board.getPiece(startPosition);
        if (piece == null){
            return null; //this is if there is no piece at the start position
        }
        Collection<ChessMove> availableMoves = new ArrayList<>();
        Collection<ChessMove> pieceMoves = piece.pieceMoves(board, startPosition); //call pieceMoves from previous phase to access available moves

        ChessBoard copyBoard;
        for (ChessMove pieceMove : pieceMoves){
            copyBoard = board.copy();

            availableMoves.add(pieceMove);
        }
//

//        next step is to find valid moves
//        I want to copy the board and see if I can make the move

        return availableMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException{
        throw new RuntimeException("Not implemented");
//        this method should change the team turn
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return true;
//        finish
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
//        king has to be in check, and there can't be any valid moves
        if (isInCheck(teamColor)){
//            if (no valid moves)
            return true; //finish this if statement
        } else {
            return false;
        }
//        finish
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
//        this is when there are absolutely no more valid moves that either team can make
        if (isInCheck(teamColor)){
            return false; //king cannot be in check for a stalemate to happen
        } else {
            return true;
        }
    }

//    make method for king position to make other methods easier
    public ChessPosition kingPosition (TeamColor color, ChessBoard board){
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                ChessPosition kingPosition = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(kingPosition);
                if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == color){
                    return kingPosition;
                }
            }
        }
        return null;
    }


    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return currentTeam == chessGame.currentTeam && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeam, board);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "currentTeam=" + currentTeam +
                ", board=" + board +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
