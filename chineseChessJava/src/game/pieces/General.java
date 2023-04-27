package game.pieces;

import logic.moveChecking.PieceVisitor;

/**
 * General Piece
 */
public class General extends Piece {
    public General(boolean place, int x, int y) {
    	// It's can't be worth anything since you can't ever eat the General, but we still have to set it to something
        super(place, x, y, 4, "General");
    }

	@Override
	public <T> T accept(PieceVisitor<T> visitor) {
		return visitor.visit(this);
	}
}