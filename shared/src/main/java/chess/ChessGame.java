package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;


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
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null){
            return null; //this is if there is no piece at the start position
        } else {
            Collection<ChessMove> availableMoves = new ArrayList<>();
            Collection<ChessMove> pieceMoves = piece.pieceMoves(board, startPosition); //call pieceMoves from previous phase to access available moves

            for (ChessMove pieceMove : pieceMoves){
                ChessPiece move = board.getPiece(pieceMove.getEndPosition());
                board.clonedMove(pieceMove);
                if (!isInCheck(piece.getTeamColor())){
                    availableMoves.add(pieceMove);
                }
                board.clonedMove(pieceMove.undo());
                board.addPiece(pieceMove.getEndPosition(), move);
            }
            return availableMoves;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException{
//        this method should change the team turn
        ChessPiece piece = board.getPiece(move.getStartPosition()); //get the piece at the starting position
//        A move is illegal if the chess piece cannot move there, if the move leaves the team’s king in danger, or if it’s not the corresponding team's turn.
//        check if valid move first
        if (!validMoves(move.getStartPosition()).contains(move)){
            throw new InvalidMoveException("hey");
        }
        if (piece == null){
            throw new InvalidMoveException(); //can't move a piece if there isn't a piece there in the first place lol
        }
        if (piece.getTeamColor() != currentTeam){
            throw new InvalidMoveException(); //has to be the right team's turn
        }
        board.addPiece(move.getEndPosition(), piece); //move the piece to end position
        board.addPiece(move.getStartPosition(), null); //clear starting position of that piece
//have to account for promotions too
        if (move.getPromotionPiece() != null){
            board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
//change the team color
        if (currentTeam == TeamColor.WHITE){
            currentTeam = TeamColor.BLACK;
        } else if (currentTeam == TeamColor.BLACK){
            currentTeam = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingSpot = kingPosition(teamColor, board);
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                ChessPosition newPosition = new ChessPosition(i,j);
                ChessPiece newPiece = this.board.getPiece(newPosition);
                if (newPiece != null && newPiece.getTeamColor() != teamColor){
                    Collection<ChessMove> moves = newPiece.pieceMoves(board, newPosition);
                    for (ChessMove move:moves){
                        if (move.getEndPosition().equals(kingSpot)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

//    make method for king position to make other methods easier
public ChessPosition kingPosition (TeamColor color, ChessBoard board){
    for (int i = 1; i < 9; i++){
        for (int j = 1; j < 9; j++){
            ChessPosition kingPosition = new ChessPosition(i,j);
            ChessPiece piece = board.getPiece(kingPosition);
            if (piece != null && piece.getPieceType().equals(ChessPiece.PieceType.KING) && piece.getTeamColor() == color){
                return kingPosition;
            }
        }
    }
    return null;
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
            for (int i=1; i<9; i++){
                for (int j=1; j<9; j++){
                    ChessPosition newPosition = new ChessPosition(i,j);
                    ChessPiece piece = board.getPiece(newPosition);
                    if (piece != null && piece.getTeamColor().equals(teamColor) && validMoves(newPosition)!=null){
                        return true;
                    }
                }
            }
        }
        return false;
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
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                ChessPosition newPosition = new ChessPosition(i,j);
                ChessPiece newPiece = board.getPiece(newPosition);
                if (newPiece != null && newPiece.getTeamColor().equals(teamColor) && !validMoves(newPosition).isEmpty()){
                    return false;
                }
            }
        }
        return true;
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
}