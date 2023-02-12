package game;

import java.util.ArrayList;

import logic.Move;

public class NotationHistory {
	private ArrayList<String> pastMoves = new ArrayList<String>();
	private int pastMovesSize = 0;
	
	public NotationHistory() {}
	
	public void addNotation(String movesThisTurn) {
		pastMoves.add(movesThisTurn);
		pastMovesSize += 1;
	}

	public void updateNotation(Move mostRecentMove, Piece pieceMoved) {
		String pieceName = pieceMoved.getType().substring(0, 0).toUpperCase();
		
		// World Xiangqi Format
		int originX = mostRecentMove.getOriginX();
		int finalX = mostRecentMove.getFinalX();
		
		String fileLocation = Integer.toString(originX);
		String movement, fileTarget;
		if(originX == finalX) {
			if(originX > finalX) {
				movement = "+";
			}
			else {
				movement = "-";
			}
			fileTarget = Integer.toString(Math.abs(finalX-originX)); //Abs in case moved backwards
		}
		else {
			movement = ".";
			fileTarget = Integer.toString(finalX);
		}
		String movesThisTurnWXF = pieceName + fileLocation + movement + fileTarget;
		addNotation(movesThisTurnWXF);
	}
	
	public ArrayList<String> getPastMoves() {
		return pastMoves;
	}
	
	public int getPastMovesSize() {
		return pastMovesSize;
	}
}
