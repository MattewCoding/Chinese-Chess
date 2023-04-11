package onlineTesting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import game.Board;
import game.pieces.Piece;
import logic.moveChecking.Move;
import logic.moveChecking.Moving;
import logic.moveChecking.PointVisitor;
import outOfGameScreens.Profile;

/**
 * With special thanks to Sebastian Lague for his work on Chess AI: https://www.youtube.com/watch?v=U4ogK0MIzqk
 * Scrapped class that generates the full list of moves possible by the player before evaluating them
 * @author Yang Mattew
 *
 */
public class BotScrap {
	private ArrayList<Move> bestMoves = new ArrayList<Move>();

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
	public BotScrap(Profile bot, boolean isBlackPlayer, int maxDepth) {
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

		System.out.println("Time elapsed: " + timeElapsed + " milliseconds");

		Random random = new Random();

		/*
    	for(Move m : bestMoves) {
    		System.out.println(m);
    	}*/
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
		if(depth == 0) {
			return searchAllCaptures(alpha, beta);
		}

		HashMap<Integer[], ArrayList<Integer[]>> allLegalMoves = generateMoves();

		if(allLegalMoves.isEmpty()) {
			if(currentBoard.checkMate(playingBlack)) { // Checkmate
				return NEGATIVE_INFINITY;
			}
			return 0;
		}

		for(Integer[] pieceCoord : allLegalMoves.keySet()) {
			Piece movingPiece = currentBoard.getPiece(pieceCoord[0], pieceCoord[1]);
			int pieceX = movingPiece.getX(), pieceY = movingPiece.getY();

			for(Integer[] legalDest : allLegalMoves.get(pieceCoord)) {
				int destX = legalDest[0], destY = legalDest[1];
				Piece capturedPiece = currentBoard.getPiece(destX, destY);
				Move testMove = new Move(movingPiece, pieceX, pieceY, destX, destY);

				currentBoard.doMove(testMove);
				int evaluation = (capturedPiece == null)? 0 : 10 * capturedPiece.getWorth() - movingPiece.getWorth();
				evaluation -= findIdealMove(depth - 1, -beta, -alpha);
				currentBoard.undoMove(testMove, capturedPiece);

				if(evaluation >= beta) {
					// Move was too good, opponent will avoid this position
					return beta;
				}

				// If eval > alpha, then we have a move that's better than all previous, so we don't care about the previous anymore
				if(evaluation >= alpha) {
					if(evaluation > alpha) {
						alpha = evaluation;
						bestMoves.clear();
					}

					boolean isIncluded = false;
					for(Move m: bestMoves) {
						if(m.isEqual(testMove)) {
							isIncluded = true;
							break;
						}
					}
					if(!isIncluded && depth == maxDepth) { // Add for first-order moves
						bestMoves.add(testMove);
					}
				}
			}

		}

		return alpha;
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


		return 0;
	}

	/**
	 * Generates all possible moves a piece can make for every piece available for the player
	 * @return
	 */
	public HashMap<Integer[], ArrayList<Integer[]>> generateMoves() {
		List<Piece> oneSidePieces;
		oneSidePieces = currentBoard.getAllPieces(true);
		PointVisitor searchValidMoves = new PointVisitor(currentBoard);
		HashMap<Integer[], ArrayList<Integer[]>> allLegalMoves = new HashMap<Integer[], ArrayList<Integer[]>> ();

		for(Piece movingPiece : oneSidePieces) {
			ArrayList<Integer[]> legalMovesPiece = movingPiece.accept(searchValidMoves);
			Integer[] pieceCoords = {movingPiece.getX(), movingPiece.getY()};
			allLegalMoves.put(pieceCoords, legalMovesPiece);
		}
		return allLegalMoves;
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


	public void main(String[] args) {
		Board b = new Board();
		Profile p = new Profile("Test", 0, true);
		BotScrap bot = new BotScrap(p, true, 4);
		bot.generateIdealMove();
		System.out.println(bestMoves.size());

		System.out.println("best moves: ");
		for(Move m : bestMoves) {
			System.out.println(m);
		}
	}

}
