package game;


import java.util.ArrayList;

import game.pieces.Piece;
import logic.moveChecking.Move;

public class NotationHistory {
	private ArrayList<String> pastMoves = new ArrayList<String>();
	private int pastMovesSize = 0;
	private String currentMove;
	
	public NotationHistory() {
	}
	
	public void addNotation(String moveThisTurn, Piece pieceMoved) {
		// We have the space to allow for international chess-style notation
		// (i.e. have a turn displayed on one line instead of two)
		Boolean blackIsPlaying = pieceMoved.isBlack();
		if(blackIsPlaying) {
			pastMoves.add(currentMove+ " " + moveThisTurn);
			pastMovesSize += 1;
		} else {
			currentMove = moveThisTurn;
		}
	}

	public void updateNotation(Move mostRecentMove, Piece pieceMoved) {
		String pieceName = pieceMoved.getType().substring(0, 1).toUpperCase();
		
		// World Xiangqi Format
		int originX = mostRecentMove.getOriginX();
		int originY = mostRecentMove.getOriginY();
		int finalX = mostRecentMove.getFinalX();
		int finalY = mostRecentMove.getFinalY();
		
		String fileLocation = Integer.toString(11-originX); // Counting starts from the right
		String movement, fileTarget;
		if(originX == finalX) {
			if(originY > finalY) { // This is correct but only because y=0 is on top when it should be on the bottom
				movement = "+";
			}
			else {
				movement = "-";
			}
			fileTarget = Integer.toString(Math.abs(finalY-originY)); //Abs in case moved backwards
		}
		else {
			movement = ".";
			fileTarget = Integer.toString(11-finalX);
		}
		String movesThisTurnWXF = pieceName + fileLocation + movement + fileTarget;
		//System.out.println(movesThisTurnWXF);
		addNotation(movesThisTurnWXF, pieceMoved);
	}
	
	public ArrayList<String> getPastMoves() {
		return pastMoves;
	}
	
	public int getPastMovesSize() {
		return pastMovesSize;
	}
}
