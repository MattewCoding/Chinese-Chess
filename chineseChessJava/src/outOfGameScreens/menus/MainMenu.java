package outOfGameScreens.menus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import game.GameUpdater;
import outOfGameScreens.ScreenParameters;

/**
 * The main menu of the program, where the user can access the submenus
 * @author Yang Mattew
 *
 */
public class MainMenu extends AbstractMenu  {
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private JFrame mainScreen;
	private JComboBox<String> playerComboBox;
	private JComboBox<String> themeComboBox;
	private JComboBox<String> timeComboBox;
	private String player1name= null;
	private String player2name = "Computer";

	public MainMenu(JFrame menuScreen) {
		JPanel buttonPanel = new JPanel(new GridLayout(0,1,0,20));
		buttonPanel.setBackground(boardColor);



		mainScreen=menuScreen;

		mainScreen.setBackground(boardColor);


		// Add play button that shows the welcome panel
		JButton playButton = new JButton("Jouer");
		JButton optionButton = new JButton("Options");
		JButton tutorialButton = new JButton("Apprendre");
		JButton stratsButton = new JButton("Strategies");
		JButton notationButton = new JButton("Notation");
		JButton quitButton = new JButton("Quit");
		JButton[] menuButtons = {playButton, optionButton, tutorialButton, stratsButton, notationButton, quitButton};

		playButton.addActionListener(new PlayButtonListener());
		optionButton.addActionListener(new OptionButtonListener());
		tutorialButton.addActionListener(new TutorialButtonListener());
		stratsButton.addActionListener(new StrategyButtonListener());
		notationButton.addActionListener(new NotationButtonListener());
		quitButton.addActionListener(new QuitButtonListener());

		playButton.setMnemonic(KeyEvent.VK_P);
		optionButton.setMnemonic(KeyEvent.VK_O);
		tutorialButton.setMnemonic(KeyEvent.VK_T);
		stratsButton.setMnemonic(KeyEvent.VK_S);
		notationButton.setMnemonic(KeyEvent.VK_N);
		quitButton.setMnemonic(KeyEvent.VK_Q);

		for(JButton button : menuButtons) {
			button.setFont(new Font(button.getFont().toString(), Font.PLAIN, (int)(24*ScreenParameters.XREDUCE) ));
			button.setPreferredSize(new Dimension(300,100));
			buttonPanel.add(button);
			button.setBackground(new Color(226,192,106));
			button.setBorderPainted(false);
		}
		setBackground(boardColor);
		add(buttonPanel);

		int top = 60;
		int left = top;
		int bottom = 2 * top;
		int right = left;
		setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
	}

	public class PlayButtonListener implements ActionListener{
		private JPanel welcomePanel;
		private JFrame welcomeFrame;

		@Override
		public void actionPerformed(ActionEvent e) {
			// Create a new JFrame to hold the welcome panel
			welcomeFrame = new JFrame("Welcome");
			welcomeFrame.getContentPane().add(createWelcomePanel());
			welcomeFrame.pack();
			welcomeFrame.setLocationRelativeTo(null);
			welcomeFrame.setVisible(true);


		}

		public JPanel createWelcomePanel() {

			// Initialize welcome panel with a welcome message and options
			welcomePanel = new JPanel(new GridLayout(5, 2, 10, 10));
			welcomePanel.setBackground(boardColor);
			welcomePanel.setPreferredSize(new Dimension(700, 450));

			// Add welcome message
			JLabel welcomeLabel = new JLabel("Hello! Welcome to the game!");
			welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
			welcomeLabel.setForeground(Color.BLACK);
			welcomePanel.add(welcomeLabel);
			welcomePanel.add(new JLabel());

			// Add player options
			JLabel playerLabel = new JLabel("Number of Players:");
			playerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
			welcomePanel.add(playerLabel);

			playerComboBox = new JComboBox<String>(new String[] {"1", "2"});
			playerComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
			welcomePanel.add(playerComboBox);

			// Add time options
			JLabel timeLabel = new JLabel("Time of Game:");
			timeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
			welcomePanel.add(timeLabel);

			timeComboBox = new JComboBox<String>(new String[] {"05", "10", "15", "20", "30"});
			timeComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
			welcomePanel.add(timeComboBox);

			// Add theme options
			JLabel themeLabel = new JLabel("Theme:");
			themeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
			welcomePanel.add(themeLabel);

			themeComboBox = new JComboBox<String>(new String[] {"chinese", "english"});
			themeComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
			welcomePanel.add(themeComboBox);

			// Add start button
			JButton startButton = new JButton("Start");
			welcomePanel.add(new JLabel());
			welcomePanel.add(startButton);
			startButton.addActionListener(new StartButtonListener());
			return welcomePanel;
		}
		
		public class StartButtonListener implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				welcomeFrame.dispose();
				
				// Get selected options
				int numberOfPlayers = Integer.parseInt((String)playerComboBox.getSelectedItem());
				if (player1name == null) {
					if(numberOfPlayers == 1) {
						player1name = JOptionPane.showInputDialog("Enter Player 1 Name:");

					} else {
						player1name = JOptionPane.showInputDialog("Enter Player 1 Name:");
						player2name = JOptionPane.showInputDialog("Enter Player 2 Name:");
					}

					String time = (String) timeComboBox.getSelectedItem();
					String theme = (String) themeComboBox.getSelectedItem();

					GameUpdater chessBoard = new GameUpdater(mainScreen, player1name, player2name, time, theme);
					mainScreen.setContentPane(chessBoard);
					mainScreen.revalidate();
				}
			}
		}
	}






	public class OptionButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			OptionsMenu options = new OptionsMenu(mainScreen);
			mainScreen.setContentPane(options.getSplitPane());
			mainScreen.revalidate();
		}

	}

	public class TutorialButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			SubMenu tutorialMenu = new SubMenu("menu"+ScreenParameters.PATHSEP+"How to Play.txt", mainScreen);
			mainScreen.setContentPane(tutorialMenu.getSplitPane());
			mainScreen.revalidate();

		}

	}

	public class StrategyButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			SubMenu strategyMenu = new SubMenu("menu"+ScreenParameters.PATHSEP+"Strategies.txt", mainScreen);
			mainScreen.setContentPane(strategyMenu.getSplitPane());
			mainScreen.revalidate();

		}

	}

	public class NotationButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			SubMenu notationMenu = new SubMenu("menu"+ScreenParameters.PATHSEP+"Notation.txt", mainScreen);
			mainScreen.setContentPane(notationMenu.getSplitPane());
			mainScreen.revalidate();

		}

	}

	public class QuitButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			mainScreen.dispose();

		}

	}

}
