package bot.sort;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import game.Board;
import game.pieces.Piece;
import logic.moveChecking.Move;

public class MovePreferStrategy implements Comparator<Move>{

	// This board is a separte instance of the game board, used to calculate future moves
	private Board movePreferBoard;
	private boolean playingBlack;

	public MovePreferStrategy(Board board, boolean playingBlack) {
		super();

		Piece[][] newBoardLayout = board.getCoords();
		Piece[][] updateBoard = new Piece[newBoardLayout.length][];
		for (int i = 0; i < newBoardLayout.length; i++) {
			updateBoard[i] = Arrays.copyOf(newBoardLayout[i], newBoardLayout[i].length);
		}
		movePreferBoard = new Board(updateBoard);
		this.playingBlack = playingBlack;

	}

	/**
	 * How much our pieces combined are worth
	 * @param isBlack Whose pieces we're counting
	 * @return The combined worth of our pieces
	 */
	public int getWorth(boolean isBlack) {
		int worth = 0;
		List<Piece> randomPieces;
		randomPieces = movePreferBoard.getAllPieces(isBlack);
		for(Piece piece : randomPieces) {
			worth += piece.getWorth();
		}
		return worth;
	}

	/**
	 * A rudamentary way of determining who has the advantage and by how much
	 * @return How much of a (dis)advantage the bot is at
	 */
	public boolean compareSideWorths() {
		Integer[] redEval = compareSideWorths(false);
		Integer[] blackEval = compareSideWorths(true);

		int redLeft = redEval[0];
		int redRight = redEval[1];

		int blackLeft = blackEval[0];
		int blackRight = blackEval[1];
		
		int perspective = (playingBlack)? -1 : 1;
		return ((redLeft-blackLeft)*perspective > (redRight-blackRight)*perspective);

	}
	
	public Integer[] compareSideWorths(boolean isBlack) {
		int leftWorth = 0, rightWorth = 0;
		List<Piece> leftPieces, rightPieces;
		leftPieces = movePreferBoard.getLeftPieces(isBlack);
		rightPieces = movePreferBoard.getRightPieces(isBlack);
		for(Piece piece : leftPieces) {
			leftWorth += piece.getWorth();
		}
		for(Piece piece : rightPieces) {
			rightWorth += piece.getWorth();
		}
		Integer[] res = {leftWorth, rightWorth};
		return res;
	}
	
	public int evaluateMove(Move move) {
		Piece currPiece = movePreferBoard.getPiece(move.getOriginX(), move.getOriginY());
		boolean preferLeft = compareSideWorths();
		if(currPiece.getType().equals("Cannon")) {
			return 5 - (move.getFinalX() - 5);
		}
		int finalX = move.getFinalX();
		return (preferLeft && finalX > 5) || (!preferLeft && finalX < 5) ? finalX - 5 : 0;
	}

	@Override
	public int compare(Move m1, Move m2) {
		return evaluateMove(m2) - evaluateMove(m1);
	}
}
