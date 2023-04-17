package game.pieces;

import logic.moveChecking.PieceVisitor;

/**
 * Chariot Piece
 */

public class Chariot extends Piece{

    public Chariot(boolean place, int x, int y, int id) {
        super(place, x, y, id, 9);
        this.type = "Chariot";
    }

	@Override
	public <T> T accept(PieceVisitor<T> visitor) {
		return visitor.visit(this);
	}
}