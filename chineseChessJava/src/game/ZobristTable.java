package game;

import java.util.Random;

/**
 * Singleton class allowing the Zobrist Table to be reused by anyone
 * @author Yang Mattew
 *
 */
public class ZobristTable {
	private final int COLUMNS = 11;
	private final int ROWS = 11;
	private final int PIECE_TYPES = 8;
	private final long[][][] zobristTable = new long[COLUMNS][ROWS][PIECE_TYPES];
	
	private static ZobristTable instance = new ZobristTable();
	
	/**
	 * Create a table associating each piece and position on the board to a random number
	 */
	private ZobristTable() {
		Random rand = new Random();
		for (int x = 0; x < COLUMNS; x++) {
			for (int y = 0; y < ROWS; y++) {
				for (int piece = 0; piece < PIECE_TYPES; piece++) {
					zobristTable[x][y][piece] = rand.nextLong();
				}
			}
		}
	}

	/**
	 * Static method allows users to get the only bit of useful information from this class:
	 * the table containing the number associated to each piece at a position on the board
	 * 
	 * @return A triple nested table of longs
	 */
	public static long[][][] getZobristTable() {
		return instance.zobristTable;
	}

}
