package game;

/**
 * Chariot Piece
 */

public class Chariot extends Piece{

    public Chariot(boolean place) {
        super(place);
        this.type = "Chariot";
    }

    @Override

    public void checkPattern(Move move) {
        super.checkPattern(move);

        if (!move.isHorizontal() && !move.isVertical()) {
            move.setValid(false);
        }
    }
}