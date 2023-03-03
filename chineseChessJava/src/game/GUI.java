package game;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import outOfGameScreens.ScreenParameters;

/**
 * The game's main GUI
 * 
 * @author YANG Mattew, Nasro Rona
 *
 */
public class GUI extends JPanel implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private Dashboard dashboard = new Dashboard();
	private boolean run = false;
	
	public GUI(){

		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, dashboard);
		dashboard.repaint();
		
		Thread chronoThread = new Thread(this);
		chronoThread.start();
		run = true;
	}
	
	@Override
	public void run() {
		
		// Every 40ms, check if anything has been clicked
		while(run) {
			try {
				Thread.sleep(ScreenParameters.SLEEPAMOUNT);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			//System.out.println(mouseClickedPiece + " " + mouseMovingPiece);
			
			// Ensure that the game is not stopped during the iteration.
			if (run) {
				dashboard.checkPieces();
				dashboard.repaint();

			}
		}
	}
}