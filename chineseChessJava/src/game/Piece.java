package game;

import game1.Move;

/**
 * Abstract class for Piece. Each piece is unaware of it's location. They contain little member data and are stored on points, which are managed by boards.
 *
 */


public abstract class Piece {
    protected String type;
    protected boolean place;
    private boolean captured;

    /**
     * Constructor for creating a Piece. All that is needed is which side of the board the piece is on. The side is provided as an enum.
     *
     * for the place if black it is true if red it is 0.
     */
    public Piece(boolean place) {
        this.place = place;
        this.setCaptured(false);
    }

    /**
     * This method will check if a given move is a move that this piece can generally make. For example, elephants can move diagonally two spaces. In a way, this is what defines a piece.
     */
    public void checkPattern(Move move) {
        move.setValid(true);
    }

    /**
     * Are we up river or down river? Determines the player
     **/
    
    public boolean getSide() {
        return place;
    }

    /**
     * Sets the current piece to a captured state, not capturing a different piece.
     * If this piece was captured by another, than you would use capture on this peice.
     */
    
    public void Capture() {
        this.setCaptured(true);
    }

    public String toString() {

        return this.type;
    }

    /**
     * returns the matching file name of a piece
     *
     */
    public String getImageName() {
        String fileName = "";
        if (place)
            fileName += "black_";
        else
            fileName += "red_";

        if (type.equals("Soldier"))
            fileName += "soldier";
        else if (type.equals("General"))
            fileName += "general";
        else if (type.equals("Cannon"))
            fileName += "cannon";
        else if (type.equals("Horse"))
            fileName += "horse";
        else if (type.equals("Elephant"))
            fileName += "elephant";
        else if (type.equals("Guard"))
            fileName += "guard";
        else if (type.equals("Chariot"))
            fileName += "chariot";

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
