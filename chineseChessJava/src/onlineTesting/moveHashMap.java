package onlineTesting;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import game.Board;
import game.pieces.Canon;
import game.pieces.Piece;
import logic.moveChecking.Move;
import logic.moveChecking.PointVisitor;

public class moveHashMap {
	private static HashMap<MoveTest, Integer> bestMovesDepth = new HashMap<MoveTest, Integer>();

	// This board is a separte instance of the game board, used to calculate future moves
	private static Board currentBoard;
	
	public static void main(String[] args) {
		Piece p = new Canon(false, 0, 0, 0);
		MoveTest m = new MoveTest(p, 0, 0, 0, 0);
		bestMovesDepth.put(m, 5);

		MoveTest m2 = new MoveTest(p, 0, 0, 0, 0);
		
		System.out.println(m);
		System.out.println(bestMovesDepth.get(m2));
	}
	
	public static class MoveTest{
	    private Piece piece;
	    private Piece capturedPiece;

	    private int x1;
	    private int y1;
	    private int x2;
	    private int y2;


	    private int dx;
	    private int dy;
	    private boolean isHorizontal;
	    private boolean isVertical;
	    private boolean isDiagonal;
	    private boolean isValid;
	    //private boolean isClear;
	    private int numObstacles; //number of pieces on the path


	    /**
	     * Used in move logger, not in board to save a move that isn't an attack
	     *
	     * @param piece   the piece that is being moved
	     * @param originX the starting x coordinate
	     * @param originY starting y coordinate
	     * @param finalX  the destination x coordinate
	     * @param finalY  the destination y coordinate
	     */
	    public MoveTest(Piece piece, int originX, int originY, int finalX, int finalY) {
	        //this iteration makes the move object hold the piece? Not sure how to structure
	        this.piece = piece;
	        this.x1 = originX;
	        this.y1 = originY;
	        this.x2 = finalX;
	        this.y2 = finalY;

	        this.dx = finalX - originX;
	        this.dy = finalY - originY;
	        if (dx == 0 && dy != 0) {
	            this.isVertical = true;
	        }
	        if (dy == 0 && dx != 0) {
	            this.isHorizontal = true;
	        }
	        if (Math.abs(dx) == Math.abs(dy) && dx != 0) {
	            this.isDiagonal = true;
	        }
	    }

	    public String toString() {
	    	String bOrR = piece.isBlack()? "black" : "red";
	        return bOrR + " " + piece + piece.getId() + " kill " + capturedPiece +": " + x1 + ", " + y1 + ", " + x2 + ", " + y2;
	    }
	    
	    @Override
	    public boolean equals(Object obj) {
	        if (obj == this) {
	            return true;
	        }
	        if (!(obj instanceof MoveTest)) {
	            return false;
	        }
	        MoveTest other = (MoveTest) obj;
	        return this.piece.equals(other.piece)
	                && this.x1 == other.x1
	                && this.y1 == other.y1
	                && this.x2 == other.x2
	                && this.y2 == other.y2;
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(piece, x1, y1, x2, y2);
	    }

	}
}
