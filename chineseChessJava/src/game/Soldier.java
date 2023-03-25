package game;

import game1.Move;

/**
 * Soldier Piece
 */
public class Soldier extends Piece {
    //private Side curSide;


    public Soldier(boolean place) {
        super(place);
        this.type = "Soldier";
    }

    @Override


    /**
     * Checks if the move pattern is actually valid or not for a piece. This does not handle the legality of a move.
     * For example: A pawn moves forward, but only moves left or right when it crosses a river. It can never move two spaces
     *
     * @param move
     */
    public void checkPattern(Move move) {
        super.checkPattern(move);
        
        //finds which side of river it's on, and sets it as member data, maybe scope could just be method?
        boolean curSide;
        if (move.getOriginY() <= 4) {
            curSide = true;
        } else {
            curSide = false;
        }

        //checks for vertical forward moves only based on side
        if (place == curSide) {
            if (this.place == true) {
                if (move.getDy() != 1 || !move.isVertical()) {
                    move.setValid(false);
                }
            }
            if (place == false) {
                if (move.getDy() != -1 || !move.isVertical()) {
                    move.setValid(false);
                }
            }

        }
        //allows for horizontal movement once river is crossed
        if (this.place != curSide) {
            if (!move.isHorizontal() && !move.isVertical()) {
                move.setValid(false);
            }

            if (this.place == true) {
                if (!(move.getDy() == 1 || Math.abs(move.getDx()) == 1)) {
                    move.setValid(false);
                }
            }
            if (this.place == false) {
                if (!(move.getDy() == -1 || Math.abs(move.getDx()) == 1)) {
                    move.setValid(false);
                }
            }

        }
    }
}