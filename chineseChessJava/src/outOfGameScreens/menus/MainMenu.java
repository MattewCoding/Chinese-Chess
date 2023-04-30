package outOfGameScreens.menus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import game.GameUpdater;
import outOfGameScreens.ScreenParam;
import outOfGameScreens.buttons.OptionsBackButton;

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
	private JComboBox<String> playerComboBox;
	private JComboBox<String> themeComboBox;
	private JPanel buttonPanel;
	private Font arialStandard = new Font("Arial", Font.PLAIN, 20);

	private String menuContent = "src"+ScreenParam.PATHSEP+"outofGameScreens"+ScreenParam.PATHSEP+"menuContent"+ScreenParam.PATHSEP;

	/**
	 * Creates the main menu with all of the options to the other menus
	 * @param menuScreen The main window frame
	 */
	public MainMenu(JFrame menuScreen) {
		mainScreen = menuScreen;
		createButtonPanel();
		setOpaque(false);
		setBounds(0,0,ScreenParam.SCREENWIDTH,ScreenParam.SCREENHEIGHT);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		String imgLocation = "src"+ScreenParam.PATHSEP+"images"+ScreenParam.PATHSEP+"logo"+ScreenParam.PATHSEP+"chineseboardlogo.jpeg";
		ImageIcon imageIcon = new ImageIcon(imgLocation);
		Image image = imageIcon.getImage();
		g.drawImage(image, 0, 0, ScreenParam.SCREENWIDTH, ScreenParam.SCREENHEIGHT, this);
	}

	/**
	 * Creates the buttons to access the other menus
	 */
	public void createButtonPanel() {
		int widthButtonPanel = 600;
		int heightButtonPanel = 2*ScreenParam.SCREENHEIGHT/3;

		int xButtonPanel = (ScreenParam.SCREENWIDTH - widthButtonPanel)/2;
		int yButtonPanel = (int) (50 * ScreenParam.YREDUCE);

		buttonPanel = new JPanel(new GridLayout(0,1,0,20));
		buttonPanel.setBounds(xButtonPanel, yButtonPanel, widthButtonPanel, heightButtonPanel);
		buttonPanel.setOpaque(false);

		// Add play button that shows the welcome panel
		JButton playButton = new JButton("Play");
		JButton tutorialButton = new JButton("Learn");
		JButton stratsButton = new JButton("Strategies");
		JButton notationButton = new JButton("Notation");
		JButton quitButton = new JButton("Quit");
		JButton[] menuButtons = {playButton, tutorialButton, stratsButton, notationButton, quitButton};

		playButton.addActionListener(new PlayButtonListener());
		tutorialButton.addActionListener(new TutorialButtonListener());
		stratsButton.addActionListener(new StrategyButtonListener());
		notationButton.addActionListener(new NotationButtonListener());
		quitButton.addActionListener(new QuitButtonListener());

		playButton.setMnemonic(KeyEvent.VK_P);
		tutorialButton.setMnemonic(KeyEvent.VK_T);
		stratsButton.setMnemonic(KeyEvent.VK_S);
		notationButton.setMnemonic(KeyEvent.VK_N);
		quitButton.setMnemonic(KeyEvent.VK_Q);

		for(JButton button : menuButtons) {
			button.setFont(new Font(button.getFont().toString(), Font.PLAIN, (int)(24*ScreenParam.XREDUCE) ));
			button.setPreferredSize(new Dimension(300,100));
			button.setBackground(new Color(226,192,106));
			button.setBorderPainted(false);

			buttonPanel.add(button);
		}
	}

	public JPanel getButtonPanel() {
		return buttonPanel;
	}

	/**
	 * The background image for the main menu
	 * @author Nasro Rona
	 *
	 */
	public class BackgroundImage extends JPanel{

		private static final long serialVersionUID = 1L;
		private Image backgroundImage;

		public BackgroundImage() {
			String imgLocation = "src"+ScreenParam.PATHSEP+"images"+ScreenParam.PATHSEP+"logo"+ScreenParam.PATHSEP+"chineseboardlogo.jpeg";
			backgroundImage = new ImageIcon(imgLocation).getImage();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(backgroundImage, 0, 0, ScreenParam.SCREENWIDTH, ScreenParam.SCREENHEIGHT, null);
		}
	}

	/**
	 * Creates the options menu to select the various options
	 * @author Nasro Rona, Yang Mattew
	 *
	 */
	public class PlayButtonListener implements ActionListener{
		private JPanel welcomePanel;
		private JFrame welcomeFrame;

		@Override
		public void actionPerformed(ActionEvent e) {
			// Create a new JFrame to hold the welcome panel
			welcomeFrame = new JFrame("Welcome");
			welcomeFrame.getContentPane().add(createWelcomePanel());
			welcomeFrame.setResizable(false);

			welcomeFrame.pack();
			welcomeFrame.setLocationRelativeTo(null);
			welcomeFrame.setVisible(true);


		}

		/**
		 * Creates the objects that will be shown in the frame
		 * @return The objects in the frame
		 */
		public JPanel createWelcomePanel() {

			//Initialize welcome panel with a welcome message and options
			welcomePanel = new JPanel(new GridBagLayout());

			welcomePanel.setBackground(boardColor);
			welcomePanel.setPreferredSize(new Dimension(700, 450));
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(10, 10, 10, 10);

			// Load the original icon image
			ImageIcon originalIcon = new ImageIcon("./src/images/logo/chess.png");

			// Create a new icon with a smaller size
			Image scaledImage = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			ImageIcon smallIcon = new ImageIcon(scaledImage);

			// Set the new icon for the welcome message label
			JLabel welcomeLabel = new JLabel("Welcome to the XiangQi chess game!", smallIcon, JLabel.CENTER);

			welcomeLabel.setFont(new Font("Arial", Font.BOLD, 30));
			welcomeLabel.setForeground(Color.BLACK);
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 2;
			welcomePanel.add(welcomeLabel, c);

			c.gridwidth = 1;
			
			// Add player options
			JLabel playerLabel = new JLabel("Number of Players:");
			playerLabel.setFont(arialStandard);
			playerLabel.setForeground(Color.BLACK);
			c.gridx = 0;
			c.gridy = 1;
			welcomePanel.add(playerLabel, c);

			playerComboBox = new JComboBox<String>(new String[] {"1", "2"});
			playerComboBox.setFont(arialStandard);
			playerComboBox.setForeground(Color.BLACK);
			c.gridx = 1;
			c.gridy = 1;
			welcomePanel.add(playerComboBox, c);

			// Add theme options
			JLabel themeLabel = new JLabel("Theme:");
			themeLabel.setFont(arialStandard);
			themeLabel.setForeground(Color.BLACK);
			c.gridx = 0;
			c.gridy = 3;
			welcomePanel.add(themeLabel, c);

			themeComboBox = new JComboBox<String>(new String[] {"Chinese", "English"});
			themeComboBox.setFont(arialStandard);
			themeComboBox.setForeground(Color.BLACK);
			c.gridx = 1;
			c.gridy = 3;
			welcomePanel.add(themeComboBox, c);

			// Add start button
			JButton startButton = new JButton("Start");
			startButton.setFont(new Font("Arial", Font.BOLD, 20));
			startButton.setForeground(Color.BLACK);
			startButton.setFocusPainted(false);
			startButton.setContentAreaFilled(false);
			startButton.setOpaque(true);
			startButton.setBackground(new Color(133, 150, 234));
			c.gridx = 1;
			c.gridy = 4;
			c.anchor = GridBagConstraints.LAST_LINE_END; // align to bottom right
			welcomePanel.add(startButton, c);

			startButton.addActionListener(new StartButtonListener());

			c.gridx = 0;
			c.gridy = 4;
			c.anchor = GridBagConstraints.LAST_LINE_START; // align to bottom left
			welcomePanel.add(new OptionsBackButton(welcomeFrame), c);

			return welcomePanel;
		}

		/**
		 * A custom {@link KeyListener} to limit how much and what can be written in a {@link JTextField}
		 * @author Yang Mattew
		 *
		 */
		public class textLimit implements KeyListener{
			private JTextField textField;
			private int limit;
			private boolean numbersOnly;

			/**
			 * No restriction on what characters can be typed
			 * @param inputter Where we're entering the characters
			 * @param max How many characters we're allowed to write
			 */
			public textLimit(JTextField inputter, int max) {
				this(inputter, max, false);
			}

			/**
			 * Can have restrictions on what characters can be typed
			 * @param inputter Where we're entering the characters
			 * @param max How many characters we're allowed to write
			 * @param numbersOnly Whether or not to only allow numbers
			 */
			public textLimit(JTextField inputter, int max, boolean numbersOnly) {
				textField = inputter;
				limit = max;
				this.numbersOnly = numbersOnly;
			}

			@Override
			public void keyTyped(KeyEvent e) {
				if(textField.getText().length() > limit && textField.getSelectedText() == null) {
					e.consume();
				}
				
				if(numbersOnly) {
					String strTyped = Character.toString(e.getKeyChar());
					boolean isNumber = strTyped.matches("[0-9]+");
					if(!isNumber) {
						e.consume();
					}
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		}

		/**
		 * More options, these ones you can type in
		 * @author Nasro Rona, Yang Mattew
		 *
		 */
		public class StartButtonListener implements ActionListener{
			private final short MAX_COLS = 12;

			private JPanel nameInput = new JPanel(new GridBagLayout());
			private JButton startButton;
			private GridBagConstraints c = new GridBagConstraints();
			
			private JLabel player1;
			private JTextField player1field;
			private JTextField time1Amount;
			
			private JLabel player2;
			private JTextField player2field;
			private JTextField time2Amount;

			@Override
			public void actionPerformed(ActionEvent e) {
				nameInput.setOpaque(true);
				nameInput.setBackground(boardColor);
				
				//welcomeFrame.dispose();
				int numberOfPlayers = Integer.parseInt((String)playerComboBox.getSelectedItem());
				
				c.insets = new Insets(10, 10, 10, 10);
				c.gridwidth = 1;

				// Player 1 name
				c.gridy = 0;
				
				player1 = new JLabel("Enter Player 1's name:");
				player1.setFont(arialStandard);
				player1.setForeground(Color.BLACK);
				c.gridx = 0;
				nameInput.add(player1, c);

				player1field = new JTextField();
				player1field = setStandardNameEntry(player1field);
				c.gridx = 1;
				nameInput.add(player1field, c);
				
				// Player 1's time
				c.gridy = 1;
				
				JLabel timeLabel = new JLabel("Timer for player 1 (minutes):");
				timeLabel.setFont(arialStandard);
				timeLabel.setForeground(Color.BLACK);
				c.gridx = 0;
				nameInput.add(timeLabel, c);

				time1Amount = new JTextField();
				time1Amount = setStandardTimeEntry(time1Amount);
				c.gridx = 1;
				nameInput.add(time1Amount, c);

				String playerName = "the computer";
				if(numberOfPlayers == 2) {
					playerName = "player 2";
					c.gridy = 2;

					player2 = new JLabel("Enter Player 2's name:");
					player2.setFont(arialStandard);
					player2.setForeground(Color.BLACK);
					c.gridx = 0;
					nameInput.add(player2, c);

					player2field = new JTextField();
					player2field = setStandardNameEntry(player2field);
					c.gridx = 1;
					nameInput.add(player2field, c);
				}

				// Player 2's time
				c.gridy = 3;

				timeLabel = new JLabel("Timer for " + playerName + " (minutes):");
				timeLabel.setFont(arialStandard);
				timeLabel.setForeground(Color.BLACK);
				c.gridx = 0;
				nameInput.add(timeLabel, c);

				time2Amount = new JTextField();
				time2Amount = setStandardTimeEntry(time2Amount);
				c.gridx = 1;
				nameInput.add(time2Amount, c);


				startButton = new JButton("Confirm");
				startButton.setFont(new Font("Arial", Font.BOLD, 20));
				startButton.setForeground(Color.BLACK);
				startButton.setFocusPainted(false);
				startButton.setContentAreaFilled(false);
				startButton.setOpaque(true);
				startButton.setBackground(new Color(133, 150, 234));
				c.gridx = 2;
				c.gridy = 4;
				c.anchor = GridBagConstraints.LAST_LINE_END; // align to bottom right
				nameInput.add(startButton, c);
				startButton.addActionListener(new LaunchGame());

				c.gridx = 0;
				c.gridy = 4;
				c.anchor = GridBagConstraints.LAST_LINE_START; // align to bottom left
				nameInput.add(new OptionsBackButton(welcomeFrame), c);

				welcomeFrame.setContentPane(nameInput);
				welcomeFrame.revalidate();
			}
			
			/**
			 * Assign the attributes used over and over again for the player names
			 * @param playerField The {@link JTextField} to set the attributes to
			 * @return playerField 
			 */
			private JTextField setStandardNameEntry(JTextField playerField) {
				playerField.setFont(arialStandard);
				playerField.setForeground(Color.BLACK);
				playerField.addKeyListener(new textLimit(playerField, MAX_COLS));
				playerField.setColumns(MAX_COLS);
				return playerField;
			}

			
			/**
			 * Assign the attributes used over and over again for the player timers
			 * @param playerField The {@link JTextField} to set the attributes to
			 * @return playerField 
			 */
			private JTextField setStandardTimeEntry(JTextField playerTime) {
				playerTime.setFont(arialStandard);
				playerTime.setForeground(Color.BLACK);
				playerTime.setColumns(MAX_COLS);
				playerTime.addKeyListener(new textLimit(playerTime, 1, true));
				return playerTime;
			}

			/**
			 * Code to launch the chess game, past the menus
			 * @author Nasro Rona, Yang Mattew
			 *
			 */
			public class LaunchGame implements ActionListener{

				@Override
				public void actionPerformed(ActionEvent e) {
					String theme = (String) themeComboBox.getSelectedItem();
					String player1name = player1field.getText();
					String time1 = time1Amount.getText();
					if(time1.length() == 1) {
						time1 = "0" + time1;
					}
					
					String player2name = (player2field == null)? "Computer Enemy" : player2field.getText();
					String time2 = time2Amount.getText();
					
					// Check that the user didn't mess up in typing the numbers
				    boolean time1Bad = time1 == null || time1.isEmpty();
				    boolean time2Bad = time2 == null || time2.isEmpty();

					if(!player1name.equals("") && !player2name.equals("") && !time1Bad && !time2Bad) {
						welcomeFrame.dispose();
						GameUpdater chessBoard = new GameUpdater(mainScreen, player1name, player2name, time1, time2, theme);
						mainScreen.getContentPane().removeAll();
						mainScreen.getContentPane().add(chessBoard);
						mainScreen.revalidate();
					} else {
						if(player1name.equals("")) {
							player1.setText("Enter an actual name please.");
						}
						if(player2name.equals("")) {
							player2.setText("Enter an actual name please.");
						}
						if(time1Bad) {
							time1Amount.setText("Invalid");
						}
						if(time2Bad) {
							time2Amount.setText("Invalid");
						}
					}

				}

			}
		}
	}

	public class TutorialButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			SubMenu tutorialMenu = new SubMenu(menuContent+"How to Play.txt", mainScreen);
			mainScreen.getContentPane().removeAll();
			mainScreen.getContentPane().add(tutorialMenu.getSplitPane());
			mainScreen.revalidate();

		}

	}

	public class StrategyButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			SubMenu strategyMenu = new SubMenu(menuContent+"Strategies.txt", mainScreen);
			mainScreen.getContentPane().removeAll();
			mainScreen.getContentPane().add(strategyMenu.getSplitPane());
			mainScreen.revalidate();

		}

	}

	public class NotationButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			SubMenu notationMenu = new SubMenu(menuContent+"Notation.txt", mainScreen);
			mainScreen.getContentPane().removeAll();
			mainScreen.getContentPane().add(notationMenu.getSplitPane());
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
