package outOfGameScreens;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;

import game.GUI;

public class MainMenu extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
	private final int SCREENWIDTH = (int)size.getWidth();
	private final int SCREENHEIGHT = (int)size.getHeight();

	/**
	 * Creates the first scrren you see upon launching the program.
	 * TODO: Create the actual menu screen w/ buttons to play, go to options/records, quit
	 */
	public MainMenu() {
		
		//Board creation
		//GUI chessBoard = new GUI();
		
		//Frame settings
		JFrame frame = new JFrame("Chinese Chess");
		frame.add(new SubMenu("src\\outOfGameScreens\\testMenu.txt"));
		//frame.getContentPane().add(chessBoard);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		
		// Have program fill up screen
		frame.setMinimumSize(new Dimension(SCREENWIDTH, SCREENHEIGHT));

		//Make frame actually show up
		frame.setVisible(true);
	}
}
