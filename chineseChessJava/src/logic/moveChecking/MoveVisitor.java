package logic.moveChecking;

import game.pieces.Canon;
import game.pieces.Chariot;
import game.pieces.Elephant;
import game.pieces.General;
import game.pieces.Guard;
import game.pieces.Horse;
import game.pieces.Soldier;

/**
 * This class will check if a given move is a move that this piece can generally make. For example, elephants can move diagonally two spaces. In a way, this is what defines a piece.
 * @author Yang Mattew, Nasro Rona
 */
public class MoveVisitor implements PieceVisitor<Boolean> {

	private Move move;

	public MoveVisitor() { }

	public void setCurrentMove(Move newMove) {
		move = newMove;
		move.setValid(true);
	}

	@Override
	public Boolean visit(Canon piece) {
		if (!(move.isHorizontal() || move.isVertical())) {
			move.setValid(false);
		}
		return move.isValid();
	}

	@Override
	public Boolean visit(Chariot piece) {
		if (!move.isHorizontal() && !move.isVertical()) {
			move.setValid(false);
		}
		return move.isValid();
	}

	@Override
	public Boolean visit(Elephant piece) {
		if (!move.isDiagonal() || Math.abs(move.getDx()) != 2) {
			move.setValid(false);
		}

		//river crossing prevention
		if (piece.isBlack()) {
			if (move.getFinalY() > 4) {
				move.setValid(false);
			}
		}
		else{
			if (move.getFinalY() < 5) {
				move.setValid(false);
			}
		}
		return move.isValid();
	}

	@Override
	public Boolean visit(General piece) {

		if (!move.isHorizontal() && !move.isVertical()) {
			move.setValid(false);
		}
		else if (Math.abs(move.getDx()) > 1 || Math.abs(move.getDy()) > 1) {
			move.setValid(false);
		}

		//stays in generals quarters
		if (move.getFinalX() < 3 || move.getFinalX() > 7) {
			move.setValid(false);
		}

		if (piece.isBlack()) {
			if (move.getFinalY() > 2) {
				move.setValid(false);
			}
		}
		else{
			if (move.getFinalY() < 8) {
				move.setValid(false);
			}
		}
		return move.isValid();
	}

	@Override
	public Boolean visit(Guard piece) {
		if (!move.isDiagonal() || Math.abs(move.getDx()) != 1) {
			move.setValid(false);
		}
		else if (move.getFinalX() < 3 || move.getFinalX() > 8) {
			move.setValid(false);
		}

		if (piece.isBlack()) {
			if (move.getFinalY() > 2) {
				move.setValid(false);
			}
		}
		else{
			if (move.getFinalY() < 8) {
				move.setValid(false);
			}
		}
		return move.isValid();
	}

	@Override
	public Boolean visit(Horse piece) {
		if (!((Math.abs(move.getDx()) == 1 && Math.abs(move.getDy()) == 2) || (Math.abs(move.getDx()) == 2 && Math.abs(move.getDy()) == 1))) {
			move.setValid(false);
		}
		return move.isValid();
	}

	@Override
	public Boolean visit(Soldier piece) {

		//finds which side of river it's on, and sets it as member data
		boolean aboveRiver = (move.getOriginY() <= 5);

		//Checks for vertical forward moves if not yet crossed river
		if (piece.isBlack() == aboveRiver) {
			if (piece.isBlack() == true) {
				if (move.getDy() != 1 || !move.isVertical()) {
					move.setValid(false);
				}
			} else {
				if (move.getDy() != -1 || !move.isVertical()) {
					move.setValid(false);
				}
			}
		}

		//allows for horizontal movement once river is crossed
		if (piece.isBlack() != aboveRiver) {
			if (!move.isHorizontal() && !move.isVertical()) {
				move.setValid(false);
			}
			if (piece.isBlack() == true) {
				if (!(move.getDy() == 1 || Math.abs(move.getDx()) == 1)) {
					move.setValid(false);
				}
			} else {
				if (!(move.getDy() == -1 || Math.abs(move.getDx()) == 1)) {
					move.setValid(false);
				}
			}

		}
		return move.isValid();
	}

}
