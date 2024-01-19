package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
//        return new ArrayList<>();
        Collection<ChessMove> availableMoves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        switch (getPieceType()) {
            case KING -> System.out.println("king");
            case QUEEN -> System.out.println("queen");
            case BISHOP -> System.out.println("bishop");
            case KNIGHT -> System.out.println("knight");
            case ROOK -> System.out.println("rook");
            case PAWN -> System.out.println("pawn");
            default -> {
            }
        }
        return availableMoves;
//        availableMoves.addAll(function(position, row, column))??
    }

//    make a for each loop?? and a while loop for sure for each of the moves. Make sure to increment each of the moves by i or j at the end of the while loop
//    for (int[] direction: directions){
//        int i = direction[0]
//        int j = direction[1]
//    }
//    while (position?? >=1, row <=8, col <=8)
    public Collection<ChessMove> rookMoves(ChessPosition myPosition, int row, int col){
//        rook can move up, down, and sideways
//        write code that takes in position and shows how it can move up, down, and sideways to reach end position
//        need to remember other pieces
        Collection<ChessMove> availableRookMoves = new ArrayList<>();

    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
