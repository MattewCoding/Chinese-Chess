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
        else if (move.getFinalX() < 3 || move.getFinalX() > 8) {
            move.setValid(false);
        }

        if (isBlack) {
            if (move.getFinalY() > 2) {
                move.setValid(false);
            }
        }
        else{
            if (move.getFinalY() < 8) {
                move.setValid(false);
            }
        }


    }
}
