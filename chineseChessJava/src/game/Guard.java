package game;

import logic.Move;

/**
 * Guard Piece
 */
public class Guard extends Piece {

    public Guard(boolean place) {
        super(place);
        this.type = "Guard";
    }

    @Override

    public void checkPattern(Move move) {
        super.checkPattern(move);
        //changed the finalX to finalY and verse visa

        if (!move.isDiagonal() || Math.abs(move.getDx()) != 1) {
            move.setValid(false);
        }
        else if (move.getFinalY() < 3 || move.getFinalY() > 8) {
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
