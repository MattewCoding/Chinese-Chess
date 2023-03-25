package game;

import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import game.pieces.Canon;
import game.pieces.Chariot;
import game.pieces.Elephant;
import game.pieces.General;
import game.pieces.Guard;
import game.pieces.Horse;
import game.pieces.Piece;
import game.pieces.Soldier;
import log.LoggerUtility;
import logic.moveChecking.Move;
import logic.moveChecking.Moving;
import outOfGameScreens.Profile;
import outOfGameScreens.menus.SubMenu;

public class Board {

	//Columns then rows
	private Piece[][] coords;
	private final int COLUMNS = 11;
	private final int ROWS = 11;
	private int mirror;

	private HashMap<String, Piece> generalPositions = new HashMap<String, Piece>();

	private static int winner =-1;
	private boolean blackCheck = false; //up is in check
	private boolean redCheck = false; //down is in check
	public static final int PLAYER1_WINS = 1;
	public static final int PLAYER2_WINS = 2;
	public static final int PLAYER1_TIMEOUT_WIN = 3;
	public static final int PLAYER2_TIMEOUT_WIN = 4;
	public static final int DRAW = 0;
	public static final int NA = -1;

	private static Logger logDataBoard = LoggerUtility.getLogger(SubMenu.class, "html");

	public Board() {
		coords = new Piece[COLUMNS][ROWS];

		int quadrant = 0, side, edgeYCoord;
		boolean onBlackSide;
		for(;quadrant < 4; quadrant++) {
			onBlackSide = (quadrant>1);
			side = onBlackSide? -1 : 1;
			edgeYCoord = 5+side*(5);

			String sideString = onBlackSide? "B" : "R";

			if(quadrant%2 == 0) {
				mirror = 1; // Where 1 is the right side of the board and -1 the left side.
			}
			else {
				mirror = -1;

				// Only needs to be created once for each side
				// Since this if-statement is only true once for both side
				// Why NOT put it here?
				coords[5][5+side*(2)] = new Soldier(onBlackSide, 5, 5+side*(2));
				coords[5][edgeYCoord] = new General(onBlackSide, 5, edgeYCoord);

				//allPieces.put("Soldier-1"+sideString, new Soldier(onBlackSide, 5, 5+side*(2)));
				generalPositions.put("General-"+sideString, new General(onBlackSide, 5, edgeYCoord));
			}

			coords[calculateX(5)][5+side*2] = new Soldier(onBlackSide, calculateX(5), 5+side*2);
			coords[calculateX(3)][5+side*2] = new Soldier(onBlackSide, calculateX(3), 5+side*2);
			coords[calculateX(4)][5+side*3] = new Canon(onBlackSide, calculateX(4), 5+side*3);

			coords[calculateX(5)][edgeYCoord] = new Chariot(onBlackSide, calculateX(5), edgeYCoord);
			coords[calculateX(4)][edgeYCoord] = new Horse(onBlackSide, calculateX(4), edgeYCoord);
			coords[calculateX(3)][edgeYCoord] = new Elephant(onBlackSide, calculateX(3), edgeYCoord);
			coords[calculateX(1)][edgeYCoord] = new Guard(onBlackSide, calculateX(1), edgeYCoord);
		}
	}

	public int calculateX(int x) {
		return 5+mirror*x;
	}

	public Piece[][] getCoords() {
		return coords;
	}

	public void setCoords(Piece[][] coords) {
		this.coords = coords;
	}

	/**
	 * Return the piece at the specified coordinates
	 * @param x The horizontal coordinate. Lower value indicates a piece further left on the board.
	 * @param y The vertical coordinate. Lower value indicates a piece higher up on the board
	 * @return The current piece located at the coordinates, or null if there's no piece
	 */
	public Piece getPiece(int x, int y) {
		return coords[x][y];
	}

	/**
	 * Moves the piece on the board.
	 * @param move Contains the original position and the final position of the piece.
	 */
	public void doMove(Move move) {
		Piece curr = this.coords[move.getOriginX()][move.getOriginY()];

		curr.movePiece(move.getFinalX(), move.getFinalY());

		this.coords[move.getFinalX()][move.getFinalY()] = curr;
		this.coords[move.getOriginX()][move.getOriginY()] = null;
	}


	/**
	 * Physically undoes a move by altering the state of the pieces
	 * Not for use in the undo button. It used for move testing reasons.
	 *
	 * @param move the current move
	 * @param captured The piece that was previously captured, so that it can be restored
	 */
	public void undoMove(Move move, Piece captured) {
		Piece curr = getPiece(move.getFinalX(), move.getFinalY());
		coords[move.getOriginX()][ move.getOriginY()] = curr;
		//getPiece(move.getFinalX(), move.getFinalY()).Capture();
		coords[move.getFinalX()][ move.getFinalY()] = captured;
		//System.out.print(" Illegal Move");
	}


	public boolean tryMove(Move move, Profile player) {

		if (new Moving(this,move).isLegal()) {

			Piece curr = this.getPiece(move.getOriginX(),move.getOriginY());
			Piece captured = this.getPiece(move.getFinalX(),move.getFinalY());

			int x = move.getOriginX();
			int y = move.getOriginY();
			int finalX = move.getFinalX();
			int finalY = move.getFinalY();

			System.out.println(curr.isBlack() + " " + player.getPlayerPlace());
			if (curr.isBlack() == player.getPlayerPlace()) {
				this.doMove(move);
				testCheck();
				if (curr.isBlack() == true && blackCheck) {
					System.out.println(" Illegal Move, you're in check");
					this.undoMove(move, captured);
					return false;
				}
				if (curr.isBlack() == false && redCheck) {
					System.out.println(" Illegal Move, you're in check");
					this.undoMove(move, captured);
					return false;


				} else {
					//the move is legal, now lets see if it's a winning move.
					if (blackCheck && curr.isBlack() == false) {
						if (checkMate(true)) {
							setWinner(PLAYER1_WINS);
							//                            System.out.println("##########################CHECK MATE#############################");
							//                            System.out.println(player.getName() + "WINS!");
							//                            System.out.println("##########################CHECK MATE#############################");
						}
						//                        return true;

						//the move is legal, now lets see if it's a winning move.
					} else if (redCheck && curr.isBlack() == true) {
						if (checkMate(false)) {
							setWinner(PLAYER2_WINS);
							//                            System.out.println("##########################CHECK MATE#############################");
							//                            System.out.println(player.getName() + "WINS!");
							//                            System.out.println("##########################CHECK MATE#############################");
						}
						//                        return true;

						//the move is legal, now lets see if it's a stalemate , i reoved the || separated()
					} else if (curr.isBlack() == false) {
						if (checkMate(true)) {
							setWinner(DRAW);
							//                            System.out.println("##########################STALE MATE#############################");
							//                            System.out.println("ITS A DRAW");
							//                            System.out.println("##########################STALE MATE#############################");
						}
						//                        return true;
					} else if (curr.isBlack() == true) {
						if (checkMate(false)) {
							setWinner(DRAW);
							//                            System.out.println("##########################STALE MATE#############################");
							//                            System.out.println("ITS A DRAW");
							//                            System.out.println("##########################STALE MATE#############################");
						}
						//                        return true;
					}

					// if (!checkMate) {   //LEGAL MOVE AND NOT IN CHECKMATE?
					System.out.println("Moved " + curr + " from (" + x + ", " + y + ") to (" + finalX + ", " + finalY + ")");
					if (captured != null) {
						player.addPieceCaptured(captured);
						System.out.println(captured + " Captured!");
						//MoveLogger.addMove(new Move(curr, captured, x, y, finalX, finalY));
					} else {
						//MoveLogger.addMove(new Move(curr, x, y, finalX, finalY));
					}
					this.undoMove(move, captured);
					//DO OTHER THINGS =============

					return true;
					//   }

				}
			} else {
				System.out.println("That's not your piece");
				return false;
			}
		}
		System.out.println("Illegal Move");
		return false;


	}

	private void testCheck() {
		redCheck = false;
		blackCheck = false;
		for (int x = 0; x < 11; x++) {
			for (int y = 0; y < 11; y++) {
				if (this.getPiece(x, y) != null) {

					if (!redCheck && this.getPiece(x, y).isBlack() == true) {
						if (new Moving(this, new Move(x, y, this.getRedGeneralX(), this.getRedGeneralY()), 0).isLegal()) {
							redCheck = true;
							//                            System.out.println("Down is in check");
						}
					} else if (!blackCheck && this.getPiece(x, y).isBlack() == false) {
						if (new Moving(this, new Move(x, y, this.getBlackGeneralX(), this.getBlackGeneralY()), 0).isLegal()) {
							blackCheck = true;
							//                            System.out.println("up is in check");
						}
					}
				}
			}
		}
	}


	private boolean checkMate(boolean loserPlace) {
		//running through every loser piece
		for (int x = 0; x < 11; x++) {
			for (int y = 0; y < 11; y++) {

				if (this.getPiece(x, y) != null && this.getPiece(x, y).isBlack() == loserPlace) {

					//running through every possible point to generate every possible move
					for (int i = 0; i < 9; i++) {
						for (int j = 0; j < 10; j++) {
							Move tempMove = new Move(x, y, i, j); //generating the temporary move
							Piece tempCaptured = this.getPiece(i, j);
							//if that move is legal then attempt it.
							if (new Moving(this, tempMove).isLegal()) { //trying every possible move for the piece
								this.doMove(tempMove); //doing the temporary move
								testCheck(); //updates check status
								//if any of these moves were both legal, and result with us not being in check, we aren't in checkmate.
								if (loserPlace == false) {
									if (!redCheck) {
										this.undoMove(tempMove, tempCaptured);
										return false;
									}
								}
								if (loserPlace == true) {
									if (!blackCheck) {
										this.undoMove(tempMove, tempCaptured);
										return false;
									}
								}
								this.undoMove(tempMove, tempCaptured);

							}
						}
					}
				}
			}
		}
		return true;
	}

	public General getGeneralRed() {
		return (General) generalPositions.get("General-R");
	}

	public General getGeneralBlack() {
		return (General) generalPositions.get("General-B");
	}

	public int getRedGeneralX() {
		return getGeneralRed().getX();
	}

	public int getRedGeneralY() {
		return getGeneralRed().getY();
	}

	public int getBlackGeneralX() {
		return getGeneralBlack().getX();
	}


	public int getBlackGeneralY() {
		return getGeneralBlack().getY();
	}

	public void updateGenerals(int newX, int newY, boolean isBlack) {
		General redGen = getGeneralRed(), blackGen = getGeneralBlack();
		if(isBlack) {
			blackGen.setX(newX);
			blackGen.setY(newY);
		} else {
			redGen.setX(newX);
			redGen.setY(newY);
		}
	}

	public boolean generalsAligned() {
		return getRedGeneralX() == getBlackGeneralX();
	}

	// TODO: this is broken
	/**
	 * Returns true if the generals aren't facing each other by counting the obstacles between them if they're in line.
	 * @param move The move attemping to be done
	 * @return Boolean True if they are facing each other
	 */
	public Boolean approveGenerals(Move move) {
		int numberOfPieces=0;
		
		// TODO: Add condition: what if it's the general itself?
		
		// Generals can only face each other if they're in the same column and the piece moving is moving out of said column
		// OR if the general moves into the same row as the other general
		boolean pieceCanExposeGeneral = generalsAligned() && (move.getOriginX() != move.getFinalX()) && (move.getOriginX() == getRedGeneralX());
		
		boolean generalCanCheckThemself;
		Piece currentPiece = getPiece(move.getOriginX(), move.getOriginY());

		if(generalCanCheckThemself = (currentPiece.getType() == "General")) {
			if(currentPiece.isBlack()) { // if true this is a black piece being moved
				generalCanCheckThemself = (move.getFinalX() == getRedGeneralX());
			} else {
				generalCanCheckThemself = (move.getFinalX() == getBlackGeneralX());
			}
		}
		
		if(pieceCanExposeGeneral || generalCanCheckThemself) {

			// We could count the generals and make it work but
			// This is more clear as it better shows how many pieces are
			// Inbetween the generals
			for(int y = getBlackGeneralY()+1; y < getRedGeneralY() - 1; y++) {
				if(getPiece(getRedGeneralX(), y) != null) {
					numberOfPieces++;
				}
			}
			// If there's only one, then based on the if-statements it has to be
			// The piece we're moving
			return numberOfPieces != 1;
		}
		return true;
	}

	@Override
	public String toString() {
		// This is a debug method to ensure correct placement
		// of the pieces
		for(int i =0;i<COLUMNS;i++) {
			for(int j =0; j<ROWS;j++) {
				String firstLetter;
				if(coords[i][j] == null) firstLetter = "__";
				else firstLetter = coords[i][j].toString().substring(0, 2);
				System.out.print(firstLetter + " ");
			}
			System.out.println();
		}
		return "";
	}

	public static int getWinner() {
		return winner;
	}

	public static void setWinner(int winner) {
		Board.winner = winner;
	}

	public static int getWinner() {
		return winner;
	}

	public static void setWinner(int winner) {
		Board.winner = winner;
	}
}

