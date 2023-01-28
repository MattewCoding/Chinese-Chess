package game;

public class Board {

	//Columns then rows
	private Piece[][] coords;
	private final int COLUMNS = 11;
	private final int ROWS = 11;

	public Board() {
		coords = new Piece[COLUMNS][ROWS];

		short quadrant = 0, mirror, side;
		boolean creatingBlack;
		for(;quadrant < 4; quadrant++) {
			creatingBlack = (quadrant>1);
			if(creatingBlack) side = -1;
			else side = 1;
			
			if(quadrant%2 == 0) mirror = 1;
			else {
				mirror = -1;

				// Only needs to be created once for each side
				// Since this if-statement is only true once for both side
				// Why NOT put it here?
				coords[5+side*(2)][5] = new Soldier(creatingBlack);
				coords[5+side*(5)][5] = new General(creatingBlack);
			}
			
			coords[5+side*(2)][5+mirror*(5)] = new Soldier(creatingBlack);
			coords[5+side*(2)][5+mirror*(3)] = new Soldier(creatingBlack);
			coords[5+side*(3)][5+mirror*(4)] = new Canon(creatingBlack);

			coords[5+side*(5)][5+mirror*(5)] = new Chariot(creatingBlack);
			//coords[5+side*(5)][5+mirror*(4)] = new Horse(creatingBlack);
			coords[5+side*(5)][5+mirror*(3)] = new Elephant(creatingBlack);
			coords[5+side*(5)][5+mirror*(1)] = new Guard(creatingBlack);
		}
	}

	public Piece[][] getCoords() {
		return coords;
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
}
