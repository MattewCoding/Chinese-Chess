package outOfGameScreens;

import java.awt.Color;

import java.util.ArrayList;

import game.pieces.Piece;
import logic.TimerListener;

/*
 * this class has the basic information about a player such as his color,id, score,timing and winning state
 * @author NASRO Rona
 */

public class Profile {
	private String id;
	private int score;
	private boolean place;
	private Color color;
	private ArrayList<Piece> piecesCaptured;
	private boolean checkmateStatus;
	private TimerListener timer;
   
	
	

	public Profile(String id, int score, boolean place,String time) {
		this.id = id;
		this.score = score;
		this.piecesCaptured = new ArrayList<Piece>();
		this.checkmateStatus = false;
		this.place = place;
		

		timer = new TimerListener(place,time);
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
		this.score = getNumPiecesCaptured() * 5;
		if (checkmateStatus) {
			score += 200;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public boolean isPlace() {
		return place;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public TimerListener getTimer() {
		return timer;
	}

	public void setTimer(TimerListener timer) {
		this.timer = timer;
	}

	// CHECKMATE STATUS xkx
	public boolean getCheckmateStatus() {
		return checkmateStatus;
	}

	/*
	 * checkmateStatus whether the player is in checkmate
	 */

	public void setCheckmateStatus(Boolean checkmateStatus) {
		this.checkmateStatus = checkmateStatus; // comment
	}


	public boolean getPlayerPlace() {
		return place;
	}

	public void setPlace(boolean place) {
		this.place = place;
	}

	// TIMER

	

	/**
	 * Start the timer. Call at start of turn. Update the timer gui.
	 * 
	 * @param panel the timer gui to update
	 */
	

	/**
	 * Stop the timer. Call at end of turn. Add time elapsed during that turn to
	 * previous turn's elapsed time.
	 */
	public void startTurnTimer() {
		timer.start();
		//timeElapsed += timer.getElapsedTime();
	}
	
	public void stopTurnTimer() {
		timer.stop();
		//timeElapsed += timer.getElapsedTime();
	}

	
	

	/**
	 * Get whether the player's timer is running
	 * 
	 * @return the status of the timer, whether its running or not.
	 */
	public boolean isTimerRunning() {
		return timer.getElapsedTime()!="10:00";
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}

