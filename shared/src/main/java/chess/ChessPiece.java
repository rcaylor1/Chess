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
            case KING -> availableMoves.addAll(kingMoves(board, row, col));
            case QUEEN -> availableMoves.addAll(queenMoves(board, row, col));
            case BISHOP -> availableMoves.addAll(bishopMoves(board, row, col));
            case KNIGHT -> availableMoves.addAll(knightMoves(board, row, col));
            case ROOK -> availableMoves.addAll(rookMoves(board, row, col));
            case PAWN -> availableMoves.addAll(pawnMoves(board, row, col));
            default -> {
            }
        }
        return availableMoves;
    }


    public Collection<ChessMove> rookMoves(ChessBoard board, int row, int col){
        Collection<ChessMove> availableRookMoves = new ArrayList<>();
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
                    availableRookMoves.add(new ChessMove(new ChessPosition(row, col), newPosition, null));
                } else if (captured.getTeamColor() != pieceColor) {
                    availableRookMoves.add(new ChessMove(new ChessPosition(row, col), newPosition, null));
                    check = false;//added to prevent infinite loop
                } else if (captured.getTeamColor() == pieceColor){
                    check = false;
                    continue;
//                    we don't want to capture our own piece so don't add
                }
//                add move to position of newRow and newCol
                newRow += i;
                newCol += j;
            }
        }
        return availableRookMoves;
    }

    public Collection<ChessMove> bishopMoves(ChessBoard board, int row, int col){
//        should be super similar to rook moves I think??
        Collection<ChessMove> availableBishopMoves = new ArrayList<>();
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
                    availableBishopMoves.add(new ChessMove(new ChessPosition(row, col), newPosition, null));
                } else if (captured.getTeamColor() != pieceColor) {
                    availableBishopMoves.add(new ChessMove(new ChessPosition(row, col), newPosition, null));
                    check = false;
                } else if (captured.getTeamColor() == pieceColor){
                    check = false;
                    continue;
                }
                newRow += i;
                newCol += j;
            }
        }
        return availableBishopMoves;
//        I was right thank goodness
    }

    public Collection<ChessMove> queenMoves(ChessBoard board, int row, int col){
        Collection<ChessMove> availableQueenMoves = new ArrayList<>();
//        has all the same moves as rook and bishop slay
        int[][] moves = {{1,1}, {1,-1}, {-1,1}, {-1,-1}, {0,1}, {1,0}, {-1,0}, {0,-1}};
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
                    availableQueenMoves.add(new ChessMove(new ChessPosition(row, col), newPosition, null));
                } else if (captured.getTeamColor() != pieceColor) {
                    availableQueenMoves.add(new ChessMove(new ChessPosition(row, col), newPosition, null));
                    check = false;
                } else if (captured.getTeamColor() == pieceColor){
                    check = false;
                    continue;
                }
                newRow += i;
                newCol += j;
            }
        }
        return availableQueenMoves;
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, int row, int col){
        Collection<ChessMove> availableKingMoves = new ArrayList<>();
//        can only move one square at a time so maybe not a while loop??
        int [][] moves = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
        boolean check = true;
        for (int [] move:moves){
            check = true;
            int i = move[0];
            int j = move[1];

            int newRow = i + row;
            int newCol = j + col;

            while (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8 && check) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece captured = board.getPiece(newPosition);
                if (captured == null){
                    availableKingMoves.add(new ChessMove(new ChessPosition(row, col), newPosition, null));
                } else if (captured.getTeamColor() != pieceColor) {
                    availableKingMoves.add(new ChessMove(new ChessPosition(row, col), newPosition, null));
                    check = false;
                } else if (captured.getTeamColor() == pieceColor){
                    check = false;
                    continue;
                }
                break; //don't want the while loop incrementing so break out of it once it runs once
            }
        }

        return availableKingMoves;
    }

    public Collection<ChessMove> knightMoves(ChessBoard board, int row, int col){
        Collection<ChessMove> availableKnightMoves = new ArrayList<>();
//        moves in an L shape, so what would that look like??
//        they can jump over pieces, so how can that be implemented??
        int [][] moves = {{2,1}, {-2,1}, {-2,-1}, {2,-1}, {1,2}, {1,-2}, {-1,-2}, {-1,2}};
        boolean check = true;
        for (int [] move:moves){
            check = true;
            int i = move[0];
            int j = move[1];

            int newRow = i + row;
            int newCol = j + col;

            while (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8 && check) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece captured = board.getPiece(newPosition);
                if (captured == null){
                    availableKnightMoves.add(new ChessMove(new ChessPosition(row, col), newPosition, null));
                } else if (captured.getTeamColor() != pieceColor) {
                    availableKnightMoves.add(new ChessMove(new ChessPosition(row, col), newPosition, null));
                    check = false;
                } else if (captured.getTeamColor() == pieceColor){
                    check = false;
                    continue;
                }
                break;
            }
        }
        return availableKnightMoves;
    }

    public Collection<ChessMove> pawnMoves(ChessBoard board, int row, int col){
        Collection<ChessMove> availablePawnMoves = new ArrayList<>();
//        pawns can only move up 1, but up 2 on first turn. If statements maybe?? Also, they can only capture diagonally
//        color matters on this one because they move different directions
//        I think instead of a 2D array, initialize an integer and change value based on black and white since the directions are different for the two colors
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece newPiece = board.getPiece(position);
        int direction = 0;
        int initialDirection = 0;
        if (newPiece.getTeamColor() == ChessGame.TeamColor.BLACK){
            direction = -1;
            initialDirection = -2;
//            black pawn moves down
        } else if (newPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
            direction = 1;
            initialDirection = 2;
//            white pawn moves up;
        }
//        ok so the pawn can only move two on the first turn, so make another int variable
//        promotion piece will be null because we're not dealing with promotions right now
//        white pawns start on row 2 and black pawns start on row 7
        int initialNewRow = position.getRow()+initialDirection;
        int initialBlockedRow = position.getRow()+initialDirection+1;
        ChessPosition initialBlockedPosition = new ChessPosition(initialBlockedRow, col);
        ChessPiece initialBlockedAgain = board.getPiece(initialBlockedPosition);
        ChessPosition initialNewPosition = new ChessPosition(initialNewRow, col);
        ChessPiece initialBlocked = board.getPiece(initialNewPosition);
//        ChessMove initialMove = new ChessMove(position, new ChessPosition(newRow, newPosition.getColumn()),null);
        if ((position.getRow()==7 && newPiece.getTeamColor()==ChessGame.TeamColor.BLACK) || position.getRow()==2&&newPiece.getTeamColor()==ChessGame.TeamColor.WHITE){
            if (initialBlocked == null && initialBlockedAgain == null){
                availablePawnMoves.add(new ChessMove(position, initialNewPosition, null));
            }
        }

//        time to move forward one space; this is where things get complicated lol promotion starts to come into play
        int newRow = position.getRow()+direction;
        ChessPosition newPosition = new ChessPosition(newRow, col);
        ChessPiece blocked = board.getPiece(newPosition);

        if (blocked == null){
            if ((newPosition.getRow() == 1 && newPiece.getTeamColor()==ChessGame.TeamColor.BLACK) || (newPosition.getRow() == 8 && newPiece.getTeamColor()==ChessGame.TeamColor.WHITE)){
                availablePawnMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
                availablePawnMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
                availablePawnMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                availablePawnMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
//                promote the pawns to any other piece except King
            } else {
                availablePawnMoves.add(new ChessMove(position, newPosition, null));
//                this is returning the moves that aren't promoted
            }
        }
//        ok so now do captures
//        go to the left first
        int leftCaptureColumn = position.getColumn() -1;
        ChessPosition leftCapturePosition = new ChessPosition(newRow, leftCaptureColumn);
        ChessPiece capture = board.getPiece(leftCapturePosition);

        if (newRow >=1 && newRow <=8 && col >= 1 && col <= 8){
            if (capture != null && capture.getTeamColor() != pieceColor){
                if ((leftCapturePosition.getRow() == 1 && newPiece.getTeamColor()==ChessGame.TeamColor.BLACK) || (leftCapturePosition.getRow() == 8 && newPiece.getTeamColor()==ChessGame.TeamColor.WHITE)){
                    availablePawnMoves.add(new ChessMove(position, leftCapturePosition, ChessPiece.PieceType.ROOK));
                    availablePawnMoves.add(new ChessMove(position, leftCapturePosition, ChessPiece.PieceType.QUEEN));
                    availablePawnMoves.add(new ChessMove(position, leftCapturePosition, ChessPiece.PieceType.KNIGHT));
                    availablePawnMoves.add(new ChessMove(position, leftCapturePosition, ChessPiece.PieceType.BISHOP));
                } else {
                    availablePawnMoves.add(new ChessMove(position, leftCapturePosition, null));
                }
            }
        }
//        do same thing to the right

        int rightCaptureColumn = position.getColumn()+1;
        ChessPosition rightCapturePosition = new ChessPosition(newRow, rightCaptureColumn);
        ChessPiece rightCapture = board.getPiece(rightCapturePosition);

        if (newRow >=1 && newRow <=8 && col >= 1 && col <= 8){
            if (rightCapture != null && rightCapture.getTeamColor() != pieceColor){
                if ((rightCapturePosition.getRow() == 1 && newPiece.getTeamColor()==ChessGame.TeamColor.BLACK) || (rightCapturePosition.getRow() == 8 && newPiece.getTeamColor()==ChessGame.TeamColor.WHITE)){
                    availablePawnMoves.add(new ChessMove(position, rightCapturePosition, ChessPiece.PieceType.ROOK));
                    availablePawnMoves.add(new ChessMove(position, rightCapturePosition, ChessPiece.PieceType.QUEEN));
                    availablePawnMoves.add(new ChessMove(position, rightCapturePosition, ChessPiece.PieceType.KNIGHT));
                    availablePawnMoves.add(new ChessMove(position, rightCapturePosition, ChessPiece.PieceType.BISHOP));
                } else {
                    availablePawnMoves.add(new ChessMove(position, rightCapturePosition, null));
                }
            }
        }

        return availablePawnMoves;
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
