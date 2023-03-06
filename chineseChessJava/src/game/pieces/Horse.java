package game.pieces;

import logic.moveChecking.PieceVisitor;

public class Horse extends Piece {
	public Horse(boolean place) {
        super(place);
        this.type = "Horse";
    }

	@Override
	public <T> T accept(PieceVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
