package outOfGameScreens;

import javax.swing.SwingUtilities;

/**
 * 
 * @author YANG Mattew
 *
 */
public class GameLauncher implements Runnable{

	/**
	 * Launches the game
	 * @param args Parameters one can input from the console
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new GameLauncher());
	}
	
	@Override
	public void run() {
		new MainMenu();
	}
}
