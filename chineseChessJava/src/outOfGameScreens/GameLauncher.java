package outOfGameScreens;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import outOfGameScreens.menus.MainMenu;

/**
 * Launches the game by opening the main menu and setting up the frame.
 * Principally, this is the JFrame.
 * @author Yang Mattew
 *
 */
public class GameLauncher extends JFrame{

	/**
	 * Launches the game
	 * @param args Parameters one can input from the console
	 */
	public static void main(String[] args) {
		new GameLauncher();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates the first screen you see upon launching the program.
	 */
	public GameLauncher() {
		super("Chinese Chess");
		//GameUpdater chessBoard = new GameUpdater();
		
		/*  This part is for testing the various screens bc we dont have the main menu screen */
		//  This code is for creating the board
		//frame.getContentPane().add(chessBoard);
		
		//  This code tests the sub-menus
		//frame.getContentPane().add(new SubMenu("menu/How to Play.txt", this).getSplitPane());
		
		//This code tests the menu
		getContentPane().add(new MainMenu(this));
		
		/* This part is to make sure the frame shows up */
		//Frame settings
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		pack();
		
		// Have program fill up screen
		setMinimumSize(new Dimension(ScreenParameters.SCREENWIDTH, ScreenParameters.SCREENHEIGHT));
		setVisible(true);
	}
	
}
