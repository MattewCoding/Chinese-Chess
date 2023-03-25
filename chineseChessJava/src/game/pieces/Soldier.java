package game.pieces;

import logic.moveChecking.PieceVisitor;

/**
 * Soldier Piece
 */
public class Soldier extends Piece {
    //private Side curSide;


    public Soldier(boolean place, int x, int y) {
        super(place, x, y);
        this.type = "Soldier";
    }
    
	@Override
	public <T> T accept(PieceVisitor<T> visitor) {
		return visitor.visit(this);
	}
}