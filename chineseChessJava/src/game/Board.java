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

public class Board {

	// Creating
	//Columns then rows
	private Piece[][] coords;
	private int mirror;
	public final int COLUMNS = 11;
	public final int ROWS = 11;

	// Processing
	private HashMap<String, Piece> generalPositions = new HashMap<String, Piece>();
	private PointVisitor searchValidMoves;

	// Winning
	private static int winner =-1;
	private boolean blackCheck = false; //up is in check
	private boolean redCheck = false; //down is in check

	// Hashing
	private final int PIECE_TYPES = 8;
	private final HashMap<String, Integer> piece_number_asso = new HashMap<String, Integer>();
	private final long[][][] zobristTable = new long[COLUMNS][ROWS][PIECE_TYPES];
	private long hashValue = 0L;

	private static Logger logDataBoard = LoggerUtility.getLogger(Board.class, "html");

	public Board() {
		initHashMap();
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
				coords[5][5+side*(2)] = new Soldier(onBlackSide, 5, 5+side*(2));
				coords[5][edgeYCoord] = new General(onBlackSide, 5, edgeYCoord);

				generalPositions.put("General-"+sideString, coords[5][edgeYCoord]);
			}

			coords[calcX(5)][5+side*2] = new Soldier(onBlackSide, calcX(5), 5+side*2);
			coords[calcX(3)][5+side*2] = new Soldier(onBlackSide, calcX(3), 5+side*2);
			coords[calcX(4)][5+side*3] = new Canon(onBlackSide, calcX(4), 5+side*3);

			coords[calcX(5)][edgeYCoord] = new Chariot(onBlackSide, calcX(5), edgeYCoord);
			coords[calcX(4)][edgeYCoord] = new Horse(onBlackSide, calcX(4), edgeYCoord);
			coords[calcX(3)][edgeYCoord] = new Elephant(onBlackSide, calcX(3), edgeYCoord);
			coords[calcX(1)][edgeYCoord] = new Guard(onBlackSide, calcX(1), edgeYCoord);
		}
		
		for (int i = 0; i < 8; i++) {
		    for (int j = 0; j < 8; j++) {
		    	String name;
		    	if(coords[i][j] != null) {
		    		name = coords[i][j].getType();
		    	} else {
		    		name = "";
		    	}
		        updateHash(i, j, name);
		    }
		}
	}

	/**
	 * Create the board with the pieces in specific spots
	 * @param presetCoords The current state of the board
	 */
	public Board(Piece[][] presetCoords) {
		initHashMap();
		initZobrist();
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
	
	public void initHashMap() {
		piece_number_asso.put("", 0);
		piece_number_asso.put("Cannon", 1);
		piece_number_asso.put("Chariot", 2);
		piece_number_asso.put("Elephant", 3);
		piece_number_asso.put("General", 4);
		piece_number_asso.put("Guard", 5);
		piece_number_asso.put("Horse", 6);
		piece_number_asso.put("Soldier", 7);
	}

	public Piece[][] getCoords() {
		return coords;
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

	public void setCoords(Piece[][] coords) {
		this.coords = coords;
	}

	public void setPiece(int x, int y, Piece p) {
		coords[x][y] = p;
	}

	/**
	 * Returns whether or not the red general is in check
	 * @return boolean representing whether or not the red general is in check
	 */
	public boolean getRedCheck() {
		return redCheck;
	}
	public void setRedCheck(boolean newRedCheck) {
		redCheck = newRedCheck;
	}

	/**
	 * Returns whether or not the black general is in check
	 * @return boolean representing whether or not the black general is in check
	 */
	public boolean getBlackCheck() {
		return blackCheck;
	}
	public void setBlackCheck(boolean newBlackCheck) {
		blackCheck = newBlackCheck;
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

	public boolean generalsAligned() {
		return getRedGeneralX() == getBlackGeneralX();
	}

	public static int getWinner() {
		return winner;
	}

	public static void setWinner(int winner) {
		Board.winner = winner;
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
	
	public long[][][] getZobristTable() {
		return zobristTable;
	}

	/**
	 * Updates the hash value of the current board state by XORing it
	 * with the Zobrist key for the given piece at the given coordinates.
	 * @param x the x-coordinate of the piece to update
	 * @param y the y-coordinate of the piece to update
	 * @param pieceType the type of piece to update (as an integer code, defined elsewhere)
	 */
	public void updateHash(int x, int y, String pieceType) {
		int pieceTypeNumber = piece_number_asso.get(pieceType);
		hashValue ^= zobristTable[x][y][pieceTypeNumber];
	}

	// Get the current hash value
	public long getHash() {
		return hashValue;
	}
}

