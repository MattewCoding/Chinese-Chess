package logic.moveChecking;
import game.Board;
import game.pieces.Piece;
import logic.boardChecking.BoardManager;

public class Moving {
	private Board board;
	private Move move;
	private MoveVisitor moveChecker = new MoveVisitor();

	private int obstacleCount; //number of pieces in the way
	private boolean isClear; //if obstacles = 0
	private boolean attack;
	private boolean legal;
	
	/**
	 * This is the deep checker. Upon instantiation it will:
	 * <ul>
	 *     <li>Check Move patter validity</li>
	 *     <li>Check if it's an attacking move or a movement move</li>
	 *     <li>Count the number of obstackles and determine if it's a legal move (ie Chariot can attack by jumping over one obstacle</li>
	 *     <li>Check if the generals are facing each other</li>
	 * </ul>
	 * It differs from the shallow check because it checks generals, something not required for delivering check.
	 *
	 * @param board the current board object
	 * @param move  the move to be validated
	 */
	public Moving(Board board, Move move) {
		this.board = board;
		this.move = move;
		this.legal = true;
		
		//  0. check if we're crossing the river or going into the river
		checkCrossing();

		//  1. check if movement pattern is legal (ie horse moves 1 up 2 left)
		if (legal) {
			checkPiece();
		}
		Piece currentPiece = board.getPiece(move.getOriginX(), move.getOriginY());
		
		//  2. Check if moving the piece exposes our general
		if (legal) {
			if(currentPiece.getType() == "General") {
				// Simulate the general move to test validity
				board = BoardManager.updateGenerals(board, move.getFinalX(), move.getFinalY(), currentPiece.isBlack());
				legal = BoardManager.approveGenerals(board, move);
				board = BoardManager.updateGenerals(board, move.getOriginX(), move.getOriginY(), currentPiece.isBlack());
			} else {
				legal = BoardManager.approveGenerals(board, move);
			}
		}
		//System.out.println("Moving: Moving: " + legal);
		
		//  3. check if we are doing an attack, and also check if the end point is blocked by a friendly piece
		if (legal) {
			isAttack();
		}

		//  4. Check if the path is clear, if not see if we're an attacking cannon or a non attacking cannon that can't move
		if (legal) {
			obstacleStats();

			if (!isClear) {
				if (currentPiece.toString().equals("Cannon")) {
					if (!(obstacleCount == 1 && attack)) {
						legal = false;
					}
				} else {
					legal = false;
				}
			} else {
				if (currentPiece.toString().equals("Cannon")) {
					if (attack) {
						legal = false;
					}
				}
			}
		}
	}
	
	/**
	 * This is the shallow checker. Upon instantiation it will:
	 * <ul>
	 *     <li>Check Move patter validity</li>
	 *     <li>Check if it's an attacking move or a movement move</li>
	 *     <li>Count the number of obstackles and determine if it's a legal move (ie Chariot can attack by jumping over one obstacle</li>
	 * </ul>
	 * <p>
	 * It differs from the deep check because it doesn't check generals, something not required for delivering check.
	 *
	 * @param board the current board object
	 * @param move  the move to be validated
	 * @param i     is just used to overload
	 */
	public Moving(Board board, Move move, int i) {
		this.board = board;
		this.move = move;
		this.legal = true;

		//  1. first check if movement pattern is legal (ie horse moves 1 up 2 left)
		checkPiece();

		//  2. check if we are doing an attack, and also check if the end point is blocked by a friendly piece
		if (legal) {
			isAttack();
		}

		//  3. Check if the path is clear, if not See if we're an attacking cannon or a non attacking cannon that can't move

		if (legal) {
			obstacleStats();

			if (!isClear) {
				if (board.getPiece(move.getOriginX(), move.getOriginY()).toString().equals("Cannon")) {
					if (!(obstacleCount == 1 && attack)) {
						legal = false;
					}
				} else {
					legal = false;
				}
			} else {
				if (board.getPiece(move.getOriginX(), move.getOriginY()).toString().equals("Cannon")) {
					if (attack) {
						legal = false;
					}
				}
			}
		}
	}
	
	private void checkCrossing() {
		int startY = move.getOriginY(), endY = move.getFinalY();
		boolean crossingDown = startY < 5 && endY > 5;
		boolean crossingUp = startY > 5 && endY < 5;
		legal = (endY != 5);
		
		if(crossingDown) {
			move.setDy(move.getDy() - 1);
		} else if(crossingUp) {
			move.setDy(move.getDy() + 1);
		}
	}

	/**
	 * Checks if the move pattern is a valid move pattern and if there's a piece present.
	 * If not, terminates the process
	 */
	private void checkPiece() {
		Piece temp = board.getPiece(move.getOriginX(), move.getOriginY());
		moveChecker.setCurrentMove(move);
		this.legal = temp.accept(moveChecker);

	}

	/**
	 * Checks the destination piece to see if we're attacking or self blocked
	 */
	private void isAttack() {
		if (board.getPiece(move.getFinalX(), move.getFinalY()) == null) {
			attack = false;
		} else {
			boolean origin = board.getPiece(move.getOriginX(), move.getOriginY()).isBlack();
			boolean dest = board.getPiece(move.getFinalX(), move.getFinalY()).isBlack();
			
			attack = origin != dest;
			if (origin == dest) {
				this.attack = false;
				this.legal = false;
				//this.isClear = false; //shouldn't matter because illegal anyway now
			}
		}

	}

	/**
	 * Finds out if there are obstacles, and if so, how many?
	 * Useful for seeing if we have one obstacle for the cannon
	 * Useful for seeing if a piece is blocked, handles knights as well.
	 */
	private void obstacleStats() {

		isClear = true;
		this.obstacleCount = 0;

		//vertical move
		if (move.isVertical()) {
			if (move.getDy() > 0) {
				for (int y = move.getOriginY() + 1; y < move.getFinalY(); y++) {
					if (board.getPiece(move.getOriginX(), y) != null) {
						obstacleCount++;
					}
				}
			} else if (move.getDy() < 0) {
				for (int y = move.getOriginY() - 1; y > move.getFinalY(); y--) {
					if (board.getPiece(move.getOriginX(), y) != null) {
						obstacleCount++;
					}
				}
			}


		}
		//horizontal move
		else if (move.isHorizontal()) {
			if (move.getDx() > 0) {
				for (int x = move.getOriginX() + 1; x < move.getFinalX(); x++) {
					if (board.getPiece(x, move.getOriginY()) != null) {
						obstacleCount++;
					}
				}
			} else if (move.getDx() < 0) {
				for (int x = move.getOriginX() - 1; x > move.getFinalX(); x--) {
					if (board.getPiece(x, move.getOriginY()) != null) {
						obstacleCount++;
					}
				}
			}
			//diagonal move
		}
		//diagonal move
		else if (move.isDiagonal()) {

			//left up
			if (move.getDx() < 0 && move.getDy() < 0) {
				for (int x = 1; x < move.getDx(); x++) {
					if (board.getPiece(move.getOriginX() - x, move.getOriginY() - x) != null) {
						obstacleCount++;
					}
				}
			}
			//left down
			else if (move.getDx() < 0 && move.getDy() > 0) {
				for (int x = 1; x < move.getDx(); x++) {
					if (board.getPiece(move.getOriginX() - x, move.getOriginY() + x) != null) {
						obstacleCount++;
					}
				}
			}
			//right down
			else if (move.getDx() > 0 && move.getDy() > 0) {
				for (int x = 1; x < move.getDx(); x++) {
					if (board.getPiece(move.getOriginX() + x, move.getOriginY() + x) != null) {
						obstacleCount++;
					}
				}
			}

			//right up
			else {// (move.getDx() > 0 && move.getDy() > 0) {
				for (int x = 1; x < move.getDx(); x++) {
					if (board.getPiece(move.getOriginX() + x, move.getOriginY() - x) != null) {
						obstacleCount++;
					}
				}
			}
		}
		//only for knights, as only knights have non linear moves. Knights are only blocked by the nearest pieces.
		else {

			if (move.getDx() == 2) {
				if (board.getPiece(move.getOriginX() + 1, move.getOriginY()) != null) {
					obstacleCount++;
				}
			} else if (move.getDx() == -2) {
				if (board.getPiece(move.getOriginX() - 1, move.getOriginY()) != null) {
					obstacleCount++;
				}
			} else if (move.getDy() == 2) {
				if (board.getPiece(move.getOriginX(), move.getOriginY() + 1) != null) {
					obstacleCount++;
				}
			} else if (move.getDy() == -2) {
				if (board.getPiece(move.getOriginX(), move.getOriginY() - 1) != null) {
					obstacleCount++;
				}
			}


		}
		if (obstacleCount != 0) {
			isClear = false;
		}


	}

	public boolean isLegal() {
		return legal;
	}
	
	public Move getMove() {
		return move;
	}
}

