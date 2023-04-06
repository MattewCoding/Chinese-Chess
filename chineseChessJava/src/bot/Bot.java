package bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.Board;
import game.pieces.Piece;
import logic.moveChecking.Move;
import logic.moveChecking.Moving;
import logic.moveChecking.PointVisitor;
import outOfGameScreens.Profile;

public class Bot {
	
	private boolean playingBlack;
	private Profile bot;
	private Board board;
	
	public Bot(Profile bot, boolean isBlackPlayer) {
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
	
	/**
	 * Finds the ideal move that the computer can make.
	 * Uses alpha-beta pruning to speed up the search
	 * @param depth
	 * @return
	 */
	public int findIdealMove(int depth, int alpha, int beta) {
		if(depth == 0) {
			return evaluate();
		}
		
		List<Piece> oneSidePieces;
		oneSidePieces = board.getAllPieces(true);
	    PointVisitor searchValidMoves = new PointVisitor(board);
	    int moveAmount = 0;
    	
    	for(Piece movingPiece : oneSidePieces) {
    		// For each piece, move it and test how good that position is
    		ArrayList<Integer[]> legalMovesPiece = movingPiece.accept(searchValidMoves);
    		moveAmount += legalMovesPiece.size();
    		for(Integer[] legalDest : legalMovesPiece) {
    			Move testMove = new Move(movingPiece, movingPiece.getX(), movingPiece.getY(), legalDest[0], legalDest[1]);
    			Moving checkValidMove = new Moving(board, testMove);
    			if(board.tryMove(checkValidMove, bot)) {
    				updateBoard(board);
    				int evaluation = -findIdealMove(depth - 1, -beta, -alpha);
    				board.undoMove(testMove, movingPiece);
    				updateBoard(board);
    				if(evaluation >= beta) {
    					// Move was too good, opponent will avoid this position
    					return beta;
    				}
    				alpha = Math.max(alpha, evaluation);
    			}
    			
    		}
    		
    	}
    	if(moveAmount == 0) {
    		//TODO: if(board.getB)
    	}
    	return alpha;
	}
	
	public int evaluate() {
		int redEval = getWorth(false);
		int blackEval = getWorth(true);
		
		int evaluation = redEval - blackEval;
		int perspective = (playingBlack)? -1 : 1;
		
		return evaluation * perspective;
		
	}
	
	public int getWorth(boolean isBlack) {
		int worth = 0;
		List<Piece> randomPieces;
		randomPieces = board.getAllPieces(isBlack);
		for(Piece piece : randomPieces) {
			worth += piece.getWorth();
		}
		return worth;
	}
	
	public void updateBoard(Board newBoardStatus) {
		board = newBoardStatus;
	}

}
