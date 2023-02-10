package game;

import game1.Move;

public class Board {

	//Columns then rows
	private Piece[][] coords;
	private final int COLUMNS = 11;
	private final int ROWS = 11;
	private int redGeneralX = 4;
    private int redGeneralY = 0;
    private int blackGeneralX = 4;
    private int blackGeneralY = 9;

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

	public void setCoords(Piece[][] coords) {
		this.coords = coords;
	}

	public Piece getCoords(int x, int y) {
		return coords[x][y];
	}
	
	
	
	public void doMove(Move move) {
        Piece curr = this.coords[move.getOriginY()][move.getOriginX()];
        
        this.coords[move.getFinalY()][move.getFinalX()] = curr;
        this.coords[move.getOriginY()][move.getOriginX()]= null;
    }


    /**
     * Physically undoes a move by altering the state of the pieces
     * Not for use in the undo button. It used for move testing reasons.
     *
     * @param move the current move
     * @param captured The piece that was previously captured, so that it can be restored
     */
    public void undoMove(Move move, Piece captured) {
        Piece curr = getCoords(move.getFinalX(), move.getFinalY());
        coords[move.getOriginX()][ move.getOriginY()] = curr;
        getCoords(move.getFinalX(), move.getFinalY()).Capture();
        //System.out.print(" Illegal Move");
    }
    
    
    public void updateGenerals() {
        //finds location of generals

        for (int x = 3; x < 8; x++) {
            for (int y = 0; y < 3; y++) {
                Piece curr = getCoords(x, y);
                if (curr != null && curr.toString().equals("General")) {
                    this.redGeneralX = x;
                    this.redGeneralY = y;
                }

            }

            for (int y = 8; y < 11; y++) {
                Piece curr = getCoords(x, y);
                if (curr != null && curr.toString().equals("General")) {
                	this.blackGeneralX = x;
                	this.blackGeneralY = y;
                }
            }
        }
    }
	
	
	
	

	public int getRedGeneralX() {
		return redGeneralX;
	}

	public int getRedGeneralY() {
		return redGeneralY;
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

	public int getBlackGeneralX() {
		return blackGeneralX;
	}


	public int getBlackGeneralY() {
		return blackGeneralY;
	}
}

