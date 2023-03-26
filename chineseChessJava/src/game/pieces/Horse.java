package game.pieces;

import logic.moveChecking.PieceVisitor;

public class Horse extends Piece {
	public Horse(boolean place, int x, int y, int id) {
        super(place, x, y, id, 4);
        this.type = "Horse";
    }

	@Override
	public <T> T accept(PieceVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
