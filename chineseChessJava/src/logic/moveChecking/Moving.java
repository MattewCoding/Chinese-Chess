package logic.moveChecking;
import game.Board;
import game.pieces.Piece;

public class Moving {
	private Board board;
	private Move move;
	private MoveVisitor moveChecker = new MoveVisitor();

	private int obstacleCount; //number of pieces in the way
	private boolean isClear; //if obstacles = 0
	private boolean attack;
	private boolean legal;
	
	private int redGeneralX, redGeneralY, blackGeneralX, blackGeneralY;

	/**
	 * This is the deep checker. Upon instantiation it will:
	 * <ul>
	 *     <li>Check Move patter validity</li>
	 *     <li>Check if it's an attacking move or a movement move</li>
	 *     <li>Count the number of obstackles and determine if it's a legal move (ie Chariot can attack by jumping over one obstacle</li>
	 *     <li>Check if the generals are facing each other</li>
	 * </ul>
	 * <p>
	 * It differs from the shallow check because it checks generals, something not required for delivering check.
	 *
	 * @param board the current board object
	 * @param move  the move to be validated
	 */
	public Moving(Board board, Move move) {
		this.board = board;
		this.move = move;
		this.legal = true;

		//  1. first check if movement pattern is legal (ie horse moves 1 up 2 left)
		CheckPiece();
		Piece currentPiece = board.getCoords(move.getOriginX(), move.getOriginY());
		
		//  2. Check if moving the piece exposes our general
		getGenerals();
		approveGenerals();

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
		CheckPiece();

		//  2. check if we are doing an attack, and also check if the end point is blocked by a friendly piece
		if (legal) {
			isAttack();
		}

		//  3. Check if the path is clear, if not See if we're an attacking cannon or a non attacking cannon that can't move

		if (legal) {
			obstacleStats();

			if (!isClear) {
				if (board.getCoords(move.getOriginX(), move.getOriginY()).toString().equals("Cannon")) {
					if (!(obstacleCount == 1 && attack)) {
						legal = false;
					}
				} else {
					legal = false;
				}
			} else {
				if (board.getCoords(move.getOriginX(), move.getOriginY()).toString().equals("Cannon")) {
					if (attack) {
						legal = false;
					}
				}
			}
		}
	}


	
	/**
	 * Returns the column number if the generals are in the same column
	 * 
	 * @return curr The column number where both generals are or -1 if in different columns
	 */
	public void getGenerals() {
        for (int x = 3; x < 8; x++) {
            for (int y = 0; y < 3; y++) {
                Piece curr = board.getCoords(x, y);
                if (curr != null && curr.toString().equals("General")) {
                	redGeneralX = x;
                	redGeneralY = y;
                }

            }

            for (int y = 8; y < 11; y++) {
                Piece curr = board.getCoords(x, y);
                if (curr != null && curr.toString().equals("General")) {
                	blackGeneralX = x;
                	blackGeneralY = y;
                }
            }
        }
	}


	/**
	 * Returns that the generals aren't facing each other by counting the obstacles between them if they're in line.
	 *
	 * @return True if they are facing eachother (illegal)
	 */
	private void approveGenerals() {
		// Generals can only face each other if they're in the same column
		// And the piece moving is moving out of said column
		if( (redGeneralX == blackGeneralX) && (move.getOriginX() == redGeneralX) && (move.getOriginX() != move.getFinalX()) ) {
			int numberOfPieces=0;
			
			// We could count the generals and make it work but
			// This is more clear as it better shows how many pieces are
			// Inbetween the generals
			for(int y = redGeneralY+1; y < blackGeneralY - 1; y++) {
				if(board.getCoords(redGeneralX, y) != null) {
					numberOfPieces++;
				}
			}
			// If there's only one, then based on the if-statements it has to be
			// The piece we're moving
			legal = numberOfPieces != 1;
		}

	}


	/**
	 * Checks if the move pattern is a valid move pattern and if there's a piece present.
	 * If not, terminates the process
	 */
	private void CheckPiece() {
		Piece temp = board.getCoords(move.getOriginX(), move.getOriginY());

		if (temp == null) {
			this.legal = false;
		} else {
			moveChecker.setCurrentMove(move);
			this.legal = temp.accept(moveChecker);
		}

	}

	/**
	 * Checks the destination piece to see if we're attacking or self blocked
	 */
	private void isAttack() {
		if (board.getCoords(move.getFinalX(), move.getFinalY()) == null) {
			attack = false;
		} else {
			boolean origin = board.getCoords(move.getOriginX(), move.getOriginY()).isBlack();
			boolean dest = board.getCoords(move.getFinalX(), move.getFinalY()).isBlack();
			
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
					if (board.getCoords(move.getOriginX(), y) != null) {
						obstacleCount++;
					}
				}
			} else if (move.getDy() < 0) {
				for (int y = move.getOriginY() - 1; y > move.getFinalY(); y--) {
					if (board.getCoords(move.getOriginX(), y) != null) {
						obstacleCount++;
					}
				}
			}


		}
		//horizontal move
		else if (move.isHorizontal()) {
			if (move.getDx() > 0) {
				for (int x = move.getOriginX() + 1; x < move.getFinalX(); x++) {
					if (board.getCoords(x, move.getOriginY()) != null) {
						obstacleCount++;
					}
				}
			} else if (move.getDx() < 0) {
				for (int x = move.getOriginX() - 1; x > move.getFinalX(); x--) {
					if (board.getCoords(x, move.getOriginY()) != null) {
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
					if (board.getCoords(move.getOriginX() - x, move.getOriginY() - x) != null) {
						obstacleCount++;
					}
				}
			}
			//left down
			else if (move.getDx() < 0 && move.getDy() > 0) {
				for (int x = 1; x < move.getDx(); x++) {
					if (board.getCoords(move.getOriginX() - x, move.getOriginY() + x) != null) {
						obstacleCount++;
					}
				}
			}
			//right down
			else if (move.getDx() > 0 && move.getDy() > 0) {
				for (int x = 1; x < move.getDx(); x++) {
					if (board.getCoords(move.getOriginX() + x, move.getOriginY() + x) != null) {
						obstacleCount++;
					}
				}
			}

			//right up
			else {// (move.getDx() > 0 && move.getDy() > 0) {
				for (int x = 1; x < move.getDx(); x++) {
					if (board.getCoords(move.getOriginX() + x, move.getOriginY() - x) != null) {
						obstacleCount++;
					}
				}
			}
		}
		//only for knights, as only knights have non linear moves. Knights are only blocked by the nearest pieces.
		else {

			if (move.getDx() == 2) {
				if (board.getCoords(move.getOriginX() + 1, move.getOriginY()) != null) {
					obstacleCount++;
				}
			} else if (move.getDx() == -2) {
				if (board.getCoords(move.getOriginX() - 1, move.getOriginY()) != null) {
					obstacleCount++;
				}
			} else if (move.getDy() == 2) {
				if (board.getCoords(move.getOriginX(), move.getOriginY() + 1) != null) {
					obstacleCount++;
				}
			} else if (move.getDy() == -2) {
				if (board.getCoords(move.getOriginX(), move.getOriginY() - 1) != null) {
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
}
