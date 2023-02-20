package logic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Timer to keep track of time elapsed and update timer GUI (*going to be modified*)
 * @author Rona Nasro
 *
 */

public class TimerListener extends JComponent{

	Timer timer;
	private long startTime;
	private String elapsedTime = "00:00";



	public TimerListener(){


		startTime = System.currentTimeMillis();

		timer = new Timer(1000, new TimerActionListener());
		timer.start();

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
			elapsedTime = String.format("%02d:%02d", minutes, seconds);
			repaint();
		}
	}

	public void stop() {
		timer.stop();
	}


}

