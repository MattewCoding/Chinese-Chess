package game.pieces;

import logic.moveChecking.PieceVisitor;

/**
 * Abstract class for Piece. Each piece is unaware of it's location. They contain little member data and are stored on points, which are managed by boards.
 *
 */


public abstract class Piece {
    protected String type;
    protected boolean isBlack;
    private boolean captured;

    /**
     * Constructor for creating a Piece. All that is needed is which side of the board the piece is on. The side is provided as an enum.
     *
     * for the isBlack if black it is true if red it is false.
     */
    public Piece(boolean place) {
        this.isBlack = place;
        this.setCaptured(false);
    }
	
	public abstract <T> T accept(PieceVisitor<T> visitor);

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

    public String toString() {
        return this.type;
    }

    public String getType() {
    	return this.type;
    }
    
    public void setType(String type) {
    	this.type = type;
    }
    /**
     * returns the matching file name of a piece
     *
     */
    public String getImageName() {
        String fileName = "";
        if (isBlack)
            fileName += "black_";
        else
            fileName += "red_";
        
        fileName += type.toLowerCase();

        fileName += ".png";
        return fileName;
    }


    public boolean isCaptured() {
		return captured;
	}

	public void setCaptured(boolean captured) {
		this.captured = captured;
	}

}
