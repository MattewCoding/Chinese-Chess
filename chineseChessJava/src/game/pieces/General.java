package game.pieces;

import logic.moveChecking.PieceVisitor;

/**
 * General Piece
 */
public class General extends Piece {
    public General(boolean place, int x, int y) {
        super(place, x, y);

        this.type = "General";
    }

	@Override
	public <T> T accept(PieceVisitor<T> visitor) {
		return visitor.visit(this);
	}
}