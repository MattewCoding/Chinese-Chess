package game;

import game1.Move;

/**
 * General Piece
 */
public class General extends Piece {
    public General(boolean place) {
        super(place);

        this.type = "General";
    }

    @Override

    public void checkPattern(Move move) {

        super.checkPattern(move);

        if (!move.isHorizontal() && !move.isVertical()) {
            move.setValid(false);
        }
        else if (Math.abs(move.getDx()) > 1 || Math.abs(move.getDy()) > 1) {
            move.setValid(false);
        }

        //stays in generals quarters
        if (move.getFinalY() < 3 || move.getFinalY() > 7) {
            move.setValid(false);
        }

        if (place) {
            if (move.getFinalX() > 2) {
                move.setValid(false);
            }
        }
        else{
            if (move.getFinalX() < 8) {
                move.setValid(false);
            }
        }


    }
}