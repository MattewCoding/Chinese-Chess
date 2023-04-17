package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
import logic.moveChecking.PointVisitor;
import outOfGameScreens.Profile;
import outOfGameScreens.menus.SubMenu;

public class Board {

	// Creating
	//Columns then rows
	private Piece[][] coords;
	private final int COLUMNS = 11;
	private final int ROWS = 11;
	private int mirror;

	// Processing
    private HashMap<String, Piece> generalPositions = new HashMap<String, Piece>();
    private PointVisitor searchValidMoves;

	// Winning
	private static int winner =-1;
	private boolean blackCheck = false; //up is in check
	private boolean redCheck = false; //down is in check
	
	public static final int PLAYER1_WINS = 1;
	public static final int PLAYER2_WINS = 2;
	public static final int PLAYER1_TIMEOUT_WIN = 3;
	public static final int PLAYER2_TIMEOUT_WIN = 4;
	public static final int DRAW = 0;
	public static final int NA = -1;

	// Hashing
	private final int PIECE_TYPES = 7*2;
    private final long[][][] zobristTable = new long[COLUMNS][ROWS][PIECE_TYPES];
	private long hashValue;
	
	//private static Logger logDataBoard = LoggerUtility.getLogger(SubMenu.class, "html");

	public Board() {
		initZobrist();
		coords = new Piece[COLUMNS][ROWS];

		int quadrant = 0, side, edgeYCoord, pieceId = 0;
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
				coords[5][5+side*(2)] = new Soldier(onBlackSide, 5, 5+side*(2), pieceId++);
				coords[5][edgeYCoord] = new General(onBlackSide, 5, edgeYCoord, pieceId++);

				generalPositions.put("General-"+sideString, coords[5][edgeYCoord]);
			}

			coords[calcX(5)][5+side*2] = new Soldier(onBlackSide, calcX(5), 5+side*2, pieceId++);
			coords[calcX(3)][5+side*2] = new Soldier(onBlackSide, calcX(3), 5+side*2, pieceId++);
			coords[calcX(4)][5+side*3] = new Canon(onBlackSide, calcX(4), 5+side*3, pieceId++);

			coords[calcX(5)][edgeYCoord] = new Chariot(onBlackSide, calcX(5), edgeYCoord, pieceId++);
			coords[calcX(4)][edgeYCoord] = new Horse(onBlackSide, calcX(4), edgeYCoord, pieceId++);
			coords[calcX(3)][edgeYCoord] = new Elephant(onBlackSide, calcX(3), edgeYCoord, pieceId++);
			coords[calcX(1)][edgeYCoord] = new Guard(onBlackSide, calcX(1), edgeYCoord, pieceId++);
		}
	}
	
	/**
	 * Create the board with the pieces in specific spots
	 * @param presetCoords The current state of the board
	 */
	public Board(Piece[][] presetCoords) {
		coords = presetCoords;
		
		for(int x = 0; x < ROWS; x++) {
			for(int y = 0; y < COLUMNS; y++) {
				Piece testPiece = getPiece(x, y);
				if(testPiece != null && testPiece.getType() == "General") {
					String sideString = testPiece.isBlack()? "B" : "R";
					generalPositions.put("General-"+sideString, coords[x][y]);
				}
			}
		}
	}

	public int calcX(int x) {
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
		// Update position in piece, board, and list of pieces
		Piece curr = this.coords[move.getOriginX()][move.getOriginY()];
		int finalX = move.getFinalX();
		int finalY = move.getFinalY();

		curr.movePiece(finalX, finalY);

		this.coords[finalX][finalY] = curr;
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

		curr.movePiece(move.getOriginX(), move.getOriginY());

		coords[move.getOriginX()][ move.getOriginY()] = curr;
		//getPiece(move.getFinalX(), move.getFinalY()).Capture();
		coords[move.getFinalX()][ move.getFinalY()] = captured;
		//System.out.print(" Illegal Move");
	}
    
    
    public Move GenerateMoves(List<Piece> randomPieces){
    	searchValidMoves = new PointVisitor(this);
    	ArrayList<Integer[]> legalMoves = new ArrayList<Integer[]>();
    	Piece movingPiece = null;

    	Random random = new Random();
    	while(legalMoves.size() == 0) { // Make sure the piece can actually move
    		movingPiece = randomPieces.get(random.nextInt(randomPieces.size()));
    		legalMoves = movingPiece.accept(searchValidMoves);
    	}
    	Integer[] legalMove = legalMoves.get(random.nextInt(0,legalMoves.size()));
    	Move move = new Move(movingPiece, movingPiece.getX(),movingPiece.getY(),legalMove[0],legalMove[1]);
    	return move;
    }
    
    /**
     * Creates a list of all the black pieces
     * 
     * @param isBlack Whether we want the black pieces or the red pieces
     * @return The list of black pieces
     */
    public List<Piece> getAllPieces(boolean isBlack) {
        List<Piece> pieces = new ArrayList<Piece>();
        for(int x = 0; x < COLUMNS; x++) {
            for(int y = 0; y < ROWS; y++) {
                Piece piece = getPiece(x, y);
                if(piece != null && (piece.isBlack() == isBlack)) {
                    pieces.add(piece);
                }
            }
        }
        return pieces;
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
	public boolean tryMove(Moving moving, Profile player) {
		Move move = moving.getMove();

		if (moving.isLegal()) {

			Piece curr = this.getPiece(move.getOriginX(),move.getOriginY());
			Piece captured = this.getPiece(move.getFinalX(),move.getFinalY());

			int x = move.getOriginX();
			int y = move.getOriginY();
			int finalX = move.getFinalX();
			int finalY = move.getFinalY();

			if (curr.isBlack() == player.getPlayerPlace()) {
				this.doMove(move);
				testCheck();
				if (curr.isBlack() && blackCheck) {
					System.out.println(" Illegal Move, you're in check");
					this.undoMove(move, captured);
					return false;
				}
				if (!curr.isBlack() && redCheck) {
					System.out.println(" Illegal Move, you're in check");
					this.undoMove(move, captured);
					return false;
				} else {
					//the move is legal, now let's see if it's a winning move.
					if (blackCheck && !curr.isBlack()) {
						if (checkMate(true)) {
							setWinner(PLAYER1_WINS);
						}
	
						//the move is legal, now lets see if it's a winning move.
					} else if (redCheck && curr.isBlack()) {
						if (checkMate(false)) {
							setWinner(PLAYER2_WINS);
						}
	
						//the move is legal, now lets see if it's a stalemate , i recoved the || separated()
					} else if (!curr.isBlack()) {
						if (checkMate(true)) {
							setWinner(DRAW);
						}
					} else if (curr.isBlack()) {
						if (checkMate(false)) {
							setWinner(DRAW);
						}
					}
	
					// if (!checkMate) {   //LEGAL MOVE AND NOT IN CHECKMATE?
					System.out.println("Moved " + curr + " from (" + x + ", " + y + ") to (" + finalX + ", " + finalY + ")");
					if (captured != null) {
						player.addPieceCaptured(captured);
						System.out.println(captured + " Captured!");
						//MoveLogger.addMove(new Move(curr, captured, x, y, finalX, finalY));
					}
					return true;
				}
			} else {
				System.out.println("That's not your piece");
				return false;
			}
		}
		System.out.println("Illegal Move");
		return false;


	}

	/**
	 * Checks if either the red or black general is in check
	 */
	public void testCheck() {
		redCheck = false;
		blackCheck = false;
		
		// loop through all the pieces on the board
		for (int x = 0; x < 11; x++) {
			for (int y = 0; y < 11; y++) {
				// if there is a piece on this square
				if (this.getPiece(x, y) != null) {
					Piece piece = this.getPiece(x, y);
					int generalX = piece.isBlack() ? this.getRedGeneralX() : this.getBlackGeneralX();
					int generalY = piece.isBlack() ? this.getRedGeneralY() : this.getBlackGeneralY();
					
					// check if this piece can move to the opposite color general's position
					if (new Moving(this, new Move(x, y, generalX, generalY), 0).isLegal()) {
						if (piece.isBlack()) {
							redCheck = true;
						} else {
							blackCheck = true;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Returns whether or not the red general is in check
	 * @return boolean representing whether or not the red general is in check
	 */
	public boolean getRedCheck() {
	    return redCheck;
	}

	/**
	 * Returns whether or not the black general is in check
	 * @return boolean representing whether or not the black general is in check
	 */
	public boolean getBlackCheck() {
	    return blackCheck;
	}



	/**
	 * Tests whether or not we are in checkmate
	 * @param isBlack Whose side could be in checkmate
	 * @return Whether or not the specified side is in checkmate
	 */
	public boolean checkMate(boolean isBlack) {
		//updateGenerals();
		//running through every loser piece
		for (int x = 0; x < 11; x++) {
			for (int y = 0; y < 11; y++) {
				if (this.getPiece(x, y) != null && this.getPiece(x, y).isBlack() == isBlack) {
					PointVisitor pointV = new PointVisitor(this);
					Piece currentPiece = this.getPiece(x, y);
					ArrayList<Integer[]> moveSet = currentPiece.accept(pointV);

					for(Integer[] move : moveSet) {
						int i = move[0], j = move[1];
						Move tempMove = new Move(x, y, i, j); //generating the temporary move
						Piece tempCaptured = this.getPiece(i, j);
						
						// Attempt the move
						this.doMove(tempMove); //doing the temporary move
						testCheck(); //updates check status
						
						// If any of these moves were both legal, and result with us not being in check, we aren't in checkmate.
						if (isBlack == false) {
							if (!redCheck) {
								this.undoMove(tempMove, tempCaptured);
								return false;
							}
						}
						if (isBlack == true) {
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

	/**
	 * Returns true if the generals aren't facing each other by counting the obstacles between them if they're in line.
	 * @param move The move attemping to be done
	 * @return Boolean True if they are facing each other
	 */
	public Boolean approveGenerals(Move move) {
		int numberOfPieces=0;

		// Generals can only face each other if they're in the same column and the piece moving is moving out of said column
		// OR if the general moves into the same row as the other general
		boolean pieceCanExposeGeneral = generalsAligned() && (move.getOriginX() != move.getFinalX()) && (move.getOriginX() == getRedGeneralX());
		boolean sameColumn = false;
		Piece currentPiece = getPiece(move.getOriginX(), move.getOriginY());

		if(currentPiece.getType() == "General") {
			// Simulate the move to see if the general would be exposed to the enemy general
			sameColumn = currentPiece.isBlack()? move.getFinalX() == getRedGeneralX() : move.getFinalX() == getBlackGeneralX();
			
			// If the move captures another piece, we ought to note that
			if(move.isVertical() && getPiece(move.getFinalX(), move.getFinalY()) != null) {
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
			for(int y = getBlackGeneralY()+1; y < getRedGeneralY() - 1; y++) {
				if(getPiece(getRedGeneralX(), y) != null) {
					numberOfPieces++;
				}
			}
			// If there's only one, then based on the if-statements it has to be
			// The piece we're moving
			return numberOfPieces > 1;
		}
		return true;
	}
	
    // Initialize the Zobrist table with random values
    public void initZobrist() {
        Random rand = new Random();
        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                for (int piece = 0; piece < PIECE_TYPES; piece++) {
                    zobristTable[x][y][piece] = rand.nextLong();
                }
            }
        }
    }

    // Update the hash value for a piece at the given position
    public void updateHash(int x, int y, int pieceType) {
        hashValue ^= zobristTable[x][y][pieceType];
    }
    
    // Get the current hash value
    public long getHash() {
        return hashValue;
    }

	@Override
	public String toString() {
		// This is a debug method to ensure correct placement
		// of the pieces
		for(int i =0; i<ROWS;i++) {
			for(int j =0;j<COLUMNS;j++) {
				String firstLetter;
				if(coords[j][i] == null) {
					firstLetter = "__";
				}
				else {
					firstLetter = coords[j][i].toString().substring(0, 2);
				}
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
}

