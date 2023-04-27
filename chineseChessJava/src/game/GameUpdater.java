package game;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import outOfGameScreens.EndGame;
import outOfGameScreens.ScreenParam;

/**
 * The game's main game updater, where the updates happen
 * 
 * @author YANG Mattew, Nasro Rona
 *
 */
public class GameUpdater extends JPanel implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private GUI gui;
	private boolean run = false;
	private boolean end = false;
	private JFrame mainScreen;
	private EndGame endScreen;
	
	public GameUpdater(JFrame gameScreen, String player1name, String player2name, String time, String theme){
		mainScreen = gameScreen;
		
		gui = new GUI(player1name, player2name, time, theme);
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, gui);
		gui.repaint();
		
		Thread chronoThread = new Thread(this);
		chronoThread.start();
		run = true;
	}
	
	@Override
	public void run() {
		
		// Every 40ms, check if anything has been clicked
		while(run) {
			try {
				Thread.sleep(ScreenParam.SLEEPAMOUNT);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			
			// Ensure that the game is not stopped during the iteration.
			if (run) {
				gui.checkPieces();
				gui.repaint();
				if(gui.hasEnded()) {
					endScreen = new EndGame(Board.getWinner(), gui.getPlayer1(), gui.getPlayer2());
					mainScreen.setContentPane(endScreen.getContentPane());
					mainScreen.revalidate();
					run = false;
					end = true;
				}
			}
		}
		while (end) {
			if(endScreen.closed()) {
				mainScreen.dispose();
				end = false;
			}
		}
	}
}