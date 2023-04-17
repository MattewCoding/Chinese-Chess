package outOfGameScreens;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.border.Border;

import outOfGameScreens.menus.MainMenu;

/*
 * @author Rona Nasro
 */

public class EndGame extends JFrame {

	private static final long serialVersionUID = 1L;
	private Profile winner;
	private Profile loser;
	private boolean Draw = false;

	private JLabel winnerTextLabel;
	
	private JLabel winnerScoreText;
	private JLabel winnerLabel;
	private JLabel winnerScore;

	private JLabel loserScoreText;
	private JLabel loserScore;
	private JLabel loserLabel;
	
	private JLabel timer1Label;
	private JLabel timer2Label;
	private JLabel timer1LabelText;
	private JLabel timer2LabelText;
	
	private JLabel backTextLabel;

	private Container c ;

	private final int textBoxSize = 500;
	private int center = ScreenParameters.SCREENWIDTH/4;

	protected Color boardColor = ScreenParameters.BOARDCOLOR;

	public EndGame(int winnerState,Profile player1, Profile player2){
		int h1 = (int) (70*ScreenParameters.YREDUCE);
		int h2 = (int) (50*ScreenParameters.YREDUCE);
		int h3 = (int) (30*ScreenParameters.YREDUCE);

		if(winnerState== 1) {
			winner = player1;
			loser = player2;
		} else if(winnerState== 2){
			winner = player2;
			loser = player1;
		} else {
			Draw = true;
			winner = player1;
			loser = player2;
		}

		setSize(ScreenParameters.SCREENWIDTH, ScreenParameters.SCREENHEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);

		JPanel panel = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();
		JPanel panel4 = new JPanel();

		panel.setBackground(boardColor);
		panel.setLayout(null);
		panel2.setBackground(boardColor);
		panel2.setLayout(null);
		panel3.setBackground(boardColor);
		panel3.setLayout(null);
		panel4.setBackground(boardColor);
		panel4.addMouseListener(new panel4Listener());
		panel4.setLayout(null);
		
		int panel3Height = ScreenParameters.SCREENHEIGHT/4;
		
		int winPanelHeight = 11*ScreenParameters.SCREENHEIGHT/20;
		int xPosPanel4 = panel3Height+winPanelHeight;
		int panel4Height = ScreenParameters.SCREENHEIGHT - xPosPanel4;
		
		panel.setBounds(0, panel3Height, ScreenParameters.SCREENWIDTH/2, winPanelHeight); // Left screen
		panel2.setBounds(ScreenParameters.SCREENWIDTH/2, panel3Height, ScreenParameters.SCREENWIDTH/2, winPanelHeight); // Right screen
		panel3.setBounds(0, 0, ScreenParameters.SCREENWIDTH, panel3Height); // Top screen
		panel4.setBounds(0, xPosPanel4, ScreenParameters.SCREENWIDTH, panel4Height); // Bottom screen

		Border br = BorderFactory.createLineBorder(Color.black);

		String winText = (winnerState == 0)? "It is a draw" : "Congratulations " + winner.getId() +", you win!";
		String backText = "Back to main menu";
		winnerTextLabel = new JLabel(winText);
		winnerLabel = new JLabel(winner.getId());
		loserLabel = new JLabel(loser.getId());
		backTextLabel = new JLabel(backText);
		
		winnerScoreText = new JLabel("Your Score :");
		loserScoreText = new JLabel("Your Score :");
		winnerScore = new JLabel(String.valueOf(winner.getScore()));
		loserScore = new JLabel(String.valueOf(loser.getScore()));
		timer1Label = new JLabel(winner.getTimer().getElapsedTime());
		timer2Label = new JLabel(loser.getTimer().getElapsedTime());
		timer1LabelText = new JLabel("Timing :");
		timer2LabelText = new JLabel("Timing :");

		//styling
		winnerTextLabel.setFont(new Font("Monsserat", Font.BOLD, h1));
		winnerTextLabel.setForeground(Color.BLACK);
		
		backTextLabel.setFont(new Font("Monsserat", Font.BOLD, h2));
		backTextLabel.setForeground(Color.BLACK);
		
		String fontUsed = winnerTextLabel.getFont().getFontName();
		if(Draw) {
			winnerLabel.setFont(new Font(fontUsed, Font.BOLD, h2));
			winnerLabel.setForeground(Color.BLUE);
			loserLabel.setFont(new Font(fontUsed, Font.BOLD, h2));
			loserLabel.setForeground(Color.BLUE);
		} else {
			winnerLabel.setFont(new Font(fontUsed, Font.BOLD, h2));
			winnerLabel.setForeground(Color.GREEN);
			loserLabel.setFont(new Font(fontUsed, Font.BOLD, h2));
			loserLabel.setForeground(Color.RED);
		}
		winnerScore.setFont(new Font(fontUsed, Font.BOLD, h2));
		loserScore.setFont(new Font(fontUsed, Font.BOLD, h2));
		winnerScoreText.setFont(new Font(fontUsed, Font.CENTER_BASELINE, h2));
		loserScoreText.setFont(new Font(fontUsed, Font.CENTER_BASELINE, h2));
		timer1Label.setFont(new Font(fontUsed, Font.BOLD, h3));
		timer2Label.setFont(new Font(fontUsed, Font.BOLD, h3));
		timer1LabelText.setFont(new Font(fontUsed, Font.BOLD, h3));
		timer2LabelText.setFont(new Font(fontUsed, Font.BOLD, h3));

		//settings Bounds
		int estWinTextSize = winText.length() * h1;
		winnerTextLabel.setBounds(ScreenParameters.SCREENWIDTH/2 - estWinTextSize/4, 0, ScreenParameters.SCREENWIDTH, panel3Height);

		int estBackTextSize = backText.length() * h2;
		backTextLabel.setBounds(ScreenParameters.SCREENWIDTH/2 - estBackTextSize/4, 0, ScreenParameters.SCREENWIDTH, panel4Height);
		
		int scoreTextSize = "Your Score ".length() * h2;
		int timingTextSize = "Timing ".length() * h3;
		int topLabel = (int) (70 * ScreenParameters.YREDUCE);
		int midLabel = (int) (140 * ScreenParameters.YREDUCE);
		int botLabel = (int) (210 * ScreenParameters.YREDUCE);
		int timerTopLabel = (int) (300 * ScreenParameters.YREDUCE);
		int timerBotLabel = (int) (350 * ScreenParameters.YREDUCE);
		
		int winTextSize = winner.getId().length() * h2;
		int winScoreSize = String.valueOf(winner.getScore()).length() * h2;
		winnerLabel.setBounds(center - winTextSize/4, topLabel, textBoxSize, ScreenParameters.SCREENHEIGHT/15);
		winnerScoreText.setBounds(center - scoreTextSize/4, midLabel, textBoxSize, 130);
		winnerScore.setBounds(center - winScoreSize/4, botLabel, textBoxSize, 130);
		
		int loseTextSize = loser.getId().length() * h2;
		int loseScoreSize = String.valueOf(loser.getScore()).length() * h2;
		loserLabel.setBounds(center - loseTextSize/4, topLabel, textBoxSize, ScreenParameters.SCREENHEIGHT/15);
		loserScoreText.setBounds(center - scoreTextSize/4, midLabel, textBoxSize, 130);
		loserScore.setBounds(center - loseScoreSize/4, botLabel, textBoxSize, 130);

		int timer1LabelSize = winner.getTimer().getElapsedTime().length() * h3;
		timer1LabelText.setBounds(center - timingTextSize/4, timerTopLabel, textBoxSize, 170);
		timer1Label.setBounds(center - timer1LabelSize/4, timerBotLabel, textBoxSize, 170);

		int timer2LabelSize = loser.getTimer().getElapsedTime().length() * h3;
		timer2LabelText.setBounds(center - timingTextSize/4, timerTopLabel, textBoxSize, 170);
		timer2Label.setBounds(center - timer2LabelSize/4, timerBotLabel, textBoxSize, 170);

		//adding the labels
		panel.add(winnerLabel);
		panel.add(winnerScoreText);
		panel.add(winnerScore);
		panel.add(timer1Label);
		panel.add(timer1LabelText);
		panel.setBorder(br);
		
		panel2.add(loserLabel);
		panel2.add(loserScoreText);
		panel2.add(loserScore);
		panel2.add(timer2Label);
		panel2.add(timer2LabelText);
		panel2.setBorder(br);
		
		panel3.add(winnerTextLabel);
		panel3.setBorder(br);
		
		panel4.add(backTextLabel);
		panel4.setBorder(br);

		//adding the panels to the frame
		//super.add(c);
		c = getContentPane();
		c.add(panel);
		c.add(panel2);
		c.add(panel3);
		c.add(panel4);

		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private class panel4Listener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			dispose();
			new GameLauncher();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static void main(String[] args) {
		Profile player1 = new Profile("Rona", 0, false, "15:00");
		Profile player2 = new Profile("lgLgi", 0, true, "15:00");
		new EndGame(1, player1, player2);
	}

}