package outOfGameScreens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import game.GUI;

public class MainMenu2 extends JPanel implements ListSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MainMenu mainScreen;
	private JFrame menuFrame;

	public MainMenu2(MainMenu menuScreen) {
		JPanel buttonPanel = new JPanel(new GridLayout(0,1,0,20));
		buttonPanel.setBackground(ScreenParameters.BOARDCOLOR);

		mainScreen=menuScreen;
		menuFrame=mainScreen.getFrame();

		mainScreen.getFrame().setBackground(ScreenParameters.BOARDCOLOR);

		JButton playButton = new JButton("Jouer");
		JButton optionButton = new JButton("Options");
		JButton tutorialButton = new JButton("Apprendre");
		JButton stratsButton = new JButton("Strategies");
		JButton notationButton = new JButton("Notation");
		JButton[] menuButtons = {playButton, optionButton, tutorialButton, stratsButton, notationButton};

		playButton.addActionListener(new PlayButtonListener());
		optionButton.addActionListener(new OptionButtonListener());
		tutorialButton.addActionListener(new TutorialButtonListener());
		tutorialButton.addActionListener(new StrategyButtonListener());
		notationButton.addActionListener(new NotationButtonListener());
		
		playButton.setMnemonic(KeyEvent.VK_P);
		optionButton.setMnemonic(KeyEvent.VK_O);
		tutorialButton.setMnemonic(KeyEvent.VK_T);
		notationButton.setMnemonic(KeyEvent.VK_N);

		double xReduce = ScreenParameters.SCREENWIDTH/1600.0;

		for(JButton button : menuButtons) {
			button.setFont(new Font(button.getFont().toString(), Font.PLAIN, (int)(24*xReduce) ));
			button.setPreferredSize(new Dimension(300,100));
			buttonPanel.add(button);
			button.setBackground(new Color(226,192,106));
			button.setBorderPainted(false);
		}
		setBackground(ScreenParameters.BOARDCOLOR);
		add(buttonPanel);
		
        int top = 60;
        int left = top;
        int bottom = 2 * top;
        int right = left;
		setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub

	}

	public class PlayButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			GUI chessBoard = new GUI();
			menuFrame.setContentPane(chessBoard);
			menuFrame.revalidate();
		}

	}

	public class OptionButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
		}

	}

	public class TutorialButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			SubMenu tutorialMenu = new SubMenu("menu/How to Play.txt", mainScreen);
			menuFrame.setContentPane(tutorialMenu.getSplitPane());
			menuFrame.revalidate();

		}

	}

	public class StrategyButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			SubMenu strategyMenu = new SubMenu("menu/Strategies.txt", mainScreen);
			menuFrame.setContentPane(strategyMenu.getSplitPane());
			menuFrame.revalidate();

		}

	}

	public class NotationButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			SubMenu notationMenu = new SubMenu("menu/Notation.txt", mainScreen);
			menuFrame.setContentPane(notationMenu.getSplitPane());
			menuFrame.revalidate();

		}

	}

}
