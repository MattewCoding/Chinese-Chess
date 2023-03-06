package game.pieces;

import logic.moveChecking.PieceVisitor;

/**
 * Cannon Piece
 */
public class Canon extends Piece {
    public Canon(boolean place) {
        super(place);
        this.type = "Cannon";
    }

	@Override
	public <T> T accept(PieceVisitor<T> visitor) {
		return visitor.visit(this);
	}
}