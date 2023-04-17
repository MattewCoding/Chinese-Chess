package game.pieces;

import logic.moveChecking.PieceVisitor;

/**
 * Soldier Piece
 */
public class Soldier extends Piece {
    //private Side curSide;


    public Soldier(boolean place, int x, int y, int id) {
        super(place, x, y, id, 1);
        this.type = "Soldier";
    }
    
    public void crossRiver() {
    	super.worth = 2;
    }
    
	@Override
	public <T> T accept(PieceVisitor<T> visitor) {
		return visitor.visit(this);
	}
}