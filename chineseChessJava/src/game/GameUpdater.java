package game;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import outOfGameScreens.ScreenParameters;

/**
 * The game's main GameUpdater, where the updates happen
 * 
 * @author YANG Mattew, Nasro Rona
 *
 */
public class GameUpdater extends JPanel implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private GUIRenamed gUIRenamed = new GUIRenamed();
	private boolean run = false;
	
	public GameUpdater(){

		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, gUIRenamed);
		gUIRenamed.repaint();
		
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
			
			// Ensure that the game is not stopped during the iteration.
			if (run) {
				gUIRenamed.checkPieces();
				gUIRenamed.repaint();

			}
		}
	}
}