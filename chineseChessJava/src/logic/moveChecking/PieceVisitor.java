package logic.moveChecking;

import game.pieces.*;

/**
 * Generic piece visitor supporting all piece types.
 * 
 * @author Yang Mattew
 **/
public interface PieceVisitor<T> {

	T visit(Canon piece);

	T visit(Chariot piece);

	T visit(Elephant piece);

	T visit(General piece);

	T visit(Guard piece);

	T visit(Horse piece);

	T visit(Soldier piece);
	
}
