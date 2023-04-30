package game.pieces;

import logic.moveChecking.PieceVisitor;

/**
 * Chariot Piece
 * @author NASRO Rona
 */

public class Chariot extends Piece{

    public Chariot(boolean place, int x, int y) {
        super(place, x, y, 9, "Chariot");
    }

	@Override
	public <T> T accept(PieceVisitor<T> visitor) {
		return visitor.visit(this);
	}
}