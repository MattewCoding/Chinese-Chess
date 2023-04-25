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
import logic.moveChecking.Move;
import logic.moveChecking.Moving;
import logic.moveChecking.PointVisitor;
import outOfGameScreens.Profile;

/**
 * With special thanks to Sebastian Lague for his work on Chess AI: https://www.youtube.com/watch?v=U4ogK0MIzqk
 * @author Yang Mattew
 *
 */
public class Bot {
	private static Logger logDataBot = LoggerUtility.getLogger(Bot.class, "html");
	
	private ArrayList<Move> bestMoves = new ArrayList<Move>();
	private Map<Long, Integer> transpositionTable = new HashMap<>();

	private boolean playingBlack;
	private Profile bot;
	private int maxDepth;

	// This board is a separte instance of the game board, used to calculate future moves
	private Board currentBoard;

	// It's impossible to get -1000 in advantage even with the order modifiers (maxBase = 60 * maxModifier = 10)
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
		this.maxDepth = maxDepth;
	}

	/**
	 * Generates a random move
	 * @param currentBoard The current state of the currentBoard
	 * @param player The bot player
	 * @return
	 */
	public Move generateMove(Board board, Profile player) {
		List<Piece> randomPieces;
		randomPieces = board.getAllPieces(true);
		// Auto generate a move
		Move move = board.GenerateMoves(randomPieces);
		Moving moving = new Moving(board, move);

		while(!board.tryMove(moving,player)) {
			randomPieces = board.getAllPieces(true);
			// Auto generate a move
			move = board.GenerateMoves(randomPieces);
			moving = new Moving(board, move);
		}
		return move;
	}

	/**
	 * Finds an optimal move based on future possibilities it/the player can do
	 * @return An ideal move
	 */
	public Move generateIdealMove() {
		long startTime = System.currentTimeMillis();


		bestMoves.clear();
		findIdealMove(maxDepth, NEGATIVE_INFINITY, POSITIVE_INFINITY);

		long endTime = System.currentTimeMillis();
		long timeElapsed = endTime - startTime;

		Random random = new Random();

		logDataBot.info("Best Moves:");
		for(Move m : bestMoves) {
			logDataBot.info(m);
		}

		logDataBot.info("Time elapsed: " + timeElapsed + " milliseconds");
		int n = random.nextInt(bestMoves.size());
		//System.out.println(bestMoves.get(n));
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
	public int findIdealMove(int depth, int alpha, int beta) {
		long hash = currentBoard.getHash();
		if (transpositionTable.containsKey(hash) && depth == transpositionTable.get(hash)) {
			return transpositionTable.get(hash);
		}

		if (depth == 0) {
			int score = evaluate();
			transpositionTable.put(hash, score);
			return score;
		}

		List<Piece> pieces = currentBoard.getAllPieces(playingBlack);
		PointVisitor searchValidMoves = new PointVisitor(currentBoard);
		List<Move> moves = new ArrayList<>();

		for (Piece piece : pieces) {
			List<Integer[]> legalMovesPiece = piece.accept(searchValidMoves);
			for (Integer[] legalDest : legalMovesPiece) {
				int pieceX = piece.getX(), pieceY = piece.getY();
				int destX = legalDest[0], destY = legalDest[1];
				Piece capturedPiece = currentBoard.getPiece(destX, destY);
				Move move = new Move(piece, pieceX, pieceY, destX, destY);
				if (capturedPiece != null) {
					move.setCapturedPiece(capturedPiece);
				}
				moves.add(move);
			}
		}

		moves.sort(new moveSortingStrategy(currentBoard));

		int bestScore = NEGATIVE_INFINITY;
		for (Move move : moves) {
			currentBoard.doMove(move);
			int evaluation;
			if (move.getCapturedPiece() == null) {
				evaluation = -findIdealMove(depth - 1, -beta, -alpha);
	        } else {
	        	evaluation = 10 * move.getCapturedPiece().getWorth() - move.getPiece().getWorth() - findIdealMove(depth - 1, -beta, -alpha);
	        }
			currentBoard.undoMove(move, move.getCapturedPiece());

			//System.out.println(evaluation + " v. " + bestScore + " v. " + alpha);
			bestScore = Math.max(evaluation, bestScore);
			if (bestScore >= beta) {
				// Move was too good, opponent will avoid this position
				return beta;
			}

			if (bestScore >= alpha) {
				if(bestScore > alpha) {
					alpha = bestScore;
					if(depth == maxDepth) {
						bestMoves.clear();
					}
				}
				if(!bestMoves.contains(move) && depth == maxDepth) { // Add for first-order moves
					bestMoves.add(move);
				}
			}
		}

		if (bestScore == NEGATIVE_INFINITY) { // Is checkmate or stalemate
			if (currentBoard.checkMate(playingBlack)) { // Checkmate
				transpositionTable.put(hash, bestScore);
				return bestScore;
			}
			transpositionTable.put(hash, 0);
			return 0;
		}
		transpositionTable.put(hash, bestScore);

		return bestScore;
	}


	/**
	 * Captures aren't generally forces, so evaluate position before capturing.
	 * Otherwise check captures; if no good captures, rate unfavorably the position
	 * @param alpha The current player's best possible score
	 * @param beta The opponent's best possible score
	 * @return The adjusted advantage of the move
	 */
	public int searchAllCaptures(int alpha, int beta) {
		int evaluation = evaluate();
		if(evaluation >= beta) {
			return beta;
		}
		alpha = Math.max(alpha, evaluation);

		List<Piece> oneSidePieces;
		oneSidePieces = currentBoard.getAllPieces(playingBlack);
		PointVisitor searchValidMoves = new PointVisitor(currentBoard);

		for(Piece movingPiece : oneSidePieces) {
			ArrayList<Integer[]> legalMovesPiece = movingPiece.accept(searchValidMoves);

			for(Integer[] legalDest : legalMovesPiece) {
				int destX = legalDest[0], destY = legalDest[1];
				Piece capturedPiece = currentBoard.getPiece(destX, destY);

				if(capturedPiece != null) {
					int pieceX = movingPiece.getX(), pieceY = movingPiece.getY();
					Move testMove = new Move(movingPiece, pieceX, pieceY, destX, destY);

					currentBoard.doMove(testMove);
					evaluation = -searchAllCaptures(-beta, -alpha);
					currentBoard.undoMove(testMove, capturedPiece);

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
		randomPieces = currentBoard.getAllPieces(isBlack);
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
		Piece[][] newBoardLayout = newBoardStatus.getCoords();
		Piece[][] updateBoard = new Piece[newBoardLayout.length][];
		for (int i = 0; i < newBoardLayout.length; i++) {
			updateBoard[i] = Arrays.copyOf(newBoardLayout[i], newBoardLayout[i].length);
		}
		currentBoard = new Board(updateBoard);
	}

	public class moveSortingStrategy implements Comparator<Move>{

		// This board is a separte instance of the game board, used to calculate future moves
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
			randomPieces = currentBoard.getAllPieces(isBlack);
			for(Piece piece : randomPieces) {
				worth += piece.getWorth();
			}
			return worth;
		}

		public int evaluateMove(Move move) {
			Piece capturedPiece = moveSortingBoard.getPiece(move.getFinalX(), move.getFinalY());
			moveSortingBoard.doMove(move);
			int capturingOffset = (capturedPiece == null)? 0 : 10 * move.getCapturedPiece().getWorth() - move.getPiece().getWorth();
			int evaluation = capturingOffset + evaluate();
			moveSortingBoard.undoMove(move, capturedPiece);
			return evaluation;
		}

		@Override
		public int compare(Move m1, Move m2) {
			return evaluateMove(m2) - evaluateMove(m1);
		}

	}

}
