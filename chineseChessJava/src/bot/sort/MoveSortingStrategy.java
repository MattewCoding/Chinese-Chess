package bot.sort;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import game.Board;
import game.pieces.Piece;
import logic.moveChecking.Move;
import outOfGameScreens.ScreenParameters;

/**
 * A way to sort the moves to prefer checking for better moves first.
 * @author Yang Mattew
 *
 */
public class MoveSortingStrategy implements Comparator<Move>{

	// This board is a separte instance of the game board, used to calculate future moves
	private Board moveSortingBoard;
	private boolean playingBlack;
	private HashMap<Move, Integer> hashMoves;

	public MoveSortingStrategy(Board board, boolean playingBlack) {
		super();

		Piece[][] newBoardLayout = board.getCoords();
		Piece[][] updateBoard = new Piece[newBoardLayout.length][];
		for (int i = 0; i < newBoardLayout.length; i++) {
			updateBoard[i] = Arrays.copyOf(newBoardLayout[i], newBoardLayout[i].length);
		}
		moveSortingBoard = new Board(updateBoard);
		this.playingBlack = playingBlack;

	}

	public MoveSortingStrategy(HashMap<Move, Integer> bestMovesDepth) {
		hashMoves = bestMovesDepth;
	}

	/**
	 * A rudamentary way of determining who has the advantage and by how much
	 * @return How much of a (dis)advantage the bot is at
	 */
	public int evaluate() {
		int redEval = getWorth(false);
		int blackEval = getWorth(true);

		int evaluation = redEval - blackEval;
		int perspective = (playingBlack)? -1 : 1;

		return evaluation * perspective;

	}

	/**
	 * How much our pieces combined are worth
	 * @param isBlack Whose pieces we're counting
	 * @return The combined worth of our pieces
	 */
	public int getWorth(boolean isBlack) {
		int worth = 0;
		List<Piece> randomPieces;
		randomPieces = moveSortingBoard.getAllPieces(isBlack);
		for(Piece piece : randomPieces) {
			worth += piece.getWorth();
		}
		return worth;
	}

	public int evaluateMove(Move move) {
		Piece capturedPiece = moveSortingBoard.getPiece(move.getFinalX(), move.getFinalY());
		boolean isBlack = move.getPiece().isBlack();
		
		moveSortingBoard.doMove(move);
		
		// Checking if opponent is in checkmate
		boolean isCheckmate = moveSortingBoard.checkMate(!isBlack);
		if(isCheckmate) {
			return ScreenParameters.POSITIVE_INFINITY;
		}
		
		int capturingOffset = (capturedPiece == null)? 0 : 10 * move.getCapturedPiece().getWorth() - move.getPiece().getWorth();
		
		moveSortingBoard.testCheck();
		boolean isChecking = isBlack? moveSortingBoard.getRedCheck() : moveSortingBoard.getBlackCheck();
		int checkOffset = isChecking? 10 : 0;
		
		int evaluation = capturingOffset + checkOffset + evaluate();
		moveSortingBoard.undoMove(move, capturedPiece);
		return evaluation;
	}

	@Override
	public int compare(Move m1, Move m2) {
		/*
		System.out.println("bestMoves: MoveSortingStrat; ");
		for(Move move: hashMoves.keySet()) {
			System.out.println(move + " vs. " + m2 + " = " + move.equals(m2));
			System.out.println(hashMoves.containsKey(move) + " " + hashMoves.containsKey(m2));
		}
		
		System.out.println("Comparing");
		System.out.println(m2);
		System.out.println(m1);*/
		
		//return hashMoves.get(m2) - hashMoves.get(m1);
		return evaluateMove(m2) - evaluateMove(m1);
	}

}