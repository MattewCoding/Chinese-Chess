package game.pieces;

import logic.moveChecking.PieceVisitor;

/**
 * Elephant Piece
 * @author NASRO Rona
 */
public class Elephant extends Piece {
    public Elephant(boolean place, int x, int y) {
        super(place, x, y, 2, "Elephant");
    }

	@Override
	public <T> T accept(PieceVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
