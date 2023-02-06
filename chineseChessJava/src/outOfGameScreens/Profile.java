package outOfGameScreens;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import game.Piece;

public class Profile {
	private String id;
	private int score;
	private ArrayList<Piece> piecesCaptured;
	private boolean checkmateStatus;
	private BufferedWriter sw;
	private BufferedReader sr,br;
	
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
		String line = null;
		try {
            br = new BufferedReader(new FileReader("Scores.txt"));
            String lineToRemove = String.valueOf(score);
            while((line = br.readLine()) != null) {
            	if(line == id){
            		line.replace(br.readLine(), lineToRemove);
            	}
            }
			sw.write(id);
			sw.write("\n"+score);
			sw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
    
    public String readerScore(String name){
    	String Line;
		String result = null;
    	try {
			sr = new BufferedReader(new FileReader("Scores.txt"));
			
			while((Line = sr.readLine()) != null) {
				if(Line == this.id) {
					result = sr.readLine();
				}
			}
			sr.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return result;
		
    }

}
