package logic.boardChecking;

import java.util.ArrayList;

import java.util.List;

import org.apache.logging.log4j.Logger;

import game.Board;
import game.pieces.General;
import game.pieces.Piece;
import log.LoggerUtility;
import logic.moveChecking.Move;
import logic.moveChecking.Moving;
import logic.moveChecking.PointVisitor;
import outOfGameScreens.Profile;

/*
 *  @author NASRO Rona, YANG Mattew
 */

public class BoardManager {
	
	private static Logger logDataBoard = LoggerUtility.getLogger(Board.class, "html");

	public static final int NA = -1;
	public static final int DRAW = 0;
	public static final int PLAYER1_WINS = 1;
	public static final int PLAYER2_WINS = 2;
	public static final int PLAYER1_TIMEOUT_WIN = 3;
	public static final int PLAYER2_TIMEOUT_WIN = 4;
    
    /**
     * Creates a list of all of one side's pieces
     * 
     * @param isBlack Whether we want the black pieces or the red pieces
     * @return The list of black pieces
     */
    public static List<Piece> getAllPieces(Board board, boolean isBlack) {
        List<Piece> pieces = new ArrayList<Piece>();
        for(int x = 0; x < board.COLUMNS; x++) {
            for(int y = 0; y < board.ROWS; y++) {
                Piece piece = board.getPiece(x, y);
                if(piece != null && (piece.isBlack() == isBlack)) {
                    pieces.add(piece);
                }
            }
        }
        return pieces;
    }

	/**
	 * Moves the piece on the board.
	 * @param move Contains the original position and the final position of the piece.
	 */
	public static Board doMove(Board board, Move move) {
		// Update position in piece, board, and list of pieces
		Piece curr = board.getPiece(move.getOriginX(), move.getOriginY());
		int finalX = move.getFinalX();
		int finalY = move.getFinalY();

		curr.movePiece(finalX, finalY);

		board.setPiece(finalX, finalY, curr);
		board.setPiece(move.getOriginX(), move.getOriginY(), null);
		return board;
	}


	/**
	 * Physically undoes a move by altering the state of the pieces
	 * Not for use in the undo button. It used for move testing reasons.
	 *
	 * @param move the current move
	 * @param captured The piece that was previously captured, so that it can be restored
	 */
	public static Board undoMove(Board board, Move move, Piece captured) {
		Piece curr = board.getPiece(move.getFinalX(), move.getFinalY());

		curr.movePiece(move.getOriginX(), move.getOriginY());

		board.setPiece(move.getOriginX(), move.getOriginY(), curr);
		board.setPiece(move.getFinalX(), move.getFinalY(), captured);

		return board;
	}

    /**
     * Checks:
     * <ul>
     * 		<li>If the move puts the player in check</li>
     * 		<li>If the player in check, checks if they have any legal moves left.
     * 			If so, only those set of moves are permitted</li>
     * </ul>
     * @param moving The move the player has requested to make.
     * @param player Who is currently moving the pieces
     * @param moveTesting Whether or not we're testing if the move is legal, as opposed to test+move if legal
     * @return Whether or not the move is legal
     */
	public static boolean tryMove(Board board, Moving moving, Profile player) {
		Move move = moving.getMove();

		if (moving.isLegal()) {

			Piece curr = board.getPiece(move.getOriginX(), move.getOriginY());
			Piece captured = board.getPiece(move.getFinalX(), move.getFinalY());

			int x = move.getOriginX();
			int y = move.getOriginY();
			int finalX = move.getFinalX();
			int finalY = move.getFinalY();

			if (curr.isBlack() == player.getPlayerPlace()) {
				board = doMove(board, move);
				board = testCheck(board);
				if (curr.isBlack() && board.getBlackCheck()) {
					logDataBoard.info(" Illegal Move, you're in check");
					board = undoMove(board, move, captured);
					return false;
				}
				if (!curr.isBlack() && board.getRedCheck()) {
					logDataBoard.info(" Illegal Move, you're in check");
					board = undoMove(board, move, captured);
					return false;
				} else {
					//the move is legal, now let's see if it's a winning move.
					if (board.getBlackCheck() && !curr.isBlack()) {
						if (checkMate(board, true) ) {
							Board.setWinner(PLAYER1_WINS);
						}
	
						//the move is legal, now lets see if it's a winning move.
					} else if (board.getRedCheck() && curr.isBlack()) {
						if (checkMate(board, false)) {
							Board.setWinner(PLAYER2_WINS);
						}
	
						//the move is legal, now lets see if it's a stalemate , i recoved the || separated()
					} else if (!curr.isBlack()) {
						if (checkMate(board, true)) {
							Board.setWinner(DRAW);
						}
					} else if (curr.isBlack()) {
						if (checkMate(board, false)) {
							Board.setWinner(DRAW);
						}
					}
	
					// if (!checkMate) {   //LEGAL MOVE AND NOT IN CHECKMATE?
					//System.out.println("Moved " + curr + " from (" + x + ", " + y + ") to (" + finalX + ", " + finalY + ")");
					logDataBoard.info("Moved " + curr + " from (" + x + ", " + y + ") to (" + finalX + ", " + finalY + ")");
					if (captured != null) {
						player.addPieceCaptured(captured);
						logDataBoard.info(captured + " Captured!");
					}
					return true;
				}
			} else {
				logDataBoard.info("That's not your piece");
				return false;
			}
		}
		logDataBoard.info("Illegal Move");
		return false;
	}
	
	/**
	 * Tests whether or not we are in checkmate
	 * @param isBlack Whose side could be in checkmate
	 * @return Whether or not the specified side is in checkmate
	 */
	public static boolean checkMate(Board board, boolean isBlack) {
		//updateGenerals();
		//running through every loser piece
		for (int x = 0; x < 11; x++) {
			for (int y = 0; y < 11; y++) {
				if (board.getPiece(x, y) != null && board.getPiece(x, y).isBlack() == isBlack) {
					PointVisitor pointV = new PointVisitor(board);
					Piece currentPiece = board.getPiece(x, y);
					ArrayList<Integer[]> moveSet = currentPiece.accept(pointV);

					for(Integer[] move : moveSet) {
						int i = move[0], j = move[1];
						Move tempMove = new Move(x, y, i, j); //generating the temporary move
						Piece tempCaptured = board.getPiece(i, j);

						// Attempt the move
						board = doMove(board, tempMove); //doing the temporary move
						board = testCheck(board); //updates check status

						// If any of these moves were both legal, and result with us not being in check, we aren't in checkmate.
						if (isBlack == false) {
							if (!board.getRedCheck()) {
								board = undoMove(board, tempMove, tempCaptured);
								return false;
							}
						}
						if (isBlack == true) {
							if (!board.getBlackCheck()) {
								board = undoMove(board, tempMove, tempCaptured);
								return false;
							}
						}
						board = undoMove(board, tempMove, tempCaptured);
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * Checks if either the red or black general is in check
	 */
	public static Board testCheck(Board board) {
		board.setRedCheck(false);
		board.setBlackCheck(false);

		// loop through all the pieces on the board
		for (int x = 0; x < 11; x++) {
			for (int y = 0; y < 11; y++) {
				// if there is a piece on this square
				if (board.getPiece(x, y) != null) {
					Piece piece = board.getPiece(x, y);
					int generalX = piece.isBlack() ? board.getRedGeneralX() : board.getBlackGeneralX();
					int generalY = piece.isBlack() ? board.getRedGeneralY() : board.getBlackGeneralY();

					// check if this piece can move to the opposite color general's position
					if (new Moving(board, new Move(x, y, generalX, generalY), 0).isLegal()) {
						if (piece.isBlack()) {
							board.setRedCheck(true);
						} else {
							board.setBlackCheck(true);
						}
					}
				}
			}
		}
		return board;
	}

	/**
	 * Returns true if the generals aren't facing each other by counting the obstacles between them if they're in line.
	 * @param move The move attemping to be done
	 * @return Boolean True if they are facing each other
	 */
	public static Boolean approveGenerals(Board board, Move move) {
		int numberOfPieces=0;

		// Generals can only face each other if they're in the same column and the piece moving is moving out of said column
		// OR if the general moves into the same row as the other general
		boolean pieceCanExposeGeneral = board.generalsAligned() && (move.getOriginX() != move.getFinalX()) && (move.getOriginX() == board.getRedGeneralX());
		boolean sameColumn = false;
		Piece currentPiece = board.getPiece(move.getOriginX(), move.getOriginY());

		if(currentPiece.getType() == "General") {
			// Simulate the move to see if the general would be exposed to the enemy general
			sameColumn = currentPiece.isBlack()? move.getFinalX() == board.getRedGeneralX() : move.getFinalX() == board.getBlackGeneralX();

			// If the move captures another piece, we ought to note that
			if(move.isVertical() && board.getPiece(move.getFinalX(), move.getFinalY()) != null) {
				numberOfPieces = 0;
			} else {
				// Account for the fact that the piece we're moving is the general itself
				numberOfPieces = 1;
			}
		}

		if(pieceCanExposeGeneral || sameColumn) {

			// We could count the generals and make it work but
			// This is more clear as it better shows how many pieces are
			// Inbetween the generals
			for(int y = board.getBlackGeneralY()+1; y < board.getRedGeneralY() - 1; y++) {
				if(board.getPiece(board.getRedGeneralX(), y) != null) {
					numberOfPieces++;
				}
			}
			// If there's only one, then based on the if-statements it has to be
			// The piece we're moving
			return numberOfPieces > 1;
		}
		return true;
	}
	
	public static Board updateGenerals(Board board, int newX, int newY, boolean isBlack) {
		General redGen = board.getGeneralRed(), blackGen = board.getGeneralBlack();
		if(isBlack) {
			blackGen.setX(newX);
			blackGen.setY(newY);
		} else {
			redGen.setX(newX);
			redGen.setY(newY);
		}
		return board;
	}
}
