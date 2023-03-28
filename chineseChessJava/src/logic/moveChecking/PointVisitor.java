package logic.moveChecking;

import java.util.ArrayList;
import game.Board;
import game.pieces.Canon;
import game.pieces.Chariot;
import game.pieces.Elephant;
import game.pieces.General;
import game.pieces.Guard;
import game.pieces.Horse;
import game.pieces.Piece;
import game.pieces.Soldier;

/**
 * This class will determine all of the legal moves a piece can make.
 * @author Yang Mattew
 */
public class PointVisitor implements PieceVisitor<ArrayList<Integer[]>>{
	// Reminder: (0,0) is top-left (lowering y goes up, lowering x goes left)

	private ArrayList<Integer[]> legalMoves;
	private Board currentBoard;
	private Piece currentPiece;
	private int pieceX, pieceY;

	public PointVisitor(Board startState) {
		currentBoard = startState;
	}

	/**
	 * Resets the possible legal moves for the piece and updates the state of the board and piece
	 */
	public void init(Piece piece) {
		legalMoves = new ArrayList<Integer[]>();
		currentPiece = piece;
		pieceX = currentPiece.getX();
		pieceY = currentPiece.getY();
	}

	/**
	 * Update the board whenever a move has been made
	 * @param newBoard The new state of the board
	 */
	public void updateBoard(Board newBoard) {
		currentBoard = newBoard;
	}

	/**
	 * Returns true if the specified square exists (that is, is an existing point on the board)
	 * @param x The horizontal coordinate of the specified square
	 * @param y The vertical coordinate of the specified square
	 * @return Boolean True if the piece is in range, false otherwise
	 */
	public Boolean isInBoard(int x, int y) {
		int[] standardBounds = {-1, 11};
		return isInBound(x,y, standardBounds, standardBounds);
	}

	/**
	 * Returns true if the specified square actually exists and false if it is out of bounds
	 * @param x The horizontal coordinate of the specified square
	 * @param y The vertical coordinate of the specified square
	 * @param topLeft The min and max values of x (exclusive)
	 * @param bottomRight The min and max values of y (exclusive)
	 * @return Boolean True if the piece is in range, false otherwise
	 */
	public Boolean isInBound(int x, int y, int[] xBounds, int[] yBounds) {
		Boolean xInRange = (x > xBounds[0] && x < xBounds[1]);
		Boolean yInRange = (y > yBounds[0] && y < yBounds[1]);
		return xInRange && yInRange;
	}

	/**
	 * Returns true if the specified square is empty and false if it isn't
	 * @param x The horizontal coordinate of the specified square
	 * @param y The vertical coordinate of the specified square
	 * @return Boolean
	 */
	public Boolean isEmpty(int x, int y) {
		return (currentBoard.getPiece(x, y) == null);
	}

	/**
	 * Returns true if the specifed square has an opponent piece
	 * @param piece The piece being moved
	 * @param x The horizontal coordinate of the specified square
	 * @param y The vertical coordinate of the specified square
	 * @return Boolean
	 */
	public Boolean isEdible(int x, int y) {
		Piece attackedPiece = currentBoard.getPiece(x,y);
		return (currentPiece.isBlack() != attackedPiece.isBlack());
	}


	// TODO: this is broken
	/**
	 * Returns true if the selected piece is the only piece between the two generals, false otherwise.
	 * @return Boolean
	 */
	public Boolean exposesGeneral(int newX, int newY) {
		int generalX = currentBoard.getBlackGeneralX();
		int numberOfPieces = 0;

		boolean generalsInSameColumn = currentBoard.generalsAligned();
		boolean currentPieceInSameColumn = generalX == pieceX;
		boolean movingHorizontally = pieceX != newX;

		boolean isGeneral = currentPiece.getType() == "General";
		if(isGeneral) {
			currentPieceInSameColumn = true;
			// This only matters if they move horizontally into the column of the enemy
			// Or if the general removes a piece in front of them
			if(movingHorizontally) {
				// Simulate general moving and check if in same column as enemy
				int blackGeneralX = currentBoard.getBlackGeneralX();
				int redGeneralX = currentBoard.getRedGeneralX();
				generalsInSameColumn = currentPiece.isBlack()? (newX == redGeneralX) : (newX == blackGeneralX);
				generalX = newX;
				numberOfPieces = 1; // Offset the fact that it's the general being moved
			} else {
				// Simulate the piece in front being eaten
				if(currentBoard.getPiece(newX, newY) != null) {
					numberOfPieces = 0;
				} else {
					numberOfPieces = 1;
				}
			}
		}
		if(generalsInSameColumn && currentPieceInSameColumn && movingHorizontally) {
			int blackGeneralY = currentBoard.getBlackGeneralY();
			int redGeneralY = currentBoard.getRedGeneralY();

			for(int y = blackGeneralY + 1; y < redGeneralY - 1; y++) {
				if(currentBoard.getPiece(generalX, y) != null) {
					numberOfPieces++;
				}
			}
			return numberOfPieces <= 1;
		}
		return false;
	}

	/**
	 * Add a specified square to the list of legal moves if the piece, upon moving to said square:
	 * <ul>
	 * 		<li>Is not off the board (i.e. it stays within bounds)</li>
	 * 		<li>Does not put their general in check</li>
	 * </ul>
	 * Upon moving to said square at least one of the following must be true:
	 * 	<ul>
	 * 		<li>The current piece is not on top of a friendly piec</li>
	 * 		<li>The current piece is on top of an enemy piece</li>
	 * 	</ul>
	 * 
	 * @param piece The piece being moved
	 * @param x The horizontal coordinate of the specified square
	 * @param y The vertical coordinate of the specified square
	 */
	public void addIfLegal(int x, int y) {
		int[] standardBounds = {-1, 11};
		addIfLegal(x, y, standardBounds, standardBounds);
	}

	/**
	 * Check if a piece is legal, but only if the move is restricted to the castle
	 * 
	 * @param piece The piece being moved
	 * @param x The horizontal coordinate of the specified square
	 * @param y The vertical coordinate of the specified square
	 */
	public void addIfLegalCastle(int x, int y) {
		int[] xBounds = {2,8};
		int[] yBounds = {7,11};
		if(currentPiece.isBlack()) { // Castle on different side
			yBounds[0] = -1;
			yBounds[1] = 3;
		}
		addIfLegal(x, y, xBounds, yBounds);
	}
	
	/**
	 * River variant
	 * @param x
	 * @param y
	 */
	public void addIfLegalRiver(int x, int y) {
		int[] xBounds = {-1,11};
		int[] yBounds = {5,11};
		if(currentPiece.isBlack()) { // Castle on top side
			yBounds[0] = -1;
			yBounds[1] = 5;
		}
		addIfLegal(x, y, xBounds, yBounds);
	}


	/**
	 * Add a specified square to the list of legal moves if the piece, upon moving to said square:
	 * <ul>
	 * 		<li>Is within bounds (either not off the board or not outside the castle)</li>
	 * 		<li>Does not put their general in check</li>
	 * </ul>
	 * Upon moving to said square at least one of the following must be true:
	 * 	<ul>
	 * 		<li>The current piece is not on top of a friendly piec</li>
	 * 		<li>The current piece is on top of an enemy piece</li>
	 * 	</ul>
	 * 
	 * @param piece The piece being moved
	 * @param x The horizontal coordinate of the specified square
	 * @param y The vertical coordinate of the specified square
	 * @param topLeft The min and max values of x (exclusive)
	 * @param bottomRight The min and max values of y (exclusive)
	 */
	public void addIfLegal(int x, int y, int[] xBounds, int[] yBounds) {
		if(y==5) {
			int startY = currentPiece.getY();
			if(startY < 5) { // Going downwards
				y++;
			} else {
				y--;
			}
		}
		
		Integer[] position = {x, y};

		// Normally isEdible shouldn't ever throw an outOfBounds error
		// Because isEmpty will be true before that happens
		if(!legalMoves.contains(position) && isInBound(x,y, xBounds, yBounds) && !exposesGeneral(x,y) && (isEmpty(x,y) || isEdible(x,y)) ) {
			addLegal(x,y);
		}
	}

	/**
	 * Adds the specified square to the list of legal moves
	 * @param x The horizontal coordinate of the specified square
	 * @param y The vertical coordinate of the specified square
	 */
	public void addLegal(int x, int y) {
		Integer[] legalPosition = {x, y};
		legalMoves.add(legalPosition);
	}

	/**
	 * Checks if the squares immediately left and right of the specified square is empty.
	 * If it is, it adds the square to the list of legal moves
	 * @param piece The piece we're moving
	 * @param x The horizontal coordinate of the specified square
	 * @param y The vertical coordinate of the specified square
	 */
	public void checkLeftAndRight(int x, int y) {
		addIfLegal(x+1, y);
		addIfLegal(x-1, y);
	}

	public int locateEnemyGeneral(Piece currentPiece) {
		int enemyY;
		if(currentPiece.isBlack()) {
			enemyY = currentBoard.getRedGeneralY();
		} else {
			enemyY = currentBoard.getBlackGeneralY();
		}

		// We don't care about the enemy general if they're out of reach
		if(Math.abs(enemyY - currentPiece.getY()) <= 1) {

		}
		return 0;
	}

	@Override
	public ArrayList<Integer[]> visit(Canon piece) {
		init(piece);

		int posY = pieceY;

		// Checking vertical movement //

		// Java 'reads' left to right, therefore posY -= 1 will be the first thing to be done
		// Therefore isUnoccupied's first call will be with parameters (pieceX, pieceY-1)
		// And will stop once posY is less than 0, avoiding an outOfBoundsError
		// Because -1 >= 0 will be false before the isEmpty call
		while(--posY >= 0 && isEmpty(pieceX, posY)) {
			addLegal(pieceX, posY);
		}
		// Cannon has extra move: it can only eat hopping over a piece
		while(--posY >= 0 && isEmpty(pieceX, posY)) { }
		addIfLegal(pieceX, posY);

		posY = pieceY;
		while(++posY <= 10 && isEmpty(pieceX, posY)) {
			addLegal(pieceX, posY);
		}
		while(++posY <= 10 && isEmpty(pieceX, posY)) { }
		addIfLegal(pieceX, posY);

		// Checking horizontal movement //
		int posX = pieceX;
		while(--posX >= 0 && isEmpty(posX, pieceY)) {
			addLegal(posX, pieceY);
		}
		while(--posX >= 0 && isEmpty(posX, pieceY)) { }
		addIfLegal(posX, pieceY);

		posX = pieceX;
		while(++posX <= 10 && isEmpty(posX, pieceY)) {
			addLegal(posX, pieceY);
		}
		while(++posX <= 10 && isEmpty(posX, pieceY)) { }
		addIfLegal(posX, pieceY);

		return legalMoves;
	}

	@Override
	public ArrayList<Integer[]> visit(Chariot piece) {
		init(piece);

		// Checking vertical movement
		int posY = pieceY;
		while(--posY >= 0 && isEmpty(pieceX, posY)) {
			addLegal(pieceX, posY);
		}
		addIfLegal(pieceX, posY); // We should also be able to eat the opponent

		posY = pieceY;
		while(++posY <= 10 && isEmpty(pieceX, posY)) {
			addLegal(pieceX, posY);
		}
		addIfLegal(pieceX, posY);

		// Checking horizontal movement
		int posX = pieceX;
		while(--posX >= 0 && isEmpty(posX, pieceY)) {
			addLegal(posX, pieceY);
		}
		addIfLegal(posX, pieceY);

		posX = pieceX;
		while(++posX <= 10 && isEmpty(posX, pieceY)) {
			addLegal(posX, pieceY);
		}
		addIfLegal(posX, pieceY);

		return legalMoves;
	}

	@Override
	public ArrayList<Integer[]> visit(Elephant piece) {
		init(piece);

		if(isInBoard(pieceX+1, pieceY+1) && isEmpty(pieceX+1, pieceY+1)) {
			addIfLegalRiver(pieceX+2, pieceY+2);
		}

		if(isInBoard(pieceX+1, pieceY-1) && isEmpty(pieceX+1, pieceY-1)) {
			addIfLegalRiver(pieceX+2, pieceY-2);
		}

		if(isInBoard(pieceX-1, pieceY+1) && isEmpty(pieceX-1, pieceY+1)) {
			addIfLegalRiver(pieceX-2, pieceY+2);
		}

		if(isInBoard(pieceX-1, pieceY-1) && isEmpty(pieceX-1, pieceY-1)) {
			addIfLegalRiver(pieceX-2, pieceY-2);
		}
		
		return legalMoves;
	}

	@Override
	public ArrayList<Integer[]> visit(General piece) {
		init(piece);

		addIfLegalCastle(pieceX, pieceY+1);
		addIfLegalCastle(pieceX, pieceY-1);
		addIfLegalCastle(pieceX+1, pieceY);
		addIfLegalCastle(pieceX-1, pieceY);
		return legalMoves;
	}

	@Override
	public ArrayList<Integer[]> visit(Guard piece) {
		init(piece);
		
		addIfLegalCastle(pieceX+1, pieceY+1);
		addIfLegalCastle(pieceX+1, pieceY-1);
		addIfLegalCastle(pieceX-1, pieceY+1);
		addIfLegalCastle(pieceX-1, pieceY-1);
		return legalMoves;
	}

	@Override
	public ArrayList<Integer[]> visit(Horse piece) {
		init(piece);

		// Since a horse moves straight then diagonally, check whether it can first move straight
		if(isInBoard(pieceX+1, pieceY) && isEmpty(pieceX+1, pieceY)) { //right
			int tempX = pieceX+2;
			addIfLegal(tempX, pieceY+1);
			addIfLegal(tempX, pieceY-1);
		}

		if(isInBoard(pieceX-1, pieceY) && isEmpty(pieceX-1, pieceY)) { //left
			int tempX = pieceX-2;
			addIfLegal(tempX, pieceY+1);
			addIfLegal(tempX, pieceY-1);
		}

		int crossRiverY = (pieceY+1 == 5)? pieceY+1 : pieceY;
		if(isInBoard(pieceX, crossRiverY+1) && isEmpty(pieceX, crossRiverY+1)) { //down
			int tempY = crossRiverY+2;
			checkLeftAndRight(pieceX, tempY);
		}

		crossRiverY = (pieceY-1 == 5)? pieceY-1 : pieceY;
		if(isInBoard(pieceX, crossRiverY-1) && isEmpty(pieceX, crossRiverY-1)) { //up
			int tempY = crossRiverY-2;
			checkLeftAndRight(pieceX, tempY);
		}

		return legalMoves;
	}

	@Override
	public ArrayList<Integer[]> visit(Soldier piece) {
		init(piece);

		if(piece.isBlack()) {
			addIfLegal(pieceX, pieceY+1);
			if(pieceY > 5) { // We're on Red's side, so we can move sideways
				checkLeftAndRight(pieceX, pieceY);
			}
		} else {
			addIfLegal(pieceX, pieceY-1);
			if(pieceY < 5) { // We're on Black's side, so we can move sideways
				checkLeftAndRight(pieceX, pieceY);
			}
		}
		return legalMoves;
	}

}
