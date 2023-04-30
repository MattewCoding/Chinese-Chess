package game.pieces;

import logic.moveChecking.PieceVisitor;

/**
 * Cannon Piece
 * @author NASRO Rona
 */
public class Canon extends Piece {
    public Canon(boolean place, int x, int y) {
        super(place, x, y, 5, "Cannon");
    }

	@Override
	public <T> T accept(PieceVisitor<T> visitor) {
		return visitor.visit(this);
	}
}