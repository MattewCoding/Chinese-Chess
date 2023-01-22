package chineseChess;

import javax.swing.SwingUtilities;

/**
 * 
 * @author YANG Mattew
 *
 */
public class GameLauncher {

	/**
	 * Launches the game
	 * @param args Parameters one can input from the console
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Menu();
			}
		});
	}
}
