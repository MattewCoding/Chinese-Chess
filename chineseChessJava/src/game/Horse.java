package game;

public class Horse extends Piece {
	public Horse(boolean place) {
        super(place);
        this.type = "Horse";
    }

    public void checkPattern(game1.Move move) {
        super.checkPattern(move);

        if (!((Math.abs(move.getDx()) == 1 && Math.abs(move.getDy()) == 2) || (Math.abs(move.getDx()) == 2 && Math.abs(move.getDy()) == 1))) {
            move.setValid(false);
        }
    }
}
