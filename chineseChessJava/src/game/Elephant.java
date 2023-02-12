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
        //changed the finalY to finalX
        if (place) {
            if (move.getFinalX() > 4) {
                move.setValid(false);
            }
        }
        else{
            if (move.getFinalX() < 5) {
                move.setValid(false);
            }
        }

    }
}
