package game;

import game.pieces.Canon;
import game.pieces.Chariot;
import game.pieces.Elephant;
import game.pieces.General;
import game.pieces.Guard;
import game.pieces.Horse;
import game.pieces.Piece;
import game.pieces.Soldier;
import logic.moveChecking.Move;
import logic.moveChecking.Moving;
import outOfGameScreens.Profile;

public class Board {

	//Columns then rows
	private Piece[][] coords;
	private final int COLUMNS = 11;
	private final int ROWS = 11;
	private int redGeneralX = 4;
    private int redGeneralY = 0;
    private int blackGeneralX = 4;
    private int blackGeneralY = 9;
    
    private static int winner;
    private boolean blackCheck = false; //up is in check
    private boolean redCheck = false; //down is in check
    public static final int PLAYER1_WINS = 1;
    public static final int PLAYER2_WINS = 2;
    public static final int PLAYER1_TIMEOUT_WIN = 3;
    public static final int PLAYER2_TIMEOUT_WIN = 4;
    public static final int DRAW = 0;
    public static final int NA = -1;

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
				coords[5][5+side*(2)] = new Soldier(creatingBlack);
				coords[5][5+side*(5)] = new General(creatingBlack);
			}
			
			coords[5+mirror*(5)][5+side*(2)] = new Soldier(creatingBlack);
			coords[5+mirror*(3)][5+side*(2)] = new Soldier(creatingBlack);
			coords[5+mirror*(4)][5+side*(3)] = new Canon(creatingBlack);

			coords[5+mirror*(5)][5+side*(5)] = new Chariot(creatingBlack);
			coords[5+mirror*(4)][5+side*(5)] = new Horse(creatingBlack);
			coords[5+mirror*(3)][5+side*(5)] = new Elephant(creatingBlack);
			coords[5+mirror*(1)][5+side*(5)] = new Guard(creatingBlack);
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
        Piece curr = this.coords[move.getOriginX()][move.getOriginY()];
        
        this.coords[move.getFinalX()][move.getFinalY()] = curr;
        this.coords[move.getOriginX()][move.getOriginY()]= null;
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
    

    public boolean tryMove(Move move, Profile player, Profile player2) {

        if (new Moving(this,move).isLegal()) {

            Piece curr = this.getCoords(move.getOriginX(),move.getOriginY());
            Piece captured = this.getCoords(move.getFinalX(),move.getFinalY());

            int x = move.getOriginX();
            int y = move.getOriginY();
            int finalX = move.getFinalX();
            int finalY = move.getFinalY();


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
                            winner = PLAYER1_WINS;
//                            System.out.println("##########################CHECK MATE#############################");
//                            System.out.println(player.getName() + "WINS!");
//                            System.out.println("##########################CHECK MATE#############################");
                        }
//                        return true;

                        //the move is legal, now lets see if it's a winning move.
                    } else if (redCheck && curr.isBlack() == true) {
                        if (checkMate(false)) {
                            winner = PLAYER2_WINS;
//                            System.out.println("##########################CHECK MATE#############################");
//                            System.out.println(player.getName() + "WINS!");
//                            System.out.println("##########################CHECK MATE#############################");
                        }
//                        return true;

                        //the move is legal, now lets see if it's a stalemate , i reoved the || separated()
                    } else if (curr.isBlack() == false) {
                        if (checkMate(true)) {
                            winner = DRAW;
//                            System.out.println("##########################STALE MATE#############################");
//                            System.out.println("ITS A DRAW");
//                            System.out.println("##########################STALE MATE#############################");
                        }
//                        return true;
                    } else if (curr.isBlack() == true) {
                        if (checkMate(false)) {
                            winner = DRAW;
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
        this.updateGenerals();
        redCheck = false;
        blackCheck = false;
        for (int x = 0; x < 11; x++) {
            for (int y = 0; y < 11; y++) {
                if (this.getCoords(x, y) != null) {

                    if (!redCheck && this.getCoords(x, y).isBlack() == true) {
                        if (new Moving(this, new Move(x, y, this.getRedGeneralX(), this.getRedGeneralY()), 0).isLegal()) {
                            redCheck = true;
//                            System.out.println("Down is in check");
                        }
                    } else if (!blackCheck && this.getCoords(x, y).isBlack() == false) {
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
        this.updateGenerals();


        //running through every loser piece
        for (int x = 0; x < 11; x++) {
            for (int y = 0; y < 11; y++) {

                if (this.getCoords(x, y) != null && this.getCoords(x, y).isBlack() == loserPlace) {

                    //running through every possible point to generate every possible move
                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 10; j++) {
                            Move tempMove = new Move(x, y, i, j); //generating the temporary move
                            Piece tempCaptured = this.getCoords(i, j);
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

