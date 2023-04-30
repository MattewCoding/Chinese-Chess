package bot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.logging.log4j.Logger;

import game.Board;
import game.pieces.Piece;
import log.LoggerUtility;
import logic.boardChecking.BoardManager;
import logic.moveChecking.Move;
import logic.moveChecking.PointVisitor;
import outOfGameScreens.Profile;

/**
 * With special thanks to Sebastian Lague for his work on Chess AI: https://www.youtube.com/watch?v=U4ogK0MIzqk
 * @author Yang Mattew
 *
 */
public class Bot {

	private static Logger logDataBot = LoggerUtility.getLogger(Bot.class, "html");

	/**
	 * The most optimal moves that the bot can make (there can be more than one, esp. at the beginning of the game)
	 */
	private ArrayList<Move> bestMoves = new ArrayList<Move>();

	/**
	 * A list of positions and their calculated advantage
	 */
	private Map<Long, Integer> transpositionTable = new HashMap<>();

	private boolean playingBlack;

	/**
	 * The amount of moves we want to look ahead
	 */
	private int maxDepth;

	/**
	 * This board is a separate instance of the game board, used to calculate future moves
	 */
	private Board currentBoard;

	// It's impossible to get -1000 in advantage even with the order modifiers
	private final static int NEGATIVE_INFINITY = -1000;
	private final static int POSITIVE_INFINITY = 1000;

	/**
	 * Creates the bot
	 * @param bot The bot's win/lost information
	 * @param isBlackPlayer Which side the bot is playing
	 */
	public Bot(Profile bot, boolean isBlackPlayer, int maxDepth) {
		playingBlack = isBlackPlayer;
		currentBoard = new Board();
		
		// 0 breaks searchAllCaptures because it requires a previous move and here there is no previous move because we haven't made any
		assert maxDepth > 0;
		this.maxDepth = maxDepth;
	}

	/**
	 * Finds an optimal move based on future possibilities it/the player can do
	 * @return An ideal move
	 */
	public Move generateIdealMove() {
		long startTime = System.currentTimeMillis();

		long endTime = System.currentTimeMillis();

		// We need to clear the previously calculated optimal moves to find
		// The new optimal moves
		bestMoves.clear();
		findIdealMove(maxDepth, NEGATIVE_INFINITY, POSITIVE_INFINITY, null);
		endTime = System.currentTimeMillis();

		for(Move m : bestMoves) {
			logDataBot.info(m);
		}
		long timeElapsed = endTime - startTime;
		logDataBot.info("Time elapsed: " + timeElapsed + " milliseconds");

		// Since all moves are equally optimal, we simply pick a random one
		int n = new Random().nextInt(bestMoves.size());
		return bestMoves.get(n);
	}

	/**
	 * Finds the ideal move that the computer can make.
	 * Uses alpha-beta pruning to speed up the search
	 * @param depth How many moves ahead we want to search
	 * @param alpha The current player's best possible score
	 * @param beta The opponent's best possible score
	 * @return How good a move is
	 */
	public int findIdealMove(int depth, int alpha, int beta, Move previousMove) {

		// Check if we haven't already calculated the advantage for this position
		long hash = currentBoard.getHash();
		if (transpositionTable.containsKey(hash) && depth == transpositionTable.get(hash)) {
			return transpositionTable.get(hash);
		}

		// Once we've reached how far we want to look ahead, we simply need to calculate the advantage
		if (depth == 0) {
			int advantage = searchAllCaptures(alpha, beta, previousMove);
			transpositionTable.put(hash, advantage);
			return advantage;
		}

		// We need to search through all the possible moves we can make in order to find the best one
		List<Piece> pieces = BoardManager.getAllPieces(currentBoard, playingBlack);
		PointVisitor searchValidMoves = new PointVisitor(currentBoard);
		List<Move> moves = new ArrayList<>();
		for (Piece piece : pieces) {
			List<Integer[]> legalMovesPiece = piece.accept(searchValidMoves);
			for (Integer[] legalDest : legalMovesPiece) {
				int pieceX = piece.getX(), pieceY = piece.getY();
				int destX = legalDest[0], destY = legalDest[1];
				Piece capturedPiece = currentBoard.getPiece(destX, destY);
				Move move = new Move(piece, capturedPiece, pieceX, pieceY, destX, destY);
				moves.add(move);
			}
		}

		// We don't know which moves are better, but we can take a guess
		moves.sort(new moveSortingStrategy(currentBoard));

		int bestScore = NEGATIVE_INFINITY;
		for (Move move : moves) {

			// Calculate the advantage from doing this move
			currentBoard = BoardManager.doMove(currentBoard, move);

			Piece piece = move.getPiece();
			currentBoard.updateHash(move.getOriginX(), move.getOriginY(), piece.getType());
			currentBoard.updateHash(move.getFinalX(), move.getFinalY(), piece.getType());

			int advantage = -findIdealMove(depth - 1, -beta, -alpha, move);

			currentBoard = BoardManager.undoMove(currentBoard, move, move.getCapturedPiece());
			currentBoard.updateHash(move.getFinalX(), move.getFinalY(), piece.getType());
			currentBoard.updateHash(move.getOriginX(), move.getOriginY(), piece.getType());

			bestScore = Math.max(advantage, bestScore);
			if (bestScore >= beta) {
				// Move was too good, opponent will avoid this position
				return beta;
			}

			if (bestScore >= alpha) {
				if(bestScore > alpha) {
					alpha = bestScore;
					if(depth == maxDepth) {
						// Found a new set of best moves, so we need to start over
						bestMoves.clear();
					}
				}
				if(!bestMoves.contains(move) && depth == maxDepth) { // Add for first-order moves
					bestMoves.add(move);
				}
			}
		}

		// Is checkmate or stalemate
		// This only happens if the move set was empty
		// (the lowest possible advantage will never be as low as negative infinity)
		if (bestScore == NEGATIVE_INFINITY) {
			if (BoardManager.checkMate(currentBoard, playingBlack)) { // Is checkmate
				transpositionTable.put(hash, bestScore);
				return bestScore;
			}
			transpositionTable.put(hash, 0);
			return 0;
		}

		// Add the associated position to the table for future referencing
		transpositionTable.put(hash, bestScore);

		return bestScore;
	}


	/**
	 * Captures aren't generally forced, so evaluate position before capturing.
	 * Otherwise check captures; if no good captures, rate unfavorably the position
	 * @param alpha The current player's best possible score
	 * @param beta The opponent's best possible score
	 * @return The adjusted advantage of the move
	 */
	public int searchAllCaptures(int alpha, int beta, Move previousMove) {
		int evaluation = evaluate(previousMove);
		if(evaluation >= beta) {
			// Move is already good enough
			return beta;
		}
		alpha = Math.max(alpha, evaluation);

		// We want to see the places that our pieces can move to, and thus capture
		List<Piece> oneSidePieces;
		oneSidePieces = BoardManager.getAllPieces(currentBoard, playingBlack);
		PointVisitor searchValidMoves = new PointVisitor(currentBoard);

		// For each piece we check all of its positions
		for(Piece movingPiece : oneSidePieces) {
			ArrayList<Integer[]> legalMovesPiece = movingPiece.accept(searchValidMoves);

			for(Integer[] legalDest : legalMovesPiece) {
				int destX = legalDest[0], destY = legalDest[1];
				Piece capturedPiece = currentBoard.getPiece(destX, destY);

				// If we can capture, we recursively check if doing this is advantageous
				// i.e. does eating this put ourselves in danger?
				if(capturedPiece != null) {
					int pieceX = movingPiece.getX(), pieceY = movingPiece.getY();
					Move testMove = new Move(movingPiece, pieceX, pieceY, destX, destY);

					currentBoard = BoardManager.doMove(currentBoard, testMove);
					evaluation = -searchAllCaptures(-beta, -alpha, testMove);
					currentBoard = BoardManager.undoMove(currentBoard, testMove, capturedPiece);

					if(evaluation >= beta) {
						return beta;
					}
					alpha = Math.max(alpha, evaluation);
				}
			}
		}
		return alpha;
	}

	/**
	 * Calculates the evaluation of the current board state after a given move.
	 * The evaluation is based on the worth of pieces on each side, whether the opponent
	 * general is in check, and the benefit gained by capturing a piece or checking the opponent.
	 *
	 * @param move The move to evaluate
	 * @return The evaluation of the current board state, with the perspective of the player to move.
	 */
	public int evaluate(Move move) {
		// Calculate the worth of pieces on each side
		int redEval = getWorth(false);
		int blackEval = getWorth(true);

		// See if we're checking the opponent general
		currentBoard = BoardManager.testCheck(currentBoard);
		boolean isCheck = move.getPiece().isBlack()? currentBoard.getRedCheck() : currentBoard.getBlackCheck();

		// Add up the various benefits to see if this move is really advantageous
		int capturingOffset = (move.getCapturedPiece() == null)? 0 : 10 * move.getCapturedPiece().getWorth() - move.getPiece().getWorth();
		int checking = isCheck? 100 : 0;
		int evaluation = - capturingOffset - checking + redEval - blackEval;
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
		randomPieces = BoardManager.getAllPieces(currentBoard, isBlack);
		for(Piece piece : randomPieces) {
			worth += piece.getWorth();
		}
		return worth;
	}

	/**
	 * Update the current state of the currentBoard (used for checking ideal moves)
	 * @param newBoardStatus The new state of the currentBoard
	 */
	public void updateBoard(Board newBoardStatus) {
		// Simply doing an assign won't work because we want the new board to have a different address
		// i.e. newBoardStatus and currentBoard should be independent from one another
		Piece[][] newBoardLayout = newBoardStatus.getCoords();
		Piece[][] updateBoard = new Piece[newBoardLayout.length][];
		for (int i = 0; i < newBoardLayout.length; i++) {
			updateBoard[i] = Arrays.copyOf(newBoardLayout[i], newBoardLayout[i].length);
		}
		currentBoard = new Board(updateBoard);
	}

	/**
	 * Sort the moves by making reasonable guesses, by implementing Comparator to rank guesses.
	 * The idea is that since alpha-beta pruning ignores worse moves found later on,
	 * by starting with potentially optimal moves, we can eliminate more worse guesses.
	 * @author Yang Mattew
	 *
	 */
	public class moveSortingStrategy implements Comparator<Move>{

		/**
		 * This board is a separte instance of the game board, used to calculate future moves.
		 */
		private Board moveSortingBoard;

		public moveSortingStrategy(Board board) {
			super();

			Piece[][] newBoardLayout = board.getCoords();
			Piece[][] updateBoard = new Piece[newBoardLayout.length][];
			for (int i = 0; i < newBoardLayout.length; i++) {
				updateBoard[i] = Arrays.copyOf(newBoardLayout[i], newBoardLayout[i].length);
			}
			moveSortingBoard = new Board(updateBoard);

		}

		/**
		 * How much our pieces combined are worth
		 * @param isBlack Whose pieces we're counting
		 * @return The combined worth of our pieces
		 */
		public int getWorth(boolean isBlack) {
			int worth = 0;
			List<Piece> randomPieces;
			randomPieces = BoardManager.getAllPieces(currentBoard, isBlack);
			for(Piece piece : randomPieces) {
				worth += piece.getWorth();
			}
			return worth;
		}

		/**
		 * Calculates the evaluation of the current board state after a given move.
		 * This is equivalent to findIdealMove(0, NEGATIVE_INFINITY, POSITIVE_INFINITY, null)
		 *
		 * @param move The move to evaluate
		 * @return The evaluation of the current board state, with the perspective of the player to move.
		 */
		public int evaluateMove(Move move) {
			Piece capturedPiece = moveSortingBoard.getPiece(move.getFinalX(), move.getFinalY());
			moveSortingBoard = BoardManager.doMove(moveSortingBoard, move);
			int evaluation = evaluate(move);
			moveSortingBoard = BoardManager.undoMove(moveSortingBoard, move, capturedPiece);
			return evaluation;
		}

		@Override
		public int compare(Move m1, Move m2) {
			return evaluateMove(m2) - evaluateMove(m1);
		}

	}

}
