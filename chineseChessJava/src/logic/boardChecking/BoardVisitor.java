package logic.boardChecking;

import game.Board;

public interface BoardVisitor<T> {

	T visit(Board board);

}
