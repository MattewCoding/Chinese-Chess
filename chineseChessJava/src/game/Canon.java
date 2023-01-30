package game;

import game1.Move;

/**
 * Cannon Piece
 */
public class Canon extends Piece {
    public Canon(boolean place) {
        super(place);
        this.type = "Cannon";
    }

    @Override

    public void checkPattern(Move move) {
        super.checkPattern(move);

        if (!(move.isHorizontal() || move.isVertical())) {
            move.setValid(false);
        }
    }
}