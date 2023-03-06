package logic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.Timer;

import outOfGameScreens.ScreenParameters;

/**
 * Timer to keep track of time elapsed and update timer GUI (*going to be modified*)
 * @author Rona Nasro
 *
 */

public class TimerListener extends JComponent{

	Timer timer;
	private long pausedTime;
	private long startTime;
	private String elapsedTime;
	
	private int fullMinutes, fullSeconds;

	public void setElapsedTime(String elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public TimerListener(Boolean isRed){
		startTime = System.currentTimeMillis();

		timer = new Timer(1, new TimerActionListener());
		timer.start();
		
		// In case the players have different times
		elapsedTime = isRed? ScreenParameters.getRedTime() : ScreenParameters.getBlackTime();
		fullMinutes = Integer.parseInt(elapsedTime.substring(0, 2));
		fullSeconds = Integer.parseInt(elapsedTime.substring(3, 5));

	}

	public String getElapsedTime() {
		return elapsedTime;
	}

	public class TimerActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			long elapsed = System.currentTimeMillis() - startTime;
			int hours = (int) (elapsed / 3600000);
			int minutes = (int) ((elapsed - hours * 3600000) / 60000);
			int seconds = (int) ((elapsed - hours * 3600000 - minutes * 60000) / 1000);
			int milliseconds = (int) elapsed%1000; // Only works bc we're measuring in milliseconds

			// Basically implementing subtraction on mod 60
			seconds = fullSeconds - seconds;
			minutes = (seconds<0)? fullMinutes - minutes - 1: fullMinutes - minutes;
			seconds = (seconds<0)? seconds + 60 : seconds;
			milliseconds = 1000 - milliseconds;
			elapsedTime = String.format("%02d:%02d:%03d", minutes, seconds, milliseconds);
			repaint();
		}
	}

	public void stop() {
		pausedTime = System.currentTimeMillis();
		timer.stop();
		// TODO Auto-generated method stub

	}

	public void start() {
		long elapsedPausedTime = System.currentTimeMillis() - pausedTime;
		startTime += elapsedPausedTime;
		timer.start();
		// TODO Auto-generated method stub

	}
}

