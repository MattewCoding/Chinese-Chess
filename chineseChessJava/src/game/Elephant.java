package game;

import logic.Move;

/**
 * Elephant Piece
 */
public class Elephant extends Piece {
    public Elephant(boolean place) {
        super(place);
        this.type = "Elephant";
    }

    @Override

    public void checkPattern(Move move) {
        super.checkPattern(move);

        if (!move.isDiagonal() || Math.abs(move.getDx()) != 2) {
            move.setValid(false);
        }

        //river crossing prevention
        if (place) {
            if (move.getFinalY() > 4) {
                move.setValid(false);
            }
        }
        else{
            if (move.getFinalY() < 5) {
                move.setValid(false);
            }
        }

    }
}
