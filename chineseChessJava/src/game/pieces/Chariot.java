package game.pieces;

import logic.moveChecking.PieceVisitor;

/**
 * Chariot Piece
 */

public class Chariot extends Piece{

    public Chariot(boolean place) {
        super(place);
        this.type = "Chariot";
    }

	@Override
	public <T> T accept(PieceVisitor<T> visitor) {
		return visitor.visit(this);
	}
}