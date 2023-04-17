package outOfGameScreens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.Logger;

import log.LoggerUtility;
import outOfGameScreens.menus.MainMenu;
import outOfGameScreens.menus.SubMenu;

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
		
		//This code tests the menu
		MainMenu mainMenu = new MainMenu(this);
		add(mainMenu.getButtonPanel());
		add(mainMenu);
		
		/* This part is to make sure the frame shows up */
		//Frame settings
        setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		pack();
		
		// Have program fill up screen
		setMinimumSize(new Dimension(ScreenParameters.SCREENWIDTH, ScreenParameters.SCREENHEIGHT));
		setVisible(true);
	}
	
}
