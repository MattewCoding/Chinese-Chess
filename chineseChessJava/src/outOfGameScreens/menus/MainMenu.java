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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import game.GameUpdater;
import outOfGameScreens.ScreenParam;

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
	private JComboBox<String> timeComboBox;
	private JPanel buttonPanel;
	private String player1name= null;
	private String player2name = "Computer";
	
	private String menuContent = "src"+ScreenParam.PATHSEP+"outofGameScreens"+ScreenParam.PATHSEP+"menuContent"+ScreenParam.PATHSEP;

	public MainMenu(JFrame menuScreen) {
		mainScreen=menuScreen;
		createButtonPanel();
		setOpaque(true);
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
		JButton tutorialButton = new JButton("Learn How to Play");
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

			//Initialize welcome panel with a welcome message and options
			welcomePanel = new JPanel(new GridBagLayout());
			//welcomePanel.setBackground(image);

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
			JLabel welcomeLabel = new JLabel(" Welcome to the XiangQi chess game!", smallIcon, JLabel.CENTER);

			welcomeLabel.setFont(new Font("Arial", Font.BOLD, 30));
			welcomeLabel.setForeground(Color.BLACK);
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 2;
			welcomePanel.add(welcomeLabel, c);

			// Add player options
			JLabel playerLabel = new JLabel("Number of Players:");
			playerLabel.setFont(new Font("Arial", Font.PLAIN, 20));
			playerLabel.setForeground(Color.BLACK);
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 1;
			welcomePanel.add(playerLabel, c);

			playerComboBox = new JComboBox<String>(new String[] {"1", "2"});
			playerComboBox.setFont(new Font("Arial", Font.PLAIN, 20));
			playerComboBox.setForeground(Color.BLACK);
			c.gridx = 1;
			c.gridy = 1;
			c.gridwidth = 1;
			welcomePanel.add(playerComboBox, c);

			// Add time options
			JLabel timeLabel = new JLabel("Time of Game:");
			timeLabel.setFont(new Font("Arial", Font.PLAIN, 20));
			timeLabel.setForeground(Color.BLACK);
			c.gridx = 0;
			c.gridy = 2;
			c.gridwidth = 1;
			welcomePanel.add(timeLabel, c);

			timeComboBox = new JComboBox<String>(new String[] {"05", "10", "15", "20", "30"});
			timeComboBox.setFont(new Font("Arial", Font.PLAIN, 20));
			timeComboBox.setForeground(Color.BLACK);
			c.gridx = 1;
			c.gridy = 2;
			c.gridwidth = 1;
			welcomePanel.add(timeComboBox, c);

			// Add theme options
			JLabel themeLabel = new JLabel("Theme:");
			themeLabel.setFont(new Font("Arial", Font.PLAIN, 20));
			themeLabel.setForeground(Color.BLACK);
			c.gridx = 0;
			c.gridy = 3;
			c.gridwidth = 1;
			welcomePanel.add(themeLabel, c);

			themeComboBox = new JComboBox<String>(new String[] {"chinese", "english"});
			themeComboBox.setFont(new Font("Arial", Font.PLAIN, 20));
			themeComboBox.setForeground(Color.BLACK);
			c.gridx = 1;
			c.gridy = 3;
			c.gridwidth = 1;
			welcomePanel.add(themeComboBox, c);

			// Add start button
			JButton startButton = new JButton("Start");
			startButton.setFont(new Font("Arial", Font.BOLD, 20));
			startButton.setForeground(Color.BLACK);
			startButton.setFocusPainted(false);
			startButton.setContentAreaFilled(false);
			startButton.setOpaque(true);
			startButton.setBackground(new Color(63, 81, 181));
			c.gridx = 1;
			c.gridy = 4;
			c.gridwidth = 1;
			c.anchor = GridBagConstraints.LAST_LINE_END; // align to bottom right
			welcomePanel.add(startButton, c);

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

					if(player1name != null && player2name != null) {
						GameUpdater chessBoard = new GameUpdater(mainScreen, player1name, player2name, time, theme);
						mainScreen.setContentPane(chessBoard);
						mainScreen.revalidate();
					}
				}
			}
		}
	}




	public class TutorialButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			SubMenu tutorialMenu = new SubMenu(menuContent+"How to Play.txt", mainScreen);
			mainScreen.setContentPane(tutorialMenu.getSplitPane());
			mainScreen.revalidate();

		}

	}

	public class StrategyButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			SubMenu strategyMenu = new SubMenu(menuContent+"Strategies.txt", mainScreen);
			mainScreen.setContentPane(strategyMenu.getSplitPane());
			mainScreen.revalidate();

		}

	}

	public class NotationButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			SubMenu notationMenu = new SubMenu(menuContent+"Notation.txt", mainScreen);
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
