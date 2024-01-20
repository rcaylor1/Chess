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
        Collection<ChessMove> availableMoves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        switch (getPieceType()) {
            case KING -> System.out.println("king");
            case QUEEN -> System.out.println("queen");
            case BISHOP -> availableMoves.addAll(bishopMoves(board, row, col));
            case KNIGHT -> System.out.println("knight");
            case ROOK -> availableMoves.addAll(rookMoves(board, row, col));
            case PAWN -> System.out.println("pawn");
            default -> {
            }
        }
        return availableMoves;
    }


    public Collection<ChessMove> rookMoves(ChessBoard board, int row, int col){
        Collection<ChessMove> availableMoves = new ArrayList<>();
//        store all possible moves
        int[][] moves = {{1,0},{0,1},{-1,0},{0,-1}};
        boolean check=true;
        for (int[] move: moves) {
            check = true;//gives another parameter to while loop and prevents infinite loop
            int i = move[0];
            int j = move[1];
// create the move by adding column or row to the new row
            int newRow = i + row;
            int newCol = j + col;
//            System.out.println(newCol);
            while (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8 && check) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
//                System.out.println(newPosition);
                ChessPiece captured = board.getPiece(newPosition);
//looking to see if there's a piece in the space already and which team it's on
                if (captured == null){
                    availableMoves.add(new ChessMove(new ChessPosition(row, col), newPosition, null));
                } else if (captured.getTeamColor() != pieceColor) {
                    availableMoves.add(new ChessMove(new ChessPosition(row, col), newPosition, null));
                    check = false;//added to prevent infinite loop
                } else if (captured.getTeamColor() == pieceColor){
                    check = false;
                    continue;
//                    we don't want to capture our own piece so don't add
                }
//                add move to position of newRow and newCol
                newRow += i;
                newCol += j;
//                System.out.println(newPosition);
            }
        }
        return availableMoves;
    }

    public Collection<ChessMove> bishopMoves(ChessBoard board, int row, int col){
//        should be super similar to rook moves I think??
        Collection<ChessMove> availableMoves = new ArrayList<>();
        int[][] moves = {{1,1}, {1,-1}, {-1,1}, {-1,-1}}; //similar to rook but goes diagonal
        boolean check = true;
        for (int[] move: moves){
            check = true;
            int i = move[0];
            int j = move[1];

            int newRow = i + row;
            int newCol = j + col;

            while (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8 && check) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece captured = board.getPiece(newPosition);
                if (captured == null){
                    availableMoves.add(new ChessMove(new ChessPosition(row, col), newPosition, null));
                } else if (captured.getTeamColor() != pieceColor) {
                    availableMoves.add(new ChessMove(new ChessPosition(row, col), newPosition, null));
                    check = false;
                } else if (captured.getTeamColor() == pieceColor){
                    check = false;
                    continue;
                }
                newRow += i;
                newCol += j;
            }
        }
        return availableMoves;
//        i was right thank goodness
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
