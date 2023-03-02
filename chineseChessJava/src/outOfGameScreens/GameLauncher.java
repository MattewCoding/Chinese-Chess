package outOfGameScreens;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GameLauncher extends JPanel implements Runnable{

	/**
	 * Launches the game
	 * @param args Parameters one can input from the console
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new GameLauncher());
	}
	
	@Override
	public void run() {
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JFrame frame;

	/**
	 * Creates the first screen you see upon launching the program.
	 */
	public GameLauncher() {
		frame = new JFrame("Chinese Chess");
		//GUI chessBoard = new GUI();
		
		/*  This part is for testing the various screens bc we dont have the main menu screen */
		//  This code is for creating the board
		//frame.getContentPane().add(chessBoard);
		
		//  This code tests the sub-menus
		//frame.getContentPane().add(new SubMenu("menu/How to Play.txt", this).getSplitPane());
		
		//This code tests the menu
		frame.getContentPane().add(new MainMenu(this));
		
		/* This part is to make sure the frame shows up */
		//Frame settings
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		
		// Have program fill up screen
		frame.setMinimumSize(new Dimension(ScreenParameters.SCREENWIDTH, ScreenParameters.SCREENHEIGHT));
		frame.setVisible(true);
	}
	
	public JFrame getFrame() {
		return frame;
	}
}
