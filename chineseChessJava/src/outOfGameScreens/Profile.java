package outOfGameScreens;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import game.pieces.Piece;
import logic.TimerListener;

public class Profile {
	private String id;
	private int score;
	private boolean place;
	private Color color;
	private ArrayList<Piece> piecesCaptured;
	private boolean checkmateStatus;
	private BufferedWriter sw;
	private BufferedReader sr, br;
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

	public void stringWriter() {
		String line = null;
		try {
			br = new BufferedReader(new FileReader("Scores.txt"));
			String lineToRemove = String.valueOf(score);
			while ((line = br.readLine()) != null) {
				if (line == id) {
					line.replace(br.readLine(), lineToRemove);
				}
			}
			sw.write(id);
			sw.write("\n" + score);
			sw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String readerScore(String name) {
		String Line;
		String result = null;
		try {
			sr = new BufferedReader(new FileReader("Scores.txt"));

			while ((Line = sr.readLine()) != null) {
				if (Line == this.id) {
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

