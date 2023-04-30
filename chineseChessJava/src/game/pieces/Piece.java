package game.pieces;

import logic.moveChecking.PieceVisitor;

/**
 * Abstract class for Piece. Each piece is unaware of it's location. They contain little member data and are stored on points, which are managed by boards.
 *  @author NASRO Rona
 *
 */


public abstract class Piece {
    protected String type;
    protected boolean isBlack;
    private boolean captured;
    private int x;
    private int y;
    private String imageName;
    protected int worth;

    /**
     * Constructor for creating a Piece. All that is needed is which side of the board the piece is on. The side is provided as an enum.
     *
     * for the isBlack if black it is true if red it is false.
     */
    public Piece(boolean place, int x, int y, int worth, String type) {
        this.isBlack = place;
        this.setCaptured(false);
        this.x = x;
        this.y = y;
        this.type = type;
        
        this.worth = worth;
        
        imageName = "";
        imageName += (isBlack)? "black_" : "red_";
        imageName += type.toLowerCase();
        imageName += ".png";
    }
    
    /**
     * Move a piece on the board to a new position. Can chain multiple moves.
     * 
     * @param newX The new x-coordinate of the piece.
     * @param newY The new y-coordinate of the piece.
     * @return The piece itself, for multiple moves.
     */
    public Piece movePiece(int newX, int newY) {
    	x = newX;
    	y = newY;
    	return this;
    }

    /**
     * Are we up river or down river? Determines the player. Returns true if we are up the river
     **/
    public boolean isBlack() {
        return isBlack;
    }

    /**
     * Sets the current piece to a captured state, not capturing a different piece.
     * If this piece was captured by another, than you would use capture on this piece.
     */
    public void Capture() {
        this.setCaptured(true);
    }

    public String getType() {
    	return this.type;
    }
    
    public void setType(String type) {
    	this.type = type;
    }


    public boolean isCaptured() {
		return captured;
	}

	public void setCaptured(boolean captured) {
		this.captured = captured;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public int getWorth() {
		return worth;
	}
	
	public Integer[] getPosition() {
		Integer[] pos = {x,y};
		return pos;
	}
	
    /**
     * Finds where a piece's image is stored
     *
     * @return fileName the file location of the piece's image relative to src/../
     */
    public String getImageName() {
    	return imageName;
    }

    public String toString() {
        return this.type;
    }

	public abstract <T> T accept(PieceVisitor<T> visitor);
}
