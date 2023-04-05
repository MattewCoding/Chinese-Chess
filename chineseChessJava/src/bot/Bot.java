package bot;

import java.util.List;

import game.Board;
import game.pieces.Piece;
import logic.moveChecking.Move;
import logic.moveChecking.Moving;
import outOfGameScreens.Profile;

public class Bot {
	
	private boolean playingBlack;
	private Board board;
	
	public Bot(boolean isBlackPlayer) {
		playingBlack = isBlackPlayer;
	}
	
	public Move generateMove(Board board, Profile player2) {
		List<Piece> randomPieces;
		randomPieces = board.getAllPieces(true);
		// Auto generate a move
		Move move = board.GenerateMoves(randomPieces);
		Moving moving = new Moving(board, move);

		while(!board.tryMove(moving,player2)) {
			randomPieces = board.getAllPieces(true);
			// Auto generate a move
			move = board.GenerateMoves(randomPieces);
			moving = new Moving(board, move);
		}
		return move;
	}
	
	public void findIdealMove(Board board) {
		
	}
	
	public int evaluate(Board board) {
		int redEval = getWorth(false);
		int blackEval = getWorth(true);
		
		int evaluation = redEval - blackEval;
		int perspective = (playingBlack)? -1 : 1;
		
		return 0;
		
	}
	
	public int getWorth(boolean isBlack) {
		int worth = 0;
		List<Piece> randomPieces;
		randomPieces = board.getAllPieces(true);
		for(Piece piece : randomPieces) {
			worth += piece.getWorth();
		}
		return worth;
	}
	
	public void updateBoard(Board newBoardStatus) {
		board = newBoardStatus;
	}

}
