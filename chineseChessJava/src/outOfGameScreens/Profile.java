package outOfGameScreens;

import java.io.StringWriter;
import java.util.ArrayList;

import game.Piece;

public class Profile {
	private String id;
	private int score;
	private ArrayList<Piece> piecesCaptured;
	private boolean checkmateStatus;
	
	public Profile(String id, int score) {
		this.id = id;
		this.score = score;
		this.piecesCaptured = new ArrayList<Piece>();
		this.checkmateStatus = false;
	}
	
	
	public void addPieceCaptured(Piece pieceCaptured) {
        piecesCaptured.add(pieceCaptured);
    }
	
	
	public ArrayList<Piece> getPiecesCaptured() {
        return piecesCaptured;
    }
	
	public int getNumPiecesCaptured() {
        return this.piecesCaptured.size();
    }
	
	
	public void calculateScore() {
		this.score = getNumPiecesCaptured()*5;
		if(checkmateStatus) {
			score += 200;
		}
	}
	
	
	public String getId() {
		return id; 
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
	//CHECKMATE STATUS xkx
    public boolean getCheckmateStatus() {
        return checkmateStatus;
    }

    /*
     * checkmateStatus whether the player is in checkmate
     */
    
    public void setCheckmateStatus(Boolean checkmateStatus) {
        this.checkmateStatus = checkmateStatus; //comment
    }

    
    public void stringWriter() {
        StringWriter sw = new StringWriter();
        
        sw.write("Player's score");
        sw.append(id).append("/").append(String.valueOf(score));
        
        String s = sw.toString();
    }

}
