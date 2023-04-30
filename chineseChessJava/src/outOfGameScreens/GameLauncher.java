package outOfGameScreens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
        setLayout(new BorderLayout());
        getContentPane().removeAll();
		
		//This code tests the menu
		MainMenu mainMenu = new MainMenu(this);
		getContentPane().add(mainMenu.getButtonPanel());
		getContentPane().add(mainMenu);
		
		/* This part is to make sure the frame shows up */
		//Frame settings
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		pack();
		
		// Have program fill up screen
		setMinimumSize(new Dimension(ScreenParam.SCREENWIDTH, ScreenParam.SCREENHEIGHT));
		setVisible(true);
	}
	
}
