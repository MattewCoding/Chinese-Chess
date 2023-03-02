package game.pieces;

import logic.moveChecking.PieceVisitor;

/**
 * Soldier Piece
 */
public class Soldier extends Piece {
    //private Side curSide;


    public Soldier(boolean place) {
        super(place);
        this.type = "Soldier";
    }
    
	@Override
	public <T> T accept(PieceVisitor<T> visitor) {
		return visitor.visit(this);
	}
}