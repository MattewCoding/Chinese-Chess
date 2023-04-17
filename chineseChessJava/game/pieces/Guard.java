package game.pieces;

import logic.moveChecking.PieceVisitor;

/**
 * Guard Piece
 */
public class Guard extends Piece {

    public Guard(boolean place, int x, int y, int id) {
        super(place, x, y, id, 2);
        this.type = "Guard";
    }

	@Override
	public <T> T accept(PieceVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
