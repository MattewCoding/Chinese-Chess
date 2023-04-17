package game.pieces;

import logic.moveChecking.PieceVisitor;

/**
 * Cannon Piece
 */
public class Canon extends Piece {
    public Canon(boolean place, int x, int y, int id) {
        super(place, x, y, id, 5);
        this.type = "Cannon";
    }

	@Override
	public <T> T accept(PieceVisitor<T> visitor) {
		return visitor.visit(this);
	}
}